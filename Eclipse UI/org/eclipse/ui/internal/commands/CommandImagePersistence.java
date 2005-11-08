/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.ui.internal.commands;import java.net.URL;import java.util.ArrayList;import java.util.List;import org.eclipse.core.runtime.IConfigurationElement;import org.eclipse.core.runtime.IExtensionDelta;import org.eclipse.core.runtime.IExtensionRegistry;import org.eclipse.core.runtime.IRegistryChangeEvent;import org.eclipse.core.runtime.IRegistryChangeListener;import org.eclipse.core.runtime.Platform;import org.eclipse.jface.commands.CommandImageManager;import org.eclipse.swt.widgets.Display;import org.eclipse.ui.PlatformUI;import org.eclipse.ui.commands.ICommandService;import org.eclipse.ui.internal.IWorkbenchConstants;import org.eclipse.ui.internal.util.BundleUtility;/** * <p> * A static class for accessing the registry. * </p> *  * @since 3.2 */final class CommandImagePersistence extends CommonCommandPersistence {	/**	 * The name of the attribute storing the disabled icon for a command image.	 */	private static final String ATTRIBUTE_DISABLED_ICON = "disabledIcon"; //$NON-NLS-1$	/**	 * The name of the attribute storing the hover icon for a command image.	 */	private static final String ATTRIBUTE_HOVER_ICON = "hoverIcon"; //$NON-NLS-1$	/**	 * The name of the attribute storing the default icon for a command image.	 */	private static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$	/**	 * The name of the attribute storing the style for a command image.	 */	private static final String ATTRIBUTE_STYLE = "style"; //$NON-NLS-1$	/**	 * The name of the element storing an image.	 */	private static final String ELEMENT_IMAGE = "image"; //$NON-NLS-1$	/**	 * The name of the commands extension point.	 */	private static final String EXTENSION_COMMAND_IMAGES = PlatformUI.PLUGIN_ID			+ '.' + IWorkbenchConstants.PL_COMMAND_IMAGES;	/**	 * The index of the image elements in the indexed array.	 * 	 * @see CommandImagePersistence#read(CommandImageManager,ICommandService)	 */	private static final int INDEX_IMAGES = 0;	/**	 * Reads all of the images from the command images extension point.	 * 	 * @param configurationElements	 *            The configuration elements in the command images extension	 *            point; must not be <code>null</code>, but may be empty.	 * @param configurationElementCount	 *            The number of configuration elements that are really in the	 *            array.	 * @param commandImageManager	 *            The command image manager to which the images should be added;	 *            must not be <code>null</code>.	 * @param commandService	 *            The command service for the workbench; must not be	 *            <code>null</code>.	 */	private static final void readImagesFromRegistry(			final IConfigurationElement[] configurationElements,			final int configurationElementCount,			final CommandImageManager commandImageManager,			final ICommandService commandService) {		// Undefine all the previous images.		commandImageManager.clear();		final List warningsToLog = new ArrayList(1);		for (int i = 0; i < configurationElementCount; i++) {			final IConfigurationElement configurationElement = configurationElements[i];			// Read out the command identifier.			final String commandId = readRequiredFromRegistry(					configurationElement, ATTRIBUTE_COMMAND_ID, warningsToLog,					"Image needs an id"); //$NON-NLS-1$			if (commandId == null) {				continue;			}			if (!commandService.getCommand(commandId).isDefined()) {				// Reference to an undefined command. This is invalid.				addWarning(warningsToLog,						"Cannot bind to an undefined command", //$NON-NLS-1$						configurationElement.getNamespace(), commandId);				continue;			}			// Read out the style.			final String style = readOptionalFromRegistry(configurationElement,					ATTRIBUTE_STYLE);			// Read out the default icon.			final String icon = readRequiredFromRegistry(configurationElement,					ATTRIBUTE_ICON, warningsToLog, commandId);			if (icon == null) {				continue;			}			final String disabledIcon = readOptionalFromRegistry(					configurationElement, ATTRIBUTE_DISABLED_ICON);			final String hoverIcon = readOptionalFromRegistry(					configurationElement, ATTRIBUTE_HOVER_ICON);			final URL iconURL = BundleUtility.find(configurationElement					.getNamespace(), icon);			commandImageManager.bind(commandId,					CommandImageManager.TYPE_DEFAULT, style, iconURL);			if (disabledIcon != null) {				final URL disabledIconURL = BundleUtility.find(						configurationElement.getNamespace(), disabledIcon);				commandImageManager.bind(commandId,						CommandImageManager.TYPE_DISABLED, style,						disabledIconURL);			}			if (hoverIcon != null) {				final URL hoverIconURL = BundleUtility.find(						configurationElement.getNamespace(), hoverIcon);				commandImageManager.bind(commandId,						CommandImageManager.TYPE_HOVER, style, hoverIconURL);			}		}		logWarnings(				warningsToLog,				"Warnings while parsing the images from the 'org.eclipse.ui.commandImages' extension point."); //$NON-NLS-1$	}	/**	 * Constructs a new instance of <code>CommandImagePersistence</code>.	 */	CommandImagePersistence() {		// Do nothing	}	/**	 * Reads all of the command images from the registry,	 * 	 * @param commandImageManager	 *            The command image manager which should be populated with the	 *            values from the registry; must not be <code>null</code>.	 * @param commandService	 *            The command service for the workbench; must not be	 *            <code>null</code>.	 */	final void read(final CommandImageManager commandImageManager,			final ICommandService commandService) {		// Create the extension registry mementos.		final IExtensionRegistry registry = Platform.getExtensionRegistry();		int imageCount = 0;		final IConfigurationElement[][] indexedConfigurationElements = new IConfigurationElement[1][];		// Sort the commands extension point based on element name.		final IConfigurationElement[] commandImagesExtensionPoint = registry				.getConfigurationElementsFor(EXTENSION_COMMAND_IMAGES);		for (int i = 0; i < commandImagesExtensionPoint.length; i++) {			final IConfigurationElement configurationElement = commandImagesExtensionPoint[i];			final String name = configurationElement.getName();			// Check if it is a binding definition.			if (ELEMENT_IMAGE.equals(name)) {				addElementToIndexedArray(configurationElement,						indexedConfigurationElements, INDEX_IMAGES,						imageCount++);			}		}		readImagesFromRegistry(indexedConfigurationElements[INDEX_IMAGES],				imageCount, commandImageManager, commandService);		/*		 * Adds listener so that future registry changes trigger an update of		 * the command manager automatically.		 */		if (!listenersAttached) {			registry.addRegistryChangeListener(new IRegistryChangeListener() {				public final void registryChanged(						final IRegistryChangeEvent event) {					final IExtensionDelta[] imageDeltas = event							.getExtensionDeltas(PlatformUI.PLUGIN_ID,									IWorkbenchConstants.PL_COMMAND_IMAGES);					if (imageDeltas.length == 0) {						return;					}					/*					 * At least one of the deltas is non-zero, so re-read all of					 * the bindings.					 */					Display.getDefault().asyncExec(new Runnable() {						public void run() {							read(commandImageManager, commandService);						}					});				}			}, PlatformUI.PLUGIN_ID);			listenersAttached = true;		}	}}