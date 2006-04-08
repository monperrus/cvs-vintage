// $Id: PropPanelActor.java,v 1.57 2006/04/08 22:36:06 tfmorris Exp $
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

package org.argouml.uml.ui.behavior.use_cases;

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.foundation.core.PropPanelClassifier;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;


/**
 * Builds the property panel for a actor.<p>
 *
 * @author jrobbins
 */

public class PropPanelActor extends PropPanelClassifier {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = 7368183497864490115L;

    /**
     * Construct a new property panel for an Actor.
     */
    public PropPanelActor() {
    	super("Actor", lookupIcon("Actor"),
                ConfigLoader.getTabPropsOrientation());


    	addField(Translator.localize("label.name"),
                getNameTextField());
    	addField(Translator.localize("label.namespace"),
                getNamespaceSelector());

        add(getModifiersPanel());

    	addSeparator();

    	addField(Translator.localize("label.generalizations"),
                getGeneralizationScroll());
    	addField(Translator.localize("label.specializations"),
                getSpecializationScroll());

    	addSeparator();

    	addField(Translator.localize("label.association-ends"),
            getAssociationEndScroll());

        // The toolbar buttons that go at the top:
        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNewActor());
        addAction(getActionNewReception());
        addAction(new ActionNewStereotype());
        addAction(new ActionDeleteSingleModelElement());
    }

} /* end class PropActor */
