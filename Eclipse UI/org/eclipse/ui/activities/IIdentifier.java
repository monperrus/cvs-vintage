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

package org.eclipse.ui.activities;

/**
 * <p>
 * An instance of <code>IIdentifier</code> is a handle representing an
 * identifier as defined by the extension point <code>org.eclipse.ui.activities</code>.
 * The identifier of the handle is identifier of the identifier being
 * represented.
 * </p>
 * <p>
 * An instance of <code>IIdentifier</code> can be obtained from an instance
 * of <code>IActivityManager</code> for any identifier, whether or not a
 * identifier with that identifier is defined in the plugin registry.
 * </p>
 * <p>
 * The handle-based nature of this API allows it to work well with runtime
 * plugin activation and deactivation, which causes dynamic changes to the
 * plugin registry, and therefore, potentially, dynamic changes to the set of
 * identifier definitions.
 * </p>
 * <p>
 * This interface is not intended to be extended or implemented by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 * @see IActivityManager
 * @see IIdentifierListener
 */
public interface IIdentifier extends Comparable {

	/**
	 * Registers an instance of <code>IIdentifierListener</code> to listen
	 * for changes to attributes of this instance.
	 * 
	 * @param identifierListener
	 *            the instance of <code>IIdentifierListener</code> to
	 *            register. Must not be <code>null</code>. If an attempt is
	 *            made to register an instance of <code>IIdentifierListener</code>
	 *            which is already registered with this instance, no operation
	 *            is performed.
	 */
	void addIdentifierListener(IIdentifierListener identifierListener);

	/**
	 * Returns the identifier of this handle.
	 * 
	 * @return the identifier of this handle. Guaranteed not to be <code>null</code>.
	 */
	String getId();

	/**
	 * <p>
	 * Returns whether or not the identifier represented by this handle is
	 * enabled.
	 * </p>
	 * <p>
	 * Notification is sent to all registered listeners if this attribute
	 * changes.
	 * </p>
	 * 
	 * @return <code>true</code>, iff the identifier represented by this
	 *         handle is enabled.
	 */
	boolean isEnabled();

	/**
	 * Unregisters an instance of <code>IIdentifierListener</code> listening
	 * for changes to attributes of this instance.
	 * 
	 * @param identifierListener
	 *            the instance of <code>IIdentifierListener</code> to
	 *            unregister. Must not be <code>null</code>. If an attempt
	 *            is made to unregister an instance of <code>IIdentifierListener</code>
	 *            which is not already registered with this instance, no
	 *            operation is performed.
	 */
	void removeIdentifierListener(IIdentifierListener identifierListener);
}
