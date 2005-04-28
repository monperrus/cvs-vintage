// $Id: UnknownHandler.java,v 1.1 2005/04/28 19:37:17 bobtarling Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.gef;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Handler for unknown elements in PGML files or elements that are
 * completely specified by their attributes.
 * This handler skips the element's contents
 * and sub-elements.
 */
public class UnknownHandler extends DefaultHandler {
    private int _depthCount;
    private HandlerStack _stack;
    private static Log LOG=LogFactory.getLog( UnknownHandler.class);

    /**
     * @param stack The stack of ContentHandler's for this parsing operation
     */
    public UnknownHandler( HandlerStack stack)
    {
        _depthCount=1;
        _stack=stack;
    }

    /**
     * Increments depth count
     */
    public void startElement( String uri, String localname, String qname,
        Attributes attributes) throws SAXException
    {
        LOG.info( "Ignoring unexpected element: "+qname);
        _depthCount++;
    }

    /**
     * Decrements depth count; pops itself off the stack when the depth count
     * is 0
     */
    public void endElement( String uri, String localname, String qname)
        throws SAXException
    {
        if ( --_depthCount==0)
            _stack.popHandlerStack();
    }
}
