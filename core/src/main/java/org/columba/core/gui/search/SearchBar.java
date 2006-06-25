package org.columba.core.gui.search;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.columba.core.main.MainInterface;
import org.columba.core.resourceloader.IconKeys;
import org.columba.core.resourceloader.ImageLoader;
import org.columba.core.search.api.ISearchCriteria;
import org.columba.core.search.api.ISearchManager;
import org.columba.core.search.api.ISearchProvider;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class SearchBar extends JPanel implements DocumentListener, KeyListener{

	private IconTextField textField;

	private ImageIcon icon = ImageLoader.getSmallIcon(IconKeys.EDIT_FIND);

	private JButton button;
	
	private ActionListener listener;
	
	public SearchBar() {
		super();

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		textField = new IconTextField(icon, 10);
		textField.setPopupMenu(createPopupMenu(""));

		button = new JButton("Search");
		button.setActionCommand("ALL");
		button.setMnemonic('s');

		
		FormLayout layout = new FormLayout(
				"fill:default:grow, 3dlu, pref",
				// 2 columns
				"");

		// create a form builder
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);

		builder.append(textField);
		builder.append(button);
		
		textField.getDocument().addDocumentListener(this);
		textField.addKeyListener(this);
	}

	public void addActionListener(ActionListener listener) {
		this.listener = listener;
		
		button.addActionListener(listener);
	}
	
	private JPopupMenu createPopupMenu(String searchTerm) {
		JPopupMenu menu = new JPopupMenu();
		ISearchManager manager = MainInterface.searchManager;
		List<ISearchProvider> list = manager.getAllProviders();
		Iterator<ISearchProvider> it = list.iterator();
		while (it.hasNext()) {
			ISearchProvider p = it.next();
			
			ISearchCriteria c = p.getCriteria(searchTerm);
			if ( p == null ) continue;
			
			JMenuItem m = new JMenuItem(c.getName());
			m.setToolTipText(c.getDescription());
			m.setActionCommand(p.getName());
			m.addActionListener(listener);
			menu.add(m);
		}
		
		return menu;
	}

	public void install(JMenuBar menubar) {
		if (menubar == null)
			throw new IllegalArgumentException("menubar == null");

		Component box = Box.createHorizontalGlue();
		menubar.add(box);

		menubar.add(this);
	}
	
	public String getSearchTerm() {
		return textField.getText();
	}

	public void insertUpdate(DocumentEvent e) {
		textField.setPopupMenu(createPopupMenu(getSearchTerm()));
	}

	public void removeUpdate(DocumentEvent e) {
		textField.setPopupMenu(createPopupMenu(getSearchTerm()));
	}

	public void changedUpdate(DocumentEvent e) {
		textField.setPopupMenu(createPopupMenu(getSearchTerm()));
	}
	
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		char ch = e.getKeyChar();

		if (ch == KeyEvent.VK_ENTER) {
			listener.actionPerformed(new ActionEvent(this, -1, "ALL"));
		}
	}
	
}
