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

package org.argouml.i18n;
import java.util.*;
import org.argouml.util.*;
import javax.swing.*;
import java.awt.event.*;

public class MenuResourceBundle extends ListResourceBundle {


   static final Object[][] _contents = {
        {"action.adjust-grid", "Adjust Grid" },
        {"action.adjust-grid-snap", "Adjust Grid Snap" },
        {"action.adjust-page-breaks", "Adjust Page Breaks" },
        {"action.align-bottoms", "Align Bottoms" },
        {"action.align-horizontal-centers", "Align Horizontal Centers" },
        {"action.align-lefts", "Align Lefts" },
        {"action.align-rights", "Align Rights" },
        {"action.align-to-grid", "Align To Grid" },
        {"action.align-tops", "Align Tops" },
        {"action.align-vertical-centers", "Align Vertical Centers" },
        {"action.bring-forward", "Bring Forward" },
        {"action.bring-to-front", "Bring To Front" },
        {"action.distribute-horizontal-centers", "Distribute Horizontal Centers" },
        {"action.distribute-horizontal-spacing", "Distribute Horizontal Spacing" },
        {"action.distribute-vertical-centers", "Distribute Vertical Centers"},
        {"action.distribute-vertical-spacing", "Distribute Vertical Spacing"},
        {"action.goto-checklist", "Checklist" },
        {"action.goto-constraints", "Constraints" },
        {"action.goto-javadocs", "Javadocs" },
        {"action.goto-history", "History" },
        {"action.goto-properties", "Properties" },
        {"action.goto-source", "Source" },
        {"action.goto-tagged-values", "Tagged Values" },
        {"action.goto-todo-item", "ToDo Item" },
        {"action.new", "New" },
        {"action.nudge-down", "Nudge Down" },
        {"action.nudge-left", "Nudge Left" },
        {"action.nudge-right", "Nudge Right" },
        {"action.nudge-up", "Nudge Up" },
        {"action.notation", "Notation" },
        {"action.open-project", "Open Project..." },
        {"action.save-project", "Save Project" },
        {"action.send-backward", "Send Backward" },
        {"action.send-to-back", "Send To Back" },
        {"action.save-project-as", "Save Project As..." },
        {"Import", "Import" },
        {"action.import-sources", "Import sources..." },
        {"action.print", "Print..." },
        {"Save GIF...", "Save GIF..." },
        {"action.as-diagram", "As Diagram"},
        {"action.as-metrics", "As Metrics"},
        {"action.as-table", "As Table"},
        {"action.save-graphics", "Save Graphics..." },
	{"action.save-configuration", "Save Configuration"},
        {"action.exit", "Exit" },
        {"action.undo", "Undo" },
        {"action.redo", "Redo" },
        {"action.cut", "Cut" },
        {"action.copy", "Copy" },
        {"action.paste", "Paste" },
	{"action.settings", "Settings..."},
        {"Set Source Path...", "Set Source Path..." },
        {"action.remove-from-diagram", "Remove From Diagram" },
        {"action.delete-from-model", "Delete From Model" },
        {"action.empty-trash", "Empty Trash" },
        {"action.navigate-back", "Navigate Back" },
        {"action.navigate-forward", "Navigate Forward" },
        {"action.nav-config", "NavConfig" },
        {"action.find", "Find..." },
        {"action.goto-diagram", "Goto Diagram..." },
        {"action.next-editing-tab", "Next Editing Tab" },
        {"Next Details Tab", "Next Details Tab" },
        {"action.buttons-on-selection", "Buttons on Selection" },
        {"action.create-multiple", "Create Multiple..." },
        {"action.add-top-level-package", "Add Top-Level Package" },
        {"action.class-diagram", "ClassDiagram" },
        {"action.usecase-diagram", "UseCaseDiagram" },
        {"action.state-diagram", "StateDiagram" },
        {"action.activity-diagram", "ActivityDiagram" },
        {"action.collaboration-diagram", "CollaborationDiagram" },
        {"action.deployment-diagram", "DeploymentDiagram" },
        {"action.sequence-diagram", "SequenceDiagram" },
        {"button.add-attribute", "Add Attribute" },
        {"button.add-operation", "Add Operation" },
        {"Add Message", "Add Message" },
        {"Add Internal Transition", "Add Internal Transition" },
        {"action.generate-selected-classes", "Generate Selected Classes..." },
        {"action.generate-all-classes", "Generate All Classes..." },
        {"Generate Code for Project", "Generate Code for Project..." },
        {"Settings for Generate for Project", "Settings for Generate for Project..." },
        {"action.toggle-auto-critique", "Toggle Auto-Critique" },
        {"action.design-issues", "Design Issues..." },
        {"action.design-goals", "Design Goals..." },
        {"action.browse-critics", "Browse Critics..." },
        {"action.toggle-flat-view", "Toggle Flat View" },
        {"action.new-todo-item", "New To Do Item..." },
        {"action.resolve-item", "Resolve Item..." },
        {"action.send-email-to-expert", "Send Email To Expert..." },
        {"action.more-info", "More Info..." },
        {"action.snooze-critic", "Snooze Critic" },
        {"action.about-argouml", "About ArgoUML..." },
        {"action.zoom-percent-10", "10 %" },
        {"action.zoom-percent-25", "25 %" },
        {"action.zoom-percent-50", "50 %" },
        {"action.zoom-percent-75", "75 %" },
        {"action.zoom-percent-100", "100 %" },
        {"action.zoom-percent-150", "150 %" },
        {"Properties", "Properties" },
        {"1", "1" },
        {"0..1", "0..1" },
        {"0..*", "0..*" },
        {"1..*", "1..*" },
        {"aggregate", "aggregate" },
        {"composite", "composite" },
        {"none", "none" },
        {"Show Attribute Compartment", "Show Attribute Compartment" },
        {"Hide Attribute Compartment", "Hide Attribute Compartment" },
        {"Show Operation Compartment", "Show Operation Compartment" },
        {"Hide Operation Compartment", "Hide Operation Compartment" },
        {"Show All Compartments", "Show All Compartments" },
        {"Hide All Compartments", "Hide All Compartments" },
        {"File", "File" },
        {"Mnemonic_File", "F" },
        {"Mnemonic_New", "N" },
        {"Mnemonic_Open", "O" },
        {"Mnemonic_Save", "S" },
        {"Mnemonic_SaveAs", "A" },
        {"Mnemonic_Print", "P" },
        {"Mnemonic_SaveGraphics", "G" },
        {"Mnemonic_Exit", "X" },
        {"Edit", "Edit" },
        {"Mnemonic_Edit", "E" },
        {"Select", "Select" },
        {"Mnemonic_Cut", "X" },
        {"Mnemonic_Copy", "C" },
        {"Mnemonic_Paste", "V" },
        {"Mnemonic_RemoveFromDiagram", "R" },
        {"Mnemonic_DeleteFromModel", "D" },
        {"View", "View" },
        {"Mnemonic_View", "V" },
        {"Editor Tabs", "Editor Tabs" },
        {"Details Tabs", "Details Tabs" },
        {"Create", "Create" },
        {"Mnemonic_Create", "C" },
        {"Diagrams", "Diagrams" },
        {"Create Diagram", "Create Diagram" },
        {"Arrange", "Arrange" },
        {"Mnemonic_Arrange", "A" },
        {"Align", "Align" },
        {"Distribute", "Distribute" },
        {"Reorder", "Reorder" },
        {"Nudge", "Nudge" },
        {"Generation", "Generation" },
        {"Mnemonic_Generate", "G" },
        {"Critique", "Critique" },
        {"Mnemonic_Critique", "R" },
        {"Help", "Help" },
        {"Mnemonic_Help", "H" },
        {"action.layout-automatic", "Automatic" },
        {"action.layout-incremental", "Incremental" },
        {"Automatic", "Automatic" },
        {"Incremental", "Incremental" },
        {"ToDoItem", "ToDoItem" },
        {"Javadocs", "Javadocs" },
        {"Source", "Source" },
        {"Constraints", "Constraints" },
        {"tab.checklist", "Checklist" },
        {"tab.constraints", "Constraints" },
        {"tab.documentation", "Documentation" },
        {"tab.history", "History" },
        {"tab.properties", "Properties" },
        {"tab.source", "Source" },
        {"tab.style", "Style" },
        {"tab.tagged-values", "TaggedValues" },
        {"tab.todo-item", "ToDoItem" },
        {"TaggedValues", "TaggedValues" },
        {"Checklist", "Checklist" },
        {"History", "History" },
        {"Layout", "Layout" },
        {"Zoom", "Zoom"},
        {"Tools", "Tools"},
    // arrange menu
        {"Set minimum size", "Fit size for content"},
        {"Toggle Auto Resizing", "Automatically fit size for content"},
    // shortcut keys
        { "Shortcut_New", KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK) },
        { "Shortcut_Open", KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK) },
        { "Shortcut_Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK) },
        { "Shortcut_Print", KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK) },
        { "Shortcut_Select_All", KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK) },
        { "Shortcut_Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK) },
        { "Shortcut_Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK) },
        { "Shortcut_Cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK) },
        { "Shortcut_Remove_From_Diagram", KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK) },
        { "Shortcut_Find", KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0) },
        { "Shortcut_Generate_All", KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0) },
        { "Shortcut_Exit", KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK) },
        { "Shortcut_Delete", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)}
   };

     public Object[][] getContents() {
        return _contents;
     }
}
