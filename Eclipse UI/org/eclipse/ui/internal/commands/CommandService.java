/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.SerializationException;
import org.eclipse.core.commands.State;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.commands.PersistentState;
import org.eclipse.ui.commands.ICallbackReference;
import org.eclipse.ui.commands.ICallbackUpdater;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.util.PrefUtil;

/**
 * <p>
 * Provides services related to the command architecture within the workbench.
 * This service can be used to access the set of commands and handlers.
 * </p>
 * 
 * @since 3.1
 */
public final class CommandService implements ICommandService {

	/**
	 * The preference key prefix for all handler state.
	 */
	private static final String PREFERENCE_KEY_PREFIX = "org.eclipse.ui.commands/state"; //$NON-NLS-1$

	/**
	 * Creates a preference key for the given piece of state on the given
	 * command.
	 * 
	 * @param command
	 *            The command for which the preference key should be created;
	 *            must not be <code>null</code>.
	 * @param stateId
	 *            The identifier of the state for which the preference key
	 *            should be created; must not be <code>null</code>.
	 * @return A suitable preference key; never <code>null</code>.
	 */
	static final String createPreferenceKey(final Command command,
			final String stateId) {
		return PREFERENCE_KEY_PREFIX + '/' + command.getId() + '/' + stateId;
	}

	/**
	 * The command manager that supports this service. This value is never
	 * <code>null</code>.
	 */
	private final CommandManager commandManager;

	/**
	 * The persistence class for this command service.
	 */
	private final CommandPersistence commandPersistence;

	/**
	 * Constructs a new instance of <code>CommandService</code> using a
	 * command manager.
	 * 
	 * @param commandManager
	 *            The command manager to use; must not be <code>null</code>.
	 */
	public CommandService(final CommandManager commandManager) {
		if (commandManager == null) {
			throw new NullPointerException(
					"Cannot create a command service with a null manager"); //$NON-NLS-1$
		}
		this.commandManager = commandManager;
		this.commandPersistence = new CommandPersistence(this);
	}

	public final void addExecutionListener(final IExecutionListener listener) {
		commandManager.addExecutionListener(listener);
	}

	public final void defineUncategorizedCategory(final String name,
			final String description) {
		commandManager.defineUncategorizedCategory(name, description);
	}

	public final ParameterizedCommand deserialize(
			final String serializedParameterizedCommand)
			throws NotDefinedException, SerializationException {
		return commandManager.deserialize(serializedParameterizedCommand);
	}

	public final void dispose() {
		commandPersistence.dispose();

		/*
		 * All state on all commands neeeds to be disposed. This is so that the
		 * state has a chance to persist any changes.
		 */
		final Command[] commands = commandManager.getAllCommands();
		for (int i = 0; i < commands.length; i++) {
			final Command command = commands[i];
			final String[] stateIds = command.getStateIds();
			for (int j = 0; j < stateIds.length; j++) {
				final String stateId = stateIds[j];
				final State state = command.getState(stateId);
				if (state instanceof PersistentState) {
					final PersistentState persistentState = (PersistentState) state;
					if (persistentState.shouldPersist()) {
						persistentState.save(PrefUtil
								.getInternalPreferenceStore(),
								createPreferenceKey(command, stateId));
					}
				}
			}
		}
		commandCallbacks = null;
	}

	public final Category getCategory(final String categoryId) {
		return commandManager.getCategory(categoryId);
	}

	public final Command getCommand(final String commandId) {
		return commandManager.getCommand(commandId);
	}

	public final Category[] getDefinedCategories() {
		return commandManager.getDefinedCategories();
	}

	public final Collection getDefinedCategoryIds() {
		return commandManager.getDefinedCategoryIds();
	}

	public final Collection getDefinedCommandIds() {
		return commandManager.getDefinedCommandIds();
	}

	public final Command[] getDefinedCommands() {
		return commandManager.getDefinedCommands();
	}

	public Collection getDefinedParameterTypeIds() {
		return commandManager.getDefinedParameterTypeIds();
	}

	public ParameterType[] getDefinedParameterTypes() {
		return commandManager.getDefinedParameterTypes();
	}

	public final String getHelpContextId(final Command command)
			throws NotDefinedException {
		return commandManager.getHelpContextId(command);
	}

	public final String getHelpContextId(final String commandId)
			throws NotDefinedException {
		final Command command = getCommand(commandId);
		return commandManager.getHelpContextId(command);
	}

	public ParameterType getParameterType(final String parameterTypeId) {
		return commandManager.getParameterType(parameterTypeId);
	}

	public final void readRegistry() {
		commandPersistence.read();
	}

	public final void removeExecutionListener(final IExecutionListener listener) {
		commandManager.removeExecutionListener(listener);
	}

	public final void setHelpContextId(final IHandler handler,
			final String helpContextId) {
		commandManager.setHelpContextId(handler, helpContextId);
	}

	/**
	 * This is a map of commandIds to a list containing currently registered
	 * callbacks, in the form of ICallbackReferences.
	 */
	private Map commandCallbacks = new HashMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.commands.ICommandService#refreshCallbacks(java.lang.String,
	 *      java.util.Map)
	 */
	public final void refreshCallbacks(String commandId, Map filter) {
		Command cmd = getCommand(commandId);

		if (!cmd.isDefined() || !(cmd.getHandler() instanceof ICallbackUpdater)) {
			return;
		}
		final ICallbackUpdater updater = (ICallbackUpdater) cmd.getHandler();

		List callbackRefs = (List) commandCallbacks.get(commandId);
		if (callbackRefs == null) {
			return;
		}

		for (Iterator i = callbackRefs.iterator(); i.hasNext();) {
			ICallbackReference callbackRef = (ICallbackReference) i.next();
			Map parms = Collections
					.unmodifiableMap(callbackRef.getParameters());
			if (filter == null) {
				updater.updateCallback(callbackRef.getCallback(), parms);
			} else {
				boolean match = true;
				for (Iterator j = filter.entrySet().iterator(); j.hasNext()
						&& match;) {
					Map.Entry parmEntry = (Map.Entry) j.next();
					Object value = parms.get(parmEntry.getKey());
					if (!parmEntry.getValue().equals(value)) {
						match = false;
					}
				}
				if (match) {
					updater.updateCallback(callbackRef.getCallback(), parms);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.commands.ICommandService#registerCallbackForCommand(org.eclipse.core.commands.ParameterizedCommand,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public final ICallbackReference registerCallbackForCommand(
			ParameterizedCommand command, IAdaptable callback)
			throws NotDefinedException {
		if (!command.getCommand().isDefined()) {
			throw new NotDefinedException(
					"Cannot define a callback for undefined command " //$NON-NLS-1$
							+ command.getCommand().getId());
		}
		if (callback == null) {
			throw new NotDefinedException("No callback defined for command " //$NON-NLS-1$
					+ command.getCommand().getId());
		}

		CallbackReference ref = new CallbackReference(command.getId(),
				callback, command.getParameterMap());
		registerCallback(ref);
		return ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.commands.ICommandService#registerCallback(org.eclipse.ui.commands.ICallbackReference)
	 */
	public void registerCallback(ICallbackReference callbackReference) {
		List parameterizedCommands = (List) commandCallbacks
				.get(callbackReference.getCommandId());
		if (parameterizedCommands == null) {
			parameterizedCommands = new ArrayList();
			commandCallbacks.put(callbackReference.getCommandId(),
					parameterizedCommands);
		}
		parameterizedCommands.add(callbackReference);

		// If the active handler wants to update the callback, it can do
		// so now
		Command command = getCommand(callbackReference.getCommandId());
		if (command.isDefined()) {
			if (command.getHandler() instanceof ICallbackUpdater) {
				((ICallbackUpdater) command.getHandler()).updateCallback(
						callbackReference.getCallback(), callbackReference
								.getParameters());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.commands.ICommandService#unregisterCallback(org.eclipse.ui.commands.ICallbackReference)
	 */
	public void unregisterCallback(ICallbackReference callbackReference) {
		List parameterizedCommands = (List) commandCallbacks
				.get(callbackReference.getCommandId());
		if (parameterizedCommands != null) {
			parameterizedCommands.remove(callbackReference);
			if (parameterizedCommands.isEmpty()) {
				commandCallbacks.remove(callbackReference.getCommandId());
			}
		}
	}
}
