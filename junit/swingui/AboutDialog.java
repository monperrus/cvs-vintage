package junit.swingui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import junit.runner.*;

class AboutDialog extends JDialog {
	public AboutDialog(JFrame parent) {
		super(parent);
		 
		setResizable(false);
		getContentPane().setLayout(new GridBagLayout());
		setSize(296, 138);
		setTitle("About");
		
		JButton button= new JButton("Close");
		button.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
		
		JLabel label1= new JLabel("JUnit");
		label1.setFont(new Font("dialog", Font.PLAIN, 36));
		
		JLabel label2= new JLabel("JUnit "+Version.id()+" by Kent Beck and Erich Gamma");
		label2.setFont(new Font("dialog", Font.PLAIN, 14));
		
		JLabel logo= createLogo();

		GridBagConstraints constraintsLabel1= new GridBagConstraints();
		constraintsLabel1.gridx = 3; constraintsLabel1.gridy = 0;
		constraintsLabel1.gridwidth = 1; constraintsLabel1.gridheight = 1;
		constraintsLabel1.anchor = GridBagConstraints.CENTER;
		constraintsLabel1.weightx = 0.0;
		constraintsLabel1.weighty = 0.0;
		getContentPane().add(label1, constraintsLabel1);

		GridBagConstraints constraintsLabel2= new GridBagConstraints();
		constraintsLabel2.gridx = 2; constraintsLabel2.gridy = 1;
		constraintsLabel2.gridwidth = 2; constraintsLabel2.gridheight = 1;
		constraintsLabel2.anchor = GridBagConstraints.CENTER;
		constraintsLabel2.weightx = 0.0;
		constraintsLabel2.weighty = 0.0;
		getContentPane().add(label2, constraintsLabel2);

		GridBagConstraints constraintsButton1= new GridBagConstraints();
		constraintsButton1.gridx = 2; constraintsButton1.gridy = 2;
		constraintsButton1.gridwidth = 2; constraintsButton1.gridheight = 1;
		constraintsButton1.anchor = GridBagConstraints.CENTER;
		constraintsButton1.weightx = 0.0;
		constraintsButton1.weighty = 0.0;
		constraintsButton1.insets= new Insets(8, 0, 8, 0);
		getContentPane().add(button, constraintsButton1);

		GridBagConstraints constraintsLogo1= new GridBagConstraints();
		constraintsLogo1.gridx = 2; constraintsLogo1.gridy = 0;
		constraintsLogo1.gridwidth = 1; constraintsLogo1.gridheight = 1;
		constraintsLogo1.anchor = GridBagConstraints.CENTER;
		constraintsLogo1.weightx = 0.0;
		constraintsLogo1.weighty = 0.0;
		getContentPane().add(logo, constraintsLogo1);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	}
	protected JLabel createLogo() {
		java.net.URL url= BaseTestRunner.class.getResource("logo.gif");
		return new JLabel(new ImageIcon(url));
	}
}