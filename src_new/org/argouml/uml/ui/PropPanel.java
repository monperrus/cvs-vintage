// $Id: PropPanel.java,v 1.87 2004/08/16 19:30:58 mvw Exp $
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

package org.argouml.uml.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.swingext.LabelledLayout;
import org.argouml.swingext.Orientation;
import org.argouml.swingext.Vertical;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.TabSpawnable;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.ui.targetmanager.TargettableModelView;
import org.argouml.uml.Profile;
import org.argouml.uml.ProfileJava;
import org.tigris.gef.presentation.Fig;
import org.tigris.toolbar.ToolBar;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;

/**
 * This abstract class provides the basic layout and event dispatching
 * support for all Property Panels.<p>
 *
 * The property panel is {@link org.argouml.swingext.LabelledLayout layed out}
 * as a number (specified in the constructor) of equally sized panels
 * that split the available space.  Each panel has a column of
 * "captions" and matching column of "fields" which are laid out
 * indepently from the other panels.<p>
 *
 * The Properties panels for UML Model Elements are structured in an
 * inheritance hierarchy that matches the UML 1.3 metamodel.
 */
public abstract class PropPanel
    extends TabSpawnable
    implements TabModelTarget, MElementListener, UMLUserInterfaceContainer
{
    ////////////////////////////////////////////////////////////////
    // instance vars
    private Object target;
    private Object modelElement;
    private static Profile profile;

    private ResourceBundle bundle = null;

    private Vector panels = new Vector();

    private int lastRow;

    private EventListenerList listenerList;

    /**
     * The metaclass/property pairs for the third party listener (if we have
     * set one up. We use this when creating a new listener on target
     * change.<p>
     */
    private JPanel center;

    protected JToolBar buttonPanel;
    private JPanel buttonPanelWithFlowLayout = new JPanel();

    private JLabel titleLabel;

    private JPanel captionPanel = new JPanel();

    protected static ImageIcon _deleteIcon =
	ResourceLoaderWrapper
	    .lookupIconResource("RedDelete");
    protected static ImageIcon _navUpIcon =
	ResourceLoaderWrapper
	    .lookupIconResource("NavigateUp");

    protected Font smallFont = LookAndFeelMgr.getInstance().getSmallFont();

    /**
     * Construct new PropPanel using LabelledLayout.<p>
     *
     * @param icon The icon to display for the panel
     * @param title The title of the panel
     *
     * @param orientation the orientation
     */
    public PropPanel(String title, ImageIcon icon, Orientation orientation) {
        super(title);
        setOrientation(orientation);
        buttonPanel = new ToolBar();
        buttonPanel.putClientProperty("JToolBar.isRollover",  Boolean.TRUE);
        buttonPanel.setFloatable(false);
        //buttonPanel.putClientProperty("JToolBar.isRollover",  Boolean.TRUE);

        setLayout(new LabelledLayout(orientation == Vertical.getInstance()));

        if (icon != null) {
            setTitleLabel(new JLabel(localize(title), icon, 
                    SwingConstants.LEFT));
        }
        else {
            setTitleLabel(new JLabel(localize(title)));
        }
        //buttonPanel = new JPanel(new SerialLayout());
        getTitleLabel().setLabelFor(buttonPanel);
        add(getTitleLabel());
        add(buttonPanel);
    }

    /**
     * Constructs a new Proppanel without an icon. If there is an icon it's
     * updated at runtime via settarget.<p>
     *
     * @param title
     * @param orientation
     */
    public PropPanel(String title, Orientation orientation) {
        this (title, null, orientation);
    }

    /**
     * Set the orientation of the panel
     *
     * @see org.argouml.swingext.Orientable#setOrientation(org.argouml.swingext.Orientation)
     */
    public void setOrientation(Orientation orientation) {
        super.setOrientation(orientation);
    }

    /**
     * Add a component with the specified label.<p>
     *
     * @param label the label for the component
     * @param component the component
     * @return the label added
     */
    public JLabel addField(String label, Component component) {
        JLabel jlabel = new JLabel(localize(label));
        jlabel.setFont(LookAndFeelMgr.getInstance().getSmallFont());
        component.setFont(LookAndFeelMgr.getInstance().getSmallFont());
        jlabel.setLabelFor(component);
        add(jlabel);
        add(component);
        return jlabel;
    }

    /**
     * Add a component with the specified label positioned after
     * another component.
     *
     * @param label the label for the component
     * @param component the component
     *
     * @param afterComponent the component before
     * @return the newly added label
     */
    public JLabel addFieldAfter(String label, Component component,
				Component afterComponent) {
        int nComponent = this.getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == afterComponent) {
                JLabel jlabel = new JLabel(localize(label));
                jlabel.setFont(LookAndFeelMgr.getInstance().getSmallFont());
                component.setFont(LookAndFeelMgr.getInstance().getSmallFont());
                jlabel.setLabelFor(component);
                add(jlabel, ++i);
                add(component, ++i);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     * Add a component with the specified label positioned before
     * another component.<p>
     *
     * @param label the label for the component
     * @param component the component
     * @param beforeComponent the component
     *
     * @return the newly added component
     */
    public JLabel addFieldBefore(String label, Component component,
				 Component beforeComponent) {
        int nComponent = this.getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == beforeComponent) {
                JLabel jlabel = new JLabel(localize(label));
                jlabel.setFont(LookAndFeelMgr.getInstance().getSmallFont());
                component.setFont(LookAndFeelMgr.getInstance().getSmallFont());
                jlabel.setLabelFor(component);
                add(jlabel, i);
                add(component, ++i);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     *   Adds a component to the fields of the specified panel
     *     and sets the background and color to indicate
     *     the field is a link.
     *   @param label the required string label
     *   @param component Component to be added
     *   @deprecated replaced by according widgets as of version 0.17.1
     */
    public final void addLinkField(String label, JComponent component) {
        component.setBackground(getBackground());
        component.setForeground(Color.blue);
        addField(label, component);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#localize(java.lang.String)
     */
    public final String localize(String key) {
        String localized = key;
        if (bundle == null) {
            bundle = getResourceBundle();
        }
        if (bundle != null) {
            try {
                localized = bundle.getString(key);
            } catch (MissingResourceException e) {
            }
            if (localized == null) {
                localized = key;
            }
        }
        return localized;
    }

    protected final void addSeperator() {
        add(LabelledLayout.getSeperator());
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getProfile()
     */
    public Profile getProfile() {
        if (profile == null) {
            profile = ProfileJava.getInstance();
        }
        return profile;
    }

    /**
       This method (and addMElementListener) can be overriden if the
       prop panel wants to monitor additional objects.
       ONLY use it if the target is a NSUML modelelement

       @param theTarget target of prop panel

    */
    protected void removeMElementListener(Object theTarget) {
        UmlModelEventPump.getPump().removeModelEventListener(this, theTarget);
    }

    /**
       This method (and removeMElementListener) can be overriden if the
       prop panel wants to monitor additional objects.  This method
       is public only since it is called from a Runnable object.

       @param theTarget target of prop panel
    */
    public void addMElementListener(Object theTarget) {
        UmlModelEventPump.getPump().addModelEventListener(this, theTarget);
    }

    /**
     * Set the target to be associated with a particular property panel.<p>
     *
     * This involves resetting the third party listeners.<p>
     * @deprecated As Of Argouml version 0.13.5,
     *             This will change visibility from release 0.16
     * @param t  The object to be set as a target.
     */

    public void setTarget(Object t) {
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;

        // If the target has changed notify the third party listener if it
        // exists and dispatch a new NSUML element listener to
        // ourself. Otherwise dispatch a target reasserted to ourself.
        Runnable dispatch = null;
        if (t != target) {

            // Set up the target and its model element variant.

            target = t;
            modelElement = null;
            if (listenerList == null) {
                listenerList = registrateTargetListeners(this);
            }

            if (ModelFacade.isAModelElement(target)) {
                modelElement = target;
            }

            // This will add a new MElement listener after update is complete

            dispatch =
		new UMLChangeDispatch(this,
				      UMLChangeDispatch.TARGET_CHANGED_ADD);

        }
        else {
            dispatch =
		new UMLChangeDispatch(this,
				      UMLChangeDispatch.TARGET_REASSERTED);

        }
        SwingUtilities.invokeLater(dispatch);

        // update the titleLabel
        if (getTitleLabel() != null) {
            Icon icon =
		ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIcon(t);
            if (icon != null)
                getTitleLabel().setIcon(icon);
        }
    }

    /**
     * Builds a eventlistenerlist of all targetlisteners that are part of this
     * container and its children.
     *
     * @param container the container to search for targetlisteners
     * @return an EventListenerList with all TargetListeners on this
     * container and its children.
     */
    private EventListenerList registrateTargetListeners(Container container) {
        Component[] components = container.getComponents();
        EventListenerList list = new EventListenerList();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof TargetListener) {
                list.add(TargetListener.class, (TargetListener) components[i]);
            }
            if (components[i] instanceof TargettableModelView) {
                list.add(TargetListener.class,
			 ((TargettableModelView) components[i])
			     .getTargettableModel());
            }
            if (components[i] instanceof Container) {
                EventListenerList list2 =
		    registrateTargetListeners((Container) components[i]);
                Object[] objects = list2.getListenerList();
                for (int j = 1; j < objects.length; j += 2) {
                    list.add(TargetListener.class, (TargetListener) objects[j]);
                }
            }
        }
        return list;
    }

    /**
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public final Object getTarget() {
        return target;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getModelElement()
     */
    public final Object getModelElement() {
        return modelElement;
    }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        SwingUtilities.invokeLater(new UMLChangeDispatch(this, 0));
    }

    /**
     * @see org.argouml.ui.TabTarget#shouldBeEnabled(java.lang.Object)
     */
    public boolean shouldBeEnabled(Object target) {
        target = (target instanceof Fig) ? ((Fig) target).getOwner() : target;
        return ModelFacade.isAModelElement(target);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#propertySet(ru.novosoft.uml.MElementEvent)
     */
    public void propertySet(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.propertySet(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#listRoleItemSet(ru.novosoft.uml.MElementEvent)
     */
    public void listRoleItemSet(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.listRoleItemSet(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#recovered(ru.novosoft.uml.MElementEvent)
     */
    public void recovered(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.recovered(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#removed(ru.novosoft.uml.MElementEvent)
     */
    public void removed(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.removed(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleAdded(ru.novosoft.uml.MElementEvent)
     */
    public void roleAdded(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.roleAdded(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleRemoved(ru.novosoft.uml.MElementEvent)
     */
    public void roleRemoved(MElementEvent mee) {
        UMLChangeDispatch dispatch = new UMLChangeDispatch(this, 0);
        dispatch.roleRemoved(mee);
        SwingUtilities.invokeLater(dispatch);
    }

    /**
     * This method can be overriden in derived Panels where the
     * appropriate namespace for display may not be the same as
     * the namespace of the target
     *
     * @return the namespace
     */
    protected Object getDisplayNamespace() {
        Object ns = null;
        Object theTarget = getTarget();
        if (ModelFacade.isAModelElement(theTarget)) {
            ns = ModelFacade.getNamespace(theTarget);
        }
        return ns;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatElement(java.lang.Object)
     */
    public String formatElement(/*MModelElement*/Object element) {
        return getProfile().formatElement(element, getDisplayNamespace());
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatNamespace(java.lang.Object)
     */
    public String formatNamespace(/*MNamespace*/Object namespace) {
        return getProfile().formatElement(namespace, null);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatCollection(java.util.Iterator)
     */
    public String formatCollection(Iterator iter) {
        Object namespace = getDisplayNamespace();
        return getProfile().formatCollection(iter, namespace);
    }

    /**
     * @deprecated As of ArgoUml version 0.13.5, replaced by
     * {@link TargetManager#navigateBackward()}.
     */
    public void navigateBackAction() {
        TargetManager.getInstance().navigateBackward();
    }

    /**
     * Calling this method with an array of metaclasses (for example,
     * MClassifier.class) will result in the prop panel propagating
     * any name changes or removals on any object that on the same
     * event queue as the target that is assignable to one of the
     * metaclasses.<p>
     *
     * <em>Note</em>. Despite the name, the old implementation tried
     * to listen for ownedElement and baseClass events as well as name
     * events. We incorporate all these.<p>
     *
     * <em>Note</em> Reworked the implementation to use the new
     * UmlModelEventPump mechanism. In the future proppanels should
     * register directly with UmlModelEventPump IF they are really
     * interested in the events themselves. If components on the
     * proppanels are interested, these components should register
     * themselves.<p>
     *
     * @deprecated As of ArgoUml version unknown(earlier than 0.13.5),
     *             replaced by
     * {@link org.argouml.model.uml.UmlModelEventPump#addModelEventListener(
     *            Object , Object)}.
     *             since components should register themselves.
     *
     * @param metaclasses  The metaclass array we wish to listen to.
     */
    public void setNameEventListening(Class[] metaclasses) {

        /*
	   old implementation

	   // Convert to the third party listening pair list

	   Vector targetList = new Vector (metaclasses.length * 6);

	   for (int i = 0 ; i < metaclasses.length ; i++) {
	   Class mc = metaclasses[i];

	   targetList.add(mc);
	   targetList.add("name");

	   targetList.add(mc);
	   targetList.add("baseClass");

	   targetList.add(mc);
	   targetList.add("ownedElement");
	   }

	   addThirdPartyEventListening(targetList.toArray());
        */
        for (int i = 0; i < metaclasses.length; i++) {
            Class clazz = metaclasses[i];
            if (((Class) ModelFacade.NAMESPACE).isAssignableFrom(clazz)) {
                UmlModelEventPump.getPump()
		    .addClassModelEventListener(this, clazz, "ownedElement");
            }
            if (((Class) ModelFacade.MODELELEMENT).isAssignableFrom(clazz)) {
                UmlModelEventPump.getPump()
		    .addClassModelEventListener(this, clazz, "name");
            }
            if (clazz.equals(ModelFacade.STEREOTYPE)) {
                UmlModelEventPump.getPump()
		    .addClassModelEventListener(this, clazz, "baseClass");
            }
        }
    }

    public void removeElement() {
        Object theTarget = getTarget();
        if (ModelFacade.isABase(theTarget)) {
            Object newTarget = ModelFacade.getModelElementContainer(theTarget);
            Object base = theTarget;
            TargetManager.getInstance().setTarget(base);
            ActionEvent event = new ActionEvent(this, 1, "delete");
            new ActionRemoveFromModel().actionPerformed(event);
            if (newTarget != null) {
                TargetManager.getInstance().setTarget(newTarget);
            }
        }
    }

    /**
     * Check whether this element can be deleted.
     * Currently it only checks whether we delete the main model.
     * ArgoUML does not like that.
     *
     * @since 0.13.2
     * @return whether this element can be deleted
     */
    public boolean isRemovableElement() {
        return ((getTarget() != null)
		&& (getTarget()
		    != (ProjectManager.getManager().getCurrentProject()
			.getModel())));
    }

    /**
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        // we can neglect this, the TabProps allways selects the first target
	// in a set of targets. The first target can only be
	// changed in a targetRemoved or a TargetSet event
        fireTargetAdded(e);
    }

    /**
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // how to handle empty target lists?
        // probably the TabProps should only show an empty pane in that case
        fireTargetRemoved(e);
        // setTarget(e.getNewTarget());
        

    }

    /**
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
        fireTargetSet(e);

    }

    private void fireTargetSet(TargetEvent targetEvent) {
	//          Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == TargetListener.class) {
		// Lazily create the event:
		((TargetListener) listeners[i + 1]).targetSet(targetEvent);
	    }
	}
    }

    private void fireTargetAdded(TargetEvent targetEvent) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == TargetListener.class) {
		// Lazily create the event:
		((TargetListener) listeners[i + 1]).targetAdded(targetEvent);
	    }
	}
    }

    private void fireTargetRemoved(TargetEvent targetEvent) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == TargetListener.class) {
		// Lazily create the event:
		((TargetListener) listeners[i + 1]).targetRemoved(targetEvent);
	    }
	}
    }

    protected void setTitleLabel(JLabel theTitleLabel) {
        this.titleLabel = theTitleLabel;
    }

    protected JLabel getTitleLabel() {
        return titleLabel;
    }
} /* end class PropPanel */
