// $Id: UMLDiscriminatorNameDocument.java,v 1.3 2004/12/21 14:02:42 mvw Exp $
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

package org.argouml.uml.ui.foundation.core;

import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLPlainTextDocument;

/**
 * The document that is edited in the discriminator field of a generalization.
 * 
 * @since Oct 10, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class UMLDiscriminatorNameDocument extends UMLPlainTextDocument {

    /**
     * Constructor for UMLModelElementNameDocument.
     */
    public UMLDiscriminatorNameDocument() {
        super("discriminator");
    }

    /**
     * @see org.argouml.uml.ui.UMLPlainTextDocument#setProperty(java.lang.String)
     */
    protected void setProperty(String text) {
        ModelFacade.setDiscriminator(getTarget(), text);
    }

    /**
     * @see org.argouml.uml.ui.UMLPlainTextDocument#getProperty()
     */
    protected String getProperty() {
        return (String) ModelFacade.getDiscriminator(getTarget());
    }

}