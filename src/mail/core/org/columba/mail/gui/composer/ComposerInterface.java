// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.


package org.columba.mail.gui.composer;

import javax.swing.JFrame;

import org.columba.core.command.TaskManager;
import org.columba.core.config.Config;
import org.columba.core.config.WindowItem;
import org.columba.core.gui.FrameController;
import org.columba.core.util.CharsetManager;
import org.columba.main.MainInterface;
import org.columba.addressbook.gui.AddressbookPanel;
import org.columba.mail.composer.MessageComposer;
import org.columba.mail.folder.Folder;
import org.columba.mail.gui.composer.action.ComposerActionListener;
import org.columba.mail.gui.composer.util.IdentityInfoPanel;
import org.columba.mail.gui.composer.util.UndoDocument;
import org.columba.mail.gui.tree.TreeView;
import org.columba.mail.pgp.PGPController;

public class ComposerInterface
{
  
    public ComposerSpellCheck composerSpellCheck;
    
    public JFrame composerFrame;
   
    public ComposerActionListener composerActionListener;
    public TreeView treeViewer;
    public MessageComposer messageComposer;
   
    public TaskManager taskManager;
    public IdentityInfoPanel identityInfoPanel;
    public Config config;
    public UndoDocument message;
    public PGPController pgpController;
    public Folder composerFolder;
    public MainInterface mainInterface;
    
    
    public AddressbookPanel addressbookPanel;


	public AttachmentController attachmentController;
	public SubjectController subjectController;
	public PriorityController priorityController;
	public AccountController accountController;
	public EditorController editorController;
	public HeaderController headerController;
	
	public ComposerController composerController;
	
	public CharsetManager charsetManager;
	
	public  WindowItem windowItem;
	public JFrame addressbookFrame;
	
	public FrameController frameController;
		
    public ComposerInterface( Config conf  )
        {
	    config = conf;
        }
        
    public ComposerInterface() {};

}





