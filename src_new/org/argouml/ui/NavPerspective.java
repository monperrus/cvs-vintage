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

// File: NavPerspective.java
// Classes: NavPerspective
// Original Author: your email address here

package org.argouml.ui;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.common_behavior.MComponentInstance;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.behavior.common_behavior.MNodeInstance;
import ru.novosoft.uml.behavior.common_behavior.MObject;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MGeneralization;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.model_management.MSubsystem;

/**
 * This class represents 3 concepts, although it should only represent
 * a navigation perspective, TODO: separate.
 *
 * <p>This class represents:
 *   - a navigation tree model / perspective (which is a collection of GoRules)
 *
 * <p>16 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
 * the display of extends/includes and extension points in the package centric
 * view.
 *
 * <p>$Id: NavPerspective.java,v 1.28 2003/05/05 06:47:44 kataka Exp $
 */
public class NavPerspective
    extends TreeModelComposite
    implements
        Serializable, Cloneable  {

    ////////////////////////////////////////////////////////////////
    // constructor

    /** needs documenting */
    public NavPerspective(String name) {
        super(name);
    }

    ////////////////////////////////////////////////////////////////
    // TreeModel implementation - overriding TreeModelComposite

    /**
     * Will return the first found child object in the navtree. The child can be 
     * a TreeNode in case the super will be called to handle this. In all other
     * cases we try to handle it in a recursive way.
     *
     * <p>TODO this does not work yet since the implementation of getChildren of
     * AbstractGoRule only takes one level into account.
     *
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
        
        if (child == null || parent == null) return -1;
        
        int helperindex = -1;
        for (int i = 0; i < _goRules.size(); i++) {
            
            AbstractGoRule rule =
            (AbstractGoRule) _goRules.get(i);
            // the given parent turns up to have children
            helperindex = rule.getIndexOfChild(parent, child);
            if (helperindex > -1) { // we found the correct element
                return i + helperindex;
            } else {
                helperindex = getHelperIndex(rule, parent, child);
                if (helperindex > -1) {
                    return i + helperindex;
                }
            }
            
        }
        return -1;
    }

    /** I think this only gets called during argo initialisation */
    private int getHelperIndex(
                    AbstractGoRule rule,
                    Object parent,
                    Object child) {

        if (parent == child)
            throw new IllegalStateException("Parent cannot equal child");
        if (rule.getChildCount(parent) == 0)
            return -1;
        else {
            int index = rule.getIndexOfChild(parent, child);
            if (index == -1) {
                // the level directly under the parent does not contain the child

                    int counter = 0;
                    Iterator it = rule.getChildren(parent).iterator();
                    while (it.hasNext()) {
                        index = getHelperIndex(rule, it.next(), child);
                        if (index > -1) {                       
                            return counter + index;
                        }
                        counter++;
                    }
            }
        }
        return -1;
    }

    /** required for the nav config dialog */
    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }
    
} /* end class NavPerspective */
