//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.

package org.columba.core.gui.frame;

import java.awt.event.MouseAdapter;

import org.columba.core.charset.CharsetManager;
import org.columba.core.config.ViewItem;
import org.columba.core.gui.menu.Menu;
import org.columba.core.gui.selection.SelectionManager;
import org.columba.core.gui.statusbar.StatusBar;
import org.columba.core.logging.ColumbaLogger;
import org.columba.core.main.MainInterface;
import org.columba.core.xml.XmlElement;
import org.columba.mail.gui.frame.TooltipMouseHandler;

/**
 * @author Timo Stich (tstich@users.sourceforge.net)
 * 
 */
public abstract class AbstractFrameController {

	protected StatusBar statusBar;
	protected MouseAdapter mouseTooltipHandler;

	protected ViewItem viewItem;

	protected AbstractFrameView view;
	protected SelectionManager selectionManager;
	

	protected String id;

	protected XmlElement defaultView;

	/**
	 * Constructor for FrameController.
	 * 
	 * Warning: Never do any inits in the constructor -> use init() instead!
	 */
	public AbstractFrameController(String id, ViewItem viewItem) {
		this.id = id;
		this.viewItem = viewItem;

		defaultView = new XmlElement("view");
		XmlElement window = new XmlElement("window");
		window.addAttribute("width", "640");
		window.addAttribute("height", "480");
		window.addAttribute("maximized", "true");
		defaultView.addElement(window);
		XmlElement toolbars = new XmlElement("toolbars");
		toolbars.addAttribute("main","true");
		defaultView.addElement(toolbars);

		if (viewItem == null)
			this.viewItem = new ViewItem(createDefaultConfiguration(id));

		statusBar = new StatusBar(MainInterface.processor.getTaskManager());

		mouseTooltipHandler = new TooltipMouseHandler(statusBar);

		selectionManager = new SelectionManager();
		init();

		initActions();

		view = createView();
		//view.init();
		
		initInternActions();
	}

	protected void initActions() {
		/*
		ActionPluginHandler handler = null;
		try {

			handler =
				(ActionPluginHandler) MainInterface.pluginManager.getHandler(
					"org.columba.core.action");
			handler.initGuiElements();
		} catch (PluginHandlerNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		*/
	}

	protected XmlElement createDefaultConfiguration(String id) {
		XmlElement child = (XmlElement) defaultView.clone();
		child.addAttribute("id", id);

		return child;
	}

	/**
	 * - create all additional controllers
	 * - register SelectionHandlers
	 */
	protected abstract void init();

	protected abstract void initInternActions();

	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * Returns the mouseTooltipHandler.
	 * @return MouseAdapter
	 */
	public MouseAdapter getMouseTooltipHandler() {
		return mouseTooltipHandler;
	}

	public void saveAndClose() {
		view.saveWindowPosition();
		//model.saveAndUnregister(id);
	}

	public void close() {
		if (MainInterface.DEBUG) {
                        ColumbaLogger.log.info("closing FrameController");
                }

		view.saveWindowPosition();

		view.setVisible(false);

		FrameModel.close(this);

		//FrameModel.close(this);
		//model.unregister(id);

		//getView().setVisible(false);	
	}

	abstract protected AbstractFrameView createView();

	public void openView() {
		view.loadWindowPosition();

		view.setVisible(true);
	}

	/**
	 * @return ViewItem
	 */
	public ViewItem getViewItem() {
		return viewItem;
	}

	/**
	 * Sets the item.
	 * @param item The item to set
	 */
	public void setViewItem(ViewItem item) {
		this.viewItem = item;
	}
	
	public void enableToolbar(String id, boolean enable)
	{
		getViewItem().set("toolbars", id, enable);
	}
	
	public boolean isToolbarEnabled(String id)
	{
		return getViewItem().getBoolean("toolbars", id, true);
	}

	/**
	 * @return FrameView
	 */
	public AbstractFrameView getView() {
		return view;
	}

	public Menu getMenu() {
		return view.getMenu();
	}

	/**
	 * @return SelectionManager
	 */
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	/**
	 * Sets the selectionManager.
	 * @param selectionManager The selectionManager to set
	 */
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void executeCutAction() {}
	public void executeCopyAction() {}
	public void executePasteAction() {}
	

}
