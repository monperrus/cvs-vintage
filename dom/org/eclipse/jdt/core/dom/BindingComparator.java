/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.dom;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

/**
 * Internal helper class for comparing bindings.
 * 
 * @since 3.1
 */
class BindingComparator {
	/**
	 * @param bindings
	 * @param otherBindings
	 * @return true if both parameters are equals, false otherwise
	 */
	static boolean isEqual(TypeVariableBinding[] bindings, TypeVariableBinding[] otherBindings) {
		if (bindings == null) {
			return otherBindings == null;
		} else if (otherBindings == null) {
			return false;
		} else {
			int length = bindings.length;
			int otherLength = otherBindings.length;
			if (length != otherLength) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				TypeVariableBinding typeVariableBinding = bindings[i];
				TypeVariableBinding typeVariableBinding2 = otherBindings[i];
				if (!isEqual(typeVariableBinding.declaringElement, typeVariableBinding2.declaringElement)
						|| !isEqual(typeVariableBinding.firstBound, typeVariableBinding2.firstBound)
						|| !isEqual(typeVariableBinding.superclass, typeVariableBinding2.superclass)
						|| !isEqual(typeVariableBinding.superInterfaces, typeVariableBinding2.superInterfaces)) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * @param declaringElement
	 * @param declaringElement2
	 * @return true if both parameters are equals, false otherwise
	 */
	static boolean isEqual(Binding declaringElement, Binding declaringElement2) {
		if (declaringElement instanceof org.eclipse.jdt.internal.compiler.lookup.TypeBinding) {
			if (!(declaringElement2 instanceof org.eclipse.jdt.internal.compiler.lookup.TypeBinding)){
				return false;
			}
			return isEqual((org.eclipse.jdt.internal.compiler.lookup.TypeBinding) declaringElement,
					(org.eclipse.jdt.internal.compiler.lookup.TypeBinding) declaringElement2,
					false);
		} else if (declaringElement instanceof org.eclipse.jdt.internal.compiler.lookup.MethodBinding) {
			if (!(declaringElement2 instanceof org.eclipse.jdt.internal.compiler.lookup.MethodBinding)) {
				return false;
			}
			return isEqualWithoutTypeVariables((org.eclipse.jdt.internal.compiler.lookup.MethodBinding) declaringElement,
					(org.eclipse.jdt.internal.compiler.lookup.MethodBinding) declaringElement2);
		} else if (declaringElement instanceof VariableBinding) {
			if (!(declaringElement2 instanceof VariableBinding)) {
				return false;
			}
			return isEqual((VariableBinding) declaringElement,
					(VariableBinding) declaringElement2);
		} else if (declaringElement instanceof org.eclipse.jdt.internal.compiler.lookup.PackageBinding) {
			if (!(declaringElement2 instanceof org.eclipse.jdt.internal.compiler.lookup.PackageBinding)) {
				return false;
			}
			org.eclipse.jdt.internal.compiler.lookup.PackageBinding packageBinding = (org.eclipse.jdt.internal.compiler.lookup.PackageBinding) declaringElement;
			org.eclipse.jdt.internal.compiler.lookup.PackageBinding packageBinding2 = (org.eclipse.jdt.internal.compiler.lookup.PackageBinding) declaringElement2;
			return CharOperation.equals(packageBinding.compoundName, packageBinding2.compoundName);
		} else if (declaringElement instanceof ImportBinding) {
			if (!(declaringElement2 instanceof ImportBinding)) {
				return false;
			}
			ImportBinding importBinding = (ImportBinding) declaringElement;
			ImportBinding importBinding2 = (ImportBinding) declaringElement2;
			return importBinding.isStatic() == importBinding2.isStatic()
				&& importBinding.onDemand == importBinding2.onDemand
				&& CharOperation.equals(importBinding.compoundName, importBinding2.compoundName);
		}
		return false;
	}
	
	static boolean isEqual(org.eclipse.jdt.internal.compiler.lookup.MethodBinding methodBinding,
			org.eclipse.jdt.internal.compiler.lookup.MethodBinding methodBinding2) {
		return (methodBinding == null && methodBinding2 == null)
			|| (CharOperation.equals(methodBinding.selector, methodBinding2.selector)
				&& isEqual(methodBinding.returnType, methodBinding2.returnType) 
				&& isEqual(methodBinding.parameters, methodBinding2.parameters)
				&& isEqual(methodBinding.thrownExceptions, methodBinding2.thrownExceptions)
				&& isEqual(methodBinding.typeVariables, methodBinding2.typeVariables))
				&& isEqual(methodBinding.declaringClass, methodBinding2.declaringClass);
	}
			
	static boolean isEqualWithoutTypeVariables(org.eclipse.jdt.internal.compiler.lookup.MethodBinding methodBinding,
			org.eclipse.jdt.internal.compiler.lookup.MethodBinding methodBinding2) {
		return (methodBinding == null && methodBinding2 == null)
			|| (CharOperation.equals(methodBinding.selector, methodBinding2.selector)
				&& isEqual(methodBinding.returnType, methodBinding2.returnType) 
				&& isEqual(methodBinding.parameters, methodBinding2.parameters)
				&& isEqual(methodBinding.thrownExceptions, methodBinding2.thrownExceptions));
	}

	static boolean isEqual(VariableBinding variableBinding, VariableBinding variableBinding2) {
		return variableBinding.modifiers == variableBinding2.modifiers
				&& CharOperation.equals(variableBinding.name, variableBinding2.name)
				&& isEqual(variableBinding.type, variableBinding2.type);
	}

	static boolean isEqual(FieldBinding fieldBinding, FieldBinding fieldBinding2) {
		return fieldBinding.modifiers == fieldBinding2.modifiers
				&& CharOperation.equals(fieldBinding.name, fieldBinding2.name)
				&& isEqual(fieldBinding.type, fieldBinding2.type)
				&& isEqual(fieldBinding.declaringClass, fieldBinding2.declaringClass);
	}

	/**
	 * @param bindings
	 * @param otherBindings
	 * @return true if both parameters are equals, false otherwise
	 */
	static boolean isEqual(org.eclipse.jdt.internal.compiler.lookup.TypeBinding[] bindings, org.eclipse.jdt.internal.compiler.lookup.TypeBinding[] otherBindings) {
		if (bindings == null) {
			return otherBindings == null;
		} else if (otherBindings == null) {
			return false;
		} else {
			int length = bindings.length;
			int otherLength = otherBindings.length;
			if (length != otherLength) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				if (!isEqual(bindings[i], otherBindings[i])) {
					return false;
				}
			}
			return true;
		}
	}

	static boolean isEqual(org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding, org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding2, boolean checkTypeVariables) {
		if (typeBinding == null) {
			return typeBinding2 == null;
		} else if (typeBinding2 == null) {
			return false;
		} else if (typeBinding.isBaseType()) {
			// base type
			if (!typeBinding2.isBaseType()) {
				return false;
			}
			return typeBinding.id == typeBinding2.id;
		} else if (typeBinding.isArrayType()) {
			// array case
			if (!typeBinding2.isArrayType()) {
				return false;
			}
			return typeBinding.dimensions() == typeBinding2.dimensions()
					&& isEqual(typeBinding.leafComponentType(), typeBinding2.leafComponentType());
		} else {
			// reference type
			ReferenceBinding referenceBinding = (ReferenceBinding) typeBinding;
			ReferenceBinding referenceBinding2 = (ReferenceBinding) typeBinding2;
			if (referenceBinding.isParameterizedType()) {
				if (!referenceBinding2.isParameterizedType()) {
					return false;
				}
				ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding) referenceBinding;
				ParameterizedTypeBinding parameterizedTypeBinding2 = (ParameterizedTypeBinding) referenceBinding2;
				return CharOperation.equals(referenceBinding.compoundName, referenceBinding2.compoundName)
					&& (referenceBinding.isInterface() == referenceBinding2.isInterface())
					&& (referenceBinding.isEnum() == referenceBinding2.isEnum())
					&& (referenceBinding.isAnnotationType() == referenceBinding2.isAnnotationType())
					&& (referenceBinding.modifiers == referenceBinding2.modifiers)
					&& isEqual(parameterizedTypeBinding.arguments, parameterizedTypeBinding2.arguments);
			} else if (referenceBinding.isWildcard()) {
				if (!referenceBinding2.isWildcard()) {
					return false;
				}
				WildcardBinding wildcardBinding = (WildcardBinding) referenceBinding;
				WildcardBinding wildcardBinding2 = (WildcardBinding) referenceBinding2;
				return isEqual(wildcardBinding.bound, wildcardBinding2.bound)
					&& wildcardBinding.kind == wildcardBinding2.kind;
			} else if (referenceBinding.isGenericType()) {
				if (!referenceBinding2.isGenericType()) {
					return false;
				}
				if (checkTypeVariables) {
					if (!isEqual(referenceBinding.typeVariables(), referenceBinding2.typeVariables())) {
						return false;
					}
				}
				return CharOperation.equals(referenceBinding.compoundName, referenceBinding2.compoundName)
					&& (referenceBinding.isGenericType() == referenceBinding2.isGenericType())
					&& (referenceBinding.isRawType() == referenceBinding2.isRawType())
					&& (referenceBinding.isInterface() == referenceBinding2.isInterface())
					&& (referenceBinding.isEnum() == referenceBinding2.isEnum())
					&& (referenceBinding.isAnnotationType() == referenceBinding2.isAnnotationType())
					&& (referenceBinding.modifiers == referenceBinding2.modifiers);
			} else {
				return CharOperation.equals(referenceBinding.compoundName, referenceBinding2.compoundName)
					&& (referenceBinding.isRawType() == referenceBinding2.isRawType())
					&& (referenceBinding.isInterface() == referenceBinding2.isInterface())
					&& (referenceBinding.isEnum() == referenceBinding2.isEnum())
					&& (referenceBinding.isAnnotationType() == referenceBinding2.isAnnotationType())
					&& (referenceBinding.modifiers == referenceBinding2.modifiers);
			}
		}
	}
	/**
	 * @param typeBinding
	 * @param typeBinding2
	 * @return true if both parameters are equals, false otherwise
	 */
	static boolean isEqual(org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding, org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding2) {
		return isEqual(typeBinding, typeBinding2, true);
	}
}
