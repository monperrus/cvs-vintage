package org.eclipse.ui.internal;

/******************************************************************************* 
 * Copyright (c) 2000, 2003 IBM Corporation and others. 
 * All rights reserved. This program and the accompanying materials! 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html 
 * 
 * Contributors: 
 *    IBM Corporation - initial API and implementation 
 *    Cagatay Kavukcuoglu <cagatayk@acm.org>
 *      - Fix for bug 10025 - Resizing views should not use height ratios
**********************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.Geometry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dnd.DragUtil;
import org.eclipse.ui.internal.dnd.IDragOverListener;
import org.eclipse.ui.internal.dnd.IDropTarget;
import org.eclipse.ui.internal.registry.IViewDescriptor;
import org.eclipse.ui.internal.skins.IPresentablePart;
import org.eclipse.ui.internal.skins.IPresentationSite;
import org.eclipse.ui.internal.skins.StackDropResult;
import org.eclipse.ui.internal.skins.StackPresentation;
import org.eclipse.ui.internal.skins.newlook.DefaultStackPresentationSite;
import org.eclipse.ui.internal.skins.newlook.PartTabFolderPresentation;


public class PartTabFolder extends LayoutPart implements ILayoutContainer, IWorkbenchDragSource {
	
	private WorkbenchPage page;
	private PartPane.PaneContribution paneContribution;

	private DefaultStackPresentationSite presentationSite = new DefaultStackPresentationSite() {
		public void selectPart(IPresentablePart toSelect) {
			super.selectPart(toSelect);
			
			setCurrentLayoutPart(getLayoutPart(toSelect));
						
			// set the title of the detached window to reflect the active tab
			Window window = getWindow();
			if (window instanceof DetachedWindow) {
				if (current == null || !(current instanceof PartPane))
					window.getShell().setText("");//$NON-NLS-1$
				else
					window.getShell().setText(((PartPane) current).getPartReference().getTitle());
			}
		}
		
		public void setPresentation(StackPresentation newPresentation) {
			super.setPresentation(newPresentation);
			
			updateSystemMenu();
		}
		
		public void setState(int newState) {
			PartTabFolder.this.setState(newState);
		}
		
		public void dragStart(IPresentablePart beingDragged, Point initialLocation, boolean keyboard) {
			LayoutPart pane = getPaneFor(beingDragged);
			
			if (pane != null) {			
				DragUtil.performDrag(pane, 
						Geometry.toDisplay(getParent(), getPresentation().getControl().getBounds()),
						initialLocation, true);
			}
		}
		
		public void close(IPresentablePart part) {
			PartTabFolder.this.close(part);
		}
		
		public boolean isClosable(IPresentablePart part) {
			Perspective perspective = page.getActivePerspective();
			
			if (perspective == null) {
				// Shouldn't happen -- can't have a PartTabFolder without a perspective
				return false;
			}
			
			ViewPane pane = (ViewPane)getPaneFor(part);
			
			if (pane == null) {
				// Shouldn't happen -- this should only be called for ViewPanes that are already in the tab folder
				return false;
			}
			
			return !perspective.isFixedView(pane.getViewReference());
		}
			
		public boolean isMovable(IPresentablePart part) {
			return isClosable(part);
		}
	};

	// inactiveCurrent is only used when restoring the persisted state of
	// perspective on startup.
	private LayoutPart current;
	private LayoutPart inactiveCurrent;
	private boolean active = false;
	private int flags;
		
	private List children = new ArrayList(3);
	
	/**
	 * PartTabFolder constructor comment.
	 */
	public PartTabFolder(WorkbenchPage page) {
		this(page, SWT.MIN | SWT.MAX);
	}
	
	/**
	 * @param part
	 */
	protected void close(IPresentablePart part) {
		if (!presentationSite.isClosable(part)) {
			return;
		}
		
		LayoutPart layoutPart = getPaneFor(part);
		
		if (layoutPart != null && layoutPart instanceof ViewPane) {
			ViewPane viewPane = (ViewPane) layoutPart;
			
			//getPresentation().removePart(part);
			viewPane.doHide();
		}
	}

	private LayoutPart getPaneFor(IPresentablePart part) {
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart next = (LayoutPart) iter.next();
			
			if (next.getPresentablePart() == part) {
				return next;
			}
		}
		
		return null;
	}
	
	public PartTabFolder(WorkbenchPage page, int flags) {
		super("PartTabFolder");

		setID(this.toString());
		// Each folder has a unique ID so relative positioning is unambiguous.
		
		// save off a ref to the page
		//@issue is it okay to do this??
		//I think so since a PartTabFolder is
		//not used on more than one page.
		this.page = page;
		this.flags = flags;
	}
	
	/**
	 * Add a part at a particular position
	 */	
	private void add(LayoutPart newChild, int idx) {
		IPresentablePart position = getPresentablePartAtIndex(idx);
		LayoutPart targetPart = getPaneFor(position);
		int childIdx = children.indexOf(targetPart);
		
		if (childIdx == -1) {
			children.add(newChild);
		} else {
			children.add(idx, newChild);
		}
		
		if (active) {
			showPart(newChild, position);
		}
	}

	/**
	 * See IVisualContainer#add
	 */
	public void add(LayoutPart child) {
		children.add(child);
		if (active) {
			showPart(child, null);
		}
	}
	
	/**
	 * See ILayoutContainer::allowBorder
	 *
	 * There is already a border around the tab
	 * folder so no need for one from the parts.
	 */
	public boolean allowsBorder() {
		// @issue need to support old look even if a theme is set (i.e. show border
		//   even when only one item) -- separate theme attribute, or derive this
		//   from existing attributes?
		// @issue this says to show the border only if there are no items, but 
		//   in this case the folder should not be visible anyway
//		if (tabThemeDescriptor != null)
//			return (mapTabToPart.size() < 1);
//		return mapTabToPart.size() <= 1;
		return false;
	}

	/**
	 * Returns the layout part for the given presentable part
	 * 
	 * @param toFind
	 * @return
	 */
	private LayoutPart getLayoutPart(IPresentablePart toFind) {
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart next = (LayoutPart)iter.next();
			
			if (next.getPresentablePart() == toFind) {
				return next;
			}
		}
		
		return null;
	}
	
	private IPresentablePart getPresentablePartAtIndex(int idx) {
		List presentableParts = getPresentableParts();
		
		if (idx >= 0 && idx < presentableParts.size()) {
			return (IPresentablePart)presentableParts.get(idx);
		}
		
		return null;
	}
	
	/**
	 * Returns a list of IPresentablePart
	 * 
	 * @return
	 */
	private List getPresentableParts() {
		List result = new ArrayList(children.size());
		
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart part = (LayoutPart)iter.next();
			
			IPresentablePart presentablePart = part.getPresentablePart();

			if (presentablePart != null) {
				result.add(presentablePart);
			}			
		}
		
		return result;
	}
	
	public void createControl(Composite parent) {

		if (presentationSite.getPresentation() != null)
			return;
	
		presentationSite.setPresentation(new PartTabFolderPresentation(parent, 
				presentationSite, flags, page.getTheme()));

		active = true;
		
		// Add all visible children to the presentation
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart part = (LayoutPart)iter.next();
			
			showPart(part, null);
		}
		
		// Set current page.
		if (getItemCount() > 0) {
			int newPage = 0;
			if (current != null)
				newPage = indexOf(current);
			setSelection(newPage);
		}
		
		Control ctrl = getPresentation().getControl();
		
		// Add a drop target that lets us drag views directly to a particular tab
		DragUtil.addDragTarget(ctrl, new IDragOverListener() {

			public IDropTarget drag(Control currentControl, final Object draggedObject, 
					Point position, Rectangle dragRectangle) {
				
				if (!(draggedObject instanceof ViewPane)) {
					return null;
				}
				
				final ViewPane pane = (ViewPane)draggedObject;
				
				// Don't allow views to be dragged between windows
				if (pane.getWorkbenchWindow() != getWorkbenchWindow()) {
					return null;
				}

				final StackDropResult dropResult = getPresentation().dragOver(currentControl, position);
				
				if (dropResult == null) {
					return null;
				}
				
				IPresentablePart draggedControl = getPresentablePartAtIndex(dropResult.getDropIndex());
				
				// If we're dragging a pane over itself do nothing
				if (draggedControl == pane.getPresentablePart()) {
					return null;
				}
								
				return new IDropTarget() {

					public void drop() {
 						
						// Don't worry about reparenting the view if we're simply
						// rearranging tabs within this folder
						if (pane.getContainer() != PartTabFolder.this) {
							page.getActivePerspective().getPresentation().derefPart(pane);
							pane.reparent(getParent());
						} else {
							remove(pane);
						}
						
						add(pane, dropResult.getDropIndex());
						setSelection(pane);	
						pane.setFocus();
					}

					public Cursor getCursor() {
						return DragCursors.getCursor(DragCursors.CENTER);
					}

					public Rectangle getSnapRectangle() {
						return dropResult.getSnapRectangle();
					}
				};
			}
			
		});

		ctrl.setData(this);
	}
	
	/**
	 * Makes the given part visible in the presentation
	 * 
	 * @param presentablePart
	 */
	private void showPart(LayoutPart part, IPresentablePart position) {
		
		part.setContainer(this);
		
		IPresentablePart presentablePart = part.getPresentablePart();
		
		if (presentablePart == null) {
			return;
		}
		
		part.createControl(getParent());
		part.setContainer(this);
		part.moveAbove(getPresentation().getControl());
		
		presentationSite.getPresentation().addPart(presentablePart, position);
		
		if (current == null) {
			presentationSite.selectPart(presentablePart);
		}
	}

	/**
	 * See LayoutPart#dispose
	 */
	public void dispose() {

		if (!active)
			return;

		StackPresentation presentation = presentationSite.getPresentation();
		
		presentationSite.dispose();
		//presentationSite.selectPart(null);
		
		//presentationSite.setPresentation(null);
		
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart next = (LayoutPart)iter.next();
			
			next.setContainer(null);
		}

		active = false;
	}

	private StackPresentation getPresentation() {
		return presentationSite.getPresentation();
	}
	
	/**
	 * Open the tracker to allow the user to move
	 * the specified part using keyboard.
	 */
	public void openTracker(LayoutPart part) {
		DragUtil.performDrag(part, DragUtil.getDisplayBounds(part.getControl()));
	}
	
	/**
	 * Gets the presentation bounds.
	 */
	public Rectangle getBounds() {
		if (getPresentation() == null) {
			return new Rectangle(0,0,0,0);
		}
		
		return getPresentation().getControl().getBounds();		
	}

	// getMinimumHeight() added by cagatayk@acm.org 
	/**
	 * @see LayoutPart#getMinimumHeight()
	 */
	public int getMinimumHeight() {		
		if (getPresentation() == null) {
			return 0;
		}
		
		return getPresentation().computeMinimumSize().y;
	}
	
	/**
	 * See IVisualContainer#getChildren
	 */
	public LayoutPart[] getChildren() {
		return (LayoutPart[])children.toArray(new LayoutPart[children.size()]);
	}
	
	public Control getControl() {
		StackPresentation presentation = getPresentation();
		
		if (presentation == null) {
			return null;
		}
		
		return presentation.getControl();
	}
	
	/**
	 * Answer the number of children.
	 */
	public int getItemCount() {
		if (active) {
			return getPresentableParts().size();
		}
		
		return children.size();
	}
	
	/**
	 * Get the parent control.
	 */
	public Composite getParent() {
		return getControl().getParent();
	}
	
	public int getSelection() {
		if (!active)
			return 0;
		
		return getPresentableParts().indexOf(presentationSite.getCurrent());
	}

	/**
	 * Returns the visible child.
	 */
	public LayoutPart getVisiblePart() {
		if (current == null)
			return inactiveCurrent;
		return current;
	}
	
	public int indexOf(LayoutPart item) {
		return indexOf(item.getPresentablePart());
	}
	
	private int indexOf(IPresentablePart part) {
		int result = getPresentableParts().indexOf(part);
		
		if (result < 0) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * See IVisualContainer#remove
	 */
	public void remove(LayoutPart child) {
		IPresentablePart presentablePart = child.getPresentablePart();

		if (presentablePart != null) {
			presentationSite.getPresentation().removePart(presentablePart);
		}
		
		children.remove(child);

		if (active) {
			child.setContainer(null);
		}
		
		updateContainerVisibleTab();
	}
	
	
		/**
	 * Reparent a part. Also reparent visible children...
	 */
	public void reparent(Composite newParent) {
		if (!newParent.isReparentable())
			return;

		Control control = getControl();
		if ((control == null) || (control.getParent() == newParent))
			return;

		super.reparent(newParent);

		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			LayoutPart next = (LayoutPart)iter.next();
			next.reparent(newParent);
		}		
	}
	/**
	 * See IVisualContainer#replace
	 */
	public void replace(LayoutPart oldChild, LayoutPart newChild) {	
		IPresentablePart oldPart = oldChild.getPresentablePart();
		IPresentablePart newPart = newChild.getPresentablePart();
		
		int idx = children.indexOf(oldChild);
		children.add(idx, newChild);
		
		if (active) {				
			showPart(newChild, oldPart);
		}
		
		if (oldChild == inactiveCurrent) {
			setCurrentLayoutPart(newChild);
			inactiveCurrent = null;
		}
		
		remove(oldChild);		
	}
	
	/**
	 * @see IPersistable
	 */
	public IStatus restoreState(IMemento memento) {
		// Read the active tab.
		String activeTabID = memento.getString(IWorkbenchConstants.TAG_ACTIVE_PAGE_ID);
		
		// Read the page elements.
		IMemento[] children = memento.getChildren(IWorkbenchConstants.TAG_PAGE);
		if (children != null) {
			// Loop through the page elements.
			for (int i = 0; i < children.length; i++) {
				// Get the info details.
				IMemento childMem = children[i];
				String partID = childMem.getString(IWorkbenchConstants.TAG_CONTENT);
				String tabText = childMem.getString(IWorkbenchConstants.TAG_LABEL);

				IViewDescriptor descriptor = (IViewDescriptor)WorkbenchPlugin.getDefault().
					getViewRegistry().find(partID);
			
				if (descriptor != null) {
					tabText = descriptor.getLabel();
				}

				// Create the part.
				LayoutPart part = new PartPlaceholder(partID);
				add(part);
				//1FUN70C: ITPUI:WIN - Shouldn't set Container when not active
				//part.setContainer(this);
				if (partID.equals(activeTabID)) {
					// Mark this as the active part.
					inactiveCurrent = part;
				}
			}
		}
		
		Integer expanded = memento.getInteger(IWorkbenchConstants.TAG_EXPANDED);
		setState((expanded == null || expanded.intValue() != IPresentationSite.STATE_MINIMIZED) ? 
				IPresentationSite.STATE_RESTORED : IPresentationSite.STATE_MINIMIZED);
		
		return new Status(IStatus.OK, PlatformUI.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}
	/**
	 * @see IPersistable
	 */
	public IStatus saveState(IMemento memento) {

		// Save the active tab.
		if (current != null)
			memento.putString(IWorkbenchConstants.TAG_ACTIVE_PAGE_ID, current.getID());

		Iterator iter = children.iterator();
		while(iter.hasNext()) {
			LayoutPart next = (LayoutPart)iter.next();

			IMemento childMem = memento.createChild(IWorkbenchConstants.TAG_PAGE);
			
			IPresentablePart part = next.getPresentablePart();
			String tabText = "LabelNotFound"; //$NON-NLS-1$ 
			if (part != null) {
				tabText = part.getName();
			}
			childMem.putString(IWorkbenchConstants.TAG_LABEL, tabText);
			childMem.putString(IWorkbenchConstants.TAG_CONTENT, next.getID());
		}
				
		memento.putInteger(IWorkbenchConstants.TAG_EXPANDED, (presentationSite.getState() == IPresentationSite.STATE_MINIMIZED) ? 
				IPresentationSite.STATE_MINIMIZED : IPresentationSite.STATE_RESTORED);
		
		return new Status(IStatus.OK, PlatformUI.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}
	
	private void hidePart(LayoutPart part) {
		IPresentablePart presentablePart = part.getPresentablePart();
		
		if (presentablePart == null) {
			return;
		}
		
		getPresentation().removePart(presentablePart);
		if (active) {
			part.setContainer(null);
		}
	}
	
	/**
	 * Sets the presentation bounds.
	 */
	public void setBounds(Rectangle r) {
		if (getPresentation() != null) {
			getPresentation().setBounds(r);
		}
	}
	
	public void setSelection(int index) {
		if (!active)
			return;

		getPresentation().selectPart((IPresentablePart)getPresentableParts().get(index));	
	}
	
	private void setSelection(LayoutPart part) {

		if (!active)
			return;
		if (part instanceof PartPlaceholder)
			return;

		IPresentablePart presentablePart = part.getPresentablePart();
		
		if (presentablePart != null) {
			presentationSite.selectPart(presentablePart);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.IWorkbenchDropTarget#addDropTargets(java.util.Collection)
	 */
	public void addDropTargets(Collection result) {
		addDropTargets(result, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.IWorkbenchDragSource#getType()
	 */
	public int getType() {
		return VIEW;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.IWorkbenchDragSource#isDragAllowed(org.eclipse.swt.graphics.Point)
	 */
	public boolean isDragAllowed(Point point) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.IWorkbenchDropTarget#targetPartFor(org.eclipse.ui.internal.IWorkbenchDragSource)
	 */
	public LayoutPart targetPartFor(IWorkbenchDragSource dragSource) {
		return this;
	}

	/**
	 * Set the active appearence on the tab folder.
	 * @param active
	 */
	public void setActive(boolean activeState) {
		if (activeState && presentationSite.getState() == IPresentationSite.STATE_MINIMIZED) {
			setState(IPresentationSite.STATE_RESTORED);
		}
		
		getPresentation().setActive(activeState);
	}

//	/**
//	 * Sets the theme id.
//	 *
//	 * @param theme the theme id to set.
//	 */
//	public void setTheme(String theme) {
//		if ((theme != null) && (theme.length() > 0)) {
//			this.themeid = theme;
//			tabThemeDescriptor = WorkbenchThemeManager.getInstance().getTabThemeDescriptor(theme);
//		}
//	}
	
//	/**
//	 * Replace the image on the tab with the supplied image.
//	 * @param part PartPane
//	 * @param image Image
//	 */
//	private void updateImage(final PartPane part, final Image image){
//		final CTabItem item = getTab(part);
//		if(item != null){
//			UIJob updateJob = new UIJob("Tab Update"){ //$NON-NLS-1$
//				/* (non-Javadoc)
//				 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
//				 */
//				public IStatus runInUIThread(IProgressMonitor monitor) {
//					part.setImage(item,image);
//					return Status.OK_STATUS;
//				}
//			};
//			updateJob.setSystem(true);
//			updateJob.schedule();
//		}
//	}
	
	/**
	 * Indicate busy state in the supplied partPane.
	 * @param partPane PartPane.
	 */
	public void showBusy(PartPane partPane, boolean busy) {
//		updateTab(
//			partPane,
//			JFaceResources.getImage(ProgressManager.BUSY_OVERLAY_KEY));
	}
	
//	/**
//	 * Restore the part to the default.
//	 * @param partPane PartPane
//	 */
//	public void clearBusy(PartPane partPane) {
//		//updateTab(partPane,partPane.getPartReference().getTitleImage());
//	}
	
//	/**
//	 * Replace the image on the tab with the supplied image.
//	 * @param part PartPane
//	 * @param image Image
//	 */
//	private void updateTab(PartPane part, final Image image){
//		final CTabItem item = getTab(part);
//		if(item != null){
//			UIJob updateJob = new UIJob("Tab Update"){ //$NON-NLS-1$
//				/* (non-Javadoc)
//				 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
//				 */
//				public IStatus runInUIThread(IProgressMonitor monitor) {
//					item.setImage(image);
//					return Status.OK_STATUS;
//				}
//			};
//			updateJob.setSystem(true);
//			updateJob.schedule();
//		}
//			
//	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.LayoutPart#setContainer(org.eclipse.ui.internal.ILayoutContainer)
	 */
	public void setContainer(ILayoutContainer container) {
		
		super.setContainer(container);

		if (presentationSite.getState() == IPresentationSite.STATE_MAXIMIZED) {
			if (!page.isZoomed()) {
				setState(IPresentationSite.STATE_RESTORED);
			}
		}
	}

	private void setState(int newState) {
		if (newState == presentationSite.getState()) {
			return;
		}
		
		int oldState = presentationSite.getState();
		
		presentationSite.setPresentationState(newState);
		
		if (current != null) {
			if (presentationSite.getState() == IPresentationSite.STATE_MAXIMIZED) {
				((PartPane) current).doZoom();
			} else {
				WorkbenchPage page = ((PartPane) current).getPage();
				if (page.isZoomed()) {
					page.zoomOut();
				}
			
				updateControlBounds();
				
				if (oldState == IPresentationSite.STATE_MINIMIZED) {
					forceLayout();
				}
			}
		}
		
		if (presentationSite.getState() == IPresentationSite.STATE_MINIMIZED) {
			page.refreshActiveView();
		}
	}
	
	private void updateControlBounds() {
		Rectangle bounds = getPresentation().getControl().getBounds();
		int minimumHeight = getMinimumHeight();
		
		if (presentationSite.getState() == IPresentationSite.STATE_MINIMIZED && bounds.height != minimumHeight) {
			bounds.width = getMinimumWidth();
			bounds.height = minimumHeight;	
			getPresentation().setBounds(bounds);
			
			forceLayout();
		}
	}
	
	/**
	 * Forces the layout to be recomputed for all parts
	 */
	private void forceLayout() {
		PartSashContainer cont = (PartSashContainer) getContainer();
		if (cont != null) {
			LayoutTree tree = cont.getLayoutTree(); 
			tree.setBounds(getParent().getClientArea());
		}
	}
	
	public void findSashes(LayoutPart part, ViewPane.Sashes sashes) {
		ILayoutContainer container = getContainer();
		
		if (container != null) {
			container.findSashes(this, sashes);
		}
	}
	
	/**
	 * Update the container to show the correct visible tab based on the
	 * activation list.
	 * 
	 * @param org.eclipse.ui.internal.ILayoutContainer
	 */
	private void updateContainerVisibleTab() {

		LayoutPart[] parts = getChildren();
		if (parts.length < 1)
			return;

		PartPane selPart = null;
		int topIndex = 0;
		IWorkbenchPartReference sortedPartsArray[] = page.getSortedParts();
		List sortedParts = Arrays.asList(sortedPartsArray);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] instanceof PartPane) {
				IWorkbenchPartReference part =
					((PartPane) parts[i]).getPartReference();
				int index = sortedParts.indexOf(part);
				if (index >= topIndex) {
					topIndex = index;
					selPart = (PartPane) parts[i];
				}
			}
		}

		if (selPart != null) {
			//Make sure the new visible part is restored.
			//If part can't be restored an error part is created.
			selPart.getPartReference().getPart(true);
			int selIndex = indexOf(selPart);
			if (getSelection() != selIndex)
				setSelection(selIndex);
		}
	}
	
	public boolean resizesVertically() {
		return presentationSite.getState() != IPresentationSite.STATE_MINIMIZED;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.ILayoutContainer#allowsAutoFocus()
	 */
	public boolean allowsAutoFocus() {
		if (presentationSite.getState() == IPresentationSite.STATE_MINIMIZED) {
			return false;
		}
		
		ILayoutContainer parent = getContainer();
		
		if (parent != null && ! parent.allowsAutoFocus()) {
			return false;
		}
		
		return true;
	}
	
	private void setCurrentLayoutPart(LayoutPart newCurrent) {
		current = newCurrent;
		
		updateSystemMenu();
	}
	
	private void updateSystemMenu() {
		
		StackPresentation presentation = getPresentation();
		
		if (presentation == null) {
			if (paneContribution != null) {
				paneContribution.dispose();
				paneContribution = null;
			}
			return;
		}
		
		IMenuManager systemMenuManager = presentation.getSystemMenuManager();
		if (paneContribution != null) {
			systemMenuManager.remove(paneContribution);
			paneContribution.dispose();
			paneContribution = null;
		}
		
		if (PartTabFolder.this.current != null) {
			paneContribution = ((PartPane)PartTabFolder.this.current).createPaneContribution(); 
			systemMenuManager.add(paneContribution);
		}

	}
}

