/*
 * VFSFileChooserDialog.java - VFS file chooser
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
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
//}}}

/**
 * Wraps the VFS browser in a modal dialog.
 * @author Slava Pestov
 * @version $Id: VFSFileChooserDialog.java,v 1.39 2003/05/26 00:15:41 spestov Exp $
 */
public class VFSFileChooserDialog extends EnhancedDialog
{
	//{{{ VFSFileChooserDialog constructor
	public VFSFileChooserDialog(View view, String path,
		int mode, boolean multipleSelection)
	{
		super(view,jEdit.getProperty("vfs.browser.title"),true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		String name;
		if(mode == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
			name = null;
		else if(path == null || path.endsWith(File.separator)
			|| path.endsWith("/"))
		{
			name = null;
		}
		else
		{
			VFS vfs = VFSManager.getVFSForPath(path);
			name = vfs.getFileName(path);
			path = vfs.getParentOfPath(path);
		}

		browser = new VFSBrowser(view,path,mode,multipleSelection,null);
		browser.getBrowserView().getTable().setRequestFocusEnabled(false);
		browser.getBrowserView().getParentDirectoryList()
			.setRequestFocusEnabled(false);
		/* browser.getBrowserView().getTable().addKeyListener(new KeyHandler()); */
		browser.addBrowserListener(new BrowserHandler());
		content.add(BorderLayout.CENTER,browser);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(12,0,0,0));

		filenameField = new VFSFileNameField(browser,null);
		filenameField.setText(name);
		filenameField.selectAll();
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		box.add(filenameField);
		box.add(Box.createGlue());

		if(mode != VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
		{
			JLabel label = new JLabel(jEdit.getProperty("vfs.browser.dialog.filename"));
			label.setDisplayedMnemonic(jEdit.getProperty(
				"vfs.browser.dialog.filename.mnemonic").charAt(0));
			label.setLabelFor(filenameField);
			panel.add(label);
			panel.add(Box.createHorizontalStrut(12));

			panel.add(box);

			panel.add(Box.createHorizontalStrut(12));
		}
		else
			panel.add(Box.createGlue());

		if(mode == VFSBrowser.BROWSER || mode == VFSBrowser.OPEN_DIALOG
			|| mode == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
		{
			GUIUtilities.requestFocus(this,browser.getBrowserView().getTable());
		}
		else
		{
			GUIUtilities.requestFocus(this,filenameField);
		}

		ok = new JButton();
		getRootPane().setDefaultButton(ok);

		switch(mode)
		{
		case VFSBrowser.OPEN_DIALOG:
		case VFSBrowser.BROWSER_DIALOG:
			ok.setText(jEdit.getProperty("vfs.browser.dialog.open"));
			break;
		case VFSBrowser.CHOOSE_DIRECTORY_DIALOG:
			ok.setText(jEdit.getProperty("vfs.browser.dialog.choose-dir"));
			// so that it doesn't resize...
			Dimension dim = ok.getPreferredSize();
			ok.setPreferredSize(dim);
			break;
		case VFSBrowser.SAVE_DIALOG:
			ok.setText(jEdit.getProperty("vfs.browser.dialog.save"));
			break;
		}

		ok.addActionListener(new ActionHandler());
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		panel.add(cancel);

		content.add(BorderLayout.SOUTH,panel);

		VFSManager.getIOThreadPool().addProgressListener(
			workThreadHandler = new WorkThreadHandler());

		pack();
		GUIUtilities.loadGeometry(this,"vfs.browser.dialog");
		show();
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		GUIUtilities.saveGeometry(this,"vfs.browser.dialog");
		VFSManager.getIOThreadPool().removeProgressListener(workThreadHandler);
		super.dispose();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		VFS.DirectoryEntry[] files = browser.getSelectedFiles();

		if(files.length != 0)
		{
			browser.filesActivated(VFSBrowser.M_OPEN,false);
		}
		else
		{
			if(browser.getMode() == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
			{
				isOK = true;
				dispose();
				return;
			}

			String filename = filenameField.getText();
			if(filename == null || filename.length() == 0)
			{
				getToolkit().beep();
				return;
			}

			String bufferDir = browser.getView().getBuffer()
				.getDirectory();
			if(filename.equals("-"))
				filename = bufferDir;
			else if(filename.startsWith("-/")
				|| filename.startsWith("-" + File.separator))
			{
				filename = MiscUtilities.constructPath(
					bufferDir,filename.substring(2));
			}

			final int[] type = { -1 };
			final String path = MiscUtilities.constructPath(
				browser.getDirectory(),filename);
			final VFS vfs = VFSManager.getVFSForPath(path);
			Object session = vfs.createVFSSession(path,this);
			if(session == null)
				return;

			VFSManager.runInWorkThread(new GetFileTypeRequest(
				vfs,session,path,type));
			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					switch(type[0])
					{
					case VFS.DirectoryEntry.FILE:
						if(vfs instanceof FileVFS)
						{
							if(doFileExistsWarning(path))
								break;
						}
						isOK = true;
						if(browser.getMode() == VFSBrowser.BROWSER_DIALOG)
						{
							Hashtable props = new Hashtable();
							props.put(Buffer.ENCODING,browser.currentEncoding);
							jEdit.openFile(browser.getView(),
								browser.getDirectory(),
								path,false,props);
						}
						dispose();
						break;
					case VFS.DirectoryEntry.DIRECTORY:
					case VFS.DirectoryEntry.FILESYSTEM:
						browser.setDirectory(path);
						break;
					}
				}
			});
		}
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}

	//{{{ getSelectedFiles() method
	public String[] getSelectedFiles()
	{
		if(!isOK)
			return null;

		String filename = filenameField.getText();

		if(browser.getMode() == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
		{
			return new String[] { browser.getDirectory() };
		}
		else if(filename != null && filename.length() != 0)
		{
			String path = browser.getDirectory();
			return new String[] { MiscUtilities.constructPath(
				path,filename) };
		}
		else
		{
			Vector vector = new Vector();
			VFS.DirectoryEntry[] selectedFiles = browser.getSelectedFiles();
			for(int i = 0; i < selectedFiles.length; i++)
			{
				VFS.DirectoryEntry file = selectedFiles[i];
				if(file.type == VFS.DirectoryEntry.FILE)
					vector.addElement(file.path);
			}
			String[] retVal = new String[vector.size()];
			vector.copyInto(retVal);
			return retVal;
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private VFSBrowser browser;
	private VFSFileNameField filenameField;
	private JButton ok;
	private JButton cancel;
	private boolean isOK;
	private WorkThreadHandler workThreadHandler;
	//}}}

	//{{{ doFileExistsWarning() method
	private boolean doFileExistsWarning(String filename)
	{
		if(browser.getMode() == VFSBrowser.SAVE_DIALOG
			&& new File(filename).exists())
		{
			String[] args = { MiscUtilities.getFileName(filename) };
			int result = GUIUtilities.confirm(browser,
				"fileexists",args,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if(result != JOptionPane.YES_OPTION)
				return true;
		}

		return false;
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == ok)
			{
				if(!browser.getDirectory().equals(
					browser.getDirectoryField().getText()))
				{
					browser.setDirectory(browser.getDirectoryField().getText());
				}
				else
					ok();
			}
			else if(evt.getSource() == cancel)
				cancel();
		}
	} //}}}

	//{{{ BrowserHandler class
	class BrowserHandler implements BrowserListener
	{
		//{{{ filesSelected() method
		public void filesSelected(VFSBrowser browser, VFS.DirectoryEntry[] files)
		{
			if(files.length == 0)
			{
				if(browser.getMode() == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
				{
					ok.setText(jEdit.getProperty(
						"vfs.browser.dialog.choose-dir"));
				}
				return;
			}
			else if(files.length == 1)
			{
				if(browser.getMode() == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
				{
					ok.setText(jEdit.getProperty(
						"vfs.browser.dialog.open"));
				}

				VFS.DirectoryEntry file = files[0];
				if(file.type == VFS.DirectoryEntry.FILE)
				{
					String path = file.path;
					String directory = browser.getDirectory();
					String parent = MiscUtilities
						.getParentOfPath(path);
					if(VFSBrowser.pathsEqual(parent,directory))
						path = file.name;

					filenameField.setText(path);
					filenameField.selectAll();
				}
			}
			else
			{
				if(browser.getMode() == VFSBrowser.CHOOSE_DIRECTORY_DIALOG)
				{
					ok.setText(jEdit.getProperty(
						"vfs.browser.dialog.open"));
				}

				filenameField.setText(null);
			}
		} //}}}

		//{{{ filesActivated() method
		public void filesActivated(VFSBrowser browser, VFS.DirectoryEntry[] files)
		{
			filenameField.selectAll();

			if(files.length == 0)
			{
				// user pressed enter when the vfs table or
				// file name field has focus, with nothing
				// selected.
				ok();
				return;
			}

			for(int i = 0; i < files.length; i++)
			{
				if(files[i].type == VFS.DirectoryEntry.FILE)
				{
					String path = files[i].path;
					VFS vfs = VFSManager.getVFSForPath(path);
					if(browser.getMode() == VFSBrowser.SAVE_DIALOG
						&& vfs instanceof FileVFS)
					{
						if(doFileExistsWarning(path))
							return;
					}

					isOK = true;
					filenameField.setText(null);
					dispose();
					return;
				}
				else
					return;
			}
		} //}}}
	} //}}}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{
		public void keyTyped(KeyEvent evt)
		{
			switch(evt.getKeyChar())
			{
			case '/':
			case '-':
			case '~':
				filenameField.processKeyEvent(evt);
				filenameField.requestFocus();
				break;
			}
		}
	} //}}}

	//{{{ WorkThreadListener class
	class WorkThreadHandler implements WorkThreadProgressListener
	{
		//{{{ statusUpdate() method
		public void statusUpdate(WorkThreadPool threadPool, int threadIndex)
		{
			// synchronize with hide/showWaitCursor()
			synchronized(VFSFileChooserDialog.this)
			{
				int requestCount = threadPool.getRequestCount();
				if(requestCount == 0)
				{
					getContentPane().setCursor(
						Cursor.getDefaultCursor());
				}
				else if(requestCount >= 1)
				{
					getContentPane().setCursor(
						Cursor.getPredefinedCursor(
						Cursor.WAIT_CURSOR));
				}
			}
		} //}}}

		//{{{ progressUpdate() method
		public void progressUpdate(WorkThreadPool threadPool, int threadIndex)
		{
		} //}}}
	} //}}}

	//{{{ GetFileTypeRequest class
	class GetFileTypeRequest implements Runnable
	{
		VFS    vfs;
		Object session;
		String path;
		int[]  type;

		GetFileTypeRequest(VFS vfs, Object session,
			String path, int[] type)
		{
			this.vfs     = vfs;
			this.session = session;
			this.path    = path;
			this.type    = type;
		}

		public void run()
		{
			try
			{
				VFS.DirectoryEntry entry
					= vfs._getDirectoryEntry(
						session,
						path,
						browser);
				if(entry == null)
				{
					// non-existent file
					type[0] = VFS.DirectoryEntry.FILE;
				}
				else
					type[0] = entry.type;
			}
			catch(IOException e)
			{
				Log.log(Log.ERROR,this,e);
				VFSManager.error(browser,path,
					"ioerror",
					new String[]
					{ e.toString() });
				return;
			}
			finally
			{
				try
				{
					vfs._endVFSSession(
						session,
						browser);
				}
				catch(IOException e)
				{
					Log.log(Log.ERROR,this,e);
					VFSManager.error(browser,path,
						"ioerror",
						new String[]
						{ e.toString() });
					return;
				}
			}
		}
	} //}}}

	//}}}
}
