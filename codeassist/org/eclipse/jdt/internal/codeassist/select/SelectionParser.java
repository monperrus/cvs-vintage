/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.select;

/*
 * Parser able to build specific completion parse nodes, given a cursorLocation.
 *
 * Cursor location denotes the position of the last character behind which completion
 * got requested:
 *  -1 means completion at the very beginning of the source
 *	0  means completion behind the first character
 *  n  means completion behind the n-th character
 */

import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.*;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.impl.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.Util;

public class SelectionParser extends AssistParser {
	// OWNER
	protected static final int SELECTION_PARSER = 1024;
	protected static final int SELECTION_OR_ASSIST_PARSER = ASSIST_PARSER + SELECTION_PARSER;

	// KIND : all values known by SelectionParser are between 1025 and 1549
	protected static final int K_BETWEEN_CASE_AND_COLON = SELECTION_PARSER + 1; // whether we are inside a block
	protected static final int K_INSIDE_RETURN_STATEMENT = SELECTION_PARSER + 2; // whether we are between the keyword 'return' and the end of a return statement
	

	public ASTNode assistNodeParent; // the parent node of assist node

	/* public fields */

	public int selectionStart, selectionEnd;

	public static final char[] SUPER = "super".toCharArray(); //$NON-NLS-1$
	public static final char[] THIS = "this".toCharArray(); //$NON-NLS-1$

public SelectionParser(ProblemReporter problemReporter) {
	super(problemReporter);
	this.javadocParser.checkDocComment = true;
}
public char[] assistIdentifier(){
	return ((SelectionScanner)this.scanner).selectionIdentifier;
}
protected void attachOrphanCompletionNode(){
	if (this.isOrphanCompletionNode){
		ASTNode orphan = this.assistNode;
		this.isOrphanCompletionNode = false;


		/* if in context of a type, then persists the identifier into a fake field return type */
		if (this.currentElement instanceof RecoveredType){
			RecoveredType recoveredType = (RecoveredType)this.currentElement;
			/* filter out cases where scanner is still inside type header */
			if (recoveredType.foundOpeningBrace) {
				/* generate a pseudo field with a completion on type reference */
				if (orphan instanceof TypeReference){
					this.currentElement = this.currentElement.add(new SelectionOnFieldType((TypeReference)orphan), 0);
					return;
				}
			}
		}

		if (orphan instanceof Expression) {
			buildMoreCompletionContext((Expression)orphan);
		} else {
			Statement statement = (Statement) orphan;
			this.currentElement = this.currentElement.add(statement, 0);
		}
		this.currentToken = 0; // given we are not on an eof, we do not want side effects caused by looked-ahead token
	}
}
private void buildMoreCompletionContext(Expression expression) {
	ASTNode parentNode = null;

	int kind = topKnownElementKind(SELECTION_OR_ASSIST_PARSER);
	if(kind != 0) {
		int info = topKnownElementInfo(SELECTION_OR_ASSIST_PARSER);
		nextElement : switch (kind) {
			case K_BETWEEN_CASE_AND_COLON :
				if(this.expressionPtr > 0) {
					SwitchStatement switchStatement = new SwitchStatement();
					switchStatement.expression = this.expressionStack[this.expressionPtr - 1];
					if(this.astLengthPtr > -1 && this.astPtr > -1) {
						int length = this.astLengthStack[this.astLengthPtr];
						int newAstPtr = this.astPtr - length;
						ASTNode firstNode = this.astStack[newAstPtr + 1];
						if(length != 0 && firstNode.sourceStart > switchStatement.expression.sourceEnd) {
							switchStatement.statements = new Statement[length + 1];
							System.arraycopy(
								this.astStack,
								newAstPtr + 1,
								switchStatement.statements,
								0,
								length);
						}
					}
					CaseStatement caseStatement = new CaseStatement(expression, expression.sourceStart, expression.sourceEnd);
					if(switchStatement.statements == null) {
						switchStatement.statements = new Statement[]{caseStatement};
					} else {
						switchStatement.statements[switchStatement.statements.length - 1] = caseStatement;
					}
					parentNode = switchStatement;
					this.assistNodeParent = parentNode;
				}
				break nextElement;
			case K_INSIDE_RETURN_STATEMENT :
				if(info == this.bracketDepth) {
					ReturnStatement returnStatement = new ReturnStatement(expression, expression.sourceStart, expression.sourceEnd);
					parentNode = returnStatement;
					this.assistNodeParent = parentNode;
				}
				break nextElement;
		}
	}
	if(parentNode != null) {
		this.currentElement = this.currentElement.add((Statement)parentNode, 0);
	} else {
		this.currentElement = this.currentElement.add((Statement)wrapWithExplicitConstructorCallIfNeeded(expression), 0);
		if(this.lastCheckPoint < expression.sourceEnd) {
			this.lastCheckPoint = expression.sourceEnd + 1;
		}
	}
}
private boolean checkRecoveredType() {
	if (this.currentElement instanceof RecoveredType){
		/* check if current awaiting identifier is the completion identifier */
		if (this.indexOfAssistIdentifier() < 0) return false;

		if ((this.lastErrorEndPosition >= this.selectionStart)
			&& (this.lastErrorEndPosition <= this.selectionEnd+1)){
			return false;
		}
		RecoveredType recoveredType = (RecoveredType)this.currentElement;
		/* filter out cases where scanner is still inside type header */
		if (recoveredType.foundOpeningBrace) {
			this.assistNode = this.getTypeReference(0);
			this.lastCheckPoint = this.assistNode.sourceEnd + 1;
			this.isOrphanCompletionNode = true;
			return true;
		}
	}
	return false;
}
protected void classInstanceCreation(boolean hasClassBody) {

	// ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt

	// ClassBodyopt produces a null item on the astStak if it produces NO class body
	// An empty class body produces a 0 on the length stack.....


	if ((this.astLengthStack[this.astLengthPtr] == 1)
		&& (this.astStack[this.astPtr] == null)) {


		int index;
		if ((index = this.indexOfAssistIdentifier()) < 0) {
			super.classInstanceCreation(hasClassBody);
			return;
		} else if(this.identifierLengthPtr > -1 &&
					(this.identifierLengthStack[this.identifierLengthPtr] - 1) != index) {
			super.classInstanceCreation(hasClassBody);
			return;
		}
		QualifiedAllocationExpression alloc;
		this.astPtr--;
		this.astLengthPtr--;
		alloc = new SelectionOnQualifiedAllocationExpression();
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		int length;
		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}
		// trick to avoid creating a selection on type reference
		char [] oldIdent = assistIdentifier();
		setAssistIdentifier(null);
		alloc.type = getTypeReference(0);

		setAssistIdentifier(oldIdent);

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);

		this.assistNode = alloc;
		this.lastCheckPoint = alloc.sourceEnd + 1;
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	} else {
		super.classInstanceCreation(hasClassBody);
	}
}
protected void consumeArrayCreationExpressionWithoutInitializer() {
	// ArrayCreationWithoutArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs
	// ArrayCreationWithoutArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs

	super.consumeArrayCreationExpressionWithoutInitializer();

	ArrayAllocationExpression alloc = (ArrayAllocationExpression)this.expressionStack[this.expressionPtr];
	if (alloc.type == this.assistNode){
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	}
}
protected void consumeArrayCreationExpressionWithInitializer() {
	// ArrayCreationWithArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs ArrayInitializer

	super.consumeArrayCreationExpressionWithInitializer();

	ArrayAllocationExpression alloc = (ArrayAllocationExpression)this.expressionStack[this.expressionPtr];
	if (alloc.type == this.assistNode){
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	}
}
protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() {
	// ClassInstanceCreationExpression ::= Primary '.' 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt
	// ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt

	QualifiedAllocationExpression alloc;
	int length;
	if (((length = this.astLengthStack[this.astLengthPtr]) == 1) && (this.astStack[this.astPtr] == null)) {

		if (this.indexOfAssistIdentifier() < 0) {
			super.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
			return;
		}

		//NO ClassBody
		this.astPtr--;
		this.astLengthPtr--;
		alloc = new SelectionOnQualifiedAllocationExpression();
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}

		// trick to avoid creating a selection on type reference
		char [] oldIdent = assistIdentifier();
		setAssistIdentifier(null);
		alloc.type = getTypeReference(0);

		setAssistIdentifier(oldIdent);

		length = this.genericsLengthStack[this.genericsLengthPtr--];
		this.genericsPtr -= length;
		System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
		this.intPtr--; // remove the position of the '<'

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);

		this.assistNode = alloc;
		this.lastCheckPoint = alloc.sourceEnd + 1;
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	} else {
		super.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
	}

	this.expressionLengthPtr--;
	QualifiedAllocationExpression qae =
		(QualifiedAllocationExpression) this.expressionStack[this.expressionPtr--];
	qae.enclosingInstance = this.expressionStack[this.expressionPtr];
	this.expressionStack[this.expressionPtr] = qae;
	qae.sourceStart = qae.enclosingInstance.sourceStart;
}
protected void consumeClassInstanceCreationExpressionWithTypeArguments() {
	// ClassInstanceCreationExpression ::= 'new' TypeArguments ClassType '(' ArgumentListopt ')' ClassBodyopt
	AllocationExpression alloc;
	int length;
	if (((length = this.astLengthStack[this.astLengthPtr]) == 1)
		&& (this.astStack[this.astPtr] == null)) {

		if (this.indexOfAssistIdentifier() < 0) {
			super.consumeClassInstanceCreationExpressionWithTypeArguments();
			return;
		}

		//NO ClassBody
		this.astPtr--;
		this.astLengthPtr--;
		alloc = new SelectionOnQualifiedAllocationExpression();
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}

		// trick to avoid creating a selection on type reference
		char [] oldIdent = assistIdentifier();
		setAssistIdentifier(null);
		alloc.type = getTypeReference(0);

		setAssistIdentifier(oldIdent);

		length = this.genericsLengthStack[this.genericsLengthPtr--];
		this.genericsPtr -= length;
		System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
		this.intPtr--; // remove the position of the '<'

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);

		this.assistNode = alloc;
		this.lastCheckPoint = alloc.sourceEnd + 1;
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	} else {
		super.consumeClassInstanceCreationExpressionWithTypeArguments();
	}
}
protected void consumeEnterAnonymousClassBody(boolean qualified) {
	// EnterAnonymousClassBody ::= $empty

	if (this.indexOfAssistIdentifier() < 0) {
		super.consumeEnterAnonymousClassBody(qualified);
		return;
	}

	// trick to avoid creating a selection on type reference
	char [] oldIdent = assistIdentifier();
	setAssistIdentifier(null);
	TypeReference typeReference = getTypeReference(0);
	setAssistIdentifier(oldIdent);

	TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
	anonymousType.name = CharOperation.NO_CHAR;
	anonymousType.bits |= (ASTNode.IsAnonymousType|ASTNode.IsLocalType);
	QualifiedAllocationExpression alloc = new SelectionOnQualifiedAllocationExpression(anonymousType);
	markEnclosingMemberWithLocalType();
	pushOnAstStack(anonymousType);

	alloc.sourceEnd = this.rParenPos; //the position has been stored explicitly
	int argumentLength;
	if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= argumentLength;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			alloc.arguments = new Expression[argumentLength],
			0,
			argumentLength);
	}
	
	if (qualified) {
		this.expressionLengthPtr--;
		alloc.enclosingInstance = this.expressionStack[this.expressionPtr--];
	}

	alloc.type = typeReference;

	anonymousType.sourceEnd = alloc.sourceEnd;
	//position at the type while it impacts the anonymous declaration
	anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
	alloc.sourceStart = this.intStack[this.intPtr--];
	pushOnExpressionStack(alloc);

	this.assistNode = alloc;
	this.lastCheckPoint = alloc.sourceEnd + 1;
	if (!this.diet){
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
		this.currentToken = 0; // opening brace already taken into account
		this.hasReportedError = true;
	}

	anonymousType.bodyStart = this.scanner.currentPosition;
	this.listLength = 0; // will be updated when reading super-interfaces
	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = anonymousType.bodyStart;
		this.currentElement = this.currentElement.add(anonymousType, 0);
		this.currentToken = 0; // opening brace already taken into account
		this.lastIgnoredToken = -1;
	}
}
protected void consumeEnterVariable() {
	// EnterVariable ::= $empty
	// do nothing by default

	super.consumeEnterVariable();

	AbstractVariableDeclaration variable = (AbstractVariableDeclaration) this.astStack[this.astPtr];
	if (variable.type == this.assistNode){
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = false; // already attached inside variable decl
	}
}

protected void consumeExitVariableWithInitialization() {
	super.consumeExitVariableWithInitialization();

	// does not keep the initialization if selection is not inside
	AbstractVariableDeclaration variable = (AbstractVariableDeclaration) this.astStack[this.astPtr];
	int start = variable.initialization.sourceStart;
	int end =  variable.initialization.sourceEnd;
	if ((this.selectionStart < start) &&  (this.selectionEnd < start) ||
			(this.selectionStart > end) && (this.selectionEnd > end)) {
		variable.initialization = null;
	}

}

protected void consumeFieldAccess(boolean isSuperAccess) {
	// FieldAccess ::= Primary '.' 'Identifier'
	// FieldAccess ::= 'super' '.' 'Identifier'

	if (this.indexOfAssistIdentifier() < 0) {
		super.consumeFieldAccess(isSuperAccess);
		return;
	}
	FieldReference fieldReference =
		new SelectionOnFieldReference(
			this.identifierStack[this.identifierPtr],
			this.identifierPositionStack[this.identifierPtr--]);
	this.identifierLengthPtr--;
	if (isSuperAccess) { //considerates the fieldReferenceerence beginning at the 'super' ....
		fieldReference.sourceStart = this.intStack[this.intPtr--];
		fieldReference.receiver = new SuperReference(fieldReference.sourceStart, this.endPosition);
		pushOnExpressionStack(fieldReference);
	} else { //optimize push/pop
		if ((fieldReference.receiver = this.expressionStack[this.expressionPtr]).isThis()) { //fieldReferenceerence begins at the this
			fieldReference.sourceStart = fieldReference.receiver.sourceStart;
		}
		this.expressionStack[this.expressionPtr] = fieldReference;
	}
	this.assistNode = fieldReference;
	this.lastCheckPoint = fieldReference.sourceEnd + 1;
	if (!this.diet){
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	}
	this.isOrphanCompletionNode = true;
}
protected void consumeFormalParameter(boolean isVarArgs) {
	if (this.indexOfAssistIdentifier() < 0) {
		super.consumeFormalParameter(isVarArgs);
		if((!this.diet || this.dietInt != 0) && this.astPtr > -1) {
			Argument argument = (Argument) this.astStack[this.astPtr];
			if(argument.type == this.assistNode) {
				this.isOrphanCompletionNode = true;
				this.restartRecovery	= true;	// force to restart in recovery mode
				this.lastIgnoredToken = -1;
			}
		}
	} else {
		this.identifierLengthPtr--;
		char[] identifierName = this.identifierStack[this.identifierPtr];
		long namePositions = this.identifierPositionStack[this.identifierPtr--];
		int extendedDimensions = this.intStack[this.intPtr--];
		int endOfEllipsis = 0;
		if (isVarArgs) {
			endOfEllipsis = this.intStack[this.intPtr--];
		}
		int firstDimensions = this.intStack[this.intPtr--];
		final int typeDimensions = firstDimensions + extendedDimensions;
		TypeReference type = getTypeReference(typeDimensions);
		if (isVarArgs) {
			type = copyDims(type, typeDimensions + 1);
			if (extendedDimensions == 0) {
				type.sourceEnd = endOfEllipsis;
			}
			type.bits |= ASTNode.IsVarArgs; // set isVarArgs
		}
		int modifierPositions = this.intStack[this.intPtr--];
		this.intPtr--;
		Argument arg =
			new SelectionOnArgumentName(
				identifierName,
				namePositions,
				type,
				this.intStack[this.intPtr + 1] & ~ClassFileConstants.AccDeprecated); // modifiers
		arg.declarationSourceStart = modifierPositions;

		// consume annotations
		int length;
		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			System.arraycopy(
				this.expressionStack,
				(this.expressionPtr -= length) + 1,
				arg.annotations = new Annotation[length],
				0,
				length);
		}

		pushOnAstStack(arg);

		this.assistNode = arg;
		this.lastCheckPoint = (int) namePositions;
		this.isOrphanCompletionNode = true;

		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}

		/* if incomplete method header, listLength counter will not have been reset,
			indicating that some arguments are available on the stack */
		this.listLength++;
	}
}
protected void consumeInstanceOfExpression() {
	if (indexOfAssistIdentifier() < 0) {
		super.consumeInstanceOfExpression();
	} else {
		getTypeReference(this.intStack[this.intPtr--]);
		this.isOrphanCompletionNode = true;
		this.restartRecovery = true;
		this.lastIgnoredToken = -1;
	}
}
protected void consumeInstanceOfExpressionWithName() {
	if (indexOfAssistIdentifier() < 0) {
		super.consumeInstanceOfExpressionWithName();
	} else {
		getTypeReference(this.intStack[this.intPtr--]);
		this.isOrphanCompletionNode = true;
		this.restartRecovery = true;
		this.lastIgnoredToken = -1;
	}
}
protected void consumeLocalVariableDeclarationStatement() {
	super.consumeLocalVariableDeclarationStatement();

	// force to restart in recovery mode if the declaration contains the selection
	if (!this.diet) {
		LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr];
		if ((this.selectionStart >= localDeclaration.sourceStart)
				&&  (this.selectionEnd <= localDeclaration.sourceEnd)) {
			this.restartRecovery	= true;
			this.lastIgnoredToken = -1;
		}
	}
}
protected void consumeMarkerAnnotation() {
	int index;

	if ((index = this.indexOfAssistIdentifier()) < 0) {
		super.consumeMarkerAnnotation();
		return;
	}

	MarkerAnnotation markerAnnotation = null;
	int length = this.identifierLengthStack[this.identifierLengthPtr];
	TypeReference typeReference;

	/* retrieve identifiers subset and whole positions, the assist node positions
		should include the entire replaced source. */

	char[][] subset = identifierSubSet(index);
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);

	/* build specific assist on type reference */

	if (index == 0) {
		/* assist inside first identifier */
		typeReference = createSingleAssistTypeReference(
						assistIdentifier(),
						positions[0]);
	} else {
		/* assist inside subsequent identifier */
		typeReference =	createQualifiedAssistTypeReference(
						subset,
						assistIdentifier(),
						positions);
	}
	this.assistNode = typeReference;
	this.lastCheckPoint = typeReference.sourceEnd + 1;

	markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
	markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
	pushOnExpressionStack(markerAnnotation);
}
protected void consumeMemberValuePair() {
	if (this.indexOfAssistIdentifier() < 0) {
		super.consumeMemberValuePair();
		return;
	}

	char[] simpleName = this.identifierStack[this.identifierPtr];
	long position = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	int end = (int) position;
	int start = (int) (position >>> 32);
	Expression value = this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr--;
	MemberValuePair memberValuePair = new SelectionOnNameOfMemberValuePair(simpleName, start, end, value);
	pushOnAstStack(memberValuePair);

	this.assistNode = memberValuePair;
	this.lastCheckPoint = memberValuePair.sourceEnd + 1;


}
protected void consumeMethodInvocationName() {
	// MethodInvocation ::= Name '(' ArgumentListopt ')'

	// when the name is only an identifier...we have a message send to "this" (implicit)

	char[] selector = this.identifierStack[this.identifierPtr];
	int accessMode;
	if(selector == assistIdentifier()) {
		if(CharOperation.equals(selector, SUPER)) {
			accessMode = ExplicitConstructorCall.Super;
		} else if(CharOperation.equals(selector, THIS)) {
			accessMode = ExplicitConstructorCall.This;
		} else {
			super.consumeMethodInvocationName();
			return;
		}
	} else {
		super.consumeMethodInvocationName();
		return;
	}

	final ExplicitConstructorCall constructorCall = new SelectionOnExplicitConstructorCall(accessMode);
	constructorCall.sourceEnd = this.rParenPos;
	constructorCall.sourceStart = (int) (this.identifierPositionStack[this.identifierPtr] >>> 32);
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(this.expressionStack, this.expressionPtr + 1, constructorCall.arguments = new Expression[length], 0, length);
	}

	if (!this.diet){
		pushOnAstStack(constructorCall);
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	} else {
		pushOnExpressionStack(new Expression(){
			public TypeBinding resolveType(BlockScope scope) {
				constructorCall.resolve(scope);
				return null;
			}
			public StringBuffer printExpression(int indent, StringBuffer output) {
				return output;
			}
		});
	}
	this.assistNode = constructorCall;
	this.lastCheckPoint = constructorCall.sourceEnd + 1;
	this.isOrphanCompletionNode = true;
}
protected void consumeMethodInvocationPrimary() {
	//optimize the push/pop
	//MethodInvocation ::= Primary '.' 'Identifier' '(' ArgumentListopt ')'

	char[] selector = this.identifierStack[this.identifierPtr];
	int accessMode;
	if(selector == assistIdentifier()) {
		if(CharOperation.equals(selector, SUPER)) {
			accessMode = ExplicitConstructorCall.Super;
		} else if(CharOperation.equals(selector, THIS)) {
			accessMode = ExplicitConstructorCall.This;
		} else {
			super.consumeMethodInvocationPrimary();
			return;
		}
	} else {
		super.consumeMethodInvocationPrimary();
		return;
	}

	final ExplicitConstructorCall constructorCall = new SelectionOnExplicitConstructorCall(accessMode);
	constructorCall.sourceEnd = this.rParenPos;
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(this.expressionStack, this.expressionPtr + 1, constructorCall.arguments = new Expression[length], 0, length);
	}
	constructorCall.qualification = this.expressionStack[this.expressionPtr--];
	constructorCall.sourceStart = constructorCall.qualification.sourceStart;

	if (!this.diet){
		pushOnAstStack(constructorCall);
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	} else {
		pushOnExpressionStack(new Expression(){
			public TypeBinding resolveType(BlockScope scope) {
				constructorCall.resolve(scope);
				return null;
			}
			public StringBuffer printExpression(int indent, StringBuffer output) {
				return output;
			}
		});
	}

	this.assistNode = constructorCall;
	this.lastCheckPoint = constructorCall.sourceEnd + 1;
	this.isOrphanCompletionNode = true;
}
protected void consumeNormalAnnotation() {
	int index;

	if ((index = this.indexOfAssistIdentifier()) < 0) {
		super.consumeNormalAnnotation();
		return;
	}

	NormalAnnotation normalAnnotation = null;
	int length = this.identifierLengthStack[this.identifierLengthPtr];
	TypeReference typeReference;

	/* retrieve identifiers subset and whole positions, the assist node positions
		should include the entire replaced source. */

	char[][] subset = identifierSubSet(index);
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);

	/* build specific assist on type reference */

	if (index == 0) {
		/* assist inside first identifier */
		typeReference = createSingleAssistTypeReference(
						assistIdentifier(),
						positions[0]);
	} else {
		/* assist inside subsequent identifier */
		typeReference =	createQualifiedAssistTypeReference(
						subset,
						assistIdentifier(),
						positions);
	}
	this.assistNode = typeReference;
	this.lastCheckPoint = typeReference.sourceEnd + 1;

	normalAnnotation = new NormalAnnotation(typeReference, this.intStack[this.intPtr--]);
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		System.arraycopy(
			this.astStack,
			(this.astPtr -= length) + 1,
			normalAnnotation.memberValuePairs = new MemberValuePair[length],
			0,
			length);
	}
	normalAnnotation.declarationSourceEnd = this.rParenPos;
	pushOnExpressionStack(normalAnnotation);
}
protected void consumeSingleMemberAnnotation() {
	int index;

	if ((index = this.indexOfAssistIdentifier()) < 0) {
		super.consumeSingleMemberAnnotation();
		return;
	}

	SingleMemberAnnotation singleMemberAnnotation = null;
	int length = this.identifierLengthStack[this.identifierLengthPtr];
	TypeReference typeReference;

	/* retrieve identifiers subset and whole positions, the assist node positions
		should include the entire replaced source. */

	char[][] subset = identifierSubSet(index);
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);

	/* build specific assist on type reference */

	if (index == 0) {
		/* assist inside first identifier */
		typeReference = createSingleAssistTypeReference(
						assistIdentifier(),
						positions[0]);
	} else {
		/* assist inside subsequent identifier */
		typeReference =	createQualifiedAssistTypeReference(
						subset,
						assistIdentifier(),
						positions);
	}
	this.assistNode = typeReference;
	this.lastCheckPoint = typeReference.sourceEnd + 1;

	singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
	singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr--;
	singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
	pushOnExpressionStack(singleMemberAnnotation);
}
protected void consumeStaticImportOnDemandDeclarationName() {
	// TypeImportOnDemandDeclarationName ::= 'import' 'static' Name '.' '*'
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	int index;

	/* no need to take action if not inside assist identifiers */
	if ((index = indexOfAssistIdentifier()) < 0) {
		super.consumeStaticImportOnDemandDeclarationName();
		return;
	}
	/* retrieve identifiers subset and whole positions, the assist node positions
		should include the entire replaced source. */
	int length = this.identifierLengthStack[this.identifierLengthPtr];
	char[][] subset = identifierSubSet(index+1); // include the assistIdentifier
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);

	/* build specific assist node on import statement */
	ImportReference reference = createAssistImportReference(subset, positions, ClassFileConstants.AccStatic);
	reference.bits |= ASTNode.OnDemand;
	this.assistNode = reference;
	this.lastCheckPoint = reference.sourceEnd + 1;

	pushOnAstStack(reference);

	if (this.currentToken == TokenNameSEMICOLON){
		reference.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		reference.declarationSourceEnd = (int) positions[length-1];
	}
	//endPosition is just before the ;
	reference.declarationSourceStart = this.intStack[this.intPtr--];
	// flush annotations defined prior to import statements
	reference.declarationSourceEnd = flushCommentsDefinedPriorTo(reference.declarationSourceEnd);

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = reference.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(reference, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeToken(int token) {
	super.consumeToken(token);

	// if in a method or if in a field initializer
	if (isInsideMethod() || isInsideFieldInitialization()) {
		switch (token) {
			case TokenNamecase :
				pushOnElementStack(K_BETWEEN_CASE_AND_COLON);
				break;
			case TokenNameCOLON:
				if(topKnownElementKind(SELECTION_OR_ASSIST_PARSER) == K_BETWEEN_CASE_AND_COLON) {
					popElement(K_BETWEEN_CASE_AND_COLON);
				}
				break;
			case TokenNamereturn:
				pushOnElementStack(K_INSIDE_RETURN_STATEMENT, this.bracketDepth);
				break;
			case TokenNameSEMICOLON:
				switch(topKnownElementKind(SELECTION_OR_ASSIST_PARSER)) {
					case K_INSIDE_RETURN_STATEMENT :
						if(topKnownElementInfo(SELECTION_OR_ASSIST_PARSER) == this.bracketDepth) {
							popElement(K_INSIDE_RETURN_STATEMENT);
						}
						break;
				}
				break;
		}
	}
}
protected void consumeTypeImportOnDemandDeclarationName() {
	// TypeImportOnDemandDeclarationName ::= 'import' Name '.' '*'
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	int index;

	/* no need to take action if not inside assist identifiers */
	if ((index = indexOfAssistIdentifier()) < 0) {
		super.consumeTypeImportOnDemandDeclarationName();
		return;
	}
	/* retrieve identifiers subset and whole positions, the assist node positions
		should include the entire replaced source. */
	int length = this.identifierLengthStack[this.identifierLengthPtr];
	char[][] subset = identifierSubSet(index+1); // include the assistIdentifier
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);

	/* build specific assist node on import statement */
	ImportReference reference = createAssistImportReference(subset, positions, ClassFileConstants.AccDefault);
	reference.bits |= ASTNode.OnDemand;
	this.assistNode = reference;
	this.lastCheckPoint = reference.sourceEnd + 1;

	pushOnAstStack(reference);

	if (this.currentToken == TokenNameSEMICOLON){
		reference.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		reference.declarationSourceEnd = (int) positions[length-1];
	}
	//endPosition is just before the ;
	reference.declarationSourceStart = this.intStack[this.intPtr--];
	// flush comments defined prior to import statements
	reference.declarationSourceEnd = flushCommentsDefinedPriorTo(reference.declarationSourceEnd);

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = reference.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(reference, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
public ImportReference createAssistImportReference(char[][] tokens, long[] positions, int mod){
	return new SelectionOnImportReference(tokens, positions, mod);
}
public ImportReference createAssistPackageReference(char[][] tokens, long[] positions){
	return new SelectionOnPackageReference(tokens, positions);
}
protected JavadocParser createJavadocParser() {
	return new SelectionJavadocParser(this);
}
protected LocalDeclaration createLocalDeclaration(char[] assistName,int sourceStart,int sourceEnd) {
	if (this.indexOfAssistIdentifier() < 0) {
		return super.createLocalDeclaration(assistName, sourceStart, sourceEnd);
	} else {
		SelectionOnLocalName local = new SelectionOnLocalName(assistName, sourceStart, sourceEnd);
		this.assistNode = local;
		this.lastCheckPoint = sourceEnd + 1;
		return local;
	}
}
public NameReference createQualifiedAssistNameReference(char[][] previousIdentifiers, char[] assistName, long[] positions){
	return new SelectionOnQualifiedNameReference(
					previousIdentifiers,
					assistName,
					positions);
}
public TypeReference createQualifiedAssistTypeReference(char[][] previousIdentifiers, char[] assistName, long[] positions){
	return new SelectionOnQualifiedTypeReference(
					previousIdentifiers,
					assistName,
					positions);
}
public TypeReference createParameterizedQualifiedAssistTypeReference(
		char[][] tokens, TypeReference[][] typeArguments, char[] assistname, TypeReference[] assistTypeArguments, long[] positions) {
	return new SelectionOnParameterizedQualifiedTypeReference(tokens, assistname, typeArguments, assistTypeArguments, positions);

}
public NameReference createSingleAssistNameReference(char[] assistName, long position) {
	return new SelectionOnSingleNameReference(assistName, position);
}
public TypeReference createSingleAssistTypeReference(char[] assistName, long position) {
	return new SelectionOnSingleTypeReference(assistName, position);
}
public TypeReference createParameterizedSingleAssistTypeReference(TypeReference[] typeArguments, char[] assistName, long position) {
	return new SelectionOnParameterizedSingleTypeReference(assistName, typeArguments, position);
}
public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end) {

	this.selectionStart = start;
	this.selectionEnd = end;
	SelectionScanner selectionScanner = (SelectionScanner)this.scanner;
	selectionScanner.selectionIdentifier = null;
	selectionScanner.selectionStart = start;
	selectionScanner.selectionEnd = end;
	return this.dietParse(sourceUnit, compilationResult);
}
protected NameReference getUnspecifiedReference() {
	/* build a (unspecified) NameReference which may be qualified*/

	int completionIndex;

	/* no need to take action if not inside completed identifiers */
	if ((completionIndex = indexOfAssistIdentifier()) < 0) {
		return super.getUnspecifiedReference();
	}

	int length = this.identifierLengthStack[this.identifierLengthPtr];
	if (CharOperation.equals(assistIdentifier(), SUPER)){
		Reference reference;
		if (completionIndex > 0){ // qualified super
			// discard 'super' from identifier stacks
			this.identifierLengthStack[this.identifierLengthPtr] = completionIndex;
			int ptr = this.identifierPtr -= (length - completionIndex);
			pushOnGenericsLengthStack(0);
			pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
			reference =
				new SelectionOnQualifiedSuperReference(
					getTypeReference(0),
					(int)(this.identifierPositionStack[ptr+1] >>> 32),
					(int) this.identifierPositionStack[ptr+1]);
		} else { // standard super
			this.identifierPtr -= length;
			this.identifierLengthPtr--;
			reference = new SelectionOnSuperReference((int)(this.identifierPositionStack[this.identifierPtr+1] >>> 32), (int) this.identifierPositionStack[this.identifierPtr+1]);
		}
		pushOnAstStack(reference);
		this.assistNode = reference;
		this.lastCheckPoint = reference.sourceEnd + 1;
		if (!this.diet || this.dietInt != 0){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
		return new SingleNameReference(CharOperation.NO_CHAR, 0); // dummy reference
	}
	NameReference nameReference;
	/* retrieve identifiers subset and whole positions, the completion node positions
		should include the entire replaced source. */
	char[][] subset = identifierSubSet(completionIndex);
	this.identifierLengthPtr--;
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr + 1,
		positions,
		0,
		length);
	/* build specific completion on name reference */
	if (completionIndex == 0) {
		/* completion inside first identifier */
		nameReference = createSingleAssistNameReference(assistIdentifier(), positions[0]);
	} else {
		/* completion inside subsequent identifier */
		nameReference = createQualifiedAssistNameReference(subset, assistIdentifier(), positions);
	}
	this.assistNode = nameReference;
	this.lastCheckPoint = nameReference.sourceEnd + 1;
	if (!this.diet){
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	}
	this.isOrphanCompletionNode = true;
	return nameReference;
}
/*
 * Copy of code from superclass with the following change:
 * In the case of qualified name reference if the cursor location is on the
 * qualified name reference, then create a CompletionOnQualifiedNameReference
 * instead.
 */
protected NameReference getUnspecifiedReferenceOptimized() {

	int index = indexOfAssistIdentifier();
	NameReference reference = super.getUnspecifiedReferenceOptimized();

	if (index >= 0){
		if (!this.diet){
			this.restartRecovery	= true;	// force to restart in recovery mode
			this.lastIgnoredToken = -1;
		}
		this.isOrphanCompletionNode = true;
	}
	return reference;
}
public void initializeScanner(){
	this.scanner = new SelectionScanner(this.options.sourceLevel);
}
protected MessageSend newMessageSend() {
	// '(' ArgumentListopt ')'
	// the arguments are on the expression stack

	char[] selector = this.identifierStack[this.identifierPtr];
	if (selector != assistIdentifier()){
		return super.newMessageSend();
	}
	MessageSend messageSend = new SelectionOnMessageSend();
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			messageSend.arguments = new Expression[length],
			0,
			length);
	}
	this.assistNode = messageSend;
	if (!this.diet){
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	}

	this.isOrphanCompletionNode = true;
	return messageSend;
}
protected MessageSend newMessageSendWithTypeArguments() {
	char[] selector = this.identifierStack[this.identifierPtr];
	if (selector != assistIdentifier()){
		return super.newMessageSendWithTypeArguments();
	}
	MessageSend messageSend = new SelectionOnMessageSend();
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			messageSend.arguments = new Expression[length],
			0,
			length);
	}
	this.assistNode = messageSend;
	if (!this.diet){
		this.restartRecovery	= true;	// force to restart in recovery mode
		this.lastIgnoredToken = -1;
	}

	this.isOrphanCompletionNode = true;
	return messageSend;
}
public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end) {

	if (end == -1) return super.parse(sourceUnit, compilationResult, start, end);

	this.selectionStart = start;
	this.selectionEnd = end;
	SelectionScanner selectionScanner = (SelectionScanner)this.scanner;
	selectionScanner.selectionIdentifier = null;
	selectionScanner.selectionStart = start;
	selectionScanner.selectionEnd = end;
	return super.parse(sourceUnit, compilationResult, -1, -1/*parse without reseting the scanner*/);
}
/*
 * Reset context so as to resume to regular parse loop
 * If unable to reset for resuming, answers false.
 *
 * Move checkpoint location, reset internal stacks and
 * decide which grammar goal is activated.
 */
protected boolean resumeAfterRecovery() {

	/* if reached assist node inside method body, but still inside nested type,
		should continue in diet mode until the end of the method body */
	if (this.assistNode != null
		&& !(this.referenceContext instanceof CompilationUnitDeclaration)){
		this.currentElement.preserveEnclosingBlocks();
		if (this.currentElement.enclosingType() == null) {
			if(!(this.currentElement instanceof RecoveredType)) {
				resetStacks();
				return false;
			}

			RecoveredType recoveredType = (RecoveredType)this.currentElement;
			if(recoveredType.typeDeclaration != null && recoveredType.typeDeclaration.allocation == this.assistNode){
				resetStacks();
				return false;
			}
		}
	}
	return super.resumeAfterRecovery();
}

public void selectionIdentifierCheck(){
	if (checkRecoveredType()) return;
}
public void setAssistIdentifier(char[] assistIdent){
	((SelectionScanner)this.scanner).selectionIdentifier = assistIdent;
}
/*
 * Update recovery state based on current parser/scanner state
 */
protected void updateRecoveryState() {

	/* expose parser state to recovery state */
	this.currentElement.updateFromParserState();

	/* may be able to retrieve completionNode as an orphan, and then attach it */
	selectionIdentifierCheck();
	attachOrphanCompletionNode();

	// if an assist node has been found and a recovered element exists,
	// mark enclosing blocks as to be preserved
	if (this.assistNode != null && this.currentElement != null) {
		this.currentElement.preserveEnclosingBlocks();
	}

	/* check and update recovered state based on current token,
		this action is also performed when shifting token after recovery
		got activated once.
	*/
	recoveryTokenCheck();
}

public  String toString() {
	String s = Util.EMPTY_STRING;
	s = s + "elementKindStack : int[] = {"; //$NON-NLS-1$
	for (int i = 0; i <= this.elementPtr; i++) {
		s = s + String.valueOf(this.elementKindStack[i]) + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$
	s = s + "elementInfoStack : int[] = {"; //$NON-NLS-1$
	for (int i = 0; i <= this.elementPtr; i++) {
		s = s + String.valueOf(this.elementInfoStack[i]) + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$
	return s + super.toString();
}
}
