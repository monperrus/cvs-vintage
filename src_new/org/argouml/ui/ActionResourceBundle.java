// Copyright (c) 1996-2001 The Regents of the University of California. All
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

package org.argouml.ui;
import java.util.*;
import org.argouml.util.*;
import javax.swing.*;
import java.awt.event.*;

public class ActionResourceBundle extends ListResourceBundle {

  static final Object[][] _contents = {
    {"template.save_project.confirm_overwrite", "Are you sure you want to overwrite {0}?"},
    {"template.save_project.status_writing", "Writing {0}..."},
    {"template.save_project.status_wrote", "Wrote {0}"},
    {"template.save_project.file_not_found",
       "A problem occurred while saving: \"{0}\".\n" +
       "Your file might be corrupted."},
    {"template.save_project.io_exception",
       "A problem occurred while saving: \"{0}\".\n" +
       "Your file might be corrupted."},
    {"text.save_project.confirm_overwrite_title", "Confirm overwrite"},
    {"text.save_project.file_not_found_title", "Problem while saving"},
    {"text.save_project.io_exception_title", "Problem while saving"},
    {"text.save_as_project.unstable_release",
       "This is a developer release of ArgoUML. You should not use it \n" +
       "for production use, it's only for testing. You may save your models,\n" +
       "but do not expect future releases of ArgoUML to be able to read them.\n" +
       "If you want to use a \"stable\" release, please go to www.argouml.org\n" +
       "and get one there. Thank you."},
    {"text.save_as_project.unstable_release_title", "Warning"},
    {"text.save_as_project.chooser_title", "Save Project: "}
  };

  public Object[][] getContents() {
    return _contents;
  }
}