/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;

/**
 * Represents the area set aside for editor workbooks.
 * This container only accepts EditorStack and PartSash
 * as layout parts.
 *
 * Note no views are allowed within this container.
 */
public class EditorArea extends PartSashContainer {
	
	private static final String DEFAULT_WORKBOOK_ID = "DefaultEditorWorkbook";//$NON-NLS-1$
	private ArrayList editorWorkbooks = new ArrayList(3);
	private EditorStack activeEditorWorkbook;
	private DropTarget dropTarget;
	private WorkbenchPage page;
	
public EditorArea(String editorId, WorkbenchPage page) {
	super(editorId,page);

	//this.partDropListener = listener;
	this.page = page;
	createDefaultWorkbook();
}
/**
 * Add an editor to the active workbook.
 */
public void addEditor(EditorPane pane) {
	EditorStack workbook = getActiveWorkbook();
	workbook.add(pane);
}
/**
 * Notification that a child layout part has been
 * added to the container. Subclasses may override
 * this method to perform any container specific
 * work.
 */
protected void childAdded(LayoutPart child) {
	if (child instanceof EditorStack)
		editorWorkbooks.add(child);
}
/**
 * Notification that a child layout part has been
 * removed from the container. Subclasses may override
 * this method to perform any container specific
 * work.
 */
protected void childRemoved(LayoutPart child) {
	if (child instanceof EditorStack) {
		editorWorkbooks.remove(child);
		if (activeEditorWorkbook == child)
			setActiveWorkbook(null, false);
	}
}
protected EditorStack createDefaultWorkbook() {
	EditorStack newWorkbook = EditorStack.newEditorWorkbook(this, page);
	newWorkbook.setID(DEFAULT_WORKBOOK_ID);
	add(newWorkbook);
	return newWorkbook;
}
/**
 * Subclasses override this method to specify
 * the composite to use to parent all children
 * layout parts it contains.
 */
protected Composite createParent(Composite parentWidget) {
	return new Composite(parentWidget, SWT.NONE);
}
/**
 * Dispose of the editor area.
 */
public void dispose() {
	// Free editor workbooks.
	Iterator iter = editorWorkbooks.iterator();
	while (iter.hasNext()) {
		EditorStack wb = (EditorStack)iter.next();
		wb.dispose();
	}
	editorWorkbooks.clear();

	// Free rest.
	super.dispose();
}
/**
 * Subclasses override this method to dispose
 * of any swt resources created during createParent.
 */
protected void disposeParent() {
	this.parent.dispose();
}
/**
 * Return the editor workbook which is active.
 */
public EditorStack getActiveWorkbook() {
	if (activeEditorWorkbook == null) {
		if (editorWorkbooks.size() < 1)
			setActiveWorkbook(createDefaultWorkbook(), false);
		else 
			setActiveWorkbook((EditorStack)editorWorkbooks.get(0), false);
	}

	return activeEditorWorkbook;
}
/**
 * Return the editor workbook id which is active.
 */
public String getActiveWorkbookID() {
	return getActiveWorkbook().getID();
}
/**
 * Return the all the editor workbooks.
 */
public ArrayList getEditorWorkbooks() {
	return (ArrayList)editorWorkbooks.clone();
}
/**
 * Return the all the editor workbooks.
 */
public int getEditorWorkbookCount() {
	return editorWorkbooks.size();
}
/**
 * Return true is the workbook specified
 * is the active one.
 */
protected boolean isActiveWorkbook(EditorStack workbook) {
	return activeEditorWorkbook == workbook;
}
/**
 * Find the sashs around the specified part.
 */
public void findSashes(LayoutPart pane,PartPane.Sashes sashes) {
	//Find the sashes around the current editor and
	//then the sashes around the editor area.
	super.findSashes(pane,sashes);
	
	ILayoutContainer container = getContainer();
	if (container != null) {
		container.findSashes(this,sashes);
	}
}
/**
 * Remove all the editors
 */
public void removeAllEditors() {
	EditorStack currentWorkbook = getActiveWorkbook();

	// Iterate over a copy so the original can be modified.	
	Iterator workbooks = ((ArrayList)editorWorkbooks.clone()).iterator();
	while (workbooks.hasNext()) {
		EditorStack workbook = (EditorStack)workbooks.next();
		workbook.removeAll();
		if (workbook != currentWorkbook) {
			remove(workbook);
			workbook.dispose();
		}
	}
}
/**
 * Remove an editor from its' workbook.
 */
public void removeEditor(EditorPane pane) {
	EditorStack workbook = pane.getWorkbook();
	if (workbook == null)
		return;
	workbook.remove(pane);

	// remove the editor workbook if empty
	if (workbook.getItemCount() < 1 /* && editorWorkbooks.size() > 1*/) {
		remove(workbook);
		workbook.dispose();
	}
}
/**
 * @see IPersistablePart
 */
public IStatus restoreState(IMemento memento) {
	// Remove the default editor workbook that is
	// initialy created with the editor area.
	if (children != null) {
		EditorStack defaultWorkbook = null;
		for (int i = 0; i < children.size(); i++) {
			LayoutPart child = (LayoutPart)children.get(i);
			if (child.getID() == DEFAULT_WORKBOOK_ID) {
				defaultWorkbook = (EditorStack)child;
				if (defaultWorkbook.getItemCount() > 0)
					defaultWorkbook = null;
			}
		}
		if (defaultWorkbook != null)
			remove(defaultWorkbook);
	}

	// Restore the relationship/layout
	IMemento [] infos = memento.getChildren(IWorkbenchConstants.TAG_INFO);
	Map mapIDtoPart = new HashMap(infos.length);

	for (int i = 0; i < infos.length; i ++) {
		// Get the info details.
		IMemento childMem = infos[i];
		String partID = childMem.getString(IWorkbenchConstants.TAG_PART);
		String relativeID = childMem.getString(IWorkbenchConstants.TAG_RELATIVE);
		int relationship = 0;
		int left = 0, right = 0;
		float ratio = 0.5f;
		if (relativeID != null) {
			relationship = childMem.getInteger(IWorkbenchConstants.TAG_RELATIONSHIP).intValue();
			Float ratioFloat = childMem.getFloat(IWorkbenchConstants.TAG_RATIO);
			Integer leftInt = childMem.getInteger(IWorkbenchConstants.TAG_RATIO_LEFT);
			Integer rightInt = childMem.getInteger(IWorkbenchConstants.TAG_RATIO_RIGHT);
			if (leftInt != null && rightInt != null) {
				left = leftInt.intValue();
				right = rightInt.intValue();
			} else if (ratioFloat != null) {
				ratio = ratioFloat.floatValue();
			}
		}

		// Create the part.
		EditorStack workbook = EditorStack.newEditorWorkbook(this, page);
		workbook.setID(partID);
		// 1FUN70C: ITPUI:WIN - Shouldn't set Container when not active
		workbook.setContainer(this);
		
		// Add the part to the layout
		if (relativeID == null) {
			add(workbook);
		} else {
			LayoutPart refPart = (LayoutPart)mapIDtoPart.get(relativeID);
			if (refPart != null) {
				//$TODO pass in left and right
				if (left == 0 || right == 0)
					add(workbook, relationship, ratio, refPart);
				else
					add(workbook, relationship, left, right, refPart);
			} else {
				WorkbenchPlugin.log("Unable to find part for ID: " + relativeID);//$NON-NLS-1$
			}
		}
		mapIDtoPart.put(partID, workbook);
	}
	return new Status(IStatus.OK,PlatformUI.PLUGIN_ID,0,"",null); //$NON-NLS-1$
}
/**
 * @see IPersistablePart
 */
public IStatus saveState(IMemento memento) {
	RelationshipInfo[] relationships = computeRelation();
	for (int i = 0; i < relationships.length; i ++) {
		// Save the relationship info ..
		//		private LayoutPart part;
		// 		private int relationship;
		// 		private float ratio;
		// 		private LayoutPart relative;
		RelationshipInfo info = relationships[i];
		IMemento childMem = memento.createChild(IWorkbenchConstants.TAG_INFO);
		childMem.putString(IWorkbenchConstants.TAG_PART, info.part.getID());
		if (info.relative != null) {
			childMem.putString(IWorkbenchConstants.TAG_RELATIVE, info.relative.getID());
			childMem.putInteger(IWorkbenchConstants.TAG_RELATIONSHIP, info.relationship);
			childMem.putInteger(IWorkbenchConstants.TAG_RATIO_LEFT, info.left);
			childMem.putInteger(IWorkbenchConstants.TAG_RATIO_RIGHT, info.right);
			// Note: "ratio" is not used in newer versions of Eclipse, which use "left" 
			// and "right" (above) instead
			childMem.putFloat(IWorkbenchConstants.TAG_RATIO, info.getRatio());
		}
	}
	return new Status(IStatus.OK,PlatformUI.PLUGIN_ID,0,"",null); //$NON-NLS-1$
}
/**
 * Set the editor workbook which is active.
 */
public void setActiveWorkbook(EditorStack newWorkbook, boolean hasFocus) {
	EditorStack oldWorkbook = activeEditorWorkbook;
	activeEditorWorkbook = newWorkbook;
	
	if (oldWorkbook != null && oldWorkbook != newWorkbook)
		oldWorkbook.tabFocusHide();

	if (newWorkbook != null)
		newWorkbook.tabFocusShow(hasFocus);
		
	updateTabList();
}
/**
 * Set the editor workbook which is active.
 */
public void setActiveWorkbookFromID(String id) {
	for (int i = 0; i < editorWorkbooks.size(); i++) {
		EditorStack workbook = (EditorStack) editorWorkbooks.get(i);
		if (workbook.getID().equals(id))
			setActiveWorkbook(workbook, false);
	}
}

/**
 * Updates the editor area's tab list to include the active
 * editor and its tab.
 */
public void updateTabList() {
	Composite parent = getParent();
	if (parent != null) {  // parent may be null on startup
		EditorStack wb = getActiveWorkbook();
		if (wb == null) {
			parent.setTabList(new Control[0]);
		}
		else {
			parent.setTabList(wb.getTabList());
		}
	}
}


	/**
	 * @see org.eclipse.ui.internal.LayoutPart#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		//let the user drop files/editor input on the editor area
		addDropSupport();		
	}
	private void addDropSupport() {
		if (dropTarget == null) {
			WorkbenchWindowConfigurer winConfigurer = ((WorkbenchWindow) page.getWorkbenchWindow()).getWindowConfigurer();
		
			dropTarget = new DropTarget(getControl(), DND.DROP_DEFAULT | DND.DROP_COPY);
			dropTarget.setTransfer(winConfigurer.getTransfers());
			if (winConfigurer.getDropTargetListener() != null) {
				dropTarget.addDropListener(winConfigurer.getDropTargetListener());
			}
		}
	}

	/* package */ DropTarget getDropTarget() {
		return dropTarget;
	}
	/**
	 * @see org.eclipse.ui.internal.LayoutPart#getImportance()
	 */
	public boolean isCompressible() {
		//Added for bug 19524
		return true;
	}	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.ILayoutContainer#allowsAutoFocus()
	 */
	public boolean allowsAutoFocus() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.PartSashContainer#isStackType(org.eclipse.ui.internal.LayoutPart)
	 */
	public boolean isStackType(LayoutPart toTest) {
		return (toTest instanceof EditorStack);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.PartSashContainer#isPaneType(org.eclipse.ui.internal.LayoutPart)
	 */
	public boolean isPaneType(LayoutPart toTest) {
		return (toTest instanceof EditorPane);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.PartSashContainer#createStack(org.eclipse.ui.internal.LayoutPart)
	 */
	protected LayoutPart createStack(LayoutPart sourcePart) {
		EditorStack newWorkbook = EditorStack.newEditorWorkbook(this, page);
		newWorkbook.add((EditorPane)sourcePart);
		newWorkbook.setVisibleEditor((EditorPane)sourcePart);
		
		return newWorkbook;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.PartSashContainer#setVisiblePart(org.eclipse.ui.internal.ILayoutContainer, org.eclipse.ui.internal.LayoutPart)
	 */
	protected void setVisiblePart(ILayoutContainer container, LayoutPart visiblePart) {
		EditorStack refPart = (EditorStack)container;
		
		refPart.becomeActiveWorkbook(true);
		refPart.setVisibleEditor((EditorPane)visiblePart);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.PartSashContainer#getVisiblePart(org.eclipse.ui.internal.ILayoutContainer)
	 */
	protected LayoutPart getVisiblePart(ILayoutContainer container) {
		EditorStack refPart = (EditorStack)container;

		return refPart.getVisibleEditor();
	}

}
