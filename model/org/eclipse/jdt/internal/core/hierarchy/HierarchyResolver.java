/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.hierarchy;

/**
 * This is the public entry point to resolve type hierarchies.
 *
 * When requesting additional types from the name environment, the resolver
 * accepts all forms (binary, source & compilation unit) for additional types.
 *
 * Side notes: Binary types already know their resolved supertypes so this
 * only makes sense for source types. Even though the compiler finds all binary
 * types to complete the hierarchy of a given source type, is there any reason
 * why the requestor should be informed that binary type X subclasses Y &
 * implements I & J?
 */

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberTypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.IGenericType;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.jdt.internal.core.Member;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.core.util.AstNodeFinder;
import org.eclipse.jdt.internal.core.util.ElementInfoConverter;
import org.eclipse.jdt.internal.core.util.HandleFactory;

public class HierarchyResolver implements ITypeRequestor {
	
/**
 * A wrapper around the simple name of a type that is missing.
 */
public class MissingType implements IGenericType {
	public String simpleName;
	
	public MissingType(String simpleName) {
		this.simpleName = simpleName;
	}

	/*
	 * @see IDependent#getFileName()
	 */
	public char[] getFileName() {
		return null;
	}
	
	/*
	 * @see IGenericType#getModifiers()
	 */
	public int getModifiers() {
		return 0;
	}

	/*
	 * @see IGenericType#isBinaryType()
	 */
	public boolean isBinaryType() {
		return false;
	}

	/*
	 * @see IGenericType#isClass()
	 */
	public boolean isClass() {
		return false;
	}

	/*
	 * @see IGenericType#isInterface()
	 */
	public boolean isInterface() {
		return false;
	}
	
	public String toString() {
		return "Missing type: " + this.simpleName; //$NON-NLS-1$
	}

}
	private ReferenceBinding focusType;
	private boolean hasMissingSuperClass;
	LookupEnvironment lookupEnvironment;
	private CompilerOptions options;
	HierarchyBuilder requestor;
	private ReferenceBinding[] typeBindings;

	private int typeIndex;
	private IGenericType[] typeModels;
	
public HierarchyResolver(INameEnvironment nameEnvironment, Map settings, HierarchyBuilder requestor, IProblemFactory problemFactory) {
	// create a problem handler with the 'exit after all problems' handling policy
	options = new CompilerOptions(settings);
	IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.exitAfterAllProblems();
	ProblemReporter problemReporter = new ProblemReporter(policy, options, problemFactory);

	this.setEnvironment(
		new LookupEnvironment(this, options, problemReporter, nameEnvironment),
		requestor);
}
public HierarchyResolver(LookupEnvironment lookupEnvironment, HierarchyBuilder requestor) {
	this.setEnvironment(lookupEnvironment, requestor);
}
/**
 * Add an additional binary type
 */

public void accept(IBinaryType binaryType, PackageBinding packageBinding) {
	BinaryTypeBinding typeBinding = this.lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding);
	try {
		this.remember(binaryType, typeBinding);
	} catch (AbortCompilation e) {
		// ignore
	}
}
/**
 * Add an additional compilation unit.
 */

public void accept(ICompilationUnit sourceUnit) {
	//System.out.println("Cannot accept compilation units inside the HierarchyResolver.");
	this.lookupEnvironment.problemReporter.abortDueToInternalError(
		new StringBuffer(Util.bind("accept.cannot")) //$NON-NLS-1$
			.append(sourceUnit.getFileName())
			.toString());
}
/**
 * Add additional source types
 */
public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding) {
	// find most enclosing type first (needed when explicit askForType(...) is done 
	// with a member type (e.g. p.A$B))
	ISourceType sourceType = sourceTypes[0];
	while (sourceType.getEnclosingType() != null)
		sourceType = sourceType.getEnclosingType();
	
	// build corresponding compilation unit
	CompilationResult result = new CompilationResult(sourceType.getFileName(), 1, 1, this.options.maxProblemsPerUnit);
	CompilationUnitDeclaration unit =
		SourceTypeConverter.buildCompilationUnit(
			new ISourceType[] {sourceType}, // ignore secondary types, to improve laziness
			false, // no need for field and methods
			true, // need member types
			false, // no need for field initialization
			lookupEnvironment.problemReporter, 
			result);
		
	// build bindings
	if (unit != null) {
		try {
			this.lookupEnvironment.buildTypeBindings(unit);
			rememberWithMemberTypes(sourceType, unit.types[0].binding);
			this.lookupEnvironment.completeTypeBindings(unit, false);
		} catch (AbortCompilation e) {
			// missing 'java.lang' package: ignore
		}
	}
}
/*
 * Find the super class of the given type in the cache.
 * Returns a MissingType if the class is not found,
 * or null if type has no super class.
 */
private IGenericType findSuperClass(IGenericType type, ReferenceBinding typeBinding) {
	ReferenceBinding superBinding = typeBinding.superclass();
	if (superBinding != null) {
		if (superBinding.id == TypeIds.T_JavaLangObject && typeBinding.isHierarchyInconsistent()) {
			char[] superclassName;
			char separator;
			if (type instanceof IBinaryType) {
				superclassName = ((IBinaryType)type).getSuperclassName();
				separator = '/';
			} else if (type instanceof ISourceType) {
				superclassName = ((ISourceType)type).getSuperclassName();
				separator = '.';
			} else if (type instanceof HierarchyType) {
				superclassName = ((HierarchyType)type).superclassName;
				separator = '.';
			} else {
				return null;
			}
			
			if (superclassName != null) { // check whether subclass of Object due to broken hierarchy (as opposed to explicitly extending it)
				int lastSeparator = CharOperation.lastIndexOf(separator, superclassName);
				char[] simpleName = lastSeparator == -1 ? superclassName : CharOperation.subarray(superclassName, lastSeparator+1, superclassName.length);
				if (!CharOperation.equals(simpleName, TypeConstants.OBJECT)) {
					this.hasMissingSuperClass = true;
					return new MissingType(new String(simpleName));
				}
			}
		}
		for (int t = typeIndex; t >= 0; t--) {
			if (typeBindings[t] == superBinding) {
				return typeModels[t];
			}
		}
	} 
	return null;
}
/*
 * Find the super interfaces of the given type in the cache.
 * Returns a MissingType if the interface is not found.
 */
private IGenericType[] findSuperInterfaces(IGenericType type, ReferenceBinding typeBinding) {
	char[][] superInterfaceNames;
	char separator;
	if (type instanceof IBinaryType) {
		superInterfaceNames = ((IBinaryType)type).getInterfaceNames();
		separator = '/';
	} else if (type instanceof ISourceType) {
		ISourceType sourceType = (ISourceType)type;
		if (sourceType.getName().length == 0) { // if anonymous type
			if (typeBinding.superInterfaces() != null && typeBinding.superInterfaces().length > 0) {
				superInterfaceNames = new char[][] {sourceType.getSuperclassName()};
			} else {
				superInterfaceNames = sourceType.getInterfaceNames();
			}
		} else {
			superInterfaceNames = sourceType.getInterfaceNames();
		}
		separator = '.';
	} else if (type instanceof HierarchyType) {
		superInterfaceNames = ((HierarchyType)type).superInterfaceNames;
		separator = '.';
	} else{
		return null;
	}
	
	ReferenceBinding[] interfaceBindings = typeBinding.superInterfaces();
	int bindingIndex = 0;
	int bindingLength = interfaceBindings == null ? 0 : interfaceBindings.length;
	int length = superInterfaceNames == null ? 0 : superInterfaceNames.length;
	IGenericType[] superinterfaces = new IGenericType[length];
	next : for (int i = 0; i < length; i++) {
		char[] superInterfaceName = superInterfaceNames[i];
		int lastSeparator = CharOperation.lastIndexOf(separator, superInterfaceName);
		char[] simpleName = lastSeparator == -1 ? superInterfaceName : CharOperation.subarray(superInterfaceName, lastSeparator+1, superInterfaceName.length);
		if (bindingIndex < bindingLength) {
			ReferenceBinding interfaceBinding = interfaceBindings[bindingIndex];

			// ensure that the binding corresponds to the interface defined by the user
			if (CharOperation.equals(simpleName, interfaceBinding.sourceName)) {
				bindingIndex++;
				for (int t = typeIndex; t >= 0; t--) {
					if (typeBindings[t] == interfaceBinding) {
						superinterfaces[i] = typeModels[t];
						continue next;
					}
				}
			}
		}
		superinterfaces[i] = new MissingType(new String(simpleName));
	}
	return superinterfaces;
}
private void remember(IGenericType suppliedType, ReferenceBinding typeBinding) {
	if (typeBinding == null) return;
	
	if (suppliedType.isBinaryType()) {
		// fault in its hierarchy...
		// NB: AbortCompilation is handled by caller
		typeBinding.superclass();
		typeBinding.superInterfaces();
	}
	
	if (++typeIndex == typeModels.length) {
		System.arraycopy(typeModels, 0, typeModels = new IGenericType[typeIndex * 2], 0, typeIndex);
		System.arraycopy(typeBindings, 0, typeBindings = new ReferenceBinding[typeIndex * 2], 0, typeIndex);
	}
	typeModels[typeIndex] = suppliedType;
	typeBindings[typeIndex] = typeBinding;
}
private void rememberWithMemberTypes(ISourceType suppliedType, ReferenceBinding typeBinding) {
	if (typeBinding == null) return;

	remember(suppliedType, typeBinding);

	ISourceType[] memberTypes = suppliedType.getMemberTypes();
	if (memberTypes == null) return;
	for (int m = memberTypes.length; --m >= 0;) {
		ISourceType memberType = memberTypes[m];
		rememberWithMemberTypes(memberType, typeBinding.getMemberType(memberType.getName()));
	}
}
private void rememberWithMemberTypes(TypeDeclaration typeDeclaration, HierarchyType enclosingType, ICompilationUnit unit) {

	if (typeDeclaration.binding == null) return;

	// simple super class name
	char[] superclassName = null;
	TypeReference superclass = typeDeclaration.superclass;
	if (superclass != null) {
		char[][] typeName = superclass.getTypeName();
		superclassName = typeName == null ? null : typeName[typeName.length-1];
	}
	
	// simple super interface names
	char[][] superInterfaceNames = null;
	TypeReference[] superInterfaces = typeDeclaration.superInterfaces;
	if (superInterfaces != null) {
		int length = superInterfaces.length;
		superInterfaceNames = new char[length][];
		for (int i = 0; i < length; i++) {
			TypeReference superInterface = superInterfaces[i];
			char[][] typeName = superInterface.getTypeName();
			superInterfaceNames[i] = typeName[typeName.length-1];
		}
	}

	HierarchyType hierarchyType = new HierarchyType(
		enclosingType, 
		!typeDeclaration.isInterface(),
		typeDeclaration.name,
		typeDeclaration.binding.modifiers,
		superclassName,
		superInterfaceNames,
		unit);
	remember(hierarchyType, typeDeclaration.binding);

	// propagate into member types
	if (typeDeclaration.memberTypes == null) return;
	MemberTypeDeclaration[] memberTypes = typeDeclaration.memberTypes;
	for (int i = 0, max = memberTypes.length; i < max; i++){
		rememberWithMemberTypes(memberTypes[i], hierarchyType, unit);
	}
}
/*
 * Remember the given type and the super types in the same compilation unit
 */
private void rememberWithSuperTypes(ReferenceBinding referenceBinding, HandleFactory factory, CompilationUnitScope compilationUnitScope, Openable openable) {
	if (!(referenceBinding instanceof SourceTypeBinding)) return;
	SourceTypeBinding type = (SourceTypeBinding)referenceBinding;
	if (type.scope.compilationUnitScope() != compilationUnitScope) return;
	IJavaElement element = factory.createElement(type.scope.referenceContext, compilationUnitScope.referenceContext, openable);
	if (element == null) return;
	ISourceType sourceType = null;
	try {
		sourceType = (ISourceType)((JavaElement)element).getElementInfo();
	} catch (JavaModelException e) {
		return;
	}
	remember(sourceType, type);
	
	if (type.superclass != null) {
		rememberWithSuperTypes(type.superclass, factory, compilationUnitScope, openable);
	}
	ReferenceBinding[] superInterfaces = type.superInterfaces;
	if (superInterfaces != null) {
		for (int i = 0, length = superInterfaces.length; i < length; i++) {
			ReferenceBinding superInterface = superInterfaces[i];
			rememberWithSuperTypes(superInterface, factory, compilationUnitScope, openable);
		}
	}
	
}
private void reportHierarchy() {
	int objectIndex = -1;
	for (int current = typeIndex; current >= 0; current--) {
		ReferenceBinding typeBinding = typeBindings[current];

		// java.lang.Object treated at the end
		if (typeBinding.id == TypeIds.T_JavaLangObject) {
			objectIndex = current;
			continue;
		}

		IGenericType suppliedType = typeModels[current];

		if (!subOrSuperOfFocus(typeBinding)) {
			continue; // ignore types outside of hierarchy
		}

		IGenericType superclass;
		if (typeBinding.isInterface()){ // do not connect interfaces to Object
			superclass = null;
		} else {
			superclass = this.findSuperClass(suppliedType, typeBinding);
		}
		IGenericType[] superinterfaces = this.findSuperInterfaces(suppliedType, typeBinding);
		
		requestor.connect(suppliedType, superclass, superinterfaces);
	}
	// add java.lang.Object only if the super class is not missing
	if (!this.hasMissingSuperClass && objectIndex > -1) {
		requestor.connect(typeModels[objectIndex], null, null);
	}
}
private void reset(){
	this.lookupEnvironment.reset();

	this.typeIndex = -1;
	this.typeModels = new IGenericType[5];
	this.typeBindings = new ReferenceBinding[5];
}
/**
 * Resolve the supertypes for the supplied source type.
 * Inform the requestor of the resolved supertypes using:
 *    connect(ISourceType suppliedType, IGenericType superclass, IGenericType[] superinterfaces)
 */

public void resolve(IGenericType suppliedType) {
	try {
		if (suppliedType.isBinaryType()) {
			remember(suppliedType, this.lookupEnvironment.cacheBinaryType((IBinaryType) suppliedType));
		} else {
			// must start with the top level type
			ISourceType topLevelType = (ISourceType) suppliedType;
			while (topLevelType.getEnclosingType() != null)
				topLevelType = topLevelType.getEnclosingType();
			CompilationResult result = new CompilationResult(topLevelType.getFileName(), 1, 1, this.options.maxProblemsPerUnit);
			CompilationUnitDeclaration unit =
				SourceTypeConverter.buildCompilationUnit(
					new ISourceType[]{topLevelType}, 
					false, // no need for field and methods
					true, // need member types
					false, // no need for field initialization
					this.lookupEnvironment.problemReporter, 
					result);

			if (unit != null) {
				this.lookupEnvironment.buildTypeBindings(unit);
				rememberWithMemberTypes(topLevelType, unit.types[0].binding);

				this.lookupEnvironment.completeTypeBindings(unit, false);
			}
		}
		reportHierarchy();
	} catch (AbortCompilation e) { // ignore this exception for now since it typically means we cannot find java.lang.Object
	} finally {
		reset();
	}
}
/**
 * Resolve the supertypes for the supplied source types.
 * Inform the requestor of the resolved supertypes for each
 * supplied source type using:
 *    connect(ISourceType suppliedType, IGenericType superclass, IGenericType[] superinterfaces)
 *
 * Also inform the requestor of the supertypes of each
 * additional requested super type which is also a source type
 * instead of a binary type.
 */

public void resolve(IGenericType[] suppliedTypes, ICompilationUnit[] sourceUnits, IProgressMonitor monitor) {
	try {
		int suppliedLength = suppliedTypes == null ? 0 : suppliedTypes.length;
		int sourceLength = sourceUnits == null ? 0 : sourceUnits.length;
		CompilationUnitDeclaration[] units = new CompilationUnitDeclaration[suppliedLength + sourceLength];
		
		// cache binary type bidings
		BinaryTypeBinding[] binaryBindings = new BinaryTypeBinding[suppliedLength];
		for (int i = 0; i < suppliedLength; i++) {
			if (suppliedTypes[i].isBinaryType()) {
				IBinaryType binaryType = (IBinaryType) suppliedTypes[i];
				try {
					binaryBindings[i] = this.lookupEnvironment.cacheBinaryType(binaryType, false);
				} catch (AbortCompilation e) {
					// classpath problem for this type: ignore
				}
			}
		}
		
		// build type bindings
		for (int i = 0; i < suppliedLength; i++) {
			if (suppliedTypes[i].isBinaryType()) {
				if (binaryBindings[i] != null) {
					try {
						remember(suppliedTypes[i], binaryBindings[i]);
					} catch (AbortCompilation e) {
						// classpath problem for this type: ignore
					}
				}
			} else {
				// must start with the top level type
				ISourceType topLevelType = (ISourceType) suppliedTypes[i];
				while (topLevelType.getEnclosingType() != null)
					topLevelType = topLevelType.getEnclosingType();
				CompilationResult result = new CompilationResult(topLevelType.getFileName(), i, suppliedLength, this.options.maxProblemsPerUnit);
				units[i] = 
					SourceTypeConverter.buildCompilationUnit(
						new ISourceType[]{topLevelType}, 
						false, // no need for field and methods
						true, // need member types
						false, // no need for field initialization
						this.lookupEnvironment.problemReporter, 
						result);
				if (units[i] != null) {
					try {
						this.lookupEnvironment.buildTypeBindings(units[i]);
					} catch (AbortCompilation e) {
						// classpath problem for this type: ignore
					}
				}
			}
			worked(monitor, 1);
		}
		Parser parser = new Parser(this.lookupEnvironment.problemReporter, true);
		for (int i = 0; i < sourceLength; i++){
			ICompilationUnit sourceUnit = sourceUnits[i];
			CompilationResult unitResult = new CompilationResult(sourceUnit, suppliedLength+i, suppliedLength+sourceLength, this.options.maxProblemsPerUnit); 
			CompilationUnitDeclaration parsedUnit = parser.dietParse(sourceUnit, unitResult);
			if (parsedUnit != null) {
				units[suppliedLength+i] = parsedUnit;
				this.lookupEnvironment.buildTypeBindings(parsedUnit);
			}
			worked(monitor, 1);
		}
		
		// complete type bindings (ie. connect super types) and remember them
		for (int i = 0; i < suppliedLength; i++) {
			if (!suppliedTypes[i].isBinaryType()) { // note that binary types have already been remembered above
				CompilationUnitDeclaration parsedUnit = units[i];
				if (parsedUnit != null) {
					// must start with the top level type
					ISourceType topLevelType = (ISourceType) suppliedTypes[i];
					suppliedTypes[i] = null; // no longer needed pass this point				
					while (topLevelType.getEnclosingType() != null)
						topLevelType = topLevelType.getEnclosingType();
					try {
						this.lookupEnvironment.completeTypeBindings(parsedUnit, false);
						rememberWithMemberTypes(topLevelType, parsedUnit.types[0].binding);
					} catch (AbortCompilation e) {
						// classpath problem for this type: ignore
					}
				}
			}
			worked(monitor, 1);
		}
		for (int i = 0; i < sourceLength; i++) {
			CompilationUnitDeclaration parsedUnit = units[suppliedLength+i];
			if (parsedUnit != null) {
				this.lookupEnvironment.completeTypeBindings(parsedUnit, false);
				int typeCount = parsedUnit.types == null ? 0 : parsedUnit.types.length;
				ICompilationUnit sourceUnit = sourceUnits[i];
				sourceUnits[i] = null; // no longer needed pass this point
				for (int j = 0; j < typeCount; j++){
					rememberWithMemberTypes(parsedUnit.types[j], null, sourceUnit);
				}
			}
			worked(monitor, 1);
		}

		reportHierarchy();
		
	} catch (ClassCastException e){ // work-around for 1GF5W1S - can happen in case duplicates are fed to the hierarchy with binaries hiding sources
	} catch (AbortCompilation e) { // ignore this exception for now since it typically means we cannot find java.lang.Object
	} finally {
		reset();
	}
}
/**
 * Resolve the supertypes for the supplied source types.
 * Inform the requestor of the resolved supertypes for each
 * supplied source type using:
 *    connect(ISourceType suppliedType, IGenericType superclass, IGenericType[] superinterfaces)
 *
 * Also inform the requestor of the supertypes of each
 * additional requested super type which is also a source type
 * instead of a binary type.
 */

public void resolve(IGenericType[] suppliedTypes, IProgressMonitor monitor) {
	resolve(suppliedTypes, null, monitor);
}
public void resolveLocalType(IType type, Member declaringMember) throws JavaModelException {

	if (!(type instanceof SourceType)) {
		// TODO (jerome) handle binary type
		return;
	}

	// get top-level source types
	// NB: need all top-level source types as a local type can refer to a secondary type in the same unit
	org.eclipse.jdt.core.ICompilationUnit cu = type.getCompilationUnit();
	IType[] topLevelTypes = cu.getTypes();
	int length = topLevelTypes.length;
	SourceTypeElementInfo[] sourceTypes = new SourceTypeElementInfo[length];
	for (int i = 0; i < length; i++) {
		sourceTypes[i] = (SourceTypeElementInfo)((SourceType)topLevelTypes[i]).getElementInfo();
	}
	
	// build compilation unit declaration
	CompilationResult result = new CompilationResult(sourceTypes[0].getFileName(), 1, 1, this.options.maxProblemsPerUnit);
	CompilationUnitDeclaration unit =
		ElementInfoConverter.buildCompilationUnit(
			sourceTypes,
			true, // need local and anonymous types
			this.lookupEnvironment.problemReporter, 
			result);
			
	// build bindings
	if (unit != null) {
		TypeDeclaration typeDecl = new AstNodeFinder(unit).findType(type);
		if (typeDecl == null) return;
		try {
			this.lookupEnvironment.buildTypeBindings(unit);
			// TODO (jerome) optimize to resolve method that contains local type only
			this.lookupEnvironment.completeTypeBindings(unit, true/*build fields and methods*/);
			unit.scope.faultInTypes();
			unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
			unit.resolve();
			this.focusType = typeDecl.binding;
			rememberWithSuperTypes(this.focusType, new HandleFactory(), unit.scope, (Openable)type.getCompilationUnit());
			reportHierarchy();
		} catch (AbortCompilation e) {
			// missing 'java.lang' package: ignore
		}
	}

}
private void setEnvironment(LookupEnvironment lookupEnvironment, HierarchyBuilder requestor) {
	this.lookupEnvironment = lookupEnvironment;
	this.requestor = requestor;

	this.typeIndex = -1;
	this.typeModels = new IGenericType[5];
	this.typeBindings = new ReferenceBinding[5];
}
/**
 * Set the focus type (ie. the type that this resolver is computing the hierarch for.
 * Returns the binding of this focus type or null if it could not be found.
 */
public ReferenceBinding setFocusType(char[][] compoundName) {
	if (compoundName == null || this.lookupEnvironment == null) return null;
	this.focusType = this.lookupEnvironment.getCachedType(compoundName);
	if (this.focusType == null) {
		this.focusType = this.lookupEnvironment.askForType(compoundName);
	}
	return this.focusType;
}
public boolean subOrSuperOfFocus(ReferenceBinding typeBinding) {
	if (this.focusType == null) return true; // accept all types (case of hierarchy in a region)
	if (this.subTypeOfType(this.focusType, typeBinding)) return true;
	if (this.subTypeOfType(typeBinding, this.focusType)) return true;
	return false;
}
private boolean subTypeOfType(ReferenceBinding subType, ReferenceBinding typeBinding) {
	if (typeBinding == null || subType == null) return false;
	if (subType == typeBinding) return true;
	ReferenceBinding superclass = subType.superclass();
//	if (superclass != null && superclass.id == TypeIds.T_JavaLangObject && subType.isHierarchyInconsistent()) return false;
	if (this.subTypeOfType(superclass, typeBinding)) return true;
	ReferenceBinding[] superInterfaces = subType.superInterfaces();
	if (superInterfaces != null) {
		for (int i = 0, length = superInterfaces.length; i < length; i++) {
			if (this.subTypeOfType(superInterfaces[i], typeBinding)) return true;
		} 
	}
	return false;
}
protected void worked(IProgressMonitor monitor, int work) {
	if (monitor != null) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(work);
		}
	}
}
}
