// $Id: PerspectiveConfigurator.java,v 1.12 2004/10/23 10:11:43 mvw Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.ui.explorer;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.ui.ArgoDialog;
import org.argouml.ui.SpacerPanel;
import org.argouml.ui.explorer.rules.PerspectiveRule;

/**
 *
 * The "Configure Perspectives" dialog.
 *
 * This class replaces the old Perspective configurator and improves it by:
 * - saving perspectives to the user profile.
 *
 * Created on 21 December 2003, 21:47
 * @author  alexb
 */
public class PerspectiveConfigurator extends ArgoDialog {
    
    private static final Logger LOG =
	Logger.getLogger(PerspectiveConfigurator.class);
    
    private static int numNavConfig = 0;

    /** Insets in pixels  */
    private static final int INSET_PX = 3;
    
    ////////////////////////////////////////////////////////////////
    // instance variables
    
    private JPanel  configPanel;
    private JList   perspectiveList;
    private JList   perspectiveRulesList;
    private JList   ruleLibraryList;
    private JButton newPerspectiveButton;
    private JButton removePerspectiveButton;
    private JButton duplicatePerspectiveButton;
    private JButton moveUpButton, moveDownButton;
    private JButton addRuleButton;
    private JButton removeRuleButton;
    
    private DefaultListModel perspectiveListModel;
    private DefaultListModel perspectiveRulesListModel;
    private DefaultListModel ruleLibraryListModel;
    
    /** 
     * Creates a new instance of PerspectiveDesignerDialog.
     * 
     * @param parent the parent frame
     */
    public PerspectiveConfigurator(Frame parent) {
        
        super(parent,
	      Translator.localize("dialog.title.configure-perspectives"),
	      ArgoDialog.OK_CANCEL_OPTION,
	      true);
        
        initPersPanel();
        loadData();
        
        setContent(configPanel);
        
        getOkButton().addActionListener(new OkListener());

        numNavConfig++;
    }
    
    /**
     * load the perspectives from the perspective manager for presentation.
     */
    private void loadData() {
        
        Vector perspectives = new Vector();
        Vector perspectivesBackup = new Vector();
        Vector rulesLib = new Vector();
        
        perspectives.addAll(PerspectiveManager.getInstance().getPerspectives());
        rulesLib.addAll(PerspectiveManager.getInstance().getRules());
        
        // must add an editable list of new ExplorerPerspective's
        // to the list model so that the orginal ones are not changed
        // in the case of a cancel action by the user.
        for (int i = 0; i < perspectives.size(); i++) {
            
            ExplorerPerspective perspective =  
                (ExplorerPerspective) perspectives.get(i);
            Object[] ruleArray = perspective.getRulesArray();
            
            ExplorerPerspective editablePerspective = 
                new ExplorerPerspective(perspective.toString());
            for (int r = 0; r < ruleArray.length; r++) {
                editablePerspective.addRule((PerspectiveRule) ruleArray[r]);
            }
            
            perspectiveListModel.addElement(editablePerspective);
        }
        for (int i = 0; i < rulesLib.size(); i++) {
            ruleLibraryListModel.addElement(rulesLib.get(i));
        }
    }
    
    /**
     * Initialize the Perspectives tab panel.
     */
    public void initPersPanel() {
        configPanel = new JPanel();
                
	makeLists();
        
        makeButtons();
        
        makeLayout();
        
        makeActionListeners();
        
        //disable the buttons for now, since no selection has been made yet
        removePerspectiveButton.setEnabled(false);
        duplicatePerspectiveButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
        addRuleButton.setEnabled(false);
        removeRuleButton.setEnabled(false);
    }
    
    /**
     * Add action listeners to the buttons and lists.
     */
    private void makeActionListeners() {
        newPerspectiveButton.addActionListener(new NewPerspectiveListener());
        removePerspectiveButton.addActionListener(
                new RemovePerspectiveListener());
        duplicatePerspectiveButton.addActionListener(
                new DuplicatePerspectiveListener());
        moveUpButton.addActionListener(new MoveUpListener());
        moveDownButton.addActionListener(new MoveDownListener());
        addRuleButton.addActionListener(new RuleListener());
        removeRuleButton.addActionListener(new RuleListener());
        
        perspectiveList.addListSelectionListener(
                new PerspectiveListSelectionListener());
        perspectiveRulesList.addListSelectionListener(
                new RulesListSelectionListener());
        perspectiveRulesList.addMouseListener(new RuleListMouseListener());
        ruleLibraryList.addListSelectionListener(
                new LibraryListSelectionListener());
        ruleLibraryList.addMouseListener(new RuleListMouseListener());
    }

    /**
     * Make the layout for the dialog box.
     */
    private void makeLayout() {
        GridBagLayout gb = new GridBagLayout();
        configPanel.setLayout(gb);
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 3;      c.ipady = 3;
        
        JLabel persLabel = new JLabel(
            Translator.localize("label.perspectives"));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;      c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 1.0;  c.weighty = 0.0;
        gb.setConstraints(persLabel, c);
        configPanel.add(persLabel);
        
        JScrollPane persScroll = new JScrollPane(perspectiveList,
			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.gridx = 0;	  c.gridy = 1;
        c.gridwidth = 4;
        c.weightx = 1.0;  c.weighty = 1.0;
        gb.setConstraints(persScroll, c);
        configPanel.add(persScroll);
        
        JPanel persButtons = new JPanel(new GridLayout(5, 1, 0, 5));
        persButtons.add(newPerspectiveButton);
        persButtons.add(removePerspectiveButton);
        persButtons.add(duplicatePerspectiveButton);
        persButtons.add(moveUpButton);
        persButtons.add(moveDownButton);
        JPanel persButtonWrapper =
	    new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        persButtonWrapper.add(persButtons);
        c.gridx = 4;      c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.0;  c.weighty = 0.0;
        c.ipadx = 0;      c.ipady = 0;
        c.insets = new Insets(0, 5, 0, 0);
        gb.setConstraints(persButtonWrapper, c);
        configPanel.add(persButtonWrapper);
        
        JLabel ruleLibLabel = new JLabel(
            Translator.localize("label.rules-library"));
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0;  c.weighty = 0.0;
        c.ipadx = 3;      c.ipady = 3;
        c.insets = new Insets(10, 0, 0, 0);
        gb.setConstraints(ruleLibLabel, c);
        configPanel.add(ruleLibLabel);
        
        addRuleButton.setMargin(new Insets(2, 15, 2, 15));
        removeRuleButton.setMargin(new Insets(2, 15, 2, 15));
        JPanel xferButtons = new JPanel();
        xferButtons.setLayout(new BoxLayout(xferButtons, BoxLayout.Y_AXIS));
        xferButtons.add(addRuleButton);
        xferButtons.add(new SpacerPanel());
        xferButtons.add(removeRuleButton);
        c.gridx = 2;      c.gridy = 4;
        c.weightx = 0.0;  c.weighty = 0.0;
        c.insets = new Insets(0, 3, 0, 5);
        gb.setConstraints(xferButtons, c);
        configPanel.add(xferButtons);
        
        JLabel rulesLabel = new JLabel(
            Translator.localize("label.selected-rules"));
        c.gridx = 3;      c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.insets = new Insets(10, 0, 0, 0);
        gb.setConstraints(rulesLabel, c);
        configPanel.add(rulesLabel);
        
        c.gridx = 0;      c.gridy = 4;
        c.weighty = 1.0;
        c.gridwidth = 2;  c.gridheight = 2;
        c.insets = new Insets(0, 0, 0, 0);
        JScrollPane ruleLibScroll =
	    new JScrollPane(ruleLibraryList,
			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gb.setConstraints(ruleLibScroll, c);
        configPanel.add(ruleLibScroll);
        
        c.gridx = 3;	  c.gridy = 4;
        c.gridwidth = 2;  c.gridheight = 2;
        JScrollPane rulesScroll = new JScrollPane(perspectiveRulesList,
			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gb.setConstraints(rulesScroll, c);
        configPanel.add(rulesScroll);
    }

    /**
     * Make the buttons on the dialog box with localized strings and mnemonics.
     */
    private void makeButtons() {
        newPerspectiveButton = new JButton();
        nameButton(newPerspectiveButton, "button.new");
        removePerspectiveButton = new JButton();
        nameButton(removePerspectiveButton, "button.remove");
        duplicatePerspectiveButton = new JButton();
        nameButton(duplicatePerspectiveButton, "button.duplicate");
        moveUpButton = new JButton();
        nameButton(moveUpButton, "button.move-up");
        moveDownButton = new JButton();
        nameButton(moveDownButton, "button.move-down");
        
        addRuleButton = new JButton(">>");
        addRuleButton.setToolTipText(Translator.localize("button.add-rule"));
        removeRuleButton = new JButton("<<");
        removeRuleButton.setToolTipText(Translator.localize(
                "button.remove-rule"));
    }

    /**
     * Make the lists on the dialog box and fill them.
     */
    private void makeLists() {
        perspectiveListModel = new DefaultListModel();
        perspectiveList = new JList(perspectiveListModel);
        perspectiveRulesListModel = new DefaultListModel();
        perspectiveRulesList = new JList(perspectiveRulesListModel);
        ruleLibraryListModel = new DefaultListModel();
        ruleLibraryList = new JList(ruleLibraryListModel);
        
        perspectiveList.setBorder(BorderFactory.createEmptyBorder(
                INSET_PX, INSET_PX, INSET_PX, INSET_PX));
        perspectiveRulesList.setBorder(BorderFactory.createEmptyBorder(
                INSET_PX, INSET_PX, INSET_PX, INSET_PX));
        ruleLibraryList.setBorder(BorderFactory.createEmptyBorder(
                INSET_PX, INSET_PX, INSET_PX, INSET_PX));
        
        perspectiveList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        perspectiveRulesList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        ruleLibraryList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Updates the perspectives in the explorer,
     * saves the user perspectives and exits.
     */
    class OkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
            PerspectiveManager.getInstance().removeAllPerspectives();
            
            for (int i = 0; i < perspectiveListModel.getSize(); i++) {
                Object elem = perspectiveListModel.getElementAt(i);
                PerspectiveManager.getInstance().addPerspective(elem);
            }
            
            PerspectiveManager.getInstance().saveUserPerspectives();
        }
    }
    
    class NewPerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object[] msgArgs = {
                new Integer((perspectiveList.getModel().getSize() + 1)) };
	    ExplorerPerspective newPers =
		new ExplorerPerspective(Translator.messageFormat(
                    "dialog.perspective.explorer-perspective", msgArgs));
	    perspectiveListModel.insertElementAt(newPers, 0);
	    perspectiveList.setSelectedValue(newPers, true);
	    perspectiveRulesListModel.clear();
        }
    }
    
    class RemovePerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object sel = perspectiveList.getSelectedValue();
            if (perspectiveListModel.getSize() > 1) {
                perspectiveListModel.removeElement(sel);
            }
            perspectiveList.setSelectedIndex(0);
            if (perspectiveListModel.getSize() == 1) {
                removePerspectiveButton.setEnabled(false);
	    }
        }
    }
    
    class DuplicatePerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object sel = perspectiveList.getSelectedValue();
            if (sel != null) {
                Object[] msgArgs = {sel.toString() };
                ExplorerPerspective newPers = 
                    ((ExplorerPerspective) sel).makeNamedClone(Translator
                        .messageFormat("dialog.perspective.copy-of", msgArgs));
                perspectiveListModel.insertElementAt(newPers, 0);
                perspectiveList.setSelectedValue(newPers, true);
            }
        }
    }
    
    class RuleListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
            Object src = e.getSource();
            if (perspectiveList.getSelectedValue() == null) return;
            
            if (src == addRuleButton) doAddRule();
            else if (src == removeRuleButton) doRemoveRule();
            //            else if (src == _ruleLibList) doAddRule();
            //            else if (src == _rulesList) doRemoveRule();
        }
    }
    
    class RuleListMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent me) {
            Object src = me.getSource();
            if (me.getClickCount() != 2
		|| perspectiveList.getSelectedValue() == null) {
		return;
	    }
            
            if (src == ruleLibraryList && addRuleButton.isEnabled()) {
                doAddRule();
	    }
            if (src == perspectiveRulesList && removeRuleButton.isEnabled()) {
                doRemoveRule();
	    }
        }
    }
    
    private void doAddRule() {
        Object sel = ruleLibraryList.getSelectedValue();
        try {
            String ruleName = sel.getClass().getName();
            PerspectiveRule newRule =  
                (PerspectiveRule) Class.forName(ruleName).newInstance();
            
            perspectiveRulesListModel.insertElementAt(newRule, 0);
            ((ExplorerPerspective) perspectiveList
                .getSelectedValue()).addRule(newRule);
        } catch (Exception e) {
            LOG.error("problem adding rule");
        }
    }
    
    private void doRemoveRule() {
        PerspectiveRule sel =
	    (PerspectiveRule) perspectiveRulesList.getSelectedValue();
	Object selPers = perspectiveList.getSelectedValue();
        
        perspectiveRulesListModel.removeElement(sel);
        ((ExplorerPerspective) selPers).removeRule(sel);
        
        perspectiveRulesList.setSelectedIndex(0);
    }
    
    class MoveUpListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int sel = perspectiveList.getSelectedIndex();
            if (sel > 0) {
                Object selObj = perspectiveListModel.get(sel);
                Object prevObj = perspectiveListModel.get(sel - 1);
                perspectiveListModel.set(sel, prevObj);
                perspectiveListModel.set(sel - 1, selObj);
                perspectiveList.setSelectedIndex(sel - 1);
            }
        }
    }
    
    class MoveDownListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int sel = perspectiveList.getSelectedIndex();
            if (sel < (perspectiveListModel.getSize() - 1)) {
                Object selObj = perspectiveListModel.get(sel);
                Object nextObj = perspectiveListModel.get(sel + 1);
                perspectiveListModel.set(sel, nextObj);
                perspectiveListModel.set(sel + 1, selObj);
                perspectiveList.setSelectedIndex(sel + 1);
            }
        }
    }
    
    class PerspectiveListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = perspectiveList.getSelectedValue();
            Object selRule = ruleLibraryList.getSelectedValue();
            removePerspectiveButton.setEnabled(selPers != null);
            duplicatePerspectiveButton.setEnabled(selPers != null);
            moveUpButton.setEnabled(perspectiveList.getSelectedIndex() > 0);
            moveDownButton.setEnabled((selPers != null) 
                    && (perspectiveList.getSelectedIndex() 
                            < (perspectiveList.getModel().getSize() - 1)));
            
            if (selPers == null) return;
            
            ExplorerPerspective pers = (ExplorerPerspective) selPers;
            perspectiveRulesListModel.clear();
            
            for (int i = 0; i < pers.getRulesArray().length; i++) {
                perspectiveRulesListModel.insertElementAt(
                                pers.getRulesArray()[i], 0);
            }
            addRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
    
    class RulesListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = perspectiveList.getSelectedValue();
            Object selRule = perspectiveRulesList.getSelectedValue();
            removeRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
    
    class LibraryListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = perspectiveList.getSelectedValue();
            Object selRule = ruleLibraryList.getSelectedValue();
            addRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
}
