// $Id: WizBreakCircularComp.java,v 1.8 2003/09/11 00:07:16 bobtarling Exp $
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



// File: WizBreakCircularComp.java
// Classes: WizBreakCircularComp
// Original Author: jrobbins@ics.uci.edu
// $Id: WizBreakCircularComp.java,v 1.8 2003/09/11 00:07:16 bobtarling Exp $

package org.argouml.uml.cognitive.critics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

import org.argouml.cognitive.ui.WizStepChoice;
import org.argouml.cognitive.ui.WizStepConfirm;
import org.argouml.kernel.Wizard;
import org.argouml.model.ModelFacade;
import org.argouml.uml.generator.GeneratorDisplay;
import org.tigris.gef.util.VectorSet;
/** A non-modal wizard to help the user change select an association
 *  to make non-aggregate. */

public class WizBreakCircularComp extends Wizard {
    protected static Logger cat =
	Logger.getLogger(WizBreakCircularComp.class);
						      
    protected String _instructions1 =
	"Please select one of the following classes. " +
	"In the next two steps a association of that class will " +
	"be made non-aggregate.";
							  
    protected String _instructions2 =
	"Please select one of the following associations. " +
	"In the next step that association will " +
	"be made non-aggregate.";
							      
    protected String _instructions3 =
	"Are you sure you want to make this association non-aggregate?";
								  
    protected WizStepChoice _step1 = null;
    protected WizStepChoice _step2 = null;
    protected WizStepConfirm _step3 = null;
									      
    protected Object _selectedCls = null;
    protected Object _selectedAsc = null;

    public WizBreakCircularComp() { }

    public int getNumSteps() { return 3; }

    protected Vector getOptions1() {
	Vector res = new Vector();
	if (_item != null) {
	    VectorSet offs = _item.getOffenders();
	    int size = offs.size();
	    for (int i = 0; i < size; i++) {
		Object me = /*(MModelElement)*/ offs.elementAt(i);
		String s = GeneratorDisplay.Generate(ModelFacade.getName(me));
		res.addElement(s);
	    }
	}
	return res;
    }
 
    protected Vector getOptions2() {
	Vector res = new Vector();
	if (_selectedCls != null) {
	    Collection aes = ModelFacade.getAssociationEnds(_selectedCls);
	    int size = aes.size();
	    Object fromType = _selectedCls;
	    String fromName = GeneratorDisplay.Generate(ModelFacade.getName(fromType));
	    for (Iterator iter = aes.iterator(); iter.hasNext();) {
		Object fromEnd = /*(MAssociationEnd)*/ iter.next();
		Object asc = ModelFacade.getAssociation(fromEnd);
		Object toEnd = /*(MAssociationEnd)*/
		    new ArrayList(ModelFacade.getConnections(asc)).get(0);
		if (toEnd == fromEnd)
		    toEnd = /*(MAssociationEnd)*/ new ArrayList(ModelFacade.getConnections(asc)).get(1);
		Object toType = ModelFacade.getType(toEnd);
		String ascName = GeneratorDisplay.Generate(ModelFacade.getName(asc));
		String toName = GeneratorDisplay.Generate(ModelFacade.getName(toType));
		String s = ascName + " from " + fromName + " to " + toName;
		res.addElement(s);
	    }
	}
	return res;
    }

    /** Create a new panel for the given step.  */
    public JPanel makePanel(int newStep) {
	switch (newStep) {
	case 1:
	    if (_step1 == null) {
		_step1 = new WizStepChoice(this, _instructions1, getOptions1());
		_step1.setTarget(_item);
	    }
	    return _step1;
	case 2:
	    if (_step2 == null) {
		_step2 = new WizStepChoice(this, _instructions2, getOptions2());
		_step2.setTarget(_item);
	    }
	    return _step2;
	case 3:
	    if (_step3 == null) {
		_step3 = new WizStepConfirm(this, _instructions3);
	    }
	    return _step3;
	}
	return null;
    }

    /** Take action at the completion of a step. For example, when the
     *  given step is 0, do nothing; and when the given step is 1, do
     *  the first action.  Argo non-modal wizards should take action as
     *  they do along, as soon as possible, they should not wait until
     *  the final step. */
    public void doAction(int oldStep) {
	cat.debug("doAction " + oldStep);
	int choice = -1;
	VectorSet offs = _item.getOffenders();
	switch (oldStep) {
	case 1:
	    if (_step1 != null) choice = _step1.getSelectedIndex();
	    if (choice == -1) {
		throw new Error("nothing selected, should not get here");
	    }
	    _selectedCls = /*(MClassifier)*/ offs.elementAt(choice);
	    break;
	    ////////////////
	case 2:
	    if (_step2 != null) choice = _step2.getSelectedIndex();
	    if (choice == -1) {
		throw new Error("nothing selected, should not get here");
	    }
	    Object ae = null;
	    Iterator iter = ModelFacade.getAssociationEnds(_selectedCls).iterator();
	    for (int n = 0; n <= choice; n++)
		ae = /*(MAssociationEnd)*/ iter.next();
	    _selectedAsc = ModelFacade.getAssociation(ae);
	    break;
	    ////////////////
	case 3:
	    if (_selectedAsc != null) {
		List conns = new ArrayList(ModelFacade.getConnections(_selectedAsc));
		Object ae0 = /*(MAssociationEnd)*/ conns.get(0);
		Object ae1 = /*(MAssociationEnd)*/ conns.get(1);
		try {
		    ModelFacade.setAggregation(ae0, ModelFacade.NONE_AGGREGATIONKIND);
		    ModelFacade.setAggregation(ae1, ModelFacade.NONE_AGGREGATIONKIND);
		}
		catch (Exception pve) {
		    cat.error("could not set aggregation", pve);
		}
	    }
	    break;
	}
    }
 
    public boolean canGoNext() { return canFinish(); }

    public boolean canFinish() {
	if (!super.canFinish()) return false;
	if (_step == 0) return true;
	if (_step == 1 && _step1 != null && _step1.getSelectedIndex() != -1)
	    return true;
	if (_step == 2 && _step2 != null && _step2.getSelectedIndex() != -1)
	    return true;
	return false;
    }
 
} /* end class WizBreakCircularComp */