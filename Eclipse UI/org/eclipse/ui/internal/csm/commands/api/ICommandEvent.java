/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.csm.commands.api;

/**
 * <p>
 * An instance of <code>ICommandEvent</code> describes changes to an instance of 
 * <code>ICommand</code>. 
 * </p>
 * <p>
 * This interface is not intended to be extended or implemented by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 * @see ICommand
 * @see ICommandListener#commandChanged
 */
public interface ICommandEvent {

	/**
	 * Returns the instance of <code>ICommand</code> that has changed.
	 *
	 * @return the instance of <code>ICommand</code> that has changed. 
	 *         Guaranteed not to be <code>null</code>.
	 */
	ICommand getCommand();

	/**
	 * TODO javadoc
	 */	
	boolean hasActiveChanged();

	/**
	 * TODO javadoc
	 */	
	boolean hasCategoryIdChanged();	

	/**
	 * TODO javadoc
	 */	
	boolean hasDefinedChanged();
	
	/**
	 * TODO javadoc
	 */	
	boolean hasDescriptionChanged();	
	
	/**
	 * TODO javadoc
	 */	
	boolean hasEnabledChanged();

	/**
	 * TODO javadoc
	 */		
	boolean hasNameChanged();

	/**
	 * TODO javadoc
	 */		
	boolean haveActivityBindingsChanged();	
	
	/**
	 * TODO javadoc
	 */		
	boolean haveImageBindingsChanged();	
	
	/**
	 * TODO javadoc
	 */		
	boolean haveKeySequenceBindingsChanged();		
}
