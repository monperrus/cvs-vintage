/*
Copyright (c) 2000, 2001, 2002 IBM Corp.
All rights reserved.  This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
Contributors:
Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog font should be
activated and used by other components.
*/

package org.eclipse.ui.internal.actions.keybindings;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.actions.Action;
import org.eclipse.ui.internal.actions.Util;

final class DialogCustomize extends Dialog {

	private final static int DIFFERENCE_ADD = 0;	
	private final static int DIFFERENCE_CHANGE = 1;	
	private final static int DIFFERENCE_MINUS = 2;	
	private final static int DIFFERENCE_NONE = 3;	
	private final static int SPACE = 8;	
	private final static RGB rgbConflict = new RGB(255, 0, 0);
	private final static RGB rgbConflictMinus = new RGB(255, 192, 192);
	private final static RGB rgbMinus =	new RGB(192, 192, 192);
	private final static String ACTION_CONFLICT = Messages.getString("DialogCustomize.ActionConflict"); //$NON-NLS-1$
	private final static String ACTION_UNDEFINED = Messages.getString("DialogCustomize.ActionUndefined"); //$NON-NLS-1$
	private final static String ZERO_LENGTH_STRING = ""; //$NON-NLS-1$

	private final class ActionRecord {

		String actionId;
		KeySequence keySequence;
		String scopeId;
		String configurationId;
		Set customSet;
		Set defaultSet;
	}

	private final class KeySequenceRecord {

		String scopeId;
		String configurationId;
		Set customSet;
		Set defaultSet;
	}

	private String defaultConfigurationId;
	private String defaultScopeId;
	private SortedSet preferenceBindingSet;	
	
	private KeyManager keyManager;
	private KeyMachine keyMachine;
	private SortedMap registryActionMap;
	private SortedSet registryBindingSet;
	private SortedMap registryConfigurationMap;
	private SortedMap registryScopeMap;
	
	private List actions;
	private List configurations;
	private List scopes;

	private String[] actionNames;
	private String[] configurationNames;	
	private String[] scopeNames;

	private Label labelAction;
	private Combo comboAction;
	private Table tableAction;
	//private Button buttonShowConflicts;	
	private Label labelKeySequence;
	private Combo comboKeySequence;
	private Table tableKeySequence;
	private Button buttonBrowseSelectedAction;
	private Group groupState;
	private Label labelScope; 
	private Combo comboScope;
	private Label labelConfiguration; 
	private Combo comboConfiguration;
	private Group groupAction;
	private Button buttonDefault;
	private Text textDefault;
	private Button buttonCustom; 
	private Combo comboCustom;

	private SortedMap tree;
	private Map nameToKeySequenceMap;
	private List actionRecords = new ArrayList();	
	private List keySequenceRecords = new ArrayList();

	public DialogCustomize(Shell parentShell, String defaultConfigurationId, String defaultScopeId, SortedSet preferenceBindingSet)
		throws IllegalArgumentException {
		super(parentShell);
		
		if (defaultConfigurationId == null || defaultScopeId == null || preferenceBindingSet == null)
			throw new IllegalArgumentException();
			
		this.defaultConfigurationId = defaultConfigurationId;
		this.defaultScopeId = defaultScopeId;
		preferenceBindingSet = new TreeSet(preferenceBindingSet);
		Iterator iterator = preferenceBindingSet.iterator();
		
		while (iterator.hasNext())
			if (!(iterator.next() instanceof Binding))
				throw new IllegalArgumentException();
	
		this.preferenceBindingSet = preferenceBindingSet;

		keyManager = KeyManager.getInstance();
		keyMachine = keyManager.getKeyMachine();

		registryActionMap = org.eclipse.ui.internal.actions.Registry.getInstance().getActionMap();
		actions = new ArrayList();
		actions.addAll(registryActionMap.values());
		Collections.sort(actions, Action.nameComparator());				
	
		registryBindingSet = keyManager.getRegistryBindingSet();
		
		registryConfigurationMap = keyManager.getRegistryConfigurationMap();
		configurations = new ArrayList();
		configurations.addAll(registryConfigurationMap.values());	
		Collections.sort(configurations, Configuration.nameComparator());				
		
		registryScopeMap = keyManager.getRegistryScopeMap();	
		scopes = new ArrayList();
		scopes.addAll(registryScopeMap.values());	
		Collections.sort(scopes, Scope.nameComparator());				

		actionNames = new String[1 + actions.size()];
		actionNames[0] = ACTION_UNDEFINED;
		
		for (int i = 0; i < actions.size(); i++)
			actionNames[i + 1] = ((Action) actions.get(i)).getLabel().getName();

		configurationNames = new String[configurations.size()];
		
		for (int i = 0; i < configurations.size(); i++)
			configurationNames[i] = ((Configuration) configurations.get(i)).getLabel().getName();

		scopeNames = new String[scopes.size()];
		
		for (int i = 0; i < scopes.size(); i++)
			scopeNames[i] = ((Scope) scopes.get(i)).getLabel().getName();
		
		tree = new TreeMap();
		SortedSet bindingSet = new TreeSet();
		bindingSet.addAll(preferenceBindingSet);
		bindingSet.addAll(registryBindingSet);
		iterator = bindingSet.iterator();
		
		while (iterator.hasNext()) {
			Binding binding = (Binding) iterator.next();				
			set(tree, binding, false);			
		}

		nameToKeySequenceMap = new HashMap();	
		Collection keySequences = tree.keySet();
		iterator = keySequences.iterator();

		while (iterator.hasNext()) {
			KeySequence keySequence = (KeySequence) iterator.next();
			String name = keyManager.getTextForKeySequence(keySequence);
			
			if (!nameToKeySequenceMap.containsKey(name))
				nameToKeySequenceMap.put(name, keySequence);
		}

		//uncomment this line for a resizable dialog. layout behavior while resizing has been tested.
		//setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public SortedSet getPreferenceBindingSet() {
		return Collections.unmodifiableSortedSet(preferenceBindingSet);	
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("DialogCustomize.Title"));
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		createUI(composite);
		return composite;		
	}	

	protected void okPressed() {
		preferenceBindingSet = solve(tree);
		super.okPressed();
	}

	private void clear(SortedMap tree, KeySequence keySequence, String scope, String configuration) {			
		Map scopeMap = (Map) tree.get(keySequence);
		
		if (scopeMap != null) {
			Map configurationMap = (Map) scopeMap.get(scope);
		
			if (configurationMap != null) {
				Map pluginMap = (Map) configurationMap.get(configuration);
	
				if (pluginMap != null) {
					pluginMap.remove(null);
					
					if (pluginMap.isEmpty()) {
						configurationMap.remove(configuration);
						
						if (configurationMap.isEmpty()) {
							scopeMap.remove(scope);	

							if (scopeMap.isEmpty()) {
								tree.remove(keySequence);	
							}							
						}	
					}	
				}	
			}
		}
	}

	private void set(SortedMap tree, Binding binding, boolean consolidate) {			
		Map scopeMap = (Map) tree.get(binding.getKeySequence());
		
		if (scopeMap == null) {
			scopeMap = new TreeMap();	
			tree.put(binding.getKeySequence(), scopeMap);
		}

		Map configurationMap = (Map) scopeMap.get(binding.getScope());
		
		if (configurationMap == null) {
			configurationMap = new TreeMap();	
			scopeMap.put(binding.getScope(), configurationMap);
		}
		
		Map pluginMap = (Map) configurationMap.get(binding.getConfiguration());
		
		if (pluginMap == null) {
			pluginMap = new HashMap();	
			configurationMap.put(binding.getConfiguration(), pluginMap);
		}

		Map actionMap = consolidate ? null : (Map) pluginMap.get(binding.getPlugin());
		
		if (actionMap == null) {
			actionMap = new HashMap();	
			pluginMap.put(binding.getPlugin(), actionMap);
		}

		Set bindingSet = (Set) actionMap.get(binding.getAction());
		
		if (bindingSet == null) {
			bindingSet = new TreeSet();
			actionMap.put(binding.getAction(), bindingSet);	
		}

		if (consolidate)
			bindingSet.clear();
		
		bindingSet.add(binding);
	}

	private SortedSet solve(SortedMap tree) {
		SortedSet bindingSet = new TreeSet();
		Iterator iterator = tree.values().iterator();
		
		while (iterator.hasNext()) {
			Map scopeMap = (Map) iterator.next();
			Iterator iterator2 = scopeMap.values().iterator();
			
			while (iterator2.hasNext()) {
				Map configurationMap = (Map) iterator2.next();
				Iterator iterator3 = configurationMap.values().iterator();
				
				while (iterator3.hasNext()) {
					Map pluginMap = (Map) iterator3.next();
					Map actionMap = (Map) pluginMap.get(null);
					
					if (actionMap != null) {
						Iterator iterator4 = actionMap.values().iterator();
						
						while (iterator4.hasNext())
							bindingSet.addAll((Set) iterator4.next());
					}
				}
			}		
		}
		
		return bindingSet;
	}

	private String getConfigurationId() {
		int selection = comboConfiguration.getSelectionIndex();
		
		if (selection >= 0 && selection < configurations.size()) {
			Configuration configuration = (Configuration) configurations.get(selection);
			return configuration.getLabel().getId();				
		}
		
		return null;
	}

	private String[] getKeySequences() {
		String[] items = (String[]) nameToKeySequenceMap.keySet().toArray(new String[nameToKeySequenceMap.size()]);
		Arrays.sort(items, Collator.getInstance());
		return items;
	}

	private String getScopeId() {
		int selection = comboScope.getSelectionIndex();
		
		if (selection >= 0 && selection < scopes.size()) {
			Scope scope = (Scope) scopes.get(selection);
			return scope.getLabel().getId();				
		}
		
		return null;
	}

	private void change(boolean custom) {
		KeySequence keySequence = null;
		String name = comboKeySequence.getText();
		
		if (name != null || name.length() > 0) {
			keySequence = (KeySequence) nameToKeySequenceMap.get(name);
			
			if (keySequence == null)
				keySequence = KeyManager.parseKeySequenceStrict(name);
		}				

		if (keySequence != null) {
			String scopeId = getScopeId();
			String configurationId = getConfigurationId();

			if (!custom)
				clear(tree, keySequence, scopeId, configurationId);						
			else { 
				String actionId = null;				
				int selection = comboCustom.getSelectionIndex();
				
				if (selection < 0)
					selection = comboAction.getSelectionIndex();
		
				selection--;
			
				if (selection >= 0 && selection < actions.size()) {
					Action action = (Action) actions.get(selection);
					actionId = action.getLabel().getId();
				}				

				set(tree, Binding.create(actionId, configurationId, keySequence, null, 0, scopeId), true);				
				/*
				name = keyManager.getTextForKeySequence(keySequence);			
				
				if (!nameToKeySequenceMap.containsKey(name))
					nameToKeySequenceMap.put(name, keySequence);
	
				comboKeySequence.setItems(getKeySequences());
				*/					
			}
		}
				
		update();
	}

	private void modifiedComboKeySequence() {
		update();
	}

	private void selectedButtonBrowseSelectedAction() {
		// TBD browse action
	}

	private void selectedButtonCustom() {
		change(true);
	}

	private void selectedButtonDefault() {
		change(false);
	}

	private void selectedButtonShowConflicts() {
		// TBD add dialog to display the plugin map for selected row in tableAction
	}

	private void selectedComboAction() {
		update();
	}

	private void selectedComboConfiguration() {
		update();				
	}

	private void selectedComboCustom() {
		change(true);
	}

	private void selectedComboKeySequence() {			
		update();
	}
	
	private void selectedComboScope() {
		update();	
	}

	private void selectedTableAction() {
		update();	
	}
	
	private void selectedTableKeySequence() {
		update();	
	}

	private void buildActionRecords(SortedMap tree, String actionId, List actionRecords) {
		if (actionRecords != null) {
			actionRecords.clear();
				
			if (tree != null) {
				Iterator iterator = tree.entrySet().iterator();
					
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					KeySequence keySequence = (KeySequence) entry.getKey();					
					Map scopeMap = (Map) entry.getValue();						
		
					if (scopeMap != null) {
						Iterator iterator2 = scopeMap.entrySet().iterator();
						
						while (iterator2.hasNext()) {
							Map.Entry entry2 = (Map.Entry) iterator2.next();
							String scopeId = (String) entry2.getKey();										
							Map configurationMap = (Map) entry2.getValue();						
							Iterator iterator3 = configurationMap.entrySet().iterator();
										
							while (iterator3.hasNext()) {
								Map.Entry entry3 = (Map.Entry) iterator3.next();
								String configurationId = (String) entry3.getKey();					
								Map pluginMap = (Map) entry3.getValue();													
								Set customSet = new HashSet();
								Set defaultSet = new HashSet();						
								buildPluginSets(pluginMap, customSet, defaultSet);

								if (customSet.contains(actionId) || defaultSet.contains(actionId)) {
									ActionRecord actionRecord = new ActionRecord();
									actionRecord.actionId = actionId;
									actionRecord.keySequence = keySequence;
									actionRecord.scopeId = scopeId;
									actionRecord.configurationId = configurationId;
									actionRecord.customSet = customSet;
									actionRecord.defaultSet = defaultSet;	
									actionRecords.add(actionRecord);									
								}
							}
						}
					}
				}												
			}	
		}
	}
	
	private void buildKeySequenceRecords(SortedMap tree, KeySequence keySequence, List keySequenceRecords) {
		if (keySequenceRecords != null) {
			keySequenceRecords.clear();
			
			if (tree != null && keySequence != null) {
				Map scopeMap = (Map) tree.get(keySequence);
			
				if (scopeMap != null) {
					Iterator iterator = scopeMap.entrySet().iterator();
			
					while (iterator.hasNext()) {
						Map.Entry entry = (Map.Entry) iterator.next();
						String scopeId2 = (String) entry.getKey();					
						Map configurationMap = (Map) entry.getValue();						
						Iterator iterator2 = configurationMap.entrySet().iterator();
							
						while (iterator2.hasNext()) {
							Map.Entry entry2 = (Map.Entry) iterator2.next();
							String configurationId2 = (String) entry2.getKey();					
							Map pluginMap = (Map) entry2.getValue();			
							KeySequenceRecord keySequenceRecord = new KeySequenceRecord();
							keySequenceRecord.scopeId = scopeId2;
							keySequenceRecord.configurationId = configurationId2;							
							keySequenceRecord.customSet = new HashSet();
							keySequenceRecord.defaultSet = new HashSet();						
							buildPluginSets(pluginMap, keySequenceRecord.customSet, keySequenceRecord.defaultSet);			
							keySequenceRecords.add(keySequenceRecord);
						}												
					}	
				}								
			}			
		}
	}

	private void buildPluginSets(Map pluginMap, Set customSet, Set defaultSet) {
		Iterator iterator = pluginMap.entrySet().iterator(); 

		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String pluginId = (String) entry.getKey();
			Map actionMap = (Map) entry.getValue();
			Iterator iterator2 = actionMap.keySet().iterator();
	
			while (iterator2.hasNext()) {
				String actionId = (String) iterator2.next();
		
				if (pluginId == null)
					customSet.add(actionId);
				else 
					defaultSet.add(actionId);									
			}
		}
	}

	private void buildTableAction() {
		tableAction.removeAll();

		for (int i = 0; i < actionRecords.size(); i++) {
			ActionRecord actionRecord = (ActionRecord) actionRecords.get(i);
			Set customSet = actionRecord.customSet;
			Set defaultSet = actionRecord.defaultSet;			
			boolean customConflict = false;
			String customActionId = null;
			boolean defaultConflict = false;
			String defaultActionId = null;	

			if (customSet.size() > 1)
				customConflict = true;
			else if (!customSet.isEmpty())				
				customActionId = (String) customSet.iterator().next();

			if (defaultSet.size() > 1)
				defaultConflict = true;
			else if (!defaultSet.isEmpty())				
				defaultActionId = (String) defaultSet.iterator().next();

			boolean addRow = false;
			int difference = DIFFERENCE_NONE;
			String actionId = null;
			boolean actionConflict = false;
			String alternateActionId = null;
			boolean alternateActionConflict = false;
	
			if (customSet.isEmpty()) {
				if (defaultSet.contains(actionRecord.actionId)) {
					addRow = true;														
					actionId = actionRecord.actionId;
					actionConflict = defaultConflict;					
				}
			} else {
				if (defaultSet.isEmpty()) {									
					if (customSet.contains(actionRecord.actionId)) {
						addRow = true;														
						difference = DIFFERENCE_ADD;
						actionId = actionRecord.actionId;
						actionConflict = customConflict;
					}
				} else {
					if (customSet.contains(actionRecord.actionId)) {
						addRow = true;	
						difference = DIFFERENCE_CHANGE;
						actionId = actionRecord.actionId;
						actionConflict = customConflict;		
						alternateActionId = defaultActionId;
						alternateActionConflict = defaultConflict;
					} else {
						if (defaultSet.contains(actionRecord.actionId)) {
							addRow = true;	
							difference = DIFFERENCE_MINUS;
							actionId = actionRecord.actionId;
							actionConflict = defaultConflict;		
							alternateActionId = customActionId;
							alternateActionConflict = customConflict;
						}
					}
				}								
			}

			if (addRow) {
				TableItem tableItem = new TableItem(tableAction, SWT.NULL);					

				switch (difference) {
					case DIFFERENCE_ADD:
						tableItem.setImage(0, ImageFactory.getImage("plus"));
						break;
	
					case DIFFERENCE_CHANGE:
						tableItem.setImage(0, ImageFactory.getImage("change"));
						break;
	
					case DIFFERENCE_MINUS:
						tableItem.setImage(0, ImageFactory.getImage("minus"));
						break;
	
					case DIFFERENCE_NONE:
						break;				
				}

				Scope scope = (Scope) registryScopeMap.get(actionRecord.scopeId);
				tableItem.setText(1, scope != null ? scope.getLabel().getName() : "[" + actionRecord.scopeId + "]");
				Configuration configuration = (Configuration) registryConfigurationMap.get(actionRecord.configurationId);			
				tableItem.setText(2, configuration != null ? configuration.getLabel().getName() : "[" + actionRecord.configurationId + "]");
				boolean conflict = actionConflict || alternateActionConflict;
				StringBuffer stringBuffer = new StringBuffer();
	
				if (actionRecord.keySequence != null)
					stringBuffer.append(keyManager.getTextForKeySequence(actionRecord.keySequence));
	
				if (actionConflict)
					stringBuffer.append(" " + ACTION_CONFLICT);

				if (difference == DIFFERENCE_CHANGE) {
					stringBuffer.append(" (was: ");
					String alternateActionName = null;
					
					if (alternateActionId == null) 
						alternateActionName = ACTION_UNDEFINED;
					else {
						Action action = (Action) registryActionMap.get(alternateActionId);
						
						if (action != null)
							alternateActionName = action.getLabel().getName();
						else
							alternateActionName = "[" + alternateActionId + "]";
					}
									
					stringBuffer.append(alternateActionName);
	
					if (alternateActionConflict)
						stringBuffer.append(" " + ACTION_CONFLICT);
	
					stringBuffer.append(')');
				} else if (difference == DIFFERENCE_MINUS) {
					stringBuffer.append(" (now: ");
					
					String alternateActionName = null;
					
					if (alternateActionId == null) 
						alternateActionName = ACTION_UNDEFINED;
					else {
						Action action = (Action) registryActionMap.get(alternateActionId);
						
						if (action != null)
							alternateActionName = action.getLabel().getName();
						else
							alternateActionName = "[" + alternateActionId + "]";
					}
									
					stringBuffer.append(alternateActionName);
					
					if (alternateActionConflict)
						stringBuffer.append(" " + ACTION_CONFLICT);

					stringBuffer.append(')');
				}
	
				tableItem.setText(3, stringBuffer.toString());				
	
				if (difference == DIFFERENCE_MINUS) {
					if (conflict)
						tableItem.setForeground(new Color(getShell().getDisplay(), rgbConflictMinus));	
					else 
						tableItem.setForeground(new Color(getShell().getDisplay(), rgbMinus));	
				} else if (conflict)
					tableItem.setForeground(new Color(getShell().getDisplay(), rgbConflict));	
			}
		}			
	}
	
	private void buildTableKeySequence() {
		String scopeId = getScopeId();
		String configurationId = getConfigurationId();
		tableKeySequence.removeAll();
	
		for (int i = 0; i < keySequenceRecords.size(); i++) {
			KeySequenceRecord keySequenceRecord = (KeySequenceRecord) keySequenceRecords.get(i);
			Set customSet = keySequenceRecord.customSet;
			Set defaultSet = keySequenceRecord.defaultSet;			
			boolean customConflict = false;
			String customActionId = null;
			boolean defaultConflict = false;
			String defaultActionId = null;	

			if (customSet.size() > 1)
				customConflict = true;
			else if (!customSet.isEmpty())				
				customActionId = (String) customSet.iterator().next();

			if (defaultSet.size() > 1)
				defaultConflict = true;
			else if (!defaultSet.isEmpty())				
				defaultActionId = (String) defaultSet.iterator().next();

			int difference = DIFFERENCE_NONE;
			String actionId = null;
			boolean actionConflict = false;
			String alternateActionId = null;
			boolean alternateActionConflict = false;

			if (customSet.isEmpty()) {
				actionId = defaultActionId;															
				actionConflict = defaultConflict;
			} else {
				actionId = customActionId;															
				actionConflict = customConflict;						

				if (defaultSet.isEmpty())
					difference = DIFFERENCE_ADD;
				else {
					difference = DIFFERENCE_CHANGE;									
					alternateActionId = defaultActionId;
					alternateActionConflict = defaultConflict;																		
				}
			}

			TableItem tableItem = new TableItem(tableKeySequence, SWT.NULL);					

			switch (difference) {
				case DIFFERENCE_ADD:
					tableItem.setImage(0, ImageFactory.getImage("plus"));
					break;
	
				case DIFFERENCE_CHANGE:
					tableItem.setImage(0, ImageFactory.getImage("change"));
					break;
	
				case DIFFERENCE_MINUS:
					tableItem.setImage(0, ImageFactory.getImage("minus"));
					break;
	
				case DIFFERENCE_NONE:
					break;				
			}

			Scope scope = (Scope) registryScopeMap.get(keySequenceRecord.scopeId);
			tableItem.setText(1, scope != null ? scope.getLabel().getName() : "[" + keySequenceRecord.scopeId + "]");
			Configuration configuration = (Configuration) registryConfigurationMap.get(keySequenceRecord.configurationId);			
			tableItem.setText(2, configuration != null ? configuration.getLabel().getName() : "[" + keySequenceRecord.configurationId + "]");
			boolean conflict = actionConflict || alternateActionConflict;
			StringBuffer stringBuffer = new StringBuffer();
			String actionName = null;
					
			if (actionId == null) 
				actionName = ACTION_UNDEFINED;
			else {
				Action action = (Action) registryActionMap.get(actionId);
						
				if (action != null)
					actionName = action.getLabel().getName();
				else
					actionName = "[" + actionId + "]";
			}
			
			stringBuffer.append(actionName);

			if (actionConflict)
				stringBuffer.append(" " + ACTION_CONFLICT);

			if (difference == DIFFERENCE_CHANGE) {
				stringBuffer.append(" (was: ");
				String alternateActionName = null;
					
				if (alternateActionId == null) 
					alternateActionName = ACTION_UNDEFINED;
				else {
					Action action = (Action) registryActionMap.get(alternateActionId);
						
					if (action != null)
						alternateActionName = action.getLabel().getName();
					else
						alternateActionName = "[" + alternateActionId + "]";
				}
									
				stringBuffer.append(alternateActionName);
	
				if (alternateActionConflict)
					stringBuffer.append(" " + ACTION_CONFLICT);
	
				stringBuffer.append(')');
			}
	
			tableItem.setText(3, stringBuffer.toString());

			if (difference == DIFFERENCE_MINUS) {
				if (conflict)
					tableItem.setForeground(new Color(getShell().getDisplay(), rgbConflictMinus));	
				else 
					tableItem.setForeground(new Color(getShell().getDisplay(), rgbMinus));	
			} else if (conflict)
				tableItem.setForeground(new Color(getShell().getDisplay(), rgbConflict));	
		}
	}

	private void selectTableAction(KeySequence keySequence, String scopeId, String configurationId) {	
	}

	private void selectTableKeySequence(String scopeId, String configurationId) {		
	}

	private void update() {
		actionRecords.clear();
		int selection = comboAction.getSelectionIndex();

		if (selection >= 0 && selection <= actions.size() && tree != null) {		
			String actionId = null;				
			
			if (selection > 0) {
				Action action = (Action) actions.get(selection - 1);
				actionId = action.getLabel().getId();
			}

			buildActionRecords(tree, actionId, actionRecords);
		}

		buildTableAction();

		KeySequence keySequence = null;
		String name = comboKeySequence.getText();		
		keySequence = (KeySequence) nameToKeySequenceMap.get(name);
			
		if (keySequence == null)
			// TBD review. still not strict enough. convertAccelerator says 'Ctrl+Ax' is valid.				
			keySequence = KeyManager.parseKeySequenceStrict(name);

		buildKeySequenceRecords(tree, keySequence, keySequenceRecords);

		buildTableKeySequence();
			
		String scopeId = getScopeId();
		String configurationId = getConfigurationId();	

		Set customSet = Collections.EMPTY_SET;
		Set defaultSet = Collections.EMPTY_SET;
		Iterator iterator = keySequenceRecords.iterator();
		
		while (iterator.hasNext()) {
			KeySequenceRecord keySequenceRecord = (KeySequenceRecord) iterator.next();

			if (Util.equals(scopeId, keySequenceRecord.scopeId) && Util.equals(configurationId, keySequenceRecord.configurationId)) {							
				customSet = keySequenceRecord.customSet;
				defaultSet = keySequenceRecord.defaultSet;
				break;
			}			
		}

		setAction(customSet, defaultSet);

		boolean bValidKeySequence = keySequence != null && keySequence.getKeyStrokes().size() >= 1;
		tableKeySequence.setEnabled(bValidKeySequence);
		buttonBrowseSelectedAction.setEnabled(bValidKeySequence); //TBD + table has selection
		groupState.setEnabled(bValidKeySequence);
		labelScope.setEnabled(bValidKeySequence);
		comboScope.setEnabled(bValidKeySequence);
		labelConfiguration.setEnabled(bValidKeySequence);
		comboConfiguration.setEnabled(bValidKeySequence);
		groupAction.setEnabled(bValidKeySequence);
		buttonDefault.setEnabled(bValidKeySequence);
		textDefault.setEnabled(bValidKeySequence);
		buttonCustom.setEnabled(bValidKeySequence);
		comboCustom.setEnabled(bValidKeySequence);
	}

	private GridLayout createGridLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = SPACE;
		gridLayout.marginHeight = SPACE;
		gridLayout.marginWidth = SPACE;
		gridLayout.verticalSpacing = SPACE;
		return gridLayout;
	}		
		
	private void createUI(Composite composite) {
		Font font = composite.getFont();
		GridLayout gridLayout = createGridLayout();
		composite.setLayout(gridLayout);

		Group groupBrowseAction = new Group(composite, SWT.NULL);	
		groupBrowseAction.setFont(font);
		gridLayout = createGridLayout();
		gridLayout.numColumns = 3;		
		groupBrowseAction.setLayout(gridLayout);
		groupBrowseAction.setLayoutData(new GridData(GridData.FILL_BOTH));
		groupBrowseAction.setText(Messages.getString("DialogCustomize.GroupBrowseAction"));	

		labelAction = new Label(groupBrowseAction, SWT.LEFT);
		labelAction.setFont(font);
		labelAction.setText(Messages.getString("DialogCustomize.LabelAction"));

		comboAction = new Combo(groupBrowseAction, SWT.READ_ONLY);
		comboAction.setFont(font);
		GridData gridData = new GridData();
		gridData.widthHint = 250;
		comboAction.setLayoutData(gridData);
		
		Label spacer = new Label(groupBrowseAction, SWT.NULL);
		spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));				

		tableAction = new Table(groupBrowseAction, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tableAction.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 75;		
		gridData.horizontalSpan = 3;		
		tableAction.setLayoutData(gridData);
		tableAction.setFont(font);

		TableColumn tableColumn = new TableColumn(tableAction, SWT.NULL, 0);
		tableColumn.setResizable(false);
		tableColumn.setText(ZERO_LENGTH_STRING);
		tableColumn.setWidth(20);

		tableColumn = new TableColumn(tableAction, SWT.NULL, 1);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderScope"));
		tableColumn.setWidth(100);

		tableColumn = new TableColumn(tableAction, SWT.NULL, 2);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderConfiguration"));
		tableColumn.setWidth(100);

		tableColumn = new TableColumn(tableAction, SWT.NULL, 3);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderKeySequence"));
		tableColumn.setWidth(250);	

		/*
		buttonShowConflicts = new Button(groupBrowseAction, SWT.CENTER | SWT.PUSH);
		buttonShowConflicts.setFont(font);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.heightHint = convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		gridData.horizontalSpan = 3;				
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		buttonShowConflicts.setText(Messages.getString("DialogCustomize.ButtonShowConflicts"));
		gridData.widthHint = Math.max(widthHint, buttonShowConflicts.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x) + SPACE;
		buttonShowConflicts.setLayoutData(gridData);		
		*/

		Group groupBrowseKeySequence = new Group(composite, SWT.NULL);	
		groupBrowseKeySequence.setFont(font);
		gridLayout = createGridLayout();
		gridLayout.numColumns = 3;		
		groupBrowseKeySequence.setLayout(gridLayout);
		groupBrowseKeySequence.setLayoutData(new GridData(GridData.FILL_BOTH));
		groupBrowseKeySequence.setText(Messages.getString("DialogCustomize.GroupBrowseKeySequence"));	

		labelKeySequence = new Label(groupBrowseKeySequence, SWT.LEFT);
		labelKeySequence.setFont(font);
		labelKeySequence.setText(Messages.getString("DialogCustomize.LabelKeySequence"));

		comboKeySequence = new Combo(groupBrowseKeySequence, SWT.NULL);
		comboKeySequence.setFont(font);
		gridData = new GridData();
		gridData.widthHint = 250;
		comboKeySequence.setLayoutData(gridData);
		
		spacer = new Label(groupBrowseKeySequence, SWT.NULL);
		spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));				

		tableKeySequence = new Table(groupBrowseKeySequence, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tableKeySequence.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 75;		
		gridData.horizontalSpan = 3;		
		tableKeySequence.setLayoutData(gridData);
		tableKeySequence.setFont(font);

		tableColumn = new TableColumn(tableKeySequence, SWT.NULL, 0);
		tableColumn.setResizable(false);
		tableColumn.setText(ZERO_LENGTH_STRING);
		tableColumn.setWidth(20);

		tableColumn = new TableColumn(tableKeySequence, SWT.NULL, 1);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderScope"));
		tableColumn.setWidth(100);

		tableColumn = new TableColumn(tableKeySequence, SWT.NULL, 2);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderConfiguration"));
		tableColumn.setWidth(100);

		tableColumn = new TableColumn(tableKeySequence, SWT.NULL, 3);
		tableColumn.setResizable(true);
		tableColumn.setText(Messages.getString("DialogCustomize.HeaderAction"));
		tableColumn.setWidth(250);	

		buttonBrowseSelectedAction = new Button(groupBrowseKeySequence, SWT.CENTER | SWT.PUSH);
		buttonBrowseSelectedAction.setFont(font);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.heightHint = convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		gridData.horizontalSpan = 3;				
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		buttonBrowseSelectedAction.setText(Messages.getString("DialogCustomize.ButtonBrowseSelectedAction"));
		gridData.widthHint = Math.max(widthHint, buttonBrowseSelectedAction.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x) + SPACE;
		buttonBrowseSelectedAction.setLayoutData(gridData);		
		
		Composite compositeStateAndAction = new Composite(groupBrowseKeySequence, SWT.NULL);
		gridLayout = createGridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;		
		gridLayout.numColumns = 2;
		compositeStateAndAction.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		compositeStateAndAction.setLayoutData(gridData);

		groupState = new Group(compositeStateAndAction, SWT.NULL);	
		groupState.setFont(font);
		gridLayout = createGridLayout();
		gridLayout.numColumns = 2;		
		groupState.setLayout(gridLayout);
		groupState.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupState.setText(Messages.getString("DialogCustomize.GroupState"));

		labelScope = new Label(groupState, SWT.LEFT);
		labelScope.setFont(font);
		labelScope.setText(Messages.getString("DialogCustomize.LabelScope"));

		comboScope = new Combo(groupState, SWT.READ_ONLY);
		comboScope.setFont(font);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 100;
		comboScope.setLayoutData(gridData);

		labelConfiguration = new Label(groupState, SWT.LEFT);
		labelConfiguration.setFont(font);
		labelConfiguration.setText(Messages.getString("DialogCustomize.LabelConfiguration"));

		comboConfiguration = new Combo(groupState, SWT.READ_ONLY);
		comboConfiguration.setFont(font);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 100;
		comboConfiguration.setLayoutData(gridData);

		groupAction = new Group(compositeStateAndAction, SWT.NULL);	
		groupAction.setFont(font);
		gridLayout = createGridLayout();
		gridLayout.numColumns = 2;		
		groupAction.setLayout(gridLayout);
		groupAction.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupAction.setText(Messages.getString("DialogCustomize.GroupAction"));

		buttonDefault = new Button(groupAction, SWT.LEFT | SWT.RADIO);
		buttonDefault.setFont(font);
		buttonDefault.setText(Messages.getString("DialogCustomize.ButtonDefault"));

		textDefault = new Text(groupAction, SWT.BORDER | SWT.READ_ONLY);
		textDefault.setFont(font);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 250;
		textDefault.setLayoutData(gridData);

		buttonCustom = new Button(groupAction, SWT.LEFT | SWT.RADIO);
		buttonCustom.setFont(font);
		buttonCustom.setText(Messages.getString("DialogCustomize.ButtonCustom"));

		comboCustom = new Combo(groupAction, SWT.READ_ONLY);
		comboCustom.setFont(font);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 250;
		comboCustom.setLayoutData(gridData);

		comboAction.setItems(actionNames);
		comboKeySequence.setItems(getKeySequences());
		comboScope.setItems(scopeNames);
		comboConfiguration.setItems(configurationNames);
		comboCustom.setItems(actionNames);

		setConfigurationId(defaultConfigurationId);
		setScopeId(defaultScopeId);

		comboAction.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedComboAction();
			}	
		});

		tableAction.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				selectedButtonShowConflicts();	
			}			
		});		

		tableAction.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = tableAction.getSelectionIndex();

				if (i >= 0) {
					ActionRecord actionRecord = (ActionRecord) actionRecords.get(i);						
					
					if (actionRecord != null) {
						setKeySequence(actionRecord.keySequence);					
						setScopeId(actionRecord.scopeId);
						setConfigurationId(actionRecord.configurationId);	
					}
				}
				
				selectedTableAction();
			}	
		});

		/*
		buttonShowConflicts.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedButtonShowConflicts();
			}	
		});
		*/		

		comboKeySequence.addModifyListener(new ModifyListener() {			
			public void modifyText(ModifyEvent e) {
				modifiedComboKeySequence();
			}	
		});

		comboKeySequence.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedComboKeySequence();
			}	
		});

		tableKeySequence.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				//selectedButtonBrowseSelectedAction();	
			}			
		});		

		tableKeySequence.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = tableKeySequence.getSelectionIndex();
				
				if (i >= 0) {
					KeySequenceRecord keySequenceRecord = (KeySequenceRecord) keySequenceRecords.get(i);						
					
					if (keySequenceRecord != null) {
						setScopeId(keySequenceRecord.scopeId);
						setConfigurationId(keySequenceRecord.configurationId);	
					}
				}
				
				selectedTableKeySequence();
			}	
		});

		buttonBrowseSelectedAction.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedButtonBrowseSelectedAction();
			}	
		});

		comboScope.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedComboScope();
			}	
		});

		comboConfiguration.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedComboConfiguration();
			}	
		});

		buttonDefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedButtonDefault();
			}	
		});

		buttonCustom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedButtonCustom();
			}	
		});
		
		comboCustom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedComboCustom();
			}	
		});

		update();
	}

	private void browseAction(String actionId) {		
	}

	private void unbrowseAction() {
	}
	
	private void browseKeySequence(KeySequence keySequence) {
	}
	
	private void unbrowseKeySequence() {
	}

	private void setAction(Set customSet, Set defaultSet) {		
		comboCustom.deselectAll();
		buttonDefault.setSelection(customSet.isEmpty());
		buttonCustom.setSelection(!customSet.isEmpty());									

		boolean customConflict = false;
		String customActionId = null;
		boolean defaultConflict = false;
		String defaultActionId = null;	

		if (customSet.size() > 1)
			customConflict = true;
		else if (!customSet.isEmpty())				
			customActionId = (String) customSet.iterator().next();

		if (defaultSet.size() > 1)
			defaultConflict = true;
		else if (!defaultSet.isEmpty())				
			defaultActionId = (String) defaultSet.iterator().next();

		comboCustom.setText(customActionId != null ? customActionId : "");

		if (!customSet.isEmpty()) {
			if (customConflict)
				comboCustom.setText(ACTION_CONFLICT);
			else {			
				if (customActionId == null)
					comboCustom.select(0);
				else
					for (int i = 0; i < actions.size(); i++) {
						Action action = (Action) actions.get(i);		
								
						if (action.getLabel().getId().equals(customActionId)) {
							comboCustom.select(i + 1);
							break;		
						}
					}			
			}
		}

		textDefault.setText(defaultActionId != null ? defaultActionId : "");

		if (defaultConflict)
			textDefault.setText(ACTION_CONFLICT);
		else {
			if (defaultActionId == null)
				textDefault.setText(ACTION_UNDEFINED);
			else {
				for (int j = 0; j < actions.size(); j++) {
					Action action = (Action) actions.get(j);		
								
					if (action.getLabel().getId().equals(defaultActionId)) {
						textDefault.setText(action.getLabel().getName());
						break;		
					}
				}
			}
		}	
	}
	
	private void setConfigurationId(String configurationId) {				
		comboConfiguration.clearSelection();
		comboConfiguration.deselectAll();
		
		if (configurationId != null)	
			for (int i = 0; i < configurations.size(); i++) {
				Configuration configuration = (Configuration) configurations.get(i);		
				
				if (configuration.getLabel().getId().equals(configurationId)) {
					comboConfiguration.select(i);
					break;		
				}
			}
	}	

	private void setKeySequence(KeySequence keySequence) {				
		comboKeySequence.clearSelection();
		comboKeySequence.deselectAll();
		
		if (keySequence != null) {
			String name = keyManager.getTextForKeySequence(keySequence);
			
			if (name != null)
				comboKeySequence.setText(name);
		}	
	}	
	
	private void setScopeId(String scopeId) {				
		comboScope.clearSelection();
		comboScope.deselectAll();
		
		if (scopeId != null)	
			for (int i = 0; i < scopes.size(); i++) {
				Scope scope = (Scope) scopes.get(i);		
				
				if (scope.getLabel().getId().equals(scopeId)) {
					comboScope.select(i);
					break;		
				}
			}
	}
}
