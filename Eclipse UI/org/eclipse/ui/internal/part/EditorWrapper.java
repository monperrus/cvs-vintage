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
package org.eclipse.ui.internal.part;

import org.eclipse.core.components.ComponentException;
import org.eclipse.core.components.Components;
import org.eclipse.core.components.ServiceFactory;
import org.eclipse.core.components.IServiceProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.Bundle;

/**
 * Wraps a Part in a form that can be converted into an IEditorPart.
 * 
 * @since 3.1
 */
public class EditorWrapper extends PartWrapper {
    private IEditorPart part;
    
    /**
     * 
     */
    public EditorWrapper(Composite parentControl, Bundle bundle, IWorkbenchPage page, PartGenerator gen, ServiceFactory context) throws ComponentException {
        super(parentControl, bundle, page, gen, context);

        try {
            part = (IEditorPart)getWrappedPart().getService(IEditorPart.class);
            if (part == null) {
                IServiceProvider container = getContainer();
                StandardWorkbenchServices services = new StandardWorkbenchServices(container);
                IPartPropertyProvider provider = (IPartPropertyProvider) Components.queryInterface(getWrappedPart(), IPartPropertyProvider.class);
                part = new NewPartToOldAdapter(services, provider);
            }
        } catch (ComponentException e) {
            getWrappedPart().getControl().dispose();
            throw e;
        }
    }
    
    public IEditorPart getEditorPart() {
        return part;
    }
}
