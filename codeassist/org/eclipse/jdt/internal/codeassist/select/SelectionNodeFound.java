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
package org.eclipse.jdt.internal.codeassist.select;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class SelectionNodeFound extends RuntimeException {
	
	public ASTNode node;
	public Binding binding;
	public boolean isDeclaration;
	private static final long serialVersionUID = -7335444736618092295L; // backward compatible
	
public SelectionNodeFound() {
	this(null, null, false); // we found a problem in the selection node
}
public SelectionNodeFound(ASTNode node, Binding binding) {
	this(node, binding, false);
}
public SelectionNodeFound(ASTNode node, Binding binding, boolean isDeclaration) {
	this.node = node;
	this.binding = binding;
	this.isDeclaration = isDeclaration;
}
}
