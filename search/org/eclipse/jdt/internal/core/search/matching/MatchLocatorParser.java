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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

/**
 * A parser that locates ast nodes that match a given search pattern.
 */
public class MatchLocatorParser extends Parser {

MatchingNodeSet nodeSet;
PatternLocator patternLocator;
private ASTVisitor localDeclarationVisitor;

public static MatchLocatorParser createParser(ProblemReporter problemReporter, MatchLocator locator) {
	if ((locator.matchContainer & PatternLocator.COMPILATION_UNIT_CONTAINER) != 0)
		return new ImportMatchLocatorParser(problemReporter, locator);
	return new MatchLocatorParser(problemReporter, locator);
}

/**
 * An ast visitor that visits local type declarations.
 */
public class NoClassNoMethodDeclarationVisitor extends ASTVisitor {
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		return (constructorDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		return (fieldDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type;
	}
	public boolean visit(Initializer initializer, MethodScope scope) {
		return (initializer.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		return (methodDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
}
public class MethodButNoClassDeclarationVisitor extends NoClassNoMethodDeclarationVisitor {
	public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		patternLocator.match(localTypeDeclaration, nodeSet);
		return true;
	}
}
public class ClassButNoMethodDeclarationVisitor extends ASTVisitor {
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		patternLocator.match(constructorDeclaration, nodeSet);
		return (constructorDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		patternLocator.match(fieldDeclaration, nodeSet);
		return (fieldDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type;
	}
	public boolean visit(Initializer initializer, MethodScope scope) {
		patternLocator.match(initializer, nodeSet);
		return (initializer.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
	public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		patternLocator.match(memberTypeDeclaration, nodeSet);
		return true;
	}
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		patternLocator.match(methodDeclaration, nodeSet);
		return (methodDeclaration.bits & ASTNode.HasLocalTypeMASK) != 0; // continue only if it has local type
	}
}
public class ClassAndMethodDeclarationVisitor extends ClassButNoMethodDeclarationVisitor {
	public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		patternLocator.match(localTypeDeclaration, nodeSet);
		return true;
	}
}

protected MatchLocatorParser(ProblemReporter problemReporter, MatchLocator locator) {
	super(problemReporter, true);
	this.reportOnlyOneSyntaxError = true;
	this.patternLocator = locator.patternLocator;
	if ((locator.matchContainer & PatternLocator.CLASS_CONTAINER) != 0) {
		this.localDeclarationVisitor = (locator.matchContainer & PatternLocator.METHOD_CONTAINER) != 0
			? new ClassAndMethodDeclarationVisitor()
			: new ClassButNoMethodDeclarationVisitor();
	} else {
		this.localDeclarationVisitor = (locator.matchContainer & PatternLocator.METHOD_CONTAINER) != 0
			? new MethodButNoClassDeclarationVisitor()
			: new NoClassNoMethodDeclarationVisitor();
	}
}
public void checkComment() {
	super.checkComment();
	if (this.javadocParser.checkDocComment && this.javadoc != null) {
		// Search for pattern locator matches in javadoc comment @throws/@exception tags
		TypeReference[] thrownExceptions = this.javadoc.thrownExceptions;
		int throwsTagsLength = thrownExceptions == null ? 0 : thrownExceptions.length;
		for (int i = 0; i < throwsTagsLength; i++) {
			TypeReference typeRef = thrownExceptions[i];
			this.patternLocator.match(typeRef, this.nodeSet);
		}

		// Search for pattern locator matches in javadoc comment @see tags
		Expression[] references = this.javadoc.references;
		int seeTagsLength = references == null ? 0 : references.length;
		for (int i = 0; i < seeTagsLength; i++) {
			Expression reference = references[i];
			if (reference instanceof TypeReference) {
				TypeReference typeRef = (TypeReference) reference;
				this.patternLocator.match(typeRef, this.nodeSet);
			} else if (reference instanceof JavadocFieldReference) {
				JavadocFieldReference fieldRef = (JavadocFieldReference) reference;
				this.patternLocator.match(fieldRef, this.nodeSet);
				if (fieldRef.receiver instanceof TypeReference && !fieldRef.receiver.isThis()) {
					TypeReference typeRef = (TypeReference) fieldRef.receiver;
					this.patternLocator.match(typeRef, this.nodeSet);
				}
			} else if (reference instanceof JavadocMessageSend) {
				JavadocMessageSend messageSend = (JavadocMessageSend) reference;
				this.patternLocator.match(messageSend, this.nodeSet);
				if (messageSend.receiver instanceof TypeReference && !messageSend.receiver.isThis()) {
					TypeReference typeRef = (TypeReference) messageSend.receiver;
					this.patternLocator.match(typeRef, this.nodeSet);
				}
			} else if (reference instanceof JavadocAllocationExpression) {
				JavadocAllocationExpression constructor = (JavadocAllocationExpression) reference;
				this.patternLocator.match(constructor, this.nodeSet);
				if (constructor.type != null && !constructor.type.isThis()) {
					this.patternLocator.match(constructor.type, this.nodeSet);
				}
			}
		}
	}
}
protected void classInstanceCreation(boolean alwaysQualified) {
	super.classInstanceCreation(alwaysQualified);
	this.patternLocator.match(this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeAssignment() {
	super.consumeAssignment();
	this.patternLocator.match(this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeExplicitConstructorInvocation(int flag, int recFlag) {
	super.consumeExplicitConstructorInvocation(flag, recFlag);
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeExplicitConstructorInvocationWithTypeArguments(int flag, int recFlag) {
	super.consumeExplicitConstructorInvocationWithTypeArguments(flag, recFlag);
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeFieldAccess(boolean isSuperAccess) {
	super.consumeFieldAccess(isSuperAccess);

	// this is always a Reference
	this.patternLocator.match((Reference) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeLocalVariableDeclaration() {
	super.consumeLocalVariableDeclaration();

	// this is always a LocalDeclaration
	this.patternLocator.match((LocalDeclaration) this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeMethodInvocationName() {
	super.consumeMethodInvocationName();

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeMethodInvocationNameWithTypeArguments() {
	super.consumeMethodInvocationNameWithTypeArguments();

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeMethodInvocationPrimary() {
	super.consumeMethodInvocationPrimary(); 

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeMethodInvocationPrimaryWithTypeArguments() {
	super.consumeMethodInvocationPrimaryWithTypeArguments();

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeMethodInvocationSuper() {
	super.consumeMethodInvocationSuper();

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumeMethodInvocationSuperWithTypeArguments() {
	super.consumeMethodInvocationSuperWithTypeArguments();

	// this is always a MessageSend
	this.patternLocator.match((MessageSend) this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected void consumePrimaryNoNewArray() {
	// pop parenthesis positions (and don't update expression positions
	// (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=23329)
	intPtr--;
	intPtr--;
}

protected void consumePrimaryNoNewArrayWithName() {
	// PrimaryNoNewArray ::=  PushLPAREN Expression PushRPAREN 
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	// pop parenthesis positions (and don't update expression positions
	// (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=23329)
	intPtr--;
	intPtr--;
}
protected void consumeUnaryExpression(int op, boolean post) {
	super.consumeUnaryExpression(op, post);
	this.patternLocator.match(this.expressionStack[this.expressionPtr], this.nodeSet);
}
protected TypeReference copyDims(TypeReference typeRef, int dim) {
	TypeReference result = super.copyDims(typeRef, dim);
	 if (this.nodeSet.removePossibleMatch(typeRef) != null)
		this.nodeSet.addPossibleMatch(result);
	 else if (this.nodeSet.removeTrustedMatch(typeRef) != null)
		this.nodeSet.addTrustedMatch(result, true);
	return result;
}
protected TypeReference getTypeReference(int dim) {
	TypeReference typeRef = super.getTypeReference(dim);
	this.patternLocator.match(typeRef, this.nodeSet); // NB: Don't check container since type reference can happen anywhere
	return typeRef;
}
protected NameReference getUnspecifiedReference() {
	NameReference nameRef = super.getUnspecifiedReference();
	this.patternLocator.match(nameRef, this.nodeSet); // NB: Don't check container since unspecified reference can happen anywhere
	return nameRef;
}
protected NameReference getUnspecifiedReferenceOptimized() {
	NameReference nameRef = super.getUnspecifiedReferenceOptimized();
	this.patternLocator.match(nameRef, this.nodeSet); // NB: Don't check container since unspecified reference can happen anywhere
	return nameRef;
}
/**
 * Parses the method bodies in the given compilation unit
 * @param unit CompilationUnitDeclaration
 */
public void parseBodies(CompilationUnitDeclaration unit) {
	TypeDeclaration[] types = unit.types;
	if (types == null) return;

	for (int i = 0; i < types.length; i++) {
		TypeDeclaration type = types[i];
		this.patternLocator.match(type, this.nodeSet);
		this.parseBodies(type, unit);
	}
}
/**
 * Parses the member bodies in the given type.
 * @param type TypeDeclaration
 * @param unit CompilationUnitDeclaration
 */
protected void parseBodies(TypeDeclaration type, CompilationUnitDeclaration unit) {
	FieldDeclaration[] fields = type.fields;
	if (fields != null) {
		for (int i = 0; i < fields.length; i++) {
			FieldDeclaration field = fields[i];
			if (field instanceof Initializer)
				this.parse((Initializer) field, type, unit);
			field.traverse(localDeclarationVisitor, null);
		}
	}

	AbstractMethodDeclaration[] methods = type.methods;
	if (methods != null) {
		for (int i = 0; i < methods.length; i++) {
			AbstractMethodDeclaration method = methods[i];
			if (method.sourceStart >= type.bodyStart) { // if not synthetic
				if (method instanceof MethodDeclaration) {
					MethodDeclaration methodDeclaration = (MethodDeclaration) method;
					this.parse(methodDeclaration, unit);
					methodDeclaration.traverse(localDeclarationVisitor, (ClassScope) null);
				} else if (method instanceof ConstructorDeclaration) {
					ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) method;
					this.parse(constructorDeclaration, unit);
					constructorDeclaration.traverse(localDeclarationVisitor, (ClassScope) null);
				}
			} else if (method.isDefaultConstructor()) {
				method.parseStatements(this, unit);
			}
		}
	}

	TypeDeclaration[] memberTypes = type.memberTypes;
	if (memberTypes != null) {
		for (int i = 0; i < memberTypes.length; i++) {
			TypeDeclaration memberType = memberTypes[i];
			this.parseBodies(memberType, unit);
			memberType.traverse(localDeclarationVisitor, (ClassScope) null);
		}
	}
}
}

class ImportMatchLocatorParser extends MatchLocatorParser {

protected ImportMatchLocatorParser(ProblemReporter problemReporter, MatchLocator locator) {
	super(problemReporter, locator);
}
protected void consumeStaticImportOnDemandDeclarationName() {
	super.consumeStaticImportOnDemandDeclarationName();
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeSingleStaticImportDeclarationName() {
	super.consumeSingleStaticImportDeclarationName();
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeSingleTypeImportDeclarationName() {
	super.consumeSingleTypeImportDeclarationName();
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
protected void consumeTypeImportOnDemandDeclarationName() {
	super.consumeTypeImportOnDemandDeclarationName();
	this.patternLocator.match(this.astStack[this.astPtr], this.nodeSet);
}
}