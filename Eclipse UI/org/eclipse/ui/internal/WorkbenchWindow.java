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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.commands.CommandHandlerServiceFactory;
import org.eclipse.ui.commands.ICompoundCommandHandlerService;
import org.eclipse.ui.commands.IMutableCommandHandlerService;
import org.eclipse.ui.commands.IWorkbenchCommandSupport;
import org.eclipse.ui.commands.IWorkbenchWindowCommandSupport;
import org.eclipse.ui.contexts.IWorkbenchWindowContextSupport;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.commands.ActionHandler;
import org.eclipse.ui.internal.commands.ws.WorkbenchWindowCommandSupport;
import org.eclipse.ui.internal.contexts.ws.WorkbenchWindowContextSupport;
import org.eclipse.ui.internal.misc.Assert;
import org.eclipse.ui.internal.misc.UIStats;
import org.eclipse.ui.internal.progress.AnimationItem;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSet;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

/**
 * A window within the workbench.
 */
public class WorkbenchWindow extends ApplicationWindow implements IWorkbenchWindow {

	private int number;
	private PageList pageList = new PageList();
	private PageListenerList pageListeners = new PageListenerList();
	private PerspectiveListenerListOld perspectiveListeners = new PerspectiveListenerListOld();
	private IPartDropListener partDropListener;
	private WWinPerspectiveService perspectiveService = new WWinPerspectiveService(this);
	private WWinPartService partService = new WWinPartService(this);
	private ActionPresentation actionPresentation;
	private WWinActionBars actionBars;
	private Label separator2;
	private Label separator3;
	private ToolBarManager shortcutBar;
	private ShortcutBarPart shortcutBarPart;
	private ShortcutBarPartDragDrop shortcutDND;
	private boolean updateDisabled = true;
	private boolean closing = false;
	private boolean shellActivated = false;
	private Menu perspectiveBarMenu;
	private Menu fastViewBarMenu;
	private MenuItem restoreItem;
	private AnimationItem animationItem;

	private Label noOpenPerspective;
	private Rectangle normalBounds;
	private boolean asMaximizedState = false;

	/**
	 * Bit flags indication which submenus (New, Show Views, ...) this
	 * window contains. Initially none.
	 * 
	 * @since 3.0
	 */
	private int submenus = 0x00;

	/**
	 * Object for configuring this workbench window. Lazily initialized to
	 * an instance unique to this window.
	 *  
	 * @since 3.0
	 */
	private WorkbenchWindowConfigurer windowConfigurer = null;

	// constants for shortcut bar group ids 
	static final String GRP_PAGES = "pages"; //$NON-NLS-1$
	static final String GRP_PERSPECTIVES = "perspectives"; //$NON-NLS-1$
	static final String GRP_FAST_VIEWS = "fastViews"; //$NON-NLS-1$

	// static fields for inner classes.
	static final int VGAP = 0;
	static final int CLIENT_INSET = 3;
	static final int BAR_SIZE = 23;

	/**
	 * Constant (bit mask) indicating which the Show View submenu is
	 * probably present somewhere in this window.
	 * 
	 * @see #addSubmenu
	 * @since 3.0
	 */
	public static final int SHOW_VIEW_SUBMENU = 0x01;

	/**
	 * Constant (bit mask) indicating which the Open Perspective submenu is
	 * probably present somewhere in this window.
	 * 
	 * @see #addSubmenu
	 * @since 3.0
	 */
	public static final int OPEN_PERSPECTIVE_SUBMENU = 0x02;

	/**
	 * Constant (bit mask) indicating which the New Wizard submenu is
	 * probably present somewhere in this window.
	 * 
	 * @see #addSubmenu
	 * @since 3.0
	 */
	public static final int NEW_WIZARD_SUBMENU = 0x04;

	/**
	 * Remembers that this window contains the given submenu.
	 * 
	 * @param type the type of submenu, one of: 
	 * {@link #NEW_WIZARD_SUBMENU NEW_WIZARD_SUBMENU},
	 * {@link #OPEN_PERSPECTIVE_SUBMENU OPEN_PERSPECTIVE_SUBMENU},
	 * {@link #SHOW_VIEW_SUBMENU SHOW_VIEW_SUBMENU}
	 * @see #containsSubmenu
	 * @since 3.0
	 */
	public void addSubmenu(int type) {
		submenus |= type;
	}

	/**
	 * Checks to see if this window contains the given type of submenu.
	 * 
	 * @param type the type of submenu, one of: 
	 * {@link #NEW_WIZARD_SUBMENU NEW_WIZARD_SUBMENU},
	 * {@link #OPEN_PERSPECTIVE_SUBMENU OPEN_PERSPECTIVE_SUBMENU},
	 * {@link #SHOW_VIEW_SUBMENU SHOW_VIEW_SUBMENU}
	 * @return <code>true</code> if window contains submenu,
	 * <code>false</code> otherwise
	 * @see #addSubmenu
	 * @since 3.0
	 */
	public boolean containsSubmenu(int type) {
		return ((submenus & type) != 0);
	}

	/**
	 * Constant indicating that all the actions bars should be
	 * filled.
	 * 
	 * @since 3.0
	 */
	private static final int FILL_ALL_ACTION_BARS =
		WorkbenchAdvisor.FILL_MENU_BAR
			| WorkbenchAdvisor.FILL_TOOL_BAR
			| WorkbenchAdvisor.FILL_STATUS_LINE;

	/**
	 * The layout for the workbench window's shell.
	 */
	class WorkbenchWindowLayout extends Layout {

		protected Point computeSize(
			Composite composite,
			int wHint,
			int hHint,
			boolean flushCache) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
				return new Point(wHint, hHint);

			Point result = new Point(0, 0);
			Control[] ws = composite.getChildren();
			for (int i = 0; i < ws.length; i++) {
				Control w = ws[i];
				boolean skip = false;
				if (w == getCoolBarControl()) {
					skip = true;
					result.y += BAR_SIZE;
				} else if (getShortcutBar() != null && w == getShortcutBar().getControl()) {
					skip = true;
				}
				if (!skip) {
					Point e = w.computeSize(wHint, hHint, flushCache);
					result.x = Math.max(result.x, e.x);
					result.y += e.y + VGAP;
				}
			}

			result.x += BAR_SIZE; // For shortcut bar.
			if (wHint != SWT.DEFAULT)
				result.x = wHint;
			if (hHint != SWT.DEFAULT)
				result.y = hHint;
			return result;
		}

		protected void layout(Composite composite, boolean flushCache) {
			Rectangle clientArea = composite.getClientArea();

			//Null on carbon
			if (getSeperator1() != null) {
				//Layout top seperator
				Point sep1Size = getSeperator1().computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				getSeperator1().setBounds(clientArea.x, clientArea.y, clientArea.width, sep1Size.y);
				clientArea.y += sep1Size.y;
				clientArea.height -= sep1Size.y;
			}

			int coolBarWidth = clientArea.width;

			//Layout the coolbar	
			Control coolBar = getCoolBarControl();
			if (coolBar != null) {
				if (getWindowConfigurer().getShowCoolBar()) {
					int height = BAR_SIZE;

					if (coolBarChildrenExist()) {
						Point coolBarSize =
							coolBar.computeSize(clientArea.width, SWT.DEFAULT, flushCache);
						height = coolBarSize.y;
					}
					coolBar.setBounds(clientArea.x, clientArea.y, coolBarWidth, height);
					clientArea.y += height;
					clientArea.height -= height;
				} else
					getCoolBarControl().setBounds(0, 0, 0, 0);
			}

			//Layout side seperator
			Control sep2 = getSeparator2();
			if (sep2 != null) {
				if (getWindowConfigurer().getShowCoolBar()) {
					Point sep2Size = sep2.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
					sep2.setBounds(clientArea.x, clientArea.y, clientArea.width, sep2Size.y);
					clientArea.y += sep2Size.y;
					clientArea.height -= sep2Size.y;
				} else
					sep2.setBounds(0, 0, 0, 0);
			}

			int width = BAR_SIZE;
			//Layout the progress indicator
			if (showProgressIndicator()) {
				if (animationItem != null) {
					Control progressWidget = animationItem.getControl();
					Rectangle bounds = animationItem.getImageBounds();
					int offset = 0;
					if (width > bounds.width)
						offset = (width - bounds.width) / 2;
					progressWidget.setBounds(
						offset,
						clientArea.y + clientArea.height - bounds.height,
						width,
						bounds.height);
					width = Math.max(width, bounds.width);

				}
			}

			if (getStatusLineManager() != null) {
				Control statusLine = getStatusLineManager().getControl();
				if (statusLine != null) {
					if (getWindowConfigurer().getShowStatusLine()) {

						if (getShortcutBar() != null
							&& getWindowConfigurer().getShowShortcutBar()) {
							Widget shortcutBar = getShortcutBar().getControl();
							if (shortcutBar != null && shortcutBar instanceof ToolBar) {
								ToolBar bar = (ToolBar) shortcutBar;
								if (bar.getItemCount() > 0) {
									ToolItem item = bar.getItem(0);
									width = Math.max(width, item.getWidth());
									Rectangle trim = bar.computeTrim(0, 0, width, width);
									width = trim.width;
								}
							}
						}

						Point statusLineSize =
							statusLine.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
						statusLine.setBounds(
							clientArea.x + width,
							clientArea.y + clientArea.height - statusLineSize.y,
							clientArea.width - width,
							statusLineSize.y);
						clientArea.height -= statusLineSize.y + VGAP;
					} else
						getStatusLineManager().getControl().setBounds(0, 0, 0, 0);
				}
			}

			if (getShortcutBar() != null) {
				Control shortCutBar = getShortcutBar().getControl();
				if (shortCutBar != null) {
					if (getWindowConfigurer().getShowShortcutBar()) {

						if (shortCutBar instanceof ToolBar) {
							ToolBar bar = (ToolBar) shortCutBar;
							if (bar.getItemCount() > 0) {
								ToolItem item = bar.getItem(0);
								width = item.getWidth();
								Rectangle trim = bar.computeTrim(0, 0, width, width);
								width = trim.width;
							}
						}
						shortCutBar.setBounds(clientArea.x, clientArea.y, width, clientArea.height);
						clientArea.x += width + VGAP;
						clientArea.width -= width + VGAP;
					}
				}
			} else
				getShortcutBar().getControl().setBounds(0, 0, 0, 0);

			Control sep3 = getSeparator3();

			if (sep3 != null) {
				if (getWindowConfigurer().getShowShortcutBar()) {
					Point sep3Size = sep3.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
					sep3.setBounds(clientArea.x, clientArea.y, sep3Size.x, clientArea.height);
					clientArea.x += sep3Size.x;
				} else
					sep3.setBounds(0, 0, 0, 0);
			}

			if (getClientComposite() != null)
				getClientComposite().setBounds(
					clientArea.x + CLIENT_INSET,
					clientArea.y + CLIENT_INSET + VGAP,
					clientArea.width - (2 * CLIENT_INSET),
					clientArea.height - VGAP - (2 * CLIENT_INSET));

		}
	}

	/**
	 * Creates and initializes a new workbench window.
	 * 
	 * @param number the number for the window
	 */
	public WorkbenchWindow(int number) {
		super(null);
		this.number = number;

		// Make sure there is a workbench. This call will throw
		// an exception if workbench not created yet. 
		PlatformUI.getWorkbench();

		// Setup window.
		addMenuBar();
		addCoolBar(SWT.FLAT);

		addStatusLine();
		addShortcutBar(SWT.FLAT | SWT.WRAP | SWT.VERTICAL);

		actionPresentation = new ActionPresentation(this);

		this.partDropListener = new IPartDropListener() {
			public void dragOver(PartDropEvent e) {
				WorkbenchPage page = getActiveWorkbenchPage();
				Perspective persp = page.getActivePerspective();
				PerspectivePresentation presentation = persp.getPresentation();
				presentation.onPartDragOver(e);
			}
			public void drop(PartDropEvent e) {
				WorkbenchPage page = getActiveWorkbenchPage();
				Perspective persp = page.getActivePerspective();
				PerspectivePresentation presentation = persp.getPresentation();
				presentation.onPartDrop(e);
			}
		};

		// let the application do further configuration
		getAdvisor().preWindowOpen(getWindowConfigurer());
		// Fill the action bars	
		getAdvisor().fillActionBars(
			this,
			getWindowConfigurer().getActionBarConfigurer(),
			FILL_ALL_ACTION_BARS);

		workbenchWindowCommandSupport = new WorkbenchWindowCommandSupport(this);
		workbenchWindowContextSupport = new WorkbenchWindowContextSupport(this);
		ICompoundCommandHandlerService compoundCommandHandlerService = getWorkbenchImpl().getCommandSupport().getCompoundCommandHandlerService();
		compoundCommandHandlerService.addCommandHandlerService(actionSetAndGlobalActionCommandHandlerService);
	}

	private Map actionSetHandlersByCommandId = new HashMap();
	private Map globalActionHandlersByCommandId = new HashMap();
	private IMutableCommandHandlerService actionSetAndGlobalActionCommandHandlerService = CommandHandlerServiceFactory.getMutableCommandHandlerService();
	
	void registerActionSets(IActionSet[] actionSets) {
		actionSetHandlersByCommandId.clear();
		
		for (int i = 0; i < actionSets.length; i++)
			if (actionSets[i] instanceof PluginActionSet) {
				PluginActionSet pluginActionSet = (PluginActionSet) actionSets[i];
				IAction[] pluginActions = pluginActionSet.getPluginActions();

				for (int j = 0; j < pluginActions.length; j++) {
					IAction pluginAction = pluginActions[j];
					String commandId = pluginAction.getActionDefinitionId();

					if (commandId != null)			
						actionSetHandlersByCommandId.put(commandId, new ActionHandler(pluginAction));
				}
			}
			
		setHandlersByCommandId();
	}

	void registerGlobalAction(IAction globalAction) {		
		String commandId = globalAction.getActionDefinitionId();

		if (commandId != null)
			globalActionHandlersByCommandId.put(commandId, new ActionHandler(globalAction));
	
		setHandlersByCommandId();
	}

	void setHandlersByCommandId() {
		Map handlersByCommandId = new HashMap();
		handlersByCommandId.putAll(actionSetHandlersByCommandId);
		handlersByCommandId.putAll(globalActionHandlersByCommandId);
		actionSetAndGlobalActionCommandHandlerService.setHandlersByCommandId(handlersByCommandId);
	}	
	
	/*
	 * Adds an listener to the part service.
	 */
	public void addPageListener(IPageListener l) {
		pageListeners.addPageListener(l);
	}
	/*
	 * Adds an listener to the perspective service.
	 *
	 * NOTE: Internally, please use getPerspectiveService instead.
	 */
	public void addPerspectiveListener(org.eclipse.ui.IPerspectiveListener l) {
		perspectiveListeners.addPerspectiveListener(l);
	}
	/**
	 * add a shortcut for the page.
	 */
	/* package */
	void addPerspectiveShortcut(IPerspectiveDescriptor perspective, WorkbenchPage page) {
		SetPagePerspectiveAction action = new SetPagePerspectiveAction(perspective, page);
		shortcutBar.appendToGroup(GRP_PERSPECTIVES, action);
		shortcutBar.update(false);
	}
	/**
	 * Configures this window to have a shortcut bar.
	 * Does nothing if it already has one.
	 * This method must be called before this window's shell is created.
	 */
	protected void addShortcutBar(int style) {
		if ((getShell() == null) && (shortcutBar == null)) {
			shortcutBar = new ToolBarManager(style);
			shortcutBar.add(new PerspectiveContributionItem(this));
			shortcutBar.add(new Separator(WorkbenchWindow.GRP_PAGES));
			shortcutBar.add(new Separator(WorkbenchWindow.GRP_PERSPECTIVES));
			shortcutBar.add(new Separator(WorkbenchWindow.GRP_FAST_VIEWS));
			shortcutBar.add(new ShowFastViewContribution(this));
		}
	}
	/**
	 * Close the window.
	 * 
	 * Assumes that busy cursor is active.
	 */
	private boolean busyClose() {
		// Whether the window was actually closed or not
		boolean windowClosed = false;

		// Setup internal flags to indicate window is in
		// progress of closing and no update should be done.
		closing = true;
		updateDisabled = true;

		try {
			// Only do the check if it is OK to close if we are not closing
			// via the workbench as the workbench will check this itself.
			Workbench workbench = getWorkbenchImpl();
			int count = workbench.getWorkbenchWindowCount();
			if (!workbench.isClosing() && count <= 1) {
				windowClosed = workbench.close();
			} else {
				if (okToClose()) {
					windowClosed = hardClose();
				}
			}
		} finally {
			if (!windowClosed) {
				// Reset the internal flags if window was not closed.
				closing = false;
				updateDisabled = false;
			}
		}

		return windowClosed;
	}
	/**
	 * Opens a new page. Assumes that busy cursor is active.
	 * <p>
	 * <b>Note:</b> Since release 2.0, a window is limited to contain at most
	 * one page. If a page exist in the window when this method is used, then
	 * another window is created for the new page.  Callers are strongly
	 * recommended to use the <code>IWorkbench.openPerspective</code> APIs to
	 * programmatically show a perspective.
	 * </p>
	 */
	protected IWorkbenchPage busyOpenPage(String perspID, IAdaptable input)
		throws WorkbenchException {
		IWorkbenchPage newPage = null;

		if (pageList.isEmpty()) {
			newPage = new WorkbenchPage(this, perspID, input);
			pageList.add(newPage);
			firePageOpened(newPage);
			setActivePage(newPage);
		} else {
			IWorkbenchWindow window = getWorkbench().openWorkbenchWindow(perspID, input);
			newPage = window.getActivePage();
		}

		return newPage;
	}
	/**
	 * @see Window
	 */
	public int open() {
		int result = super.open();
		getWorkbenchImpl().fireWindowOpened(this);
		getAdvisor().postWindowOpen(getWindowConfigurer());
		return result;
	}

	/* (non-Javadoc)
	 * Method declared on Window.
	 */
	protected boolean canHandleShellCloseEvent() {
		if (!super.canHandleShellCloseEvent()) {
			return false;
		}
		// let the advisor veto the user's explicit request to close the window
		return getAdvisor().preWindowShellClose(getWindowConfigurer());
	}

	/**
	 * @see IWorkbenchWindow
	 */
	public boolean close() {
		final boolean[] ret = new boolean[1];
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				ret[0] = busyClose();
			}
		});
		return ret[0];
	}

	protected boolean isClosing() {
		return closing || getWorkbenchImpl().isClosing();
	}
	/**
	 * Return whether or not the coolbar layout is locked.
	 */
	protected boolean isCoolBarLocked() {
		return getCoolBarManager().getLockLayout();
	}

	/**
	 * Close all of the pages.
	 */
	private void closeAllPages() {
		// Deactivate active page.
		setActivePage(null);

		// Clone and deref all so that calls to getPages() returns
		// empty list (if call by pageClosed event handlers)
		PageList oldList = pageList;
		pageList = new PageList();

		// Close all.
		Iterator enum = oldList.iterator();
		while (enum.hasNext()) {
			WorkbenchPage page = (WorkbenchPage) enum.next();
			firePageClosed(page);
			page.dispose();
		}
		if (!closing)
			showEmptyWindowMessage();
	}
	/**
	 * Save and close all of the pages.
	 */
	public void closeAllPages(boolean save) {
		if (save) {
			boolean ret = saveAllPages(true);
			if (!ret)
				return;
		}
		closeAllPages();
	}
	/**
	 * closePerspective method comment.
	 */
	protected boolean closePage(IWorkbenchPage in, boolean save) {
		// Validate the input.
		if (!pageList.contains(in))
			return false;
		WorkbenchPage oldPage = (WorkbenchPage) in;

		// Save old perspective.
		if (save && oldPage.isSaveNeeded()) {
			if (!oldPage.saveAllEditors(true))
				return false;
		}

		// If old page is activate deactivate.
		boolean oldIsActive = (oldPage == getActiveWorkbenchPage());
		if (oldIsActive)
			setActivePage(null);

		// Close old page. 
		pageList.remove(oldPage);
		firePageClosed(oldPage);
		oldPage.dispose();

		// Activate new page.
		if (oldIsActive) {
			IWorkbenchPage newPage = pageList.getNextActive();
			if (newPage != null)
				setActivePage(newPage);
		}
		if (!closing && pageList.isEmpty())
			showEmptyWindowMessage();
		return true;
	}
	private void showEmptyWindowMessage() {
		Composite parent = getClientComposite();
		if (noOpenPerspective == null) {
			noOpenPerspective = new Label(parent, SWT.NONE);
			noOpenPerspective.setText(WorkbenchMessages.getString("WorkbenchWindow.noPerspective")); //$NON-NLS-1$
			noOpenPerspective.setBounds(parent.getClientArea());
		}
	}
	/**
	 * Sets the ApplicationWindows's content layout.
	 * This vertical layout supports a fixed size Toolbar area, a separator line,
	 * the variable size content area,
	 * and a fixed size status line.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setLayout(getLayout());
		shell.setSize(800, 600);
		separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		createShortcutBar(shell);
		createProgressIndicator(shell);
		separator3 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);

		WorkbenchHelp.setHelp(shell, IHelpContextIds.WORKBENCH_WINDOW);

		trackShellActivation(shell);
		trackShellResize(shell);

		// If the user clicks on toolbar, status bar, or shortcut bar
		// hide the fast view.
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				WorkbenchPage currentPage = getActiveWorkbenchPage();
				if (currentPage != null) {
					if (event.type == SWT.MouseDown) {
						if (event.widget instanceof ToolBar) {
							// Ignore mouse down on actual tool bar buttons
							Point pt = new Point(event.x, event.y);
							ToolBar toolBar = (ToolBar) event.widget;
							if (toolBar.getItem(pt) != null)
								return;
						}
						currentPage.toggleFastView(null);
					}
				}
			}
		};
		getCoolBarControl().addListener(SWT.MouseDown, listener);
		Control[] children = ((Composite) getStatusLineManager().getControl()).getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null)
				children[i].addListener(SWT.MouseDown, listener);
		}
		getShortcutBar().getControl().addListener(SWT.MouseDown, listener);
	}
	/**
	 * Create the shortcut toolbar control
	 */
	private void createShortcutBar(Shell shell) {
		// Create control.
		if (shortcutBar == null)
			return;
		shortcutBar.createControl(shell);

		// Define shortcut part.  This is for drag and drop.
		shortcutBarPart = new ShortcutBarPart(shortcutBar);

		// Enable drag and drop.
		enableDragShortcutBarPart();

		// Add right mouse button support.
		ToolBar tb = shortcutBar.getControl();
		tb.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MenuDetect) {
					showShortcutBarPopup(new Point(event.x, event.y));
				}
			}
		});
	}

	/* (non-Javadoc)
	 * Method declared on ApplicationWindow.
	 */
	protected MenuManager createMenuManager() {
		final MenuManager result = super.createMenuManager();
		return result;
	}
	/**
	 * Enables fast view icons to be dragged and dropped using the given IPartDropListener.
	 */
	/*package*/
	void enableDragShortcutBarPart() {
		Control control = shortcutBarPart.getControl();
		if (control != null && shortcutDND == null) {
			// Only one ShortcutBarPartDragDrop per WorkbenchWindow.
			shortcutDND = new ShortcutBarPartDragDrop(shortcutBarPart, control);
			// Add the listener only once.
			shortcutDND.addDropListener(partDropListener);
		}
	}
	/**
	 * Returns the shortcut for a page.
	 */
	/* protected */
	IContributionItem findPerspectiveShortcut(
		IPerspectiveDescriptor perspective,
		WorkbenchPage page) {
		IContributionItem[] array = shortcutBar.getItems();
		int length = array.length;
		for (int i = 0; i < length; i++) {
			IContributionItem item = array[i];
			if (item instanceof ActionContributionItem) {
				IAction action = ((ActionContributionItem) item).getAction();
				if (action instanceof SetPagePerspectiveAction) {
					SetPagePerspectiveAction sp = (SetPagePerspectiveAction) action;
					if (sp.handles(perspective, page))
						return item;
				}
			}
		}
		return null;
	}
	/**
	 * Fires page activated
	 */
	private void firePageActivated(IWorkbenchPage page) {
		pageListeners.firePageActivated(page);
		partService.pageActivated(page);
	}
	/**
	 * Fires page closed
	 */
	private void firePageClosed(IWorkbenchPage page) {
		pageListeners.firePageClosed(page);
		partService.pageClosed(page);
	}
	/**
	 * Fires page opened
	 */
	private void firePageOpened(IWorkbenchPage page) {
		pageListeners.firePageOpened(page);
		partService.pageOpened(page);
	}
	/**
	 * Fires perspective activated
	 */
	void firePerspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		perspectiveListeners.firePerspectiveActivated(page, perspective);
		perspectiveService.firePerspectiveActivated(page, perspective);
	}
	/**
	 * Fires perspective changed
	 */
	void firePerspectiveChanged(
		IWorkbenchPage page,
		IPerspectiveDescriptor perspective,
		String changeId) {
		perspectiveListeners.firePerspectiveChanged(page, perspective, changeId);
		perspectiveService.firePerspectiveChanged(page, perspective, changeId);
	}
	/**
	 * Fires perspective closed
	 */
	void firePerspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		perspectiveService.firePerspectiveClosed(page, perspective);
	}
	/**
	 * Fires perspective opened
	 */
	void firePerspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		perspectiveService.firePerspectiveOpened(page, perspective);
	}
	/**
	 * Returns the action bars for this window.
	 */
	public WWinActionBars getActionBars() {
		if (actionBars == null) {
			actionBars = new WWinActionBars(this);
		}
		return actionBars;
	}

	/**
	 * Returns the active page.
	 *
	 * @return the active page
	 */
	public IWorkbenchPage getActivePage() {
		return pageList.getActive();
	}
	/**
	 * Returns the active workbench page.
	 *
	 * @return the active workbench page
	 */
	/* package */
	WorkbenchPage getActiveWorkbenchPage() {
		return pageList.getActive();
	}
	/**
	 * Get the workbench client area.
	 */
	protected Composite getClientComposite() {
		return (Composite) getContents();
	}
	/**
	 * Answer the menu manager for this window.
	 */
	public MenuManager getMenuManager() {
		return getMenuBarManager();
	}
	/**
	 * Returns the number.  This corresponds to a page number in a window or a
	 * window number in the workbench.
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * Returns an array of the pages in the workbench window.
	 *
	 * @return an array of pages
	 */
	public IWorkbenchPage[] getPages() {
		return pageList.getPages();
	}
	/**
	 * @see IWorkbenchWindow
	 */
	public IPartService getPartService() {
		return partService;
	}

	/**
	 * Returns the layout for the shell.
	 * 
	 * @return the layout for the shell
	 */
	protected Layout getLayout() {
		return new WorkbenchWindowLayout();
	}

	/**
	 * @see IWorkbenchWindow
	 */
	public IPerspectiveService getPerspectiveService() {
		return perspectiveService;
	}

	/**
	 * Returns the separator2 control.
	 */
	protected Label getSeparator2() {
		return separator2;
	}

	/**
	 * Returns the separator3 control.
	 */
	protected Label getSeparator3() {
		return separator3;
	}

	/**
	 * @see IWorkbenchWindow
	 */
	public ISelectionService getSelectionService() {
		return partService.getSelectionService();
	}
	/**
	 * Returns <code>true</code> when the window's shell
	 * is activated, <code>false</code> when it's shell is
	 * deactivated
	 * 
	 * @return boolean <code>true</code> when shell activated,
	 * 		<code>false</code> when shell deactivated
	 */
	public boolean getShellActivated() {
		return shellActivated;
	}
	/**
	 * Returns the shortcut bar.
	 */
	public ToolBarManager getShortcutBar() {
		return shortcutBar;
	}
	/**
	 * Returns the PartDragDrop for the shortcut bar part.
	 */
	/*package*/
	ShortcutBarPartDragDrop getShortcutDND() {
		return shortcutDND;
	}

	/**
	 * Returns the status line manager for this window (if it has one).
	 *
	 * @return the status line manager, or <code>null</code> if
	 *   this window does not have a status line
	 * @see #addStatusLine
	 */
	public StatusLineManager getStatusLineManager() {
		return super.getStatusLineManager();
	}

	/**
	 * @see IWorkbenchWindow
	 */
	public IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}
	public String getToolbarLabel(String actionSetId) {
		ActionSetRegistry registry = WorkbenchPlugin.getDefault().getActionSetRegistry();
		IActionSetDescriptor actionSet = registry.findActionSet(actionSetId);
		if (actionSet != null) {
			return actionSet.getLabel();
		} else {
			if (IWorkbenchActionConstants.TOOLBAR_FILE.equalsIgnoreCase(actionSetId))
				return WorkbenchMessages.getString("WorkbenchWindow.FileToolbar"); //$NON-NLS-1$
			if (IWorkbenchActionConstants.TOOLBAR_NAVIGATE.equalsIgnoreCase(actionSetId))
				return WorkbenchMessages.getString("WorkbenchWindow.NavigateToolbar"); //$NON-NLS-1$
		}
		return null;
	}
	/**
	 * Unconditionally close this window. Assumes the proper
	 * flags have been set correctly (e.i. closing and updateDisabled)
	 */
	private boolean hardClose() {
		boolean result;
		try {
			// Clear the action sets, fix for bug 27416.
			actionPresentation.clearActionSets();
			closeAllPages();
			// let the application do further deconfiguration
			getAdvisor().postWindowClose(getWindowConfigurer());
			getWorkbenchImpl().fireWindowClosed(this);
		} finally {
			result = super.close();
		}
		return result;
	}
	/**
	 * @see IWorkbenchWindow
	 */
	public boolean isApplicationMenu(String menuID) {
		// delegate this question to the workbench advisor
		return getAdvisor().isApplicationMenu(getWindowConfigurer(), menuID);
	}

	/**
	 * Return whether or not the given id matches the id of the coolitems that
	 * the application creates.
	 */
	/* package */
	boolean isWorkbenchCoolItemId(String id) {
		return windowConfigurer.containsCoolItem(id);
	}
	/**
	 * Locks/unlocks the CoolBar for the workbench.
	 * 
	 * @param lock whether the CoolBar should be locked or unlocked
	 */
	/* package */
	void lockCoolBar(boolean lock) {
		getCoolBarManager().setLockLayout(lock);
	}
	/**
	 * Called when this window is about to be closed.
	 *
	 * Subclasses may overide to add code that returns <code>false</code> 
	 * to prevent closing under certain conditions.
	 */
	public boolean okToClose() {
		// Save all of the editors.
		if (!getWorkbenchImpl().isClosing())
			if (!saveAllPages(true))
				return false;
		return true;
	}
	/**
	 * Opens a new page.
	 * <p>
	 * <b>Note:</b> Since release 2.0, a window is limited to contain at most
	 * one page. If a page exist in the window when this method is used, then
	 * another window is created for the new page.  Callers are strongly
	 * recommended to use the <code>IWorkbench.openPerspective</code> APIs to
	 * programmatically show a perspective.
	 * </p>
	 */
	public IWorkbenchPage openPage(final String perspId, final IAdaptable input)
		throws WorkbenchException {
		Assert.isNotNull(perspId);

		// Run op in busy cursor.
		final Object[] result = new Object[1];
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				try {
					result[0] = busyOpenPage(perspId, input);
				} catch (WorkbenchException e) {
					result[0] = e;
				}
			}
		});

		if (result[0] instanceof IWorkbenchPage)
			return (IWorkbenchPage) result[0];
		else if (result[0] instanceof WorkbenchException)
			throw (WorkbenchException) result[0];
		else
			throw new WorkbenchException(WorkbenchMessages.getString("WorkbenchWindow.exceptionMessage")); //$NON-NLS-1$
	}
	/**
	 * Opens a new page. 
	 * <p>
	 * <b>Note:</b> Since release 2.0, a window is limited to contain at most
	 * one page. If a page exist in the window when this method is used, then
	 * another window is created for the new page.  Callers are strongly
	 * recommended to use the <code>IWorkbench.openPerspective</code> APIs to
	 * programmatically show a perspective.
	 * </p>
	 */
	public IWorkbenchPage openPage(IAdaptable input) throws WorkbenchException {
		String perspId = getWorkbenchImpl().getPerspectiveRegistry().getDefaultPerspective();
		return openPage(perspId, input);
	}

	/*
	 * Removes an listener from the part service.
	 */
	public void removePageListener(IPageListener l) {
		pageListeners.removePageListener(l);
	}
	/*
	 * Removes an listener from the perspective service.
	 *
	 * NOTE: Internally, please use getPerspectiveService instead.
	 */
	public void removePerspectiveListener(org.eclipse.ui.IPerspectiveListener l) {
		perspectiveListeners.removePerspectiveListener(l);
	}
	/**
	 * Remove the shortcut for a page.
	 */
	/* package */
	void removePerspectiveShortcut(IPerspectiveDescriptor perspective, WorkbenchPage page) {
		IContributionItem item = findPerspectiveShortcut(perspective, page);
		if (item != null) {
			shortcutBar.remove(item);
			shortcutBar.update(false);
		}
	}
	private IStatus unableToRestorePage(IMemento pageMem) {
		String pageName = pageMem.getString(IWorkbenchConstants.TAG_LABEL);
		if (pageName == null)
			pageName = ""; //$NON-NLS-1$
		return new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, WorkbenchMessages.format("WorkbenchWindow.unableToRestorePerspective", new String[] { pageName }), //$NON-NLS-1$
		null);
	}
	/**
	 * @see IPersistable.
	 */
	public IStatus restoreState(IMemento memento, IPerspectiveDescriptor activeDescriptor) {
		Assert.isNotNull(getShell());

		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, WorkbenchMessages.getString("WorkbenchWindow.problemsRestoringWindow"), null); //$NON-NLS-1$

		// Read window's bounds and state.
		Rectangle displayBounds = getShell().getDisplay().getBounds();
		Rectangle shellBounds = new Rectangle(0, 0, 0, 0);
		Integer bigInt;
		bigInt = memento.getInteger(IWorkbenchConstants.TAG_X);
		shellBounds.x = bigInt == null ? 0 : bigInt.intValue();
		bigInt = memento.getInteger(IWorkbenchConstants.TAG_Y);
		shellBounds.y = bigInt == null ? 0 : bigInt.intValue();
		bigInt = memento.getInteger(IWorkbenchConstants.TAG_WIDTH);
		shellBounds.width = bigInt == null ? 0 : bigInt.intValue();
		bigInt = memento.getInteger(IWorkbenchConstants.TAG_HEIGHT);
		shellBounds.height = bigInt == null ? 0 : bigInt.intValue();
		if (!shellBounds.isEmpty()) {
			if (!shellBounds.intersects(displayBounds)) {
				Rectangle clientArea = getShell().getDisplay().getClientArea();
				shellBounds.x = clientArea.x;
				shellBounds.y = clientArea.y;
			}
			getShell().setBounds(shellBounds);
		}
		if ("true".equals(memento.getString(IWorkbenchConstants.TAG_MAXIMIZED))) { //$NON-NLS-1$
			getShell().setMaximized(true);
		}
		if ("true".equals(memento.getString(IWorkbenchConstants.TAG_MINIMIZED))) { //$NON-NLS-1$
			//		getShell().setMinimized(true);
		}

		// Restore the cool bar order by creating all the tool bar contribution items
		// This needs to be done before pages are created to ensure proper canonical creation
		// of cool items
		if (getCoolBarManager() != null) {
			CoolBarManager coolBarMgr = getCoolBarManager();
			IMemento coolBarMem = memento.getChild(IWorkbenchConstants.TAG_COOLBAR_LAYOUT);
			if (coolBarMem != null) {
				// Check if the layout is locked
				Integer lockedInt = coolBarMem.getInteger(IWorkbenchConstants.TAG_LOCKED);
				if ((lockedInt != null) && (lockedInt.intValue() == 1)) {
					coolBarMgr.setLockLayout(true);
				}else {
					coolBarMgr.setLockLayout(false);
				}
				// The new layout of the cool bar manager
				ArrayList layout = new ArrayList();
				// Traverse through all the cool item in the memento
				IMemento contributionMems[] =
					coolBarMem.getChildren(IWorkbenchConstants.TAG_COOLITEM);
				for (int i = 0; i < contributionMems.length; i++) {
					IMemento contributionMem = contributionMems[i];
					String type = contributionMem.getString(IWorkbenchConstants.TAG_ITEM_TYPE);
					String id = contributionMem.getString(IWorkbenchConstants.TAG_ID);
					IContributionItem newItem = null;
					if (type.equals(IWorkbenchConstants.TAG_TYPE_SEPARATOR)) {
						if (id != null) {
							newItem = new Separator(id);
						} else {
							newItem = new Separator();
						}
					} else if (type.equals(IWorkbenchConstants.TAG_TYPE_GROUPMARKER)) {
						newItem = new GroupMarker(id);
					} else if (type.equals(IWorkbenchConstants.TAG_TYPE_TOOLBARCONTRIBUTION)) {

						// Get Width and height
						Integer width = contributionMem.getInteger(IWorkbenchConstants.TAG_ITEM_X);
						Integer height = contributionMem.getInteger(IWorkbenchConstants.TAG_ITEM_Y);
						// Look for the object in the current cool bar manager
						IContributionItem oldItem = coolBarMgr.find(id);
						// If a tool bar contribution item already exists for this id then use the old object
						if (oldItem instanceof ToolBarContributionItem) {
							newItem = (ToolBarContributionItem) oldItem;
						} else {
							newItem =
								new ToolBarContributionItem(
										new ToolBarManager(coolBarMgr.getStyle()),
										id);
							// make it invisible by default
							newItem.setVisible(false);
							// Need to add the item to the cool bar manager so that its canonical order can be preserved
							IContributionItem refItem =
								findAlphabeticalOrder(
										IWorkbenchActionConstants.MB_ADDITIONS,
										id,
										coolBarMgr);
							coolBarMgr.insertAfter(refItem.getId(), newItem);
						}
						// Set the current height and width
						if (width != null) {
							((ToolBarContributionItem) newItem).setCurrentWidth(width.intValue());
						}
						if (height != null) {
							((ToolBarContributionItem) newItem).setCurrentHeight(height.intValue());
						}
					}
					// Add new item into cool bar manager
					if (newItem != null) {
						layout.add(newItem);
						newItem.setParent(coolBarMgr);
						coolBarMgr.markDirty();
					}
				}
				// Set the cool bar layout to the given layout.
				coolBarMgr.setLayout(layout);
			}
		}

		// Recreate each page in the window. 
		IWorkbenchPage newActivePage = null;
		IMemento[] pageArray = memento.getChildren(IWorkbenchConstants.TAG_PAGE);
		for (int i = 0; i < pageArray.length; i++) {
			IMemento pageMem = pageArray[i];
			String strFocus = pageMem.getString(IWorkbenchConstants.TAG_FOCUS);
			if (strFocus == null || strFocus.length() == 0)
				continue;

			// Get the input factory.
			IMemento inputMem = pageMem.getChild(IWorkbenchConstants.TAG_INPUT);
			String factoryID = inputMem.getString(IWorkbenchConstants.TAG_FACTORY_ID);
			if (factoryID == null) {
				WorkbenchPlugin.log("Unable to restore page - no input factory ID."); //$NON-NLS-1$
				result.add(unableToRestorePage(pageMem));
				continue;
			}
			IAdaptable input;
			try {
				UIStats.start(UIStats.RESTORE_WORKBENCH, "WorkbenchPageFactory"); //$NON-NLS-1$
				IElementFactory factory = PlatformUI.getWorkbench().getElementFactory(factoryID);
				if (factory == null) {
					WorkbenchPlugin.log("Unable to restore page - cannot instantiate input factory: " + factoryID); //$NON-NLS-1$
					result.add(unableToRestorePage(pageMem));
					continue;
				}

				// Get the input element.
				input = factory.createElement(inputMem);
				if (input == null) {
					WorkbenchPlugin.log("Unable to restore page - cannot instantiate input element: " + factoryID); //$NON-NLS-1$
					result.add(unableToRestorePage(pageMem));
					continue;
				}
			} finally {
				UIStats.end(UIStats.RESTORE_WORKBENCH, "WorkbenchPageFactory"); //$NON-NLS-1$
			}
			// Open the perspective.
			WorkbenchPage newPage = null;
			try {
				newPage = new WorkbenchPage(this, input);
				result.add(newPage.restoreState(pageMem, activeDescriptor));
				pageList.add(newPage);
				firePageOpened(newPage);
			} catch (WorkbenchException e) {
				WorkbenchPlugin.log("Unable to restore perspective - constructor failed."); //$NON-NLS-1$
				result.add(e.getStatus());
				continue;
			}

			if (strFocus != null && strFocus.length() > 0)
				newActivePage = newPage;
		}

		// If there are no pages create a default.
		if (pageList.isEmpty()) {
			try {
				String defPerspID =
					getWorkbenchImpl().getPerspectiveRegistry().getDefaultPerspective();
				WorkbenchPage newPage =
					new WorkbenchPage(this, defPerspID, getAdvisor().getDefaultWindowInput());
				pageList.add(newPage);
				firePageOpened(newPage);
			} catch (WorkbenchException e) {
				WorkbenchPlugin.log("Unable to create default perspective - constructor failed."); //$NON-NLS-1$
				result.add(e.getStatus());
				String productName = WorkbenchPlugin.getDefault().getProductName();
				if (productName == null) {
					productName = ""; //$NON-NLS-1$
				}
				getShell().setText(productName);
			}
		}

		// Set active page.
		if (newActivePage == null)
			newActivePage = (IWorkbenchPage) pageList.getNextActive();

		setActivePage(newActivePage);

		return result;
	}

	/**
	 * Returns the contribution item that the given contribution item should be inserted after.
	 * 
	 * @param startId the location to start looking alphabetically.
	 * @param itemId the target item id.
	 * @param mgr the contribution manager.
	 * @return the contribution item that the given items should be returned after.
	 */
	private IContributionItem findAlphabeticalOrder(
		String startId,
		String itemId,
		IContributionManager mgr) {
		IContributionItem[] items = mgr.getItems();
		int insertIndex = 0;

		// look for starting point
		while (insertIndex < items.length) {
			IContributionItem item = items[insertIndex];
			if (item.getId().equals(startId))
				break;
			++insertIndex;
		}

		// Find the index that this item should be inserted in
		for (int i = insertIndex + 1; i < items.length; i++) {
			IContributionItem item = (IContributionItem) items[i];
			String testId = item.getId();

			if (item.isGroupMarker())
				break;

			if (itemId != null) {
				if (itemId.compareTo(testId) < 1)
					break;
			}
			insertIndex = i;
		}

		return items[insertIndex];
	}

	/* (non-Javadoc)
	 * Method declared on IRunnableContext.
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable)
		throws InvocationTargetException, InterruptedException {
		IWorkbenchCommandSupport commandSupport = getWorkbench().getCommandSupport();
		final boolean keyFilterEnabled = commandSupport.isKeyFilterEnabled();

		ToolBarManager shortcutBar = getShortcutBar();
		Control shortcutBarControl = null;
		if (shortcutBar != null)
			shortcutBarControl = shortcutBar.getControl();
		boolean shortcutbarWasEnabled = false;
		if (shortcutBarControl != null)
			shortcutbarWasEnabled = shortcutBarControl.getEnabled();

		try {
			if (shortcutBarControl != null && !shortcutBarControl.isDisposed())
				shortcutBarControl.setEnabled(false);

			if (keyFilterEnabled)
				commandSupport.disableKeyFilter();

			super.run(fork, cancelable, runnable);
		} finally {
			if (shortcutBarControl != null && !shortcutBarControl.isDisposed())
				shortcutBarControl.setEnabled(shortcutbarWasEnabled);

			if (keyFilterEnabled)
				commandSupport.enableKeyFilter();
		}
	}
	/**
	 * Save all of the pages.  Returns true if the operation succeeded.
	 */
	private boolean saveAllPages(boolean bConfirm) {
		boolean bRet = true;
		Iterator enum = pageList.iterator();
		while (bRet && enum.hasNext()) {
			WorkbenchPage page = (WorkbenchPage) enum.next();
			bRet = page.saveAllEditors(bConfirm);
		}
		return bRet;
	}
	/**
	 * @see IPersistable
	 */
	public IStatus saveState(IMemento memento) {

		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, WorkbenchMessages.getString("WorkbenchWindow.problemsSavingWindow"), null); //$NON-NLS-1$

		// Save the window's state and bounds.
		if (getShell().getMaximized() || asMaximizedState) {
			memento.putString(IWorkbenchConstants.TAG_MAXIMIZED, "true"); //$NON-NLS-1$
		}
		if (getShell().getMinimized()) {
			memento.putString(IWorkbenchConstants.TAG_MINIMIZED, "true"); //$NON-NLS-1$
		}
		if (normalBounds == null) {
			normalBounds = getShell().getBounds();
		}
		memento.putInteger(IWorkbenchConstants.TAG_X, normalBounds.x);
		memento.putInteger(IWorkbenchConstants.TAG_Y, normalBounds.y);
		memento.putInteger(IWorkbenchConstants.TAG_WIDTH, normalBounds.width);
		memento.putInteger(IWorkbenchConstants.TAG_HEIGHT, normalBounds.height);

		/// Save the order of the cool bar contribution items
		if (getCoolBarManager() != null) {
			getCoolBarManager().refresh();
			IMemento coolBarMem = memento.createChild(IWorkbenchConstants.TAG_COOLBAR_LAYOUT);
			if (getCoolBarManager().getLockLayout() == true) {
				coolBarMem.putInteger(IWorkbenchConstants.TAG_LOCKED,1);
			}else {
				coolBarMem.putInteger(IWorkbenchConstants.TAG_LOCKED,0);
			}
			IContributionItem[] items = getCoolBarManager().getItems();
			for (int i = 0; i < items.length; i++) {
				IMemento coolItemMem = coolBarMem.createChild(IWorkbenchConstants.TAG_COOLITEM);
				IContributionItem item = items[i];
				// The id of the contribution item
				if (item.getId() != null) {
					coolItemMem.putString(IWorkbenchConstants.TAG_ID, item.getId());
				}
				// Write out type and size if applicable
				if (item.isSeparator()) {
					coolItemMem.putString(
							IWorkbenchConstants.TAG_ITEM_TYPE,
							IWorkbenchConstants.TAG_TYPE_SEPARATOR);
				} else if (item.isGroupMarker() && !item.isSeparator()) {
					coolItemMem.putString(
							IWorkbenchConstants.TAG_ITEM_TYPE,
							IWorkbenchConstants.TAG_TYPE_GROUPMARKER);
				} else {
					// Assume that it is a ToolBarContributionItem
					coolItemMem.putString(
							IWorkbenchConstants.TAG_ITEM_TYPE,
							IWorkbenchConstants.TAG_TYPE_TOOLBARCONTRIBUTION);
					ToolBarContributionItem tbItem = (ToolBarContributionItem) item;
					tbItem.saveWidgetState();
					coolItemMem.putInteger(
							IWorkbenchConstants.TAG_ITEM_X,
							tbItem.getCurrentWidth());
					coolItemMem.putInteger(
							IWorkbenchConstants.TAG_ITEM_Y,
							tbItem.getCurrentHeight());
				}
			}
		}
		

		// Save each page.
		Iterator enum = pageList.iterator();
		while (enum.hasNext()) {
			WorkbenchPage page = (WorkbenchPage) enum.next();

			// Get the input.
			IAdaptable input = page.getInput();
			if (input == null) {
				WorkbenchPlugin.log("Unable to save page input: " + page); //$NON-NLS-1$
				continue;
			}
			IPersistableElement persistable =
				(IPersistableElement) input.getAdapter(IPersistableElement.class);
			if (persistable == null) {
				WorkbenchPlugin.log("Unable to save page input: " + input); //$NON-NLS-1$
				continue;
			}

			// Save perspective.
			IMemento pageMem = memento.createChild(IWorkbenchConstants.TAG_PAGE);
			pageMem.putString(IWorkbenchConstants.TAG_LABEL, page.getLabel());
			result.add(page.saveState(pageMem));

			if (page == getActiveWorkbenchPage()) {
				pageMem.putString(IWorkbenchConstants.TAG_FOCUS, "true"); //$NON-NLS-1$
			}

			// Save input.
			IMemento inputMem = pageMem.createChild(IWorkbenchConstants.TAG_INPUT);
			inputMem.putString(IWorkbenchConstants.TAG_FACTORY_ID, persistable.getFactoryId());
			persistable.saveState(inputMem);
		}
		return result;
	}
	/**
	 * Select the shortcut for a perspective.
	 */
	/* package */
	void selectPerspectiveShortcut(
		IPerspectiveDescriptor perspective,
		WorkbenchPage page,
		boolean selected) {
		IContributionItem item = findPerspectiveShortcut(perspective, page);
		if (item != null) {
			IAction action = ((ActionContributionItem) item).getAction();
			action.setChecked(selected);
		}
	}
	/**
	 * Sets the active page within the window.
	 *
	 * @param page identifies the new active page.
	 */
	public void setActivePage(final IWorkbenchPage in) {
		if (getActiveWorkbenchPage() == in)
			return;

		// 1FVGTNR: ITPUI:WINNT - busy cursor for switching perspectives
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				// Deactivate old persp.
				WorkbenchPage currentPage = getActiveWorkbenchPage();
				if (currentPage != null) {
					currentPage.onDeactivate();
				}

				// Activate new persp.
				if (in == null || pageList.contains(in))
					pageList.setActive(in);
				WorkbenchPage newPage = pageList.getActive();
				if (newPage != null) {
					newPage.onActivate();
					firePageActivated(newPage);
					if (newPage.getPerspective() != null)
						firePerspectiveActivated(newPage, newPage.getPerspective());
				}

				if (isClosing())
					return;

				updateDisabled = false;

				// Update action bars ( implicitly calls updateActionBars() )
				updateActionSets();
				shortcutBar.update(false);
				getMenuManager().update(IAction.TEXT);

				if (noOpenPerspective != null && in != null) {
					noOpenPerspective.dispose();
					noOpenPerspective = null;
				}
			}
		});
	}

	/**
	 * Shows the popup menu for a page item in the shortcut bar.
	 */
	private void showShortcutBarPopup(Point pt) {
		// Get the tool item under the mouse.
		ToolBar toolBar = shortcutBar.getControl();
		ToolItem toolItem = toolBar.getItem(toolBar.toControl(pt));
		if (toolItem == null)
			return;

		// Get the action for the tool item.
		Object data = toolItem.getData();

		// If the tool item is an icon for a fast view
		if (data instanceof ShowFastViewContribution) {
			// The fast view bar menu is created lazily here.
			if (fastViewBarMenu == null) {
				Menu menu = new Menu(toolBar);
				MenuItem closeItem = new MenuItem(menu, SWT.NONE);
				closeItem.setText(WorkbenchMessages.getString("WorkbenchWindow.close")); //$NON-NLS-1$
				closeItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						ToolItem toolItem = (ToolItem) fastViewBarMenu.getData();
						if (toolItem != null && !toolItem.isDisposed()) {
							IViewReference ref =
								(IViewReference) toolItem.getData(
									ShowFastViewContribution.FAST_VIEW);
							getActiveWorkbenchPage().hideView(ref);
						}
					}
				});
				restoreItem = new MenuItem(menu, SWT.CHECK);
				restoreItem.setText(WorkbenchMessages.getString("WorkbenchWindow.restore")); //$NON-NLS-1$
				restoreItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						ToolItem toolItem = (ToolItem) fastViewBarMenu.getData();
						if (toolItem != null && !toolItem.isDisposed()) {
							IViewReference ref =
								(IViewReference) toolItem.getData(
									ShowFastViewContribution.FAST_VIEW);
							getActiveWorkbenchPage().removeFastView(ref);
						}
					}
				});
				fastViewBarMenu = menu;
			}
			restoreItem.setSelection(true);
			fastViewBarMenu.setData(toolItem);

			// Show popup menu.
			if (fastViewBarMenu != null) {
				fastViewBarMenu.setLocation(pt.x, pt.y);
				fastViewBarMenu.setVisible(true);
			}
		}

		if (!(data instanceof ActionContributionItem))
			return;
		IAction action = ((ActionContributionItem) data).getAction();

		// The tool item is an icon for a perspective.
		if (action instanceof SetPagePerspectiveAction) {
			// The perspective bar menu is created lazily here.
			// Its data is set (each time) to the tool item, which refers to the SetPagePerspectiveAction
			// which in turn refers to the page and perspective.
			// It is important not to refer to the action, the page or the perspective directly
			// since otherwise the menu hangs on to them after they are closed.
			// By hanging onto the tool item instead, these references are cleared when the
			// corresponding page or perspective is closed.
			// See bug 11282 for more details on why it is done this way.
			if (perspectiveBarMenu == null) {
				Menu menu = new Menu(toolBar);
				MenuItem menuItem = new MenuItem(menu, SWT.NONE);
				menuItem.setText(WorkbenchMessages.getString("WorkbenchWindow.close")); //$NON-NLS-1$
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						ToolItem toolItem = (ToolItem) perspectiveBarMenu.getData();
						if (toolItem != null && !toolItem.isDisposed()) {
							ActionContributionItem item =
								(ActionContributionItem) toolItem.getData();
							SetPagePerspectiveAction action =
								(SetPagePerspectiveAction) item.getAction();
							action.getPage().closePerspective(action.getPerspective(), true);
						}
					}
				});
				menuItem = new MenuItem(menu, SWT.NONE);
				menuItem.setText(WorkbenchMessages.getString("WorkbenchWindow.closeAll")); //$NON-NLS-1$
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						ToolItem toolItem = (ToolItem) perspectiveBarMenu.getData();
						if (toolItem != null && !toolItem.isDisposed()) {
							ActionContributionItem item =
								(ActionContributionItem) toolItem.getData();
							SetPagePerspectiveAction action =
								(SetPagePerspectiveAction) item.getAction();
							action.getPage().closeAllPerspectives();
						}
					}
				});
				perspectiveBarMenu = menu;
			}
			perspectiveBarMenu.setData(toolItem);

			// Show popup menu.
			if (perspectiveBarMenu != null) {
				perspectiveBarMenu.setLocation(pt.x, pt.y);
				perspectiveBarMenu.setVisible(true);
			}
		}
	}
	/**
	 * Hooks a listener to track the activation and
	 * deactivation of the window's shell. Notifies
	 * the active part and editor of the change
	 */
	private void trackShellActivation(Shell shell) {
		shell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent event) {
				shellActivated = true;
				getWorkbenchImpl().setActivatedWindow(WorkbenchWindow.this);
				WorkbenchPage currentPage = getActiveWorkbenchPage();
				if (currentPage != null) {
					IWorkbenchPart part = currentPage.getActivePart();
					if (part != null) {
						PartSite site = (PartSite) part.getSite();
						site.getPane().shellActivated();
					}
					IEditorPart editor = currentPage.getActiveEditor();
					if (editor != null) {
						PartSite site = (PartSite) editor.getSite();
						site.getPane().shellActivated();
					}
					getWorkbenchImpl().fireWindowActivated(WorkbenchWindow.this);
				}
			}
			public void shellDeactivated(ShellEvent event) {
				shellActivated = false;
				WorkbenchPage currentPage = getActiveWorkbenchPage();
				if (currentPage != null) {
					IWorkbenchPart part = currentPage.getActivePart();
					if (part != null) {
						PartSite site = (PartSite) part.getSite();
						site.getPane().shellDeactivated();
					}
					IEditorPart editor = currentPage.getActiveEditor();
					if (editor != null) {
						PartSite site = (PartSite) editor.getSite();
						site.getPane().shellDeactivated();
					}
					getWorkbenchImpl().fireWindowDeactivated(WorkbenchWindow.this);
				}
			}
		});
	}
	/**
	 * Hooks a listener to track the resize of the window's
	 * shell. Stores the new bounds if in normal state - that
	 * is, not in minimized or maximized state)
	 */
	private void trackShellResize(Shell newShell) {
		newShell.addControlListener(new ControlAdapter() {
			public void controlMoved(ControlEvent e) {
				saveBounds();
			}

			public void controlResized(ControlEvent e) {
				saveBounds();
			}

			private void saveBounds() {
				Shell shell = getShell();
				if (shell == null)
					return;
				if (shell.isDisposed())
					return;
				if (shell.getMinimized())
					return;
				if (shell.getMaximized()) {
					asMaximizedState = true;
					return;
				}
				asMaximizedState = false;
				normalBounds = shell.getBounds();
			}
		});
	}

	/**
	 * update the action bars.
	 */
	public void updateActionBars() {
		if (updateDisabled)
			return;
		// updateAll required in order to enable accelerators on pull-down menus
		getMenuBarManager().updateAll(false);
		getCoolBarManager().update(false);
		getStatusLineManager().update(false);
	}
	/**
	 * Update the visible action sets. This method is typically called
	 * from a page when the user changes the visible action sets
	 * within the prespective.  
	 */
	public void updateActionSets() {
		if (updateDisabled)
			return;

		WorkbenchPage currentPage = getActiveWorkbenchPage();
		if (currentPage == null)
			actionPresentation.clearActionSets();
		else {
			if (getCoolBarManager() != null) {
				getCoolBarManager().refresh();
			}
			actionPresentation.setActionSets(currentPage.getActionSets());
		}
		updateActionBars();

		// hide the launch menu if it is empty
		String path =
			IWorkbenchActionConstants.M_WINDOW
				+ IWorkbenchActionConstants.SEP
				+ IWorkbenchActionConstants.M_LAUNCH;
		IMenuManager manager = getMenuBarManager().findMenuUsingPath(path);
		IContributionItem item = getMenuBarManager().findUsingPath(path);
		
		// TODO remove: updateActiveActions();
		IActionSet actionSets[] = actionPresentation.getActionSets();
		registerActionSets(actionSets);

		if (manager == null || item == null)
			return;
		item.setVisible(manager.getItems().length >= 2);
		// there is a separator for the additions group thus >= 2
	}
	/**
	 * Updates the shorcut item
	 */
	/* package */
	void updatePerspectiveShortcut(
		IPerspectiveDescriptor oldDesc,
		IPerspectiveDescriptor newDesc,
		WorkbenchPage page) {
		if (updateDisabled)
			return;

		IContributionItem item = findPerspectiveShortcut(oldDesc, page);
		if (item != null) {
			SetPagePerspectiveAction action =
				(SetPagePerspectiveAction) ((ActionContributionItem) item).getAction();
			action.update(newDesc);
		}
	}

	/**
	 * Return whether or not to show the progress indicator.
	 * @return boolan
	 */
	private boolean showProgressIndicator() {
		return PlatformUI.getWorkbench().getPreferenceStore().getBoolean(
			IWorkbenchConstants.SHOW_PROGRESS_INDICATOR);
	}
	/**
	 * Create the progress indicator for the receiver.
	 * @param shell	the parent shell
	 */
	private void createProgressIndicator(Shell shell) {
		if (showProgressIndicator()) {
			animationItem = new AnimationItem(this);
			animationItem.createControl(shell);
		}

	}
	class PageList {
		//List of pages in the order they were created;
		private List pageList;
		//List of pages where the top is the last activated.
		private List pageStack;
		// The page explicitly activated
		private Object active;

		public PageList() {
			pageList = new ArrayList(4);
			pageStack = new ArrayList(4);
		}
		public boolean add(Object object) {
			pageList.add(object);
			pageStack.add(0, object);
			//It will be moved to top only when activated.
			return true;
		}
		public Iterator iterator() {
			return pageList.iterator();
		}
		public boolean contains(Object object) {
			return pageList.contains(object);
		}
		public boolean remove(Object object) {
			if (active == object)
				active = null;
			pageStack.remove(object);
			return pageList.remove(object);
		}
		public boolean isEmpty() {
			return pageList.isEmpty();
		}
		public IWorkbenchPage[] getPages() {
			int nSize = pageList.size();
			IWorkbenchPage[] retArray = new IWorkbenchPage[nSize];
			pageList.toArray(retArray);
			return retArray;
		}
		public void setActive(Object page) {
			if (active == page)
				return;

			active = page;

			if (page != null) {
				pageStack.remove(page);
				pageStack.add(page);
			}
		}
		public WorkbenchPage getActive() {
			return (WorkbenchPage) active;
		}
		public WorkbenchPage getNextActive() {
			if (active == null) {
				if (pageStack.isEmpty())
					return null;
				else
					return (WorkbenchPage) pageStack.get(pageStack.size() - 1);
			} else {
				if (pageStack.size() < 2)
					return null;
				else
					return (WorkbenchPage) pageStack.get(pageStack.size() - 2);
			}
		}
	}

	/**
	 * Returns the unique object that applications use to configure this window.
	 * <p>
	 * IMPORTANT This method is declared package-private to prevent regular
	 * plug-ins from downcasting IWorkbenchWindow to WorkbenchWindow and getting
	 * hold of the workbench window configurer that would allow them to tamper
	 * with the workbench window. The workbench window configurer is available
	 * only to the application.
	 * </p>
	 */
	/* package - DO NOT CHANGE */
	WorkbenchWindowConfigurer getWindowConfigurer() {
		if (windowConfigurer == null) {
			// lazy initialize
			windowConfigurer = new WorkbenchWindowConfigurer(this);
			windowConfigurer.init();
		}
		return windowConfigurer;
	}

	/**
	 * Returns the workbench advisor. Assumes the workbench
	 * has been created already.
	 * <p>
	 * IMPORTANT This method is declared private to prevent regular
	 * plug-ins from downcasting IWorkbenchWindow to WorkbenchWindow and getting
	 * hold of the workbench advisor that would allow them to tamper with the
	 * workbench. The workbench advisor is internal to the application.
	 * </p>
	 */
	private /* private - DO NOT CHANGE */
	WorkbenchAdvisor getAdvisor() {
		return getWorkbenchImpl().getAdvisor();
	}

	/*
	 * Returns the IWorkbench implementation.
	 */
	private Workbench getWorkbenchImpl() {
		return Workbench.getInstance();
	}

	/**
	 * Creates a clone copy of the current action bars
	 * @param configurer location of managers
	 * @param flags indicate which actions to load and whether its a proxy fill
	 */
	public void fillActionBars(IActionBarConfigurer configurer, int flags) {
		getAdvisor().fillActionBars(this, configurer, flags);
	}

	private IWorkbenchWindowCommandSupport workbenchWindowCommandSupport;
	private IWorkbenchWindowContextSupport workbenchWindowContextSupport;

	public IWorkbenchWindowCommandSupport getCommandSupport() {
		return workbenchWindowCommandSupport;
	}

	public IWorkbenchWindowContextSupport getContextSupport() {
		return workbenchWindowContextSupport;
	}

	public Object getAdapter(Class adapter) {
		if (IWorkbenchWindowCommandSupport.class.equals(adapter))
			return getCommandSupport();
		else if (IWorkbenchWindowContextSupport.class.equals(adapter))
			return getContextSupport();
		else
			return null;
	}
}
