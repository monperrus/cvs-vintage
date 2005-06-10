package org.columba.mail.gui.composer;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.columba.ristretto.io.FileSource;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.message.MimeType;
import org.frapuccino.iconpanel.IconPanel;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;

public class OpenAttachmentAction extends AbstractAction {

	AttachmentView view;
	
	public OpenAttachmentAction(AttachmentView view) {
		super();
		
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		int index = ((IconPanel) e.getSource()).getSelectedIndex();
		
		MimePart mimePart = view.get(index);
		MimeType type = mimePart.getHeader().getMimeType();
		if( type.getType().equals("message") && type.getSubtype().equals("rfc822") ) {
			//TODO: Open in message frame
			//TODO: Handle also message attachments from OpenInComposer action
		} else if( mimePart instanceof LocalMimePart && ((LocalMimePart)mimePart).getBody() instanceof FileSource){
			File file = ((FileSource)((LocalMimePart)mimePart).getBody()).getFile();		
		
			try {
				Desktop.open(file);
			} catch (DesktopException e1) {
			}
		}
	}

}
