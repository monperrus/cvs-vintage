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



// File: CrOppEndVsAttr.java
// Classes: CrOppEndVsAttr
// Original Author: jrobbins@ics.uci.edu
// $Id: CrOppEndVsAttr.java,v 1.14 1999/02/19 22:23:18 jrobbins Exp $

package uci.uml.critics;

import java.util.*;
import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Behavioral_Elements.Collaborations.*;

/** Well-formedness rule [2] for Classifier. See page 29 of UML 1.1
 *  Semantics. OMG document ad/97-08-04. */

//needs-more-work: split into one critic for inherited problems and
//one for pproblems directly in this class.

public class CrOppEndVsAttr extends CrUML {

  public CrOppEndVsAttr() {
    setHeadline("Rename Role or Attribute");
    sd("One of the attributes of {name} has the same name as "+
       "{name}'s role in an association.  Attributes and roles "+
       "should have distinct names.  "+
       "This may because of an inherited attribute. \n\n"+
       "Clear and unambiguous names are key to code generation and producing "+
       "an understandable and maintainable design.\n\n"+
       "To fix this, use the \"Next>\" button, or manually select the one of the "+
       "conflicting roles or attributes of this class and change its name.");

    addSupportedDecision(CrUML.decINHERITANCE);
    addSupportedDecision(CrUML.decRELATIONSHIPS);
    addSupportedDecision(CrUML.decNAMING);
    setKnowledgeTypes(Critic.KT_SYNTAX);
    addTrigger("associationEnd");
    addTrigger("structuralFeature");
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof Classifier)) return NO_PROBLEM;
    Classifier cls = (Classifier) dm;
    Vector namesSeen = new Vector();
    Vector str = cls.getStructuralFeature();
    java.util.Enumeration enum = str.elements();
    // warn about inheritied name conflicts, different critic?
    while (enum.hasMoreElements()) {
      StructuralFeature sf = (StructuralFeature) enum.nextElement();
      Name sfName = sf.getName();
      if (Name.UNSPEC.equals(sfName)) continue;
      String nameStr = sfName.getBody();
      if (nameStr.length() == 0) continue;
      namesSeen.addElement(nameStr);
    }
    Vector assocEnds = cls.getAssociationEnd();
    enum = assocEnds.elements();
    // warn about inheritied name conflicts, different critic?
    while (enum.hasMoreElements()) {
      AssociationEnd myAe = (AssociationEnd) enum.nextElement();
      Association asc = (Association) myAe.getAssociation();
      Vector conn = asc.getConnection();
      if (asc instanceof AssociationRole)
      conn = ((AssociationRole)asc).getAssociationEndRole();
      if (conn == null) continue;
      java.util.Enumeration enum2 = conn.elements();
      while (enum2.hasMoreElements()) {
	AssociationEnd ae = (AssociationEnd) enum2.nextElement();
	if (ae.getType() == cls) continue;
	Name aeName = ae.getName();
	if (Name.UNSPEC.equals(aeName)) continue;
	String aeNameStr = aeName.getBody();
	if (aeNameStr == null || aeNameStr.length() == 0) continue;
	if (namesSeen.contains(aeNameStr)) return PROBLEM_FOUND;
      }
    }
    return NO_PROBLEM;
  }

} /* end class CrOppEndVsAttr.java */

