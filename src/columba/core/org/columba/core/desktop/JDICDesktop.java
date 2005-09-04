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
package org.columba.core.desktop;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.columba.api.desktop.IDesktop;
import org.columba.core.resourceloader.GlobalResourceLoader;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.jdic.filetypes.Action;
import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;

public class JDICDesktop implements IDesktop {
	
	/** JDK 1.4+ logging framework logger, used for logging. */
	private static final Logger LOG = Logger
			.getLogger("org.columba.core.desktop");

	private AssociationService associationService;

	public JDICDesktop() {
		 associationService = new AssociationService();
	}

	public String getMimeType(File file) {
		String mimetype = "application/octet-stream";
		
		try {
			Association a = associationService.getAssociationByContent( file.toURL());
			if( a != null) {
				return a.getMimeType();
			}
		} catch (MalformedURLException e) {
		}
		
		return mimetype;
	}

	public String getMimeType(String ext) {
		String mimetype = "application/octet-stream";
		
		Association a = associationService.getFileExtensionAssociation( ext);
		if( a != null) {
			return a.getMimeType();
		}
		
		return mimetype;
	}

	public boolean supportsOpen() {
		return true;
	}

	public boolean open(File file) {
		try {
			Desktop.open(file);
		} catch (DesktopException e) {
			JOptionPane.showMessageDialog(null, GlobalResourceLoader
					.getString("org.columba.core.i18n.dialog", "error", "no_viewer"), "Error",
					JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		return true;
	}

	public boolean openAndWait(File file) {
		Association association = new AssociationService().getMimeTypeAssociation("text/plain");
		Action action = association.getActionByVerb("open");
		
		String command = action.getCommand();

		// Add the file either as parameter %1 or append to the end
		command = command.replaceAll("%1", file.getPath());
		
		if( command.indexOf(file.getPath()) == -1) {
			command = command + " " + file.getPath();
		}
		
		LOG.info("executing command: "+command);
		
		Process child;
		try {
			child = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			return false;
		}

		if (child == null) {
			return false;
		}

		try {
			// Wait for external editor to quit
			child.waitFor();

		} catch (InterruptedException ex) {
			return false;
		}
		
		return true;
	}
	
	public boolean supportsBrowse() {
		return true;
	}

	public void browse(URL url) {
		try {
			Desktop.browse(url);
		} catch (DesktopException e) {
			JOptionPane.showMessageDialog(null, GlobalResourceLoader
					.getString("org.columba.core.i18n.dialog", "error", "no_browser"), "Error",
					JOptionPane.ERROR_MESSAGE);			
		}
	}

}
