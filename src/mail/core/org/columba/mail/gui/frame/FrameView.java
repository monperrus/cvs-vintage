// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.mail.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.columba.core.config.WindowItem;
import org.columba.core.gui.statusbar.StatusBar;
import org.columba.core.gui.util.ImageLoader;
import org.columba.mail.config.MailConfig;
import org.columba.mail.gui.attachment.AttachmentView;
import org.columba.mail.gui.frame.util.SplitPane;
import org.columba.mail.gui.header.HeaderView;
import org.columba.mail.gui.message.MessageView;
import org.columba.mail.gui.table.FilterToolbar;
import org.columba.mail.gui.table.TableView;
import org.columba.mail.gui.tree.TreeView;
import org.columba.mail.gui.tree.util.FolderInfoPanel;

public class FrameView extends JFrame {
	private MailToolBar toolbar;

	private StatusBar statusBar;
	public JSplitPane mainSplitPane;
	public SplitPane rightSplitPane;

	private JPanel pane;

	private FolderInfoPanel folderInfoPanel;

	public ResourceBundle guiLabels;
	
	private JPanel tablePanel;
	
	FilterToolbar filterToolbar;
		HeaderView header;

	public FrameView() {
		//MainInterface.mainFrame = this;
		this.setIconImage(
			ImageLoader.getImageIcon("ColumbaIcon.png").getImage());

		//changeToolbars();

	}

	public void maximize() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);

		// FIXME: this works only with JDK1.4
		// has to be added with org.columba.core.util.Compatibility-class
		//setExtendedState(MAXIMIZED_BOTH);

	}
	
	public void showAttachmentViewer()
	{
		rightSplitPane.showAttachmentViewer();
	}
	
	public void hideAttachmentViewer()
	{
		rightSplitPane.hideAttachmentViewer();
	}
	
	
	public void setFolderInfoPanel(FolderInfoPanel f) {
		this.folderInfoPanel = f;
	}

	

	public void init(
		TreeView tree,
		TableView table,
		FilterToolbar filterToolbar,
		HeaderView header,
		MessageView message,
		AttachmentView attachment,
		StatusBar statusBar) {

		this.filterToolbar = filterToolbar;
		this.header = header;
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = (JPanel) this.getContentPane();

		setTitle("Columba v" + org.columba.main.MainInterface.version);

		this.getContentPane().add(statusBar, BorderLayout.SOUTH);

		this.statusBar = statusBar;
		
		mainSplitPane = new JSplitPane();
		//mainSplitPane.setDividerSize(5);

		//mainSplitPane.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

		this.getContentPane().add(mainSplitPane, BorderLayout.CENTER);

		mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		mainSplitPane.add(new JScrollPane(tree), JSplitPane.LEFT);

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout( new BorderLayout() );
		messagePanel.add( header, BorderLayout.NORTH );
		messagePanel.add( message, BorderLayout.CENTER );
		
		tablePanel = new JPanel();
		tablePanel.setLayout( new BorderLayout() );
		
		if (MailConfig
				.getMainFrameOptionsConfig()
				.getWindowItem()
				.isShowFilterToolbar()
				== true)
		tablePanel.add( filterToolbar, BorderLayout.NORTH );
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.getViewport().setBackground(Color.white);
		tablePanel.add( tableScrollPane, BorderLayout.CENTER );
		rightSplitPane =
			new SplitPane(
				tablePanel,
				messagePanel,
				new JScrollPane(attachment));

		mainSplitPane.add(rightSplitPane, JSplitPane.RIGHT);

		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		// same as menu
		

		if (MailConfig
			.getMainFrameOptionsConfig()
			.getWindowItem()
			.isShowToolbar()
			== true) {
			pane.add(toolbar);

		}

		if (MailConfig
			.getMainFrameOptionsConfig()
			.getWindowItem()
			.isShowFolderInfo()
			== true) {

			pane.add(folderInfoPanel);
		}

		getContentPane().add(pane, BorderLayout.NORTH);

		int count = MailConfig.getAccountList().count();

		if (count == 0) {
			pack();
			rightSplitPane.setDividerLocation(150);
		} else {
			mainSplitPane.setDividerLocation(
				MailConfig
					.getMainFrameOptionsConfig()
					.getWindowItem()
					.getMainSplitPane());

			rightSplitPane.setDividerLocation(
				MailConfig
					.getMainFrameOptionsConfig()
					.getWindowItem()
					.getRightSplitPane());
		}

	}

	public void setToolBar( MailToolBar toolBar )
	{
		this.toolbar = toolBar;
	}

	public void hideToolbar(boolean b) {
		pane.remove(toolbar);

		validate();
		repaint();

	}

	public void showToolbar(boolean b) {

		if (b) {
			pane.removeAll();
			pane.add(toolbar);
			pane.add(folderInfoPanel);

			validate();
			repaint();
		} else {

			pane.add(toolbar);
			validate();
			repaint();
		}

	}

	public void hideFolderInfo(boolean b) {

		pane.remove(folderInfoPanel);
		validate();
		repaint();

	}

	public void showFolderInfo(boolean b) {

		if (b) {
			pane.removeAll();
			pane.add(toolbar);
			pane.add(folderInfoPanel);

			validate();
			repaint();
		} else {

			pane.add(folderInfoPanel);

			validate();
			repaint();
		}

	}
	
	public void showFilterToolbar()
	{
		tablePanel.add( filterToolbar, BorderLayout.NORTH );
		tablePanel.validate();
		repaint();
	}
	
	public void hideFilterToolbar()
	{
		tablePanel.remove(filterToolbar);	
		tablePanel.validate();
		repaint();
	}
	
	public void saveWindowPosition() {

		java.awt.Dimension d =getSize();
		//MailConfig.saveWindowPosition( 0, 0, d.width, d.height);
		WindowItem item =
			MailConfig.getMainFrameOptionsConfig().getWindowItem();
		item.setXPosition(0);
		item.setYPosition(0);
		item.setWidth(d.width);
		item.setHeight(d.height);

		item.setMainSplitPane(mainSplitPane.getDividerLocation());
		item.setRightSplitPane(rightSplitPane.getDividerLocation());

	}

}