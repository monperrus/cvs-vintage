/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt McCutchen
 *     		Partial fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=122995.
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public abstract class ASTNode implements TypeConstants, TypeIds {
	
	public int sourceStart, sourceEnd;

	// storage for internal flags (32 bits)						BIT USAGE
	public final static int Bit1 = 0x1; 						// return type (operator) | name reference kind (name ref) | add assertion (type decl) | useful empty statement (empty statement)
	public final static int Bit2 = 0x2; 						// return type (operator) | name reference kind (name ref) | has local type (type, method, field decl)
	public final static int Bit3 = 0x4; 						// return type (operator) | name reference kind (name ref) | implicit this (this ref)
	public final static int Bit4 = 0x8; 						// return type (operator) | first assignment to local (local decl) | undocumented empty block (block, type and method decl)
	public final static int Bit5 = 0x10; 						// value for return (expression) | has all method bodies (unit) | supertype ref (type ref)
	public final static int Bit6 = 0x20; 						// depth (name ref, msg) | ignore need cast check (cast expression)
	public final static int Bit7 = 0x40; 						// depth (name ref, msg) | operator (operator) | need runtime checkcast (cast expression) | label used (labelStatement)
	public final static int Bit8 = 0x80; 						// depth (name ref, msg) | operator (operator) | unsafe cast (cast expression)
	public final static int Bit9 = 0x100; 					// depth (name ref, msg) | operator (operator) | is local type (type decl)
	public final static int Bit10= 0x200; 					// depth (name ref, msg) | operator (operator) | is anonymous type (type decl)
	public final static int Bit11 = 0x400; 					// depth (name ref, msg) | operator (operator) | is member type (type decl)
	public final static int Bit12 = 0x800; 					// depth (name ref, msg) | operator (operator) | has abstract methods (type decl)
	public final static int Bit13 = 0x1000; 				// depth (name ref, msg) | is secondary type (type decl)
	public final static int Bit14 = 0x2000; 				// strictly assigned (reference lhs)
	public final static int Bit15 = 0x4000; 				// is unnecessary cast (expression) | is varargs (type ref) | isSubRoutineEscaping (try statement)
	public final static int Bit16 = 0x8000; 				// in javadoc comment (name ref, type ref, msg)
	public final static int Bit17 = 0x10000; 				// compound assigned (reference lhs)
	public final static int Bit18 = 0x20000;				// non null (expression)				
	public final static int Bit19 = 0x40000;
	public final static int Bit20 = 0x80000; 
	public final static int Bit21 = 0x100000; 		
	public final static int Bit22 = 0x200000; 			// parenthesis count (expression)
	public final static int Bit23 = 0x400000; 			// parenthesis count (expression)
	public final static int Bit24 = 0x800000; 			// parenthesis count (expression)
	public final static int Bit25 = 0x1000000; 			// parenthesis count (expression)
	public final static int Bit26 = 0x2000000; 			// parenthesis count (expression)
	public final static int Bit27 = 0x4000000; 			// parenthesis count (expression)
	public final static int Bit28 = 0x8000000; 			// parenthesis count (expression)
	public final static int Bit29 = 0x10000000; 		// parenthesis count (expression)
	public final static int Bit30 = 0x20000000; 		// elseif (if statement) | try block exit (try statement) | fall-through (case statement)
	public final static int Bit31 = 0x40000000; 		// local declaration reachable (local decl) | ignore raw type check (type ref) | discard entire assignment (assignment)
	public final static int Bit32 = 0x80000000; 		// reachable (statement)

	public final static long Bit32L = 0x80000000L; 		
	public final static long Bit33L = 0x100000000L;
	public final static long Bit34L = 0x200000000L;
	public final static long Bit35L = 0x400000000L;
	public final static long Bit36L = 0x800000000L;
	public final static long Bit37L = 0x1000000000L;
	public final static long Bit38L = 0x2000000000L;
	public final static long Bit39L = 0x4000000000L;
	public final static long Bit40L = 0x8000000000L;
	public final static long Bit41L = 0x10000000000L;
	public final static long Bit42L = 0x20000000000L;
	public final static long Bit43L = 0x40000000000L;
	public final static long Bit44L = 0x80000000000L;
	public final static long Bit45L = 0x100000000000L;
	public final static long Bit46L = 0x200000000000L;
	public final static long Bit47L = 0x400000000000L;
	public final static long Bit48L = 0x800000000000L;
	public final static long Bit49L = 0x1000000000000L;
	public final static long Bit50L = 0x2000000000000L;
	public final static long Bit51L = 0x4000000000000L;
	public final static long Bit52L = 0x8000000000000L;
	public final static long Bit53L = 0x10000000000000L;
	public final static long Bit54L = 0x20000000000000L;
	public final static long Bit55L = 0x40000000000000L;
	public final static long Bit56L = 0x80000000000000L;

	public int bits = IsReachable; 				// reachable by default

	// for operators 
	public static final int ReturnTypeIDMASK = Bit1|Bit2|Bit3|Bit4; 
	public static final int OperatorSHIFT = 6;	// Bit7 -> Bit12
	public static final int OperatorMASK = Bit7|Bit8|Bit9|Bit10|Bit11|Bit12; // 6 bits for operator ID

	// for binary expressions
	public static final int IsReturnedValue = Bit5; 

	// for cast expressions
	public static final int UnnecessaryCast = Bit15;
	public static final int DisableUnnecessaryCastCheck = Bit6;
	public static final int GenerateCheckcast = Bit7;
	public static final int UnsafeCast = Bit8;
	
	// for name references 
	public static final int RestrictiveFlagMASK = Bit1|Bit2|Bit3;	
	public static final int FirstAssignmentToLocal = Bit4;
	
	// for this reference
	public static final int IsImplicitThis = Bit3; 

	// for single name references
	public static final int DepthSHIFT = 5;	// Bit6 -> Bit13
	public static final int DepthMASK = Bit6|Bit7|Bit8|Bit9|Bit10|Bit11|Bit12|Bit13; // 8 bits for actual depth value (max. 255)

	// for statements 
	public static final int IsReachable = Bit32; 
	public static final int IsLocalDeclarationReachable = Bit31; 
	public static final int LabelUsed = Bit7;
	public static final int DocumentedFallthrough = Bit30;
	
	// try statements
	public static final int IsSubRoutineEscaping = Bit15;
	public static final int IsTryBlockExiting = Bit30;
	
	// for type declaration
	public static final int ContainsAssertion = Bit1;
	public static final int IsLocalType = Bit9;
	public static final int IsAnonymousType = Bit10; // used to test for anonymous 
	public static final int IsMemberType = Bit11; // local member do not know it is local at parse time (need to look at binding)
	public static final int HasAbstractMethods = Bit12; // used to promote abstract enums
	public static final int IsSecondaryType = Bit13; // used to test for secondary

	// for type, method and field declarations 
	public static final int HasLocalType = Bit2; // cannot conflict with AddAssertionMASK

	// for expression 
	public static final int ParenthesizedSHIFT = 21; // Bit22 -> Bit29
	public static final int ParenthesizedMASK = Bit22|Bit23|Bit24|Bit25|Bit26|Bit27|Bit28|Bit29; // 8 bits for parenthesis count value (max. 255)

	// for references on lhs of assignment
	public static final int IsStrictlyAssigned = Bit14; // set only for true assignments, as opposed to compound ones
	public static final int IsCompoundAssigned = Bit17; // set only for compound assignments, as opposed to other ones

	// for empty statement
	public static final int IsUsefulEmptyStatement = Bit1;

	// for block and method declaration
	public static final int UndocumentedEmptyBlock = Bit4;

	// for compilation unit
	public static final int HasAllMethodBodies = Bit5;
	
	// for references in Javadoc comments
	public static final int InsideJavadoc = Bit16;
	
	// for if statement
	public static final int IsElseIfStatement = Bit30;
	
	// for type reference
	public static final int IsSuperType = Bit5;
	public static final int IsVarArgs = Bit15;
	public static final int IgnoreRawTypeCheck = Bit31;
	
	// for array initializer
	public static final int IsAnnotationDefaultValue = Bit1;
	
	// for null reference analysis
	public static final int IsNonNull = Bit18;
	
	public ASTNode() {

		super();
	}
	private static boolean checkInvocationArgument(BlockScope scope, Expression argument, TypeBinding parameterType, TypeBinding argumentType, TypeBinding originalParameterType) {
		argument.computeConversion(scope, parameterType, argumentType);

		if (argumentType != TypeBinding.NULL && parameterType.isWildcard()) {
			WildcardBinding wildcard = (WildcardBinding) parameterType;
			if (wildcard.boundKind != Wildcard.SUPER && wildcard.otherBounds == null) // lub wildcards are tolerated
		    	return true; // unsafeWildcardInvocation
		}
		TypeBinding checkedParameterType = originalParameterType == null ? parameterType : originalParameterType;
		if (argumentType != checkedParameterType) {
			if (argumentType.needsUncheckedConversion(checkedParameterType)) {
				scope.problemReporter().unsafeTypeConversion(argument, argumentType, checkedParameterType);
			}
		}
		return false;
	}
	public static void checkInvocationArguments(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding method, Expression[] arguments, TypeBinding[] argumentTypes, boolean argsContainCast, InvocationSite invocationSite) {
		boolean unsafeWildcardInvocation = false;
		TypeBinding[] params = method.parameters;
		int paramLength = params.length;
		boolean isRawMemberInvocation = !method.isStatic() 
				&& !receiverType.isUnboundWildcard() 
				&& method.declaringClass.isRawType() 
				&& method.hasSubstitutedParameters();
		
		MethodBinding rawOriginalGenericMethod = null;
		if (!isRawMemberInvocation) {
			if (method instanceof ParameterizedGenericMethodBinding) {
				ParameterizedGenericMethodBinding paramMethod = (ParameterizedGenericMethodBinding) method;
				if (paramMethod.isUnchecked || (paramMethod.isRaw && method.hasSubstitutedParameters())) {
					rawOriginalGenericMethod = method.original();
				}
			}
		}
		if (arguments != null) {
			if (method.isVarargs()) {
				// 4 possibilities exist for a call to the vararg method foo(int i, long ... value) : foo(1), foo(1, 2), foo(1, 2, 3, 4) & foo(1, new long[] {1, 2})
				int lastIndex = paramLength - 1;
				for (int i = 0; i < lastIndex; i++) {
					TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
				    if (checkInvocationArgument(scope, arguments[i], params[i] , argumentTypes[i], originalRawParam)) {
					    unsafeWildcardInvocation = true;
				    }
				}
			   int argLength = arguments.length;
			   if (lastIndex < argLength) { // vararg argument was provided
				   	TypeBinding parameterType = params[lastIndex];
					TypeBinding originalRawParam = null;
	
				    if (paramLength != argLength || parameterType.dimensions() != argumentTypes[lastIndex].dimensions()) {
				    	parameterType = ((ArrayBinding) parameterType).elementsType(); // single element was provided for vararg parameter
				    	if (!parameterType.isReifiable()) {
						    scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)invocationSite);					
				    	}
						originalRawParam = rawOriginalGenericMethod == null ? null : ((ArrayBinding)rawOriginalGenericMethod.parameters[lastIndex]).elementsType();
				    }
					for (int i = lastIndex; i < argLength; i++) {
					    if (checkInvocationArgument(scope, arguments[i], parameterType, argumentTypes[i], originalRawParam))
						    unsafeWildcardInvocation = true;
					}
				}
	
			   if (paramLength == argumentTypes.length) { // 70056
					int varargsIndex = paramLength - 1;
					ArrayBinding varargsType = (ArrayBinding) params[varargsIndex];
					TypeBinding lastArgType = argumentTypes[varargsIndex];
					int dimensions;
					if (lastArgType == TypeBinding.NULL) {
						if (!(varargsType.leafComponentType().isBaseType() && varargsType.dimensions() == 1))
							scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
					} else if (varargsType.dimensions <= (dimensions = lastArgType.dimensions())) {
						if (lastArgType.leafComponentType().isBaseType()) {
							dimensions--;
						}
						if (varargsType.dimensions < dimensions) {
							scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
						} else if (varargsType.dimensions == dimensions 
										&& varargsType.leafComponentType != lastArgType.leafComponentType()
										&& lastArgType.isCompatibleWith(varargsType)) {
							scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
						}
					}
				}
			} else {
				for (int i = 0; i < paramLength; i++) {
					TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
				    if (checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam))
					    unsafeWildcardInvocation = true;
				}
			}
			if (argsContainCast) {
				CastExpression.checkNeedForArgumentCasts(scope, receiver, receiverType, method, arguments, argumentTypes, invocationSite);
			}
		}
		if (unsafeWildcardInvocation) {
		    scope.problemReporter().wildcardInvocation((ASTNode)invocationSite, receiverType, method, argumentTypes);
		} else if (!method.isStatic() && !receiverType.isUnboundWildcard() && method.declaringClass.isRawType() && method.hasSubstitutedParameters()) {
		    scope.problemReporter().unsafeRawInvocation((ASTNode)invocationSite, method);
		} else if (rawOriginalGenericMethod != null) {
		    scope.problemReporter().unsafeRawGenericMethodInvocation((ASTNode)invocationSite, method);
		}
	}
	public ASTNode concreteStatement() {
		return this;
	}
	
	public final boolean isFieldUseDeprecated(FieldBinding field, Scope scope, boolean isStrictlyAssigned) {
	
		if (!isStrictlyAssigned && (field.isPrivate() || (field.declaringClass != null && field.declaringClass.isLocalType())) && !scope.isDefinedInField(field)) {
			// ignore cases where field is used from within inside itself 
			field.original().modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
		}
		
		if ((field.modifiers & ExtraCompilerModifiers.AccRestrictedAccess) != 0) {
			AccessRestriction restriction = 
				scope.environment().getAccessRestriction(field.declaringClass.erasure());
			if (restriction != null) {
				scope.problemReporter().forbiddenReference(field, this,
						restriction.getFieldAccessMessageTemplate(), restriction.getProblemId());
			}
		}
	
		if (!field.isViewedAsDeprecated()) return false;
	
		// inside same unit - no report
		if (scope.isDefinedInSameUnit(field.declaringClass)) return false;
		
		// if context is deprecated, may avoid reporting
		if (!scope.compilerOptions().reportDeprecationInsideDeprecatedCode && scope.isInsideDeprecatedCode()) return false;
		return true;
	}

	public boolean isImplicitThis() {
		
		return false;
	}
	
	/* Answer true if the method use is considered deprecated.
	* An access in the same compilation unit is allowed.
	*/
	public final boolean isMethodUseDeprecated(MethodBinding method, Scope scope,
			boolean isExplicitUse) {
		if ((method.isPrivate() || method.declaringClass.isLocalType()) && !scope.isDefinedInMethod(method)) {
			// ignore cases where method is used from within inside itself (e.g. direct recursions)
			method.original().modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
		}
		
		// TODO (maxime) consider separating concerns between deprecation and access restriction.
		// 				 Caveat: this was not the case when access restriction funtion was added.
		if (isExplicitUse && (method.modifiers & ExtraCompilerModifiers.AccRestrictedAccess) != 0) {
			// note: explicit constructors calls warnings are kept despite the 'new C1()' case (two
			//       warnings, one on type, the other on constructor), because of the 'super()' case.
			AccessRestriction restriction = 
				scope.environment().getAccessRestriction(method.declaringClass.erasure());
			if (restriction != null) {
				if (method.isConstructor()) {
					scope.problemReporter().forbiddenReference(method, this,
							restriction.getConstructorAccessMessageTemplate(),
							restriction.getProblemId());
				}
				else {
					scope.problemReporter().forbiddenReference(method, this,
							restriction.getMethodAccessMessageTemplate(),
							restriction.getProblemId());
				}
			}
		}
		
		if (!method.isViewedAsDeprecated()) return false;

		// inside same unit - no report
		if (scope.isDefinedInSameUnit(method.declaringClass)) return false;
		
		// non explicit use and non explicitly deprecated - no report
		if (!isExplicitUse && 
				(method.modifiers & ClassFileConstants.AccDeprecated) == 0) {
			return false;		
		}
		
		// if context is deprecated, may avoid reporting
		if (!scope.compilerOptions().reportDeprecationInsideDeprecatedCode && scope.isInsideDeprecatedCode()) return false;
		return true;
	}

	public boolean isSuper() {

		return false;
	}

	public boolean isThis() {

		return false;
	}

	/* Answer true if the type use is considered deprecated.
	* An access in the same compilation unit is allowed.
	*/
	public final boolean isTypeUseDeprecated(TypeBinding type, Scope scope) {

		if (type.isArrayType())
			type = ((ArrayBinding) type).leafComponentType;
		if (type.isBaseType())
			return false;

		ReferenceBinding refType = (ReferenceBinding) type;

		if ((refType.isPrivate() /*|| refType.isLocalType()*/) && !scope.isDefinedInType(refType)) {
			// ignore cases where type is used from within inside itself 
			((ReferenceBinding)refType.erasure()).modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
		}
		
		if (refType.hasRestrictedAccess()) {
			AccessRestriction restriction = scope.environment().getAccessRestriction(type.erasure());
			if (restriction != null) {
				scope.problemReporter().forbiddenReference(type, this, restriction.getMessageTemplate(), restriction.getProblemId());
			}
		}

		// force annotations resolution before deciding whether the type may be deprecated
		refType.initializeDeprecatedAnnotationTagBits();
	
		if (!refType.isViewedAsDeprecated()) return false;
		
		// inside same unit - no report
		if (scope.isDefinedInSameUnit(refType)) return false;
		
		// if context is deprecated, may avoid reporting
		if (!scope.compilerOptions().reportDeprecationInsideDeprecatedCode && scope.isInsideDeprecatedCode()) return false;
		return true;
	}

	public abstract StringBuffer print(int indent, StringBuffer output);

	public static StringBuffer printAnnotations(Annotation[] annotations, StringBuffer output) {
		int length = annotations.length;
		for (int i = 0; i < length; i++) {
			annotations[i].print(0, output);
			output.append(" "); //$NON-NLS-1$
		}
		return output;
	}
	
	public static StringBuffer printIndent(int indent, StringBuffer output) {

		for (int i = indent; i > 0; i--) output.append("  "); //$NON-NLS-1$
		return output;
	}

	public static StringBuffer printModifiers(int modifiers, StringBuffer output) {

		if ((modifiers & ClassFileConstants.AccPublic) != 0)
			output.append("public "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccPrivate) != 0)
			output.append("private "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccProtected) != 0)
			output.append("protected "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccStatic) != 0)
			output.append("static "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccFinal) != 0)
			output.append("final "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccSynchronized) != 0)
			output.append("synchronized "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccVolatile) != 0)
			output.append("volatile "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccTransient) != 0)
			output.append("transient "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccNative) != 0)
			output.append("native "); //$NON-NLS-1$
		if ((modifiers & ClassFileConstants.AccAbstract) != 0)
			output.append("abstract "); //$NON-NLS-1$
		return output;
	}
	
	/**
	 * Resolve annotations, and check duplicates, answers combined tagBits 
	 * for recognized standard annotations
	 */
	public static void resolveAnnotations(BlockScope scope, Annotation[] annotations, Binding recipient) {
		AnnotationBinding[] instances = null;
		int length = annotations == null ? 0 : annotations.length;
		if (recipient != null) {
			switch (recipient.kind()) {
				case Binding.PACKAGE :
					PackageBinding packageBinding = (PackageBinding) recipient;
					if ((packageBinding.tagBits & TagBits.AnnotationResolved) != 0) return;
					packageBinding.tagBits |= TagBits.AnnotationResolved;
					break;
				case Binding.TYPE :
				case Binding.GENERIC_TYPE :
					ReferenceBinding type = (ReferenceBinding) recipient;
					if ((type.tagBits & TagBits.AnnotationResolved) != 0) return;
					type.tagBits |= TagBits.AnnotationResolved;
					if (length > 0) {
						instances = new AnnotationBinding[length];
						type.setAnnotations(instances);
					}
					break;
				case Binding.METHOD :
					MethodBinding method = (MethodBinding) recipient;
					if ((method.tagBits & TagBits.AnnotationResolved) != 0) return;
					method.tagBits |= TagBits.AnnotationResolved;
					if (length > 0) {
						instances = new AnnotationBinding[length];
						method.setAnnotations(instances);
					}
					break;
				case Binding.FIELD :
					FieldBinding field = (FieldBinding) recipient;
					if ((field.tagBits & TagBits.AnnotationResolved) != 0) return;
					field.tagBits |= TagBits.AnnotationResolved;
					if (length > 0) {
						instances = new AnnotationBinding[length];
						field.setAnnotations(instances);
					}
					break;
				case Binding.LOCAL :
					LocalVariableBinding local = (LocalVariableBinding) recipient;
					if ((local.tagBits & TagBits.AnnotationResolved) != 0) return;
					local.tagBits |= TagBits.AnnotationResolved;
					if (length > 0) {
						instances = new AnnotationBinding[length];
						local.setAnnotations(instances);
					}
					break;
				default :
					return;
			}			
		}
		if (annotations == null) 
			return;
		TypeBinding[] annotationTypes = new TypeBinding[length];
		for (int i = 0; i < length; i++) {
			Annotation annotation = annotations[i];
			annotation.recipient = recipient;
			annotationTypes[i] = annotation.resolveType(scope);
			
			// null if receiver is a package binding
			if (instances != null)				
				instances[i] = annotation.getCompilerAnnotation();
		}
		// check duplicate annotations
		for (int i = 0; i < length; i++) {
			TypeBinding annotationType = annotationTypes[i];
			if (annotationType == null) continue;
			boolean foundDuplicate = false;
			for (int j = i+1; j < length; j++) {
				if (annotationTypes[j] == annotationType) {
					foundDuplicate = true;
					annotationTypes[j] = null; // report it only once
					scope.problemReporter().duplicateAnnotation(annotations[j]);
				}
			}
			if (foundDuplicate) {
				scope.problemReporter().duplicateAnnotation(annotations[i]);
			}
		}
	}
	
	/**
	 * Figures if @Deprecated annotation is specified, do not resolve entire annotations.
	 */
	public static void resolveDeprecatedAnnotations(BlockScope scope, Annotation[] annotations, Binding recipient) {
		if (annotations == null) 
			return;
		int length = annotations.length;
		if (recipient != null) {
			switch (recipient.kind()) {
				case Binding.PACKAGE :
					PackageBinding packageBinding = (PackageBinding) recipient;
					if ((packageBinding.tagBits & (TagBits.AnnotationResolved|TagBits.AnnotationDeprecated)) != 0) return;
					break;
				case Binding.TYPE :
				case Binding.GENERIC_TYPE :
					ReferenceBinding type = (ReferenceBinding) recipient;
					if ((type.tagBits & (TagBits.AnnotationResolved|TagBits.AnnotationDeprecated)) != 0) return;
					break;
				case Binding.METHOD :
					MethodBinding method = (MethodBinding) recipient;
					if ((method.tagBits & (TagBits.AnnotationResolved|TagBits.AnnotationDeprecated)) != 0) return;
					break;
				case Binding.FIELD :
					FieldBinding field = (FieldBinding) recipient;
					if ((field.tagBits & (TagBits.AnnotationResolved|TagBits.AnnotationDeprecated)) != 0) return;
					break;
				case Binding.LOCAL :
					LocalVariableBinding local = (LocalVariableBinding) recipient;
					if ((local.tagBits & (TagBits.AnnotationResolved|TagBits.AnnotationDeprecated)) != 0) return;
					break;
				default :
					return;
			}			
		}
		for (int i = 0; i < length; i++) {
			TypeBinding annotationType = annotations[i].type.resolveType(scope);
			if(annotationType != null && annotationType.isValidBinding() && annotationType.id == TypeIds.T_JavaLangDeprecated) {
				if (recipient != null) {
					switch (recipient.kind()) {
						case Binding.PACKAGE :
							PackageBinding packageBinding = (PackageBinding) recipient;
							packageBinding.tagBits |= TagBits.AnnotationDeprecated;
							break;
						case Binding.TYPE :
						case Binding.GENERIC_TYPE :
						case Binding.TYPE_PARAMETER :
							ReferenceBinding type = (ReferenceBinding) recipient;
							type.tagBits |= TagBits.AnnotationDeprecated;
							break;
						case Binding.METHOD :
							MethodBinding method = (MethodBinding) recipient;
							method.tagBits |= TagBits.AnnotationDeprecated;
							break;
						case Binding.FIELD :
							FieldBinding field = (FieldBinding) recipient;
							field.tagBits |= TagBits.AnnotationDeprecated;
							break;
						case Binding.LOCAL :
							LocalVariableBinding local = (LocalVariableBinding) recipient;
							local.tagBits |= TagBits.AnnotationDeprecated;
							break;
					}			
				}
			}
		}
	}
	
	public int sourceStart() {
		return this.sourceStart;
	}
	public int sourceEnd() {
		return this.sourceEnd;
	}
	public String toString() {

		return print(0, new StringBuffer(30)).toString();
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		// do nothing by default
	}
}
