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

package org.columba.mail.gui.config.accountwizard;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.columba.core.gui.util.*;
import org.columba.core.gui.util.wizard.*;
import org.columba.main.*;
import org.columba.mail.config.*;
import org.columba.mail.gui.config.account.*;
import org.columba.mail.gui.tree.*;
import org.columba.mail.gui.tree.util.*;
import org.columba.mail.gui.util.*;
import org.columba.mail.folder.*;
import org.columba.mail.util.MailResourceLoader;

public class AccountWizard implements ActionListener
{
	private WelcomePanel welcomePanel;
	private IdentityPanel identityPanel;
	private OutgoingServerPanel outgoingServerPanel;
	private IncomingServerPanel incomingServerPanel;
	private AdvancedPanel advancedPanel;
	private FinishPanel finishPanel;

	private boolean add;

	private JDialog dialog;

	public AccountWizard()
	{
		int count = MailConfig.getAccountList().count();

		add = false;

		dialog = DialogStore.getDialog(MailResourceLoader.getString("dialog","accountwizard","create_a_new_account")); //$NON-NLS-1$

		if (count == 0)
			init();
	}

	public AccountWizard( boolean b )
	{
		add = b;
		dialog = DialogStore.getDialog( MailResourceLoader.getString("dialog","accountwizard","create_a_new_account") ); //$NON-NLS-1$
		if ( b )
			addInit();
	}

	protected void addInit()
	{
		welcomePanel =
			new WelcomePanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","welcome"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","initial_welcome_screen"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));

		identityPanel =
			new IdentityPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","specify_your_identity_information"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"),
				true);

		incomingServerPanel =
			new IncomingServerPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","incoming_server_properties"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));
		identityPanel.setNext(incomingServerPanel);
		incomingServerPanel.setPrev(identityPanel);

		outgoingServerPanel =
			new OutgoingServerPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","outgoing_server_properties"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"),
				true);
		outgoingServerPanel.setPrev(incomingServerPanel);
		incomingServerPanel.setNext(outgoingServerPanel);

		advancedPanel =
			new AdvancedPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","advanced_options"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"),
				true);
		advancedPanel.setPrev(outgoingServerPanel);
		outgoingServerPanel.setNext(advancedPanel);

		//identityDialog.showDialog();
		//dialog.getContentPane().add( identityPanel );
		setPanel(identityPanel);
		
		dialog.pack();
		
		dialog.setSize(600,480);
	
		/*
		java.awt.Dimension dim = dialog.getSize();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		dialog.setLocation(
			screenSize.width / 2 - dim.width / 2,
			screenSize.height / 2 - dim.height / 2);
		*/
		
		dialog.setLocationRelativeTo(null);
		
		dialog.setVisible(true);
		
	}

	protected void init()
	{
		welcomePanel =
			new WelcomePanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","initial_welcome_screen"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));

		identityPanel =
			new IdentityPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","specify_your_identity_information"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));
		identityPanel.setPrev(welcomePanel);
		welcomePanel.setNext(identityPanel);

		incomingServerPanel =
			new IncomingServerPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","incoming_server_properties"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));
		identityPanel.setNext(incomingServerPanel);
		incomingServerPanel.setPrev(identityPanel);

		outgoingServerPanel =
			new OutgoingServerPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","outgoing_server_properties"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));
		outgoingServerPanel.setPrev(incomingServerPanel);
		incomingServerPanel.setNext(outgoingServerPanel);

		advancedPanel =
			new AdvancedPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","advanced_options"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png")
				);
		advancedPanel.setPrev(outgoingServerPanel);
		outgoingServerPanel.setNext(advancedPanel);

		finishPanel =
			new FinishPanel(
				dialog,
				this,
				MailResourceLoader.getString("dialog","accountwizard","account_wizard"), //$NON-NLS-1$
				MailResourceLoader.getString("dialog","accountwizard","finished_information_gathering"), //$NON-NLS-1$
				ImageLoader.getSmallImageIcon("stock_preferences.png"));
		advancedPanel.setNext(finishPanel);
		finishPanel.setPrev(advancedPanel);

		///welcomePanel.showDialog();
		setPanel(welcomePanel);
		java.awt.Dimension dim = dialog.getSize();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		dialog.setLocation(
			screenSize.width / 2 - dim.width / 2,
			screenSize.height / 2 - dim.height / 2);
		dialog.setVisible(true);
	}

	public void setPanel(DefaultWizardPanel panel)
	{
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();
	}

	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();

		if (action.equals("CANCEL"))
		{
			//identityPanel.showDialog();
			//incomingServerPanel.showDialog();
			//outgoingServerPanel.showDialog();
		}
		else if (action.equals("ACCOUNT"))
		{
			dialog.setVisible(false);

			AccountItem item = finish();

			AccountDialog dialog = new AccountDialog(item);
		}
		else if (action.equals("FINISH"))
		{
			if (add == true)
				//outgoingServerPanel.hideDialog();
				dialog.setVisible(false);
			else
				//finishPanel.hideDialog();
				dialog.setVisible(false);

			//System.out.println("saving data");

			finish();

		}

	}

	public AccountItem finish()
	{
		AccountItem item;

		if (incomingServerPanel.isPopAccount())
			item = MailConfig.getAccountList().addEmptyAccount(true);
		else
			item = MailConfig.getAccountList().addEmptyAccount(false);

		//System.out.println("name: "+ identityDialog.getAccountName() );

		IdentityItem identity = item.getIdentityItem();

		identity.setName(identityPanel.getName());
		identity.setAddress(identityPanel.getAddress());

		String accountname = advancedPanel.getAccountName();
		item.setName(accountname);

		if (incomingServerPanel.isPopAccount())
		{
			PopItem pop = item.getPopItem();

			pop.setHost(incomingServerPanel.getHost());
			pop.setUser(incomingServerPanel.getLogin());

			MainInterface.popServerCollection.add(item);

			MainInterface.frameController.getMenu().updatePopServerMenu();

		}
		else
		{
			ImapItem imap = item.getImapItem();

			imap.setHost(incomingServerPanel.getHost());
			imap.setUser(incomingServerPanel.getLogin());

			
			Folder parentFolder =
				(Folder) MainInterface.treeModel.addImapRootFolder(
					item.getName(),
					imap,
					item.getUid());

		}

		SmtpItem smtp = item.getSmtpItem();

		smtp.setHost(outgoingServerPanel.getHost());

		return item;

	}

}