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
package org.columba.mail.gui.composer.util;

import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import org.columba.core.config.Config;
import org.columba.core.util.TempFileStore;
import org.columba.mail.gui.composer.EditorView;
import org.columba.mail.gui.mimetype.MimeTypeViewer;
import org.columba.mail.message.MimeHeader;
import org.columba.mail.util.MailResourceLoader;

public class ExternalEditor {

	String Cmd;

	public ExternalEditor() {
	} // END public ExternalEditor()

	public ExternalEditor(String EditorCommand) {
	} // END public ExternalEditor(String EditorCommand)

	public boolean startExternalEditor(EditorView EditView) {
		MimeHeader myHeader = new MimeHeader("text", "plain");
		MimeTypeViewer viewer = new MimeTypeViewer();
		File tmpFile = TempFileStore.createTempFileWithSuffix("extern_edit");
		FileWriter FO;
		FileReader FI;

		try {
			FO = new FileWriter(tmpFile);
		} catch (java.io.IOException ex) {
			JOptionPane.showMessageDialog(
				null,
				"Error: Cannot write to temp file needed "
					+ "for external editor.");
			return false;
		}
		try {
			String M = EditView.getText();
			if (M != null) {
				FO.write(M);
			}
			FO.close();
		} catch (java.io.IOException ex) {
			JOptionPane.showMessageDialog(
				null,
				"Error: Cannot write to temp file needed "
					+ "for external editor.");
			return false;
		}

		Font OldFont = EditView.getFont();

		System.out.println("Setting Font to REALLY BIG!!! :-)");

		/*
		// Why doesn't this work???
		EditView.setFont(
			new Font(Config.getOptionsConfig().getThemeItem().getTextFontName(), Font.BOLD, 30));
		*/
		Font font = Config.getOptionsConfig().getGuiItem().getTextFont();
		font = font.deriveFont(30);
		
		EditView.setFont(font);
		
		EditView.setText(
			MailResourceLoader.getString(
				"menu",
				"composer",
				"extern_editor_using_msg"));

		Process child = viewer.open(myHeader, tmpFile);
                if (child == null) return false;

		try {
			// Wait for external editor to quit
			child.waitFor();
		} catch (InterruptedException ex) {
			JOptionPane.showMessageDialog(
				null,
				"Error: External editor exited " + "abnormally.");
			return false;
		}

		EditView.setFont(OldFont);

		try {
			FI = new FileReader(tmpFile);
		} catch (java.io.FileNotFoundException ex) {
			JOptionPane.showMessageDialog(
				null,
				"Error: Cannot read from temp file used "
					+ "by external editor.");
			return false;
		}
		//      int i = FI.available();
		char[] buf = new char[1000];
		int i;
		String message = new String("");
		try {
			while ((i = FI.read(buf)) >= 0) {
				//System.out.println( "*>"+String.copyValueOf(buf)+"<*");
				message += new String(buf, 0, i);
				//System.out.println( "-->"+Message+"<--");
			}
			FI.close();
		} catch (java.io.IOException ex) {
			JOptionPane.showMessageDialog(
				null,
				"Error: Cannot read from temp file used "
					+ "by external editor.");
			return false;
		}

		//System.out.println( "++>"+Message+"<++");
		//System.out.println( Message.length());

		EditView.setText(message);

		return true;
	} // END public boolean startExternalEditor()
} // END public class ExternalEditor
