// $Id: PropPanelAbstraction.java,v 1.19 2004/09/12 16:47:13 mvw Exp $
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.core;

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel for a Abstraction.
 *
 */
public class PropPanelAbstraction extends PropPanelDependency {

    /**
     * The constructor.
     * 
     */
    public PropPanelAbstraction() {
        super("Abstraction", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("UMLMenu", "label.name"), 
                getNameTextField());
        // addField(Translator.localize("UMLMenu", "label.stereotype"), 
        // new UMLComboBoxNavigator(this, Translator.localize("UMLMenu", 
        // "tooltip.nav-stereo"), getStereotypeBox()));
        addField(Translator.localize("UMLMenu", "label.stereotype"), 
                getStereotypeBox());
        addField(Translator.localize("UMLMenu", "label.namespace"), 
                getNamespaceComboBox());

        addSeperator();

        addField(Translator.localize("UMLMenu", "label.suppliers"), 
                getSupplierScroll());
        addField(Translator.localize("UMLMenu", "label.clients"), 
                getClientScroll());

        // TODO: add Mapping
        new PropPanelButton(this, buttonPanel, navUpIcon, Translator.localize(
                "UMLMenu", "button.go-up"), "navigateUp", null);
        buttonPanel
        .add(new PropPanelButton2(this, new ActionRemoveFromModel()));
    }

} /* end class PropPanelAbstraction */

