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

import java.util.List;

/**
 * Normal annotation node (added in JLS3 API).
 * <p>
 * <pre>
 * NormalAnnotation:
 *   <b>@</b> TypeName <b>(</b> [ MemberValuePair { <b>,</b> MemberValuePair } ] <b>)</b>
 * </pre>
 * </p>
 * <p>
 * Note: This API element is only needed for dealing with Java code that uses
 * new language features of J2SE 1.5. It is included in anticipation of J2SE
 * 1.5 support, which is planned for the next release of Eclipse after 3.0, and
 * may change slightly before reaching its final form.
 * </p>
 * @since 3.0
 */
public final class NormalAnnotation extends Annotation {
	
	/**
	 * The "typeName" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor TYPE_NAME_PROPERTY = 
		internalTypeNamePropertyFactory(NormalAnnotation.class);

	/**
	 * The "values" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor VALUES_PROPERTY = 
		new ChildListPropertyDescriptor(NormalAnnotation.class, "values", MemberValuePair.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 * @since 3.0
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		createPropertyList(NormalAnnotation.class);
		addProperty(TYPE_NAME_PROPERTY);
		addProperty(VALUES_PROPERTY);
		PROPERTY_DESCRIPTORS = reapPropertyList();
	}
	
	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the AST.JLS* constants
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}
	
	/**
	 * The list of member value pairs (element type: 
	 * <code MemberValuePair</code>). Defaults to an empty list.
	 */
	private ASTNode.NodeList values = 
		new ASTNode.NodeList(VALUES_PROPERTY);

	/**
	 * Creates a new unparented normal annotation node owned 
	 * by the given AST.  By default, the annotation has an
	 * unspecified type name and an empty list of member value
	 * pairs.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	NormalAnnotation(AST ast) {
		super(ast);
	    unsupportedIn2();
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * @since 3.0
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == TYPE_NAME_PROPERTY) {
			if (get) {
				return getTypeName();
			} else {
				setTypeName((Name) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == VALUES_PROPERTY) {
			return values();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on BodyDeclaration.
	 */
	final ChildPropertyDescriptor internalTypeNameProperty() {
		return TYPE_NAME_PROPERTY;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return NORMAL_ANNOTATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		NormalAnnotation result = new NormalAnnotation(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setTypeName((Name) ASTNode.copySubtree(target, getTypeName()));
		result.values().addAll(ASTNode.copySubtrees(target, values()));
		return result;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getTypeName());
			acceptChildren(visitor, this.values);
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the live list of member value pairs in this annotation.
	 * Adding and removing nodes from this list affects this node
	 * dynamically. All nodes in this list must be 
	 * {@link MemberValuePair}s; attempts to add any other 
	 * type of node will trigger an exception.
	 * 
	 * @return the live list of member value pairs in this 
	 *    annotation (element type: <code>MemberValuePair</code>)
	 */ 
	public List values() {
		return this.values;
	}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return super.memSize() + 1 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.typeName == null ? 0 : getTypeName().treeSize())
			+ this.values.listSize();
	}
}
