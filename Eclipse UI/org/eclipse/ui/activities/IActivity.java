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

import java.util.Set;

/**
 * An instance of this interface is an activity as defined by the extension
 * point <code>org.eclipse.ui.activities</code>.
 * <p>
 * An instance of this interface can be obtained from an instance of <code>IActivityManager</code>
 * for any identifier, whether or not an activity with that identifier is
 * defined in the extension registry.
 * </p>
 * <p>
 * The handle-based nature of this API allows it to work well with runtime
 * plugin activation and deactivation, which can cause dynamic changes to the
 * extension registry.
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
 */
public interface IActivity extends Comparable {

	/**
	 * Registers an instance of <code>IActivityListener</code> to listen for
	 * changes to properties of this instance.
	 * 
	 * @param activityListener
	 *            the instance to register. Must not be <code>null</code>.
	 *            If an attempt is made to register an instance which is
	 *            already registered with this instance, no operation is
	 *            performed.
	 */
	void addActivityListener(IActivityListener activityListener);

	/**
	 * Returns the set of activity activity bindings for this instance.
	 * <p>
	 * This method will return all activity activity bindings for this
	 * instance, whether or not this instance is defined.
	 * </p>
	 * <p>
	 * Notification is sent to all registered listeners if this property
	 * changes.
	 * </p>
	 * 
	 * @return the set of activity activity bindings. This set may be empty,
	 *         but is guaranteed not to be <code>null</code>. If this set is
	 *         not empty, it is guaranteed to only contain instances of <code>IActivityActivityBinding</code>.
	 */
	Set getActivityActivityBindings();

	/**
	 * Returns the set of activity pattern bindings for this instance.
	 * <p>
	 * This method will return all activity pattern bindings for this instance,
	 * whether or not this instance is defined.
	 * </p>
	 * <p>
	 * Notification is sent to all registered listeners if this property
	 * changes.
	 * </p>
	 * 
	 * @return the set of activity pattern bindings. This set may be empty, but
	 *         is guaranteed not to be <code>null</code>. If this set is not
	 *         empty, it is guaranteed to only contain instances of <code>IActivityPatternBinding</code>.
	 */
	Set getActivityPatternBindings();

	/**
	 * Returns the identifier of this instance.
	 * 
	 * @return the identifier of this instance. Guaranteed not to be <code>null</code>.
	 */
	String getId();

	/**
	 * Returns the name of this instance suitable for display to the user.
	 * <p>
	 * Notification is sent to all registered listeners if this property
	 * changes.
	 * </p>
	 * 
	 * @return the name of this instance. Guaranteed not to be <code>null</code>.
	 * @throws NotDefinedException
	 *             if this instance is not defined.
	 */
	String getName() throws NotDefinedException;

	/**
	 * Returns whether or not this instance is defined.
	 * <p>
	 * Notification is sent to all registered listeners if this property
	 * changes.
	 * </p>
	 * 
	 * @return true, iff this instance is defined.
	 */
	boolean isDefined();

	/**
	 * Returns whether or not this instance is enabled.
	 * <p>
	 * Notification is sent to all registered listeners if this property
	 * changes.
	 * </p>
	 * 
	 * @return true, iff this instance is enabled.
	 */
	boolean isEnabled();

	/**
	 * Unregisters an instance of <code>IActivityListener</code> listening
	 * for changes to properties of this instance.
	 * 
	 * @param activityListener
	 *            the instance to unregister. Must not be <code>null</code>.
	 *            If an attempt is made to unregister an instance which is not
	 *            already registered with this instance, no operation is
	 *            performed.
	 */
	void removeActivityListener(IActivityListener activityListener);
}
