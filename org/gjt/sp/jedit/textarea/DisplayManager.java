/*
 * DisplayManager.java - Low-level text display
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
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

package org.gjt.sp.jedit.textarea;

import java.awt.Toolkit;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;

/**
 * Manages low-level text display tasks.
 * @since jEdit 4.2pre1
 * @author Slava Pestov
 * @version $Id: DisplayManager.java,v 1.2 2003/03/22 23:20:30 spestov Exp $
 */
public class DisplayManager
{
	//{{{ DisplayManager constructor
	DisplayManager(Buffer buffer, JEditTextArea textArea)
	{
		this.buffer = buffer;
		this.offsetMgr = buffer._getOffsetManager();
		this.textArea = textArea;
		this.index = buffer._displayLock();

		int physicalLines = offsetMgr.getLineCount();
		int scrollLines = 0;
		for(int i = 0; i < physicalLines; i++)
		{
			if(offsetMgr.isLineVisible(i,index))
				scrollLines += getScreenLineCount(i);
		}

		scrollLineCount = new OffsetManager.Anchor(physicalLines,scrollLines,index)
		{
			public void changed()
			{
				System.err.println("screen line count changed: "
					+ scrollLineCount.scrollLine);
			}
		};
		offsetMgr.addAnchor(scrollLineCount);
		bufferChangeHandler = new BufferChangeHandler();
		buffer.addBufferChangeListener(bufferChangeHandler);
	} //}}}

	//{{{ dispose() method
	void dispose()
	{
		offsetMgr.removeAnchor(scrollLineCount);
		buffer.removeBufferChangeListener(bufferChangeHandler);
		buffer._displayUnlock(index);
	} //}}}

	//{{{ isNarrowed() method
	/**
	 * Returns if the buffer has been narrowed.
	 * @since jEdit 4.2pre1
	 */
	public boolean isNarrowed()
	{
		return narrowed;
	} //}}}

	//{{{ isLineVisible() method
	/**
	 * Returns if the specified line is visible.
	 * @param line A physical line index
	 * @since jEdit 4.2pre1
	 */
	public final boolean isLineVisible(int line)
	{
		return offsetMgr.isLineVisible(line,index);
	} //}}}

	//{{{ getFirstVisibleLine() method
	/**
	 * Returns the physical line number of the first visible line.
	 * @since jEdit 4.2pre1
	 */
	public int getFirstVisibleLine()
	{
		try
		{
			buffer.readLock();

			for(int i = 0; i < buffer.getLineCount(); i++)
			{
				if(isLineVisible(i))
					return i;
			}
		}
		finally
		{
			buffer.readUnlock();
		}

		// can't happen?
		return -1;
	} //}}}

	//{{{ getLastVisibleLine() method
	/**
	 * Returns the physical line number of the last visible line.
	 * @since jEdit 4.2pre1
	 */
	public int getLastVisibleLine()
	{
		try
		{
			buffer.readLock();

			for(int i = buffer.getLineCount() - 1; i >= 0; i--)
			{
				if(isLineVisible(i))
					return i;
			}
		}
		finally
		{
			buffer.readUnlock();
		}

		// can't happen?
		return -1;
	} //}}}

	//{{{ getNextVisibleLine() method
	/**
	 * Returns the next visible line after the specified line index.
	 * @param line A physical line index
	 * @since jEdit 4.0pre1
	 */
	public int getNextVisibleLine(int line)
	{
		if(line < 0 || line >= offsetMgr.getLineCount())
			throw new ArrayIndexOutOfBoundsException(line);

		try
		{
			buffer.readLock();

			if(line == buffer.getLineCount() - 1)
				return -1;

			for(int i = line + 1; i < buffer.getLineCount(); i++)
			{
				if(isLineVisible(i))
					return i;
			}
			return -1;
		}
		finally
		{
			buffer.readUnlock();
		}
	} //}}}

	//{{{ getPrevVisibleLine() method
	/**
	 * Returns the previous visible line before the specified line index.
	 * @param line A physical line index
	 * @since jEdit 4.0pre1
	 */
	public int getPrevVisibleLine(int line)
	{
		if(line < 0 || line >= offsetMgr.getLineCount())
			throw new ArrayIndexOutOfBoundsException(line);

		try
		{
			buffer.readLock();

			if(line == 0)
				return -1;

			for(int i = line - 1; i >= 0; i--)
			{
				if(isLineVisible(i))
					return i;
			}
			return -1;
		}
		finally
		{
			buffer.readUnlock();
		}
	} //}}}

	//{{{ getScreenLineCount() method
	public final int getScreenLineCount(int line)
	{
		if(offsetMgr.isScreenLineCountValid(line))
			return offsetMgr.getScreenLineCount(line);
		else
		{
			// this is an optimization but the less preferred
			// path. its better when the chunk cache records
			// screen line count changes.
			int newCount;
			if(textArea.softWrap)
				newCount = textArea.chunkCache.getLineInfosForPhysicalLine(line).length;
			else
				newCount = 1;
			offsetMgr.setScreenLineCount(line,newCount);
			return newCount;
		}
	} //}}}

	//{{{ getScrollLineCount() method
	public final int getScrollLineCount()
	{
		return scrollLineCount.scrollLine;
	} //}}}

	//{{{ collapseFold() method
	/**
	 * Collapses the fold at the specified physical line index.
	 * @param line A physical line index
	 * @since jEdit 4.2pre1
	 */
	public void collapseFold(int line)
	{
		int lineCount = buffer.getLineCount();
		int start = 0;
		int end = lineCount - 1;

		try
		{
			buffer.writeLock();

			// if the caret is on a collapsed fold, collapse the
			// parent fold
			if(line != 0
				&& line != buffer.getLineCount() - 1
				&& buffer.isFoldStart(line)
				&& !isLineVisible(line + 1))
			{
				line--;
			}

			int initialFoldLevel = buffer.getFoldLevel(line);

			//{{{ Find fold start and end...
			if(line != lineCount - 1
				&& buffer.getFoldLevel(line + 1) > initialFoldLevel)
			{
				// this line is the start of a fold
				start = line + 1;

				for(int i = line + 1; i < lineCount; i++)
				{
					if(buffer.getFoldLevel(i) <= initialFoldLevel)
					{
						end = i - 1;
						break;
					}
				}
			}
			else
			{
				boolean ok = false;

				// scan backwards looking for the start
				for(int i = line - 1; i >= 0; i--)
				{
					if(buffer.getFoldLevel(i) < initialFoldLevel)
					{
						start = i + 1;
						ok = true;
						break;
					}
				}

				if(!ok)
				{
					// no folds in buffer
					return;
				}

				for(int i = line + 1; i < lineCount; i++)
				{
					if(buffer.getFoldLevel(i) < initialFoldLevel)
					{
						end = i - 1;
						break;
					}
				}
			} //}}}

			//{{{ Collapse the fold...
			for(int i = start; i <= end; i++)
			{
				if(isLineVisible(i))
					setLineVisible(i,false);
			}

			//}}}
		}
		finally
		{
			buffer.writeUnlock();
		}
	} //}}}

	//{{{ expandFold() method
	/**
	 * Expands the fold at the specified physical line index.
	 * @param line A physical line index
	 * @param fully If true, all subfolds will also be expanded
	 * @since jEdit 4.2pre1
	 */
	public int expandFold(int line, boolean fully)
	{
		// the first sub-fold. used by JEditTextArea.expandFold().
		int returnValue = -1;

		int lineCount = buffer.getLineCount();
		int start = 0;
		int end = lineCount - 1;

		try
		{
			buffer.writeLock();

			int initialFoldLevel = buffer.getFoldLevel(line);

			//{{{ Find fold start and fold end...
			if(line != lineCount - 1
				&& isLineVisible(line)
				&& !isLineVisible(line + 1)
				&& buffer.getFoldLevel(line + 1) > initialFoldLevel)
			{
				// this line is the start of a fold
				start = line + 1;

				for(int i = line + 1; i < lineCount; i++)
				{
					if(/* isLineVisible(i) && */
						buffer.getFoldLevel(i) <= initialFoldLevel)
					{
						end = i - 1;
						break;
					}
				}
			}
			else
			{
				boolean ok = false;

				// scan backwards looking for the start
				for(int i = line - 1; i >= 0; i--)
				{
					if(isLineVisible(i) && buffer.getFoldLevel(i) < initialFoldLevel)
					{
						start = i + 1;
						ok = true;
						break;
					}
				}

				if(!ok)
				{
					// no folds in buffer
					return -1;
				}

				for(int i = line + 1; i < lineCount; i++)
				{
					if((isLineVisible(i) &&
						buffer.getFoldLevel(i) < initialFoldLevel)
						|| i == getLastVisibleLine())
					{
						end = i - 1;
						break;
					}
				}
			} //}}}

			//{{{ Expand the fold...

			// we need a different value of initialFoldLevel here!
			initialFoldLevel = buffer.getFoldLevel(start);

			for(int i = start; i <= end; i++)
			{
				buffer.getFoldLevel(i);
			}

			for(int i = start; i <= end; i++)
			{
				if(buffer.getFoldLevel(i) > initialFoldLevel)
				{
					if(returnValue == -1
						&& i != 0
						&& buffer.isFoldStart(i - 1))
					{
						returnValue = i - 1;
					}

					if(fully)
						setLineVisible(i,true);
				}
				else
					setLineVisible(i,true);
			}
			//}}}

			if(!fully && !isLineVisible(line))
			{
				// this is a hack, and really needs to be done better.
				expandFold(line,false);
				return returnValue;
			}
		}
		finally
		{
			buffer.writeUnlock();
		}

		return returnValue;
	} //}}}

	//{{{ expandAllFolds() method
	/**
	 * Expands all folds.
	 * @since jEdit 4.2pre1
	 */
	public void expandAllFolds()
	{
		try
		{
			buffer.writeLock();

			narrowed = false;

			for(int i = 0; i < buffer.getLineCount(); i++)
				setLineVisible(i,true);
		}
		finally
		{
			buffer.writeUnlock();
		}
	} //}}}

	//{{{ expandFolds() method
	/**
	 * This method should only be called from <code>actions.xml</code>.
	 * @since jEdit 4.2pre1
	 */
	public void expandFolds(char digit)
	{
		if(digit < '1' || digit > '9')
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		else
			expandFolds((int)(digit - '1') + 1);
	} //}}}

	//{{{ expandFolds() method
	/**
	 * Expands all folds with the specified fold level.
	 * @param foldLevel The fold level
	 * @since jEdit 4.2pre1
	 */
	public void expandFolds(int foldLevel)
	{
		try
		{
			buffer.writeLock();

			narrowed = false;

			if(buffer.getFoldHandler() instanceof IndentFoldHandler)
				foldLevel = (foldLevel - 1) * buffer.getIndentSize() + 1;

			/* this ensures that the first line is always visible */
			boolean seenVisibleLine = false;

			for(int i = 0; i < buffer.getLineCount(); i++)
			{
				if(!seenVisibleLine || buffer.getFoldLevel(i) < foldLevel)
				{
					seenVisibleLine = true;
					setLineVisible(i,true);
				}
				else
					setLineVisible(i,false);
			}
		}
		finally
		{
			buffer.writeUnlock();
		}
	} //}}}

	//{{{ narrow() method
	/**
	 * Narrows the visible portion of the buffer to the specified
	 * line range.
	 * @param start The first line
	 * @param end The last line
	 * @since jEdit 4.2pre1
	 */
	public void narrow(int start, int end)
	{
		if(start > end || start < 0 || end >= buffer.getLineCount())
			throw new ArrayIndexOutOfBoundsException(start + ", " + end);

		if(start < getFirstVisibleLine() || end > getLastVisibleLine())
			expandAllFolds();
		// ideally, this should somehow be rolled into the below loop.
		else if(start != buffer.getLineCount() - 1
			&& !isLineVisible(start + 1))
			expandFold(start,false);

		for(int i = 0; i < start; i++)
		{
			setLineVisible(i,false);
		}

		for(int i = end + 1; i < buffer.getLineCount(); i++)
		{
			setLineVisible(i,false);
		}

		narrowed = true;

		// Hack... need a more direct way of obtaining a view?
		// JEditTextArea.getView() method?
		GUIUtilities.getView(textArea).getStatus().setMessageAndClear(
			jEdit.getProperty("view.status.narrow"));
	} //}}}

	//{{{ Package-private members

	//{{{ ScrollParameters class
	static class ScrollParameters
	{
		int physicalLine;
		int subregion;
		int scrollLine;
		int amount;
	} //}}}

	//{{{ scrollUp() method
	void scrollUp(ScrollParameters params)
	{
		while(params.amount > 0)
		{
			int screenLines = getScreenLineCount(params.physicalLine);
			if(params.amount < screenLines)
			{
				return;
			}
			else
			{
				int nextLine = getNextVisibleLine(params.physicalLine);
				if(nextLine == -1)
					return;
				params.physicalLine = nextLine;
				params.amount -= screenLines;
				params.scrollLine += screenLines;
			}
		}
	} //}}}

	//{{{ scrollDown() method
	void scrollDown(ScrollParameters params)
	{
		while(params.amount > 0)
		{
			int screenLines = getScreenLineCount(params.physicalLine);
			if(params.amount < screenLines)
			{
				return;
			}
			else
			{
				int prevLine = getPrevVisibleLine(params.physicalLine);
				if(prevLine == -1)
					return;
				params.physicalLine = prevLine;
				params.amount -= screenLines;
				params.scrollLine += screenLines;
			}
		}
	} //}}}

	//}}}

	//{{{ Private members
	private Buffer buffer;
	private OffsetManager offsetMgr;
	private JEditTextArea textArea;
	private int index;
	private boolean narrowed;
	private OffsetManager.Anchor scrollLineCount;
	private BufferChangeHandler bufferChangeHandler;

	//{{{ setLineVisible() method
	private final void setLineVisible(int line, boolean visible)
	{
		offsetMgr.setLineVisible(line,index,visible);
	} //}}}

	//}}}

	//{{{ BufferChangeHandler method
	class BufferChangeHandler extends BufferChangeAdapter
	{
		boolean queuedNotifyScreenLineChanges;

		public void contentInserted(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			for(int i = 0; i <= numLines; i++)
			{
				getScreenLineCount(startLine + i);
			}

			queuedNotifyScreenLineChanges = true;
			if(!buffer.isTransactionInProgress())
				transactionComplete(buffer);
		}

		public void contentRemoved(Buffer buffer, int startLine,
			int offset, int numLines, int length)
		{
			queuedNotifyScreenLineChanges = true;
			if(!buffer.isTransactionInProgress())
				transactionComplete(buffer);
		}

		public void transactionComplete(Buffer buffer)
		{
			offsetMgr.notifyScreenLineChanges();
		}
	} //}}}
}
