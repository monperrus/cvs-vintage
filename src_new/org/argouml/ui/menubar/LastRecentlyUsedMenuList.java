// $Id: LastRecentlyUsedMenuList.java,v 1.1 2003/11/11 23:54:59 linus Exp $
// Copyright (c) 2003 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.ui.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.argouml.application.api.Configuration;
import org.argouml.application.api.ConfigurationKey;
import org.argouml.application.api.Argo;
import org.argouml.uml.ui.ActionReopenProject;

/**
 * menu extension for last recently used files menu
 * 
 * functionality:
 * it is created with a link to the (file-)menu, 
 * if it is created it is reading its content from config information
 * the add entry method adds a specific filename to the list, ensures that it
 * bubbles at top of list if it is already member of list
 * typically called by SaveFile method
 *
 * @author  Frank Jelinek
 * @since 9. November 2003 (0.15.2)
 */
public class LastRecentlyUsedMenuList {
    
    /**
     * default value for maxcount if there is no configuration
     */
    private final int _maxCountDefault = 4;

    /**
     * menu where the list is bound to
     */
    private JMenu _fileMenu = null;

    /**
     * recent loaded count
     */
    private int _lruCount = 0;

    /**
     * maxcount read from configuration
     */
    private int _maxCount = _maxCountDefault;

    /**
     * menuitems actually created and added to menu
     */
    private JMenuItem[] _menuItems = new JMenuItem[_maxCount];

    /**
     * Array of conf keys for accessing the stored entries
     */
    private ConfigurationKey[] _confKeys;
    
    /**
     * Adds the eventhandler to the menu and renames the entry
     */
    private JMenuItem addEventHandler( String filename) {
        JMenuItem item = _fileMenu.add(new ActionReopenProject());
        // the text is used by the event handler for opening the project
        item.setText(filename);
        return item;
    }

    /**
     * Creates a new instance of LastRecentlyUsedMenuList with respect
     * to the menu where the lru is bound to
     * @param filemenu at this menu the lru is bound after last entry
     * it is added with a separator
     *
     */
    public LastRecentlyUsedMenuList( JMenu filemenu) {
        String newName;
        int i;
        
        // holds file menu
        _fileMenu = filemenu;
        _lruCount = 0;
        
        // init from config
        // read number, write result as new default and prepare keys
        _maxCount = Configuration.getInteger(Argo.KEY_NUMBER_LAST_RECENT_USED,
					     _maxCountDefault);
        Configuration.setInteger(Argo.KEY_NUMBER_LAST_RECENT_USED, _maxCount);
        _confKeys = new ConfigurationKey[_maxCount];
        
        // create all nessessary configuration keys for lru
        for (i = 0; i < _maxCount; i++) {
            _confKeys[i] = 
		Configuration.makeKey("project", 
				      "mostrecent", 
				      "filelist".concat(Integer.toString(i)));
        }
        
        // read existing file names from configuration
        i = 0;
        boolean readOK = true;
        while (i < _maxCount && readOK) {
            newName = Configuration.getString(_confKeys[i], "");
            if (newName.length() > 0) {
                if (i == 0)
                    _fileMenu.addSeparator(); // just for first line!
                
                _menuItems[i] = addEventHandler(newName);
                i++;
            }
            else
                readOK = false; // empty entry stops reading --> last line!
        }
        
        // this is the recent count
        _lruCount = i;
    }
    
    /** 
     * Adds a new entry to lru list and removes the last one if
     * if it increases maxCount
     *
     * @param filename name of link which is to be used to represent
     * _and_ reopen the file
     */
    public void addEntry( String filename) {
        // add separator only the first time we are adding an entry
        if (0 == _lruCount)
            _fileMenu.addSeparator();

        // get already existing names
        String tempNames[] = new String[_maxCount];
        for (int i = 0; i < _lruCount; i++)
            tempNames[i] = _menuItems[i].getText();
        
        // delete all existing entries
        for (int i = 0; i < _lruCount; i++)
            _fileMenu.remove(_menuItems[i]);
        
        // add new entry as first entry
        _menuItems[0] = addEventHandler(filename);

        // add other existing entries, but filter the just added one
        int i, j;
        i = 0; 
	j = 1;
        while ( i < _lruCount && j < _maxCount) {
            if ( !(tempNames[i].equals(filename))) {
                _menuItems[j] = addEventHandler(tempNames[i]);
                j++;
            }
            i++;
        }
        
        // save count
        _lruCount = j;
        
        // and store configuration props
        for ( int k = 0; k < _lruCount; k++) {
            Configuration.setString(_confKeys[k], _menuItems[k].getText());
        }
    }
}
