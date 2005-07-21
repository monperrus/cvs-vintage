/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.ui.internal.keys;import java.io.IOException;import java.util.ArrayList;import java.util.Arrays;import java.util.HashSet;import java.util.Iterator;import java.util.ResourceBundle;import java.util.Set;import org.eclipse.core.commands.CommandManager;import org.eclipse.core.commands.common.NamedHandleObject;import org.eclipse.core.commands.common.NamedHandleObjectComparator;import org.eclipse.core.commands.common.NotDefinedException;import org.eclipse.core.commands.contexts.ContextManager;import org.eclipse.core.runtime.IStatus;import org.eclipse.core.runtime.Status;import org.eclipse.jface.bindings.Binding;import org.eclipse.jface.bindings.BindingManager;import org.eclipse.jface.bindings.Scheme;import org.eclipse.jface.bindings.keys.KeySequence;import org.eclipse.jface.bindings.keys.KeySequenceText;import org.eclipse.jface.bindings.keys.KeyStroke;import org.eclipse.jface.bindings.keys.ParseException;import org.eclipse.jface.contexts.IContextIds;import org.eclipse.jface.dialogs.ErrorDialog;import org.eclipse.jface.dialogs.IDialogConstants;import org.eclipse.jface.dialogs.MessageDialog;import org.eclipse.jface.preference.PreferencePage;import org.eclipse.jface.viewers.ArrayContentProvider;import org.eclipse.jface.viewers.ComboViewer;import org.eclipse.jface.viewers.NamedHandleObjectLabelProvider;import org.eclipse.jface.viewers.StructuredSelection;import org.eclipse.swt.SWT;import org.eclipse.swt.events.SelectionAdapter;import org.eclipse.swt.events.SelectionEvent;import org.eclipse.swt.graphics.Point;import org.eclipse.swt.layout.GridData;import org.eclipse.swt.layout.GridLayout;import org.eclipse.swt.widgets.Button;import org.eclipse.swt.widgets.Composite;import org.eclipse.swt.widgets.Control;import org.eclipse.swt.widgets.Label;import org.eclipse.swt.widgets.Menu;import org.eclipse.swt.widgets.MenuItem;import org.eclipse.swt.widgets.Text;import org.eclipse.ui.IWorkbench;import org.eclipse.ui.IWorkbenchPreferencePage;import org.eclipse.ui.contexts.IContextService;import org.eclipse.ui.internal.WorkbenchPlugin;import org.eclipse.ui.internal.util.Util;import org.eclipse.ui.keys.IBindingService;/** * @since 3.1 */public final class NewKeysPreferencePage extends PreferencePage implements		IWorkbenchPreferencePage {	/**	 * The resource bundle from which translations can be retrieved.	 */	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle			.getBundle(NewKeysPreferencePage.class.getName());	/**	 * A comparator that can be used for display of	 * <code>NamedHandleObject</code> instances to the end user.	 */	private static final NamedHandleObjectComparator NAMED_HANDLE_OBJECT_COMPARATOR = new NamedHandleObjectComparator();	/**	 * Sorts the given array of <code>NamedHandleObject</code> instances based	 * on their name. This is generally useful if they will be displayed to an	 * end users.	 * 	 * @param objects	 *            The objects to be sorted; must not be <code>null</code>.	 * @return The same array, but sorted in place; never <code>null</code>.	 */	private static final NamedHandleObject[] sortByName(			final NamedHandleObject[] objects) {		Arrays.sort(objects, NAMED_HANDLE_OBJECT_COMPARATOR);		return objects;	}	private IBindingService bindingService = null;	private IContextService contextService = null;	/**	 * A binding manager local to this preference page. When the page is	 * initialized, the current bindings are read out from the binding service	 * and placed in this manager. This manager is then updated as the user	 * makes changes. When the user has finished, the contents of this manager	 * are compared with the contents of the binding service. The changes are	 * then persisted.	 */	private final BindingManager localChangeManager = new BindingManager(			new ContextManager(), new CommandManager());	private ComboViewer schemeCombo = null;	private ComboViewer whenCombo = null;	private final Control createButtonBar(final Composite parent) {		GridLayout layout;		GridData gridData;		int widthHint;		// Create the composite to house the button bar.		final Composite buttonBar = new Composite(parent, SWT.NONE);		layout = new GridLayout(1, false);		layout.marginWidth = 0;		buttonBar.setLayout(layout);		gridData = new GridData();		gridData.horizontalAlignment = SWT.END;		buttonBar.setLayoutData(gridData);		// Advanced button.		final Button advancedButton = new Button(buttonBar, SWT.PUSH);		gridData = new GridData();		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);		advancedButton.setText(Util.translateString(RESOURCE_BUNDLE,				"advancedButton.Text")); //$NON-NLS-1$		gridData.widthHint = Math.max(widthHint, advancedButton.computeSize(				SWT.DEFAULT, SWT.DEFAULT, true).x) + 5;		advancedButton.setLayoutData(gridData);		return buttonBar;	}	/*	 * (non-Javadoc)	 * 	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)	 */	protected final Control createContents(final Composite parent) {		GridLayout layout = null;		// Creates a composite to hold all of the page contents.		final Composite page = new Composite(parent, SWT.NONE);		layout = new GridLayout(1, false);		layout.marginWidth = 0;		page.setLayout(layout);		createSchemeControls(page);		createTree(page);		createTreeControls(page);		createDataControls(page);		createButtonBar(page);		update(true);		return page;	}	private final Control createDataControls(final Composite parent) {		GridLayout layout;		GridData gridData;		// Creates the data area.		final Composite dataArea = new Composite(parent, SWT.NONE);		layout = new GridLayout(5, false);		layout.marginWidth = 0;		dataArea.setLayout(layout);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		dataArea.setLayoutData(gridData);		// FIRST ROW		// The command name label.		final Label commandNameLabel = new Label(dataArea, SWT.NONE);		commandNameLabel.setText(Util.translateString(RESOURCE_BUNDLE,				"commandNameLabel.Text")); //$NON-NLS-1$); //$NON-NLS-1$		// The current command name.		final Label commandNameValueLabel = new Label(dataArea, SWT.NONE);		// TODO This should be update dynamically		commandNameValueLabel.setText("Word Completion"); //$NON-NLS-1$		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.horizontalAlignment = SWT.BEGINNING;		commandNameValueLabel.setLayoutData(gridData);		// The binding label.		final Label bindingLabel = new Label(dataArea, SWT.NONE);		bindingLabel.setText(Util.translateString(RESOURCE_BUNDLE,				"bindingLabel.Text")); //$NON-NLS-1$		// The key sequence entry widget.		final Text bindingText = new Text(dataArea, SWT.BORDER);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.widthHint = 300;		bindingText.setLayoutData(gridData);		final KeySequenceText keySequenceText = new KeySequenceText(bindingText);		try {			keySequenceText.setKeySequence(KeySequence.getInstance("ALT+/")); //$NON-NLS-1$		} catch (final ParseException e) {			// TODO This should be done dynamically.		}		keySequenceText.setKeyStrokeLimit(4);		// Button for adding trapped key strokes		final Button addKeyButton = new Button(dataArea, SWT.LEFT | SWT.ARROW);		addKeyButton.setToolTipText(Util.translateString(RESOURCE_BUNDLE,				"addKeyButton.ToolTipText")); //$NON-NLS-1$		gridData = new GridData();		gridData.heightHint = schemeCombo.getCombo().getTextHeight();		addKeyButton.setLayoutData(gridData);		// Arrow buttons aren't normally added to the tab list. Let's fix that.		final Control[] tabStops = dataArea.getTabList();		final ArrayList newTabStops = new ArrayList();		for (int i = 0; i < tabStops.length; i++) {			Control tabStop = tabStops[i];			newTabStops.add(tabStop);			if (bindingText.equals(tabStop)) {				newTabStops.add(addKeyButton);			}		}		final Control[] newTabStopArray = (Control[]) newTabStops				.toArray(new Control[newTabStops.size()]);		dataArea.setTabList(newTabStopArray);		// Construct the menu to attach to the above button.		final Menu addKeyMenu = new Menu(addKeyButton);		final Iterator trappedKeyItr = KeySequenceText.TRAPPED_KEYS.iterator();		while (trappedKeyItr.hasNext()) {			final KeyStroke trappedKey = (KeyStroke) trappedKeyItr.next();			final MenuItem menuItem = new MenuItem(addKeyMenu, SWT.PUSH);			menuItem.setText(trappedKey.format());			menuItem.addSelectionListener(new SelectionAdapter() {				public void widgetSelected(SelectionEvent e) {					keySequenceText.insert(trappedKey);					bindingText.setFocus();					bindingText.setSelection(bindingText.getTextLimit());				}			});		}		addKeyButton.addSelectionListener(new SelectionAdapter() {			public void widgetSelected(SelectionEvent selectionEvent) {				Point buttonLocation = addKeyButton.getLocation();				buttonLocation = dataArea.toDisplay(buttonLocation.x,						buttonLocation.y);				Point buttonSize = addKeyButton.getSize();				addKeyMenu.setLocation(buttonLocation.x, buttonLocation.y						+ buttonSize.y);				addKeyMenu.setVisible(true);			}		});		// SECOND ROW.		// The description label.		final Label descriptionLabel = new Label(dataArea, SWT.NONE);		descriptionLabel.setText(Util.translateString(RESOURCE_BUNDLE,				"descriptionLabel.Text")); //$NON-NLS-1$		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.horizontalSpan = 2;		descriptionLabel.setLayoutData(gridData);		// The when label.		final Label whenLabel = new Label(dataArea, SWT.NONE);		whenLabel.setText(Util.translateString(RESOURCE_BUNDLE,				"whenLabel.Text")); //$NON-NLS-1$		// The when combo.		whenCombo = new ComboViewer(dataArea);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.horizontalSpan = 2;		whenCombo.getCombo().setLayoutData(gridData);		whenCombo.setLabelProvider(new NamedHandleObjectLabelProvider());		whenCombo.setContentProvider(new ArrayContentProvider());		// THIRD ROW.		// The description value.		final Label descriptionValueLabel = new Label(dataArea, SWT.WRAP);		// TODO This value should be updated dynamically.		descriptionValueLabel.setText("Context insensitive completion"); //$NON-NLS-1$		gridData = new GridData();		gridData.horizontalSpan = 5;		gridData.horizontalAlignment = SWT.FILL;		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalIndent = 30;		gridData.verticalIndent = 5;		descriptionValueLabel.setLayoutData(gridData);		return dataArea;	}	private final Control createSchemeControls(final Composite parent) {		GridLayout layout;		GridData gridData;		int widthHint;		// Create a composite to hold the controls.		final Composite schemeControls = new Composite(parent, SWT.NONE);		layout = new GridLayout(3, false);		layout.marginWidth = 0;		schemeControls.setLayout(layout);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		schemeControls.setLayoutData(gridData);		// Create the label.		final Label schemeLabel = new Label(schemeControls, SWT.NONE);		schemeLabel.setText(Util.translateString(RESOURCE_BUNDLE,				"schemeLabel.Text")); //$NON-NLS-1$		// Create the combo.		schemeCombo = new ComboViewer(schemeControls);		schemeCombo.setLabelProvider(new NamedHandleObjectLabelProvider());		schemeCombo.setContentProvider(new ArrayContentProvider());		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		schemeCombo.getCombo().setLayoutData(gridData);		// Create the delete button.		final Button deleteSchemeButton = new Button(schemeControls, SWT.PUSH);		gridData = new GridData();		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);		deleteSchemeButton.setText(Util.translateString(RESOURCE_BUNDLE,				"deleteSchemeButton.Text")); //$NON-NLS-1$		gridData.widthHint = Math.max(widthHint, deleteSchemeButton				.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x) + 5;		deleteSchemeButton.setLayoutData(gridData);		return schemeControls;	}	private final Control createTree(final Composite parent) {		final Composite filteredTableTree = new DougsSuperFilteredTableTree(				parent, SWT.SINGLE | SWT.FULL_SELECTION);		final GridLayout layout = new GridLayout(2, false);		layout.marginWidth = 0;		filteredTableTree.setLayout(layout);		final GridData gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.grabExcessVerticalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.verticalAlignment = SWT.FILL;		filteredTableTree.setLayoutData(gridData);		return filteredTableTree;	}	private final Control createTreeControls(final Composite parent) {		GridLayout layout;		GridData gridData;		int widthHint;		// Creates controls related to the tree.		final Composite treeControls = new Composite(parent, SWT.NONE);		layout = new GridLayout(2, false);		layout.marginWidth = 0;		treeControls.setLayout(layout);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		treeControls.setLayoutData(gridData);		// Create the show all check box.		final Button showAllCheckBox = new Button(treeControls, SWT.CHECK);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		showAllCheckBox.setLayoutData(gridData);		showAllCheckBox.setText(Util.translateString(RESOURCE_BUNDLE,				"showAllCheckBox.Text")); //$NON-NLS-1$		// Create the delete binding button.		final Button deleteBindingButton = new Button(treeControls, SWT.PUSH);		gridData = new GridData();		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);		deleteBindingButton.setText(Util.translateString(RESOURCE_BUNDLE,				"deleteBindingButton.Text")); //$NON-NLS-1$		gridData.widthHint = Math.max(widthHint, deleteBindingButton				.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x) + 5;		deleteBindingButton.setLayoutData(gridData);		return treeControls;	}	/*	 * (non-Javadoc)	 * 	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)	 */	public final void init(final IWorkbench workbench) {		bindingService = (IBindingService) workbench				.getAdapter(IBindingService.class);		contextService = (IContextService) workbench				.getAdapter(IContextService.class);	}	/**	 * Logs the given exception, and opens an error dialog saying that something	 * went wrong. The exception is assumed to have something to do with the	 * preference store.	 * 	 * @param exception	 *            The exception to be logged; must not be <code>null</code>.	 */	private final void logPreferenceStoreException(final Throwable exception) {		final String message = Util.translateString(RESOURCE_BUNDLE,				"PreferenceStoreError.Message"); //$NON-NLS-1$		final String title = Util.translateString(RESOURCE_BUNDLE,				"PreferenceStoreError.Title"); //$NON-NLS-1$		String exceptionMessage = exception.getMessage();		if (exceptionMessage == null) {			exceptionMessage = message;		}		final IStatus status = new Status(IStatus.ERROR,				WorkbenchPlugin.PI_WORKBENCH, 0, exceptionMessage, exception);		WorkbenchPlugin.log(message, status);		ErrorDialog.openError(schemeCombo.getCombo().getShell(), title,				message, status);	}	protected final void performDefaults() {		// Ask the user to confirm		final String title = Util.translateString(RESOURCE_BUNDLE,				"restoreDefaultsMessageBoxText"); //$NON-NLS-1$		final String message = Util.translateString(RESOURCE_BUNDLE,				"restoreDefaultsMessageBoxMessage"); //$NON-NLS-1$		final boolean confirmed = MessageDialog.openConfirm(getShell(), title,				message);		if (confirmed) {			// Fix the scheme in the local changes.			final String defaultSchemeId = bindingService.getDefaultSchemeId();			final Scheme defaultScheme = localChangeManager					.getScheme(defaultSchemeId);			try {				localChangeManager.setActiveScheme(defaultScheme);			} catch (final NotDefinedException e) {				// At least we tried....			}			// Fix the bindings in the local changes.			final Binding[] currentBindings = localChangeManager.getBindings();			final int currentBindingsLength = currentBindings.length;			final Set trimmedBindings = new HashSet();			for (int i = 0; i < currentBindingsLength; i++) {				final Binding binding = currentBindings[i];				if (binding.getType() != Binding.USER) {					trimmedBindings.add(binding);				}			}			final Binding[] trimmedBindingArray = (Binding[]) trimmedBindings					.toArray(new Binding[trimmedBindings.size()]);			localChangeManager.setBindings(trimmedBindingArray);			// Apply the changes.			try {				bindingService.savePreferences(defaultScheme,						trimmedBindingArray);			} catch (final IOException e) {				logPreferenceStoreException(e);			}		}		setScheme(localChangeManager.getActiveScheme());		super.performDefaults();	}	public final boolean performOk() {		// Save the preferences.		try {			bindingService.savePreferences(					localChangeManager.getActiveScheme(), localChangeManager							.getBindings());		} catch (final IOException e) {			logPreferenceStoreException(e);		}		return super.performOk();	}	/**	 * Sets the currently selected scheme. Setting the scheme always triggers an	 * update of the underlying widgets.	 * 	 * @param scheme	 *            The scheme to select; may be <code>null</code>.	 */	private final void setScheme(final Scheme scheme) {		schemeCombo.setSelection(new StructuredSelection(scheme));	}	private final void update(final boolean registryChanged) {		updateSchemeCombo(registryChanged);		updateWhenCombo(registryChanged);	}	private final void updateSchemeCombo(final boolean registryChanged) {		if (registryChanged) {			schemeCombo					.setInput(sortByName(bindingService.getDefinedSchemes()));			setScheme(bindingService.getActiveScheme());		}	}	private final void updateWhenCombo(final boolean registryChanged) {		if (registryChanged) {			whenCombo.setInput(sortByName(contextService.getDefinedContexts()));			// TODO This should be updated based on the active context/			whenCombo.setSelection(new StructuredSelection(contextService					.getContext(IContextIds.CONTEXT_ID_WINDOW)), true);		}	}}