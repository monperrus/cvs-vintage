// $Id: Notation.java,v 1.10 2006/05/26 16:05:40 linus Exp $
// Copyright (c) 1996-2006 The Regents of the University of California. All
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

package org.argouml.notation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.application.api.Configuration;
import org.argouml.application.api.ConfigurationKey;
import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoNotationEvent;
import org.argouml.notation.ui.SettingsTabNotation;

/**
 * Provides centralized methods dealing with notation.
 *
 * @stereotype singleton
 * @author Thierry Lach
 * @since 0.9.4
 */
public final class Notation implements PropertyChangeListener {

    /**
     * Define a static log4j category variable for ArgoUML notation.
     */
    private static final Logger LOG =
        Logger.getLogger(Notation.class);

    /**
     * The name of the default ArgoUML notation.  This notation is
     * part of ArgoUML core distribution.
     */
    private static NotationName notationArgo =
        makeNotation(
            "UML",
            "1.4",
            Argo.lookupIconResource("UmlNotation"));

    /*
     * Remark:
     * There is also a java-like notation, which is also
     * part of ArgoUML core distribution.
     */

    /**
     * The configuration key for the preferred notation.
     */
    public static final ConfigurationKey KEY_DEFAULT_NOTATION =
        Configuration.makeKey("notation", "default");

    /**
     * The configuration key that indicates whether to show stereotypes
     * in the navigation panel.
     */
    public static final ConfigurationKey KEY_SHOW_STEREOTYPES =
        Configuration.makeKey("notation", "navigation", "show", "stereotypes");

    /**
     * The configuration key that indicates whether to use guillemots
     * or greater/lessthan characters in stereotypes.
     */
    public static final ConfigurationKey KEY_USE_GUILLEMOTS =
        Configuration.makeKey("notation", "guillemots");

    /**
     * Indicates if the user only wants to see UML notation.
     */
    public static final ConfigurationKey KEY_UML_NOTATION_ONLY =
        Configuration.makeKey("notation", "only", "uml");

    /**
     * Indicates if the user wants to see visibility signs (public,
     * private, protected or # + -).
     */
    public static final ConfigurationKey KEY_SHOW_VISIBILITY =
        Configuration.makeKey("notation", "show", "visibility");

    /**
     * Indicates if the user wants to see multiplicity in attributes
     * and classes.
     */
    public static final ConfigurationKey KEY_SHOW_MULTIPLICITY =
        Configuration.makeKey("notation", "show", "multiplicity");

    /**
     * Indicates if the user wants to see the initial value.
     */
    public static final ConfigurationKey KEY_SHOW_INITIAL_VALUE =
        Configuration.makeKey("notation", "show", "initialvalue");

    /**
     * Indicates if the user wants to see the properties (everything
     * between braces), that is for example the concurrency.
     */
    public static final ConfigurationKey KEY_SHOW_PROPERTIES =
        Configuration.makeKey("notation", "show", "properties");

    /**
     * Indicates if the user wants to see the types and parameters
     * of attributes and operations.
     */
    public static final ConfigurationKey KEY_SHOW_TYPES =
        Configuration.makeKey("notation", "show", "types");

    /**
     * Default value for the shadow size of classes, interfaces etc.
     */
    public static final ConfigurationKey KEY_DEFAULT_SHADOW_WIDTH =
        Configuration.makeKey("notation", "default", "shadow-width");

    /**
     * The instance.
     */
    private static final Notation SINGLETON = new Notation();


    /**
     * The constructor.
     */
    private Notation() {
        Configuration.addListener(KEY_USE_GUILLEMOTS, this);
        Configuration.addListener(KEY_DEFAULT_NOTATION, this);
        Configuration.addListener(KEY_UML_NOTATION_ONLY, this);
        Configuration.addListener(KEY_SHOW_TYPES, this);
        Configuration.addListener(KEY_SHOW_MULTIPLICITY, this);
        Configuration.addListener(KEY_SHOW_PROPERTIES, this);
        Configuration.addListener(KEY_SHOW_VISIBILITY, this);
        Configuration.addListener(KEY_SHOW_INITIAL_VALUE, this);
        new SettingsTabNotation();
    }

    /**
     * Remove the notation change listener.
     * <code>finalize</code> should never happen, but play it safe.
     *
     * TODO: Explain why we don't call super.finalize()!
     */
    protected void finalize() {
        Configuration.removeListener(KEY_DEFAULT_NOTATION, this);
        Configuration.removeListener(KEY_USE_GUILLEMOTS, this);
        Configuration.removeListener(KEY_UML_NOTATION_ONLY, this);
        Configuration.removeListener(KEY_SHOW_TYPES, this);
        Configuration.removeListener(KEY_SHOW_MULTIPLICITY, this);
        Configuration.removeListener(KEY_SHOW_PROPERTIES, this);
        Configuration.removeListener(KEY_SHOW_VISIBILITY, this);
        Configuration.removeListener(KEY_SHOW_INITIAL_VALUE, this);
    }

    /**
     * @param n the NotationName that will become default
     */
    public static void setDefaultNotation(NotationName n) {
        LOG.info("default notation set to " + n.getConfigurationValue());
        Configuration.setString(
            KEY_DEFAULT_NOTATION,
            n.getConfigurationValue());
    }

    /**
     * Convert a String into a NotationName.
     * @param s the String
     * @return the matching Notationname
     */
    public static NotationName findNotation(String s) {
        return NotationNameImpl.findNotation(s);
    }

    /**
     * Returns the Notation as set in the menu.
     *
     * @return the default NotationName
     */
    public static NotationName getConfigueredNotation() {
        NotationName n =
            NotationNameImpl.findNotation(
                Configuration.getString(
                    KEY_DEFAULT_NOTATION,
                    notationArgo.getConfigurationValue()));
        // This is needed for the case when the default notation is
        // not loaded at this point.
        if (n == null) {
            n = NotationNameImpl.findNotation("UML 1.4");
	}
        LOG.debug("default notation is " + n.getConfigurationValue());
        return n;
    }

    ////////////////////////////////////////////////////////////////
    // class accessors

    ////////////////////////////////////////////////////////////////
    // static accessors

    /**
     * @return the singleton
     */
    public static Notation getInstance() {
        return SINGLETON;
    }

    /**
     * @param context the notation context
     * @return the notation name
     */
    public static NotationName getNotation(NotationContext context) {
        // UML is the default language
        if (Configuration.getBoolean(Notation.KEY_UML_NOTATION_ONLY, false)) {
            return notationArgo;
        }
        // Otherwise we use the given context
        return context.getContextNotation();
    }

    /**
     * Called after the notation default property gets changed.
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent pce) {
        LOG.info(
            "Notation change:"
                + pce.getOldValue()
                + " to "
                + pce.getNewValue());
        ArgoEventPump.fireEvent(
            new ArgoNotationEvent(ArgoEventTypes.NOTATION_CHANGED, pce));
    }

    ////////////////////////////////////////////////////////////////
    // Static workers for dealing with notation names.

    /**
     * Get list of available notations.
     *
     * @return list of available notations
     */
    public static ArrayList getAvailableNotations() {
        return NotationNameImpl.getAvailableNotations();
    }

    /**
     * Create a versioned notation name with an icon.
     *
     * @param k1 the name (e.g. UML)
     * @param k2 the version (e.g. 1.3)
     * @param icon the icon
     * @return the notation name
     */
    public static NotationName makeNotation(String k1, String k2, Icon icon) {
        NotationName nn = NotationNameImpl.makeNotation(k1, k2, icon);
        return nn;
    }

    /**
     * @return <code>true</code> if guillemots (&laquo; and &raquo;) are used
     * instead of &lt;&lt; and &gt;&gt;.
     */
    public static boolean getUseGuillemots() {
        return Configuration.getBoolean(KEY_USE_GUILLEMOTS, false);
    }

    /**
     * @param useGuillemots <code>true</code> if guillemots (&laquo;
     * and &raquo;) shall be used instead of &lt;&lt; and &gt;&gt;.
     */
    public static void setUseGuillemots(boolean useGuillemots) {
        Configuration.setBoolean(KEY_USE_GUILLEMOTS, useGuillemots);
    }

    /**
     * Get the default width for Fig shadows.
     *
     * @return the default width for Fig shadows
     */
    public static int getDefaultShadowWidth() {
        return Configuration.getInteger(KEY_DEFAULT_SHADOW_WIDTH, 1);
    }

    /**
     * Set the default width for Fig Shadow.
     *
     * @param width    the Fig shadow width
     */
    public static void setDefaultShadowWidth(int width) {
        Configuration.setInteger(KEY_DEFAULT_SHADOW_WIDTH, width);
    }

} // END NOTATION
