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
package org.eclipse.jdt.core.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.search.matching.JavaSearchMatch;

/**
 * TODO add spec
 * @since 3.0
 */
public class TypeDeclarationMatch extends JavaSearchMatch {

	public TypeDeclarationMatch(IJavaElement element, int accuracy, int sourceStart, int sourceEnd, SearchParticipant participant, IResource resource) {
		super(element, accuracy, sourceStart, sourceEnd, participant, resource);
	}

}
