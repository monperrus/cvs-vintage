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
package org.eclipse.ui.internal.registry;


/**
 * Describes a view that should be made "sticky".
 * 
 * @since 3.0
 */
public interface IStickyViewDescriptor {
    public static final String STICKY_FOLDER = "stickyFolder"; //$NON-NLS-1$
    
    /**
     * Return the id of the view to be made sticky.
     * 
     * @return the id of the view to be made sticky
     */
    public String getId();
    
    /**
     * Return the namespace in which this descriptor was declared.
     * 
     * @return the namespace in which this descriptor was declared
     */
    public String getNamespace();
}
