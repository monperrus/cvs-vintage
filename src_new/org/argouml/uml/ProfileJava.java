// $Id: ProfileJava.java,v 1.26 2004/12/11 15:54:09 bobtarling Exp $
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

package org.argouml.uml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.XmiReader;
import org.xml.sax.InputSource;

/**
 *   This class implements the abstract class Profile for use in modelling
 *   Java language projects.  Eventually, this class may be replaced by
 *   a configurable profile.
 * 
 * TODO: (MVW) I see only little Java specific stuff here. 
 * Most of this should be moved to a ProfileUML.java file, which 
 * should be used by default.
 * 
 * TODO: (MVW) Document the use of "argo.defaultModel" in 
 * the argo.user.properties file.
 *
 *   @author Curt Arnold
 */
public class ProfileJava extends Profile {
    
    private static final Logger LOG = Logger.getLogger(ProfileJava.class);
    
    private static ProfileJava instance = null;
    
    /**
     * @return the instance of this class
     */
    public static ProfileJava getInstance() {
	if (instance == null)
	    instance = new ProfileJava();
	return instance;
    }

    private Object/*MModel*/ defaultModel;

    private ProfileJava() {
	getProfileModel();
    }

    /**
     * @see org.argouml.uml.Profile#formatElement(java.lang.Object, 
     * java.lang.Object)
     */
    public String formatElement(Object/*MModelElement*/ element,
				Object namespace) {
	String value = null;
	if (element == null) {
	    value = "";
	} else {
	    Object elementNs = ModelFacade.getNamespace(element);
	    //
	    //   if element is an AssociationEnd use
	    //      the namespace of containing association
	    //
	    if (ModelFacade.isAAssociationEnd(element)) {
		Object assoc = ModelFacade.getAssociation(element);
		if (assoc != null) {
		    elementNs = ModelFacade.getNamespace(assoc);
		}
	    }
	    if (elementNs == namespace) {
		value = ModelFacade.getName(element);
		if (value == null || value.length() == 0) {
		    value = defaultName(element, namespace);
		}
	    } else {
		StringBuffer buffer = new StringBuffer();
		String pathSep = getPathSeparator();
		buildPath(buffer, element, pathSep);
		value = buffer.toString();
	    }
	}
	return value;
    }

    /**
     * @param assocEnd the given association end name
     * @param namespace the namespace
     * @return the default name for the given associationend
     */
    protected String defaultAssocEndName(Object/*MAssociationEnd*/ assocEnd,
					 Object namespace) {
	String name = null;
	Object/*MClassifier*/ type = ModelFacade.getType(assocEnd);
	if (type != null) {
	    name = formatElement(type, namespace);
	} else {
	    name = "unknown type";
	}
	Object/*MMultiplicity*/ mult = ModelFacade.getMultiplicity(assocEnd);
	if (mult != null) {
	    StringBuffer buf = new StringBuffer(name);
	    buf.append("[");
	    buf.append(Integer.toString(ModelFacade.getLower(mult)));
	    buf.append("..");
	    int upper = ModelFacade.getUpper(mult);
	    if (upper >= 0) {
		buf.append(Integer.toString(upper));
	    } else {
		buf.append("*");
	    }
	    buf.append("]");
	    name = buf.toString();
	}
	return name;
    }

    /**
     * This function creates a default association name from its ends.
     * 
     * @param assoc the given association
     * @param ns the namespace
     * @return the default association name
     */
    protected String defaultAssocName(Object/*MAssociation*/ assoc,
				      Object ns) {
	StringBuffer buf = new StringBuffer();
	Iterator iter = ModelFacade.getConnections(assoc).iterator();
	for (int i = 0; iter.hasNext(); i++) {
	    if (i != 0) {
		buf.append("-");
	    }
	    buf.append(defaultAssocEndName(iter.next(), ns));
	}
	return buf.toString();
    }

    /**
     * @param gen the given Generalization
     * @param namespace the namespace
     * @return the default generalization name
     */
    protected String defaultGeneralizationName(Object/*MGeneralization*/ gen,
					       Object namespace) {
	Object/*MGeneralizableElement*/ child = ModelFacade.getChild(gen);
	Object/*MGeneralizableElement*/ parent = ModelFacade.getParent(gen);
	StringBuffer buf = new StringBuffer();
	buf.append(formatElement(child, namespace));
	buf.append(" extends ");
	buf.append(formatElement(parent, namespace));
	return buf.toString();
    }

    /**
     * @param element the given modelelement
     * @param namespace the namespace
     * @return a default name for this modelelement
     */
    protected String defaultName(Object/*MModelElement*/ element,
				 Object namespace) {
	String name = null;
	if (ModelFacade.isAAssociationEnd(element)) {
	    name = defaultAssocEndName(element, namespace);
	} else {
	    if (ModelFacade.isAAssociation(element)) {
		name = defaultAssocName(element, namespace);
	    }
	    if (ModelFacade.isAGeneralization(element)) {
		name = defaultGeneralizationName(element, namespace);
	    }
	}
	if (name == null)
	    name = "anon";
	return name;
    }

    /**
     * @return the path separator (currently ".")
     */
    protected String getPathSeparator() {
	return ".";
    }

    /**
     * @param buffer (out) the buffer that will contain the path build
     * @param element the given modelelement
     * @param pathSep the path separator character(s)
     */
    private void buildPath(StringBuffer buffer,
			   Object/*MModelElement*/ element,
			   String pathSep) {
	if (element != null) {
	    Object/*MNamespace*/ parent = ModelFacade.getNamespace(element);
	    if (parent != null && parent != element) {
		buildPath(buffer, parent, pathSep);
		buffer.append(pathSep);
	    }
	    String name = ModelFacade.getName(element);
	    if (name == null || name.length() == 0) {
		name = defaultName(element, null);
	    }
	    buffer.append(name);
	}
    }

    /**
     * @return the string that separates elements
     */
    protected String getElementSeparator() {
	return ", ";
    }

    /**
     * @return the string that represents an empty collection
     */
    protected String getEmptyCollection() {
	return "[empty]";
    }

    /**
     * @see org.argouml.uml.Profile#formatCollection(java.util.Iterator, 
     * java.lang.Object)
     */
    public String formatCollection(Iterator iter, Object namespace) {
	String value = null;
	if (iter.hasNext()) {
	    StringBuffer buffer = new StringBuffer();
	    String elementSep = getElementSeparator();
	    Object obj = null;
	    for (int i = 0; iter.hasNext(); i++) {
		if (i > 0) {
		    buffer.append(elementSep);
		}
		obj = iter.next();
		if (ModelFacade.isAModelElement(obj)) {
		    buffer.append(formatElement(obj, namespace));
		} else {
		    buffer.append(obj.toString());
		}
	    }
	    value = buffer.toString();
	} else {
	    value = getEmptyCollection();
	}
	return value;
    }

    /**
     * @see org.argouml.uml.Profile#getProfileModel()
     */
    public Object/*MModel*/ getProfileModel() {
	if (defaultModel == null) {
	    defaultModel = loadProfileModel();
	}
	return defaultModel;
    }
	
    /**
     * This function loads the model object containing the default model from 
     * either property "argo.defaultModel", or "/org/argouml/default.xmi".
     * May result in null, if the files are not found.
     * 
     * @return the model object
     */
    public static Object/*MModel*/ loadProfileModel() {
	//
	//    get a file name for the default model
	//
	String defaultModelFileName =
	    System.getProperty("argo.defaultModel");

	//
	//   if the property was set
	//
	InputStream is = null;
	if (defaultModelFileName != null) {
	    //
	    //  try to find a file with that name
	    //
	    try {
		is = new FileInputStream(defaultModelFileName);
	    }
	    //
	    //   no file found, try looking in the resources
	    //
	    catch (FileNotFoundException ex) {
		is = new Object().getClass()
		    .getResourceAsStream(defaultModelFileName);
		if (is == null) {
		    LOG.error(
			      "Value of property argo.defaultModel ("
			      + defaultModelFileName
			      + ") did not correspond to an available file.\n");
		}
	    }
	}

	//
	//   either no name specified or file not found
	//        load the default
	if (is == null) {
	    defaultModelFileName = "/org/argouml/default.xmi";

	    // Notice that the class that we run getClass() in needs to be
	    // in the same ClassLoader that the default.xmi.
	    // If we run using Java Web Start then we have every ArgoUML
	    // file in the same jar (i.e. the same ClassLoader).
	    is = 
		new Object() { }
		.getClass().getResourceAsStream(defaultModelFileName);

	    if (is == null) {
		try {
		    is = 
			new FileInputStream(defaultModelFileName.substring(1));
		} catch (FileNotFoundException ex) {
		    LOG.error("Default model ("
			      + defaultModelFileName
			      + ") not found.\n", ex);
						
		}
	    }
	}

	if (is != null) {
	    try {
		XmiReader xmiReader = new XmiReader();
		//
		//   would really like to turn validation off to save
		//      a lot of scary messages
		Object/*MModel*/ model =
		    xmiReader.parseToModel(new InputSource(is));
		// 2002-07-18 Jaap Branderhorst changed the loading of
		// the projectfiles to solve hanging of argouml if a
		// project is corrupted. Issue 913 Created xmireader
		// with method getErrors to check if parsing went well
		if (xmiReader.getErrors()) {
		    throw new IOException(
					  "XMI file "
					  + defaultModelFileName
					  + " could not be parsed.");
		}

		return model;
	    } catch (Exception ex) {
		LOG.error("Error reading " + defaultModelFileName + "\n", ex);
	    }
	}

	return null;
    }
}
