/*
 * Created on 12.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.columba.mail.gui.config.template;

import org.columba.mail.message.HeaderList;

import org.columba.ristretto.message.HeaderInterface;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 * @author frd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HeaderCellRenderer extends DefaultListCellRenderer {
    HeaderList list;

    /**
     *
     */
    public HeaderCellRenderer(HeaderList list) {
        super();
        this.list = list;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList arg0, Object arg1,
        int arg2, boolean arg3, boolean arg4) {
        super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);

        HeaderInterface header = (HeaderInterface) list.get(arg1);
        String subject = (String) header.get("Subject");

        setText(subject);

        return this;
    }
}
