// $Id: ModelMemberFilePersister.java,v 1.2 2004/10/13 14:50:55 bobtarling Exp $
// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.xml.argo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.model.uml.UmlHelper;
import org.argouml.xml.xmi.XMIReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The file persister for the UML model.
 * @author Bob Tarling
 */
public class ModelMemberFilePersister extends MemberFilePersister {
    
    /** logger */
    private static final Logger LOG =
        Logger.getLogger(ModelMemberFilePersister.class);
    
    private InputStream inputStream;
    
    private Project project;

    /**
     * Construct a new ModelMemberFilePersister.
     * @param url the url from which to load the model.
     * @param theProject the project to populate.
     * @throws SAXException on any parsing error.
     */
    public ModelMemberFilePersister(URL url, Project theProject)
        throws SAXException {
        
        this.project = theProject;
        try {
            inputStream = openStreamAtXmi(url);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    /**
     * Opens the input stream of a URL and positions
     * it at the start of the XMI.
     * This is a first draft of this method.
     * Work still in progress (Bob Tarling).
     */
    private InputStream openStreamAtXmi(URL theUrl) throws IOException {
        XmlInputStream is = new XmlInputStream(theUrl.openStream(), "XMI");
        return is;
    }

    /**
     * Loads a model (XMI only) from an input source. BE ADVISED this
     * method has a side effect. It sets _UUIDREFS to the model.
     * 
     * If there is a problem with the xmi file, an error is set in the
     * getLastLoadStatus() field. This needs to be examined by the
     * calling function.
     *
     * @throws SAXException If the parser template is syntactically incorrect. 
     */
    public void load(Map AttributesMap) throws SAXException {
                
        InputSource source = new InputSource(inputStream);
        Object mmodel = null;

        // 2002-07-18
        // Jaap Branderhorst
        // changed the loading of the projectfiles to solve hanging 
        // of argouml if a project is corrupted. Issue 913
        // Created xmireader with method getErrors to check if parsing went well
        XMIReader xmiReader = null;
        try {
            xmiReader = new XMIReader();
            source.setEncoding("UTF-8");
            mmodel = xmiReader.parseToModel(source);        
        } catch (SAXException e) { // duh, this must be caught and handled
            LOG.error("SAXException caught", e);
            throw e;
        } catch (ParserConfigurationException e) { 
            LOG.error("ParserConfigurationException caught", e);
            throw new SAXException(e);
        } catch (IOException e) {
            LOG.error("IOException caught", e);
            throw new SAXException(e);
        }

        if (xmiReader.getErrors()) {
            ArgoParser.getInstance().setLastLoadStatus(false);
            ArgoParser.getInstance().setLastLoadMessage(
                    "XMI file could not be parsed.");
            LOG.error("XMI file could not be parsed.");
            throw new SAXException(
                    "XMI file could not be parsed.");
        }

        // This should probably be inside xmiReader.parse
        // but there is another place in this source
        // where XMIReader is used, but it appears to be
        // the NSUML XMIReader.  When Argo XMIReader is used
        // consistently, it can be responsible for loading
        // the listener.  Until then, do it here.
        UmlHelper.getHelper().addListenersToModel(mmodel);

        project.addMember(mmodel);

        project.setUUIDRefs(new HashMap(xmiReader.getXMIUUIDToObjectMap()));
    }
}