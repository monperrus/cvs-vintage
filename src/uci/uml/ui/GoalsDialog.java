
package uci.uml.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;
// import com.sun.java.swing.text.*;
// import com.sun.java.swing.border.*;

import uci.util.*;
import uci.ui.*;
import uci.gef.*;
import uci.argo.kernel.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Model_Management.*;


public class GoalsDialog extends JDialog
implements ActionListener, ChangeListener {

  ////////////////////////////////////////////////////////////////
  // instance variables
  protected JPanel  _mainPanel = new JPanel();
  protected JButton _okButton = new JButton("OK");
  protected Hashtable _slidersToDecisions = new Hashtable();
  protected Hashtable _slidersToDigits = new Hashtable();

  ////////////////////////////////////////////////////////////////
  // constructors

  public GoalsDialog(Frame parent) {
    super(parent, "Design Goals", false);
    setSize(300, 450);
    Container content = getContentPane();
    content.setLayout(new BorderLayout());
    initMainPanel();
    JScrollPane scroll =
      new JScrollPane(_mainPanel,
		      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    content.add(scroll, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JPanel buttonInner = new JPanel(new GridLayout(1, 1));
    buttonInner.add(_okButton);
    buttonPanel.add(buttonInner);

    content.add(buttonPanel, BorderLayout.SOUTH);
    _okButton.addActionListener(this);

    getRootPane().setDefaultButton(_okButton);
  }


  public void initMainPanel() {
    GoalModel gm = Designer.TheDesigner.getGoalModel();
    Vector goals = gm.getGoals();

    GridBagLayout gb = new GridBagLayout();
    _mainPanel.setLayout(gb);

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.ipadx = 3; c.ipady = 3;


    
//     c.gridy = 0;
//     c.gridx = 0;
//     JLabel priLabel = new JLabel("Priority:");
//     gb.setConstraints(priLabel, c);
//     _mainPanel.add(priLabel);

//     c.gridy = 0;
//     c.gridx = 1;
//     JLabel offLabel = new JLabel("Off");
//     gb.setConstraints(offLabel, c);
//     _mainPanel.add(offLabel);

//     c.gridy = 0;
//     c.gridx = 2;
//     JLabel lowLabel = new JLabel("Low");
//     gb.setConstraints(lowLabel, c);
//     _mainPanel.add(lowLabel);

//     c.gridy = 0;
//     c.gridx = 3;
//     JLabel twoLabel = new JLabel("ad");
//     gb.setConstraints(twoLabel, c);
//     _mainPanel.add(twoLabel);

//     c.gridy = 0;
//     c.gridx = 4;
//     JLabel threeLabel = new JLabel("asd");
//     gb.setConstraints(threeLabel, c);
//     _mainPanel.add(threeLabel);

//     c.gridy = 0;
//     c.gridx = 5;
//     JLabel fourLabel = new JLabel("asd");
//     gb.setConstraints(fourLabel, c);
//     _mainPanel.add(fourLabel);

//     c.gridy = 0;
//     c.gridx = 6;
//     JLabel highLabel = new JLabel("High");
//     gb.setConstraints(highLabel, c);
//     _mainPanel.add(highLabel);


    c.gridy = 1;
    java.util.Enumeration enum = goals.elements();
    while (enum.hasMoreElements()) {
      Goal d = (Goal) enum.nextElement();
      JLabel decLabel = new JLabel(d.getName());
      JLabel valueLabel = new JLabel("   " + d.getPriority());
      JSlider decSlide = new JSlider(SwingConstants.HORIZONTAL,
				     0, 5, d.getPriority());
      decSlide.setPaintTicks(true);
      decSlide.setPaintLabels(true);
      decSlide.addChangeListener(this);
      Dimension origSize = decSlide.getPreferredSize();
      Dimension smallSize = new Dimension(origSize.width/2, origSize.height);
      decSlide.setSize(smallSize);
      decSlide.setPreferredSize(smallSize);

      _slidersToDecisions.put(decSlide, d);
      _slidersToDigits.put(decSlide, valueLabel);

      c.gridx = 0;
      c.gridwidth = 1;
      c.weightx = 0.0;
      c.ipadx = 3;
      gb.setConstraints(decLabel, c);
      _mainPanel.add(decLabel);

      c.gridx = 1;
      c.gridwidth = 1;
      c.weightx = 0.0;
      c.ipadx = 0;
      gb.setConstraints(valueLabel, c);
      _mainPanel.add(valueLabel);

      c.gridx = 2;
      c.gridwidth = 6;
      c.weightx = 1.0;
      gb.setConstraints(decSlide, c);
      _mainPanel.add(decSlide);

      c.gridy++;
    }
  }
  
  ////////////////////////////////////////////////////////////////
  // event handlers
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == _okButton) {
      setVisible(false);
      dispose();
    }
  }


  public void stateChanged(ChangeEvent ce) {
    JSlider srcSlider = (JSlider) ce.getSource();
    Goal d = (Goal) _slidersToDecisions.get(srcSlider);
    JLabel valLab = (JLabel) _slidersToDigits.get(srcSlider);
    int pri = srcSlider.getValue();
    d.setPriority(pri);
    if (pri == 0) valLab.setText(" off");
    else valLab.setText("   " + pri);
  }
  
} /* end class DesignIssuesDialog */



////////////////////////////////////////////////////////////////


