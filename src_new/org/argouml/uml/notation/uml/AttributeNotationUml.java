// $Id: AttributeNotationUml.java,v 1.3 2006/04/29 21:50:32 mvw Exp $
// Copyright (c) 2005-2006 The Regents of the University of California. All
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

package org.argouml.uml.notation.uml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.application.api.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.notation.Notation;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.notation.AttributeNotation;
import org.argouml.util.MyTokenizer;

/**
 * The notation for an attribute for UML.
 * 
 * @author mvw@tigris.org
 */
public class AttributeNotationUml extends AttributeNotation {

    /**
     * The standard error etc. logger
     */
    private static final Logger LOG = 
        Logger.getLogger(AttributeNotationUml.class);

    /**
     * The constructor.
     *
     * @param attribute the attribute that is represented
     */
    public AttributeNotationUml(Object attribute) {
        super(attribute);
    }

    /**
     * @see org.argouml.notation.NotationProvider4#parse(java.lang.String)
     */
    public String parse(String text) {
        try {
            parseAttributeFig(Model.getFacade().getOwner(myAttribute), 
                    myAttribute, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.attribute";
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
     * Parse a string representing one ore more ';' separated attributes. The
     * case that a String or char contains a ';' (e.g. in an initializer) is
     * handled, but not other occurences of ';'.
     *
     * @param classifier  Classifier The classifier the attribute(s) belong to
     * @param attribute   Attribute The attribute on which the editing happened
     * @param text The string to parse
     * @throws ParseException for invalid input
     */
    public void parseAttributeFig(
            Object classifier,
            Object attribute,
            String text) throws ParseException {

        if (classifier == null || attribute == null) {
            return;
        }

        Project project = ProjectManager.getManager().getCurrentProject();

        ParseException pex = null;
        int start = 0;
        int end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        if (end == -1) {
            //no text? remove attr!
            project.moveToTrash(attribute);
            TargetManager.getInstance().setTarget(classifier);
            return;
        }
        String s = text.substring(start, end).trim();
        if (s.length() == 0) {
            //no non-whitechars in text? remove attr!
            project.moveToTrash(attribute);
            TargetManager.getInstance().setTarget(classifier);
            return;
        }
        parseAttribute(s, attribute);
        int i = new ArrayList(
                Model.getFacade().getFeatures(classifier)).indexOf(attribute);
        // check for more attributes (';' separated):
        start = end + 1;
        end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        while (end > start && end <= text.length()) {
            s = text.substring(start, end).trim();
            if (s.length() > 0) {
                // yes, there are more:
                Object model = project.getModel();
                Object intType = project.findType("int");
                Object newAttribute =
                    Model.getCoreFactory().buildAttribute(model, intType);
                if (newAttribute != null) {
                    try {
                        parseAttribute(s, newAttribute);
                        Model.getCoreHelper().setOwnerScope(
                                newAttribute,
                                Model.getFacade().getOwnerScope(attribute));
                        if (i != -1) {
                            Model.getCoreHelper().addFeature(
                                    classifier, ++i, newAttribute);
                        } else {
                            Model.getCoreHelper().addFeature(
                                    classifier, newAttribute);
                        }
                    } catch (ParseException ex) {
                        if (pex == null) {
                            pex = ex;
                        }
                    }
                }
            }
            start = end + 1;
            end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        }
        if (pex != null) {
            throw pex;
        }
    }

    /**
     * Parse a line on the form:<pre>
     *      visibility name [: type-expression] [= initial-value]
     * </pre>
     *
     * <ul>
     * <li>If only one of visibility and name is given, then it is assumed to
     * be the name and the visibility is left unchanged.
     * <li>Type and initial value can be given in any order.
     * <li>Properties can be given between any element on the form<pre>
     *      {[name] [= [value]] [, ...]}
     * </pre>
     * <li>Multiplicity can be given between any element except after the
     * initial-value and before the type or end (to allow java-style array
     * indexing in the initial value). It must be given on form [multiplicity]
     * with the square brackets included.
     * <li>Stereotypes can be given between any element except after the
     * initial-value and before the type or end (to allow java-style bit-shifts
     * in the initial value). It must be given on form
     * &lt;&lt;stereotype1,stereotype2,stereotype3&gt;&gt;.
     * </ul>
     *
     * The following properties are recognized to have special meaning:
     * frozen.<p>
     *
     * This syntax is compatible with the UML 1.3 spec.
     *
     * (formerly: visibility name [multiplicity] : type-expression =
     * initial-value {property-string} ) (2nd formerly: [visibility] [keywords]
     * type name [= init] [;] )
     *
     * @param text    The String to parse.
     * @param attribute The attribute to modify to comply 
     *                           with the instructions in s.
     * @throws ParseException
     *             when it detects an error in the attribute string. See also
     *             ParseError.getErrorOffset().
     */
    protected void parseAttribute(
            String text,
            Object attribute) throws ParseException {
        String multiplicity = null;
        String name = null;
        Vector properties = null;
        String stereotype = null;
        String token;
        String type = null;
        String value = null;
        String visibility = null;
        boolean hasColon = false;
        boolean hasEq = false;
        int multindex = -1;
        MyTokenizer st, mst;

        text = text.trim();
        if (text.length() > 0 
                && NotationUtilityUml.VISIBILITYCHARS.indexOf(text.charAt(0)) >= 0) {
            visibility = text.substring(0, 1);
            text = text.substring(1);
        }

        try {
            st = new MyTokenizer(text, " ,\t,<<,>>,[,],:,=,{,},\\,",
                    NotationUtilityUml.attributeCustomSep);
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (" ".equals(token) || "\t".equals(token)
                        || ",".equals(token)) {
                    if (hasEq) {
                        value += token;
                    }
                } else if ("<<".equals(token)) {
                    if (hasEq) {
                        value += token;
                    } else {
                        if (stereotype != null) {
                            throw new ParseException(
                                "Attribute cannot have "
                                + "two sets of stereotypes", 
                                st.getTokenIndex());
                        }
                        stereotype = "";
                        while (true) {
                            token = st.nextToken();
                            if (">>".equals(token)) {
                                break;
                            }
                            stereotype += token;
                        }
                    }
                } else if ("[".equals(token)) {
                    if (hasEq) {
                        value += token;
                    } else {
                        if (multiplicity != null) {
                            throw new ParseException(
                                    "Attribute cannot have two"
                                            + " multiplicities", st
                                            .getTokenIndex());
                        }
                        multiplicity = "";
                        multindex = st.getTokenIndex() + 1;
                        while (true) {
                            token = st.nextToken();
                            if ("]".equals(token)) {
                                break;
                            }
                            multiplicity += token;
                        }
                    }
                } else if ("{".equals(token)) {
                    String propname = "";
                    String propvalue = null;

                    if (properties == null) {
                        properties = new Vector();
                    }
                    while (true) {
                        token = st.nextToken();
                        if (",".equals(token) || "}".equals(token)) {
                            if (propname.length() > 0) {
                                properties.add(propname);
                                properties.add(propvalue);
                            }
                            propname = "";
                            propvalue = null;

                            if ("}".equals(token)) {
                                break;
                            }
                        } else if ("=".equals(token)) {
                            if (propvalue != null) {
                                throw new ParseException("Property " + propname
                                        + " cannot have two values", st
                                        .getTokenIndex());
                            }
                            propvalue = "";
                        } else if (propvalue == null) {
                            propname += token;
                        } else {
                            propvalue += token;
                        }
                    }
                    if (propname.length() > 0) {
                        properties.add(propname);
                        properties.add(propvalue);
                    }
                } else if (":".equals(token)) {
                    hasColon = true;
                    hasEq = false;
                } else if ("=".equals(token)) {
                    if (value != null) {
                        throw new ParseException("Attribute cannot have two "
                                + "default values", st.getTokenIndex());
                    }
                    value = "";
                    hasColon = false;
                    hasEq = true;
                } else {
                    if (hasColon) {
                        if (type != null) {
                            throw new ParseException(
                                    "Attribute cannot have two" + " types", st
                                            .getTokenIndex());
                        }
                        if (token.length() > 0
                                && (token.charAt(0) == '\"'
                                    || token.charAt(0) == '\'')) {
                            throw new ParseException("Type cannot be quoted",
                                    st.getTokenIndex());
                        }
                        if (token.length() > 0 && token.charAt(0) == '(') {
                            throw new ParseException("Type cannot be an "
                                    + "expression", st.getTokenIndex());
                        }
                        type = token;
                    } else if (hasEq) {
                        value += token;
                    } else {
                        if (name != null && visibility != null) {
                            throw new ParseException("Extra text in Attribute",
                                    st.getTokenIndex());
                        }
                        if (token.length() > 0
                                && (token.charAt(0) == '\"'
                                    || token.charAt(0) == '\'')) {
                            throw new ParseException(
                                    "Name or visibility cannot" + " be quoted",
                                    st.getTokenIndex());
                        }
                        if (token.length() > 0 && token.charAt(0) == '(') {
                            throw new ParseException(
                                    "Name or visibility cannot"
                                            + " be an expression", st
                                            .getTokenIndex());
                        }

                        if (name == null
                                && visibility == null
                                && token.length() > 1
                                && NotationUtilityUml.VISIBILITYCHARS.indexOf(token.charAt(0))
                                                        >= 0) {
                            visibility = token.substring(0, 1);
                            token = token.substring(1);
                        }

                        if (name != null) {
                            visibility = name;
                            name = token;
                        } else {
                            name = token;
                        }
                    }
                }
            }
        } catch (NoSuchElementException nsee) {
            throw new ParseException(
                    "Unexpected end of attribute", text.length());
        } catch (ParseException pre) {
            throw pre;
        }

        LOG.debug("ParseAttribute [name: " + name + " visibility: "
                + visibility + " type: " + type + " value: " + value
                + " stereo: " + stereotype + " mult: " + multiplicity);

        if (properties != null) {
            for (int i = 0; i + 1 < properties.size(); i += 2) {
                LOG.debug("\tProperty [name: " + properties.get(i) + " = "
                        + properties.get(i + 1) + "]");
            }
        }

        if (visibility != null) {
            Model.getCoreHelper().setVisibility(attribute,
                    NotationUtilityUml.getVisibility(visibility.trim()));
        }

        if (name != null) {
            Model.getCoreHelper().setName(attribute, name.trim());
        } else if (Model.getFacade().getName(attribute) == null
                || "".equals(Model.getFacade().getName(attribute))) {
            Model.getCoreHelper().setName(attribute, "anonymous");
        }

        if (type != null) {
            Object ow = Model.getFacade().getOwner(attribute);
            Object ns = null;
            if (ow != null && Model.getFacade().getNamespace(ow) != null) {
                ns = Model.getFacade().getNamespace(ow);
            } else {
                ns = Model.getFacade().getModel(attribute);
            }
            Model.getCoreHelper().setType(attribute, 
                    NotationUtilityUml.getType(type.trim(), ns));
        }

        if (value != null) {
            Object initExpr = Model.getDataTypesFactory().createExpression(
                Notation.getConfigueredNotation().toString(), value.trim());
            Model.getCoreHelper().setInitialValue(attribute, initExpr);
        }

        if (multiplicity != null) {
            try {
                Model.getCoreHelper().setMultiplicity(
                        attribute,
                        Model.getDataTypesFactory()
                                .createMultiplicity(multiplicity.trim()));
            } catch (IllegalArgumentException iae) {
                throw new ParseException("Bad multiplicity (" + iae + ")",
                        multindex);
            }
        }

        if (properties != null) {
            NotationUtilityUml.setProperties(attribute, properties,
                    NotationUtilityUml.attributeSpecialStrings);
        }

        NotationUtilityUml.dealWithStereotypes(attribute, stereotype, true);
    }


    /**
     * @see org.argouml.notation.NotationProvider4#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.attribute";
    }

    /**
     * Generates a string representation for the provided
     * attribute. The string representation will be of the form:
     *          visibility name [multiplicity] : type-expression =
     *                          initial-value {property-string}
     * Depending on settings in Notation, visibility, multiplicity,
     * type-expression, initial value and properties are shown/not shown.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String visibility = NotationUtilityUml.generateVisibility(myAttribute);
        // generateStereotype accepts a collection, despite its name
        String stereo = NotationUtilityUml.generateStereotype(
                Model.getFacade().getStereotypes(myAttribute));
        String name = Model.getFacade().getName(myAttribute);
        String multiplicity = generateMultiplicity(
                Model.getFacade().getMultiplicity(myAttribute));
        String type = ""; // fix for loading bad projects
        if (Model.getFacade().getType(myAttribute) != null) {
            type = Model.getFacade().getName(
                    Model.getFacade().getType(myAttribute));
        }
        String initialValue = "";
        if (Model.getFacade().getInitialValue(myAttribute) != null) {
            initialValue =
                (String) Model.getFacade().getBody(
                        Model.getFacade().getInitialValue(myAttribute));
        }
        String changeableKind = "";
        if (Model.getFacade().getChangeability(myAttribute) != null) {
            if (Model.getChangeableKind().getFrozen().equals(
                    Model.getFacade().getChangeability(myAttribute))) {
                changeableKind = "frozen";
            } else if (Model.getChangeableKind().getAddOnly().equals(
                    Model.getFacade().getChangeability(myAttribute))) {
                changeableKind = "addOnly";
            }
        }
        StringBuffer properties = new StringBuffer();
        if (changeableKind.length() > 0) {
            properties.append("{ ").append(changeableKind).append(" }");
        }

        StringBuffer sb = new StringBuffer(20);
        if ((stereo != null) && (stereo.length() > 0)) {
            sb.append(stereo).append(" ");
        }
        if ((visibility != null)
            && (visibility.length() > 0)
            && Configuration.getBoolean(Notation.KEY_SHOW_VISIBILITY)) {
            sb.append(visibility);
        }
        if ((name != null) && (name.length() > 0)) {
            sb.append(name).append(" ");
        }
        if ((multiplicity != null)
            && (multiplicity.length() > 0)
            && Configuration.getBoolean(Notation.KEY_SHOW_MULTIPLICITY)) {
            sb.append("[").append(multiplicity).append("]").append(" ");
        }
        if ((type != null) && (type.length() > 0)
            /* The "show types" defaults to TRUE, to stay compatible with older
             * ArgoUML versions that did not have this setting: */
            && Configuration.getBoolean(Notation.KEY_SHOW_TYPES, true)) {
            sb.append(": ").append(type).append(" ");
        }
        if ((initialValue != null)
            && (initialValue.length() > 0)
            && Configuration.getBoolean(Notation.KEY_SHOW_INITIAL_VALUE)) {
            sb.append(" = ").append(initialValue).append(" ");
        }
        if ((properties.length() > 0)
            && Configuration.getBoolean(Notation.KEY_SHOW_PROPERTIES)) {
            sb.append(properties);
        }
        return sb.toString().trim();
    }
    
    private static String generateMultiplicity(Object m) {
        if (m == null || "1".equals(Model.getFacade().toString(m))) {
            return "";
        }
        return Model.getFacade().toString(m);
    }

}
