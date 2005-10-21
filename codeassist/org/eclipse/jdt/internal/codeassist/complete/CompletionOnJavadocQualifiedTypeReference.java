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
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;

public class CompletionOnJavadocQualifiedTypeReference extends JavadocQualifiedTypeReference implements CompletionOnJavadoc {
	public int completionFlags = JAVADOC;
	public char[] completionIdentifier;

	public CompletionOnJavadocQualifiedTypeReference(char[][] sources, char[] identifier, long[] pos, int tagStart, int tagEnd) {
		super(sources, pos, tagStart, tagEnd);
		this.completionIdentifier = identifier;
	}

	public CompletionOnJavadocQualifiedTypeReference(JavadocQualifiedTypeReference typeRef) {
		super(typeRef.tokens, typeRef.sourcePositions, typeRef.tagSourceStart, typeRef.tagSourceStart);
		this.completionIdentifier = CharOperation.NO_CHAR;
	}

	/**
	 * @param flags The completionFlags to set.
	 */
	public void addCompletionFlags(int flags) {
		this.completionFlags |= flags;
	}

	public boolean completeAnException() {
		return (this.completionFlags & EXCEPTION) != 0;
	}

	public boolean completeInText() {
		return (this.completionFlags & TEXT) != 0;
	}

	public boolean completeBaseTypes() {
		return (this.completionFlags & BASE_TYPES) != 0;
	}

	/**
	 * Get completion node flags.
	 * 
	 * @return int Flags of the javadoc completion node.
	 */
	public int getCompletionFlags() {
		return this.completionFlags;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.AllocationExpression#printExpression(int, java.lang.StringBuffer)
	 */
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("<CompletionOnJavadocQualifiedTypeReference:"); //$NON-NLS-1$
		super.printExpression(indent, output);
		if (this.completionFlags > 0) {
			output.append("\ninfos:"); //$NON-NLS-1$
			char separator = 0;
			if (completeAnException()) {
				output.append("exception"); //$NON-NLS-1$
				separator = ',';
			}
			if (completeInText()) {
				if (separator != 0) output.append(separator);
				output.append("text"); //$NON-NLS-1$
				separator = ',';
			}
			if (completeBaseTypes()) {
				if (separator != 0) output.append(separator);
				output.append("base types"); //$NON-NLS-1$
				separator = ',';
			}
			output.append('\n');
		}
		return output.append('>');
	}
}
