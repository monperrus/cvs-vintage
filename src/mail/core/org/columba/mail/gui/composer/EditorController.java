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

package org.columba.mail.gui.composer;

import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.columba.core.gui.focus.FocusOwner;
import org.columba.core.main.MainInterface;
import org.columba.mail.gui.composer.util.UndoDocument;

/**
 * @author frd
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EditorController implements DocumentListener, FocusOwner, CaretListener {
	EditorView view;
	ComposerController controller;

	private UndoDocument document;

	public EditorController(ComposerController controller) {
		this.controller = controller;

		document = new UndoDocument();

		view = new EditorView(this, document);
		
		MainInterface.focusManager.registerComponent(this);
		
		view.addCaretListener(this);
	}

	public EditorView getView() {
		return view;
	}

	public void installListener() {
		view.installListener(this);
	}

	public void updateComponents(boolean b) {
		if (b) {
			if (controller.getModel().getBodyText() != null)
				view.setText(controller.getModel().getBodyText());
		} else {
			if (view.getText() != null)
				controller.getModel().setBodyText(view.getText());
		}
	}

	public void undo() {
		document.Undo();
	}

	public void redo() {
		document.Redo();
	}

	/************* DocumentListener implementation *******************/

	public void insertUpdate(DocumentEvent e) {
	}
	public void removeUpdate(DocumentEvent e) {
	}
	public void changedUpdate(DocumentEvent e) {
	}

	/************** FocusOwner implementation **************************/

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#copy()
	 */
	public void copy() {
		view.copy();

	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#cut()
	 */
	public void cut() {
		view.cut();

	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#delete()
	 */
	public void delete() {
		view.cut();

	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#getComponent()
	 */
	public JComponent getComponent() {
		return view;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#isCopyActionEnabled()
	 */
	public boolean isCopyActionEnabled() {
		if ( view.getSelectedText() == null ) return false;
		
		if (view.getSelectedText().length() > 0)
			return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#isCutActionEnabled()
	 */
	public boolean isCutActionEnabled() {
		if ( view.getSelectedText() == null ) return false;
		
		if (view.getSelectedText().length() > 0)
			return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#isDeleteActionEnabled()
	 */
	public boolean isDeleteActionEnabled() {
		if ( view.getSelectedText() == null ) return false;
		
		if (view.getSelectedText().length() > 0)
			return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#isPasteActionEnabled()
	 */
	public boolean isPasteActionEnabled() {
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#isSelectAllActionEnabled()
	 */
	public boolean isSelectAllActionEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.focus.FocusOwner#paste()
	 */
	public void paste() {
		view.paste();

	}

	/* (non-Javadoc)
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent arg0) {
		MainInterface.focusManager.updateActions();

	}

}
