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
package org.columba.mail.pop3;

import org.columba.core.command.StatusObservable;
import org.columba.core.logging.ColumbaLogger;
import org.columba.core.main.MainInterface;

import org.columba.mail.folder.headercache.AbstractHeaderCache;
import org.columba.mail.folder.headercache.CachedHeaderfields;
import org.columba.mail.folder.headercache.ObjectReader;
import org.columba.mail.folder.headercache.ObjectWriter;
import org.columba.mail.message.ColumbaHeader;
import org.columba.mail.message.HeaderList;
import org.columba.mail.util.MailResourceLoader;

import java.util.Enumeration;


/**
 * Header caching facility very similar to the ones used by folders.
 * <p>
 * We need this for the managing server/messages remotely feature, which shows
 * a messagelist of all messages on the POP3 server to the user.
 *
 * @author freddy
 */
public class POP3HeaderCache extends AbstractHeaderCache {
    protected POP3Server server;

    /**
     * Constructor for POP3HeaderCache.
     *
     * @param folder
     */
    public POP3HeaderCache(POP3Server server) {
        super(server.getConfigFile());

        this.server = server;
    }

    public StatusObservable getObservable() {
        return server.getObservable();
    }

    public void load() throws Exception {
        ColumbaLogger.log.info("loading header-cache=" + headerFile);
        headerList = new HeaderList();

        try {
            reader = new ObjectReader(headerFile);
        } catch (Exception e) {
            if (MainInterface.DEBUG) {
                e.printStackTrace();
            }
        }

        int capacity = ((Integer) reader.readObject()).intValue();
        ColumbaLogger.log.info("capacity=" + capacity);

        if (getObservable() != null) {
            getObservable().setMessage(MailResourceLoader.getString(
                    "statusbar", "message", "load_headers"));
        }

        if (getObservable() != null) {
            getObservable().setMax(capacity);
        }

        for (int i = 1; i <= capacity; i++) {
            if (getObservable() != null) {
                getObservable().setCurrent(i);
            }

            ColumbaHeader h = new ColumbaHeader();

            loadHeader(h);

            headerList.add(h, h.get("columba.pop3uid"));

            //headerList.add(h, (String) h.get("columba.uid"));
        }

        // close stream
        reader.close();
    }

    public void save() throws Exception {
        // we didn't load any header to save
        if (!isHeaderCacheLoaded()) {
            return;
        }

        ColumbaLogger.log.info("saving header-cache=" + headerFile);

        try {
            writer = new ObjectWriter(headerFile);
        } catch (Exception e) {
            if (MainInterface.DEBUG) {
                e.printStackTrace();
            }
        }

        int count = headerList.count();

        if (count == 0) {
            return;
        }

        writer.writeObject(new Integer(count));

        ColumbaHeader h;

        for (Enumeration e = headerList.keys(); e.hasMoreElements();) {
            String str = (String) e.nextElement();

            h = (ColumbaHeader) headerList.get(str);

            saveHeader(h);
        }

        writer.close();
    }

    protected void loadHeader(ColumbaHeader h) throws Exception {
        String[] columnNames = CachedHeaderfields.POP3_HEADERFIELDS;

        for (int j = 0; j < columnNames.length; j++) {
            h.set(columnNames[j], reader.readObject());
        }
    }

    protected void saveHeader(ColumbaHeader h) throws Exception {
        String[] columnNames = CachedHeaderfields.POP3_HEADERFIELDS;
        Object o;

        for (int j = 0; j < columnNames.length; j++) {
            writer.writeObject(h.get(columnNames[j]));
        }
    }
}
