// $Id: ActionMultiplicity.java,v 1.8 2005/01/30 14:05:18 linus Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLAction;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;


/**
 * Action to set the Multiplicity.
 *
 */
public class ActionMultiplicity extends UMLAction {
    private String str = "";
    private Object/*MMultiplicity*/ mult = null;


    ////////////////////////////////////////////////////////////////
    // static variables

    // multiplicity
    private static UMLAction srcMultOne =
	new ActionMultiplicity(Model.getMultiplicities().get11(), "src");
    private static UMLAction destMultOne =
	new ActionMultiplicity(Model.getMultiplicities().get11(), "dest");

    private static UMLAction srcMultZeroToOne =
	new ActionMultiplicity(Model.getMultiplicities().get01(), "src");
    private static UMLAction destMultZeroToOne =
	new ActionMultiplicity(Model.getMultiplicities().get01(), "dest");

    private static UMLAction srcMultZeroToMany =
	new ActionMultiplicity(Model.getMultiplicities().get0N(), "src");
    private static UMLAction destMultZeroToMany =
	new ActionMultiplicity(Model.getMultiplicities().get0N(), "dest");

    private static UMLAction srcMultOneToMany =
	new ActionMultiplicity(Model.getMultiplicities().get1N(), "src");
    private static UMLAction destMultOneToMany =
	new ActionMultiplicity(Model.getMultiplicities().get1N(), "dest");


    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * The Constructor.
     *
     * @param m the multiplicity
     * @param s "src" or "dest". Anything else is interpreted as "dest".
     */
    protected ActionMultiplicity(Object/*MMultiplicity*/ m, String s) {
	//super(m.getLower() + ".." + m.getUpper(), NO_ICON);
	super(m.toString(), true, NO_ICON);
	str = s;
	mult = m;
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
	Vector sels = Globals.curEditor().getSelectionManager().selections();
	if (sels.size() == 1) {
	    Selection sel = (Selection) sels.firstElement();
	    Fig f = sel.getContent();
	    Object owner = ((FigEdgeModelElement) f).getOwner();
	    Collection ascEnds = ModelFacade.getConnections(owner);
            Iterator iter = ascEnds.iterator();
	    Object ascEnd = null;
	    if (str.equals("src")) {
		ascEnd = iter.next();
            } else {
                while (iter.hasNext()) {
                    ascEnd = iter.next();
                }
            }
	    Model.getCoreHelper().setMultiplicity(ascEnd, mult);
	}
    }

    /**
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     */
    public boolean shouldBeEnabled() {
	return true;
    }


    /**
     * @return Returns the srcMultOne.
     */
    public static UMLAction getSrcMultOne() {
        return srcMultOne;
    }


    /**
     * @return Returns the destMultOne.
     */
    public static UMLAction getDestMultOne() {
        return destMultOne;
    }


    /**
     * @return Returns the srcMultZeroToOne.
     */
    public static UMLAction getSrcMultZeroToOne() {
        return srcMultZeroToOne;
    }


    /**
     * @return Returns the destMultZeroToOne.
     */
    public static UMLAction getDestMultZeroToOne() {
        return destMultZeroToOne;
    }


    /**
     * @return Returns the srcMultZeroToMany.
     */
    public static UMLAction getSrcMultZeroToMany() {
        return srcMultZeroToMany;
    }


    /**
     * @return Returns the destMultZeroToMany.
     */
    public static UMLAction getDestMultZeroToMany() {
        return destMultZeroToMany;
    }


    /**
     * @return Returns the srcMultOneToMany.
     */
    public static UMLAction getSrcMultOneToMany() {
        return srcMultOneToMany;
    }


    /**
     * @return Returns the destMultOneToMany.
     */
    public static UMLAction getDestMultOneToMany() {
        return destMultOneToMany;
    }
} /* end class ActionSrcMultOneToMany */
