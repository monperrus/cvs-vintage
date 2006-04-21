/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.commands;

import java.util.Collection;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.SerializationException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.services.IDisposable;

/**
 * <p>
 * Provides services related to the command architecture within the workbench.
 * This service can be used to access the set of commands and command
 * categories.
 * </p>
 * <p>
 * This interface should not be implemented or extended by clients.
 * </p>
 * 
 * @since 3.1
 */
public interface ICommandService extends IDisposable {

	/**
	 * The identifier of the category in which all auto-generated commands will
	 * appear. This value must never be <code>null</code>.
	 * 
	 * @since 3.2
	 */
	public static final String AUTOGENERATED_CATEGORY_ID = CommandManager.AUTOGENERATED_CATEGORY_ID;

	/**
	 * Adds an execution listener to the command service. This listener will be
	 * notified as commands are executed.
	 * 
	 * @param listener
	 *            The listener to add; must not be <code>null</code>.
	 */
	public void addExecutionListener(IExecutionListener listener);

	/**
	 * Sets the name and description of the category for uncategorized commands.
	 * This is the category that will be returned if
	 * {@link #getCategory(String)} is called with <code>null</code>.
	 * 
	 * @param name
	 *            The name of the category for uncategorized commands; must not
	 *            be <code>null</code>.
	 * @param description
	 *            The description of the category for uncategorized commands;
	 *            may be <code>null</code>.
	 * @since 3.2
	 */
	public void defineUncategorizedCategory(String name, String description);

	/**
	 * <p>
	 * Returns a {@link ParameterizedCommand} with a command and
	 * parameterizations as specified in the provided
	 * <code>serializedParameterizedCommand</code> string. The
	 * <code>serializedParameterizedCommand</code> must use the format
	 * returned by {@link ParameterizedCommand#serialize()} and described in the
	 * Javadoc for that method.
	 * </p>
	 * <p>
	 * If a parameter id encoded in the
	 * <code>serializedParameterizedCommand</code> does not exist in the
	 * encoded command, that parameter id and value are ignored. A given
	 * parameter id should not be used more than once in
	 * <code>serializedParameterizedCommand</code>. This will not result in
	 * an exception, but the value of the parameter when the command is executed
	 * cannot be specified here.
	 * </p>
	 * <p>
	 * This method will never return <code>null</code>, however it may throw
	 * an exception if there is a problem processing the serialization string or
	 * the encoded command is undefined.
	 * </p>
	 * 
	 * @param serializedParameterizedCommand
	 *            a <code>String</code> representing a command id and
	 *            parameter ids and values
	 * @return a <code>ParameterizedCommand</code> with the command and
	 *         parameterizations encoded in the
	 *         <code>serializedParameterizedCommand</code>
	 * @throws NotDefinedException
	 *             if the command indicated in
	 *             <code>serializedParameterizedCommand</code> is not defined
	 * @throws SerializationException
	 *             if there is an error deserializing
	 *             <code>serializedParameterizedCommand</code>
	 * @see ParameterizedCommand#serialize()
	 * @see CommandManager#deserialize(String)
	 * @since 3.2
	 */
	public ParameterizedCommand deserialize(
			String serializedParameterizedCommand) throws NotDefinedException,
			SerializationException;

	/**
	 * Retrieves the category with the given identifier. If no such category
	 * exists, then an undefined category with the given id is created.
	 * 
	 * @param categoryId
	 *            The identifier to find. If the category is <code>null</code>,
	 *            then a category suitable for uncategorized items is defined
	 *            and returned.
	 * @return A category with the given identifier, either defined or
	 *         undefined.
	 */
	public Category getCategory(String categoryId);

	/**
	 * Retrieves the command with the given identifier. If no such command
	 * exists, then an undefined command with the given id is created.
	 * 
	 * @param commandId
	 *            The identifier to find; must not be <code>null</code>.
	 * @return A command with the given identifier, either defined or undefined.
	 */
	public Command getCommand(String commandId);

	/**
	 * Returns the collection of all of the defined categories in the workbench.
	 * 
	 * @return The collection of categories (<code>Category</code>) that are
	 *         defined; never <code>null</code>, but may be empty.
	 * @since 3.2
	 */
	public Category[] getDefinedCategories();

	/**
	 * Returns the collection of the identifiers for all of the defined
	 * categories in the workbench.
	 * 
	 * @return The collection of category identifiers (<code>String</code>)
	 *         that are defined; never <code>null</code>, but may be empty.
	 */
	public Collection getDefinedCategoryIds();

	/**
	 * Returns the collection of the identifiers for all of the defined commands
	 * in the workbench.
	 * 
	 * @return The collection of command identifiers (<code>String</code>)
	 *         that are defined; never <code>null</code>, but may be empty.
	 */
	public Collection getDefinedCommandIds();

	/**
	 * Returns the collection of all of the defined commands in the workbench.
	 * 
	 * @return The collection of commands (<code>Command</code>) that are
	 *         defined; never <code>null</code>, but may be empty.
	 * @since 3.2
	 */
	public Command[] getDefinedCommands();

	/**
	 * Returns the collection of the identifiers for all of the defined command
	 * parameter types in the workbench.
	 * 
	 * @return The collection of command parameter type identifiers (<code>String</code>)
	 *         that are defined; never <code>null</code>, but may be empty.
	 * @since 3.2
	 */
	public Collection getDefinedParameterTypeIds();

	/**
	 * Returns the collection of all of the defined command parameter types in
	 * the workbench.
	 * 
	 * @return The collection of command parameter types (<code>ParameterType</code>)
	 *         that are defined; never <code>null</code>, but may be empty.
	 * @since 3.2
	 */
	public ParameterType[] getDefinedParameterTypes();

	/**
	 * Gets the help context identifier for a particular command. The command's
	 * handler is first checked for a help context identifier. If the handler
	 * does not have a help context identifier, then the help context identifier
	 * for the command is returned. If neither has a help context identifier,
	 * then <code>null</code> is returned.
	 * 
	 * @param command
	 *            The command for which the help context should be retrieved;
	 *            must not be <code>null</code>.
	 * @return The help context identifier to use for the given command; may be
	 *         <code>null</code>.
	 * @throws NotDefinedException
	 *             If the given command is not defined.
	 * @since 3.2
	 */
	public String getHelpContextId(Command command) throws NotDefinedException;

	/**
	 * Gets the help context identifier for a particular command. The command's
	 * handler is first checked for a help context identifier. If the handler
	 * does not have a help context identifier, then the help context identifier
	 * for the command is returned. If neither has a help context identifier,
	 * then <code>null</code> is returned.
	 * 
	 * @param commandId
	 *            The identifier of the command for which the help context
	 *            should be retrieved; must not be <code>null</code>.
	 * @return The help context identifier to use for the given command; may be
	 *         <code>null</code>.
	 * @throws NotDefinedException
	 *             If the command with the given identifier is not defined.
	 * @since 3.2
	 */
	public String getHelpContextId(String commandId) throws NotDefinedException;

	/**
	 * Retrieves the command parameter type with the given identifier. If no
	 * such parameter type exists, then an undefined parameter type with the
	 * given id is created.
	 * 
	 * @param parameterTypeId
	 *            The identifier to find; must not be <code>null</code>.
	 * @return A command parameter type with the given identifier, either
	 *         defined or undefined.
	 * @since 3.2
	 */
	public ParameterType getParameterType(String parameterTypeId);

	/**
	 * <p>
	 * Reads the command information from the registry and the preferences. This
	 * will overwrite any of the existing information in the command service.
	 * This method is intended to be called during start-up. When this method
	 * completes, this command service will reflect the current state of the
	 * registry and preference store.
	 * </p>
	 */
	public void readRegistry();

	/**
	 * Removes an execution listener from the command service.
	 * 
	 * @param listener
	 *            The listener to remove; must not be <code>null</code>.
	 */
	public void removeExecutionListener(IExecutionListener listener);

	/**
	 * Sets the help context identifier to associate with a particular handler.
	 * 
	 * @param handler
	 *            The handler with which to register a help context identifier;
	 *            must not be <code>null</code>.
	 * @param helpContextId
	 *            The help context identifier to register; may be
	 *            <code>null</code> if the help context identifier should be
	 *            removed.
	 * @since 3.2
	 */
	public void setHelpContextId(IHandler handler, String helpContextId);
}
