/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.config.pop3preprocessor;

import org.columba.core.gui.util.ButtonWithMnemonic;
import org.columba.core.gui.util.NotifyDialog;
import org.columba.core.main.MainInterface;
import org.columba.core.plugin.PluginHandlerNotFoundException;

import org.columba.mail.gui.util.URLController;
import org.columba.mail.plugin.POP3PreProcessingFilterPluginHandler;
import org.columba.mail.util.MailResourceLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * @author Sonja
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ChooseFilterDialog extends JDialog implements ListSelectionListener,
    ActionListener {
    protected JList list;
    protected ButtonWithMnemonic okButton;
    protected boolean success;
    protected POP3PreProcessingFilterPluginHandler pluginHandler;

    public ChooseFilterDialog(JDialog dialog) {
        super(dialog, true);

        setTitle("Choose Preprocessing Filter");

        pluginHandler = null;

        try {
            pluginHandler = (POP3PreProcessingFilterPluginHandler) MainInterface.pluginManager.getHandler(
                    "org.columba.mail.pop3preprocessingfilter");
        } catch (PluginHandlerNotFoundException ex) {
            NotifyDialog d = new NotifyDialog();
            d.showDialog(ex);
        }

        initComponents();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    protected void initComponents() {
        String[] names = pluginHandler.getPluginIdList();

        list = new JList(names);
        list.addListSelectionListener(this);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.getViewport().setBackground(Color.white);
        scrollPane.setPreferredSize(new Dimension(300, 250));

        listPanel.add(scrollPane, BorderLayout.CENTER);

        Container mainPanel = getContentPane();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(listPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        okButton = new ButtonWithMnemonic(MailResourceLoader.getString(
                    "global", "ok"));
        okButton.setActionCommand("OK"); //$NON-NLS-1$
        okButton.addActionListener(this);
        okButton.setEnabled(false);
        buttonPanel.add(okButton);

        ButtonWithMnemonic cancelButton = new ButtonWithMnemonic(MailResourceLoader.getString(
                    "global", "cancel"));
        cancelButton.setActionCommand("CANCEL"); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        ButtonWithMnemonic helpButton = new ButtonWithMnemonic(MailResourceLoader.getString(
                    "global", "help"));
        helpButton.setActionCommand("HELP");
        helpButton.addActionListener(this);
        buttonPanel.add(helpButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        getRootPane().registerKeyboardAction(this, "CANCEL",
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(this, "HELP",
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void valueChanged(ListSelectionEvent event) {
        int index = list.getSelectedIndex();

        if (index == -1) {
            // no item selected
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    public String getSelection() {
        int index = list.getSelectedIndex();

        return (String) list.getSelectedValue();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        String action = arg0.getActionCommand();

        if (action.equals("OK")) {
            setVisible(false);
            success = true;
        } else if (action.equals("CANCEL")) {
            setVisible(false);
            success = false;
        } else if (action.equals("HELP")) {
            URLController c = new URLController();

            try {
                c.open(new URL(
                        "http://columba.sourceforge.net/phpwiki/index.php/User%20manual#x34.x2e.5"));
            } catch (MalformedURLException mue) {
            }
        }
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return success;
    }
}
