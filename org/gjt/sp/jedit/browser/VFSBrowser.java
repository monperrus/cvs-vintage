/*
 * VFSBrowser.java - VFS browser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit.browser;

//{{{ Imports
import bsh.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * The main class of the VFS browser.
 * @author Slava Pestov
 * @version $Id: VFSBrowser.java,v 1.116 2006/06/18 18:51:39 vanza Exp $
 */
public class VFSBrowser extends JPanel implements EBComponent, DefaultFocusComponent
{
	public static final String NAME = "vfs.browser";

	//{{{ Browser types
	/**
	 * Open file dialog mode. Equals JFileChooser.OPEN_DIALOG for
	 * backwards compatibility.
	 */
	public static final int OPEN_DIALOG = 0;

	/**
	 * Save file dialog mode. Equals JFileChooser.SAVE_DIALOG for
	 * backwards compatibility.
	 */
	public static final int SAVE_DIALOG = 1;
	/**
	 * Choose directory dialog mode.
	 */
	public static final int BROWSER_DIALOG = 4;
	/**
	 * Choose directory dialog mode.
	 */
	public static final int CHOOSE_DIRECTORY_DIALOG = 3;

	/**
	 * Stand-alone browser mode.
	 */
	public static final int BROWSER = 2;
	//}}}

	//{{{ browseDirectoryInNewWindow() method
	/**
	 * Opens the specified directory in a new, floating, file system browser.
	 * @param view The view
	 * @param path The directory's path
	 * @since jEdit 4.1pre2
	 */
	public static void browseDirectoryInNewWindow(View view, String path)
	{
		DockableWindowManager wm = view.getDockableWindowManager();
		if(path != null)
		{
			// this is such a bad way of doing it, but oh well...
			jEdit.setTemporaryProperty("vfs.browser.path.tmp",path);
		}
		wm.floatDockableWindow("vfs.browser");
		jEdit.unsetProperty("vfs.browser.path.tmp");
	} //}}}

	//{{{ browseDirectory() method
	/**
	 * Opens the specified directory in a file system browser.
	 * @param view The view
	 * @param path The directory's path
	 * @since jEdit 4.0pre3
	 */
	public static void browseDirectory(View view, String path)
	{
		DockableWindowManager wm = view.getDockableWindowManager();
		VFSBrowser browser = (VFSBrowser)wm.getDockable(NAME);
		if(browser != null)
		{
			wm.showDockableWindow(NAME);
			browser.setDirectory(path);
		}
		else
		{
			if(path != null)
			{
				// this is such a bad way of doing it, but oh well...
				jEdit.setTemporaryProperty("vfs.browser.path.tmp",path);
			}
			wm.addDockableWindow("vfs.browser");
			jEdit.unsetProperty("vfs.browser.path.tmp");
		}
	} //}}}

	//{{{ getActionContext() method
	/**
	 * Returns the browser action context.
	 * @since jEdit 4.2pre1
	 */
	public static ActionContext getActionContext()
	{
		return actionContext;
	} //}}}

	//{{{ VFSBrowser constructor
	/**
	 * Creates a new VFS browser.
	 * @param view The view to open buffers in by default
	 */
	public VFSBrowser(View view, String position)
	{
		this(view,null,BROWSER,true,position);
	} //}}}

	//{{{ VFSBrowser constructor
	/**
	 * Creates a new VFS browser.
	 * @param view The view to open buffers in by default
	 * @param path The path to display
	 * @param mode The browser mode
	 * @param multipleSelection True if multiple selection should be allowed
	 * @param position Where the browser is located
	 * @since jEdit 4.2pre1
	 */
	public VFSBrowser(View view, String path, int mode,
		boolean multipleSelection, String position)
	{
		super(new BorderLayout());

		listenerList = new EventListenerList();

		this.mode = mode;
		this.multipleSelection = multipleSelection;
		this.view = view;

		currentEncoding = jEdit.getProperty("buffer.encoding",
			System.getProperty("file.encoding"));
		autoDetectEncoding = jEdit.getBooleanProperty(
			"buffer.encodingAutodetect");

		ActionHandler actionHandler = new ActionHandler();

		Box topBox = new Box(BoxLayout.Y_AXIS);

		horizontalLayout = (mode != BROWSER
			|| DockableWindowManager.TOP.equals(position)
			|| DockableWindowManager.BOTTOM.equals(position));

		toolbarBox = new Box(horizontalLayout
			? BoxLayout.X_AXIS
			: BoxLayout.Y_AXIS);

		topBox.add(toolbarBox);

		GridBagLayout layout = new GridBagLayout();
		JPanel pathAndFilterPanel = new JPanel(layout);

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridwidth = cons.gridheight = 1;
		cons.gridx = cons.gridy = 0;
		cons.fill = GridBagConstraints.BOTH;
		cons.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel(jEdit.getProperty("vfs.browser.path"),
			SwingConstants.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		layout.setConstraints(label,cons);
		pathAndFilterPanel.add(label);

		pathField = new HistoryTextField("vfs.browser.path");
		pathField.setInstantPopups(true);
		pathField.setEnterAddsToHistory(false);
		pathField.setSelectAllOnFocus(true);

		// because its preferred size can be quite wide, we
		// don't want it to make the browser way too big,
		// so set the preferred width to 0.
		Dimension prefSize = pathField.getPreferredSize();
		prefSize.width = 0;
		pathField.setPreferredSize(prefSize);
		pathField.addActionListener(actionHandler);
		cons.gridx = 1;
		cons.weightx = 1.0f;

		layout.setConstraints(pathField,cons);
		pathAndFilterPanel.add(pathField);

		filterCheckbox = new JCheckBox(jEdit.getProperty("vfs.browser.filter"));
		filterCheckbox.setMargin(new Insets(0,0,0,0));
		filterCheckbox.setRequestFocusEnabled(false);
		filterCheckbox.setBorder(new EmptyBorder(0,0,0,12));
		filterCheckbox.setSelected(jEdit.getBooleanProperty(
			"vfs.browser.filter-enabled"));

		filterCheckbox.addActionListener(actionHandler);

		if(mode != CHOOSE_DIRECTORY_DIALOG)
		{
			cons.gridx = 0;
			cons.weightx = 0.0f;
			cons.gridy = 1;
			layout.setConstraints(filterCheckbox,cons);
			pathAndFilterPanel.add(filterCheckbox);
		}

		filterField = new HistoryTextField("vfs.browser.filter");
		filterField.setInstantPopups(true);
		filterField.setSelectAllOnFocus(true);
		filterField.addActionListener(actionHandler);

		if(mode != CHOOSE_DIRECTORY_DIALOG)
		{
			cons.gridx = 1;
			cons.weightx = 1.0f;
			layout.setConstraints(filterField,cons);
			pathAndFilterPanel.add(filterField);
		}

		topBox.add(pathAndFilterPanel);
		add(BorderLayout.NORTH,topBox);

		add(BorderLayout.CENTER,browserView = new BrowserView(this));

		propertiesChanged();

		String filter;
		if(mode == BROWSER || !jEdit.getBooleanProperty(
			"vfs.browser.currentBufferFilter"))
		{
			filter = jEdit.getProperty("vfs.browser.last-filter");
			if(filter == null)
				filter = jEdit.getProperty("vfs.browser.default-filter");
		}
		else
		{
			String ext = MiscUtilities.getFileExtension(
				view.getBuffer().getName());
			if(ext.length() == 0)
				filter = jEdit.getProperty("vfs.browser.default-filter");
			else
				filter = "*" + ext;
		}

		filterField.setText(filter);
		filterField.addCurrentToHistory();

		updateFilterEnabled();

		// see VFSBrowser.browseDirectory()
		if(path == null)
			path = jEdit.getProperty("vfs.browser.path.tmp");

		if(path == null || path.length() == 0)
		{
			String userHome = System.getProperty("user.home");
			String defaultPath = jEdit.getProperty("vfs.browser.defaultPath");
			if(defaultPath.equals("home"))
				path = userHome;
			else if(defaultPath.equals("working"))
				path = System.getProperty("user.dir");
			else if(defaultPath.equals("buffer"))
			{
				if(view != null)
				{
					Buffer buffer = view.getBuffer();
					path = buffer.getDirectory();
				}
				else
					path = userHome;
			}
			else if(defaultPath.equals("last"))
			{
				HistoryModel pathModel = HistoryModel.getModel("vfs.browser.path");
				if(pathModel.getSize() == 0)
					path = "~";
				else
					path = pathModel.getItem(0);
			}
			else if(defaultPath.equals("favorites"))
				path = "favorites:";
			else
			{
				// unknown value??!!!
				path = userHome;
			}
		}

		final String _path = path;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setDirectory(_path);
			}
		});
	} //}}}

	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		browserView.focusOnFileView();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		jEdit.setBooleanProperty("vfs.browser.filter-enabled",
			filterCheckbox.isSelected());
		if(mode == BROWSER || !jEdit.getBooleanProperty(
			"vfs.browser.currentBufferFilter"))
		{
			jEdit.setProperty("vfs.browser.last-filter",
				filterField.getText());
		}
		EditBus.removeFromBus(this);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getWhat() == BufferUpdate.CREATED
				|| bmsg.getWhat() == BufferUpdate.CLOSED)
				browserView.updateFileView();
		}
		else if(msg instanceof PluginUpdate)
		{
			PluginUpdate pmsg = (PluginUpdate)msg;
			if(pmsg.getWhat() == PluginUpdate.LOADED
				|| pmsg.getWhat() == PluginUpdate.UNLOADED)
			{
				plugins.updatePopupMenu();
			}
		}
		else if(msg instanceof VFSUpdate)
		{
			maybeReloadDirectory(((VFSUpdate)msg).getPath());
		}
	} //}}}

	//{{{ getView() method
	public View getView()
	{
		return view;
	} //}}}

	//{{{ getMode() method
	public int getMode()
	{
		return mode;
	} //}}}

	//{{{ isMultipleSelectionEnabled() method
	public boolean isMultipleSelectionEnabled()
	{
		return multipleSelection;
	} //}}}

	//{{{ isHorizontalLayout() method
	public boolean isHorizontalLayout()
	{
		return horizontalLayout;
	} //}}}

	//{{{ getShowHiddenFiles() method
	public boolean getShowHiddenFiles()
	{
		return showHiddenFiles;
	} //}}}

	//{{{ setShowHiddenFiles() method
	public void setShowHiddenFiles(boolean showHiddenFiles)
	{
		this.showHiddenFiles = showHiddenFiles;
	} //}}}

	//{{{ getFilenameFilter() method
	/**
	 * Returns the file name filter glob.
	 * @since jEdit 3.2pre2
	 */
	public String getFilenameFilter()
	{
		if(filterCheckbox.isSelected())
		{
			String filter = filterField.getText();
			if(filter.length() == 0)
				return "*";
			else
				return filter;
		}
		else
			return "*";
	} //}}}

	//{{{ setFilenameFilter() method
	public void setFilenameFilter(String filter)
	{
		if(filter == null || filter.length() == 0 || filter.equals("*"))
			filterCheckbox.setSelected(false);
		else
		{
			filterCheckbox.setSelected(true);
			filterField.setText(filter);
		}
	} //}}}

	//{{{ getDirectoryField() method
	public HistoryTextField getDirectoryField()
	{
		return pathField;
	} //}}}

	//{{{ getDirectory() method
	public String getDirectory()
	{
		return path;
	} //}}}

	//{{{ setDirectory() method
	public void setDirectory(String path)
	{
		if(path.startsWith("file:"))
			path = path.substring(5);

		pathField.setText(path);

		if(!startRequest())
			return;

		updateFilenameFilter();
		browserView.saveExpansionState();
		browserView.loadDirectory(null,path,true);
		this.path = path;

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				endRequest();
			}
		});
	} //}}}

	//{{{ getRootDirectory() method
	public static String getRootDirectory()
	{
		if(OperatingSystem.isMacOS() || OperatingSystem.isDOSDerived())
			return FileRootsVFS.PROTOCOL + ":";
		else
			return "/";
	} //}}}

	//{{{ rootDirectory() method
	/**
	 * Goes to the local drives directory.
	 * @since jEdit 4.0pre4
	 */
	public void rootDirectory()
	{
		setDirectory(getRootDirectory());
	} //}}}

	//{{{ reloadDirectory() method
	public void reloadDirectory()
	{
		// used by FTP plugin to clear directory cache
		VFSManager.getVFSForPath(path).reloadDirectory(path);

		updateFilenameFilter();
		browserView.saveExpansionState();
		browserView.loadDirectory(null,path,false);
	} //}}}

	//{{{ delete() method
	/**
	 * Note that all files must be on the same VFS.
	 * @since jEdit 4.3pre2
	 */
	public void delete(VFSFile[] files)
	{
		String dialogType;

		if(MiscUtilities.isURL(files[0].getDeletePath())
			&& FavoritesVFS.PROTOCOL.equals(
			MiscUtilities.getProtocolOfURL(files[0].getDeletePath())))
		{
			dialogType = "vfs.browser.delete-favorites";
		}
		else
		{
			dialogType = "vfs.browser.delete-confirm";
		}

		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < files.length; i++)
		{
			buf.append(files[i].getPath());
			buf.append('\n');
		}

		Object[] args = { buf.toString() };
		int result = GUIUtilities.confirm(this,dialogType,args,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
		if(result != JOptionPane.YES_OPTION)
			return;

		VFS vfs = VFSManager.getVFSForPath(files[0].getDeletePath());

		if(!startRequest())
			return;

		for(int i = 0; i < files.length; i++)
		{
			Object session = vfs.createVFSSession(files[i].getDeletePath(),this);
			if(session == null)
				continue;

			VFSManager.runInWorkThread(new BrowserIORequest(
				BrowserIORequest.DELETE,this,
				session,vfs,files[i].getDeletePath(),
				null,null));
		}

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				endRequest();
			}
		});
	} //}}}

	//{{{ rename() method
	public void rename(String from)
	{
		VFS vfs = VFSManager.getVFSForPath(from);

		String filename = vfs.getFileName(from);
		String[] args = { filename };
		String to = GUIUtilities.input(this,"vfs.browser.rename",
			args,filename);
		if(to == null)
			return;

		to = MiscUtilities.constructPath(vfs.getParentOfPath(from),to);

		Object session = vfs.createVFSSession(from,this);
		if(session == null)
			return;

		if(!startRequest())
			return;

		VFSManager.runInWorkThread(new BrowserIORequest(
			BrowserIORequest.RENAME,this,
			session,vfs,from,to,null));

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				endRequest();
			}
		});
	} //}}}

	//{{{ mkdir() method
	public void mkdir()
	{
		String newDirectory = GUIUtilities.input(this,"vfs.browser.mkdir",null);
		if(newDirectory == null)
			return;

		// if a directory is selected, create new dir in there.
		// if a file is selected, create new dir inside its parent.
		VFSFile[] selected = getSelectedFiles();
		String parent;
		if(selected.length == 0)
			parent = path;
		else if(selected[0].getType() == VFSFile.FILE)
		{
			parent = selected[0].getPath();
			parent = VFSManager.getVFSForPath(parent)
				.getParentOfPath(parent);
		}
		else
			parent = selected[0].getPath();

		VFS vfs = VFSManager.getVFSForPath(parent);

		// path is the currently viewed directory in the browser
		newDirectory = MiscUtilities.constructPath(parent,newDirectory);

		Object session = vfs.createVFSSession(newDirectory,this);
		if(session == null)
			return;

		if(!startRequest())
			return;

		VFSManager.runInWorkThread(new BrowserIORequest(
			BrowserIORequest.MKDIR,this,
			session,vfs,newDirectory,null,null));

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				endRequest();
			}
		});
	} //}}}

	//{{{ newFile() method
	/**
	 * Creates a new file in the current directory.
	 * @since jEdit 4.0pre2
	 */
	public void newFile()
	{
		VFSFile[] selected = getSelectedFiles();
		if(selected.length >= 1)
		{
			VFSFile file = selected[0];
			if(file.getType() == VFSFile.DIRECTORY)
				jEdit.newFile(view,file.getPath());
			else
			{
				VFS vfs = VFSManager.getVFSForPath(file.getPath());
				jEdit.newFile(view,vfs.getParentOfPath(file.getPath()));
			}
		}
		else
			jEdit.newFile(view,path);
	} //}}}

	//{{{ searchInDirectory() method
	/**
	 * Opens a directory search in the current directory.
	 * @since jEdit 4.0pre2
	 */
	public void searchInDirectory()
	{
		VFSFile[] selected = getSelectedFiles();
		if(selected.length >= 1)
		{
			VFSFile file = selected[0];
			searchInDirectory(file.getPath(),file.getType() != VFSFile.FILE);
		}
		else
		{
			searchInDirectory(this.path,true);
		}
	} //}}}

	//{{{ searchInDirectory() method
	/**
	 * Opens a directory search in the specified directory.
	 * @param path The path name
	 * @param directory True if the path is a directory, false if it is a file
	 * @since jEdit 4.2pre1
	 */
	public void searchInDirectory(String path, boolean directory)
	{
		String filter;

		if(directory)
		{
			filter = getFilenameFilter();
		}
		else
		{
			String name = MiscUtilities.getFileName(path);
			String ext = MiscUtilities.getFileExtension(name);
			filter = (ext == null || ext.length() == 0
				? getFilenameFilter()
				: "*" + ext);
			path = MiscUtilities.getParentOfPath(path);
		}

		SearchAndReplace.setSearchFileSet(new DirectoryListSet(
			path,filter,true));
		SearchDialog.showSearchDialog(view,null,SearchDialog.DIRECTORY);
	} //}}}

	//{{{ getBrowserView() method
	public BrowserView getBrowserView()
	{
		return browserView;
	} //}}}

	//{{{ getSelectedFiles() method
	/**
	 * @since jEdit 4.3pre2
	 */
	public VFSFile[] getSelectedFiles()
	{
		return browserView.getSelectedFiles();
	} //}}}

	//{{{ locateFile() method
	/**
	 * Goes to the given file's directory and selects the file in the list.
	 * @param path The file
	 * @since jEdit 4.2pre2
	 */
	public void locateFile(final String path)
	{
		if(!filenameFilter.matcher(MiscUtilities.getFileName(path)).matches())
			setFilenameFilter(null);

		setDirectory(MiscUtilities.getParentOfPath(path));
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				browserView.getTable().selectFile(path);
			}
		});
	} //}}}

	//{{{ createPluginsMenu() method
	public JComponent createPluginsMenu(JComponent pluginMenu, boolean showManagerOptions)
	{
		ActionHandler actionHandler = new ActionHandler();
		if(showManagerOptions && getMode() == VFSBrowser.BROWSER)
		{
			pluginMenu.add(GUIUtilities.loadMenuItem("plugin-manager",false));
			pluginMenu.add(GUIUtilities.loadMenuItem("plugin-options",false));
			if (pluginMenu instanceof JMenu)
				((JMenu)pluginMenu).addSeparator();
			else if (pluginMenu instanceof JPopupMenu)
				((JPopupMenu)pluginMenu).addSeparator();

		}
		else
			/* we're in a modal dialog */;

		ArrayList vec = new ArrayList();

		//{{{ old API
		Enumeration e = VFSManager.getFilesystems();

		while(e.hasMoreElements())
		{
			VFS vfs = (VFS)e.nextElement();
			if((vfs.getCapabilities() & VFS.BROWSE_CAP) == 0)
				continue;

				JMenuItem menuItem = new JMenuItem(jEdit.getProperty(
						"vfs." + vfs.getName() + ".label"));
				menuItem.setActionCommand(vfs.getName());
				menuItem.addActionListener(actionHandler);
				vec.add(menuItem);
		} //}}}

		//{{{ new API
		EditPlugin[] plugins = jEdit.getPlugins();
		for(int i = 0; i < plugins.length; i++)
		{
			JMenuItem menuItem = plugins[i].createBrowserMenuItems();
			if(menuItem != null)
				vec.add(menuItem);
		} //}}}

		if(vec.size() != 0)
		{
			MiscUtilities.quicksort(vec,new MiscUtilities.MenuItemCompare());
			for(int i = 0; i < vec.size(); i++)
				pluginMenu.add((JMenuItem)vec.get(i));
		}
		else
		{
			JMenuItem mi = new JMenuItem(jEdit.getProperty(
					"vfs.browser.plugins.no-plugins.label"));
			mi.setEnabled(false);
			pluginMenu.add(mi);
		}

		return pluginMenu;
	} //}}}

	//{{{ addBrowserListener() method
	public void addBrowserListener(BrowserListener l)
	{
		listenerList.add(BrowserListener.class,l);
	} //}}}

	//{{{ removeBrowserListener() method
	public void removeBrowserListener(BrowserListener l)
	{
		listenerList.remove(BrowserListener.class,l);
	} //}}}

	//{{{ filesActivated() method
	// canDoubleClickClose set to false when ENTER pressed
	public static final int M_OPEN = 0;
	public static final int M_OPEN_NEW_VIEW = 1;
	public static final int M_OPEN_NEW_PLAIN_VIEW = 2;
	public static final int M_OPEN_NEW_SPLIT = 3;
	public static final int M_INSERT = 4;

	/**
	 * This method does the "double-click" handling. It is public so that
	 * <code>browser.actions.xml</code> can bind to it.
	 * @since jEdit 4.2pre2
	 */
	public void filesActivated(int mode, boolean canDoubleClickClose)
	{
		VFSFile[] selectedFiles = browserView.getSelectedFiles();

		Buffer buffer = null;

check_selected: for(int i = 0; i < selectedFiles.length; i++)
		{
			VFSFile file = selectedFiles[i];

			if(file.getType() == VFSFile.DIRECTORY
				|| file.getType() == VFSFile.FILESYSTEM)
			{
				if(mode == M_OPEN_NEW_VIEW && this.mode == BROWSER)
					browseDirectoryInNewWindow(view,file.getPath());
				else
					setDirectory(file.getPath());
			}
			else if(this.mode == BROWSER || this.mode == BROWSER_DIALOG)
			{
				if(mode == M_INSERT)
				{
					view.getBuffer().insertFile(view,
						file.getPath());
					continue check_selected;
				}

				Buffer _buffer = jEdit.getBuffer(file.getPath());
				if(_buffer == null)
				{
					Hashtable props = new Hashtable();
					props.put(Buffer.ENCODING,currentEncoding);
					props.put(Buffer.ENCODING_AUTODETECT,
						new Boolean(autoDetectEncoding));
					_buffer = jEdit.openFile(null,null,
						file.getPath(),false,props);
				}
				else if(doubleClickClose && canDoubleClickClose
					&& this.mode != BROWSER_DIALOG
					&& selectedFiles.length == 1)
				{
					// close if this buffer is currently
					// visible in the view.
					EditPane[] editPanes = view.getEditPanes();
					for(int j = 0; j < editPanes.length; j++)
					{
						if(editPanes[j].getBuffer() == _buffer)
						{
							jEdit.closeBuffer(view,_buffer);
							return;
						}
					}
				}

				if(_buffer != null)
					buffer = _buffer;
			}
			else
			{
				// if a file is selected in OPEN_DIALOG or
				// SAVE_DIALOG mode, just let the listener(s)
				// handle it
			}
		}

		if(buffer != null)
		{
			switch(mode)
			{
			case M_OPEN:
				view.setBuffer(buffer);
				break;
			case M_OPEN_NEW_VIEW:
				jEdit.newView(view,buffer,false);
				break;
			case M_OPEN_NEW_PLAIN_VIEW:
				jEdit.newView(view,buffer,true);
				break;
			case M_OPEN_NEW_SPLIT:
				view.splitHorizontally().setBuffer(buffer);
				break;
			}
		}

		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i < listeners.length; i++)
		{
			if(listeners[i] == BrowserListener.class)
			{
				BrowserListener l = (BrowserListener)listeners[i+1];
				l.filesActivated(this,selectedFiles);
			}
		}
	} //}}}

	//{{{ Package-private members
	String currentEncoding;
	boolean autoDetectEncoding;

	//{{{ updateFilenameFilter() method
	void updateFilenameFilter()
	{
		try
		{
			String filter = filterField.getText();
			if(filter.length() == 0)
				filter = "*";
			filenameFilter = Pattern.compile(MiscUtilities.globToRE(filter),
							 Pattern.CASE_INSENSITIVE);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,VFSBrowser.this,e);
			String[] args = { filterField.getText(),
				e.getMessage() };
			GUIUtilities.error(this,"vfs.browser.bad-filter",args);
		}
	} //}}}

	//{{{ directoryLoaded() method
	void directoryLoaded(Object node, Object[] loadInfo,
		boolean addToHistory)
	{
		VFSManager.runInAWTThread(new DirectoryLoadedAWTRequest(
			node,loadInfo,addToHistory));
	} //}}}

	//{{{ filesSelected() method
	void filesSelected()
	{
		VFSFile[] selectedFiles = browserView.getSelectedFiles();

		if(mode == BROWSER)
		{
			for(int i = 0; i < selectedFiles.length; i++)
			{
				VFSFile file = selectedFiles[i];
				Buffer buffer = jEdit.getBuffer(file.getPath());
				if(buffer != null && view != null)
					view.setBuffer(buffer);
			}
		}

		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i < listeners.length; i++)
		{
			if(listeners[i] == BrowserListener.class)
			{
				BrowserListener l = (BrowserListener)listeners[i+1];
				l.filesSelected(this,selectedFiles);
			}
		}
	} //}}}

	//{{{ endRequest() method
	void endRequest()
	{
		requestRunning = false;
	} //}}}

	//}}}

	//{{{ Private members

	private static ActionContext actionContext;

	static
	{
		actionContext = new BrowserActionContext();

		ActionSet builtInActionSet = new ActionSet(null,null,null,
			jEdit.class.getResource("browser.actions.xml"));
		builtInActionSet.setLabel(jEdit.getProperty("action-set.browser"));
		builtInActionSet.load();
		actionContext.addActionSet(builtInActionSet);
	}

	//{{{ Instance variables
	private EventListenerList listenerList;
	private View view;
	private boolean horizontalLayout;
	private String path;
	private HistoryTextField pathField;
	private JCheckBox filterCheckbox;
	private HistoryTextField filterField;
	private Box toolbarBox;
	private FavoritesMenuButton favorites;
	private PluginsMenuButton plugins;
	private BrowserView browserView;
	private Pattern filenameFilter;
	private int mode;
	private boolean multipleSelection;

	private boolean showHiddenFiles;
	private boolean sortMixFilesAndDirs;
	private boolean sortIgnoreCase;
	private boolean doubleClickClose;

	private boolean requestRunning;
	private boolean maybeReloadRequestRunning;
	//}}}

	//{{{ createMenuBar() method
	private JPanel createMenuBar()
	{
		JPanel menuBar = new JPanel();
		menuBar.setLayout(new BoxLayout(menuBar,BoxLayout.X_AXIS));
		menuBar.setBorder(new EmptyBorder(0,1,0,3));

		menuBar.add(new CommandsMenuButton());
		menuBar.add(Box.createHorizontalStrut(3));
		menuBar.add(plugins = new PluginsMenuButton());
		menuBar.add(Box.createHorizontalStrut(3));
		menuBar.add(favorites = new FavoritesMenuButton());

		return menuBar;
	} //}}}

	//{{{ createToolBar() method
	private Box createToolBar()
	{
		if(mode == BROWSER)
			return GUIUtilities.loadToolBar(actionContext,
				"vfs.browser.toolbar-browser");
		else
			return GUIUtilities.loadToolBar(actionContext,
				"vfs.browser.toolbar-dialog");
	} //}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		showHiddenFiles = jEdit.getBooleanProperty("vfs.browser.showHiddenFiles");
		sortMixFilesAndDirs = jEdit.getBooleanProperty("vfs.browser.sortMixFilesAndDirs");
		sortIgnoreCase = jEdit.getBooleanProperty("vfs.browser.sortIgnoreCase");
		doubleClickClose = jEdit.getBooleanProperty("vfs.browser.doubleClickClose");

		browserView.propertiesChanged();

		toolbarBox.removeAll();

		if(jEdit.getBooleanProperty("vfs.browser.showToolbar"))
		{
			Box toolbar = createToolBar();
			if(horizontalLayout)
				toolbarBox.add(toolbar);
			else
			{
				toolbar.add(Box.createGlue());
				toolbarBox.add(toolbar);
			}
		}

		if(jEdit.getBooleanProperty("vfs.browser.showMenubar"))
		{
			JPanel menubar = createMenuBar();
			if(horizontalLayout)
			{
				toolbarBox.add(Box.createHorizontalStrut(6));
				toolbarBox.add(menubar,0);
			}
			else
			{
				menubar.add(Box.createGlue());
				toolbarBox.add(menubar);
			}
		}
		else
			favorites = null;

		toolbarBox.add(Box.createGlue());

		revalidate();

		if(path != null)
			reloadDirectory();
	} //}}}

	/* We do this stuff because the browser is not able to handle
	 * more than one request yet */

	//{{{ startRequest() method
	private boolean startRequest()
	{
		if(requestRunning)
		{
			// dump stack trace for debugging purposes
			Log.log(Log.DEBUG,this,new Throwable("For debugging purposes"));

			GUIUtilities.error(this,"browser-multiple-io",null);
			return false;
		}
		else
		{
			requestRunning = true;
			return true;
		}
	} //}}}

	//{{{ updateFilterEnabled() method
	private void updateFilterEnabled()
	{
		filterField.setEnabled(filterCheckbox.isSelected());
	} //}}}

	//{{{ maybeReloadDirectory() method
	private void maybeReloadDirectory(String dir)
	{
		if(MiscUtilities.isURL(dir)
			&& MiscUtilities.getProtocolOfURL(dir).equals(
			FavoritesVFS.PROTOCOL))
		{
			if(favorites != null)
				favorites.popup = null;
		}

		// this is a dirty hack and it relies on the fact
		// that updates for parents are sent before updates
		// for the changed nodes themselves (if this was not
		// the case, the browser wouldn't be updated properly
		// on delete, etc).
		//
		// to avoid causing '> 1 request' errors, don't reload
		// directory if request already active
		if(maybeReloadRequestRunning)
		{
			//Log.log(Log.WARNING,this,"VFS update: request already in progress");
			return;
		}

		// save a file -> sends vfs update. if a VFS file dialog box
		// is shown from the same event frame as the save, the
		// VFSUpdate will be delivered before the directory is loaded,
		// and before the path is set.
		if(path != null)
		{
			try
			{
				maybeReloadRequestRunning = true;

				browserView.maybeReloadDirectory(dir);
			}
			finally
			{
				VFSManager.runInAWTThread(new Runnable()
				{
					public void run()
					{
						maybeReloadRequestRunning = false;
					}
				});
			}
		}
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == pathField || source == filterField
				|| source == filterCheckbox)
			{
				updateFilterEnabled();

				String path = pathField.getText();
				if(path != null)
					setDirectory(path);

				browserView.focusOnFileView();
			}
		}
	} //}}}

	//{{{ CommandsMenuButton class
	class CommandsMenuButton extends JButton
	{
		//{{{ CommandsMenuButton constructor
		CommandsMenuButton()
		{
			setText(jEdit.getProperty("vfs.browser.commands.label"));
			setIcon(GUIUtilities.loadIcon("ToolbarMenu.gif"));
			setHorizontalTextPosition(SwingConstants.LEADING);

			popup = new BrowserCommandsMenu(VFSBrowser.this,null);

			CommandsMenuButton.this.setRequestFocusEnabled(false);
			setMargin(new Insets(1,1,1,1));
			CommandsMenuButton.this.addMouseListener(new MouseHandler());

			if(OperatingSystem.isMacOSLF())
				CommandsMenuButton.this.putClientProperty("JButton.buttonType","toolbar");
		} //}}}

		BrowserCommandsMenu popup;

		//{{{ MouseHandler class
		class MouseHandler extends MouseAdapter
		{
			public void mousePressed(MouseEvent evt)
			{
				if(!popup.isVisible())
				{
					popup.update();

					GUIUtilities.showPopupMenu(
						popup,CommandsMenuButton.this,0,
						CommandsMenuButton.this.getHeight(),
						false);
				}
				else
				{
					popup.setVisible(false);
				}
			}
		} //}}}
	} //}}}

	//{{{ PluginsMenuButton class
	class PluginsMenuButton extends JButton
	{
		//{{{ PluginsMenuButton constructor
		PluginsMenuButton()
		{
			setText(jEdit.getProperty("vfs.browser.plugins.label"));
			setIcon(GUIUtilities.loadIcon("ToolbarMenu.gif"));
			setHorizontalTextPosition(SwingConstants.LEADING);

			PluginsMenuButton.this.setRequestFocusEnabled(false);
			setMargin(new Insets(1,1,1,1));
			PluginsMenuButton.this.addMouseListener(new MouseHandler());

			if(OperatingSystem.isMacOSLF())
				PluginsMenuButton.this.putClientProperty("JButton.buttonType","toolbar");
		} //}}}

		JPopupMenu popup;

		//{{{ updatePopupMenu() method
		void updatePopupMenu()
		{
			popup = null;
		} //}}}

		//{{{ createPopupMenu() method
		private void createPopupMenu()
		{
			if(popup != null)
				return;

			popup = (JPopupMenu)createPluginsMenu(new JPopupMenu(),true);
		} //}}}

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				VFS vfs = VFSManager.getVFSByName(evt.getActionCommand());
				String directory = vfs.showBrowseDialog(null,
					VFSBrowser.this);
				if(directory != null)
					setDirectory(directory);
			}
		} //}}}

		//{{{ MouseHandler class
		class MouseHandler extends MouseAdapter
		{
			public void mousePressed(MouseEvent evt)
			{
				createPopupMenu();

				if(!popup.isVisible())
				{
					GUIUtilities.showPopupMenu(
						popup,PluginsMenuButton.this,0,
						PluginsMenuButton.this.getHeight(),
						false);
				}
				else
				{
					popup.setVisible(false);
				}
			}
		} //}}}
	} //}}}

	//{{{ FavoritesMenuButton class
	class FavoritesMenuButton extends JButton
	{
		//{{{ FavoritesMenuButton constructor
		FavoritesMenuButton()
		{
			setText(jEdit.getProperty("vfs.browser.favorites.label"));
			setIcon(GUIUtilities.loadIcon("ToolbarMenu.gif"));
			setHorizontalTextPosition(SwingConstants.LEADING);

			FavoritesMenuButton.this.setRequestFocusEnabled(false);
			setMargin(new Insets(1,1,1,1));
			FavoritesMenuButton.this.addMouseListener(new MouseHandler());

			if(OperatingSystem.isMacOSLF())
				FavoritesMenuButton.this.putClientProperty("JButton.buttonType","toolbar");
		} //}}}

		JPopupMenu popup;

		//{{{ createPopupMenu() method
		void createPopupMenu()
		{
			popup = new JPopupMenu();
			ActionHandler actionHandler = new ActionHandler();

			JMenuItem mi = new JMenuItem(
				jEdit.getProperty(
				"vfs.browser.favorites"
				+ ".add-to-favorites.label"));
			mi.setActionCommand("add-to-favorites");
			mi.addActionListener(actionHandler);
			popup.add(mi);

			mi = new JMenuItem(
				jEdit.getProperty(
				"vfs.browser.favorites"
				+ ".edit-favorites.label"));
			mi.setActionCommand("dir@favorites:");
			mi.addActionListener(actionHandler);
			popup.add(mi);

			popup.addSeparator();

			VFSFile[] favorites = FavoritesVFS.getFavorites();
			if(favorites.length == 0)
			{
				mi = new JMenuItem(
					jEdit.getProperty(
					"vfs.browser.favorites"
					+ ".no-favorites.label"));
				mi.setEnabled(false);
				popup.add(mi);
			}
			else
			{
				MiscUtilities.quicksort(favorites,
					new VFS.DirectoryEntryCompare(
					sortMixFilesAndDirs,
					sortIgnoreCase));
				for(int i = 0; i < favorites.length; i++)
				{
					VFSFile favorite = favorites[i];
					mi = new JMenuItem(favorite.getPath());
					mi.setIcon(FileCellRenderer
						.getIconForFile(
						favorite,false));
					String cmd = (favorite.getType() ==
						VFSFile.FILE
						? "file@" : "dir@")
						+ favorite.getPath();
					mi.setActionCommand(cmd);
					mi.addActionListener(actionHandler);
					popup.add(mi);
				}
			}
		} //}}}

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				String actionCommand = evt.getActionCommand();
				if(actionCommand.equals("add-to-favorites"))
				{
					// if any directories are selected, add
					// them, otherwise add current directory
					VFSFile[] selected = getSelectedFiles();
					if(selected == null || selected.length == 0)
					{
						if(path.equals(FavoritesVFS.PROTOCOL + ":"))
						{
							GUIUtilities.error(VFSBrowser.this,
								"vfs.browser.recurse-favorites",
								null);
						}
						else
						{
							FavoritesVFS.addToFavorites(path,
								VFSFile.DIRECTORY);
						}
					}
					else
					{
						for(int i = 0; i < selected.length; i++)
						{
							VFSFile file = selected[i];
							FavoritesVFS.addToFavorites(file.getPath(),
								file.getType());
						}
					}
				}
				else if(actionCommand.startsWith("dir@"))
				{
					setDirectory(actionCommand.substring(4));
				}
				else if(actionCommand.startsWith("file@"))
				{
					switch(getMode())
					{
					case BROWSER:
						jEdit.openFile(view,actionCommand.substring(5));
						break;
					default:
						locateFile(actionCommand.substring(5));
						break;
					}
				}
			}
		} //}}}

		//{{{ MouseHandler class
		class MouseHandler extends MouseAdapter
		{
			public void mousePressed(MouseEvent evt)
			{
				if(popup != null && popup.isVisible())
				{
					popup.setVisible(false);
					return;
				}

				if(popup == null)
					createPopupMenu();

				GUIUtilities.showPopupMenu(
					popup,FavoritesMenuButton.this,0,
					FavoritesMenuButton.this.getHeight(),
					false);
			}
		} //}}}
	} //}}}

	//{{{ DirectoryLoadedAWTRequest class
	class DirectoryLoadedAWTRequest implements Runnable
	{
		private Object node;
		private Object[] loadInfo;
		private boolean addToHistory;

		DirectoryLoadedAWTRequest(Object node, Object[] loadInfo,
			boolean addToHistory)
		{
			this.node = node;
			this.loadInfo = loadInfo;
			this.addToHistory = addToHistory;
		}

		public void run()
		{
			String path = (String)loadInfo[0];
			if(path == null)
			{
				// there was an error
				return;
			}

			VFSFile[] list = (VFSFile[])loadInfo[1];

			if(node == null)
			{
				// This is the new, canonical path
				VFSBrowser.this.path = path;
				if(!pathField.getText().equals(path))
					pathField.setText(path);
				if(path.endsWith("/") ||
					path.endsWith(File.separator))
				{
					// ensure consistent history;
					// eg we don't want both
					// foo/ and foo
					path = path.substring(0,
						path.length() - 1);
				}

				if(addToHistory)
				{
					HistoryModel.getModel("vfs.browser.path")
						.addItem(path);
				}
			}

			boolean filterEnabled = filterCheckbox.isSelected();

			ArrayList directoryVector = new ArrayList();

			int directories = 0;
			int files = 0;
			int invisible = 0;

			if(list != null)
			{
				for(int i = 0; i < list.length; i++)
				{
					VFSFile file = list[i];
					if(file.isHidden() && !showHiddenFiles)
					{
						invisible++;
						continue;
					}

					if(file.getType() == VFSFile.FILE
						&& filterEnabled
						&& filenameFilter != null
						&& !filenameFilter.matcher(file.getName()).matches())
					{
						invisible++;
						continue;
					}

					if(file.getType() == VFSFile.FILE)
						files++;
					else
						directories++;

					directoryVector.add(file);
				}

				MiscUtilities.quicksort(directoryVector,
					new VFS.DirectoryEntryCompare(
					sortMixFilesAndDirs,
					sortIgnoreCase));
			}

			browserView.directoryLoaded(node,path,
				directoryVector);

			// to notify listeners that any existing
			// selection has been deactivated

			// turns out under some circumstances this
			// method can switch the current buffer in
			// BROWSER mode.

			// in any case, this is only needed for the
			// directory chooser (why?), so we add a
			// check. otherwise poor Rick will go insane.
			if(mode == CHOOSE_DIRECTORY_DIALOG)
				filesSelected();
		}

		public String toString()
		{
			return (String)loadInfo[0];
		}
	} //}}}

	//{{{ BrowserActionContext class
	static class BrowserActionContext extends ActionContext
	{
		/**
		 * If event source hierarchy contains a VFSDirectoryEntryTable,
		 * this is the currently selected files there. Otherwise, this
		 * is the currently selected item in the parent directory list.
		 */
		private VFSFile[] getSelectedFiles(EventObject evt,
			VFSBrowser browser)
		{
			Component source = (Component)evt.getSource();

			if(GUIUtilities.getComponentParent(source,JList.class)
				!= null)
			{
				Object[] selected = browser.getBrowserView()
					.getParentDirectoryList()
					.getSelectedValues();
				VFSFile[] returnValue = new VFSFile[
					selected.length];
				System.arraycopy(selected,0,returnValue,0,
					selected.length);
				return returnValue;
			}
			else
			{
				return browser.getSelectedFiles();
			}
		}

		public void invokeAction(EventObject evt, EditAction action)
		{
			VFSBrowser browser = (VFSBrowser)
				GUIUtilities.getComponentParent(
				(Component)evt.getSource(),
				VFSBrowser.class);

			VFSFile[] files = getSelectedFiles(evt,browser);

			// in the future we will want something better,
			// eg. having an 'evt' object passed to
			// EditAction.invoke().

			// for now, since all browser actions are
			// written in beanshell we set the 'browser'
			// variable directly.
			NameSpace global = BeanShell.getNameSpace();
			try
			{
				global.setVariable("browser",browser);
				global.setVariable("files",files);

				View view = browser.getView();
				// I guess ideally all browsers
				// should have views, but since they
				// don't, we just use the active view
				// in that case, since some actions
				// depend on a view being there and
				// I don't want to add checks to
				// them all
				if(view == null)
					view = jEdit.getActiveView();
				action.invoke(view);
			}
			catch(UtilEvalError err)
			{
				Log.log(Log.ERROR,this,err);
			}
			finally
			{
				try
				{
					global.setVariable("browser",null);
					global.setVariable("files",null);
				}
				catch(UtilEvalError err)
				{
					Log.log(Log.ERROR,this,err);
				}
			}
		}
	} //}}}

	//}}}
}
