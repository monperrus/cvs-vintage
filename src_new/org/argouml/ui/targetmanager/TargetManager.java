// $Id: TargetManager.java,v 1.3 2003/04/30 14:59:57 kataka Exp $
// Copyright (c) 2002 The Regents of the University of California. All
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
package org.argouml.ui.targetmanager;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

/**
 * <p>
 * The manager of the target of argouml. The target of argouml is the selected
 * element in the model. This can either be an instance of a meta-class (an
 * Interface or a Class for example) but it can also be a diagram or a figure 
 * on a diagram.
 * </p>
 * <p>
 * Via an event mechanism this manager makes sure that all objects interested 
 * in knowing wether the event changed are acknowledged.
 * </p>
 * 
 * @author jaap.branderhorst@xs4all.nl
 */
public final class TargetManager {
	/**
	 * The log4j logger to log messages to
	 */
	private Logger _log = Logger.getLogger(this.getClass());

	/**
	 * The singleton instance
	 */
	private static TargetManager instance = new TargetManager();

	/**
	 * The targets stored in an object array to improve performance
	 */
	private Object[] _targets = new Object[0];

	/**
	 * The list with targetlisteners
	 */
	private EventListenerList _listenerList = new EventListenerList();

	private boolean inTransaction = false;

	/**
	 * Singleton retrieval method
	 * @return The targetmanager
	 */
	public static TargetManager getInstance() {
		return instance;
	}

	/**
	 * Sets the targets to the single given object. If there are targets at the 
	 * moment of calling this method, these will be removed as targets. To 
	 * all interested targetlisteners, a TargetEvent will be fired. If the 
	 * new target o equals the current target, no events will be fired, nor will
	 * the target be (re)set.
	 * @param o The new target
	 */
	public synchronized void setTarget(Object o) {
		if (!isInTargetTransaction()) {
			startTargetTransaction();
			Object[] targets = new Object[] { o };
			if (!targets.equals(_targets)) {
				fireTargetSet(targets);
				_targets = new Object[] { o };

			}
			endTargetTransaction();
		}
	}

	/**
	 * Returns the current target. If there are more then 1 target, 
	 * a TargetException will be fired because this is clearly a programming
	 * error.
	 * @return The current target
	 * @throws TargetException if there are more then 1 target.
	 */
	public synchronized Object getTarget() throws TargetException {
		if (_targets.length == 0) {
			_log.warn("Returning null as target. No target was selected.");
		}
		return _targets.length >= 1 ? _targets[0] : null;
	}

	/**
	 * Sets the given collection to the current targets. If the collection 
	 * equals the current targets, the targets will not be (re)set. When setting
	 * the targets, a TargetEvent will be fired to each interested listener.
	 * @param targetsList The new targets list.
	 */
	public synchronized void setTargets(Collection targetsList) {
		if (!isInTargetTransaction()) {
			startTargetTransaction();
			if (targetsList != null && !targetsList.isEmpty()) {
				Object[] targets = targetsList.toArray();
				if (!targets.equals(_targets)) {
					fireTargetSet(targets);
					_targets = targets;
				}
			} else {
				_targets = new Object[0];
			}
			endTargetTransaction();
		}
	}

	/**
	 * Adds a target to the targets list. If the target is allready in the targets
	 * list no (re)setting will take place. Otherwise the target will be added
	 * and an appropriate TargetEvent will be fired to all interested listeners.
	 * @param target the target to be added.
	 */
	public synchronized void addTarget(Object target) {
		if (target != null && !isInTargetTransaction()) {
			startTargetTransaction();
			Object[] targets = new Object[_targets.length + 1];
			System.arraycopy(_targets, 0, targets, 0, _targets.length);
			targets[_targets.length] = target;
			fireTargetAdded(target);
			_targets = targets;
			endTargetTransaction();
		}
	}

	/**
	 * Removes the target from the targets list. Does do nothing if the target
	 * does not exist in the targets list. Fires an appropriate TargetEvent to 
	 * all interested listeners.
	 * @param target The target to remove.
	 */
	public synchronized void removeTarget(Object target) {
		if (target != null && !isInTargetTransaction()) {
			startTargetTransaction();
			boolean found = false;
			for (int i = 0; i < _targets.length; i++) {
				if (_targets[i] == target) {
					Object[] targets = new Object[_targets.length - 1];
					// Copy the list up to index
					System.arraycopy(_targets, 0, targets, 0, i);
					// Copy from two past the index, up to
					// the end of tmp (which is two elements
					// shorter than the old list)
					if (i < targets.length)
						System.arraycopy(
							_targets,
							i + 1,
							targets,
							i,
							targets.length - i);
					// set the listener array to the new array or null
					fireTargetRemoved(target);
					_targets = (targets.length == 0) ? new Object[0] : targets;

				}
			}
			endTargetTransaction();
		}
	}

	/**
	 * Returns a collection with all targets. Returns null if there are no
	 * targets.
	 * @return A collection with all targets.
	 */
	public synchronized Collection getTargets() {
		return _targets.length > 0 ? Arrays.asList(_targets) : null;
	}

	/**
	 * Adds a listener.
	 * @param listener the listener to add
	 */
	public void addTargetListener(TargetListener listener) {
		_listenerList.add(TargetListener.class, listener);
	}

	/**
	 * Removes a listener.
	 * @param listener the listener to remove
	 */
	public void removeTargetListener(TargetListener listener) {
		_listenerList.remove(TargetListener.class, listener);
	}

	private void fireTargetSet(Object[] newTargets) {
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		TargetEvent targetEvent =
			new TargetEvent(this, TargetEvent.TARGET_SET, _targets, newTargets);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TargetListener.class) {
				// Lazily create the event:                     
				 ((TargetListener) listeners[i + 1]).targetSet(targetEvent);
			}
		}
	}

	private void fireTargetAdded(Object targetAdded) {
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		TargetEvent targetEvent =
			new TargetEvent(
				this,
				TargetEvent.TARGET_SET,
				_targets,
				new Object[] { targetAdded });

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TargetListener.class) {
				// Lazily create the event:                     
				 ((TargetListener) listeners[i + 1]).targetAdded(targetEvent);
			}
		}
	}

	private void fireTargetRemoved(Object targetRemoved) {
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		TargetEvent targetEvent =
			new TargetEvent(
				this,
				TargetEvent.TARGET_SET,
				_targets,
				new Object[] { targetRemoved });

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TargetListener.class) {
				// Lazily create the event:                     
				((TargetListener) listeners[i + 1]).targetRemoved(targetEvent);
			}
		}
	}

	private void startTargetTransaction() {
		inTransaction = true;
	}

	private boolean isInTargetTransaction() {
		return inTransaction;
	}

	private void endTargetTransaction() {
		inTransaction = false;
	}

}
