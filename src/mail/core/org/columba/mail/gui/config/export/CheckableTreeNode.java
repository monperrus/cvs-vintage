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

package org.columba.mail.gui.config.export;

import javax.swing.tree.TreeNode;

import org.columba.core.gui.checkabletree.CheckableItemImpl;
import org.columba.mail.folder.FolderTreeNode;

/**
 * @author fdietz
 */
public class CheckableTreeNode extends CheckableItemImpl {

    private FolderTreeNode node;
    
    /**
     * 
     */
    public CheckableTreeNode(String name) {
        super(name);
    }

    /**
     * @return
     */
    public FolderTreeNode getNode() {
        return node;
    }

    /**
     * @param node
     */
    public void setNode(FolderTreeNode node) {
        this.node = node;
    }
}
