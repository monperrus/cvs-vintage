//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.
package org.columba.core.gui.focus;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.columba.core.action.BasicAction;

/**
 * 
 * Every {@link FocusOwner} should register at the <code>FocusManager</code>.
 * <p>
 * FocusManager enables and disables the following Actions:
 * <ul>
 *  <li>CutAction</li>
 *  <li>CopyAction</li>
 *  <li>PasteAction</li>
 *  <li>DeleteAction</li>
 *  <li>SelectAllAction</li>
 * </ul>
 * 
 * 
 * @author fdietz
 *
 */
public class FocusManager implements FocusListener {

	/**
	 * list of focus owners
	 */
	List list;

	/**
	 * map associating focus listener ui with focus owner
	 */
	Map map;

	/**
	 * all actions
	 */
	BasicAction cutAction;
	BasicAction copyAction;
	BasicAction pasteAction;
	BasicAction deleteAction;
	BasicAction selectAllAction;
	BasicAction undoAction;
	BasicAction redoAction;

	/**
	 * current focus owner
	 */
	FocusOwner current = null;

	/**
	 * last available focus owner
	 */
	FocusOwner last = null;

	public FocusManager() {
		list = new Vector();
		map = new HashMap();
	}

	/**
	 * register FocusOwner and add FocusListener
	 * 
	 * @param c		focus owner
	 */
	public void registerComponent(FocusOwner c) {
		list.add(c);

		// associate ui component with FocusOwner
		map.put(c.getComponent(), c);

		c.getComponent().addFocusListener(this);
	}

	/**
	 * Get current focus owner
	 * 
	 * Try first current owner. If this fails, try
	 * the last available one.
	 * 
	 * @return	current focus owner
	 */
	protected FocusOwner getCurrentOwner() {
		if (current != null)
			return current;

		if (last != null)
			return last;

		return null;
	}

	/**
	 * 
	 * FocusOwner objects should call this method on
	 * selection changes in their view component to
	 * enable/disable the actions
	 *
	 */
	public void updateActions() {
		enableActions(getCurrentOwner());
	}

	/**
	 * enable/disable actions
	 * 
	 * @param o		current focus owner
	 */
	protected void enableActions(FocusOwner o) {
		if (o == null) {
			//  no component has the focus
			// -> disable all actions

			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
			pasteAction.setEnabled(false);
			deleteAction.setEnabled(false);
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);
			selectAllAction.setEnabled(false);
			return;
		}

		cutAction.setEnabled(o.isCutActionEnabled());
		copyAction.setEnabled(o.isCopyActionEnabled());
		pasteAction.setEnabled(o.isPasteActionEnabled());
		deleteAction.setEnabled(o.isDeleteActionEnabled());
		undoAction.setEnabled(o.isUndoActionEnabled());
		redoAction.setEnabled(o.isRedoActionEnabled());
		selectAllAction.setEnabled(o.isSelectAllActionEnabled());
	}

	/**
	 * Component gained focus
	 * 
	 */
	public void focusGained(FocusEvent event) {

		current = (FocusOwner) map.get(event.getSource());

		updateActions();

	}

	/**
	 * Component lost focus
	 */
	public void focusLost(FocusEvent event) {

		FocusOwner lost = (FocusOwner) map.get(event.getSource());

		last = current;

		current = null;
		//current = lost;

		updateActions();

	}

	/**
	 * execute cut action of currently available focus owner 
	 *
	 */
	public void cut() {
		getCurrentOwner().cut();

		enableActions(getCurrentOwner());
	}

	/**
		 * execute copy action of currently available focus owner 
		 *
		 */
	public void copy() {
		getCurrentOwner().copy();

		enableActions(getCurrentOwner());
	}

	/**
		 * execute paste action of currently available focus owner 
		 *
		 */
	public void paste() {
		getCurrentOwner().paste();

		enableActions(getCurrentOwner());
	}

	/**
		 * execute delete action of currently available focus owner 
		 *
		 */
	public void delete() {
		getCurrentOwner().delete();

		enableActions(getCurrentOwner());
	}

	/**
	 * execute redo action of currentyl available focus owner
	 *
	 */
	public void redo() {
		getCurrentOwner().redo();
		enableActions(getCurrentOwner());
	}

	/**
	 * execute undo action of currentyl available focus owner
	 *
	 */
	public void undo() {
		getCurrentOwner().undo();
		enableActions(getCurrentOwner());
	}

	/**
		 * execute selectAll action of currentyl available focus owner
		 *
		 */
	public void selectAll() {
		getCurrentOwner().selectAll();
		enableActions(getCurrentOwner());
	}

	/************************* setter of actions **********************/

	/**
	 * @param action
	 */
	public void setCopyAction(BasicAction action) {
		copyAction = action;
	}

	/**
	 * @param action
	 */
	public void setCutAction(BasicAction action) {
		cutAction = action;
	}

	/**
	 * @param action
	 */
	public void setDeleteAction(BasicAction action) {
		deleteAction = action;
	}

	/**
	 * @param action
	 */
	public void setPasteAction(BasicAction action) {
		pasteAction = action;
	}

	/**
	 * @param action
	 */
	public void setRedoAction(BasicAction action) {
		redoAction = action;
	}

	/**
	 * @param action
	 */
	public void setSelectAllAction(BasicAction action) {
		selectAllAction = action;
	}

	/**
	 * @param action
	 */
	public void setUndoAction(BasicAction action) {
		undoAction = action;
	}

}