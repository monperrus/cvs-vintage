/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

/*
 * Created on May 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.help.*;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.helpers.LogLog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Encapsulates the full Toolbar, and menus and all the actions that can be performed from it.
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
class ChainsawToolBarAndMenus implements ChangeListener, SettingsListener {
  private static final String SETTING_RESPONSIVENESS = "Responsiveness";
  private static final String SETTING_TAB_PLACEMENT = "tab.placement";
  private final SmallToggleButton showReceiversButton;
  final JTextField findTextField;
  private final Action changeModelAction;
  private final Action clearAction;
  private final Action closeAction;
  private final Action findNextAction;
  private final Action lockToolbarAction;
  private final Action pauseAction;
  private final Action showPreferencesAction;
  private final Action showReceiversAction;
  private final Action toggleLogTreeAction;
  private final Action toggleDetailPaneAction;
  private final Action toggleToolbarAction;
  private final Action undockAction;
  private final Collection lookAndFeelMenus = new ArrayList();
  private final JCheckBoxMenuItem toggleShowReceiversCheck =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleDetailMenuItem =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleCyclicMenuItem =
    new JCheckBoxMenuItem();  
  private final FileMenu fileMenu;
  private final JCheckBoxMenuItem toggleStatusBarCheck =
    new JCheckBoxMenuItem();
  private final JMenu viewMenu = new JMenu("View");
  private final JMenuBar menuBar;
  private final JCheckBoxMenuItem menuItemClose = new JCheckBoxMenuItem();
  private final JRadioButtonMenuItem levelDisplayIcon =
    new JRadioButtonMenuItem("Icon");
  private final JRadioButtonMenuItem levelDisplayText =
    new JRadioButtonMenuItem("Text");
  private final JRadioButtonMenuItem tabsBottom =
    new JRadioButtonMenuItem("Bottom");
  private final JRadioButtonMenuItem tabsTop = new JRadioButtonMenuItem("Top");
  private final JSlider responsiveSlider;
  private final JToolBar toolbar;
  private LogUI logui;
  private final SmallButton clearButton = new SmallButton();
  private final SmallToggleButton detailPaneButton = new SmallToggleButton();
  private final SmallToggleButton logTreePaneButton = new SmallToggleButton();
  private final SmallToggleButton pauseButton = new SmallToggleButton();
  private final     SmallToggleButton toggleCyclicButton = new SmallToggleButton();

  private String lastFind = "";
  private String levelDisplay = ChainsawConstants.LEVEL_DISPLAY_ICONS;
  private final Action[] logPanelSpecificActions;
  private final ChangeListener panelListener;
  private Map panelMenuMap = new HashMap();
  private Map panelEnabledMap = new HashMap();
  private JMenuItem showTabs;

  ChainsawToolBarAndMenus(final LogUI logui) {
    this.logui = logui;
    toolbar = new JToolBar(JToolBar.HORIZONTAL);
    menuBar = new JMenuBar();
    fileMenu = new FileMenu(logui);
    closeAction = createCloseHelpAction();
    changeModelAction = createChangeModelAction();
    findTextField = createFindField();
    findNextAction = setupFindFieldsAndActions();
    showPreferencesAction = createShowPreferencesAction();
    lockToolbarAction = createLockableToolbarAction();
    toggleToolbarAction = createToggleToolbarAction();
    toggleLogTreeAction = createToggleLogTreeAction();
    pauseAction = createPauseAction();
    clearAction = createClearAction();
    undockAction = createUndockAction();
    showReceiversAction = createShowReceiversAction();
    showReceiversButton = new SmallToggleButton(showReceiversAction);

    toggleDetailPaneAction = createToggleDetailPaneAction();
    responsiveSlider =
      new JSlider(JSlider.VERTICAL, 0, 5000, logui.handler.getQueueInterval());
    createMenuBar();
    createToolbar();

    panelListener =
      new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            Map m = logui.getPanels();

            if (m != null) {
              Set s = m.entrySet();
              Iterator iter = s.iterator();

              while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

                if (!panelMenuMap.keySet().contains(entry.getKey())) {
                  panelMenuMap.put(
                    entry.getKey(),
                    getDisplayPanelMenuItem(entry.getKey().toString()));

                  //default to enabled
                  panelEnabledMap.put(entry.getKey(), Boolean.TRUE);
                  showTabs.add(
                    (JCheckBoxMenuItem) panelMenuMap.get(entry.getKey()));
                }

                boolean entryEnabled =
                  ((Boolean) panelEnabledMap.get(entry.getKey())).booleanValue();
                boolean newEnabled =
                  ((Boolean) entry.getValue()).booleanValue();

                if (entryEnabled != newEnabled) {
                  ((JCheckBoxMenuItem) panelMenuMap.get(entry.getKey())).getModel()
                   .setEnabled(newEnabled);
                  panelEnabledMap.put(
                    entry.getKey(), Boolean.valueOf(newEnabled));
                }
              }
            }
          }
        };

    logPanelSpecificActions =
      new Action[] {
        pauseAction, findNextAction, clearAction, fileMenu.getFileSaveAction(),
        toggleDetailPaneAction, showPreferencesAction, undockAction,
        toggleLogTreeAction, changeModelAction,
      };
  }

  /**
   * @return
   */
  private Action createChangeModelAction() {
    Action action = new AbstractAction("Use Cyclic", new ImageIcon(ChainsawIcons.REFRESH)){

      public void actionPerformed(ActionEvent arg0) {
        LogPanel logPanel = logui.getCurrentLogPanel();
        logPanel.toggleCyclic();      
        scanState();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Changes between Cyclic and Unlimited mode.");
    return action;    
  }

  /**
  * @return
  */
  private Action createToggleLogTreeAction() {
    Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          if (logui.getCurrentLogPanel() != null) {
            logui.getCurrentLogPanel().toggleLogTreePanel();
          }
        }
      };

    action.putValue(Action.NAME, "Logger Tree");
    action.putValue(Action.SHORT_DESCRIPTION, "Toggles the Log Tree panel");
    action.putValue("enabled", Boolean.TRUE);
    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));

    //		TODO find an icon
    return action;
  }

  /**
     * DOCUMENT ME!
     *
     * @param event DOCUMENT ME!
     */
  public void loadSettings(LoadSettingsEvent event) {
    try {
      levelDisplay = event.getSetting(ChainsawConstants.LEVEL_DISPLAY);

      if (levelDisplay.equals(ChainsawConstants.LEVEL_DISPLAY_ICONS)) {
        levelDisplayIcon.setSelected(true);
      } else {
        levelDisplayText.setSelected(true);
      }

      final int responsiveness =
        event.asInt(ChainsawToolBarAndMenus.SETTING_RESPONSIVENESS);
      final int tabPlacement =
        event.asInt(ChainsawToolBarAndMenus.SETTING_TAB_PLACEMENT);

      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            responsiveSlider.setValue(responsiveness);
            logui.getTabbedPane().setTabPlacement(tabPlacement);
            scanState();
          }
        });
    } catch (NullPointerException e) {
      LogLog.error("error decoding setting", e);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param event DOCUMENT ME!
   */
  public void saveSettings(SaveSettingsEvent event) {
    event.saveSetting(
      ChainsawToolBarAndMenus.SETTING_RESPONSIVENESS,
      responsiveSlider.getValue());

    event.saveSetting(
      ChainsawToolBarAndMenus.SETTING_TAB_PLACEMENT,
      logui.getTabbedPane().getTabPlacement());

    event.saveSetting(ChainsawConstants.LEVEL_DISPLAY, levelDisplay);
  }

  /**
   * DOCUMENT ME!
   */
  public void stateChange() {
    scanState();
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void stateChanged(ChangeEvent e) {
    scanState();
  }

  JMenuBar getMenubar() {
    return menuBar;
  }

  JToolBar getToolbar() {
    return toolbar;
  }

  private Action createClearAction() {
    final Action action =
      new AbstractAction("Clear") {
        public void actionPerformed(ActionEvent e) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          logPanel.clearModel();
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK));
    action.putValue(
      Action.SHORT_DESCRIPTION, "Removes all the events from the current view");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DELETE));

    return action;
  }

  private Action createCloseHelpAction() {
    final Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          closeAction.putValue(Action.NAME, "Welcome tab");
          logui.removeWelcomePanel();

          if (menuItemClose.isSelected()) {
            logui.addWelcomePanel();
          } else {
          }
        }
      };

    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));

    //    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
    action.putValue(Action.SHORT_DESCRIPTION, "Toggles the Welcome tab");
    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    action.putValue(Action.NAME, "Welcome tab");

    return action;
  }

  private void createFindDocListener(final JTextField field) {
    field.getDocument().addDocumentListener(
      new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          find(false);
        }

        public void removeUpdate(DocumentEvent e) {
          find(false);
        }

        public void changedUpdate(DocumentEvent e) {
          find(false);
        }
      });
  }

  static JTextField createFindField() {
    JTextField tf = new JTextField();
    Dimension fixedSize = new Dimension(132, 24);
    tf.setPreferredSize(fixedSize);
    tf.setMaximumSize(fixedSize);
    tf.setMinimumSize(fixedSize);
    tf.setToolTipText("type in a simple string to find events");

    return tf;
  }

  private Action createLockableToolbarAction() {
    final Action lockToolbarAction =
      new AbstractAction("Lock Toolbar") {
        private boolean lock = true;

        public void actionPerformed(ActionEvent e) {
          lock = !lock;

          final boolean isLocked = lock;
          Runnable runnable = null;
          runnable =
            new Runnable() {
                public void run() {
                  toolbar.setFloatable(!isLocked);
                  toolbar.repaint();
                }
              };
          SwingUtilities.invokeLater(runnable);
        }
      };

    return lockToolbarAction;
  }

  private void createMenuBar() {
    JMenu activeTabMenu = new JMenu("Current tab");

    JMenuItem menuItemUseRightMouse =
      new JMenuItem(
        "Other options available via panel's right mouse button popup menu");
    menuItemUseRightMouse.setEnabled(false);

    viewMenu.setMnemonic('V');

    JCheckBoxMenuItem lockToolbarCheck =
      new JCheckBoxMenuItem(lockToolbarAction);
    lockToolbarCheck.setSelected(true);

    JCheckBoxMenuItem showToolbarCheck =
      new JCheckBoxMenuItem(toggleToolbarAction);
    showToolbarCheck.setSelected(true);

    menuItemClose.setAction(closeAction);

    JCheckBoxMenuItem pause = new JCheckBoxMenuItem(pauseAction);
    JMenuItem menuPrefs = new JMenuItem(showPreferencesAction);
    menuPrefs.setText(
      showPreferencesAction.getValue(Action.SHORT_DESCRIPTION).toString());

    JMenuItem menuUndock = new JMenuItem(undockAction);

    showTabs = new JMenu("Display tabs");

    toggleDetailMenuItem.setAction(toggleDetailPaneAction);
    toggleDetailMenuItem.setSelected(true);
    
    toggleCyclicMenuItem.setAction(changeModelAction);
    
    toggleCyclicMenuItem.setSelected(true);
    
    JCheckBoxMenuItem toggleLogTreeMenuItem =
      new JCheckBoxMenuItem(toggleLogTreeAction);
    toggleLogTreeMenuItem.setSelected(true);

    final Action toggleStatusBarAction =
      new AbstractAction("Show Status bar") {
        public void actionPerformed(ActionEvent arg0) {
          if (toggleStatusBarCheck.isSelected()) {
            logui.addStatusBar();
          } else {
            logui.removeStatusBar();
          }
        }
      };

    toggleStatusBarAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
    toggleStatusBarCheck.setAction(toggleStatusBarAction);
    toggleStatusBarCheck.setSelected(true);

    
    activeTabMenu.add(pause);
    activeTabMenu.add(toggleCyclicMenuItem);
    activeTabMenu.addSeparator();
    activeTabMenu.add(toggleDetailMenuItem);
    activeTabMenu.add(toggleLogTreeMenuItem);
    activeTabMenu.addSeparator();
    activeTabMenu.add(menuUndock);
    activeTabMenu.add(menuPrefs);

    activeTabMenu.addSeparator();
    activeTabMenu.add(new JMenuItem(clearAction));
    activeTabMenu.addSeparator();
    activeTabMenu.add(menuItemUseRightMouse);

    viewMenu.add(showToolbarCheck);
    viewMenu.add(toggleStatusBarCheck);
    viewMenu.add(toggleShowReceiversCheck);
    viewMenu.add(menuItemClose);
    viewMenu.addSeparator();

    ButtonGroup levelIconGroup = new ButtonGroup();
    JMenu levelIconMenu = new JMenu("Display Level column as");
    levelIconMenu.setMnemonic('l');

    levelIconGroup.add(levelDisplayIcon);
    levelIconGroup.add(levelDisplayText);

    levelDisplayIcon.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          levelDisplay = ChainsawConstants.LEVEL_DISPLAY_ICONS;
          SettingsManager.getInstance().saveSettings();
          SettingsManager.getInstance().loadSettings();
        }
      });

    levelDisplayText.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          levelDisplay = ChainsawConstants.LEVEL_DISPLAY_TEXT;
          SettingsManager.getInstance().saveSettings();
          SettingsManager.getInstance().loadSettings();
        }
      });

    ButtonGroup tabPlacementGroup = new ButtonGroup();
    JMenu tabMenu = new JMenu("Tabs");
    tabMenu.setMnemonic('a');

    tabPlacementGroup.add(tabsTop);
    tabPlacementGroup.add(tabsBottom);

    tabsTop.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logui.getTabbedPane().setTabPlacement(JTabbedPane.TOP);
        }
      });

    tabsBottom.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logui.getTabbedPane().setTabPlacement(JTabbedPane.BOTTOM);
        }
      });

    JMenu responsiveNess =
      new JMenu(ChainsawToolBarAndMenus.SETTING_RESPONSIVENESS);
    responsiveNess.setMnemonic('R');

    responsiveNess.add(createResponsivenessSlider());

    final JMenu lookAndFeelMenu = new JMenu("Look & Feel");
    lookAndFeelMenu.setMnemonic('L');

    UIManager.LookAndFeelInfo[] lookAndFeels =
      UIManager.getInstalledLookAndFeels();

    final ButtonGroup lookAndFeelGroup = new ButtonGroup();

    for (int i = 0; i < lookAndFeels.length; i++) {
      final UIManager.LookAndFeelInfo lfInfo = lookAndFeels[i];
      final JRadioButtonMenuItem lfItemMenu =
        new JRadioButtonMenuItem(lfInfo.getName());
      lfItemMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
              new Runnable() {
                public void run() {
                  logui.setLookAndFeel(lfInfo.getClassName());
                }
              });
          }
        });
        
 
      lookAndFeelGroup.add(lfItemMenu);
      lookAndFeelMenu.add(lfItemMenu);
      lookAndFeelMenus.add(lfItemMenu);
    }
    
	try {
	   final Class gtkLF = Class.forName("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	   final JRadioButtonMenuItem lfIGTK =
		 new JRadioButtonMenuItem("GTK+ 2.0");
		lfIGTK.addActionListener(
		 new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
			 SwingUtilities.invokeLater(
			   new Runnable() {
				 public void run() {
				   logui.setLookAndFeel(gtkLF.getName());
				 }
			   });
		   }
		 });
		lookAndFeelGroup.add(lfIGTK);
		lookAndFeelMenu.add(lfIGTK);
		lookAndFeelMenus.add(lfIGTK);

   } catch (Exception e) {
	   LogLog.debug("Can't find new GTK L&F, might be Windows, or <JDK1.4.2");
   }

    levelIconMenu.add(levelDisplayIcon);
    levelIconMenu.add(levelDisplayText);

    if (levelDisplay.equals(ChainsawConstants.LEVEL_DISPLAY_ICONS)) {
      levelDisplayIcon.setSelected(true);
    } else {
      levelDisplayText.setSelected(true);
    }

    tabMenu.add(tabsTop);
    tabMenu.add(tabsBottom);

    viewMenu.add(showTabs);

    viewMenu.add(levelIconMenu);
    viewMenu.add(tabMenu);
    viewMenu.add(responsiveNess);
    viewMenu.add(lookAndFeelMenu);

    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');

    JMenuItem about = new JMenuItem("About Chainsaw v2...");
    about.setMnemonic('A');
    about.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logui.showAboutBox();
        }
      });

    
    Action startTutorial = new AbstractAction("Start tutorial...", new ImageIcon(ChainsawIcons.HELP)){

		public void actionPerformed(ActionEvent e) {
				new Thread(new Tutorial()).start();	
		}};
		
	startTutorial.putValue(Action.SHORT_DESCRIPTION, "Starts some pretend Receivers that generate random events for use during the Tutorial");
	helpMenu.add(startTutorial);
	helpMenu.addSeparator();
    helpMenu.add(about);
    
    menuBar.add(fileMenu);
    menuBar.add(viewMenu);
    menuBar.add(activeTabMenu);
    menuBar.add(helpMenu);
  }

  private Action createPauseAction() {
    final Action pauseAction =
      new AbstractAction("Pause") {
        public void actionPerformed(ActionEvent evt) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          String ident = logPanel.getIdentifier();
          logPanel.setPaused(!logPanel.isPaused());
          scanState();
        }
      };

    pauseAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
    pauseAction.putValue(
      Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F12"));
    pauseAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Causes incoming events for this tab to be discarded");
    pauseAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.PAUSE));

    return pauseAction;
  }

  private JComponent createResponsivenessSlider() {
    JPanel responsiveNessPanel = new JPanel();
    BoxLayout layout = new BoxLayout(responsiveNessPanel, BoxLayout.Y_AXIS);

    responsiveNessPanel.setLayout(layout);

    responsiveSlider.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    responsiveSlider.setAlignmentX(JComponent.CENTER_ALIGNMENT);

    responsiveSlider.setMinorTickSpacing(250);
    responsiveSlider.setMajorTickSpacing(1000);
    responsiveSlider.setToolTipText(
      "Adjust to set the responsiveness of the app.  How often the view is updated.");
    responsiveSlider.setSnapToTicks(true);
    responsiveSlider.setPaintTicks(true);

    responsiveSlider.setPaintLabels(true);
    responsiveSlider.setPaintTrack(true);
    responsiveSlider.setInverted(true);
    responsiveSlider.getModel().addChangeListener(
      new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (responsiveSlider.getValueIsAdjusting()) {
            /**
             * We'll wait until it stops.
             */
          } else {
            int value = responsiveSlider.getValue();

            if (value == 0) {
              value = 100;
            }

            System.out.println("Adjust responsiveness to " + value + "ms");
            logui.handler.setQueueInterval(value);
          }
        }
      });

    JLabel l1 = new JLabel("Update frequently (100ms)");
    JLabel l2 = new JLabel("Update infrequently (5 seconds)");
    responsiveNessPanel.add(l1);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.add(responsiveSlider);
    responsiveNessPanel.add(panel);
    responsiveNessPanel.add(l2);

    return responsiveNessPanel;
  }

  private Action createShowPreferencesAction() {
    Action showPreferences =
      new AbstractAction("", ChainsawIcons.ICON_PREFERENCES) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel != null) {
            logPanel.showPreferences();
          }
        }
      };

    showPreferences.putValue(
      Action.SHORT_DESCRIPTION, "Define display and color filters...");

    // TODO think of good mnemonics and HotKey for this action
    return showPreferences;
  }

  /**
   * @return
   */
  private Action createShowReceiversAction() {
    final Action action =
      new AbstractAction("Show Receivers") {
        public void actionPerformed(ActionEvent arg0) {
          logui.toggleReceiversPanel();
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Shows the currently configured Log4j Receivers");
    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F6"));

    toggleShowReceiversCheck.setAction(action);

    return action;
  }

  private Action createToggleDetailPaneAction() {
    Action action =
      new AbstractAction("Show Detail Pane") {
        boolean enabled = true;

        public void actionPerformed(ActionEvent evt) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          logPanel.toggleDetailPanel();
          scanState();
        }
      };

    action.putValue("enabled", Boolean.TRUE);
    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK));
    action.putValue(Action.SHORT_DESCRIPTION, "Hides/Shows the Detail Pane");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.INFO));

    return action;
  }

  private Action createToggleToolbarAction() {
    /**
     * -== Begin of Show/Hide toolbar action
     */
    final Action toggleToolbarAction =
      new AbstractAction("Show Toolbar") {
        private boolean hide = false;

        public void actionPerformed(ActionEvent e) {
          hide = !hide;

          Runnable runnable = null;

          if (hide) {
            runnable =
              new Runnable() {
                  public void run() {
                    logui.getContentPane().remove(toolbar);
                    logui.getRootPane().repaint();
                    logui.getRootPane().revalidate();
                  }
                };
          } else {
            runnable =
              new Runnable() {
                  public void run() {
                    logui.getContentPane().add(toolbar, BorderLayout.NORTH);
                    logui.getRootPane().repaint();
                    logui.getRootPane().revalidate();
                  }
                };
          }

          SwingUtilities.invokeLater(runnable);
        }
      };

    toggleToolbarAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));

    return toggleToolbarAction;
  }

  private void createToolbar() {
    Insets buttonMargins = new Insets(1, 1, 1, 1);

    FileMenu fileMenu = (FileMenu) menuBar.getMenu(0);

    JButton fileOpenButton =
      new SmallButton(fileMenu.getLog4JFileOpenAction());
    fileOpenButton.setMargin(buttonMargins);

    JButton fileSaveButton = new SmallButton(fileMenu.getFileSaveAction());
    fileSaveButton.setMargin(buttonMargins);

    fileOpenButton.setText("");
    fileSaveButton.setText("");

    toolbar.add(fileOpenButton);
    toolbar.add(fileSaveButton);
    toolbar.addSeparator();

    pauseButton.setAction(pauseAction);
    pauseButton.setText("");

    //		pauseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F12"),pauseAction.getValue(Action.NAME) );
    pauseButton.getActionMap().put(
      pauseAction.getValue(Action.NAME), pauseAction);

    toggleCyclicButton.setAction(changeModelAction);
    toggleCyclicButton.setText(null);
    
    detailPaneButton.setAction(toggleDetailPaneAction);
    detailPaneButton.setText(null);
    detailPaneButton.getActionMap().put(
      toggleDetailPaneAction.getValue(Action.NAME), toggleDetailPaneAction);
    detailPaneButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK),
      toggleDetailPaneAction.getValue(Action.NAME));

    logTreePaneButton.setAction(toggleLogTreeAction);

    //	logTreePaneButton.setText(null);
    logTreePaneButton.getActionMap().put(
      toggleLogTreeAction.getValue(Action.NAME), toggleLogTreeAction);
    logTreePaneButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK),
      toggleDetailPaneAction.getValue(Action.NAME));

    SmallButton prefsButton = new SmallButton(showPreferencesAction);
    SmallButton undockButton = new SmallButton(undockAction);
    undockButton.setText("");

    toolbar.add(undockButton);
    toolbar.add(pauseButton);
    toolbar.add(toggleCyclicButton);
    toolbar.addSeparator();
    toolbar.add(detailPaneButton);
    toolbar.add(logTreePaneButton);
    toolbar.add(prefsButton);
    toolbar.addSeparator();

    toolbar.add(clearButton);
    clearButton.setAction(clearAction);
    clearButton.setText("");
    toolbar.addSeparator();

    JButton findNextButton = new SmallButton(findNextAction);
    findNextButton.setText("");
    findNextButton.getActionMap().put(
      findNextAction.getValue(Action.NAME), findNextAction);
    findNextButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      (KeyStroke) findNextAction.getValue(Action.ACCELERATOR_KEY),
      findNextAction.getValue(Action.NAME));

    toolbar.add(findNextButton);

    Box findBox = Box.createHorizontalBox();
    findBox.add(findTextField);
    toolbar.add(findBox);

    toolbar.addSeparator();

    toolbar.add(showReceiversButton);

    toolbar.add(Box.createHorizontalGlue());

    toolbar.setMargin(buttonMargins);
    toolbar.setFloatable(false);
  }

  private Action createUndockAction() {
    Action action =
      new AbstractAction("Undock", ChainsawIcons.ICON_UNDOCK) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel != null) {
            logPanel.undock();
          }
        }
      };

    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Undocks the current Log panel into it's own window");

    //	TODO think of some mnemonics and HotKeys for this action
    return action;
  }

  private void find(boolean next) {
    LogPanel logPanel = logui.getCurrentLogPanel();

    if (logPanel != null) {
      if (next) {
        logPanel.find(findTextField.getText());
      } else {
        logPanel.find(findTextField.getText());
      }
    }
  }

  private void scanState() {
    switch (logui.getTabbedPane().getTabPlacement()) {
    case SwingConstants.TOP:
      tabsTop.setSelected(true);

      break;

    case SwingConstants.BOTTOM:
      tabsBottom.setSelected(true);

      break;
    }

    toggleStatusBarCheck.setSelected(logui.isStatusBarVisible());
    toggleShowReceiversCheck.setSelected(logui.isReceiverPanelVisible());
    logTreePaneButton.setSelected(logui.isLogTreePanelVisible());
    showReceiversButton.setSelected(logui.isReceiverPanelVisible());
    menuItemClose.setSelected(logui.getTabbedPane().containsWelcomePanel());

    /**
     * We get the currently selected LogPanel, and if null, deactivate some
     * actions
     */
    LogPanel logPanel = logui.getCurrentLogPanel();

    boolean activateLogPanelActions = true;

    if (logPanel == null) {
      activateLogPanelActions = false;
      logui.getStatusBar().clear();
      findTextField.setEnabled(false);

      closeAction.setEnabled(true);
    } else {
      fileMenu.getFileSaveAction().setEnabled(true);
      findTextField.setEnabled(true);

      pauseButton.getModel().setSelected(logPanel.isPaused());
      toggleCyclicButton.setSelected(logPanel.getModel().isCyclic());
      logui.getStatusBar().setPaused(logPanel.isPaused());
      toggleDetailMenuItem.setSelected(logPanel.isDetailPaneVisible());
      toggleCyclicMenuItem.setSelected(logPanel.getModel().isCyclic());
      detailPaneButton.getModel().setSelected(logPanel.isDetailPaneVisible());
    }

    for (int i = 0; i < logPanelSpecificActions.length; i++) {
      logPanelSpecificActions[i].setEnabled(activateLogPanelActions);
    }

    String currentLookAndFeel =
      UIManager.getLookAndFeel().getClass().getName();
    String currentLookAndFeelName = UIManager.getLookAndFeel().getName();

    for (Iterator iter = lookAndFeelMenus.iterator(); iter.hasNext();) {
      JRadioButtonMenuItem element = (JRadioButtonMenuItem) iter.next();

      if (element.getText().equals(currentLookAndFeelName)) {
        element.setSelected(true);
      } else {
        element.setSelected(false);
      }
    }
  }

  ChangeListener getPanelListener() {
    return panelListener;
  }

  private JCheckBoxMenuItem getDisplayPanelMenuItem(final String panelName) {
    final JCheckBoxMenuItem item = new JCheckBoxMenuItem(panelName, true);

    final Action action =
      new AbstractAction(panelName) {
        public void actionPerformed(ActionEvent e) {
          logui.displayPanel(panelName, item.isSelected());
        }
      };

    item.setAction(action);

    return item;
  }

  private Action setupFindFieldsAndActions() {
    createFindDocListener(findTextField);

    final Action action =
      new AbstractAction("Find Next") {
        public void actionPerformed(ActionEvent e) {
          find(true);
        }
      };

    //    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F3"));
    action.putValue(
      Action.SHORT_DESCRIPTION, "Finds the next occurrence of the Find string");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.FIND));

    return action;
  }
}
