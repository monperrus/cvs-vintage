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
package org.columba.core.main;

import org.columba.addressbook.gui.tree.AddressbookTreeModel;

import org.columba.core.backgroundtask.BackgroundTaskManager;
import org.columba.core.command.DefaultProcessor;
import org.columba.core.config.Config;
import org.columba.core.gui.ClipboardManager;
import org.columba.core.gui.focus.FocusManager;
import org.columba.core.plugin.PluginManager;
import org.columba.core.shutdown.ShutdownManager;


/**
 * Main Interface keeping static instances of all objects
 * which need to be accessed by other subsystems.
 * <p>
 *
 * @author fdietz
 */
public class MainInterface {
    // current version
    public static final String version = "1.0 Milestone M1";

    // if true, enables debugging output from org.columba.core.logging 
    public static boolean DEBUG = false;

    // configuration file management
    public static Config config;

    // addressbook treemodel
    // TODO: move this to the addressbook component
    public static AddressbookTreeModel addressbookTreeModel;

    // scheduler
    public static DefaultProcessor processor;
    public static PluginManager pluginManager;

    // tasks which are executed on exiting Columba
    public static ShutdownManager shutdownManager;

    // tasks which are executed by a timer in the background
    // if the program is currently in idle mode
    public static BackgroundTaskManager backgroundTaskManager;

    // every component using cut/copy/paste/etc. uses this manager
    public static ClipboardManager clipboardManager;

    // focus manager needed for cut/copy/paste/etc.
    public static FocusManager focusManager;

    public MainInterface() {
    }
}
