// $Id: FalseActionExit.java,v 1.1 2004/10/03 15:25:36 linus Exp $
// Copyright (c) 2004 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.application.api;

import org.argouml.uml.ui.ActionExit;

/**
 * Help to test ActionExit since we don't want to really do exit in the
 * test case.
 */
public class FalseActionExit extends ActionExit {
    private boolean isExited = false;
    private String argument = null;
    private static FalseActionExit lastInvoked = null;
    
    /**
     * @see org.argouml.application.api.CommandLineInterface#doCommand(java.lang.String)
     */
    public boolean doCommand(String args) {
        lastInvoked = this;
        argument = args;
        isExited = true;
        return true;
    }

    /**
     * @return Returns the argument.
     */
    public String getArgument() {
        return argument;
    }

    /**
     * @return Returns the isExited.
     */
    public boolean isExited() {
        return isExited;
    }

    /**
     * @return Returns the lastInvoked.
     */
    public static FalseActionExit getLast() {
        return lastInvoked;
    }
}

