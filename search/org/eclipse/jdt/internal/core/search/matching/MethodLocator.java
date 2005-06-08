/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.util.SimpleSet;

public class MethodLocator extends PatternLocator {

protected MethodPattern pattern;
protected boolean isDeclarationOfReferencedMethodsPattern;

//extra reference info
public char[][][] allSuperDeclaringTypeNames;

public MethodLocator(MethodPattern pattern) {
	super(pattern);

	this.pattern = pattern;
	this.isDeclarationOfReferencedMethodsPattern = this.pattern instanceof DeclarationOfReferencedMethodsPattern;
}
public void initializePolymorphicSearch(MatchLocator locator) {
	try {
		this.allSuperDeclaringTypeNames =
			new SuperTypeNamesCollector(
				this.pattern,
				this.pattern.declaringSimpleName,
				this.pattern.declaringQualification,
				locator,
				this.pattern.declaringType,
				locator.progressMonitor).collect();
	} catch (JavaModelException e) {
		// inaccurate matches will be found
	}
}
/*
 * Return whether a method may override a method in super classes erasures or not.
 */
private boolean isErasureMethodOverride(ReferenceBinding type, MethodBinding method) {
	if (type == null) return false;

	// matches superclass
	if (!type.isInterface() && !CharOperation.equals(type.compoundName, TypeConstants.JAVA_LANG_OBJECT)) {
		ReferenceBinding superClass = type.superclass();
		if (superClass.isParameterizedType()) {
			TypeBinding erasure = ((ParameterizedTypeBinding)superClass).erasure();
			if (erasure instanceof ReferenceBinding) {
				MethodBinding[] methods = superClass.getMethods(this.pattern.selector);
				int length = methods.length;
				for (int i = 0; i<length; i++) {
					if (methods[i].areParametersEqual(method)) return true;
				}
			}
		}
		if (isErasureMethodOverride(superClass, method)) {
			return true;
		}
	}

	// matches interfaces
	ReferenceBinding[] interfaces = type.superInterfaces();
	if (interfaces == null) return false;
	int iLength = interfaces.length;
	for (int i = 0; i<iLength; i++) {
		if (interfaces[i].isParameterizedType()) {
			TypeBinding erasure = ((ParameterizedTypeBinding)interfaces[i]).erasure();
			if (erasure instanceof ReferenceBinding) {
				MethodBinding[] methods = ((ReferenceBinding)erasure).getMethods(this.pattern.selector);
				int length = methods.length;
				for (int j = 0; j<length; j++) {
					if (methods[i].areParametersEqual(method)) return true;
				}
			}
		}
		if (isErasureMethodOverride(interfaces[i], method)) {
			return true;
		}
	}
	return false;
}
/*
 * Return whether a type name is in pattern all super declaring types names.
 */
private boolean isTypeInSuperDeclaringTypeNames(char[][] typeName) {
	if (allSuperDeclaringTypeNames == null) return false;
	int length = allSuperDeclaringTypeNames.length;
	for (int i= 0; i<length; i++) {
		if (CharOperation.equals(allSuperDeclaringTypeNames[i], typeName)) {
			return true;
		}
	}
	return false;
}
/**
 * Returns whether the code gen will use an invoke virtual for 
 * this message send or not.
 */
protected boolean isVirtualInvoke(MethodBinding method, MessageSend messageSend) {
	return !method.isStatic() && !method.isPrivate() && !messageSend.isSuperAccess();
}
public int match(ASTNode node, MatchingNodeSet nodeSet) {
	int declarationsLevel = IMPOSSIBLE_MATCH;
	if (this.pattern.findReferences) {
		if (node instanceof ImportReference) {
			// With static import, we can have static method reference in import reference
			ImportReference importRef = (ImportReference) node;
			int length = importRef.tokens.length-1;
			if (importRef.isStatic() && !importRef.onDemand && matchesName(this.pattern.selector, importRef.tokens[length])) {
				char[][] compoundName = new char[length][];
				System.arraycopy(importRef.tokens, 0, compoundName, 0, length);
				char[] declaringType = CharOperation.concat(pattern.declaringQualification, pattern.declaringSimpleName, '.');
				if (matchesName(declaringType, CharOperation.concatWith(compoundName, '.'))) {
					declarationsLevel = ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
				}
			}
		}
	}
	return nodeSet.addMatch(node, declarationsLevel);
}
//public int match(ConstructorDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
//public int match(Expression node, MatchingNodeSet nodeSet) - SKIP IT
//public int match(FieldDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
public int match(MethodDeclaration node, MatchingNodeSet nodeSet) {
	if (!this.pattern.findDeclarations) return IMPOSSIBLE_MATCH;

	// Verify method name
	if (!matchesName(this.pattern.selector, node.selector)) return IMPOSSIBLE_MATCH;
	
	// Verify parameters types
	boolean resolve = ((InternalSearchPattern)this.pattern).mustResolve;
	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		ASTNode[] args = node.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
		for (int i = 0; i < argsLength; i++) {
			if (!matchesTypeReference(this.pattern.parameterSimpleNames[i], ((Argument) args[i]).type)) {
				if (!((InternalSearchPattern)this.pattern).mustResolve) {
					// Set resolution flag on node set in case of types was inferred in parameterized types from generic ones...
				 	// (see  bugs https://bugs.eclipse.org/bugs/show_bug.cgi?id=79990, 96761, 96763)
					nodeSet.mustResolve = true;
					resolve = true;
				}
			}
		}
	}

	// Verify type arguments (do not reject if pattern has no argument as it can be an erasure match)
	if (this.pattern.hasMethodArguments()) {
		if (node.typeParameters == null || node.typeParameters.length != this.pattern.methodArguments.length) return IMPOSSIBLE_MATCH;
	}

	// Method declaration may match pattern
	return nodeSet.addMatch(node, resolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
public int match(MemberValuePair node, MatchingNodeSet nodeSet) {
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;

	if (!matchesName(this.pattern.selector, node.name)) return IMPOSSIBLE_MATCH;

	return nodeSet.addMatch(node, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
public int match(MessageSend node, MatchingNodeSet nodeSet) {
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;

	if (!matchesName(this.pattern.selector, node.selector)) return IMPOSSIBLE_MATCH;
	if (this.pattern.parameterSimpleNames != null && (this.pattern.shouldCountParameter() || ((node.bits & ASTNode.InsideJavadoc) != 0))) {
		int length = this.pattern.parameterSimpleNames.length;
		ASTNode[] args = node.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
	}

	return nodeSet.addMatch(node, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
//public int match(Reference node, MatchingNodeSet nodeSet) - SKIP IT
public int match(Annotation node, MatchingNodeSet nodeSet) {
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;
	MemberValuePair[] pairs = node.memberValuePairs();
	if (pairs == null || pairs.length == 0) return IMPOSSIBLE_MATCH;

	int length = pairs.length;
	MemberValuePair pair = null;
	for (int i=0; i<length; i++) {
		pair = node.memberValuePairs()[i];
		if (matchesName(this.pattern.selector, pair.name)) {
			ASTNode possibleNode = (node instanceof SingleMemberAnnotation) ? (ASTNode) node : pair;
			return nodeSet.addMatch(possibleNode, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
		}
	}
	return IMPOSSIBLE_MATCH;
}
//public int match(TypeDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
//public int match(TypeReference node, MatchingNodeSet nodeSet) - SKIP IT

protected int matchContainer() {
	if (this.pattern.findReferences) {
		// need to look almost everywhere to find in javadocs and static import
		return ALL_CONTAINER;
	}
	return CLASS_CONTAINER;
}
/* (non-Javadoc)
 * @see org.eclipse.jdt.internal.core.search.matching.PatternLocator#matchLevelAndReportImportRef(org.eclipse.jdt.internal.compiler.ast.ImportReference, org.eclipse.jdt.internal.compiler.lookup.Binding, org.eclipse.jdt.internal.core.search.matching.MatchLocator)
 * Accept to report match of static field on static import
 */
protected void matchLevelAndReportImportRef(ImportReference importRef, Binding binding, MatchLocator locator) throws CoreException {
	if (importRef.isStatic() && binding instanceof MethodBinding) {
		super.matchLevelAndReportImportRef(importRef, binding, locator);
	}
}
protected int matchMethod(MethodBinding method, boolean skipImpossibleArg) {
	if (!matchesName(this.pattern.selector, method.selector)) return IMPOSSIBLE_MATCH;

	int level = ACCURATE_MATCH;
	// look at return type only if declaring type is not specified
	if (this.pattern.declaringSimpleName == null) {
		// TODO (frederic) use this call to refine accuracy on return type
		// int newLevel = resolveLevelForType(this.pattern.returnSimpleName, this.pattern.returnQualification, this.pattern.returnTypeArguments, 0, method.returnType);
		int newLevel = resolveLevelForType(this.pattern.returnSimpleName, this.pattern.returnQualification, method.returnType);
		if (level > newLevel) {
			if (newLevel == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;
			level = newLevel; // can only be downgraded
		}
	}

	// parameter types
	int parameterCount = this.pattern.parameterSimpleNames == null ? -1 : this.pattern.parameterSimpleNames.length;
	if (parameterCount > -1) {
		// global verification
		if (method.parameters == null) return INACCURATE_MATCH;
		if (parameterCount != method.parameters.length) return IMPOSSIBLE_MATCH;
		if (!method.isValidBinding() && ((ProblemMethodBinding)method).problemId() == ProblemReasons.Ambiguous) {
			// return inaccurate match for ambiguous call (bug 80890)
			return INACCURATE_MATCH;
		}

		// verify each parameter
		for (int i = 0; i < parameterCount; i++) {
			TypeBinding argType = method.parameters[i];
			int newLevel = IMPOSSIBLE_MATCH;
			if (argType.isMemberType()) {
				// only compare source name for member type (bug 41018)
				newLevel = CharOperation.match(this.pattern.parameterSimpleNames[i], argType.sourceName(), this.isCaseSensitive)
					? ACCURATE_MATCH
					: IMPOSSIBLE_MATCH;
			} else {
				// TODO (frederic) use this call to refine accuracy on parameter types
//				 newLevel = resolveLevelForType(this.pattern.parameterSimpleNames[i], this.pattern.parameterQualifications[i], this.pattern.parametersTypeArguments[i], 0, argType);
				newLevel = resolveLevelForType(this.pattern.parameterSimpleNames[i], this.pattern.parameterQualifications[i], argType);
			}
			if (level > newLevel) {
				if (newLevel == IMPOSSIBLE_MATCH) {
					if (skipImpossibleArg) {
						// Do not consider match as impossible while finding declarations and source level >= 1.5
					 	// (see  bugs https://bugs.eclipse.org/bugs/show_bug.cgi?id=79990, 96761, 96763)
						newLevel = level;
					} else {
						return IMPOSSIBLE_MATCH;
					}
				}
				level = newLevel; // can only be downgraded
			}
		}
	}

	return level;
}
/**
 * @see org.eclipse.jdt.internal.core.search.matching.PatternLocator#matchReportReference(org.eclipse.jdt.internal.compiler.ast.ASTNode, org.eclipse.jdt.core.IJavaElement, Binding, int, org.eclipse.jdt.internal.core.search.matching.MatchLocator)
 */
protected void matchReportReference(ASTNode reference, IJavaElement element, Binding elementBinding, int accuracy, MatchLocator locator) throws CoreException {
	MethodBinding methodBinding = (reference instanceof MessageSend) ? ((MessageSend)reference).binding: ((elementBinding instanceof MethodBinding) ? (MethodBinding) elementBinding : null);
	if (this.isDeclarationOfReferencedMethodsPattern) {
		if (methodBinding == null) return;
		// need exact match to be able to open on type ref
		if (accuracy != SearchMatch.A_ACCURATE) return;

		// element that references the method must be included in the enclosing element
		DeclarationOfReferencedMethodsPattern declPattern = (DeclarationOfReferencedMethodsPattern) this.pattern; 
		while (element != null && !declPattern.enclosingElement.equals(element))
			element = element.getParent();
		if (element != null) {
			reportDeclaration(methodBinding, locator, declPattern.knownMethods);
		}
	} else {
		match = locator.newMethodReferenceMatch(element, elementBinding, accuracy, -1, -1, false /*not constructor*/, false/*not synthetic*/, reference);
		if (this.pattern.findReferences && reference instanceof MessageSend) {
			IJavaElement focus = ((InternalSearchPattern) this.pattern).focus;
			// verify closest match if pattern was bound
			// (see bug 70827)
			if (focus != null && focus.getElementType() == IJavaElement.METHOD) {
				if (methodBinding != null) {
					boolean isPrivate = Flags.isPrivate(((IMethod) focus).getFlags());
					if (isPrivate && !CharOperation.equals(methodBinding.declaringClass.sourceName, focus.getParent().getElementName().toCharArray())) {
						return; // finally the match was not possible
					}
				}
			}
			matchReportReference((MessageSend)reference, locator, ((MessageSend)reference).binding);
		} else {
			if (reference instanceof SingleMemberAnnotation) {
				reference = ((SingleMemberAnnotation)reference).memberValuePairs()[0];
				match.setImplicit(true);
			}
			int offset = reference.sourceStart;
			int length =  reference.sourceEnd - offset + 1;
			match.setOffset(offset);
			match.setLength(length);
			locator.report(match);
		}
	}
}
void matchReportReference(MessageSend messageSend, MatchLocator locator, MethodBinding methodBinding) throws CoreException {

	// Look if there's a need to special report for parameterized type
	boolean isParameterized = false;
	if (methodBinding instanceof ParameterizedGenericMethodBinding) { // parameterized generic method
		isParameterized = true;

		// Update match regarding method type arguments
		ParameterizedGenericMethodBinding parameterizedMethodBinding = (ParameterizedGenericMethodBinding) methodBinding;
		match.setRaw(parameterizedMethodBinding.isRaw);
		TypeBinding[] typeArguments = /*parameterizedMethodBinding.isRaw ? null :*/ parameterizedMethodBinding.typeArguments;
		updateMatch(typeArguments, locator, this.pattern.methodArguments, this.pattern.hasMethodParameters());

		// Update match regarding declaring class type arguments
		if (methodBinding.declaringClass.isParameterizedType() || methodBinding.declaringClass.isRawType()) {
			ParameterizedTypeBinding parameterizedBinding = (ParameterizedTypeBinding)methodBinding.declaringClass;
			if (!this.pattern.hasTypeArguments() && this.pattern.hasMethodArguments()) {
				// special case for pattern which defines method arguments but not its declaring type
				// in this case, we do not refine accuracy using declaring type arguments...!
			} else {
				updateMatch(parameterizedBinding, this.pattern.getTypeArguments(), this.pattern.hasTypeParameters(), 0, locator);
			}
		} else if (this.pattern.hasTypeArguments()) {
			match.setRule(SearchPattern.R_ERASURE_MATCH);
		}

		// Update match regarding method parameters
		// TODO ? (frederic)

		// Update match regarding method return type
		// TODO ? (frederic)

		// Special case for errors
		if (match.getRule() != 0 && messageSend.resolvedType == null) {
			match.setRule(SearchPattern.R_ERASURE_MATCH);
		}
	} else if (methodBinding instanceof ParameterizedMethodBinding) {
		isParameterized = true;
		if (methodBinding.declaringClass.isParameterizedType() || methodBinding.declaringClass.isRawType()) {
			ParameterizedTypeBinding parameterizedBinding = (ParameterizedTypeBinding)methodBinding.declaringClass;
			updateMatch(parameterizedBinding, this.pattern.getTypeArguments(), this.pattern.hasTypeParameters(), 0, locator);
		} else if (this.pattern.hasTypeArguments()) {
			match.setRule(SearchPattern.R_ERASURE_MATCH);
		}

		// Update match regarding method parameters
		// TODO ? (frederic)

		// Update match regarding method return type
		// TODO ? (frederic)

		// Special case for errors
		if (match.getRule() != 0 && messageSend.resolvedType == null) {
			match.setRule(SearchPattern.R_ERASURE_MATCH);
		}
	} else if (this.pattern.hasMethodArguments()) { // binding has no type params, compatible erasure if pattern does
		match.setRule(SearchPattern.R_ERASURE_MATCH);
	}

	// See whether it is necessary to report or not
	if (match.getRule() == 0) return; // impossible match
	boolean report = (this.isErasureMatch && match.isErasure()) || (this.isEquivalentMatch && match.isEquivalent()) || match.isExact();
	if (!report) return;

	// Report match
	int offset = (int) (messageSend.nameSourcePosition >>> 32);
	match.setOffset(offset);
	match.setLength(messageSend.sourceEnd - offset + 1);
	 if (isParameterized && this.pattern.hasMethodArguments())  {
		locator.reportAccurateParameterizedMethodReference(match, messageSend, messageSend.typeArguments);
	} else {
		locator.report(match);
	}
}
protected int referenceType() {
	return IJavaElement.METHOD;
}
public SearchMatch newDeclarationMatch(ASTNode reference, IJavaElement element, Binding elementBinding, int accuracy, int length, MatchLocator locator) {
	if (elementBinding != null) {
		MethodBinding methodBinding = (MethodBinding) elementBinding;
		// Redo arguments verif as in this case previous filter may accept different ones
		boolean equals = true;
		if (this.pattern.parameterSimpleNames != null) {
			int paramLength = this.pattern.parameterSimpleNames.length;
			for (int i=0; equals && i<paramLength; i++) {
				int level = resolveLevelForType(this.pattern.parameterSimpleNames[i], this.pattern.parameterQualifications[i], methodBinding.parameters[i]);
				if (level == IMPOSSIBLE_MATCH) equals = false;
			}
		}
		// If arguments are not equals then try to see if method arguments can match erasures in hierarchy
		if (!equals && this.pattern.findDeclarations && this.mayBeGeneric) {
			if (isErasureMethodOverride(methodBinding.declaringClass, methodBinding)) {
				return super.newDeclarationMatch(reference, element, elementBinding, accuracy, length, locator);
			}
			if (isTypeInSuperDeclaringTypeNames(methodBinding.declaringClass.compoundName)) {
				MethodBinding patternBinding = locator.getMethodBinding(this.pattern);
				if (patternBinding != null) {
					patternBinding = patternBinding.original();
					if (!isErasureMethodOverride(patternBinding.declaringClass, patternBinding)) {
						return null;
					}
				}
				return super.newDeclarationMatch(reference, element, elementBinding, accuracy, length, locator);
			}
			return null;
		}
	}
	return super.newDeclarationMatch(reference, element, elementBinding, accuracy, length, locator);
}
protected void reportDeclaration(MethodBinding methodBinding, MatchLocator locator, SimpleSet knownMethods) throws CoreException {
	ReferenceBinding declaringClass = methodBinding.declaringClass;
	IType type = locator.lookupType(declaringClass);
	if (type == null) return; // case of a secondary type

	char[] bindingSelector = methodBinding.selector;
	TypeBinding[] parameters = methodBinding.original().parameters;
	int parameterLength = parameters.length;
	String[] parameterTypes = new String[parameterLength];
	for (int i = 0; i  < parameterLength; i++) {
		char[] typeName = parameters[i].shortReadableName();
		parameterTypes[i] = Signature.createTypeSignature(typeName, false);
	}
	IMethod method = type.getMethod(new String(bindingSelector), parameterTypes);
	if (knownMethods.includes(method)) return;

	knownMethods.add(method);
	IResource resource = type.getResource();
	boolean isBinary = type.isBinary();
	IBinaryType info = null;
	if (isBinary) {
		if (resource == null)
			resource = type.getJavaProject().getProject();
		info = locator.getBinaryInfo((org.eclipse.jdt.internal.core.ClassFile)type.getClassFile(), resource);
		locator.reportBinaryMemberDeclaration(resource, method, methodBinding, info, SearchMatch.A_ACCURATE);
	} else {
		if (declaringClass instanceof ParameterizedTypeBinding)
			declaringClass = ((ParameterizedTypeBinding) declaringClass).type;
		ClassScope scope = ((SourceTypeBinding) declaringClass).scope;
		if (scope != null) {
			TypeDeclaration typeDecl = scope.referenceContext;
			AbstractMethodDeclaration methodDecl = null;
			AbstractMethodDeclaration[] methodDecls = typeDecl.methods;
			for (int i = 0, length = methodDecls.length; i < length; i++) {
				if (CharOperation.equals(bindingSelector, methodDecls[i].selector)) {
					methodDecl = methodDecls[i];
					break;
				}
			} 
			if (methodDecl != null) {
				int offset = methodDecl.sourceStart;
				Binding binding = methodDecl.binding;
				if (binding != null)
					method = (IMethod) ((JavaElement) method).resolved(binding);
				match = new MethodDeclarationMatch(method, SearchMatch.A_ACCURATE, offset, methodDecl.sourceEnd-offset+1, locator.getParticipant(), resource);
				locator.report(match);
			}
		}
	}
}
public int resolveLevel(ASTNode possibleMatchingNode) {
	if (this.pattern.findReferences) {
		if (possibleMatchingNode instanceof MessageSend) {
			return resolveLevel((MessageSend) possibleMatchingNode);
		}
		if (possibleMatchingNode instanceof SingleMemberAnnotation) {
			SingleMemberAnnotation annotation = (SingleMemberAnnotation) possibleMatchingNode;
			return resolveLevel(annotation.memberValuePairs()[0].binding);
		}
		if (possibleMatchingNode instanceof MemberValuePair) {
			MemberValuePair memberValuePair = (MemberValuePair) possibleMatchingNode;
			return resolveLevel(memberValuePair.binding);
		}
	}
	if (this.pattern.findDeclarations) {
		if (possibleMatchingNode instanceof MethodDeclaration) {
			return resolveLevel(((MethodDeclaration) possibleMatchingNode).binding);
		}
	}
	return IMPOSSIBLE_MATCH;
}
public int resolveLevel(Binding binding) {
	if (binding == null) return INACCURATE_MATCH;
	if (!(binding instanceof MethodBinding)) return IMPOSSIBLE_MATCH;

	MethodBinding method = (MethodBinding) binding;
	boolean skipVerif = this.pattern.findDeclarations && this.mayBeGeneric;
	int methodLevel = matchMethod(method, skipVerif);
	if (methodLevel == IMPOSSIBLE_MATCH) {
		if (method != method.original()) methodLevel = matchMethod(method.original(), skipVerif);
		if (methodLevel == IMPOSSIBLE_MATCH) {
			return IMPOSSIBLE_MATCH;
		} else {
			method = method.original();
		}
	}

	// declaring type
	char[] qualifiedPattern = qualifiedPattern(this.pattern.declaringSimpleName, this.pattern.declaringQualification);
	if (qualifiedPattern == null) return methodLevel; // since any declaring class will do

	boolean subType = !method.isStatic() && !method.isPrivate();
	if (subType && this.pattern.declaringQualification != null && method.declaringClass != null && method.declaringClass.fPackage != null) {
		subType = CharOperation.compareWith(this.pattern.declaringQualification, method.declaringClass.fPackage.shortReadableName()) == 0;
	}
	int declaringLevel = subType
		? resolveLevelAsSubtype(qualifiedPattern, method.declaringClass)
		: resolveLevelForType(qualifiedPattern, method.declaringClass);
	return methodLevel > declaringLevel ? declaringLevel : methodLevel; // return the weaker match
}
protected int resolveLevel(MessageSend messageSend) {
	MethodBinding method = messageSend.binding;
	if (method == null) return INACCURATE_MATCH;
	if (messageSend.resolvedType == null) {
		// Closest match may have different argument numbers when ProblemReason is NotFound
		// see MessageSend#resolveType(BlockScope)
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=97322
		int argLength = messageSend.arguments == null ? 0 : messageSend.arguments.length;
		if (pattern.parameterSimpleNames == null || argLength == pattern.parameterSimpleNames.length) {
			return INACCURATE_MATCH;
		}
		return IMPOSSIBLE_MATCH;
	}
	
	int methodLevel = matchMethod(method, false);
	if (methodLevel == IMPOSSIBLE_MATCH) {
		if (method != method.original()) methodLevel = matchMethod(method.original(), false);
		if (methodLevel == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;
		method = method.original();
	}

	// receiver type
	char[] qualifiedPattern = qualifiedPattern(this.pattern.declaringSimpleName, this.pattern.declaringQualification);
	if (qualifiedPattern == null) return methodLevel; // since any declaring class will do

	int declaringLevel;
	if (isVirtualInvoke(method, messageSend) && !(messageSend.actualReceiverType instanceof ArrayBinding)) {
		declaringLevel = resolveLevelAsSubtype(qualifiedPattern, method.declaringClass);
		if (declaringLevel == IMPOSSIBLE_MATCH) {
			if (method.declaringClass == null || this.allSuperDeclaringTypeNames == null) {
				declaringLevel = INACCURATE_MATCH;
			} else {
				char[][] compoundName = method.declaringClass.compoundName;
				for (int i = 0, max = this.allSuperDeclaringTypeNames.length; i < max; i++)
					if (CharOperation.equals(this.allSuperDeclaringTypeNames[i], compoundName))
						return methodLevel; // since this is an ACCURATE_MATCH so return the possibly weaker match
			}
		}
	} else {
		declaringLevel = resolveLevelForType(qualifiedPattern, method.declaringClass);
	}
	return methodLevel > declaringLevel ? declaringLevel : methodLevel; // return the weaker match
}
/**
 * Returns whether the given reference type binding matches or is a subtype of a type
 * that matches the given qualified pattern.
 * Returns ACCURATE_MATCH if it does.
 * Returns INACCURATE_MATCH if resolve fails
 * Returns IMPOSSIBLE_MATCH if it doesn't.
 */
protected int resolveLevelAsSubtype(char[] qualifiedPattern, ReferenceBinding type) {
	if (type == null) return INACCURATE_MATCH;

	int level = resolveLevelForType(qualifiedPattern, type);
	if (level != IMPOSSIBLE_MATCH) return level;

	// matches superclass
	if (!type.isInterface() && !CharOperation.equals(type.compoundName, TypeConstants.JAVA_LANG_OBJECT)) {
		level = resolveLevelAsSubtype(qualifiedPattern, type.superclass());
		if (level != IMPOSSIBLE_MATCH) return level;
	}

	// matches interfaces
	ReferenceBinding[] interfaces = type.superInterfaces();
	if (interfaces == null) return INACCURATE_MATCH;
	for (int i = 0; i < interfaces.length; i++) {
		level = resolveLevelAsSubtype(qualifiedPattern, interfaces[i]);
		if (level != IMPOSSIBLE_MATCH) return level;
	}
	return IMPOSSIBLE_MATCH;
}
public String toString() {
	return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
}
}
