// Copyright (c) 1996-2001 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.apache.log4j.Category;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.History;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.Actions;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.StatusBar;
import org.tigris.gef.util.Localizer;

public class UMLAction extends AbstractAction {
    protected static Category cat = Category.getInstance(UMLAction.class);

    public static boolean HAS_ICON = true;
    public static boolean NO_ICON = false;

    public UMLAction(String name) {
        this(name, true, HAS_ICON);
    }
    public UMLAction(String name, boolean hasIcon) {
        this(name, true, hasIcon);
    }

    public UMLAction(String name, boolean global, boolean hasIcon) {
        super(Translator.localize("CoreMenu", name));
        if (hasIcon) {
            Icon icon =
                ResourceLoaderWrapper
                    .getResourceLoaderWrapper()
                    .lookupIconResource(
                    Translator.getImageBinding(name),
                    Translator.localize("CoreMenu", name));
            if (icon != null)
                putValue(Action.SMALL_ICON, icon);
            else {
                cat.debug("icon not found: " + name);
            }
        }
        putValue(
            Action.SHORT_DESCRIPTION,
            Translator.localize("CoreMenu", name) + " ");
        if (global)
            Actions.addAction(this);
        // Jaap B. 17-6-2003 added next line to make sure every action is in the right enable condition on creation.
        setEnabled(shouldBeEnabled());
    }

    /** Perform the work the action is supposed to do. */
    public void actionPerformed(ActionEvent e) {
        cat.debug("pushed " + getValue(Action.NAME));
        StatusBar sb = ProjectBrowser.getInstance().getStatusBar();
        sb.doFakeProgress(stripJunk(getValue(Action.NAME).toString()), 100);
        History.TheHistory.addItemManipulation(
            "pushed " + getValue(Action.NAME),
            "",
            null,
            null,
            null);
        Actions.updateAllEnabled();
    }

    public void markNeedsSave() {
        Project p = ProjectManager.getManager().getCurrentProject();
        p.setNeedsSave(true);
    }

    public void updateEnabled(Object target) {
        setEnabled(shouldBeEnabled());
    }

    public void updateEnabled() {
        boolean b = shouldBeEnabled();
        setEnabled(b);
    }

    /** return true if this action should be available to the user. This
     *  method should examine the ProjectBrowser that owns it.  Sublass
     *  implementations of this method should always call
     *  super.shouldBeEnabled first. */
    public boolean shouldBeEnabled() {
        return true;
    }

    protected static String stripJunk(String s) {
        String res = "";
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c))
                res += c;
        }
        return res;
    }
    /**
     *    This function returns a localized menu shortcut key
     *    to the specified key.
     *
     */
    static final public KeyStroke getShortcut(String key) {
        return Localizer.getShortcut("CoreMenu", key);
    }

    /**
     *    This function returns a localized string corresponding
     *    to the specified key.
     *
     */
    static final public String getMnemonic(String key) {
        return Translator.localize("CoreMenu", key);
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    public boolean isEnabled() {
        if (!Actions.isGlobalAction(this)) {
            return shouldBeEnabled();
        }
        return super.isEnabled();
    }

} /* end class UMLAction */
