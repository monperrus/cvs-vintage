/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Region;
/**
 * The AssociatedWindow is a window that is associated with another shell.
 */
public class AssociatedWindow extends Window {
	protected static final int TRACK_OUTER_TOP_RHS = 0;
	protected static final int TRACK_INNER_TOP_RHS = 1;
	protected static final int TRACK_OUTER_BOTTOM_RHS = 2;
	protected static final int TRACK_INNER_BOTTOM_RHS = 3;
	protected static final int HORIZONTAL_VISIBLE = 0;
	protected static final int ALWAYS_VISIBLE = 1;
	private Control owner;
	private ControlListener controlListener;
	private int trackStyle;
	private int moveType;
	/**
	 * Create a new instance of the receiver parented from parent and
	 * associated with the owning Composite.
	 * 
	 * @param parent
	 *            The shell this will be parented from.
	 * @param associatedControl
	 *            The Composite that the position of this window will be
	 *            associated with.
	 */
	public AssociatedWindow(Shell parent, Control associatedControl,
			int trackStyle) {
		super(parent);
		setShellStyle(SWT.NO_TRIM);
		owner = associatedControl;
		this.trackStyle = trackStyle;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		this.associate(newShell);
	}
	/**
	 * Move the shell based on the position of the owner.
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @param moveConstant
	 *            the type of movement desired.
	 */
	protected void moveShell(Shell shellToMove, int moveConstant) {
		this.moveType = moveConstant;
		if (shellToMove.isDisposed())
			return;
		Rectangle displayRect = owner.getMonitor().getClientArea();
		Point location = getLocation(shellToMove);
		Point shellSize = shellToMove.getSize();
		if (moveType == 0) {
			if (trackStyle == 0 || trackStyle == 2) {
				if (location.x + shellSize.x > displayRect.width
						&& location.x < displayRect.width)
					location.x = displayRect.width - shellSize.x;
				else if (location.x >= displayRect.width)
					location.x = location.x - shellSize.x;
			}
			shellToMove.setLocation(location);
		} else {
			Rectangle currentRect = shellToMove.getBounds();
			currentRect.x = location.x;
			currentRect.y = location.y;
			shellToMove.setBounds(getConstrainedShellBounds(currentRect));
		}
	}
	/**
	 * Get the location depending on the track style.
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @return point the location for the shell.
	 */
	private Point getLocation(Shell shellToMove) {
		switch (trackStyle) {
			case 0 :
				return getLocationOuterTopRHS(shellToMove);
			case 1 :
				return getLocationInnerTopRHS(shellToMove);
			case 2 :
				return getLocationOuterBottomRHS(shellToMove);
			case 3 :
				return getLocationInnerBottomRHS(shellToMove);
			default :
				return getLocationOuterTopRHS(shellToMove);
		}
	}
	/**
	 * Answer the location to position the receiver relative to the outer top
	 * right hand side of the owner
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @return point the locatoin for the shell.
	 */
	private Point getLocationOuterTopRHS(Shell shellToMove) {
		Point loc = owner.getDisplay().map(owner, null, 0, 0);
		Point size = owner.getSize();
		return new Point(loc.x + size.x, loc.y);
	}
	/**
	 * Answer the location to position the receiver relative to the inner top
	 * right hand side of the owner
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @return point the location for the shell.
	 */
	private Point getLocationInnerTopRHS(Shell shellToMove) {
		Point loc = owner.getDisplay().map(owner, null, 0, 0);
		Point ownerSize = owner.getSize();
		Point size = shellToMove.getSize();
		return new Point(loc.x + ownerSize.x - size.x, loc.y);
	}
	/**
	 * Answer the location to position the receiver relative to the outer
	 * bottom right hand side of the owner
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @return point the location for the shell.
	 */
	private Point getLocationOuterBottomRHS(Shell shellToMove) {
		Point loc = owner.getDisplay().map(owner, null, 0, 0);
		Point ownerSize = owner.getSize();
		Point size = shellToMove.getSize();
		return new Point(loc.x + ownerSize.x, loc.y + ownerSize.y - size.y);
	}
	/**
	 * Answer the location to position the receiver relative to the inner
	 * botoom right hand side of the owner
	 * 
	 * @param shellToMove
	 *            the shell to move.
	 * @return point the location for the shell.
	 */
	private Point getLocationInnerBottomRHS(Shell shellToMove) {
		Point loc = owner.getDisplay().map(owner, null, 0, 0);
		Point ownerSize = owner.getSize();
		Point size = shellToMove.getSize();
		return new Point(loc.x, loc.y + ownerSize.y - size.y);
	}
	/**
	 * Track the following controls location, by locating the receiver along
	 * the right hand side of the control. If the control moves the receiver
	 * should move along with it.
	 * 
	 * @param shell
	 *            the floating shell.
	 */
	private void associate(final Shell floatingShell) {
		controlListener = new ControlListener() {
			public void controlMoved(ControlEvent e) {
				moveShell(floatingShell, moveType);
			}
			public void controlResized(ControlEvent e) {
				moveShell(floatingShell, moveType);
			}
		};
		owner.addControlListener(controlListener);
		getParentShell().addControlListener(controlListener);
		Control[] c = getParentShell().getTabList();
		Control[] newTab = new Control[c.length + 1];
		System.arraycopy(c, 0, newTab, 0, c.length);
		newTab[c.length] = floatingShell;
		getParentShell().setTabList(newTab);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		
		Region shellRegion = getShell().getRegion();
		if(shellRegion != null)
			shellRegion.dispose();
		getParentShell().removeControlListener(controlListener);
		owner.removeControlListener(controlListener);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents()
	 */
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	/**
	 * Add round border to the shell.
	 * 
	 * @param shell
	 *            the shell.
	 */
	protected void addRoundBorder(final int borderSize) {
		final Shell shell = getShell();
		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				if (shell == null || shell.isDisposed())
					return;
				// 2*Border bigger than shell boundes, return
				if (2 * borderSize >= shell.getBounds().width
						|| 2 * borderSize >= shell.getBounds().height)
					return;
				GC gc = event.gc;
				Rectangle outerShellSize = shell.getClientArea();
				Rectangle innerShellSize = new Rectangle(borderSize,
						borderSize, outerShellSize.width - 2 * borderSize,
						outerShellSize.height - 2 * borderSize);
				Region r = new Region(getShell().getDisplay());
				Region region1 = getRoundCorners(outerShellSize);
				Region region2 = getRoundCorners(innerShellSize);
				
				Region oldRegion = shell.getRegion();
				
				shell.setRegion(region1);
				
				if(oldRegion != null)
					oldRegion.dispose();
				
				r.add(region1);
				r.subtract(region2);
				gc.setClipping(r);
				gc.setBackground(shell.getDisplay().getSystemColor(
						SWT.COLOR_BLACK));
				gc.fillRoundRectangle(0, 0, outerShellSize.width,
						outerShellSize.height, 5, 5);
				r.dispose();
				region2.dispose();
			}
		});
	}
	
	/**
	 * Round a defined rectangle in the shell.
	 * 
	 * @param shell
	 *            the shell.
	 * @param size
	 *            the rectangle size.
	 */
	private Region getRoundCorners(Rectangle size) {
		Shell shell = getShell();
		Region r = new Region(shell.getDisplay());
		r.add(new Rectangle(size.x, size.y, size.width, size.height));
		Region cornerRegion = new Region(shell.getDisplay());
		addTopRightCorner(cornerRegion, size);
		addBottomRightCorner(cornerRegion, size);
		addTopLeftCorner(cornerRegion, size);
		addBottomLeftCorner(cornerRegion, size);
		r.subtract(cornerRegion);
		cornerRegion.dispose();
		return r;
	}
	/**
	 * Add top right corner for rounding.
	 * 
	 * @param cornerRegion
	 *            corner region.
	 * @param shellSize
	 *            shell size.
	 */
	private void addTopRightCorner(Region cornerRegion, Rectangle shellSize) {
		cornerRegion.add(new Rectangle((shellSize.width - 5) + shellSize.x,
				0 + shellSize.y, 5, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 3) + shellSize.x,
				1 + shellSize.y, 3, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 2) + shellSize.x,
				2 + shellSize.y, 2, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 1) + shellSize.x,
				3 + shellSize.y, 1, 2));
	}
	/**
	 * Add bottom right corner for rounding.
	 * 
	 * @param cornerRegion
	 *            corner region.
	 * @param shellSize
	 *            shell size.
	 */
	private void addBottomRightCorner(Region cornerRegion, Rectangle shellSize) {
		cornerRegion.add(new Rectangle((shellSize.width - 5) + shellSize.x,
				(shellSize.height - 1) + shellSize.y, 5, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 3) + shellSize.x,
				(shellSize.height - 2) + shellSize.y, 3, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 2) + shellSize.x,
				(shellSize.height - 3) + shellSize.y, 2, 1));
		cornerRegion.add(new Rectangle((shellSize.width - 1) + shellSize.x,
				(shellSize.height - 5) + shellSize.y, 1, 2));
	}
	/**
	 * Add top left corner for rounding.
	 * 
	 * @param cornerRegion
	 *            corner region.
	 * @param shellSize
	 *            shell size.
	 */
	private void addTopLeftCorner(Region cornerRegion, Rectangle shellSize) {
		cornerRegion.add(new Rectangle(0 + shellSize.x, 0 + shellSize.y, 5, 1));
		cornerRegion.add(new Rectangle(0 + shellSize.x, 1 + shellSize.y, 3, 1));
		cornerRegion.add(new Rectangle(0 + shellSize.x, 2 + shellSize.y, 2, 1));
		cornerRegion.add(new Rectangle(0 + shellSize.x, 3 + shellSize.y, 1, 2));
	}
	/**
	 * Add bottom left corner for rounding.
	 * 
	 * @param cornerRegion
	 *            corner region.
	 * @param shellSize
	 *            shell size.
	 */
	private void addBottomLeftCorner(Region cornerRegion, Rectangle shellSize) {
		cornerRegion.add(new Rectangle(0 + shellSize.x, (shellSize.height - 5)
				+ shellSize.y, 1, 2));
		cornerRegion.add(new Rectangle(0 + shellSize.x, (shellSize.height - 3)
				+ shellSize.y, 2, 1));
		cornerRegion.add(new Rectangle(0 + shellSize.x, (shellSize.height - 2)
				+ shellSize.y, 3, 1));
		cornerRegion.add(new Rectangle(0 + shellSize.x, (shellSize.height - 1)
				+ shellSize.y, 5, 1));
	}
}