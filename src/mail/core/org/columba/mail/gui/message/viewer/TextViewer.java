// The contents of this file are subject to the Mozilla Public License Version
// 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.mail.gui.message.viewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.columba.core.charset.CharsetOwnerInterface;
import org.columba.core.config.Config;
import org.columba.core.gui.focus.FocusManager;
import org.columba.core.gui.htmlviewer.IHTMLViewerPlugin;
import org.columba.core.gui.util.FontProperties;
import org.columba.core.io.DiskIO;
import org.columba.core.io.StreamUtils;
import org.columba.core.io.TempFileStore;
import org.columba.core.main.Main;
import org.columba.core.plugin.IExtension;
import org.columba.core.plugin.PluginManager;
import org.columba.core.plugin.exception.PluginException;
import org.columba.core.plugin.exception.PluginHandlerNotFoundException;
import org.columba.core.pluginhandler.HTMLViewerExtensionHandler;
import org.columba.core.xml.XmlElement;
import org.columba.mail.config.MailConfig;
import org.columba.mail.config.OptionsItem;
import org.columba.mail.folder.IMailbox;
import org.columba.mail.gui.frame.MailFrameMediator;
import org.columba.mail.gui.message.MessageController;
import org.columba.mail.gui.message.util.DocumentParser;
import org.columba.mail.parser.text.HtmlParser;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.message.MimeTree;

/**
 * IViewer displays message body text.
 * 
 * @author fdietz
 * 
 */
public class TextViewer extends JPanel implements IMimePartViewer, Observer,
		CaretListener {

	/** JDK 1.4+ logging framework logger, used for logging. */
	private static final Logger LOG = Logger
			.getLogger("org.columba.mail.gui.message.viewer");

	// parser to transform text to html
	private DocumentParser parser;

	// stylesheet is created dynamically because
	// user configurable fonts are used
	private String css = "";

	// enable/disable smilies configuration
	private XmlElement smilies;

	private boolean enableSmilies;

	// name of font
	private String name;

	// size of font
	private String size;

	// overwrite look and feel font settings
	private boolean overwrite;

	/*private String body;

	private URL url;*/
	
	private String body;

	/**
	 * if true, a html message is shown. Otherwise, plain/text
	 */
	private boolean htmlMessage;

	private MessageController mediator;

	private IHTMLViewerPlugin viewerPlugin;

	public TextViewer(MessageController mediator) {
		super();

		this.mediator = mediator;

		initHTMLViewerPlugin();

		setLayout(new BorderLayout());
		add(viewerPlugin.getView(), BorderLayout.CENTER);

		initConfiguration();

		initStyleSheet();

		// FocusManager.getInstance().registerComponent(new MyFocusOwner());

		mediator.addMouseListener(viewerPlugin.getView());
	}

	private void initHTMLViewerPlugin() {
		OptionsItem optionsItem = MailConfig.getInstance().getOptionsItem();
		boolean useSystemDefaultBrowser = optionsItem.getBooleanWithDefault(
				OptionsItem.MESSAGEVIEWER,
				OptionsItem.USE_SYSTEM_DEFAULT_BROWSER, false);

		if (useSystemDefaultBrowser) {
			viewerPlugin = createHTMLViewerPluginInstance("JDICHTMLViewerPlugin");
			// in case of an error -> fall-back to Swing's built-in JTextPane
			if ((viewerPlugin == null) || (viewerPlugin.initialized() == false)) {
				LOG.severe("Error while trying to load JDIC based html viewer -> falling back to Swing's JTextPane instead");

				viewerPlugin = createHTMLViewerPluginInstance("JavaHTMLViewerPlugin");
			}
		} else {
			viewerPlugin = createHTMLViewerPluginInstance("JavaHTMLViewerPlugin");
		}

	}

	private IHTMLViewerPlugin createHTMLViewerPluginInstance(String pluginId) {
		IHTMLViewerPlugin plugin = null;
		try {

			HTMLViewerExtensionHandler handler = (HTMLViewerExtensionHandler) PluginManager
					.getInstance().getHandler(HTMLViewerExtensionHandler.NAME);

			IExtension extension = handler.getExtension(pluginId);
			
			plugin = (IHTMLViewerPlugin) extension.instanciateExtension(null);

			return plugin;
		} catch (PluginHandlerNotFoundException e) {
			LOG.severe("Error while loading viewer plugin: " + e.getMessage());
			if (Main.DEBUG)
				e.printStackTrace();
		} catch (PluginException e) {
			LOG.severe("Error while loading viewer plugin: " + e.getMessage());
			if (Main.DEBUG)
				e.printStackTrace();
		} catch (Exception e) {
			LOG.severe("Error while loading viewer plugin: " + e.getMessage());
			if (Main.DEBUG)
				e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 */
	private void initConfiguration() {
		XmlElement gui = MailConfig.getInstance().get("options").getElement(
				"/options/gui");
		XmlElement messageviewer = gui.getElement("messageviewer");

		if (messageviewer == null) {
			messageviewer = gui.addSubElement("messageviewer");
		}

		messageviewer.addObserver(this);

		smilies = messageviewer.getElement("smilies");

		if (smilies == null) {
			smilies = messageviewer.addSubElement("smilies");
		}

		// register as configuration change listener
		smilies.addObserver(this);

		String enable = smilies.getAttribute("enabled", "true");

		if (enable.equals("true")) {
			enableSmilies = true;
		} else {
			enableSmilies = false;
		}

		XmlElement quote = messageviewer.getElement("quote");

		if (quote == null) {
			quote = messageviewer.addSubElement("quote");
		}

		// register as configuration change listener
		quote.addObserver(this);

		// TODO (@author fdietz): use value in initStyleSheet()
		String enabled = quote.getAttribute("enabled", "true");
		String color = quote.getAttribute("color", "0");

		// register for configuration changes
		Font font = FontProperties.getTextFont();
		name = font.getName();
		size = new Integer(font.getSize()).toString();

		XmlElement options = Config.getInstance().get("options").getElement(
				"/options");
		XmlElement gui1 = options.getElement("gui");
		XmlElement fonts = gui1.getElement("fonts");

		if (fonts == null) {
			fonts = gui1.addSubElement("fonts");
		}

		// register interest on configuratin changes
		fonts.addObserver(this);
	}

	/**
	 * @see org.columba.mail.gui.message.viewer.IMimePartViewer#view(org.columba.mail.folder.IMailbox,
	 *      java.lang.Object, java.lang.Integer[],
	 *      org.columba.mail.gui.frame.MailFrameMediator)
	 */
	public void view(IMailbox folder, Object uid, Integer[] address,
			MailFrameMediator mediator) throws Exception {

		MimePart bodyPart = null;
		InputStream bodyStream;

		MimeTree mimePartTree = folder.getMimePartTree(uid);

		bodyPart = mimePartTree.getFromAddress(address);

		if (bodyPart == null) {
			bodyStream = new ByteArrayInputStream("<No Message-Text>"
					.getBytes());
		} else {
			// Shall we use the HTML-IViewer?
			htmlMessage = bodyPart.getHeader().getMimeType().getSubtype()
					.equals("html");

			bodyStream = folder.getMimePartBodyStream(uid, bodyPart
					.getAddress());
		}

		// Which Charset shall we use ?
		Charset charset = ((CharsetOwnerInterface) mediator).getCharset();
		charset = MessageParser.extractCharset(charset, bodyPart);

		bodyStream = MessageParser.decodeBodyStream(charset, bodyPart,
				bodyStream);

		// Read Stream in String
		StringBuffer text = StreamUtils.readCharacterStream(bodyStream);

		// if HTML stripping is enabled
		if (isHTMLStrippingEnabled()) {
			// strip HTML message -> remove all HTML tags
			text = new StringBuffer(HtmlParser.stripHtmlTags(text.toString(),
					true));

			htmlMessage = false;
		}

		if (htmlMessage) {

			// this is a HTML message
			
			body = text.toString();

		} else {
			// this is a text/plain message

			body = MessageParser.transformTextToHTML(text.toString(), css,
					enableSmilies);

			// setText(body);

		}
	}

	private boolean isHTMLStrippingEnabled() {
		XmlElement html = MailConfig.getInstance().getMainFrameOptionsConfig()
				.getRoot().getElement("/options/html");

		return Boolean.valueOf(html.getAttribute("disable")).booleanValue();
	}

	/**
	 * 
	 * read text-properties from configuration and create a stylesheet for the
	 * html-document
	 * 
	 */
	private void initStyleSheet() {
		// read configuration from options.xml file
		// create css-stylesheet string
		// set font of html-element <P>
		css = "<style type=\"text/css\"><!-- .bodytext {font-family:\"" + name
				+ "\"; font-size:\"" + size + "pt; \"}"
				+ ".quoting {color:#949494;}; --></style>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.columba.mail.gui.config.general.MailOptionsDialog
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) {
		Font font = FontProperties.getTextFont();
		name = font.getName();
		size = new Integer(font.getSize()).toString();

		initStyleSheet();

		initHTMLViewerPlugin();
	}

	public String getSelectedText() {
		return viewerPlugin.getSelectedText();
	}

	/**
	 * @see org.columba.mail.gui.message.viewer.IViewer#getView()
	 */
	public JComponent getView() {
		return this;
	}

	/**
	 * @see org.columba.mail.gui.message.viewer.IViewer#updateGUI()
	 */
	public void updateGUI() throws Exception {
		viewerPlugin.view(body);
	}

	/**
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent arg0) {
		FocusManager.getInstance().updateActions();
	}

	/** ***************** FocusOwner interface ********************** */

	// class MyFocusOwner implements FocusOwner {
	// /**
	// * @see javax.swing.text.JTextComponent#copy()
	// */
	// public void copy() {
	// int start = getSelectionStart();
	// int stop = getSelectionEnd();
	//
	// StringWriter htmlSelection = new StringWriter();
	//
	// try {
	// htmlEditorKit.write(htmlSelection, getDocument(), start, stop
	// - start);
	//
	// Clipboard clipboard = getToolkit().getSystemClipboard();
	//
	// // Conversion of html text to plain
	// //TODO (@author karlpeder): make a DataFlavor that can handle
	// // HTML
	// // text
	// StringSelection selection = new StringSelection(HtmlParser
	// .htmlToText(htmlSelection.toString(), true));
	// clipboard.setContents(selection, selection);
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#cut()
	// */
	// public void cut() {
	// // not supported
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#delete()
	// */
	// public void delete() {
	// // not supported
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#getComponent()
	// */
	// public JComponent getComponent() {
	// return TextViewer.this;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isCopyActionEnabled()
	// */
	// public boolean isCopyActionEnabled() {
	//
	// if (getSelectedText() == null) {
	// return false;
	// }
	//
	// if (getSelectedText().length() > 0) {
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isCutActionEnabled()
	// */
	// public boolean isCutActionEnabled() {
	// // action not support
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isDeleteActionEnabled()
	// */
	// public boolean isDeleteActionEnabled() {
	// // action not supported
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isPasteActionEnabled()
	// */
	// public boolean isPasteActionEnabled() {
	// // action not supported
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isRedoActionEnabled()
	// */
	// public boolean isRedoActionEnabled() {
	// // action not supported
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isSelectAllActionEnabled()
	// */
	// public boolean isSelectAllActionEnabled() {
	// return true;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#isUndoActionEnabled()
	// */
	// public boolean isUndoActionEnabled() {
	// // action not supported
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#paste()
	// */
	// public void paste() {
	// // action not supported
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#redo()
	// */
	// public void redo() {
	// // action not supported
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#selectAll()
	// */
	// public void selectAll() {
	//
	// selectAll();
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.columba.core.gui.focus.FocusOwner#undo()
	// */
	// public void undo() {
	//
	// }
	// }
}