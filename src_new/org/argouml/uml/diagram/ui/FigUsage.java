// $Id: FigUsage.java,v 1.13 2005/10/14 16:13:42 bobtarling Exp $
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

import java.beans.PropertyChangeEvent;

import org.argouml.kernel.SingleStereotypeEnabler;
import org.argouml.language.helpers.NotationHelper;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigText;

/**
 * This class represents a Fig for a Usage.
 *
 * @author Markus Klink
 */
public class FigUsage extends FigDependency {

    /**
     * The constructor.
     *
     */
    public FigUsage() {
        super();
    }

    /**
     * The constructor.
     *
     * @param edge the owning UML element
     */
    public FigUsage(Object edge) {
        super(edge);
    }

    /**
     * The constructor.
     *
     * @param edge the owning UML element
     * @param lay the layer
     */
    public FigUsage(Object edge, Layer lay) {
        super(edge, lay);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent e) {
        super.modelChanged(e);
        
        if (SingleStereotypeEnabler.isEnabled()) {
            String stereoTypeStr = ((FigText)getStereotypeFig()).getText();
            if (stereoTypeStr == null || "".equals(stereoTypeStr)) {
                ((FigText)getStereotypeFig()).setText(
                    NotationHelper.getLeftGuillemot() + "use"
                    + NotationHelper.getRightGuillemot());
            }
        }
    }

} /* end class FigUsage */

