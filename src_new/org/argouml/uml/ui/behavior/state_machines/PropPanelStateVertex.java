// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.



// File: PropPanelStateVertex.java
// Classes: PropPanelStateVertex
// Original Author: oliver.heyden@gentleware.de
// $Id:

package org.argouml.uml.ui.behavior.state_machines;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.foundation.data_types.*;

import org.argouml.uml.ui.*;
import org.argouml.uml.ui.foundation.core.*;
import org.argouml.uml.MMUtil;

import org.tigris.gef.util.Util;

public abstract class PropPanelStateVertex extends PropPanelModelElement {

    ////////////////////////////////////////////////////////////////
    // constants
    protected static ImageIcon _stateIcon = Util.loadIconResource("State");
    protected static ImageIcon _actionStateIcon = Util.loadIconResource("ActionState");
    protected static ImageIcon _compositeStateIcon = Util.loadIconResource("CompositeState");
    protected static ImageIcon _simpleStateIcon = Util.loadIconResource("SimpleState");
    protected static ImageIcon _shallowHistoryIcon = Util.loadIconResource("ShallowHistory");
    protected static ImageIcon _deepHistoryIcon = Util.loadIconResource("DeepHistory");
    protected static ImageIcon _finalStateIcon = Util.loadIconResource("FinalState");
    protected static ImageIcon _initialIcon = Util.loadIconResource("Initial");
    protected static ImageIcon _forkIcon = Util.loadIconResource("Fork");
    protected static ImageIcon _joinIcon = Util.loadIconResource("Join");
    protected static ImageIcon _transitionIcon = Util.loadIconResource("Transition");

    ////////////////////////////////////////////////////////////////

    protected JScrollPane incomingScroll;
    protected JScrollPane outgoingScroll;

    ////////////////////////////////////////////////////////////////
    // contructors
    public PropPanelStateVertex(String name, int columns) {
	this(name, null, columns);
    }

    public PropPanelStateVertex(String name,ImageIcon icon, int columns) {
	super(name, icon, columns);

	Class mclass = MStateVertex.class;

	JList incomingList = new UMLList(new UMLReflectionListModel(this,"incomings",true,"getIncomings",null,null,null),true);
	incomingList.setForeground(Color.blue);
	incomingList.setVisibleRowCount(1);
	incomingList.setFont(smallFont);
        incomingScroll = new JScrollPane(incomingList);

	JList outgoingList = new UMLList(new UMLReflectionListModel(this,"outgoings",true,"getOutgoings",null,null,null),true);
	outgoingList.setForeground(Color.blue);
	outgoingList.setVisibleRowCount(1);
	outgoingList.setFont(smallFont);
        outgoingScroll = new JScrollPane(outgoingList);


	new PropPanelButton(this,buttonPanel,_navUpIcon,localize("Go up"),"navigateUp",null);
	new PropPanelButton(this,buttonPanel,_navBackIcon,localize("Go back"),"navigateBackAction","isNavigateBackEnabled");
	new PropPanelButton(this,buttonPanel,_navForwardIcon,localize("Go forward"),"navigateForwardAction","isNavigateForwardEnabled");
	new PropPanelButton(this,buttonPanel,_deleteIcon,localize("Delete"),"removeElement",null);
    }

    public void navigateUp() {
        Object target = getTarget();
        if(target instanceof MStateVertex) {
            MStateVertex elem = (MStateVertex) target;
            MStateVertex container = elem.getContainer();
            if(container != null) {
                navigateTo(container);
            }
        }
    }

      public MStateMachine getStateMachine() {
        MStateMachine machine = null;
        Object target = getTarget();
        if(target instanceof MState) {
            machine = ((MState) target).getStateMachine();
        }
        return machine;
    }

    public java.util.List getIncomings() {
        java.util.Collection incomings = null;
        Object target = getTarget();
        if(target instanceof MStateVertex) {
            incomings = ((MStateVertex) target).getIncomings();
        }
        return new Vector(incomings);
    }

    public java.util.List getOutgoings() {
        java.util.Collection outgoings = null;
        Object target = getTarget();
        if(target instanceof MStateVertex) {
            outgoings = ((MStateVertex) target).getOutgoings();
        }
        return new Vector(outgoings);
    }

      public void removeElement() {
	//overrides removeElement in PropPanel
        Object target = getTarget();
        if(target instanceof MStateVertex) {
            MStateVertex sv = (MStateVertex) target;

            Object newTarget = sv.getContainer();

            MMUtil.SINGLETON.remove(sv);

            if(newTarget != null) {
                navigateTo(newTarget);
            }
        }
    }


} /* end class PropPanelStateVertex */

