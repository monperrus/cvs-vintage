// $Id: ActivationNode.java,v 1.4 2004/11/21 20:20:09 mvw Exp $
// Copyright (c) 2003-2004 The Regents of the University of California. All
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

package org.argouml.uml.diagram.sequence;

/**
 * An Activation Node for the sequence diagram.
 *
 */
public class ActivationNode extends Node {
    private boolean cutOffTop;

    private boolean cutOffBottom;

    private boolean end;

    private boolean start;

    /**
     * The constructor.
     * 
     */
    public ActivationNode() { }

    /**
     * @return true if start
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param b start
     */
    public void setStart(boolean b) {
        start = b;
    }

    /**
     * @param theEnd true if this is an end
     */
    public void setEnd(boolean theEnd) {
        end = theEnd;
    }

    /**
     * @return true if this is an end
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * @return true is the bottom is cut off
     */
    public boolean isCutOffBottom() {
        return cutOffBottom;
    }

    /**
     * @return true if the top is ct off
     */
    public boolean isCutOffTop() {
        return cutOffTop;
    }

    /**
     * @param b true if the bottom is cut off
     */
    public void setCutOffBottom(boolean b) {
        if (b && !(end)) {
            throw new IllegalArgumentException("Cannot cutoff "
					       + "an activationNode "
					       + "that is not an end");
        }
        cutOffBottom = b;
    }

    /**
     * @param b true if the top is cut off 
     */
    public void setCutOffTop(boolean b) {
        if (b && !(start)) {
            throw new IllegalArgumentException("Cannot cutoff "
					       + "an activationNode "
					       + "that is not a start");
        }
        cutOffTop = b;
    }

}
