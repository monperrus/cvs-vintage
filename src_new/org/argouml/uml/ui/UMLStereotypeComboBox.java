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

package org.argouml.uml.ui;
import org.argouml.uml.*;
import javax.swing.*;
import ru.novosoft.uml.*;
import java.awt.event.*;
import java.awt.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.model_management.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;
import java.util.*;

/**
 *   This class implements a combo box for stereotypes.
 *   The class polls the model and profile for appropriate
 *   stereotypes for the target object.  A context popup menu
 *   allows for new stereotypes to be created and existing 
 *   stereotypes to be deleted.
 *
 *   @author Curt Arnold
 */
public class UMLStereotypeComboBox extends JComboBox implements UMLUserInterfaceComponent {

    private UMLUserInterfaceContainer _container;
    private UMLStereotypeComboBoxListModel _model;
    
    public UMLStereotypeComboBox(UMLUserInterfaceContainer container) {
        super();
        _container = container;
        _model = new UMLStereotypeComboBoxListModel(container);
        setModel(_model);
    }

    public Object getTarget() {
        return _container.getTarget();
    }
        
    public void targetChanged() {
        _model.targetChanged();
    }

    public void targetReasserted() {
    }                    
    
    public void roleAdded(final MElementEvent event) {
        _model.roleAdded(event);
    }
    
    public void recovered(final MElementEvent event) {
        _model.recovered(event);
    }
    
    public void roleRemoved(final MElementEvent event) {
        _model.roleRemoved(event);
    }
    
    public void listRoleItemSet(final MElementEvent event) {
        _model.listRoleItemSet(event);
    }
    
    public void removed(final MElementEvent event) {
        _model.removed(event);
    }

    public void propertySet(final MElementEvent event) {
        _model.propertySet(event);
    }
}