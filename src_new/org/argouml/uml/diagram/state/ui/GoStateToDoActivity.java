//Copyright (c) 1996-2001 The Regents of the University of California. All
//Rights Reserved. Permission to use, copy, modify, and distribute this
//software and its documentation without fee, and without a written
//agreement is hereby granted, provided that the above copyright notice
//and this paragraph appear in all copies.  This software program and
//documentation are copyrighted by The Regents of the University of
//California. The software program and documentation are supplied "AS
//IS", without any accompanying services from The Regents. The Regents
//does not warrant that the operation of the program will be
//uninterrupted or error-free. The end-user understands that the program
//was developed for research purposes and is advised not to rely
//exclusively on the program for any reason.  IN NO EVENT SHALL THE
//UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
//SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
//ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
//THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
//WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
//PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
//CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// $Id: GoStateToDoActivity.java,v 1.2 2002/12/28 14:46:30 kataka Exp $

package org.argouml.uml.diagram.state.ui;

import java.util.Collection;
import java.util.Vector;

import org.argouml.ui.AbstractGoRule;

import ru.novosoft.uml.behavior.state_machines.MState;


/**
 * Go rule to navigate from a state to it's doactivity. Used in the package
 * perspective.
 * 
 * @author jaap.branderhorst@xs4all.nl	
 * @since Dec 25, 2002
 */
public class GoStateToDoActivity extends AbstractGoRule {

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
        return !(node instanceof MState && getChildCount(node) > 0);
    }

    /**
     * @see org.argouml.ui.AbstractGoRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
        if (parent instanceof MState && ((MState)parent).getDoActivity() != null) {
            Vector children = new Vector();
            
            children.add(((MState)parent).getDoActivity());
            return children;
        }
        return null;
    }

    /**
     * @see org.argouml.ui.AbstractGoRule#getRuleName()
     */
    public String getRuleName() {
        return "State->Do Activity"; 
    }

}
