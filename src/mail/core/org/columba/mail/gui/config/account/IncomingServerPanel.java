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
package org.columba.mail.gui.config.account;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.columba.core.config.DefaultItem;
import org.columba.core.gui.util.ButtonWithMnemonic;
import org.columba.core.gui.util.CheckBoxWithMnemonic;
import org.columba.core.gui.util.DefaultFormBuilder;
import org.columba.core.gui.util.LabelWithMnemonic;
import org.columba.mail.config.AccountItem;
import org.columba.mail.main.MailInterface;
import org.columba.mail.util.MailResourceLoader;
import org.columba.ristretto.pop3.protocol.POP3Exception;
import org.columba.ristretto.pop3.protocol.POP3Protocol;

import com.jgoodies.forms.layout.FormLayout;

/**
 * @author freddy
 * @version
 */
public class IncomingServerPanel
    extends DefaultPanel
    implements ActionListener {
    private static final Pattern authModeTokenizePattern =
        Pattern.compile("([^;]+);?");
    private JLabel loginLabel;
    private JTextField loginTextField;
    private JLabel passwordLabel;
    private JTextField passwordTextField;
    private JLabel hostLabel;
    private JTextField hostTextField;
    private JLabel portLabel;
    private JTextField portTextField;
    private JLabel typeLabel;
    private JComboBox typeComboBox;
    private JCheckBox storePasswordCheckBox;
    private JCheckBox secureCheckBox;
    private JLabel authenticationLabel;
    private JComboBox authenticationComboBox;
    private JLabel typeDescriptionLabel;
    private PopAttributPanel popPanel;
    private ImapAttributPanel imapPanel;
    private DefaultItem serverItem = null;
    private AccountItem accountItem;
    private JCheckBox defaultAccountCheckBox;
    private ReceiveOptionsPanel receiveOptionsPanel;
    private JButton checkAuthMethods;

    //private ConfigFrame frame;
    private JDialog dialog;

    public IncomingServerPanel(
        JDialog dialog,
        AccountItem account,
        ReceiveOptionsPanel receiveOptionsPanel) {
        super();

        this.dialog = dialog;

        //super( frame, item );
        //this.frame = frame;
        this.accountItem = account;
        this.receiveOptionsPanel = receiveOptionsPanel;

        if (account.isPopAccount()) {
            serverItem = account.getPopItem();
        } else {
            serverItem = account.getImapItem();
        }

        initComponents();

        updateComponents(true);
    }

    public String getHost() {
        return hostTextField.getText();
    }

    public String getLogin() {
        return loginTextField.getText();
    }

    public boolean isPopAccount() {
        return accountItem.getElement("popserver") != null;
    }

    public boolean isSmtpAccount() {
        return accountItem.getElement("smtpserver") != null;
    }

    protected void updateComponents(boolean b) {
        if (b) {
            loginTextField.setText(serverItem.get("user"));
            passwordTextField.setText(serverItem.get("password"));
            hostTextField.setText(serverItem.get("host"));
            portTextField.setText(serverItem.get("port"));

            storePasswordCheckBox.setSelected(
                serverItem.getBoolean("save_password"));

            defaultAccountCheckBox.setSelected(
                serverItem.getBoolean("use_default_account"));

            authenticationComboBox.setSelectedItem(
                serverItem.get("login_method"));

            secureCheckBox.setSelected(
                serverItem.getBoolean("enable_ssl", false));

            defaultAccountCheckBox.setEnabled(
                MailInterface.config.getAccountList().getDefaultAccountUid()
                    != accountItem.getInteger("uid"));

            if (defaultAccountCheckBox.isEnabled()
                && defaultAccountCheckBox.isSelected()) {
                showDefaultAccountWarning();
            } else {
                layoutComponents();
            }
        } else {
            serverItem.set("user", loginTextField.getText());
            serverItem.set("host", hostTextField.getText());
            serverItem.set("password", passwordTextField.getText());
            serverItem.set("port", portTextField.getText());

            serverItem.set("save_password", storePasswordCheckBox.isSelected());

            serverItem.set("enable_ssl", secureCheckBox.isSelected());

            if (isPopAccount()) {
                // if securest write DEFAULT
                if (authenticationComboBox.getSelectedIndex() != 0) {
                    serverItem.set(
                        "login_method",
                        (String) authenticationComboBox.getSelectedItem());
                } else {
                    serverItem.set("login_method", "DEFAULT");
                }
            }

            serverItem.set(
                "use_default_account",
                defaultAccountCheckBox.isSelected());
        }
    }

    protected void showDefaultAccountWarning() {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraints = new GridBagConstraints();

        setLayout(mainLayout);

        mainConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainConstraints.anchor = GridBagConstraints.NORTHWEST;
        mainConstraints.weightx = 1.0;
        mainConstraints.insets = new Insets(0, 10, 5, 0);
        mainLayout.setConstraints(defaultAccountCheckBox, mainConstraints);
        add(defaultAccountCheckBox);

        mainConstraints = new GridBagConstraints();
        mainConstraints.weighty = 1.0;
        mainConstraints.gridwidth = GridBagConstraints.REMAINDER;

        /*
         * mainConstraints.fill = GridBagConstraints.BOTH;
         * mainConstraints.insets = new Insets(0, 0, 0, 0);
         * mainConstraints.gridwidth = GridBagConstraints.REMAINDER;
         * mainConstraints.weightx = 1.0; mainConstraints.weighty = 1.0;
         */
        JLabel label =
            new JLabel(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "using_default_account_settings"));
        Font newFont = label.getFont().deriveFont(Font.BOLD);
        label.setFont(newFont);
        mainLayout.setConstraints(label, mainConstraints);
        add(label);
    }

    protected void layoutComponents() {
        //		Create a FormLayout instance.
        FormLayout layout =
            new FormLayout(
                "10dlu, max(100;default), 3dlu, fill:max(150dlu;default):grow",

            // 2 columns
    ""); // rows are added dynamically (no need to define them here)

        JPanel topPanel = new JPanel();

        // create a form builder
        DefaultFormBuilder builder = new DefaultFormBuilder(this, layout);

        // create EmptyBorder between components and dialog-frame
        builder.setDefaultDialogBorder();

        //		skip the first column
        builder.setLeadingColumnOffset(1);

        // Add components to the panel:
        builder.append(defaultAccountCheckBox, 4);
        builder.nextLine();

        builder.appendSeparator(
            MailResourceLoader.getString("dialog", "account", "configuration"));
        builder.nextLine();

        builder.append(loginLabel, 1);
        builder.append(loginTextField);
        builder.nextLine();

        builder.append(hostLabel, 1);
        builder.append(hostTextField);
        builder.nextLine();

        builder.append(portLabel, 1);
        builder.append(portTextField);
        builder.nextLine();

        builder.appendSeparator(
            MailResourceLoader.getString("dialog", "account", "security"));
        builder.nextLine();

        JPanel panel = new JPanel();
        FormLayout l =
            new FormLayout(
                "max(100;default), 3dlu, left:max(50dlu;default), 2dlu, left:max(50dlu;default)",

            // 2 columns
    	""); // rows are added dynamically (no need to define them here)

        // create a form builder
        DefaultFormBuilder b = new DefaultFormBuilder(panel, l);
        b.append(authenticationLabel, authenticationComboBox, checkAuthMethods);
        builder.append(panel, 3);
        builder.nextLine();

        builder.append(storePasswordCheckBox, 3);
        builder.nextLine();

        builder.append(secureCheckBox, 3);
        builder.nextLine();

    }

    protected void initComponents() {
        defaultAccountCheckBox =
            new CheckBoxWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "use_default_account_settings"));

        defaultAccountCheckBox.setActionCommand("DEFAULT_ACCOUNT");
        defaultAccountCheckBox.addActionListener(this);

        //defaultAccountCheckBox.setEnabled(false);
        typeLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "server_type"));

        typeComboBox = new JComboBox();
        typeComboBox.addItem("POP3");
        typeComboBox.addItem("IMAP4");

        if (accountItem.isPopAccount()) {
            typeComboBox.setSelectedIndex(0);
        } else {
            typeComboBox.setSelectedIndex(1);
        }

        typeLabel.setLabelFor(typeComboBox);
        typeComboBox.setEnabled(false);

        typeDescriptionLabel =
            new JLabel("Description: To connect to and fetch new messages from a POP3-server.");
        typeDescriptionLabel.setEnabled(false);

        loginLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString("dialog", "account", "login"));

        loginTextField = new JTextField();
        loginLabel.setLabelFor(loginTextField);
        passwordLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString("dialog", "account", "password"));

        passwordTextField = new JTextField();

        hostLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString("dialog", "account", "host"));

        hostTextField = new JTextField();
        hostLabel.setLabelFor(hostTextField);

        portLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString("dialog", "account", "port"));

        portTextField = new JTextField();
        portLabel.setLabelFor(portTextField);

        storePasswordCheckBox =
            new CheckBoxWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "store_password_in_configuration_file"));

        secureCheckBox =
            new CheckBoxWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "use_SSL_for_secure_connection"));

        authenticationLabel =
            new LabelWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "authentication_type"));
                    
		authenticationComboBox = new JComboBox();
		authenticationLabel.setLabelFor(authenticationComboBox);

        updateAuthenticationComboBox();

        checkAuthMethods =
            new ButtonWithMnemonic(
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "authentication_checkout_methods"));
        checkAuthMethods.setActionCommand("CHECK_AUTHMETHODS");
        checkAuthMethods.addActionListener(this);
    }

    private void updateAuthenticationComboBox() {
 		authenticationComboBox.removeAllItems();
 		
        authenticationComboBox.addItem(
            MailResourceLoader.getString(
                "dialog",
                "account",
                "authentication_securest"));
        if (isPopAccount()) {
            String authMethods =
                accountItem.get("popserver", "authentication_methods");

            // Add previously fetch authentication modes
            if (authMethods != null) {
                Matcher matcher = authModeTokenizePattern.matcher(authMethods);

                while (matcher.find()) {
                    authenticationComboBox.addItem(matcher.group(1));
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals("SERVER")) //$NON-NLS-1$
            {
            System.out.println("selection changed");
        } else if (action.equals("DEFAULT_ACCOUNT")) {
            removeAll();
            receiveOptionsPanel.removeAll();

            if (defaultAccountCheckBox.isSelected()) {
                showDefaultAccountWarning();
                receiveOptionsPanel.showDefaultAccountWarning();
            } else {
                layoutComponents();
                receiveOptionsPanel.layoutComponents();
            }

            revalidate();
            receiveOptionsPanel.revalidate();
        } else if (action.equals("CHECK_AUTHMETHODS")) {
            getAuthMechanisms();
        }

    }

    private void getAuthMechanisms() {
        {
            List list = new LinkedList();

            if (isPopAccount()) {
                try {
                    list = getAuthPOP3();
                } catch (IOException e1) {
                    String name = e1.getClass().getName();
                    JOptionPane.showMessageDialog(
                        null,
                        e1.getLocalizedMessage(),
                        name.substring(name.lastIndexOf(".")),
                        JOptionPane.ERROR_MESSAGE);
                } catch (POP3Exception e1) {
                    //TODO Server does not support CAPA
                }

            } 

            // Save the authentication modes
            if (list.size() > 0) {
                StringBuffer authMethods = new StringBuffer();
                Iterator it = list.iterator();
                authMethods.append(it.next());

                while (it.hasNext()) {
                    authMethods.append(';');
                    authMethods.append(it.next());
                }

                accountItem.set(
                    "popserver",
                    "authentication_methods",
                    authMethods.toString());
            }

            updateAuthenticationComboBox();
        }
    }

    private LinkedList getAuthPOP3() throws IOException, POP3Exception {
        LinkedList list;
        POP3Protocol protocol =
            new POP3Protocol(
                accountItem.get("popserver", "host"),
                accountItem.getInteger("popserver", "port"));
        protocol.openPort();

        String[] capas = protocol.capa();

        protocol.quit();
        list = new LinkedList();

        // Search for authenticatio modes in the Capabilities
        for (int i = 0; i < capas.length; i++) {
            if (capas[i].equals("APOP")) {
                list.add(capas[i]);
            } else if (capas[i].equals("USER")) {
                list.add(capas[i]);
            } else if (capas[i].startsWith("AUTH")) {
                // TODO Check if Columba supports this auth
                // algorithm
            }
        }
        return list;
    }

    public boolean isFinished() {
        String host = getHost();
        String login = getLogin();

        if (host.length() == 0) {
            JOptionPane.showMessageDialog(
                null,
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "You_have_to_enter_a_host_name"));

            //$NON-NLS-1$
            return false;
        } else if (login.length() == 0) {
            JOptionPane.showMessageDialog(
                null,
                MailResourceLoader.getString(
                    "dialog",
                    "account",
                    "You_have_to_enter_a_login_name"));

            //$NON-NLS-1$
            return false;
        }

        return true;
    }
}
