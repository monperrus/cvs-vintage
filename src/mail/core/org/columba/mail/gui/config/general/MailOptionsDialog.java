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

package org.columba.mail.gui.config.general;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import net.javaprog.ui.wizard.plaf.basic.SingleSideEtchedBorder;

import org.columba.core.gui.util.DefaultFormBuilder;
import org.columba.core.xml.XmlElement;
import org.columba.mail.config.MailConfig;
import org.columba.mail.util.MailResourceLoader;

import com.jgoodies.forms.layout.FormLayout;

/**
 * Mail General Options Dialog
 * 
 * @author fdietz
 */
public class MailOptionsDialog extends JDialog implements ActionListener {

	JButton okButton;
	JButton cancelButton;
	JButton helpButton;

	JCheckBox markCheckBox;
	JSpinner markSpinner;

	JCheckBox preferHtmlCheckBox;
	JCheckBox enableSmiliesCheckBox;
	JCheckBox quotedColorCheckBox;
	JButton quotedColorButton;

	JCheckBox emptyTrashCheckBox;

	JLabel spellLabel;
	JButton spellButton;

	JCheckBox emptySubjectCheckBox;
	JCheckBox sendHtmlMultipartCheckBox;

	JLabel forwardLabel;
	JComboBox forwardComboBox;

	public MailOptionsDialog(JFrame frame) {
		super(
			frame,
			MailResourceLoader.getString("dialog", "general", "dialog_title"),
			true);

		initComponents();

		layoutComponents();

		updateComponents(true);

		pack();

		setLocationRelativeTo(null);

		setVisible(true);
	}

	public void updateComponents(boolean b) {

		if (b) {

			XmlElement options =
				MailConfig.get("options").getElement("/options");

			XmlElement gui = options.getElement("gui");

			XmlElement messageviewer = gui.getElement("messageviewer");
			if (messageviewer == null) {
				messageviewer = gui.addSubElement("messageviewer");
			}

			XmlElement markasread = options.getElement("markasread");

			String delay = markasread.getAttribute("delay", "2");
			String enable = markasread.getAttribute("enabled", "true");
			if (enable.equals("true"))
				markCheckBox.setSelected(true);
			else
				markCheckBox.setSelected(false);

			markSpinner.setValue(new Integer(delay));

			XmlElement smilies = messageviewer.getElement("smilies");
			if (smilies == null) {
				smilies = messageviewer.addSubElement("smilies");
			}

			String enableSmilies = smilies.getAttribute("enabled", "true");
			if (enableSmilies.equals("true"))
				enableSmiliesCheckBox.setSelected(true);
			else
				enableSmiliesCheckBox.setSelected(false);

			/*
			XmlElement quote = messageviewer.getElement("quote");
			if (quote == null) {
				quote = messageviewer.addSubElement("quote");
			}

			String enabled = quote.getAttribute("enabled", "true");
			// default color value in html-slang = #949494
			String color = quote.getAttribute("color", "0");
			if (enabled.equals("true"))
				quotedColorCheckBox.setSelected(true);
			else
				quotedColorCheckBox.setSelected(false);

			int c = Integer.parseInt(color);
			quotedColorButton.setBackground(new Color(c));
			*/
			
			XmlElement html = options.getElement("html");

			boolean preferhtml =
				new Boolean(html.getAttribute("prefer")).booleanValue();
			if (preferhtml == true)
				preferHtmlCheckBox.setSelected(true);
			else
				preferHtmlCheckBox.setSelected(false);

			XmlElement composerOptions =
				MailConfig.getComposerOptionsConfig().getRoot().getElement(
					"/options");
			XmlElement subject = composerOptions.getElement("subject");
			if (subject == null) {
				subject = composerOptions.addSubElement("subject");
			}

			String askSubject = subject.getAttribute("ask_if_empty", "true");
			if (askSubject.equals("true"))
				emptySubjectCheckBox.setSelected(true);
			else
				emptySubjectCheckBox.setSelected(false);

			XmlElement composerHtml = composerOptions.getElement("html");
			if (composerHtml == null) {
				composerHtml = composerOptions.addSubElement("html");
			}
			
			String sendHtml = composerHtml.getAttribute(
					"send_as_multipart", "true");
			if (sendHtml.equals("true")) {
				sendHtmlMultipartCheckBox.setSelected(true);
			} else {
				sendHtmlMultipartCheckBox.setSelected(false);
			}

			XmlElement forward = composerOptions.getElement("forward");
			if (forward == null) {
				forward = composerOptions.addSubElement("forward");
			}

			String forwardStyle = forward.getAttribute("style", "attachment");
			if (forwardStyle.equals("attachment"))
				forwardComboBox.setSelectedIndex(0);
			else
				forwardComboBox.setSelectedIndex(1);
				
			/*
			// composer
			String path =
				MailConfig.getComposerOptionsConfig().getSpellcheckItem().get(
					"executable");
			spellButton.setText(path);
			*/
			

		} else {

			XmlElement options =
				MailConfig.get("options").getElement("/options");

			XmlElement gui = options.getElement("gui");

			XmlElement messageviewer = gui.getElement("messageviewer");

			XmlElement markasread = options.getElement("markasread");

			markasread.addAttribute(
				"delay",
				((Integer) markSpinner.getValue()).toString());

			if (markCheckBox.isSelected())
				markasread.addAttribute("enabled", "true");
			else
				markasread.addAttribute("enabled", "false");

			// notify configuration changes listeners	
			// @see org.columba.mail.gui.table.util.MarkAsReadTimer		
			markasread.notifyObservers();

			XmlElement smilies = messageviewer.getElement("smilies");
			if (enableSmiliesCheckBox.isSelected())
				smilies.addAttribute("enabled", "true");
			else
				smilies.addAttribute("enabled", "false");

			// send notification event
			// @see org.columba.mail.gui.message.BodyTextViewer
			smilies.notifyObservers();

			XmlElement quote = messageviewer.getElement("quote");
			Color color = quotedColorButton.getBackground();
			quote.addAttribute("color", new Integer(color.getRGB()).toString());
			if (quotedColorCheckBox.isSelected())
				quote.addAttribute("enabled", "true");
			else
				quote.addAttribute("enabled", "false");

			// send notifaction event
			// @see org.columba.mail.gui.message.BodyTextViewer
			quote.notifyObservers();

			XmlElement html = options.getElement("html");

			if (preferHtmlCheckBox.isSelected())
				html.addAttribute("prefer", Boolean.TRUE.toString());
			else
				html.addAttribute("prefer", Boolean.FALSE.toString());

			XmlElement composerOptions =
				MailConfig.getComposerOptionsConfig().getRoot().getElement(
					"/options");

			XmlElement subject = composerOptions.getElement("subject");
			if (emptySubjectCheckBox.isSelected())
				subject.addAttribute("ask_if_empty", "true");
			else
				subject.addAttribute("ask_if_empty", "false");

			// notify listeners
			// @see org.columba.mail.gui.composer.SubjectController
			subject.notifyObservers();

			XmlElement composerHtml = composerOptions.getElement("html");
			if (sendHtmlMultipartCheckBox.isSelected()) {
				composerHtml.addAttribute("send_as_multipart", "true");
			} else {
				composerHtml.addAttribute("send_as_multipart", "false");
			}

			// notify listeners
			composerHtml.notifyObservers();

			XmlElement forward = composerOptions.getElement("forward");
			if (forwardComboBox.getSelectedIndex() == 0)
				forward.addAttribute("style", "attachment");
			else
				forward.addAttribute("style", "inline");

			// notify listeners
			// @see org.columba.mail.gui.table.action.ForwardAction	
			forward.notifyObservers();

			/*
			// composer
			MailConfig.getComposerOptionsConfig().getSpellcheckItem().set(
				"executable",
				spellButton.getText());
			*/
		}
	}

	protected void initComponents() {

		// general
		markCheckBox =
			new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "mark_messages_read"));

		markSpinner = new JSpinner();
		markSpinner.setModel(new SpinnerNumberModel(1, 0, 99, 1));

		emptyTrashCheckBox = new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "empty_trash"));
		emptyTrashCheckBox.setEnabled(false);

		enableSmiliesCheckBox = new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "enable_smilies"));

		quotedColorCheckBox = new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "color_quoted_text"));
		quotedColorButton = new JButton("..");
		quotedColorButton.setActionCommand("COLOR");
		quotedColorButton.addActionListener(this);

		preferHtmlCheckBox =
			new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "prefer_html"));

		// composer
		spellLabel =
			new JLabel(
				MailResourceLoader.getString(
					"dialog", "general", "aspell_path"));

		spellButton = new JButton("aspell.exe");
		spellButton.setActionCommand("PATH");
		spellButton.addActionListener(this);
		spellLabel.setLabelFor(spellButton);

		emptySubjectCheckBox =
			new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "ask_on_empty_subject"));

		sendHtmlMultipartCheckBox =
			new JCheckBox(
				MailResourceLoader.getString(
					"dialog", "general", "send_html_multipart"));

		forwardLabel = new JLabel(
				MailResourceLoader.getString(
					"dialog", "general", "forward_as"));
		String[] items = {
			MailResourceLoader.getString(
					"dialog", "general", "forward_as_attachment"),
			MailResourceLoader.getString(
					"dialog", "general", "forward_as_quoted")
		};

		forwardComboBox = new JComboBox(items);

		// button panel
		okButton = new JButton(MailResourceLoader.getString("global", "ok"));
		//mnemonic
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);

		cancelButton =
			new JButton(MailResourceLoader.getString("global", "cancel"));
		//mnemonic
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);

		helpButton =
			new JButton(MailResourceLoader.getString("global", "help"));
		helpButton.setActionCommand("HELP");
		helpButton.addActionListener(this);

	}

	protected void layoutComponents() {

		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		// Create a FormLayout instance. 
		FormLayout layout =
			new FormLayout(
				"12dlu, default, 3dlu, max(10dlu;default), 3dlu, default",
			// 3 columns
	""); // rows are added dynamically (no need to define them here)

		// create a form builder
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		// create EmptyBorder between components and dialog-frame 
		builder.setDefaultDialogBorder();

		// skip the first column
		builder.setLeadingColumnOffset(1);

		// Add components to the panel:
		builder.appendSeparator(
				MailResourceLoader.getString(
					"dialog", "general", "general_options"));
		builder.nextLine();

		builder.append(preferHtmlCheckBox, 4);
		builder.nextLine();

		builder.append(enableSmiliesCheckBox, 4);
		builder.nextLine();

		// its maybe better to leave this option out of the dialog
		// -> make it configurable in the xml file anyway
		/*
		builder.append(quotedColorCheckBox, quotedColorButton);
		builder.nextLine();
		*/
		
		builder.append(markCheckBox, markSpinner);

		builder.nextLine();

		builder.appendSeparator(
				MailResourceLoader.getString(
					"dialog", "general", "composing_messages"));
		builder.nextLine();

		builder.append(emptySubjectCheckBox, 4);
		builder.nextLine();
		
		builder.append(sendHtmlMultipartCheckBox, 4);
		builder.nextLine();

		builder.append(forwardLabel, forwardComboBox);
		builder.nextLine();

		//layout.setRowGroups(new int[][]{ {1, 3, 5, 7, 9, 11, 13, 15} });

		/*
		builder.append(spellLabel, spellButton);
		builder.nextLine();
		*/

		contentPane.add(builder.getPanel(), BorderLayout.CENTER);

		// init bottom panel with OK, Cancel buttons

		JPanel bottomPanel = new JPanel(new BorderLayout(0, 0));
		bottomPanel.setBorder(new SingleSideEtchedBorder(SwingConstants.TOP));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));

		buttonPanel.add(okButton);

		buttonPanel.add(cancelButton);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		getRootPane().registerKeyboardAction(
			this,
			"CANCEL",
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();

		if (action.equals("OK")) {

			setVisible(false);

			updateComponents(false);

		} else if (action.equals("CANCEL")) {

			setVisible(false);

		} else if (action.equals("PATH")) {
			final JFileChooser fc = new JFileChooser();

			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				spellButton.setText(file.getPath());
			}
		} else if (action.equals("COLOR")) {
			//Set up color chooser for setting quoted color
			Color newColor =
				JColorChooser.showDialog(
					this,
					MailResourceLoader.getString(
							"dialog", "general", "choose_text_color"),
					null);
			if (newColor != null) {
				quotedColorButton.setBackground(newColor);
			}
		}
	}
}
