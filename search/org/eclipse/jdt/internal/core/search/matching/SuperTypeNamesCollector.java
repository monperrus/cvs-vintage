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
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.compiler.AbstractSyntaxTreeVisitorAdapter;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.jdt.internal.core.Util;
import org.eclipse.jdt.internal.core.search.*;
import org.eclipse.jdt.internal.core.search.indexing.IIndexConstants;
import org.eclipse.jdt.internal.core.search.indexing.IndexManager;

/**
 * Collects the super type names of a given declaring type.
 * Returns NOT_FOUND_DECLARING_TYPE if the declaring type was not found.
 * Returns null if the declaring type pattern doesn't require an exact match.
 */
public class SuperTypeNamesCollector {

SearchPattern pattern;
char[] typeSimpleName;
char[] typeQualification;
MatchLocator locator;
IType type; 
IProgressMonitor progressMonitor;
char[][][] result;
int resultIndex;

/**
 * An ast visitor that visits type declarations and member type declarations
 * collecting their super type names.
 */
public class TypeDeclarationVisitor extends AbstractSyntaxTreeVisitorAdapter {
	public boolean visit(LocalTypeDeclaration typeDeclaration, BlockScope scope) {
		ReferenceBinding binding = typeDeclaration.binding;
		if (SuperTypeNamesCollector.this.matches(binding))
			SuperTypeNamesCollector.this.collectSuperTypeNames(binding);
		return true;
	}
	public boolean visit(AnonymousLocalTypeDeclaration typeDeclaration, BlockScope scope) {
		ReferenceBinding binding = typeDeclaration.binding;
		if (SuperTypeNamesCollector.this.matches(binding))
			SuperTypeNamesCollector.this.collectSuperTypeNames(binding);
		return true;
	}
	public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
		ReferenceBinding binding = typeDeclaration.binding;
		if (SuperTypeNamesCollector.this.matches(binding))
			SuperTypeNamesCollector.this.collectSuperTypeNames(binding);
		return true;
	}
	public boolean visit(MemberTypeDeclaration memberTypeDeclaration, ClassScope scope) {
		ReferenceBinding binding = memberTypeDeclaration.binding;
		if (SuperTypeNamesCollector.this.matches(binding))
			SuperTypeNamesCollector.this.collectSuperTypeNames(binding);
		return true;
	}
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		return false; // don't visit field declarations
	}
	public boolean visit(Initializer initializer, MethodScope scope) {
		return false; // don't visit initializers
	}
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		return false; // don't visit constructor declarations
	}
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		return false; // don't visit method declarations
	}
}

public SuperTypeNamesCollector(
	SearchPattern pattern,
	char[] typeSimpleName,
	char[] typeQualification,
	MatchLocator locator,
	IType type, 
	IProgressMonitor progressMonitor) {

	this.pattern = pattern;
	this.typeSimpleName = typeSimpleName;
	this.typeQualification = typeQualification;
	this.locator = locator;
	this.type = type;
	this.progressMonitor = progressMonitor;
}

protected void addToResult(char[][] compoundName) {
	int resultLength = this.result.length;
	for (int i = 0; i < resultLength; i++)
		if (CharOperation.equals(this.result[i], compoundName)) return; // already known

	if (resultLength == this.resultIndex)
		System.arraycopy(this.result, 0, this.result = new char[resultLength*2][][], 0, resultLength);
	this.result[this.resultIndex++] = compoundName;
}
/*
 * Parse the given compiation unit and build its type bindings.
 * Don't build methods and fields.
 */
protected CompilationUnitDeclaration buildBindings(ICompilationUnit compilationUnit) throws JavaModelException {
	final IFile file = (IFile) compilationUnit.getResource();
	final String fileName = file.getFullPath().lastSegment();
	final char[] mainTypeName = fileName.substring(0, fileName.length() - 5).toCharArray();

	// source unit
	IBuffer buffer = compilationUnit.getBuffer();
	final char[] source = 
		compilationUnit.isWorkingCopy()
			? (buffer == null ? null : buffer.getCharacters())
			: Util.getResourceContentsAsCharArray(file);
	org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit = 
		new org.eclipse.jdt.internal.compiler.env.ICompilationUnit() {
			public char[] getContents() { return source; }
			public char[] getFileName() { return fileName.toCharArray(); }
			public char[] getMainTypeName() { return mainTypeName; }
			public char[][] getPackageName() { return null; }
		};

	CompilationResult compilationResult = new CompilationResult(sourceUnit, 1, 1, 0);
	CompilationUnitDeclaration unit = this.locator.basicParser().dietParse(sourceUnit, compilationResult);
	if (unit != null) {
		this.locator.lookupEnvironment.buildTypeBindings(unit);
		this.locator.lookupEnvironment.completeTypeBindings(unit, false);
	}
	return unit;
}
public char[][][] collect() throws JavaModelException {
	if (this.type != null) {
		// Collect the paths of the cus that are in the hierarchy of the given type
		this.result = new char[1][][];
		this.resultIndex = 0;
		JavaProject javaProject = (JavaProject) this.type.getJavaProject();
		this.locator.initialize(javaProject, 0);
		this.locator.nameLookup.setUnitsToLookInside(this.locator.workingCopies); // NB: this uses a PerThreadObject, so it is thread safe
		try {
			if (this.type.isBinary()) {
				BinaryTypeBinding binding = this.locator.cacheBinaryType(this.type);
				if (binding != null)
					collectSuperTypeNames(binding);
			} else {
				ICompilationUnit unit = this.type.getCompilationUnit();
				CompilationUnitDeclaration parsedUnit = buildBindings(unit);
				if (parsedUnit != null)
					parsedUnit.traverse(new TypeDeclarationVisitor(), parsedUnit.scope);
			}
		} catch (AbortCompilation e) {
			// problem with classpath: report inacurrate matches
			return null;
		} finally {
			this.locator.nameLookup.setUnitsToLookInside(null);
		}
		if (this.result.length > this.resultIndex)
			System.arraycopy(this.result, 0, this.result = new char[this.resultIndex][][], 0, this.resultIndex);
		return this.result;
	}

	// Collect the paths of the cus that declare a type which matches declaringQualification + declaringSimpleName
	String[] paths = this.getPathsOfDeclaringType();
	if (paths == null) return null;

	// Create bindings from source types and binary types and collect super type names of the type declaration 
	// that match the given declaring type
	Util.sort(paths); // sort by projects
	JavaProject previousProject = null;
	this.result = new char[1][][];
	this.resultIndex = 0;
	try {
		for (int i = 0, length = paths.length; i < length; i++) {
			try {
				Openable openable = this.locator.handleFactory.createOpenable(paths[i], this.locator.scope);
				if (openable == null) continue; // outside classpath

				IJavaProject project = openable.getJavaProject();
				if (!project.equals(previousProject)) {
					if (previousProject != null)
						this.locator.nameLookup.setUnitsToLookInside(null);
					previousProject = (JavaProject) project;
					this.locator.initialize(previousProject, 0);
					this.locator.nameLookup.setUnitsToLookInside(this.locator.workingCopies);
				}
				if (openable instanceof ICompilationUnit) {
					ICompilationUnit unit = (ICompilationUnit) openable;
					CompilationUnitDeclaration parsedUnit = buildBindings(unit);
					if (parsedUnit != null)
						parsedUnit.traverse(new TypeDeclarationVisitor(), parsedUnit.scope);
				} else if (openable instanceof IClassFile) {
					IClassFile classFile = (IClassFile) openable;
					BinaryTypeBinding binding = this.locator.cacheBinaryType(classFile.getType());
					if (matches(binding))
						collectSuperTypeNames(binding);
				}
			} catch (AbortCompilation e) {
				// ignore: continue with next element
			} catch (JavaModelException e) {
				// ignore: continue with next element
			}
		}
	} finally {
		if (previousProject != null)
			this.locator.nameLookup.setUnitsToLookInside(null);
	}
	if (this.result.length > this.resultIndex)
		System.arraycopy(this.result, 0, this.result = new char[this.resultIndex][][], 0, this.resultIndex);
	return this.result;
}
/**
 * Collects the names of all the supertypes of the given type.
 */
protected void collectSuperTypeNames(ReferenceBinding binding) {
	ReferenceBinding superclass = binding.superclass();
	if (superclass != null) {
		this.addToResult(superclass.compoundName);
		this.collectSuperTypeNames(superclass);
	}

	ReferenceBinding[] interfaces = binding.superInterfaces();
	if (interfaces != null) {
		for (int i = 0; i < interfaces.length; i++) {
			ReferenceBinding interfaceBinding = interfaces[i];
			this.addToResult(interfaceBinding.compoundName);
			this.collectSuperTypeNames(interfaceBinding);
		}
	}
}
protected String[] getPathsOfDeclaringType() {
	if (this.typeQualification == null && this.typeSimpleName == null) return null;

	final PathCollector pathCollector = new PathCollector();
	IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
	IndexManager indexManager = JavaModelManager.getJavaModelManager().getIndexManager();
	SearchPattern searchPattern = new TypeDeclarationPattern(
		this.typeSimpleName != null ? null : this.typeQualification, // use the qualification only if no simple name
		null, // do find member types
		this.typeSimpleName,
		IIndexConstants.TYPE_SUFFIX,
		this.pattern.matchMode, 
		true);
	IIndexSearchRequestor searchRequestor = new IndexSearchAdapter() {
		public void acceptClassDeclaration(String resourcePath, char[] simpleTypeName, char[][] enclosingTypeNames, char[] packageName) {
			if (enclosingTypeNames != IIndexConstants.ONE_ZERO_CHAR) // filter out local and anonymous classes
				pathCollector.acceptClassDeclaration(resourcePath, simpleTypeName, enclosingTypeNames, packageName);
		}		
		public void acceptInterfaceDeclaration(String resourcePath, char[] simpleTypeName, char[][] enclosingTypeNames, char[] packageName) {
			if (enclosingTypeNames != IIndexConstants.ONE_ZERO_CHAR) // filter out local and anonymous classes
				pathCollector.acceptInterfaceDeclaration(resourcePath, simpleTypeName, enclosingTypeNames, packageName);
		}		
	};		

	indexManager.performConcurrentJob(
		new PatternSearchJob(
			searchPattern, 
			scope, 
			searchRequestor, 
			indexManager),
		IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
		progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 100));
	return pathCollector.getPaths();
}
protected boolean matches(char[][] compoundName) {
	int length = compoundName.length;
	if (length == 0) return false;
	char[] simpleName = compoundName[length-1];
	int last = length - 1;
	if (this.typeSimpleName == null || this.pattern.matchesName(simpleName, this.typeSimpleName)) {
		// most frequent case: simple name equals last segment of compoundName
		char[][] qualification = new char[last][];
		System.arraycopy(compoundName, 0, qualification, 0, last);
		return this.pattern.matchesName(this.typeQualification, CharOperation.concatWith(qualification, '.'));
	}

	if (!CharOperation.endsWith(simpleName, this.typeSimpleName)) return false;

	// member type -> transform A.B.C$D into A.B.C.D
	System.arraycopy(compoundName, 0, compoundName = new char[length+1][], 0, last);
	int dollar = CharOperation.indexOf('$', simpleName);
	if (dollar == -1) return false;
	compoundName[last] = CharOperation.subarray(simpleName, 0, dollar);
	compoundName[length] = CharOperation.subarray(simpleName, dollar+1, simpleName.length); 
	return this.matches(compoundName);
}
protected boolean matches(ReferenceBinding binding) {
	return binding != null && binding.compoundName != null && this.matches(binding.compoundName);
}
}
