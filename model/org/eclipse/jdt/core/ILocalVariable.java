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
package org.eclipse.jdt.core;

/**
 * Represents a local variable declared in a method or an initializer.
 * <code>ILocalVariable</code> are pseudo-elements created as the result of a <code>ICodeAssist.codeSelect(...)</code>
 * operation. They are not part of the Java model (<code>exists()</code> always returns <code>false</code>) and
 * they are not included in the children of an <code>IMethod</code> or an <code>IInitializer</code>.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.0
 */
public interface ILocalVariable extends IJavaElement {
	/**
	 * Returns the name of this local variable.
	 * 
	 * @return the name of this local variable.
	 */
	String getElementName();
	/**
	 * Returns the source range of this local variable's name.
	 *
	 * @return the source range of this local variable's name
	 */
	ISourceRange getNameRange();
}
