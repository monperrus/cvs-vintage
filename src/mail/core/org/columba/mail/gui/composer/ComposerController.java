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

package org.columba.mail.gui.composer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ContainerListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.columba.core.charset.CharsetEvent;
import org.columba.core.charset.CharsetListener;
import org.columba.core.charset.CharsetOwnerInterface;
import org.columba.core.config.ViewItem;
import org.columba.core.gui.frame.ContentPane;
import org.columba.core.gui.frame.DefaultFrameController;
import org.columba.core.gui.frame.FrameModel;
import org.columba.core.gui.util.LabelWithMnemonic;
import org.columba.core.io.DiskIO;
import org.columba.core.xml.XmlElement;
import org.columba.mail.config.MailConfig;
import org.columba.mail.gui.composer.action.SaveAsDraftAction;
import org.columba.mail.gui.composer.html.HtmlEditorController;
import org.columba.mail.gui.composer.html.HtmlToolbar;
import org.columba.mail.gui.composer.text.TextEditorController;
import org.columba.mail.gui.composer.util.IdentityInfoPanel;
import org.columba.mail.parser.text.HtmlParser;
import org.columba.mail.util.MailResourceLoader;
import org.frapuccino.swing.MultipleTransferHandler;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * controller for message composer dialog
 * 
 * @author frd
 */
public class ComposerController extends DefaultFrameController implements
		CharsetOwnerInterface, Observer, ContentPane, DocumentListener {

	/** JDK 1.4+ logging framework logger, used for logging. */
	private static final Logger LOG = Logger
			.getLogger("org.columba.mail.gui.composer");

	private IdentityInfoPanel identityInfoPanel;

	private AttachmentController attachmentController;

	private SubjectController subjectController;

	private PriorityController priorityController;

	private AccountController accountController;

	private AbstractEditorController editorController;

	private HeaderController headerController;

	private ComposerSpellCheck composerSpellCheck;

	private ComposerModel composerModel;

	private Charset charset;

	private EventListenerList listenerList = new EventListenerList();

	/** Buffer for listeners used by addContainerListenerForEditor and createView */
	private List containerListenerBuffer;

	private JSplitPane attachmentSplitPane;

	/** Editor viewer resides in this panel */
	private TextEditorPanel editorPanel;

	private LabelWithMnemonic subjectLabel;

	private LabelWithMnemonic smtpLabel;

	private LabelWithMnemonic priorityLabel;

	private JPanel centerPanel = new FormDebugPanel();

	private JPanel topPanel;

	private HtmlToolbar htmlToolbar;

	private boolean promptOnDialogClosing = true;

	private SignatureView signatureView;

	public ComposerController() {
		this(new ComposerModel(), FrameModel.getInstance()
				.createCustomViewItem("Composer"));

	}

	public ComposerController(ComposerModel model) {
		this(model, FrameModel.getInstance().createCustomViewItem("Composer"));
	}

	public ComposerController(ViewItem viewItem) {
		this(new ComposerModel(), viewItem);

	}

	public ComposerController(ComposerModel model, ViewItem viewItem) {
		super(viewItem);

		// init model (defaults to empty plain text message)
		composerModel = model;

		// init controllers for different parts of the composer
		identityInfoPanel = new IdentityInfoPanel();
		attachmentController = new AttachmentController(this);
		headerController = new HeaderController(this);
		subjectController = new SubjectController(this);
		getSubjectController().getView().getDocument()
				.addDocumentListener(this);

		priorityController = new PriorityController(this);
		accountController = new AccountController(this);
		composerSpellCheck = new ComposerSpellCheck(this);

		signatureView = new SignatureView(this);

		// set default html or text based on stored option
		// ... can be overridden by setting the composer model
		XmlElement optionsElement = MailConfig.getInstance().get(
				"composer_options").getElement("/options");
		XmlElement htmlElement = optionsElement.getElement("html");

		// create default element if not available
		if (htmlElement == null) {
			htmlElement = optionsElement.addSubElement("html");
		}

		String enableHtml = htmlElement.getAttribute("enable", "false");

		// set model based on configuration
		if (enableHtml.equals("true")) {
			getModel().setHtml(true);
		} else {
			getModel().setHtml(false);
		}

		// Add the composer controller as observer
		htmlElement.addObserver(this);

		// init controller for the editor depending on message type
		if (getModel().isHtml()) {
			editorController = new HtmlEditorController(this);
		} else {
			editorController = new TextEditorController(this);
		}

		initComponents();

		// add JPanel with useful HTML related actions.
		htmlToolbar = new HtmlToolbar(this);

		layoutComponents();

		showAttachmentPanel();

		// Hack to ensure charset is set correctly at start-up
		XmlElement charsetElement = optionsElement.getElement("charset");

		if (charsetElement != null) {
			String charset = charsetElement.getAttribute("name");

			if (charset != null) {
				try {
					setCharset(Charset.forName(charset));
				} catch (UnsupportedCharsetException ex) {
					// ignore this
				}
			}
		}

		// Setup DnD for the text and attachment list control.
		ComposerAttachmentTransferHandler dndTransferHandler = new ComposerAttachmentTransferHandler(
				attachmentController);
		attachmentController.getView().setDragEnabled(true);
		attachmentController.getView().setTransferHandler(dndTransferHandler);

		JEditorPane editorComponent = (JEditorPane) getEditorController()
				.getComponent();
		MultipleTransferHandler compositeHandler = new MultipleTransferHandler();
		compositeHandler.addTransferHandler(editorComponent
				.getTransferHandler());
		compositeHandler.addTransferHandler(dndTransferHandler);
		editorComponent.setDragEnabled(true);
		editorComponent.setTransferHandler(compositeHandler);

		// getContainer().setContentPane(this);

		/*
		 * if (isAccountInfoPanelVisible()) {
		 * addToolBar(getIdentityInfoPanel()); }
		 */

		// *20030917, karlpeder* If ContainerListeners are waiting to be
		// added, add them now.
		if (containerListenerBuffer != null) {
			LOG.fine("Adding ContainerListeners from buffer");

			Iterator ite = containerListenerBuffer.iterator();

			while (ite.hasNext()) {
				ContainerListener cl = (ContainerListener) ite.next();
				getEditorPanel().addContainerListener(cl);
			}

			containerListenerBuffer = null; // done, the buffer has been emptied
		}

	}

	public IdentityInfoPanel getAccountInfoPanel() {

		return getIdentityInfoPanel();
	}

	/**
	 * Show attachment panel
	 * <p>
	 * Asks the ComposerModel if message contains attachments. If so, show the
	 * attachment panel. Otherwise, hide the attachment panel.
	 */
	public void showAttachmentPanel() {
		// remove all components from container
		centerPanel.removeAll();

		// re-add all top components like recipient editor/subject editor
		centerPanel.add(topPanel, BorderLayout.NORTH);

		// if message contains attachments
		if (getAttachmentController().getView().count() > 0) {
			// create scrollapen
			JScrollPane attachmentScrollPane = new JScrollPane(
					getAttachmentController().getView());
			attachmentScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			attachmentScrollPane.setBorder(BorderFactory.createEmptyBorder(1,
					1, 1, 1));
			// create splitpane containing the bodytext editor and the
			// attachment panel
			attachmentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					editorPanel, attachmentScrollPane);
			attachmentSplitPane.setDividerLocation(0.80);
			attachmentSplitPane.setBorder(null);

			// add splitpane to the center
			centerPanel.add(attachmentSplitPane, BorderLayout.CENTER);

			// set splitpane position based on configuration settings

			ViewItem viewItem = getViewItem();

			// default value is 200 pixel
			int pos = viewItem.getIntegerWithDefault("splitpanes",
					"attachment", 200);
			attachmentSplitPane.setDividerLocation(pos);
		} else {
			// no attachments
			// -> only show bodytext editor
			centerPanel.add(editorPanel, BorderLayout.CENTER);
		}

		// re-paint composer-view
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if (getContainer() != null)
					getContainer().getFrame().validate();				
			}
		}
		);

	}

	/**
	 * @return Returns the attachmentSplitPane.
	 */
	public JSplitPane getAttachmentSplitPane() {
		return attachmentSplitPane;
	}

	/**
	 * init components
	 */
	protected void initComponents() {
		subjectLabel = new LabelWithMnemonic(MailResourceLoader.getString(
				"dialog", "composer", "subject"));
		smtpLabel = new LabelWithMnemonic(MailResourceLoader.getString(
				"dialog", "composer", "identity"));
		priorityLabel = new LabelWithMnemonic(MailResourceLoader.getString(
				"dialog", "composer", "priority"));

		editorPanel = new TextEditorPanel();
	}

	/**
	 * Layout components
	 */
	public void layoutComponents() {
		centerPanel.removeAll();

		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

		// Create a FormLayout instance.
		FormLayout layout = new FormLayout(
				"center:max(50dlu;default), 3dlu, fill:default:grow, 2dlu",

				// 2 columns
				// "fill:default, 3dlu,fill:default, 3dlu, fill:default, 3dlu,
				// fill:default, 3dlu");
				"fill:default:grow");

		// 3 row
		PanelBuilder builder = new PanelBuilder(topPanel, layout);
		CellConstraints cc = new CellConstraints();

		layout.setColumnGroups(new int[][] { { 1 } });

		// layout.setRowGroups(new int[][] { { 1, 5, 7 } });

		builder.add(smtpLabel, cc.xy(1, 1));

		builder.appendRow("3dlu");
		builder.appendRow("fill:default:grow");

		JPanel smtpPanel = new JPanel();

		FormLayout l = new FormLayout(
				"fill:default:grow, 6dlu, right:default:grow, 3dlu, right:default:grow",
				"fill:default:grow");
		PanelBuilder b = new PanelBuilder(smtpPanel, l);

		CellConstraints c = new CellConstraints();
		b.add(getAccountController().getView(), c.xy(1, 1));
		b.add(priorityLabel, c.xy(3, 1));
		b.add(getPriorityController().getView(), c.xy(5, 1));

		builder.add(smtpPanel, cc.xy(3, 1));
		builder.appendRow("3dlu");
		builder.appendRow("fill:default:grow");
		builder.add(getHeaderController().getView(), cc.xywh(1, 3, 4, 1));
		builder.appendRow("3dlu");
		builder.appendRow("fill:default:grow");
		builder.add(subjectLabel, cc.xy(1, 5));
		builder.appendRow("3dlu");
		builder.appendRow("fill:default:grow");
		builder.add(getSubjectController().getView(), cc.xy(3, 5));

		/*
		 * builder.appendRow("6dlu"); builder.appendRow("fill:default:grow");
		 * builder.add(htmlToolbar, cc.xywh(3, 7, 2, 1));
		 */

		layout.setRowGroups(new int[][] { { 1, 5 } });

		htmlToolbar.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

		// editorPanel.add(htmlToolbar, BorderLayout.NORTH);

		editorPanel.getContentPane().add(
				getEditorController().getViewUIComponent());

		editorPanel.getContentPane().add(signatureView);

		centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		centerPanel.setLayout(new BorderLayout());

		centerPanel.add(topPanel, BorderLayout.NORTH);

		JScrollPane attachmentScrollPane = new JScrollPane(
				getAttachmentController().getView());
		attachmentScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		attachmentScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1,
				1));

		attachmentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				editorPanel, attachmentScrollPane);
		attachmentSplitPane.setDividerLocation(0.80);
		attachmentSplitPane.setBorder(null);

		centerPanel.add(attachmentSplitPane, BorderLayout.CENTER);

		ViewItem viewItem = getViewItem();
		int pos = viewItem.getIntegerWithDefault("splitpanes", "attachment",
				200);
		attachmentSplitPane.setDividerLocation(pos);

	}

	/**
	 * Returns a reference to the panel, that holds the editor view. This is
	 * used by the ComposerController when adding a listener to that panel.
	 */
	public JPanel getEditorPanel() {
		return editorPanel.getContentPane();
	}

	/**
	 * Used to update the panel, that holds the editor viewer. This is necessary
	 * e.g. if the ComposerModel is changed to hold another message type (text /
	 * html), which the previous editor can not handle. If so a new editor
	 * controller is created, and thereby a new view.
	 */
	public void setNewEditorView() {

		// update panel
		editorPanel.removeAll();
		editorPanel.add(getEditorController().getViewUIComponent());
		editorPanel.validate();
	}

	public boolean isAccountInfoPanelVisible() {
		// TODO (@author fdietz): fix account info panel check

		/*
		 * return isToolbarEnabled(ACCOUNTINFOPANEL);
		 */

		return true;
	}

	/**
	 * Check if data was entered correctly.
	 * <p>
	 * This includes currently a test for an empty subject and a valid recipient
	 * (to/cc/bcc) list.
	 * 
	 * @return true, if data was entered correctly
	 */
	public boolean checkState() {
		// update ComposerModel based on user-changes in ComposerView
		updateComponents(false);

		if (!subjectController.checkState()) {
			return false;
		}

		return !headerController.checkState();
	}

	public void updateComponents(boolean b) {
		subjectController.updateComponents(b);
		editorController.updateComponents(b);
		priorityController.updateComponents(b);
		accountController.updateComponents(b);
		attachmentController.updateComponents(b);
		headerController.updateComponents(b);

		// show attachment panel if necessary
		if (b)
			showAttachmentPanel();
	}

	/**
	 * @return AccountController
	 */
	public AccountController getAccountController() {
		return accountController;
	}

	/**
	 * @return AttachmentController
	 */
	public AttachmentController getAttachmentController() {
		return attachmentController;
	}

	/**
	 * @return ComposerSpellCheck
	 */
	public ComposerSpellCheck getComposerSpellCheck() {
		return composerSpellCheck;
	}

	/**
	 * @return TextEditorController
	 */
	public AbstractEditorController getEditorController() {
		/*
		 * *20030906, karlpeder* Method signature changed to return an
		 * AbstractEditorController
		 */
		return editorController;
	}

	/**
	 * @return HeaderViewer
	 */
	public HeaderController getHeaderController() {
		return headerController;
	}

	/**
	 * @return IdentityInfoPanel
	 */
	public IdentityInfoPanel getIdentityInfoPanel() {
		return identityInfoPanel;
	}

	/**
	 * @return PriorityController
	 */
	public PriorityController getPriorityController() {
		return priorityController;
	}

	/**
	 * @return SubjectController
	 */
	public SubjectController getSubjectController() {
		return subjectController;
	}

	/**
	 * @see org.columba.core.gui.FrameController#init()
	 */
	protected void init() {

	}

	/**
	 * Returns the composer model
	 * 
	 * @return Composer model
	 */
	public ComposerModel getModel() {
		// if (composerModel == null) // *20030907, karlpeder* initialized in
		// init
		// composerModel = new ComposerModel();
		return composerModel;
	}

	/**
	 * Sets the composer model. If the message type of the new model (html /
	 * text) is different from the message type of the existing, the editor
	 * controller is changed and the view is changed accordingly. <br>
	 * Finally the components are updated according to the new model.
	 * 
	 * @param model
	 *            New composer model
	 */
	public void setComposerModel(ComposerModel model) {
		boolean wasHtml = composerModel.isHtml();
		composerModel = model;

		if (wasHtml != composerModel.isHtml()) {
			// new editor controller needed
			switchEditor(composerModel.isHtml());

			XmlElement optionsElement = MailConfig.getInstance().get(
					"composer_options").getElement("/options");
			XmlElement htmlElement = optionsElement.getElement("html");

			// create default element if not available
			if (htmlElement == null) {
				htmlElement = optionsElement.addSubElement("html");
			}

			// change configuration based on new model
			htmlElement.addAttribute("enable", Boolean.toString(composerModel
					.isHtml()));

			// notify observers - this includes this object - but here it will
			// do nothing, since the model is already setup correctly
			htmlElement.notifyObservers();
		}

		// Update all component according to the new model
		updateComponents(true);
	}

	/**
	 * Private utility for switching btw. html and text. This includes
	 * instantiating a new editor controller and refreshing the editor view
	 * accordingly. <br>
	 * Pre-condition: The caller should set the composer model before calling
	 * this method. If a message was already entered in the UI, then
	 * updateComponents should have been called to synchronize model with view
	 * before switching, else data will be lost. <br>
	 * Post-condition: The caller must call updateComponents afterwards to
	 * display model data using the new controller-view pair
	 * 
	 * @param html
	 *            True if we should switch to html, false for text
	 */
	private void switchEditor(boolean html) {
		if (composerModel.isHtml()) {
			LOG.fine("Switching to html editor");
			editorController.deleteObservers(); // clean up
			editorController = new HtmlEditorController(this);
		} else {
			LOG.fine("Switching to text editor");
			editorController.deleteObservers(); // clean up
			editorController = new TextEditorController(this);
		}

		// an update of the view is also necessary.
		setNewEditorView();
	}

	/**
	 * Register ContainerListener for the panel, that holds the editor view. By
	 * registering as listener it is possible to get information when the editor
	 * changes. <br>
	 * If the view is not yet created, the listener is stored in a buffer - add
	 * then added in createView. This is necessary to handle the timing involved
	 * in setting up the controller-view framework for the composer
	 */
	public void addContainerListenerForEditor(ContainerListener cl) {

		// add listener
		getEditorPanel().addContainerListener(cl);

	}

	/**
	 * Removes a ContainerListener from the panel, that holds the editor view
	 * (previously registered using addContainListenerForEditor)
	 */
	public void removeContainerListenerForEditor(ContainerListener cl) {
		getEditorPanel().removeContainerListener(cl);
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;

		XmlElement optionsElement = MailConfig.getInstance().get(
				"composer_options").getElement("/options");
		XmlElement charsetElement = optionsElement.getElement("charset");

		if (charset == null) {
			optionsElement.removeElement(charsetElement);
		} else {
			if (charsetElement == null) {
				charsetElement = new XmlElement("charset");
				optionsElement.addElement(charsetElement);
			}

			charsetElement.addAttribute("name", charset.name());
		}

		((ComposerModel) getModel()).setCharset(charset);
		fireCharsetChanged(new CharsetEvent(this, charset));
	}

	public void addCharsetListener(CharsetListener l) {
		listenerList.add(CharsetListener.class, l);
	}

	public void removeCharsetListener(CharsetListener l) {
		listenerList.remove(CharsetListener.class, l);
	}

	protected void fireCharsetChanged(CharsetEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CharsetListener.class) {
				((CharsetListener) listeners[i + 1]).charsetChanged(e);
			}
		}
	}

	/**
	 * Used for listenen to the enable html option
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		XmlElement e = (XmlElement) o;

		if (e.getName().equals("html")) {
			// switch btw. html and text if necessary
			String enableHtml = e.getAttribute("enable", "false");
			boolean html = Boolean.valueOf(enableHtml).booleanValue();
			boolean wasHtml = composerModel.isHtml();

			if (html != wasHtml) {
				composerModel.setHtml(html);

				// sync model with the current (old) view
				updateComponents(false);

				// convert body text to comply with new editor format
				String oldBody = composerModel.getBodyText();
				String newBody;

				if (html) {
					LOG.fine("Converting body text to html");
					newBody = HtmlParser.textToHtml(oldBody, "", null);
				} else {
					LOG.fine("Converting body text to text");
					newBody = HtmlParser.htmlToText(oldBody);
				}

				composerModel.setBodyText(newBody);

				// switch editor and resync view with model
				switchEditor(composerModel.isHtml());

				updateComponents(true);
			}

			if (html) {
				editorPanel.add(htmlToolbar, BorderLayout.NORTH);
			} else {
				editorPanel.remove(htmlToolbar);
			}

			editorPanel.validate();
		}
	}

	public void savePositions(ViewItem viewItem) {
		super.savePositions(viewItem);

		viewItem = getViewItem();

		// splitpanes
		if (attachmentSplitPane != null)
			viewItem.setInteger("splitpanes", "attachment", attachmentSplitPane
					.getDividerLocation());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.columba.core.gui.frame.AbstractFrameView#showToolbar()
	 */
	public void showToolbar() {

		/*
		 * boolean b = isToolbarVisible();
		 * 
		 * if (getToolBar() == null) { return; }
		 * 
		 * if (b) { toolbarPane.remove(toolbar); ((FrameMediator)
		 * frameController) .enableToolbar(MAIN_TOOLBAR, false); } else { if
		 * (isAccountInfoPanelVisible()) { toolbarPane.removeAll();
		 * toolbarPane.add(toolbar); toolbarPane.add(getAccountInfoPanel()); }
		 * else { toolbarPane.add(toolbar); }
		 * 
		 * ((FrameMediator) frameController).enableToolbar(MAIN_TOOLBAR, true); }
		 * 
		 * validate(); repaint();
		 */
	}

	public void showAccountInfoPanel() {

		/*
		 * boolean b = isAccountInfoPanelVisible();
		 * 
		 * if (b) { toolbarPane.remove(getAccountInfoPanel()); ((FrameMediator)
		 * frameController).enableToolbar(ACCOUNTINFOPANEL, false); } else {
		 * toolbarPane.add(getAccountInfoPanel());
		 * 
		 * ((FrameMediator) frameController).enableToolbar(ACCOUNTINFOPANEL,
		 * true); }
		 * 
		 * validate(); repaint();
		 */
	}

	/**
	 * @see org.columba.core.gui.frame.FrameMediator#close()
	 */
	public void close() {

		// don't prompt user if composer should be closed
		if (isPromptOnDialogClosing() == false)
			return;

		// only prompt user, if composer contains some text
		if (editorController.getViewText().length() == 0) {
			getContainer().getFrame().setVisible(false);

			// close Columba, if composer is only visible frame
			FrameModel.getInstance().close(null);

			return;
		}

		Object[] options = { "Close", "Cancel", "Save" };
		int n = JOptionPane.showOptionDialog(getContainer().getFrame(),
				"Message wasn't sent. Would you like to save your changes?",
				"Warning: Message was modified",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]); // default button title

		if (n == 2) {
			// save changes
			new SaveAsDraftAction(ComposerController.this)
					.actionPerformed(null);

			// close composer
			getContainer().getFrame().setVisible(false);

			// close Columba, if composer is only visible frame
			FrameModel.getInstance().close(null);
		} else if (n == 1) {
			// cancel question dialog and don't close composer
		} else {
			// close composer
			getContainer().getFrame().setVisible(false);

			// close Columba, if composer is only visible frame
			FrameModel.getInstance().close(null);
		}

	}

	public class ComposerFocusTraversalPolicy extends FocusTraversalPolicy {

		public Component getComponentAfter(Container focusCycleRoot,
				Component aComponent) {
			if (aComponent.equals(accountController.getView()))
				return priorityController.getView();
			else if (aComponent.equals(priorityController.getView()))
				return headerController.getView().getToComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getToComboBox().getTextEditor()))
				return headerController.getView().getCcComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getCcComboBox().getTextEditor()))
				return headerController.getView().getBccComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getBccComboBox().getTextEditor()))
				return subjectController.getView();
			else if (aComponent.equals(subjectController.getView()))
				return editorController.getComponent();

			return headerController.getView().getToComboBox().getTextEditor();
		}

		public Component getComponentBefore(Container focusCycleRoot,
				Component aComponent) {
			if (aComponent.equals(editorController.getComponent()))
				return subjectController.getView();
			else if (aComponent.equals(subjectController.getView()))
				return headerController.getView().getBccComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getBccComboBox().getTextEditor()))
				return headerController.getView().getCcComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getCcComboBox().getTextEditor()))
				return headerController.getView().getToComboBox()
						.getTextEditor();
			else if (aComponent.equals(headerController.getView()
					.getToComboBox().getTextEditor()))
				return priorityController.getView();
			else if (aComponent.equals(priorityController.getView()))
				return accountController.getView();

			return editorController.getComponent();
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			return headerController.getView().getToComboBox().getTextEditor();
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return editorController.getComponent();
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return accountController.getView();
		}
	}

	/**
	 * @see org.columba.core.gui.frame.ContentPane#getComponent()
	 */
	public JComponent getComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(centerPanel, BorderLayout.CENTER);

		try {
			InputStream is = DiskIO
					.getResourceStream("org/columba/mail/action/composer_menu.xml");

			getContainer().extendMenuFromURL(this, is);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}

		getContainer().extendToolbar(
				this,
				MailConfig.getInstance().get("composer_toolbar").getElement(
						"toolbar"));

		// @author: fdietz
		// disabled identity infopanel because it contains
		// only duplicate information
		// getContainer().setInfoPanel(getIdentityInfoPanel());

		getContainer().getFrame().setFocusTraversalPolicy(
				new ComposerFocusTraversalPolicy());

		// make sure that JFrame is not closed automatically
		// -> we want to prompt the user to save his work
		getContainer().setCloseOperation(false);

		return panel;
	}

	/**
	 * @see org.columba.core.gui.frame.FrameMediator#getString(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getString(String sPath, String sName, String sID) {
		return MailResourceLoader.getString(sPath, sName, sID);
	}

	/**
	 * @see org.columba.core.gui.frame.FrameMediator#getContentPane()
	 */
	public ContentPane getContentPane() {
		return this;
	}

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent arg0) {
		Document doc = arg0.getDocument();
		try {
			String subject = doc.getText(0, doc.getLength());

			getContainer().getFrame().setTitle(subject);
		} catch (BadLocationException e) {
		}
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent arg0) {
		Document doc = arg0.getDocument();
		try {
			String subject = doc.getText(0, doc.getLength());

			getContainer().getFrame().setTitle(subject);
		} catch (BadLocationException e) {
		}
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent arg0) {
		Document doc = arg0.getDocument();
		try {
			String subject = doc.getText(0, doc.getLength());

			getContainer().getFrame().setTitle(subject);
		} catch (BadLocationException e) {
		}
	}

	/**
	 * @return Returns the promptOnDialogClosing.
	 */
	public boolean isPromptOnDialogClosing() {
		return promptOnDialogClosing;
	}

	/**
	 * @param promptOnDialogClosing
	 *            The promptOnDialogClosing to set.
	 */
	public void setPromptOnDialogClosing(boolean promptOnDialogClosing) {
		this.promptOnDialogClosing = promptOnDialogClosing;
	}
}