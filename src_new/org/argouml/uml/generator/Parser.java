// $Id: Parser.java,v 1.15 2003/08/21 20:33:36 thn Exp $
// Copyright (c) 1996-2002 The Regents of the University of California. All
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
// $Id: Parser.java,v 1.15 2003/08/21 20:33:36 thn Exp $

// 12 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// extension points.


package org.argouml.uml.generator;

import java.text.ParseException;

import org.argouml.model.uml.UmlFactory;

import ru.novosoft.uml.behavior.state_machines.MEvent;
import ru.novosoft.uml.behavior.state_machines.MGuard;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.foundation.core.MParameter;

public abstract class Parser {

    public abstract Object parseExtensionPoint(String s);
    public abstract void parseOperation(String s, Object op)
	throws ParseException;
    public abstract void parseAttribute(String s, Object attr)
	throws ParseException;
    public abstract MParameter parseParameter(String s);
    //   public abstract Package parsePackage(String s);
    //   public abstract MClassImpl parseClassifier(String s);
    public abstract Object parseStereotype(String s);
    public abstract Object parseTaggedValue(String s);
    //   public abstract MAssociation parseAssociation(String s);
    //   public abstract MAssociationEnd parseAssociationEnd(String s);
    public abstract Object parseMultiplicity(String s);
    public abstract Object parseState(String s);
    public abstract MTransition parseTransition(MTransition trans, String s);
    public abstract Object parseAction(String s);
    public abstract MGuard parseGuard(String s);
    public abstract MEvent parseEvent(String s);


    public Object parseExpression(String s) {
	return UmlFactory.getFactory().getDataTypes().createExpression("Java",
								       s);
    }

    public String parseName(String s) {
	return new String(s);
    }

    public String parseUninterpreted(String s) {
	return new String(s);
    }

} /* end class Parser */
