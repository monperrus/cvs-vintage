// $Id: AttrKeyword.java,v 1.10 2003/09/22 18:58:41 bobtarling Exp $
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

package org.argouml.uml;

import java.io.Serializable;
import org.apache.log4j.Logger;
import org.argouml.model.ModelFacade;

/** This class  handles the
 * none
 * static
 * final
 * static final
 * transient
 * for the UML foundation data type
 *
 * @deprecated this class is deprecated since 0.15.1 and should be removed
 *             in 0.15.2, due to the fact that the TableModel classes
 *             have never been used, and are not maintained,
 *
 *             There is no reason why someone cannot try to complete the
 *             TableModel implementation, however, a higher priority
 *             at the moment is to clean argouml of un maintained code.
 */
public class AttrKeyword implements Serializable {
    protected static Logger cat = Logger.getLogger(AttrKeyword.class);
    
    public static final AttrKeyword NONE = new AttrKeyword("none");
    public static final AttrKeyword STATIC = new AttrKeyword("static");
    public static final AttrKeyword FINAL = new AttrKeyword("final");
    public static final AttrKeyword STATFIN = new AttrKeyword("static final");
    public static final AttrKeyword TRANS = new AttrKeyword("transient");


    public static final AttrKeyword[] POSSIBLES = {
	NONE, STATIC, FINAL, STATFIN, TRANS };

    protected String _label = null;
  
    private AttrKeyword(String label) { _label = label; }
  
    public static AttrKeyword KeywordFor(Object/*MAttribute*/ attr) {
	Object/*MScopeKind*/ sk = ModelFacade.getOwnerScope(attr);
	Object/*MChangeableKind*/ ck = ModelFacade.getChangeability(attr);
	// TODO final?
        if (ModelFacade.CLASSIFIER_SCOPEKIND.equals(sk)) {
            if (ModelFacade.FROZEN_CHANGEABLEKIND.equals(ck)) {
                return STATFIN;
            } else {
                return STATIC;
            }
        } else if (ModelFacade.FROZEN_CHANGEABLEKIND.equals(ck)) {
	    return FINAL;
        } else {
	    return NONE;
        }
    }
  
    public boolean equals(Object o) {
	if (!(o instanceof AttrKeyword)) return false;
	String oLabel = ((AttrKeyword) o)._label;
	return _label.equals(oLabel);
    }

    public int hashCode() { return _label.hashCode(); }
  
    public String toString() { return _label.toString(); }

    public void set(Object/*MAttribute*/ target) {
	//    MChangeableKind ck = MChangeableKind.NONE;
	Object/*MChangeableKind*/ ck = null;
	Object/*MScopeKind*/ sk = ModelFacade.INSTANCE_SCOPEKIND;

	if (this == TRANS)
	    cat.info("TODO: transient not supported");
     
	if (this == FINAL || this == STATFIN) ck = ModelFacade.FROZEN_CHANGEABLEKIND;
	if (this == STATIC || this == STATFIN) sk = ModelFacade.CLASSIFIER_SCOPEKIND;
      
	ModelFacade.setChangeability(target, ck);
	ModelFacade.setOwnerScope(target, sk);
	// TODO: final
    }
} /* end class AttrKeyword */
