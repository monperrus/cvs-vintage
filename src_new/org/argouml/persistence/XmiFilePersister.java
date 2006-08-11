// $Id: XmiFilePersister.java,v 1.33 2006/08/11 19:12:24 mvw Exp $
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

package org.argouml.persistence;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectMember;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.ProjectMemberTodoList;

/**
 * To persist to and from XMI file storage.
 *
 * @author Bob Tarling
 */
public class XmiFilePersister extends AbstractFilePersister 
    implements XmiExtensionParser {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(XmiFilePersister.class);

    private ArrayList pgmlStrings = new ArrayList();
    
    private String todoString;
    
    private String argoString;
    
    /**
     * The constructor.
     */
    public XmiFilePersister() {
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getExtension()
     */
    public String getExtension() {
        return "xmi";
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getDesc()
     */
    protected String getDesc() {
        return Translator.localize("combobox.filefilter.xmi");
    }

    /**
     * Save a project to a file in XMI format.
     *
     * @param project the project to save.
     * @param file The file to write.
     * @throws SaveException if anything goes wrong.
     * @throws InterruptedException     if the thread is interrupted
     */
    public void doSave(Project project, File file)
        throws SaveException, InterruptedException {

        ProgressMgr progressMgr = new ProgressMgr();
        progressMgr.setNumberOfPhases(4);
        progressMgr.nextPhase();
        
        File lastArchiveFile = new File(file.getAbsolutePath() + "~");
        File tempFile = null;

        try {
            tempFile = createTempFile(file);
        } catch (FileNotFoundException e) {
            throw new SaveException(
                    "Failed to archive the previous file version", e);
        } catch (IOException e) {
            throw new SaveException(
                    "Failed to archive the previous file version", e);
        }

        OutputStreamWriter writer = null;
        try {
            //project.setFile(file);

            OutputStream stream = new FileOutputStream(file);
            OutputStream bout = new BufferedOutputStream(stream);
            writer = 
                new OutputStreamWriter(bout, PersistenceManager.getEncoding());

            int size = project.getMembers().size();
            for (int i = 0; i < size; i++) {
                ProjectMember projectMember =
                    (ProjectMember) project.getMembers().get(i);
                if (projectMember.getType().equalsIgnoreCase(getExtension())) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Saving member of type: "
                              + ((ProjectMember) project.getMembers()
                                    .get(i)).getType());
                    }
                    MemberFilePersister persister
                        = new ModelMemberFilePersister();
                    persister.save(projectMember, writer, null);
                }
            }
            progressMgr.nextPhase();

            // if save did not raise an exception
            // and name+"#" exists move name+"#" to name+"~"
            // this is the correct backup file
            if (lastArchiveFile.exists()) {
                lastArchiveFile.delete();
            }
            if (tempFile.exists() && !lastArchiveFile.exists()) {
                tempFile.renameTo(lastArchiveFile);
            }
            if (tempFile.exists()) {
                tempFile.delete();
            }
        } catch (InterruptedException exc) {
            throw exc;
        } catch (Exception e) {
            LOG.error("Exception occured during save attempt", e);
            try {
                writer.close();
            } catch (IOException ex) { }

            // frank: in case of exception
            // delete name and mv name+"#" back to name if name+"#" exists
            // this is the "rollback" to old file
            file.delete();
            tempFile.renameTo(file);
            // we have to give a message to user and set the system to unsaved!
            throw new SaveException(e);
        }

        try {
            writer.close();
        } catch (IOException ex) {
            LOG.error("Failed to close save output writer", ex);
        }
        progressMgr.nextPhase();
    }


    /**
     * This method creates a project from the specified URL
     *
     * Unlike the constructor which forces an .argo extension This
     * method will attempt to load a raw XMI file
     *
     * This method can fail in several different ways. Either by
     * throwing an exception or by having the
     * ArgoParser.SINGLETON.getLastLoadStatus() set to not true.
     *
     * @param file The file to load the project from.
     * @return The newly loaded project.
     * @throws OpenException if the file can not be opened
     * @throws InterruptedException     if the thread is interrupted
     *
     * @see org.argouml.persistence.ProjectFilePersister#doLoad(java.io.File)
     */
    public Project doLoad(File file)
        throws OpenException, InterruptedException {

        LOG.info("Loading with XMIFilePersister");
        
        ProgressMgr progressMgr = new ProgressMgr();
        progressMgr.setNumberOfPhases(3);
        progressMgr.nextPhase();

        try {
            Project p = new Project();
            XMIParser.getSingleton().readModels(p, file.toURL(), this);
            Object model = XMIParser.getSingleton().getCurModel();
            progressMgr.nextPhase();
            Model.getUmlHelper().addListenersToModel(model);
            p.setUUIDRefs(XMIParser.getSingleton().getUUIDRefs());
            p.addMember(model);
            parseXmiExtensions(p);
            XMIParser.getSingleton().registerDiagrams(p);
            
            p.setRoot(model);
            progressMgr.nextPhase();
            ProjectManager.getManager().setSaveEnabled(false);
            return p;
        } catch (IOException e) {
            throw new OpenException(e);
        }
    }

    /**
     * Returns true. All Argo specific files have an icon.
     * 
     * @see org.argouml.persistence.AbstractFilePersister#hasAnIcon()
     */
    public boolean hasAnIcon() {
        return true;
    }

    /**
     * Parse a string of XML that is the XMI.extension contents.
     * This implementation simply stores the xml strings to process
     * in one hit after all the standard XMI has been read.
     * @see org.argouml.persistence.XmiExtensionParser#parse(java.lang.String, java.lang.String)
     */
    public void parse(String label, String xmiExtensionString) {
        if (label.equals("pgml")) {
            pgmlStrings.add(xmiExtensionString);
        } else if (label.equals("argo")) {
            argoString = xmiExtensionString;
        } else if (label.equals("todo")) {
            todoString = xmiExtensionString;
        }
    }

    /**
     * Parse all the extensions that were found when reading XMI
     */
    public void parseXmiExtensions(Project project) throws OpenException {
        
        if (argoString != null) {
            LOG.info("Parsing argoString " + argoString.length());
            InputStream inputStream = 
                new ByteArrayInputStream(argoString.getBytes());
            ArgoParser parser = new ArgoParser();
            try {
                parser.readProject(project, inputStream);
            } catch (Exception e) {
                throw new OpenException("Exception caught", e);
            }
        } else {
            project.addMember(new ProjectMemberTodoList("", project));
        }
        for (Iterator it = pgmlStrings.iterator(); it.hasNext(); ) {
            String pgml = (String) it.next();
            LOG.info("Parsing pgml " + pgml.length());
            InputStream inputStream = new ByteArrayInputStream(pgml.getBytes());
            MemberFilePersister persister =
                PersistenceManager.getInstance()
                        .getDiagramMemberFilePersister();
            persister.load(project, inputStream);
        }
        if (todoString != null) {
            LOG.info("Parsing todoString " + todoString.length());
            InputStream inputStream = 
                new ByteArrayInputStream(todoString.getBytes());
            MemberFilePersister persister = null;
            persister = new TodoListMemberFilePersister();
            persister.load(project, inputStream);
        } else {
            project.addMember(new ProjectMemberTodoList("", project));
        }
    }
}