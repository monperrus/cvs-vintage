// $Id: Parser.java,v 1.19 2003/09/28 13:48:11 d00mst Exp $
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// File: Parser.java
// Classes: Parser
// Original Author:
// $Id: Parser.java,v 1.19 2003/09/28 13:48:11 d00mst Exp $

// 12 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// extension points.


package org.argouml.uml.generator;

import java.text.ParseException;

import org.argouml.model.uml.UmlFactory;

public abstract class Parser {

    public abstract Object parseExtensionPoint(String s);
    public abstract void parseOperation(String s, Object op)
	throws ParseException;
    public abstract void parseAttribute(String s, Object attr)
	throws ParseException;

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseParameter(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseStereotype(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseTaggedValue(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseMultiplicity(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseState(String s);

    public abstract Object parseTransition(Object trans, String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseAction(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseGuard(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public abstract Object parseEvent(String s);

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public Object parseExpression(String s) {
	return UmlFactory.getFactory().getDataTypes().createExpression("Java",
								       s);
    }

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public String parseName(String s) {
	return new String(s);
    }

    /**
     * @deprecated Since 0.15.1, this is essentially a String constructor.
     *		   It breaks the idea the idea that the parser is editing
     *		   preexisting objects, which is bad. Arguably it should not
     *		   belong to the public API.
     *		   It is not used within core ArgoUML.
     *		   d00mst.
     */
    public String parseUninterpreted(String s) {
	return new String(s);
    }

} /* end class Parser */
