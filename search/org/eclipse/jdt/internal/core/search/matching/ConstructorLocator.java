/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ConstructorLocator extends PatternLocator {

protected ConstructorPattern pattern;

public ConstructorLocator(ConstructorPattern pattern) {
	super(pattern);

	this.pattern = pattern;
}
public int match(ASTNode node, MatchingNodeSet nodeSet) { // interested in ExplicitConstructorCall
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;
	if (!(node instanceof ExplicitConstructorCall)) return IMPOSSIBLE_MATCH;

	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		Expression[] args = ((ExplicitConstructorCall) node).arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
	}

	return nodeSet.addMatch(node, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
public int match(ConstructorDeclaration node, MatchingNodeSet nodeSet) {
	int referencesLevel = this.pattern.findReferences ? matchLevelForReferences(node) : IMPOSSIBLE_MATCH;
	int declarationsLevel = this.pattern.findDeclarations ? matchLevelForDeclarations(node) : IMPOSSIBLE_MATCH;

	return nodeSet.addMatch(node, referencesLevel >= declarationsLevel ? referencesLevel : declarationsLevel); // use the stronger match
}
public int match(Expression node, MatchingNodeSet nodeSet) { // interested in AllocationExpression
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;
	if (!(node instanceof AllocationExpression)) return IMPOSSIBLE_MATCH;

	// constructor name is simple type name
	AllocationExpression allocation = (AllocationExpression) node;
	char[][] typeName = allocation.type.getTypeName();
	if (this.pattern.declaringSimpleName != null && !matchesName(this.pattern.declaringSimpleName, typeName[typeName.length-1]))
		return IMPOSSIBLE_MATCH;

	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		Expression[] args = allocation.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
	}

	return nodeSet.addMatch(node, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
public int match(FieldDeclaration field, MatchingNodeSet nodeSet) {
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;
	// look only for enum constant
	if (field.type != null || !(field.initialization instanceof AllocationExpression)) return IMPOSSIBLE_MATCH;

	AllocationExpression allocation = (AllocationExpression) field.initialization;
	if (field.binding != null && field.binding.declaringClass != null) {
		if (this.pattern.declaringSimpleName != null && !matchesName(this.pattern.declaringSimpleName, field.binding.declaringClass.sourceName()))
			return IMPOSSIBLE_MATCH;
	}

	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		Expression[] args = allocation.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
	}

	return nodeSet.addMatch(field, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
//public int match(MethodDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
//public int match(MessageSend node, MatchingNodeSet nodeSet) - SKIP IT
//public int match(Reference node, MatchingNodeSet nodeSet) - SKIP IT
public int match(TypeDeclaration node, MatchingNodeSet nodeSet) {
	if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;

	// need to look for a generated default constructor
	return nodeSet.addMatch(node, ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
}
//public int match(TypeReference node, MatchingNodeSet nodeSet) - SKIP IT

protected int matchContainer() {
	if (this.pattern.findReferences) return ALL_CONTAINER; // handles both declarations + references & just references
	// COMPILATION_UNIT_CONTAINER - implicit constructor call: case of Y extends X and Y doesn't define any constructor
	// CLASS_CONTAINER - implicit constructor call: case of constructor declaration with no explicit super call
	// METHOD_CONTAINER - reference in another constructor
	// FIELD_CONTAINER - anonymous in a field initializer

	// declarations are only found in Class
	return CLASS_CONTAINER;
}
protected int matchLevelForReferences(ConstructorDeclaration constructor) {
	ExplicitConstructorCall constructorCall = constructor.constructorCall;
	if (constructorCall == null || constructorCall.accessMode != ExplicitConstructorCall.ImplicitSuper)
		return IMPOSSIBLE_MATCH;

	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		Expression[] args = constructorCall.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;
	}
	return ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
}
protected int matchLevelForDeclarations(ConstructorDeclaration constructor) {
	// constructor name is stored in selector field
	if (this.pattern.declaringSimpleName != null && !matchesName(this.pattern.declaringSimpleName, constructor.selector))
		return IMPOSSIBLE_MATCH;

	if (this.pattern.parameterSimpleNames != null) {
		int length = this.pattern.parameterSimpleNames.length;
		Argument[] args = constructor.arguments;
		int argsLength = args == null ? 0 : args.length;
		if (length != argsLength) return IMPOSSIBLE_MATCH;

		for (int i = 0; i < length; i++)
			if (!matchesTypeReference(this.pattern.parameterSimpleNames[i], args[i].type))
				return IMPOSSIBLE_MATCH;
	}

	return ((InternalSearchPattern)this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
}
public SearchMatch newDeclarationMatch(ASTNode reference, IJavaElement element, int accuracy, int length, MatchLocator locator) {
	SearchMatch match = null;
	int offset = reference.sourceStart;
	if (this.pattern.findReferences) {
		if (reference instanceof TypeDeclaration) {
			TypeDeclaration type = (TypeDeclaration) reference;
			AbstractMethodDeclaration[] methods = type.methods;
			if (methods != null) {
				for (int i = 0, max = methods.length; i < max; i++) {
					AbstractMethodDeclaration method = methods[i];
					boolean synthetic = method.isDefaultConstructor() && method.sourceStart < type.bodyStart;
					match = locator.newMethodReferenceMatch(element, accuracy, offset, length, method.isConstructor(), synthetic, method);
				}
			}
		} else if (reference instanceof ConstructorDeclaration) {
			ConstructorDeclaration constructor = (ConstructorDeclaration) reference;
			ExplicitConstructorCall call = constructor.constructorCall;
			boolean synthetic = call != null && call.isImplicitSuper();
			match = locator.newMethodReferenceMatch(element, accuracy, offset, length, constructor.isConstructor(), synthetic, constructor);
		}
	}
	if (match != null) {
		return match;
	}
	// super implementation...
    return locator.newDeclarationMatch(element, accuracy, reference.sourceStart, length);
}
public int resolveLevel(ASTNode node) {
	if (this.pattern.findReferences) {
		if (node instanceof AllocationExpression)
			return resolveLevel((AllocationExpression) node);
		if (node instanceof ExplicitConstructorCall)
			return resolveLevel(((ExplicitConstructorCall) node).binding);
		if (node instanceof TypeDeclaration)
			return resolveLevel((TypeDeclaration) node);
		if (node instanceof FieldDeclaration)
			return resolveLevel((FieldDeclaration) node);
	}
	if (node instanceof ConstructorDeclaration)
		return resolveLevel((ConstructorDeclaration) node, true);
	return IMPOSSIBLE_MATCH;
}
protected int referenceType() {
	return IJavaElement.METHOD;
}
protected int resolveLevel(AllocationExpression allocation) {
	// constructor name is simple type name
	char[][] typeName = allocation.type.getTypeName();
	if (this.pattern.declaringSimpleName != null && !matchesName(this.pattern.declaringSimpleName, typeName[typeName.length-1]))
		return IMPOSSIBLE_MATCH;

	return resolveLevel(allocation.binding);
}
protected int resolveLevel(FieldDeclaration field) {
	// only accept enum constants
	if (field.type != null || field.binding == null) return IMPOSSIBLE_MATCH;
	if (this.pattern.declaringSimpleName != null && !matchesName(this.pattern.declaringSimpleName, field.binding.type.sourceName()))
		return IMPOSSIBLE_MATCH;
	if (!(field.initialization instanceof AllocationExpression) || field.initialization.resolvedType.isLocalType()) return IMPOSSIBLE_MATCH;

	return resolveLevel(((AllocationExpression)field.initialization).binding);
}
public int resolveLevel(Binding binding) {
	if (binding == null) return INACCURATE_MATCH;
	if (!(binding instanceof MethodBinding)) return IMPOSSIBLE_MATCH;

	MethodBinding method = ((MethodBinding) binding).original();
	if (!method.isConstructor()) return IMPOSSIBLE_MATCH;

	// declaring type, simple name has already been matched by matchIndexEntry()
	int level = resolveLevelForType(this.pattern.declaringSimpleName, this.pattern.declaringQualification, method.declaringClass);
	if (level == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;

	// parameter types
	int parameterCount = this.pattern.parameterSimpleNames == null ? -1 : this.pattern.parameterSimpleNames.length;
	if (parameterCount > -1) {
		if (method.parameters == null) return INACCURATE_MATCH;
		if (parameterCount != method.parameters.length) return IMPOSSIBLE_MATCH;
		for (int i = 0; i < parameterCount; i++) {
			int newLevel = resolveLevelForType(this.pattern.parameterSimpleNames[i], this.pattern.parameterQualifications[i], method.parameters[i]);
			if (level > newLevel) {
				if (newLevel == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;
				level = newLevel; // can only be downgraded
			}
		}
	}
	return level;
}
protected int resolveLevel(ConstructorDeclaration constructor, boolean checkDeclarations) {
	int referencesLevel = IMPOSSIBLE_MATCH;
	if (this.pattern.findReferences) {
		ExplicitConstructorCall constructorCall = constructor.constructorCall;
		if (constructorCall != null && constructorCall.accessMode == ExplicitConstructorCall.ImplicitSuper) {
			// eliminate explicit super call as it will be treated with matchLevel(ExplicitConstructorCall, boolean)
			referencesLevel = resolveLevel(constructorCall.binding);
			if (referencesLevel == ACCURATE_MATCH) return ACCURATE_MATCH; // cannot get better
		}
	}
	if (!checkDeclarations) return referencesLevel;

	int declarationsLevel = this.pattern.findDeclarations ? resolveLevel(constructor.binding) : IMPOSSIBLE_MATCH;
	return referencesLevel >= declarationsLevel ? referencesLevel : declarationsLevel; // answer the stronger match
}
protected int resolveLevel(TypeDeclaration type) {
	// find default constructor
	AbstractMethodDeclaration[] methods = type.methods;
	if (methods != null) {
		for (int i = 0, length = methods.length; i < length; i++) {
			AbstractMethodDeclaration method = methods[i];
			if (method.isDefaultConstructor() && method.sourceStart < type.bodyStart) // if synthetic
				return resolveLevel((ConstructorDeclaration) method, false);
		}
	}
	return IMPOSSIBLE_MATCH;
}
/* (non-Javadoc)
 * Overrides PatternLocator method behavior in order to accept member pattern as X.Member
 * @see org.eclipse.jdt.internal.core.search.matching.PatternLocator#resolveLevelForType(char[], char[], org.eclipse.jdt.internal.compiler.lookup.TypeBinding)
 */
protected int resolveLevelForType (char[] simpleNamePattern, char[] qualificationPattern, TypeBinding type) {
	char[] qualifiedPattern = getQualifiedPattern(simpleNamePattern, qualificationPattern);
	int level = resolveLevelForType(qualifiedPattern, type);
	if (level == ACCURATE_MATCH || type == null) return level;
	boolean match = false;
	if (type.isMemberType() || type.isLocalType()) {
		if (qualificationPattern != null) {
			match = CharOperation.equals(qualifiedPattern, getQualifiedSourceName(type), this.isCaseSensitive);
		} else {
			match = CharOperation.equals(qualifiedPattern, type.sourceName(), this.isCaseSensitive);
		}
	} else if (qualificationPattern == null) {
		match = CharOperation.equals(qualifiedPattern, getQualifiedSourceName(type), this.isCaseSensitive);
	}
	return match ? ACCURATE_MATCH : IMPOSSIBLE_MATCH;
}
public String toString() {
	return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
}
}