/*
 * ActionBar.java - For invoking actions directly
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit.gui;

//{{{ Imports
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
//}}}

/**
 * Action invocation bar.
 */
public class ActionBar extends JPanel
{
	//{{{ ActionBar constructor
	public ActionBar(final View view, boolean temp)
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

		actions = jEdit.getActions();
		Arrays.sort(actions,new MiscUtilities.StringICaseCompare());
		this.view = view;
		this.temp = temp;

		add(Box.createHorizontalStrut(2));

		JLabel label = new JLabel(jEdit.getProperty("view.action.prompt"));
		add(label);
		add(Box.createHorizontalStrut(12));
		add(action = new ActionTextField());
		Dimension max = action.getPreferredSize();
		max.width = Integer.MAX_VALUE;
		action.setMaximumSize(max);
		action.addActionListener(new ActionHandler());
		action.getDocument().addDocumentListener(new DocumentHandler());

		if(temp)
		{
			close = new RolloverButton(GUIUtilities.loadIcon("closebox.gif"));
			close.addActionListener(new ActionHandler());
			close.setToolTipText(jEdit.getProperty(
				"view.action.close-tooltip"));
			add(close);
		}

		//{{{ Create the timer used by the completion popup
		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				complete();
			}
		});
		timer.setRepeats(false);
		timer.setInitialDelay(300);
		//}}}

		// if 'temp' is true, hide search bar after user is done with it
		this.temp = temp;
	} //}}}

	//{{{ getField() method
	public HistoryTextField getField()
	{
		return action;
	} //}}}

	//{{{ goToActionBar() method
	public void goToActionBar()
	{
		repeatCount = view.getInputHandler().getRepeatCount();
		System.err.println("go to action bar " + repeatCount);
		action.setText(null);
		action.grabFocus();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private boolean temp;
	private int repeatCount;
	private HistoryTextField action;
	private CompletionPopup popup;
	private Timer timer;
	private RolloverButton close;
	private EditAction[] actions;
	//}}}

	//{{{ invoke() method
	private void invoke()
	{
		String cmd;
		if(popup != null)
			cmd = popup.list.getSelectedValue().toString();
		else
		{
			cmd = action.getText().trim();
			int index = cmd.indexOf('=');
			if(index != -1)
			{
				String propName = cmd.substring(0,index).trim();
				String propValue = cmd.substring(index + 1);
				String code;
				/* construct a BeanShell snippet instead of
				 * invoking directly so that user can record
				 * property changes in macros. */
				if(propName.startsWith("buffer."))
				{
					code = "buffer.setStringProperty(\""
						+ MiscUtilities.charsToEscapes(
						propName.substring("buffer.".length())
						) + "\",\""
						+ MiscUtilities.charsToEscapes(
						propValue) + "\");\n"
						+ "buffer.propertiesChanged();";
				}
				else if(propName.startsWith("!buffer."))
				{
					code = "jEdit.setProperty(\""
						+ MiscUtilities.charsToEscapes(
						propName.substring(1)) + "\",\""
						+ MiscUtilities.charsToEscapes(
						propValue) + "\");\n"
						+ "jEdit.propertiesChanged();";
				}
				else
				{
					code = "jEdit.setProperty(\""
						+ MiscUtilities.charsToEscapes(
						propName) + "\",\""
						+ MiscUtilities.charsToEscapes(
						propValue) + "\");\n"
						+ "jEdit.propertiesChanged();";
				}

				Macros.Recorder recorder = view.getMacroRecorder();
				if(recorder != null)
					recorder.record(code);
				BeanShell.eval(view,BeanShell.getNameSpace(),code);
				cmd = null;
			}
			else if(cmd.length() != 0)
			{
				EditAction[] completions = getCompletions(cmd);
				if(completions.length != 0)
				{
					cmd = completions[0].getName();
				}
			}
			else
				cmd = null;
		}

		if(popup != null)
		{
			popup.dispose();
			popup = null;
		}


		final EditAction act = (cmd == null ? null : jEdit.getAction(cmd));
		if(temp)
			view.removeToolBar(ActionBar.this);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				view.getTextArea().grabFocus();
				if(act == null)
				{
					view.getStatus().setMessageAndClear(
						jEdit.getProperty(
						"view.action.no-completions"));
				}
				else
				{
					view.getInputHandler().setRepeatCount(repeatCount);
					view.getInputHandler().invokeAction(act);
				}
			}
		});
	} //}}}

	//{{{ getCompletions() method
	private EditAction[] getCompletions(String str)
	{
		ArrayList returnValue = new ArrayList(actions.length);
		for(int i = 0; i < actions.length; i++)
		{
			if(actions[i].getName().indexOf(str) != -1)
				returnValue.add(actions[i]);
		}

		return (EditAction[])returnValue.toArray(new EditAction[returnValue.size()]);
	} //}}}

	//{{{ complete() method
	private void complete()
	{
		String text = action.getText().trim();
		if(text.length() != 0)
		{
			EditAction[] completions = getCompletions(text);
			if(completions.length == 1 && completions[0].getName().equals(text))
			{
				// do nothing
			}
			else if(completions.length != 0)
			{
				if(popup != null)
					popup.setModel(completions);
				else
					popup = new CompletionPopup(completions);
				return;
			}
		}

		if(popup != null)
		{
			popup.dispose();
			popup = null;
		}
	} //}}}

	//{{{ timerComplete() method
	private void timerComplete()
	{
		if(popup == null)
		{
			timer.stop();
			timer.start();
		}
		else
			complete();
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == close)
				view.removeToolBar(ActionBar.this);
			else
				invoke();
		}
	} //}}}

	//{{{ DocumentHandler class
	class DocumentHandler implements DocumentListener
	{
		//{{{ insertUpdate() method
		public void insertUpdate(DocumentEvent evt)
		{
			timerComplete();
		} //}}}

		//{{{ removeUpdate() method
		public void removeUpdate(DocumentEvent evt)
		{
			timerComplete();
		} //}}}

		//{{{ changedUpdate() method
		public void changedUpdate(DocumentEvent evt) {}
		//}}}
	} //}}}

	//{{{ ActionTextField class
	class ActionTextField extends HistoryTextField
	{
		boolean repeat;
		boolean nonDigit;

		ActionTextField()
		{
			super("action");
			setSelectAllOnFocus(true);
		}

		public void processKeyEvent(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;

			switch(evt.getID())
			{
			case KeyEvent.KEY_TYPED:
				char ch = evt.getKeyChar();
				if(!nonDigit && Character.isDigit(ch))
				{
					super.processKeyEvent(evt);
					repeat = true;
					timer.stop();
					repeatCount = Integer.parseInt(action.getText());
				}
				else
				{
					nonDigit = true;
					if(repeat)
						passToView(evt);
					else
						super.processKeyEvent(evt);
				}
				break;
			case KeyEvent.KEY_PRESSED:
				int keyCode = evt.getKeyCode();
				if(evt.isActionKey()
					|| evt.isControlDown()
					|| evt.isAltDown()
					|| evt.isMetaDown()
					|| keyCode == KeyEvent.VK_BACK_SPACE
					|| keyCode == KeyEvent.VK_ENTER
					|| keyCode == KeyEvent.VK_TAB
					|| keyCode == KeyEvent.VK_ESCAPE)
				{
					nonDigit = true;
					if(repeat)
					{
						passToView(evt);
						break;
					}
					else if(keyCode == KeyEvent.VK_ESCAPE)
					{
						evt.consume();
						if(temp)
							view.removeToolBar(ActionBar.this);
						if(popup != null)
						{
							popup.dispose();
							popup = null;
						}
						view.getEditPane().focusOnTextArea();
						break;
					}
					else if((keyCode == KeyEvent.VK_UP
						|| keyCode == KeyEvent.VK_DOWN)
						&& popup != null)
					{
						popup.list.processKeyEvent(evt);
						break;
					}
				}
				super.processKeyEvent(evt);
				break;
			}
		}

		private void passToView(final KeyEvent evt)
		{
			if(temp)
				view.removeToolBar(ActionBar.this);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					view.getTextArea().grabFocus();
					view.getInputHandler().setRepeatCount(repeatCount);
					view.getInputHandler().processKeyEvent(evt);
				}
			});
		}

		public void addNotify()
		{
			super.addNotify();
			repeat = nonDigit = false;
		}
	} //}}}

	//{{{ CompletionPopup class
	class CompletionPopup extends JWindow
	{
		CompletionList list;

		//{{{ CompletionPopup constructor
		CompletionPopup(EditAction[] actions)
		{
			super(view);

			setContentPane(new JPanel(new BorderLayout())
			{
				/**
				 * Returns if this component can be traversed by pressing the
				 * Tab key. This returns false.
				 */
				public boolean isManagingFocus()
				{
					return false;
				}

				/**
				 * Makes the tab key work in Java 1.4.
				 */
				public boolean getFocusTraversalKeysEnabled()
				{
					return false;
				}
			});

			list = new CompletionList(actions);
			list.setVisibleRowCount(8);
			list.addMouseListener(new MouseHandler());
			list.setSelectedIndex(0);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// stupid scrollbar policy is an attempt to work around
			// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
			JScrollPane scroller = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			getContentPane().add(scroller, BorderLayout.CENTER);

			GUIUtilities.requestFocus(this,list);

			pack();
			Point p = new Point(0,-getHeight());
			SwingUtilities.convertPointToScreen(p,action);
			setLocation(p);
			show();

			KeyHandler keyHandler = new KeyHandler();
			addKeyListener(keyHandler);
			list.addKeyListener(keyHandler);
		} //}}}

		//{{{ setModel() method
		void setModel(EditAction[] actions)
		{
			list.setListData(actions);
			list.setSelectedIndex(0);
		} //}}}

		//{{{ MouseHandler class
		class MouseHandler extends MouseAdapter
		{
			public void mouseClicked(MouseEvent evt)
			{
				invoke();
			}
		} //}}}

		//{{{ CompletionList class
		class CompletionList extends JList
		{
			CompletionList(Object[] data)
			{
				super(data);
			}

			// we need this public not protected
			public void processKeyEvent(KeyEvent evt)
			{
				super.processKeyEvent(evt);
			}
		} //}}}

		//{{{ KeyHandler class
		class KeyHandler extends KeyAdapter
		{
			public void keyTyped(KeyEvent evt)
			{
				action.processKeyEvent(evt);
			}

			public void keyPressed(KeyEvent evt)
			{
				int keyCode = evt.getKeyCode();
				if(keyCode == KeyEvent.VK_ESCAPE)
					action.processKeyEvent(evt);
				else if(keyCode == KeyEvent.VK_ENTER)
					invoke();
			}
		} //}}}
	} //}}}

	//}}}
}
