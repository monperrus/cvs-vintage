// $Id: WizManyNames.java,v 1.7 2003/09/04 20:11:42 thierrylach Exp $
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



// File: WizManyNames.java
// Classes: WizManyNames
// Original Author: jrobbins@ics.uci.edu
// $Id: WizManyNames.java,v 1.7 2003/09/04 20:11:42 thierrylach Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Vector;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

import org.argouml.application.api.Argo;
import org.argouml.cognitive.ui.WizStepManyTextFields;
import org.argouml.kernel.Wizard;
import ru.novosoft.uml.foundation.core.MModelElement;


/** A non-modal wizard to help the user change the name of a
 *  MModelElement to a better name. */

public class WizManyNames extends Wizard {
    protected static Logger cat = Logger.getLogger(WizManyNames.class);
					      
    protected String _instructions =
	"Please change the name of the offending model element.";
    protected String _label = Argo.localize("UMLMenu", "label.name");
    public Vector _mes = null;
							  
    protected WizStepManyTextFields _step1 = null;
							      
    public WizManyNames() { }
								  
    public int getNumSteps() { return 1; }
								      
    public void setMEs(Vector mes) { _mes = mes; }
									  
    public void setInstructions(String s) { _instructions = s; }
									      
    /** Create a new panel for the given step.  */
    public JPanel makePanel(int newStep) {
	switch (newStep) {
	case 1:
	    if (_step1 == null) {
		Vector names = new Vector();
		int size = _mes.size();
		for (int i = 0; i < size; i++) {
		    MModelElement me = (MModelElement) _mes.elementAt(i);
		    names.addElement(me.getName());
		}
		_step1 = new WizStepManyTextFields(this, _instructions, names);
	    }
	    return _step1;
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
	switch (oldStep) {
	case 1:
	    Vector newNames = null;
	    if (_step1 != null) newNames = _step1.getStrings();
	    try {
		int size = _mes.size();
		for (int i = 0; i < size; i++) {
		    MModelElement me = (MModelElement) _mes.elementAt(i);
		    me.setName((String) newNames.elementAt(i));
		}
	    }
	    catch (Exception pve) {
		cat.error("could not set name", pve);
	    }
	}
    }
} /* end class WizManyNames */
