// The contents of this file are subject to the Mozilla Public License Version
// 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.mail.facade;

import org.columba.mail.command.IFolderCommandReference;
import org.columba.mail.gui.frame.MailFrameMediator;

/**
 * @author fdietz
 *
 */
public interface ISelectionFacade {
	/**
	 * Get currently selected folder in JTree.
	 * 
	 * @param mediator		top-level frame mediator		
	 * @return				selected folder
	 */
	IFolderCommandReference getTreeSelection(MailFrameMediator mediator);

	/**
	 * Get currently selected messages in JTable.
	 * 
	 * @param mediator		top-level frame mediator
	 * @return				selected messages
	 */
	IFolderCommandReference getTableSelection(MailFrameMediator mediator);

	/**
	 * Get currently selected attachments in AttachmentViewer.
	 * 
	 * @param mediator		top-level frame mediator
	 * @return				selected attachments
	 */
	IFolderCommandReference getAttachmentSelection(MailFrameMediator mediator);

	/**
	 * Get currently selected text in MessageViewer.
	 * 
	 * @param mediator		top-level frame mediator
	 * @return				selected text
	 */
	String getTextSelection(MailFrameMediator mediator);
}