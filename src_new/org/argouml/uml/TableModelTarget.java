// $Id: TableModelTarget.java,v 1.3 2003/08/31 15:19:41 alexb Exp $
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

import java.beans.*;
import java.util.*;
import javax.swing.table.TableModel;

import org.tigris.gef.util.Predicate;

import org.argouml.kernel.*;

/**
 * @deprecated this class is deprecated since 0.15.1 and should be removed
 *             in 0.15.2, due to the fact that the TableModel classes
 *             have never been used, and are not maintained,
 *
 *             There is no reason why someone cannot try to complete the
 *             TableModel implementation, however, a higher priority
 *             at the moment is to clean argouml of un maintained code.
 */

public interface TableModelTarget
    extends TableModel, VetoableChangeListener, DelayedVChangeListener 
{
    public void setTarget(Object target);
    public void setFilter(Predicate p);
} /* end interface TableModelTarget */
