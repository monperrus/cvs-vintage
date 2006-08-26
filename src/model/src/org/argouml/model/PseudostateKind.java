// $Id: PseudostateKind.java,v 1.4 2006/08/26 09:47:20 linus Exp $
// Copyright (c) 2005-2006 The Regents of the University of California. All
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

package org.argouml.model;

/**
 * The different PseudostateKinds.
 */
public interface PseudostateKind {
    /**
     * @return Returns the Choice (Branch in UML 1.3) PseudostateKind.
     * @since UML 1.4
     */
    Object getChoice();

    /**
     * @return Returns the DeepHistory PseudostateKind.
     */
    Object getDeepHistory();

    /**
     * @return Returns the Fork PseudostateKind.
     */
    Object getFork();

    /**
     * @return Returns the Initial PseudostateKind.
     */
    Object getInitial();

    /**
     * @return Returns the Join PseudostateKind.
     */
    Object getJoin();

    /**
     * @return Returns the Junction PseudostateKind.
     */
    Object getJunction();

    /**
     * @return Returns the ShallowHistory PseudostateKind.
     */
    Object getShallowHistory();
}
