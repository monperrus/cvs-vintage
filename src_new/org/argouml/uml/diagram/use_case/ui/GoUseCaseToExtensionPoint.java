// $Id: GoUseCaseToExtensionPoint.java,v 1.16 2003/09/04 20:11:43 thierrylach Exp $
// Copyright (c) 1996-99 The Regents of the University of California. All
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

// File: GoUseCaseToExtensionPoint.java
// Classes: GoUseCaseToExtensionPoint
// Original Author: mail@jeremybennett.com
// $Id: GoUseCaseToExtensionPoint.java,v 1.16 2003/09/04 20:11:43 thierrylach Exp $

// 16 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Created to support
// display of extension points in the navigator pane.


package org.argouml.uml.diagram.use_case.ui;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.model.ModelFacade;
import org.argouml.ui.AbstractGoRule;

/**
 * <p>Provides a rule to display extension points in the navigation pane.</p>
 *
 * @author  16 Apr 2002. Jeremy Bennett (mail@jeremybennett.com).
 */

public class GoUseCaseToExtensionPoint extends AbstractGoRule {
    
    protected static Logger cat =
	Logger.getLogger(GoUseCaseToExtensionPoint.class);

    /**
     * <p>Give a name to this rule.</p>
     *
     * @return  The name of the rule ("<code>Use Case->Extension
     *          Point</code>"). 
     */

    public String getRuleName() {
        return Argo.localize ("Tree", "Use Case->Extension Point");
    }

    public Collection getChildren(Object parent) { 
        if (ModelFacade.isAUseCase(parent)) {
            return ModelFacade.getExtensionPoints(parent);
        }
        return null;
    }

}  /* End of class GoUseCaseToExtensionPoint */