// $Id: StylePanelFigUseCase.java,v 1.11 2005/05/13 19:43:38 mvw Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.uml.diagram.use_case.ui;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.argouml.kernel.ProjectManager;
import org.argouml.ui.StylePanelFigNodeModelElement;

/**
 * A class to provide a style panel for use cases.<p>
 *
 * This adds a check box to control the display of he extension point
 * compartment.
 *
 * @author Jeremy Bennett
 */
public class StylePanelFigUseCase extends StylePanelFigNodeModelElement {

    /**
     * The check box for extension points.
     */
    private JCheckBox epCheckBox = new JCheckBox("Extension Points");

    /**
     * The label alongside the check box for extension points.
     */
    private JLabel displayLabel = new JLabel("Display: ");

    /**
     * Flag to indicate that a refresh is going on.
     */
    private boolean refreshTransaction = false;

    /**
     * Build a style panel. Just layout the relevant boxes.
     */
    public StylePanelFigUseCase() {

        // Invoke the parent constructor first
        super();

        // Create the check box, and then add it.

        JPanel pane = new JPanel();

        pane.setLayout(new FlowLayout(FlowLayout.LEFT));
        pane.add(epCheckBox);

        displayLabel.setLabelFor(pane);
        add(pane, 0);
        add(displayLabel, 0);
        
        // By default we don't show the attribute check box. Mark this object
        // as a listener for the check box.

        epCheckBox.setSelected(false);
        epCheckBox.addItemListener(this);
    }

    /**
     * Refresh the display. This means setting the check box from the target use
     * case fig.
     */
    public void refresh() {

        refreshTransaction = true;

        // Invoke the parent refresh first

        super.refresh();

        FigUseCase target = (FigUseCase) getTarget();

        epCheckBox.setSelected(target.isExtensionPointVisible());

        refreshTransaction = false;
    }

    /**
     * Something has changed, check if its the check box.<p>
     *
     * @param e
     *            The event that triggeed us.
     */
    public void itemStateChanged(ItemEvent e) {
        if (!refreshTransaction) {
            Object src = e.getSource();

            // If it was the check box, reset it, otherwise invoke the parent.

            if (src == epCheckBox) {
                FigUseCase target = (FigUseCase) getTarget();

                target.setExtensionPointVisible(epCheckBox.isSelected());

                ProjectManager.getManager().setNeedsSave(true);
            } else {
                super.itemStateChanged(e);
            }
        }
    }

} /* end class StylePanelFigUseCase */
