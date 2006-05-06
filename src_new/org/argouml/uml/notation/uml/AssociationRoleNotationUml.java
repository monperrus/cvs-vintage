// $Id: AssociationRoleNotationUml.java,v 1.1 2006/05/06 12:23:32 mvw Exp $
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.uml.notation.uml;

import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.notation.NotationHelper;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.notation.AssociationRoleNotation;
import org.argouml.util.MyTokenizer;

/**
 * The UML notation for an association role.
 * 
 * @author michiel
 */
public class AssociationRoleNotationUml extends AssociationRoleNotation {

    /**
     * The constructor.
     *
     * @param assocEnd teh UML associationEnd
     */
    public AssociationRoleNotationUml(Object assocEnd) {
        super(assocEnd);
    }

    /**
     * @see org.argouml.notation.NotationProvider4#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-association-role";
    }

    /**
     * @see org.argouml.notation.NotationProvider4#parse(java.lang.String)
     */
    public String parse(String text) {
        try {
            parseRole(myAssociationEnd, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.association-role";
            Object[] args = {
                pe.getLocalizedMessage(),
                new Integer(pe.getErrorOffset()),
            };
            ProjectBrowser.getInstance().getStatusBar().showStatus(
                Translator.messageFormat(msg, args));
        }
        return toString();
    }

    /**
     * @param role   The AssociationRole <em>text</em> describes.
     * @param text A String on the above format.
     * @throws ParseException
     *             when it detects an error in the role string. See also
     *             ParseError.getErrorOffset().
     */
    protected void parseRole(Object role, String text)
        throws ParseException {
        MyTokenizer st;

        String name = null;
        String stereotype = null;
        String token;

        try {
            st = new MyTokenizer(text, "<<,\u00AB,\u00BB,>>");
            while (st.hasMoreTokens()) {
                token = st.nextToken();

                if ("<<".equals(token) || "\u00AB".equals(token)) {
                    if (stereotype != null) {
                        throw new ParseException("Element cannot have "
                                + "two groups of stereotypes",
                                st.getTokenIndex());
                    }

                    stereotype = "";
                    while (true) {
                        token = st.nextToken();
                        if (">>".equals(token) || "\u00BB".equals(token)) {
                            break;
                        }
                        stereotype += token;
                    }
                } else {
                    if (name != null) {
                        throw new ParseException("Element cannot have "
                                + "two word names or qualifiers", st
                                .getTokenIndex());
                    }
                    name = token;
                }
            }
        } catch (NoSuchElementException nsee) {
            throw new ParseException("Unexpected end of element",
                    text.length());
        } catch (ParseException pre) {
            throw pre;
        }

        if (name != null) {
            name = name.trim();
        }

        if (name != null && name.startsWith("+")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(role,
                            Model.getVisibilityKind().getPublic());
        }
        if (name != null && name.startsWith("-")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(role,
                            Model.getVisibilityKind().getPrivate());
        }
        if (name != null && name.startsWith("#")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(role,
                            Model.getVisibilityKind().getProtected());
        }
        if (name != null && name.startsWith("~")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(role,
                            Model.getVisibilityKind().getPackage());
        }
        if (name != null) {
            Model.getCoreHelper().setName(role, name);
        }

        NotationUtilityUml.dealWithStereotypes(role, stereotype, true);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String name = Model.getFacade().getName(myAssociationEnd);
        if (name == null) {
            name = "";
        }

        Object visi = Model.getFacade().getVisibility(myAssociationEnd);
        String visibility = "";
        if (visi != null) {
            visibility = NotationUtilityUml.generateVisibility(visi);
        }
        if (name.length() < 1) {
            visibility = "";
            //this is the temporary solution for issue 1011
        }

        String stereoString = 
            NotationUtilityUml.generateStereotype(myAssociationEnd);

        return stereoString + visibility + name;
    }
    
}
