// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.core.util;

import javax.swing.SwingUtilities;

import org.columba.core.command.TaskManager;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker
{
	protected Object value; // see getValue(), setValue()
	protected Thread thread;
	protected TaskManager taskManager;
	protected boolean cancel;


	/**
	 * Class to maintain reference to current worker thread
	 * under separate synchronization control.
	 */
	public static class ThreadVar
	{
		private Thread thread;
		ThreadVar(Thread t)
		{
			thread = t;
		}
		synchronized Thread get()
		{
			return new Thread(thread);
		}
		synchronized void clear()
		{
			thread = null;
		}
	}

	protected ThreadVar threadVar;

	/**
	 * Get the value produced by the worker thread, or null if it
	 * hasn't been constructed yet.
	 */
	protected synchronized Object getValue()
	{
		return value;
	}

	/**
	 * Set the value produced by worker thread
	 */
	private synchronized void setValue(Object x)
	{
		value = x;
	}


	public Thread getThread()
	{
		return threadVar.get();
	}

	public ThreadVar getThreadVar()
	{
		return threadVar;
	}

	public boolean getCancel()
	{
		return cancel;
	}

	public void setCancel(boolean b)
	{
		cancel = b;
	}

	protected boolean isCanceled() {
		return cancel;	
	}


	/**
	 * Compute the value to be returned by the <code>get</code> method.
	 */
	public abstract Object construct();
	

	/**
	 * Called on the event dispatching thread (not on the worker thread)
	 * after the <code>construct</code> method has returned.
	 */
	public void finished()
	{
	}

	/**
	 * A new method that interrupts the worker thread.  Call this method
	 * to force the worker to stop what it's doing.
	 */
	public void interrupt()
	{
		Thread t = threadVar.get();
		if (t != null)
		{
			t.interrupt();
		}
		threadVar.clear();
	}

	/**
	 * Return the value created by the <code>construct</code> method.
	 * Returns null if either the constructing thread or the current
	 * thread was interrupted before a value was produced.
	 *
	 * @return the value created by the <code>construct</code> method
	 */
	public Object get()
	{
		while (true)
		{
			Thread t = threadVar.get();
			if (t == null)
			{
				return getValue();
			}
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt(); // propagate
				return null;
			}
		}
	}

	/**
	 * Start a thread that will call the <code>construct</code> method
	 * and then exit.
	 */
	public SwingWorker()
	{
		cancel = false;

		final Runnable doFinished = new Runnable()
		{
			public void run()
			{
				finished();
			}
		};

		Runnable doConstruct = new Runnable()
		{

			public void run()
			{
				try
				{
					setValue(construct());
				}
				finally
				{
					//threadVar;
				}

				SwingUtilities.invokeLater(doFinished);
			}
		};

		Thread t = new Thread(doConstruct);
		threadVar = new ThreadVar(t);
	}

	/**
	 * Start the worker thread.
	 */
	public Thread start()
	{
		thread = threadVar.get();
		if (thread != null)
		{
			thread.start();
			return thread;
		}
		return null;
	}

}