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

package org.columba.core.command;

import java.util.Vector;

import org.columba.core.logging.ColumbaLogger;
import org.columba.core.util.Mutex;
import org.columba.main.MainInterface;

public class DefaultProcessor extends Thread {

	private final static int MAX_WORKERS = 5;

	private Vector operationQueue;
	private Vector worker;

	private Mutex operationMutex;
	private Mutex workerMutex;

	private boolean isBusy;
	
	private UndoManager undoManager;
	
	private TaskManager taskManager;
	
	private GuiUpdater guiUpdater;
	
	private int timeStamp;

	public DefaultProcessor() {
		operationQueue = new Vector();
		guiUpdater = new GuiUpdater();

		worker = new Vector();

		for (int i = 0; i < MAX_WORKERS; i++) {
			worker.add(new Worker(this));
		}
		
		isBusy = true;
		
		operationMutex = new Mutex();
		workerMutex = new Mutex();

		taskManager = new TaskManager();
		
		undoManager = new UndoManager( this );
		timeStamp = 0;
		
	}

	public void addOp( Command op ) {
		addOp( op, Command.FIRST_EXECUTION );	
	}

	synchronized void addOp(Command op, int operationMode) {		

		ColumbaLogger.log.debug( "Adding Operation..." );
		
		operationMutex.getMutex();

		int p = operationQueue.size() - 1;
		OperationItem nextOp;

		// Sort in with respect to priority

		while (p != -1) {
			nextOp = (OperationItem) operationQueue.get(p);
			if ((nextOp.operation.getPriority() < op.getPriority()) && !nextOp.operation.isSynchronize())
				p--;
			else
				break;
		}

		operationQueue.insertElementAt(new OperationItem(op,operationMode), p + 1);

		operationMutex.releaseMutex();

		ColumbaLogger.log.debug( "Operation added" );
		
		notify();
	}

	private boolean canBeProcessed(OperationItem opItem) {
		return opItem.operation.canBeProcessed(opItem.operationMode);
	}

	private OperationItem getNextOpItem() {
		OperationItem nextOp = null;

		operationMutex.getMutex();

		for (int i = 0; i < operationQueue.size(); i++) {
			nextOp = (OperationItem) operationQueue.get(i);
			if( ( i!=0 ) && (nextOp.operation.isSynchronize()) ) {
				nextOp = null;
				break;	
			}
			else if (canBeProcessed(nextOp)) {
				operationQueue.remove(i);
				break;
			} else {
				nextOp.operation.incPriority();
				if( nextOp.operation.getPriority() >= Command.DEFINETLY_NEXT_OPERATION_PRIORITY ) {					
					nextOp = null;
					break;
				} else {
					nextOp = null;					
				}
			}
		}
		operationMutex.releaseMutex();

		return nextOp;
	}


	public synchronized void operationFinished(Command op, Worker w) {
		
		workerMutex.getMutex();

		worker.add(w);

		workerMutex.releaseMutex();

		ColumbaLogger.log.debug( "Operation finished" );
		notify();
	}

	private Worker getWorker() {
		Worker result = null;

		workerMutex.getMutex();

		if (worker.size() > 0)
			result = (Worker) worker.remove(0);

		workerMutex.releaseMutex();

		return result;
	}

	private synchronized void waitForNotify() {
		isBusy = false;
		try {
			wait();
		} catch (InterruptedException e) {
		}
		isBusy = true;
		
		ColumbaLogger.log.debug( "Operator woke up" );
	}

	public void run() // Scheduler
	{
		OperationItem opItem = null;
		Worker worker = null;

		while (true) {
			while ((opItem = getNextOpItem()) == null)
				waitForNotify();

			ColumbaLogger.log.debug( "Processing new Operation" );

			while ((worker = getWorker()) == null)
				waitForNotify();

			ColumbaLogger.log.debug( "Found Worker for new Operation" );
			guiUpdater.setGuiUpdater(worker);
			worker.process(opItem.operation, opItem.operationMode, timeStamp++);
			ColumbaLogger.log.debug( "Worker initilized" );
			worker.register(taskManager);
			ColumbaLogger.log.debug( "Starting Worker" );
			worker.start();
			
			ColumbaLogger.log.debug( "New Operation started..." );
		}
	}
	
	public boolean isBusy() {
		return isBusy();
	}
	
	public boolean hasFinishedCommands() {
		boolean result;
		workerMutex.getMutex();
		result = (worker.size() == MAX_WORKERS);
		workerMutex.releaseMutex();
		operationMutex.getMutex();
		result = result && (operationQueue.size() == 0);
		operationMutex.releaseMutex();
		
		return result;
	}
	/**
	 * Returns the undoManager.
	 * @return UndoManager
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * Returns the guiUpdater.
	 * @return GUIUpdater
	 */
	public GuiUpdater getGuiUpdater() {
		return guiUpdater;
	}

	/**
	 * Returns the taskManager.
	 * @return TaskManager
	 */
	public TaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * Returns the operationQueue. This method is for testing only!
	 * @return Vector
	 */
	public Vector getOperationQueue() {
		return operationQueue;
	}

}

class OperationItem {
	public Command operation;
	public int operationMode;		
	
	public OperationItem( Command op, int opMode ) {
		operation = op;
		operationMode = opMode;	
	}
}