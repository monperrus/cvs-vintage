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

package org.eclipse.ui.internal.commands;

import java.util.List;

public interface ICommandRegistry {

    void addCommandRegistryListener(
            ICommandRegistryListener commandRegistryListener);

    List getCategoryDefinitions();

    List getCommandDefinitions();

    List getHandlers();

    List getImageBindingDefinitions();

    void removeCommandRegistryListener(
            ICommandRegistryListener commandRegistryListener);
}
