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

package org.eclipse.jdt.core.dom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Primitive type nodes.
 * <pre>
 * PrimitiveType:
 *    <b>byte</b>
 *    <b>short</b>
 *    <b>char</b>
 *    <b>int</b>
 *    <b>long</b>
 *    <b>float</b>
 *    <b>double</b>
 *    <b>boolean</b>
 *    <b>void</b>
 * </pre>
 * <p>
 * Note that due to the fact that AST nodes belong to a specific AST and
 * have a specific parent, there needs to multiple instances of these
 * nodes.
 * </p>
 * 
 * @since 2.0
 */
public class PrimitiveType extends Type {
	
	/**
 	 * Primitive type codes (typesafe enumeration).
	 * <pre>
	 *    <b>byte</b> 	BYTE
	 *    <b>short</b> 	SHORT
	 *    <b>char</b> 	CHAR
	 *    <b>int</b>  	INT
	 *    <b>long</b>  	LONG
	 *    <b>float</b>  	FLOAT
	 *    <b>double</b> 	DOUBLE
	 *    <b>boolean</b>	BOOLEAN
	 *    <b>void</b>  	VOID
	 * </pre>
	 */
	public static class Code {
	
		/**
		 * The name of the type.
		 */
		private String name;
		
		/**
		 * Creates a new primitive type code with the given name.
		 * <p>
		 * Note: this constructor is package-private. The only instances
		 * ever created are the ones for the standard primitive types.
		 * </p>
		 * 
		 * @param name the standard name of the primitive type
		 */
		Code(String name) {
			this.name = name;
		}
		
		/**
		 * Returns the standard name of the primitive type.
		 * 
		 * @return the standard name of the primitive type
		 */
		public String toString() {
			return name;
		}
	}		
	
	/** Type code for the primitive type "int". */
	public static final Code INT = new Code("int");//$NON-NLS-1$
	/** Type code for the primitive type "char". */
	public static final Code CHAR = new Code("char");//$NON-NLS-1$
	/** Type code for the primitive type "boolean". */
	public static final Code BOOLEAN = new Code("boolean");//$NON-NLS-1$
	/** Type code for the primitive type "short". */
	public static final Code SHORT = new Code("short");//$NON-NLS-1$
	/** Type code for the primitive type "long". */
	public static final Code LONG = new Code("long");//$NON-NLS-1$
	/** Type code for the primitive type "float". */
	public static final Code FLOAT = new Code("float");//$NON-NLS-1$
	/** Type code for the primitive type "double". */
	public static final Code DOUBLE = new Code("double");//$NON-NLS-1$
	/** Type code for the primitive type "byte". */
	public static final Code BYTE = new Code("byte");//$NON-NLS-1$

	/** Type code for the primitive type "void". Note that "void" is
	 * special in that its only legitimate uses are as a method return
	 * type and as a type literal.
	 */
	public static final Code VOID = new Code("void");//$NON-NLS-1$
		
	/** 
	 * The primitive type code; one of the PrimitiveType constants; default
	 * is int.
	 */
	private PrimitiveType.Code typeCode = INT;
	
	/**
	 * Map from token to primitive type code (key type: <code>String</code>;
	 * value type: <code>PrimitiveType.Code</code>).
	 */
	private static final Map CODES;
	static {
		CODES = new HashMap(20);
		Code[] ops = {
				INT,
				BYTE,
				CHAR,
				BOOLEAN,
				SHORT,
				LONG,
				FLOAT,
				DOUBLE,
				VOID,
			};
		for (int i = 0; i < ops.length; i++) {
			CODES.put(ops[i].toString(), ops[i]);
		}
	}
	
	/**
	 * Returns the primitive type code corresponding to the given string,
	 * or <code>null</code> if none.
	 * <p>
	 * <code>toCode</code> is the converse of <code>toString</code>:
	 * that is, 
	 * <code>PrimitiveType.Code.toCode(code.toString()) == code</code>
	 * for all type code <code>code</code>.
	 * </p>
	 * 
	 * @param token the standard name of the primitive type
	 * @return the primitive type code, or <code>null</code> if none
	 */
	public static PrimitiveType.Code toCode(String token) {
		return (PrimitiveType.Code) CODES.get(token);
	}
	
	/**
	 * The "primitiveTypeCode" structural property of this node type.
	 * @since 3.0
	 */
	public static final SimplePropertyDescriptor PRIMITIVE_TYPE_CODE_PROPERTY = 
		new SimplePropertyDescriptor(PrimitiveType.class, "primitiveTypeCode", PrimitiveType.Code.class, MANDATORY); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		createPropertyList(PrimitiveType.class);
		addProperty(PRIMITIVE_TYPE_CODE_PROPERTY);
		PROPERTY_DESCRIPTORS = reapPropertyList();
	}

	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the
	 * <code>AST.LEVEL_*</code>LEVEL

	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}
			
	/**
	 * Creates a new unparented node for a primitive type owned by the given
	 * AST. By default, the node has type "int".
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	PrimitiveType(AST ast) {
		super(ast);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == PRIMITIVE_TYPE_CODE_PROPERTY) {
			if (get) {
				return getPrimitiveTypeCode();
			} else {
				setPrimitiveTypeCode((Code) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	public int getNodeType() {
		return PRIMITIVE_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		PrimitiveType result = new PrimitiveType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setPrimitiveTypeCode(getPrimitiveTypeCode());
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	public boolean subtreeMatch(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the primitive type code.
	 * 
	 * @return one of the primitive type code constants declared in this
	 *    class
	 */
	public PrimitiveType.Code getPrimitiveTypeCode() {
		return this.typeCode;
	}
	
	/**
	 * Sets the primitive type code.
	 * 
	 * @param typeCode one of the primitive type code constants declared in 
	 *    this class
	 * @exception IllegalArgumentException if the argument is incorrect
	 */
	public void setPrimitiveTypeCode(PrimitiveType.Code typeCode) {
		if (typeCode == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(PRIMITIVE_TYPE_CODE_PROPERTY);
		this.typeCode = typeCode;
		postValueChange(PRIMITIVE_TYPE_CODE_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		// treat Code as free
		return BASE_NODE_SIZE + 1 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return memSize();
	}
}
