// $Id: ProjectManager.java,v 1.36 2004/11/01 10:55:21 mkl Exp $
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

package org.argouml.kernel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.argouml.cognitive.Designer;
import org.argouml.ui.ArgoDiagram;

/**
 * This class manages the projects loaded in argouml.
 *
 * Classes in Argouml can ask this class for the current
 * project and set the current project.  Since we only have one
 * project in ArgoUML at the moment, this class does not manage a list
 * of projects like one would expect. This could be a nice extension
 * for the future of argouml.  As soon as the current project is
 * changed, a property changed event is fired.
 *
 * @since Nov 17, 2002
 * @author jaap.branderhorst@xs4all.nl
 * @stereotype singleton
 */
public final class ProjectManager implements PropertyChangeListener {

    /**
     * The name of the property that defines the current project.
     */
    public static final String CURRENT_PROJECT_PROPERTY_NAME =
        "currentProject";
    
    /**
     * The name of the property that defines the save state.
     */
    public static final String SAVE_STATE_PROPERTY_NAME = "saveState";

    /** logger */
    private static final Logger LOG = Logger.getLogger(ProjectManager.class);

    /**
     * The singleton instance of this class
     */
    private static ProjectManager instance;

    /**
     * The project that is visible in the projectbrowser
     */
    private static Project currentProject;

    /**
     * Flag to indicate we are creating a new current project
     */
    private boolean creatingCurrentProject;

    /**
     * The listener list
     */
    private EventListenerList listenerList = new EventListenerList();

    /**
     * The event to fire.
     * 
     * TODO: Investigate! Is the purpose really to let the next call to
     * {@link #firePropertyChanged(String, Object, Object)} fire the old 
     * event again if the previous invocation resulted in an exception?
     * If so, please document why. If not, fix it.
     */
    private PropertyChangeEvent event;

    /**
     * The singleton accessor method of this class.
     * 
     * @return The singleton.
     */
    public static ProjectManager getManager() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    /**
     * Constructor for ProjectManager.
     */
    private ProjectManager() {
        super();
    }

    /**
     * Adds a listener to the listener list.
     * 
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    /**
     * Removes a listener from the listener list.
     * 
     * @param listener The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    /**
     * Fire an event to all members of the listener list.
     * 
     * @param propertyName The name of the event.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    private void firePropertyChanged(String propertyName,
				     Object oldValue, Object newValue) 
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PropertyChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event =
                        new PropertyChangeEvent(
                            this,
                            propertyName,
                            oldValue,
                            newValue);
                ((PropertyChangeListener) listeners[i + 1]).propertyChange(
                    event);
            }
        }
        event = null;
    }

    /**
     * Sets the current project (the project that is viewable in the 
     * projectbrowser).
     * This method fires a propertychanged event.<p>
     *
     * If the argument is null, then the current project will be forgotten 
     * about.
     * 
     * @param newProject The new project.
     */
    public void setCurrentProject(Project newProject) {
        Project oldProject = currentProject;        
        currentProject = newProject;
        if (currentProject != null
	    && currentProject.getActiveDiagram() == null) {
            Vector diagrams = currentProject.getDiagrams();
            if (diagrams != null && !diagrams.isEmpty()) {
		ArgoDiagram activeDiagram =
		    (ArgoDiagram) currentProject.getDiagrams().get(0);
                currentProject.setActiveDiagram(activeDiagram);
	    }
        }
        firePropertyChanged(CURRENT_PROJECT_PROPERTY_NAME,
			    oldProject, newProject);
    }

    /**
     * Returns the current project.<p>
     * 
     * If there is no project, a new one is created.
     * 
     * @return Project The current project.
     */
    public Project getCurrentProject() {
        if (currentProject == null && !creatingCurrentProject) {
            currentProject = makeEmptyProject();
        }
        return currentProject;
    }

    /**
     * Makes an empty project with two standard diagrams.
     * @return Project
     */
    public Project makeEmptyProject() {
        creatingCurrentProject = true;
        LOG.info("making empty project");
        Project p = new Project();
        // the following line should not normally be here,
        // but is necessary for argouml start up.
        setCurrentProject(p);
        p.makeUntitledProject();
        // set the current project after making it!
        setCurrentProject(p);
        creatingCurrentProject = false;
        return p;
    }
    
    /**
     * Notify the gui from the project manager that the
     * current project's save state has changed.
     * 
     * @param newValue The new state.
     */
    public void notifySavePropertyChanged(boolean newValue) {
        
        firePropertyChanged(SAVE_STATE_PROPERTY_NAME,
                            new Boolean(!newValue),
                            new Boolean(newValue));
    }
    
    /**
     * Remove the project.
     * 
     * @param oldProject The project to be removed.
     */
    public void removeProject(Project oldProject) {
        
        if (currentProject == oldProject) {
            currentProject = null;
        }
        
        oldProject.remove();
    }

    /* react to PropertyChangeEvents, e.g. send by the Designer.
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(Designer.MODEL_TODOITEM_ADDED)) {
            getCurrentProject().setNeedsSave(true);
        }
        else if (pce.getPropertyName().equals(Designer.MODEL_TODOITEM_DISMISSED)) {
            getCurrentProject().setNeedsSave(true);
        }
        
    }
}
