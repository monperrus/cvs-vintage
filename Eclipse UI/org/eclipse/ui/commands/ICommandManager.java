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

package org.eclipse.ui.commands;

import java.util.Map;
import java.util.Set;

import org.eclipse.ui.keys.KeySequence;

/**
 * <p>
 * An instance of <code>ICommandManager</code> can be used to obtain
 * instances of <code>ICommand</code>, as well as manage whether or not
 * those instances are active or inactive, enabled or disabled.
 * </p>
 * <p>
 * This interface is not intended to be extended or implemented by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 * @see CommandManagerFactory
 * @see ICommand
 * @see ICommandManagerListener
 */
public interface ICommandManager {

    /**
     * Registers an instance of <code>ICommandManagerListener</code> to
     * listen for changes to attributes of this instance.
     * 
     * @param commandManagerListener
     *            the instance of <code>ICommandManagerListener</code> to
     *            register. Must not be <code>null</code>. If an attempt is
     *            made to register an instance of <code>ICommandManagerListener</code>
     *            which is already registered with this instance, no operation
     *            is performed.
     */
    void addCommandManagerListener(
            ICommandManagerListener commandManagerListener);

    /**
     * TODO javadoc
     */
    Set getActiveContextIds();

    /**
     * TODO javadoc
     */
    String getActiveKeyConfigurationId();

    /**
     * TODO javadoc
     */
    String getActiveLocale();

    /**
     * TODO javadoc
     */
    String getActivePlatform();

    /**
     * Returns a handle to a category given an identifier.
     * 
     * @param categoryId
     *            an identifier. Must not be <code>null</code>
     * @return a handle to a category.
     */
    ICategory getCategory(String categoryId);

    /**
     * Returns a handle to a command given an identifier.
     * 
     * @param commandId
     *            an identifier. Must not be <code>null</code>
     * @return a handle to a command.
     */
    ICommand getCommand(String commandId);

    /**
     * <p>
     * Returns the set of identifiers to defined categories.
     * </p>
     * <p>
     * Notification is sent to all registered listeners if this attribute
     * changes.
     * </p>
     * 
     * @return the set of identifiers to defined categories. This set may be
     *         empty, but is guaranteed not to be <code>null</code>. If this
     *         set is not empty, it is guaranteed to only contain instances of
     *         <code>String</code>.
     */
    Set getDefinedCategoryIds();

    /**
     * <p>
     * Returns the set of identifiers to defined commands.
     * </p>
     * <p>
     * Notification is sent to all registered listeners if this attribute
     * changes.
     * </p>
     * 
     * @return the set of identifiers to defined commands. This set may be
     *         empty, but is guaranteed not to be <code>null</code>. If this
     *         set is not empty, it is guaranteed to only contain instances of
     *         <code>String</code>.
     */
    Set getDefinedCommandIds();

    /**
     * <p>
     * Returns the set of identifiers to defined key configurations.
     * </p>
     * <p>
     * Notification is sent to all registered listeners if this attribute
     * changes.
     * </p>
     * 
     * @return the set of identifiers to defined key configurations. This set
     *         may be empty, but is guaranteed not to be <code>null</code>.
     *         If this set is not empty, it is guaranteed to only contain
     *         instances of <code>String</code>.
     */
    Set getDefinedKeyConfigurationIds();

    /**
     * Returns a handle to a key configuration given an identifier.
     * 
     * @param keyConfigurationId
     *            an identifier. Must not be <code>null</code>
     * @return a handle to a key configuration.
     */
    IKeyConfiguration getKeyConfiguration(String keyConfigurationId);

    /**
     * Given the current state of the command manager (i.e., active widgets,
     * etc.) get the key bindings that start with the given key sequence. It is
     * possible to restrict this list to include only key bindings that are
     * available in dialogs.
     * 
     * @param keySequence
     *            The key sequence to look for at the start of all the
     *            currently active key bindings; must not be <code>null</code>.
     * @param dialogOnly
     *            Whether to restrict the search to key bindings available only
     *            in dialogs.
     * @return A map of key sequence to command identifier; may be empty, but
     *         never <code>null</code>.
     */
    Map getPartialMatches(KeySequence keySequence, boolean dialogOnly);

    /**
     * Given the current state of the command manager (i.e., active widgets,
     * etc.) get the key binding that matches the given key sequence. It is
     * possible to restrict this list to include only key bindings that are
     * available in dialogs.
     * 
     * @param keySequence
     *            The key sequence to look for; must not be <code>null</code>.
     * @param dialogOnly
     *            Whether to restrict the search to key bindings available only
     *            in dialogs.
     * @return The matching command identifier, if any; otherwise, <code>null</code>.
     */
    String getPerfectMatch(KeySequence keySequence, boolean dialogOnly);

    /**
     * Given the current state of the command manager (i.e., active widgets,
     * etc.) check whether the given key sequence starts one of the active key
     * bindings. It is possible to restrict this list to include only key
     * bindings that are available in dialogs.
     * 
     * @param keySequence
     *            The key sequence to look for; must not be <code>null</code>.
     * @param dialogOnly
     *            Whether to restrict the search to key bindings available only
     *            in dialogs.
     * @return <code>true</code> if one or more key bindings begin with the
     *         given key sequence; <code>false</code> otherwise.
     */
    boolean isPartialMatch(KeySequence keySequence, boolean dialogOnly);

    /**
     * Given the current state of the command manager (i.e., active widgets,
     * etc.) check whether the given key sequence is one of the active key
     * bindings. It is possible to restrict this list to include only key
     * bindings that are available in dialogs.
     * 
     * @param keySequence
     *            The key sequence to look for; must not be <code>null</code>.
     * @param dialogOnly
     *            Whether to restrict the search to key bindings available only
     *            in dialogs.
     * @return <code>true</code> if one of the active key bindings is the
     *         given key sequence; <code>false</code> otherwise.
     */
    boolean isPerfectMatch(KeySequence keySequence, boolean dialogOnly);

    /**
     * Unregisters an instance of <code>ICommandManagerListener</code>
     * listening for changes to attributes of this instance.
     * 
     * @param commandManagerListener
     *            the instance of <code>ICommandManagerListener</code> to
     *            unregister. Must not be <code>null</code>. If an attempt
     *            is made to unregister an instance of <code>ICommandManagerListener</code>
     *            which is not already registered with this instance, no
     *            operation is performed.
     */
    void removeCommandManagerListener(
            ICommandManagerListener commandManagerListener);

    /**
     * Sets the set of identifiers to active activities.
     * 
     * @param activeContextIds
     *            the set of identifiers to active activities. This set may be
     *            empty, but it must not be <code>null</code>. If this set
     *            is not empty, it must only contain instances of <code>String</code>.
     */
    void setActiveContextIds(Set activeContextIds);

    /**
     * TODO javadoc
     */
    void setActiveKeyConfigurationId(String activeKeyConfigurationId);

    /**
     * TODO javadoc
     */
    void setActiveLocale(String activeLocale);

    /**
     * TODO javadoc
     */
    void setActivePlatform(String activePlatform);
}
