// $Id: StylePanelFigRRect.java,v 1.7 2004/09/01 18:48:04 mvw Exp $
// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

import org.argouml.i18n.Translator;
import org.tigris.gef.presentation.FigRRect;

/**
 * Provide a stylepanel for rounded rectancles in order to set the rounding edge
 * level.
 *  
 */
public class StylePanelFigRRect extends StylePanelFig {

    private JLabel roundingLabel = new JLabel(Translator
            .localize("label.stylepane.rounding")
            + ": ");

    private JTextField roundingField = new JTextField();

    /**
     * construct a default panel for rounded rectancular elements.
     *  
     */
    public StylePanelFigRRect() {
        super();
        GridBagLayout gb = (GridBagLayout) getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = 0;
        c.ipady = 0;

        Document roundingDoc = roundingField.getDocument();
        roundingDoc.addDocumentListener(this);

        c.weightx = 0.0;
        c.gridx = 3;
        c.gridy = 1;
        gb.setConstraints(roundingLabel, c);
        add(roundingLabel);

        c.weightx = 1.0;
        c.gridx = 4;
        c.gridy = 1;
        gb.setConstraints(roundingField, c);
        add(roundingField);
    }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        super.refresh();
        String roundingStr = ((FigRRect) _target).getCornerRadius() + "";
        roundingField.setText(roundingStr);
    }

    /**
     * Set the corner rounding.
     */
    protected void setTargetRounding() {
        if (_target == null) return;
        String roundingStr = roundingField.getText();
        if (roundingStr.length() == 0) return;
        int r = Integer.parseInt(roundingStr);
        ((FigRRect) _target).setCornerRadius(r);
        _target.endTrans();
    }

    /**
     * react to changes in the rounding field text box.
     * 
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e) {
        Document roundingDoc = roundingField.getDocument();
        if (e.getDocument() == roundingDoc) setTargetRounding();
        super.insertUpdate(e);
    }

} /* end class StylePanelFigRRect */
