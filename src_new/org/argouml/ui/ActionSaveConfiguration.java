// $Id: ActionSaveConfiguration.java,v 1.12 2004/07/17 16:52:03 linus Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.ui;
 
import java.awt.event.ActionEvent;
import org.argouml.i18n.Translator;
import org.argouml.application.api.Configuration;
import org.argouml.uml.ui.UMLAction;

/**
 * Action for handling Argo configuration save
 *
 * @author Thierry Lach
 * @since 0.9.4
 */
public class ActionSaveConfiguration extends UMLAction {
    /**
     * One and only instance.
     *
     * @deprecated by Linus Tolke as of 0.17.1. 
     *             Create your own instance of this object instead.
     */
    public static final ActionSaveConfiguration SINGLETON =
	new ActionSaveConfiguration();

    /**
     * Constructor.
     */
    public ActionSaveConfiguration() {
        super(Translator.localize("action.save-configuration"),
	      false);
    }

    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        ProjectBrowser.getInstance().saveScreenConfiguration();
        if (!Configuration.save()) {
	    Configuration.save(true);
        }
    }
} 



