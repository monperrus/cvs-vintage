// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products may
// be obtained by contacting the University of California. David F. Redmiles
// Department of Information and Computer Science (ICS) University of
// California Irvine, California 92697-3425 Phone: 714-824-3823. This software
// program and documentation are copyrighted by The Regents of the University
// of California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.

// File: FigText.java
// Classes: FigText
// Original Author: ics125 spring 1996
// $Id: FigText.java,v 1.8 1998/06/03 00:27:26 jrobbins Exp $

package uci.gef;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;

import uci.ui.*;
import uci.util.*;

/** This class handles painting and editing text Fig's in a
 *  LayerDiagram. Needs-More-Work: should eventually allow styled text
 *  editing, ... someday... */

public class FigText extends Fig implements KeyListener, MouseListener {

  ////////////////////////////////////////////////////////////////
  // constants

  /** Constants to specify text justification. */
  public final int JUSTIFY_LEFT = 0;
  public final int JUSTIFY_RIGHT = 1;
  public final int JUSTIFY_CENTER = 2;

  /** Minimum size of a FigText object. */
  public final int MIN_TEXT_WIDTH = 30;

  ////////////////////////////////////////////////////////////////
  // instance variables

  /** Font info. */
  protected Font _font = new Font("TimesRoman", Font.PLAIN, 10);
  protected transient FontMetrics _fm;
  protected int _lineHeight;

  /** Color of the actual text characters. */
  protected Color _textColor = Color.black;

  /** Color to be drawn behind the actual text characters. Note that
   *  this will be a smaller area than the bounding box which is
   *  filled with FillColor. */
  protected Color _textFillColor = Color.white;

  /** True if the area behind individual characters is to be filled
   *  with TextColor. */
  protected boolean _textFilled = false;

  /** True if the text should be underlined. needs-more-work. */
  protected boolean _underline = false;

  /** True if more than one line of text is allow. If false, newline
   *  characters will be ignored. True by default. */
  protected boolean _multiLine = true;

  /** Spacing between lines. Default is -4 pixels. */
  protected int _lineSpacing = -4;

  /** Internal margins between the text and the edge of the rectangle. */
  protected int _topMargin = 1;
  protected int _botMargin = 1;
  protected int _leftMargin = 1;
  protected int _rightMargin = 1;

  /** True if the FigText can only grow in size, never shrink. */
  protected boolean _expandOnly = false;

  /** Text justification can be JUSTIFY_LEFT, JUSTIFY_RIGHT, or JUSTIFY_CENTER. */
  protected int _justification;

  /** The current string to display. */
  protected String _curText;

  ////////////////////////////////////////////////////////////////
  // static initializer

  /** This puts the text properties on the "Text" and "Style" pages of
   * the uci.ui.TabPropFrame. */
  static {
    PropCategoryManager.categorizeProperty("Text", "font");
    PropCategoryManager.categorizeProperty("Text", "underline");
    PropCategoryManager.categorizeProperty("Text", "expandOnly");
    PropCategoryManager.categorizeProperty("Text", "lineSpacing");
    PropCategoryManager.categorizeProperty("Text", "topMargin");
    PropCategoryManager.categorizeProperty("Text", "botMargin");
    PropCategoryManager.categorizeProperty("Text", "leftMargin");
    PropCategoryManager.categorizeProperty("Text", "rightMargin");
    PropCategoryManager.categorizeProperty("Text", "text");
    PropCategoryManager.categorizeProperty("Style", "justification");
    PropCategoryManager.categorizeProperty("Style", "textFilled");
    PropCategoryManager.categorizeProperty("Style", "textFillColor");
    PropCategoryManager.categorizeProperty("Style", "textColor");
  }

  ////////////////////////////////////////////////////////////////
  // constructors

  /** Construct a new FigText with the given position, size, color,
   *  string, font, and font size. Text string is initially empty and
   *  centered. */
  public FigText(int x, int y, int w, int h,
		 Color textColor, String familyName, int fontSize) {
    super(x, y, w, h);
    _x = x; _y = y; _w = w; _h = h;
    _textColor = textColor;
    _font = new Font(familyName, Font.PLAIN, fontSize);
    _justification = JUSTIFY_CENTER;
    _curText = "";
  }

  /** Construct a new FigText with the given position, size, and attributes. */
  public FigText(int x, int y, int w, int h ) {
    this(x, y, w, h, Color.blue, "TimesRoman", 10);
  }

  ////////////////////////////////////////////////////////////////
  // invariant

  /** Check the class invariant to make sure that this FigText is in a
   *  valid state.  Useful for debugging. */
  public boolean OK() {
    if (!super.OK()) return false;
    return _font != null && _lineSpacing > -20 && _topMargin >= 0 &&
      _botMargin >= 0 && _leftMargin >= 0 && _rightMargin >= 0 &&
      (_justification == JUSTIFY_LEFT || _justification == JUSTIFY_CENTER ||
       _justification == JUSTIFY_RIGHT) && _textColor != null &&
      _textFillColor != null;
  }

  ////////////////////////////////////////////////////////////////
  // accessors

  /** Reply a string that indicates how the text is justified: Left,
   *  Center, or Right. */
  public String getJustificationByName() {
    if (_justification == JUSTIFY_LEFT) return "Left";
    else if (_justification == JUSTIFY_CENTER) return "Center";
    else if (_justification == JUSTIFY_RIGHT) return "Right";
    System.out.println("internal error, unknown text alignment");
    return "Unknown";
  }

  /** Set the text justification given one of these strings: Left,
   *  Center, or Right. */
  public void setJustifciaionByName(String justifyString) {
    if (justifyString.equals("Left")) _justification = JUSTIFY_LEFT;
    else if (justifyString.equals("Center")) _justification = JUSTIFY_CENTER;
    else if (justifyString.equals("Right")) _justification = JUSTIFY_RIGHT;
    _fm = null;
  }



  ////////////////////////////////////////////////////////////////
  // accessors and modifiers

  public Color getTextColor() { return _textColor; }
  public void setTextColor(Color c) {
    firePropChange("textColor", _textColor, c);
    _textColor = c;
  }
  
  public Color getTextFillColor() { return _textFillColor; }
  public void setTextFillColor(Color c) {
    firePropChange("textFillColor", _textFillColor, c);
    _textFillColor = c;
  }

  public boolean getTextFilled() { return _textFilled; }
  public void setTextFilled(boolean b) {
    firePropChange("textFilled", _textFilled, b);
    _textFilled = b;
  }

  public boolean getUnderline() { return _underline; }
  public void setUnderline(boolean b) {
    firePropChange("underline", _underline, b);
    _underline = b;
  }

  public String getJustification() { return getJustificationByName(); }
  public void setJustification(String align) {
    firePropChange("justifciaion", getJustificationByName(), align);
    setJustifciaionByName(align);
  }

  public int getLineSpacing() { return _lineSpacing; }
  public void setLineSpacing(int s) {
    firePropChange("lineSpacing", _lineSpacing, s);
    _lineSpacing = s;
    calcBounds();
  }

  public int getTopMargin() { return _topMargin; }
  public void setTopMargin(int m) {
    firePropChange("topMargin", _topMargin, m);
    _topMargin = m;
    calcBounds();
  }

  public int getBotMargin() { return _botMargin; }
  public void setBotMargin(int m) {
    firePropChange("botMargin", _botMargin, m);
    _botMargin = m;
    calcBounds();
  }

  public int getLeftMargin() { return _leftMargin; }
  public void setLeftMargin(int m) {
    firePropChange("leftMargin", _leftMargin, m);
    _leftMargin = m;
    calcBounds();
  }

  public int getRightMargin() { return _rightMargin; }
  public void setRightMargin(int m) {
    firePropChange("rightMargin", _rightMargin, m);
    _rightMargin = m;
    calcBounds();
  }

  public boolean getExpandOnly() { return _expandOnly; }
  public void setExpandOnly(boolean b) {
    firePropChange("expandOnly", _expandOnly, b);
    _expandOnly = b;
  }

  public Font getFont() { return _font; }
  public void setFont(Font f) {
    firePropChange("font", _font, f);
    _font = f;
    _fm = null;
    calcBounds();
  }

  public boolean getItalic() { return _font.isItalic(); }
  public void setItalic(boolean b) {
    int style = (getBold() ? Font.BOLD : 0) + (b ? Font.ITALIC : 0);
    Font f = new Font(_font.getFamily(), style, _font.getSize()); 
    setFont(f);
  }

  public boolean getBold() { return _font.isBold(); }
  public void setBold(boolean b) {
    int style = (b ? Font.BOLD : 0) + (getItalic() ? Font.ITALIC : 0);
    setFont(new Font(_font.getFamily(), style, _font.getSize()));
  }

  public void setMultiLine(boolean b) { _multiLine = b; }
  public boolean getMultiLine() { return _multiLine; }
  
  /** Remove the last char from the current string line and return the
   *  new string.  Called whenever the user hits the backspace key.
   *  Needs-More-Work: Very slow.  This will eventually be replaced by
   *  full text editing... if there are any volunteers to do that...*/
  public String deleteLastCharFromString(String s) {
    int len = Math.max(s.length() - 1, 0);
    char[] chars = s.toCharArray();
    return new String(chars, 0, len);
  }

  /** Delete the last char from the current string. Called whenever
   *  the user hits the backspace key */
  public void deleteLastChar() {
    _curText = deleteLastCharFromString(_curText);
    calcBounds();
  }

  /** Append a character to the current String .*/
  public void append(char c) { setText(_curText + c); }

  /** Append the given String to the current String. */
  public void append(String s) { setText(_curText + s); }

  /** set the current String to be the given String. */
  public void setText(String s) { _curText = s; calcBounds(); }

  /** Get the String held by this FigText. Multi-line text is
   *  represented by newline characters embedded in the String. */
  public String getText() { return _curText; }

  ////////////////////////////////////////////////////////////////
  // painting methods

  /** Paint the FigText. */
  public void paint(Graphics g) {
    int chunkX = _x + _leftMargin;
    int chunkY = _y + _topMargin;
    StringTokenizer lines;

    if (_filled) {
      g.setColor(_fillColor);
      g.fillRect(_x, _y, _w, _h);
    }
    if (_lineWidth > 0) {
      g.setColor(_lineColor);
      g.drawRect(_x - 1, _y - 1, _w + 1, _h + 1);
    }

    if (_font != null) g.setFont(_font);
    _fm = g.getFontMetrics(_font);
    int chunkH = _lineHeight + _lineSpacing;
    chunkY = _y + _topMargin + chunkH;
    if (_textFilled) {
      g.setColor(_textFillColor);
      lines = new StringTokenizer(_curText, "\n\r", true);
      while (lines.hasMoreTokens()) {
	String curLine = lines.nextToken();
	//if (curLine.equals("\r")) continue;
	int chunkW = _fm.stringWidth(curLine);
	switch (_justification) {
	case JUSTIFY_LEFT: break;
	case JUSTIFY_CENTER: chunkX = _x + (_w - chunkW) / 2; break;
	case JUSTIFY_RIGHT: chunkX = _x + _w - chunkW - _rightMargin; break;
	}
	if (curLine.equals("\n") || curLine.equals("\r")) chunkY += chunkH;
	else g.fillRect(chunkX, chunkY - chunkH, chunkW, chunkH);
      }
    }

    g.setColor(_textColor);
    chunkX = _x + _leftMargin;
    chunkY = _y + _topMargin + _lineHeight + _lineSpacing;
    lines = new StringTokenizer(_curText, "\n\r", true);
    while (lines.hasMoreTokens()) {
      String curLine = lines.nextToken();
      //if (curLine.equals("\r")) continue;
      int chunkW = _fm.stringWidth(curLine);
      switch (_justification) {
      case JUSTIFY_LEFT: break;
      case JUSTIFY_CENTER: chunkX = _x + ( _w - chunkW ) / 2; break;
      case JUSTIFY_RIGHT: chunkX = _x + _w  - chunkW; break;
      }
      if (curLine.equals("\n")  || curLine.equals("\r")) chunkY += chunkH;
      else g.drawString(curLine, chunkX, chunkY);
    }
  }



  ////////////////////////////////////////////////////////////////
  // event handlers: KeyListener implemtation

  /** When the user presses a key when a FigText is selected, that key
   *  should be added to the current string, or if the key was
   *  backspace, the last character is removed.  Needs-More-Work: Should
   *  also catch arrow keys and mouse clicks for full text
   *  editing... someday... */
  public void keyTyped(KeyEvent ke) {
    int mods = ke.getModifiers();
    if (mods != 0 && mods != KeyEvent.SHIFT_MASK) return;
    char c = ke.getKeyChar();
    if (!Character.isISOControl(c)) {
      startTrans();
      append(c);
      endTrans();
      ke.consume();
    }
  }

  /** This method handles backspace and enter. */
  public void keyPressed(KeyEvent ke) {
    if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      startTrans();
      deleteLastChar();
      endTrans();
      ke.consume();
    }
    else if (ke.getKeyCode() == KeyEvent.VK_F2) {
      startTextEditor();
      ke.consume();
    }
    else if (ke.getKeyCode() == KeyEvent.VK_ENTER && _multiLine) {
      startTrans();
      append('\n');
      endTrans();
      ke.consume();
    }
  }

  /** Not used, does nothing. */
  public void keyReleased(KeyEvent ke) { }

  
  ////////////////////////////////////////////////////////////////
  // event handlers: KeyListener implemtation

  public void mouseClicked(MouseEvent me) {
    if (me.getClickCount() >= 2) startTextEditor();
  }
  
  public void mousePressed(MouseEvent me) { }
  
  public void mouseReleased(MouseEvent me) { }
  
  public void mouseEntered(MouseEvent me) { }
  
  public void mouseExited(MouseEvent me) { }
  
  public void startTextEditor() {
    FigTextEditor ta = new FigTextEditor(this);
  }


  ////////////////////////////////////////////////////////////////
  // internal utility functions

  /** Compute the overall width and height of the FigText object based
   *  on the font, font size, and current text. Needs-More-Work: Right
   *  now text objects can get larger when you type more, but they
   *  do not get smaller when you backspace.  */
  protected void calcBounds() {
    if (_font == null) return;
    if (_fm == null) _fm = Toolkit.getDefaultToolkit().getFontMetrics(_font);
    int overallW = 0;
    int numLines = 1;
    StringTokenizer lines = new StringTokenizer(_curText, "\n\r", true);
    while (lines.hasMoreTokens()) {
      String curLine = lines.nextToken();
      int chunkW = _fm.stringWidth(curLine);
      if (curLine.equals("\n") || curLine.equals("\r")) numLines++;
      else overallW = Math.max(chunkW, overallW);
    }
    _lineHeight = _fm.getHeight();
    int maxDescent = _fm.getMaxDescent();
    int overallH = (_lineHeight + _lineSpacing) * numLines +
      _topMargin + _botMargin + maxDescent;
    overallW = Math.max(MIN_TEXT_WIDTH, overallW + _leftMargin + _rightMargin);
    switch (_justification) {
    case JUSTIFY_LEFT: break;
    case JUSTIFY_CENTER: if (_w < overallW) _x -= (overallW - _w) / 2; break;
    case JUSTIFY_RIGHT: if (_w < overallW) _x -= (overallW - _w); break;
    }
    _w = _expandOnly ? Math.max(_w, overallW) : overallW;
    _h = _expandOnly ? Math.max(_h, overallH) : overallH;
  }

} /* end class FigText */


////////////////////////////////////////////////////////////////


// needs-more-work: could this be a singleton?

class FigTextEditor extends JTextArea
implements PropertyChangeListener, FocusListener, DocumentListener, KeyListener {

  FigText _target;
  JPanel drawingPanel;
  
  public static int EXTRA = 10;
  
  public FigTextEditor(FigText ft) {
    _target = ft;
    Editor ce = Globals.curEditor();
    if (!(ce.getAwtComponent() instanceof JComponent)) {
      System.out.println("not a JComponent");
      return;
    }
    drawingPanel = (JPanel) ce.getAwtComponent();
    _target.firePropChange("editing", false, true);
    _target.addPropertyChangeListener(this);
    // walk up and add to glass pane
    Component awtComp = drawingPanel;
    while (!(awtComp instanceof JFrame) && awtComp != null) {
      awtComp = awtComp.getParent();
    }
    if (!(awtComp instanceof JFrame)) { System.out.println("no JFrame"); return; }
    JPanel glass = (JPanel) ((JFrame)awtComp).getGlassPane();
    ft.calcBounds();
    Rectangle bbox = ft.getBounds();
    bbox = SwingUtilities.convertRectangle(drawingPanel, bbox, glass);
    setBounds(bbox.x - EXTRA, bbox.y - EXTRA,
	      bbox.width + EXTRA*2, bbox.height + EXTRA*2 );
    glass.setVisible(true);
    glass.setLayout(null);
    glass.add(this);
    String text = ft.getText();
    if (!text.endsWith("\n")) setText(text + "\n");
    setText(text);
    setFont(ft.getFont());
    addFocusListener(this);
    addKeyListener(this);
    requestFocus();
    getDocument().addDocumentListener(this);
  }

  public void propertyChange(PropertyChangeEvent pve) { updateFigText(); }

  public void focusLost(FocusEvent fe) {
    System.out.println("FigTextEditor lostFocus");
    endEditing();
  }

  public void focusGained(FocusEvent e) {
    System.out.println("focusGained");
  }


  public void endEditing() {
    _target.startTrans();
    updateFigText();
    _target.endTrans();
    hide();
    Container parent = getParent();
    if (parent!= null) parent.remove(this);
    _target.removePropertyChangeListener(this);
    _target.firePropChange("editing", true, false);
    //drawingPanel.requestFocus();
    removeFocusListener(this);
    removeKeyListener(this);
  }
  
  ////////////////////////////////////////////////////////////////
  // event handlers for KeyListener implementaion

  
  public void keyTyped(KeyEvent ke) {
    if (ke.getKeyChar() == KeyEvent.VK_ENTER &&
	 !_target.getMultiLine()) {
      ke.consume();
    }
    //else super.keyTyped(ke);
  }

  public void keyReleased(KeyEvent ke) {
  }

  public void keyPressed(KeyEvent ke) {
    if (ke.getKeyCode() == KeyEvent.VK_F2) {
      endEditing();
      ke.consume();
    }
    else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
      endEditing();
      ke.consume();
    }
    //else super.keyPressed(ke);
  }


  ////////////////////////////////////////////////////////////////
  // event handlers for DocumentListener implementaion

  public void insertUpdate(DocumentEvent e) { updateFigText(); }
  
  public void removeUpdate(DocumentEvent e) { updateFigText(); }
  
  public void changedUpdate(DocumentEvent e) { updateFigText(); }


  ////////////////////////////////////////////////////////////////
  // internal utility methods

  protected void updateFigText() {
    if (_target == null) return;
    String text = getText();
    //_target.startTrans();
    _target.setText(text);
    //_target.endTrans();
    Component awtComp = drawingPanel;
    while (!(awtComp instanceof JFrame) && awtComp != null) {
      awtComp = awtComp.getParent();
    }
    if (!(awtComp instanceof JFrame)) { System.out.println("no JFrame"); return; }
    JPanel glass = (JPanel) ((JFrame)awtComp).getGlassPane();
    _target.calcBounds();
    Rectangle bbox = _target.getBounds();
    bbox = SwingUtilities.convertRectangle(drawingPanel, bbox, glass);
    setBounds(bbox.x - EXTRA, bbox.y - EXTRA,
	      bbox.width + EXTRA*2, bbox.height + EXTRA*2 );
    setFont(_target.getFont());
  }
  
} /* end class FigTextEditor */
