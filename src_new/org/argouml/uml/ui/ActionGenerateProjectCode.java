// Copyright (c) 1996-01 The Regents of the University of California. All
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

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.generator.Generator;
import org.argouml.uml.generator.ui.ClassGenerationDialog;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MNamespace;

/** Action to trigger code generation for all classes/interfaces in the
 *  project, which have a source code path set in tagged value 'src_path'
 *  @stereotype singleton
 */
public class ActionGenerateProjectCode extends UMLAction {

    ////////////////////////////////////////////////////////////////
    // static variables

    public static ActionGenerateProjectCode SINGLETON = new ActionGenerateProjectCode();


    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionGenerateProjectCode() {
	super("Generate Code for Project", NO_ICON);
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent ae) {
      Vector classes = new Vector();
      Project p = ProjectManager.getManager().getCurrentProject();
      Collection elems = ModelManagementHelper.getHelper().getAllModelElementsOfKind(MClassifier.class);
      Iterator iter = elems.iterator();
      while (iter.hasNext()) {
        MClassifier cls = (MClassifier)iter.next();
        if (isCodeRelevantClassifier(cls)) {
          classes.addElement(cls);
        }
      }
      ClassGenerationDialog cgd = new ClassGenerationDialog(classes,true);
      cgd.show();
    }

    public boolean shouldBeEnabled() {
      ProjectBrowser pb = ProjectBrowser.TheInstance;
      ArgoDiagram activeDiagram = pb.getActiveDiagram();
      return super.shouldBeEnabled() && (activeDiagram instanceof UMLClassDiagram);
    }

    private boolean isCodeRelevantClassifier(MClassifier cls) {
      String path = Generator.getCodePath(cls);
      if (path != null) {
        return (path.length() > 0);
      }
      MNamespace parent = cls.getNamespace();
      parent = parent.getNamespace();
      while (parent != null) {
        path = Generator.getCodePath(parent);
        if (path != null) {
          return (path.length() > 0);
        }
        parent = parent.getNamespace();
      }
      return false;
    }

} /* end class ActionGenerateProjectCode */
