/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.WorkbenchMessages;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The DecorationScheduler is the class that handles the decoration of elements
 * using a background thread.
 */
public class DecorationScheduler {

	// When decorations are computed they are added to this cache via
	// decorated() method
	Map resultCache = new HashMap();

	// Objects that need an icon and text computed for display to the user
	List awaitingDecoration = new ArrayList();

	// Objects that are awaiting a label update.
	Set pendingUpdate = new HashSet();
	
	ISchedulingRule updateRule = new ISchedulingRule(){

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
		 */
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
		 */
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this; //ALways conflict with this.
		}};

	Map awaitingDecorationValues = new HashMap();

	DecoratorManager decoratorManager;

	boolean shutdown = false;

	Job decorationJob;

	UIJob updateJob;

	/**
	 * Return a new instance of the receiver configured for the supplied
	 * DecoratorManager.
	 * 
	 * @param manager
	 */
	DecorationScheduler(DecoratorManager manager) {
		decoratorManager = manager;
		createDecorationJob();
	}

	/**
	 * Decorate the text for the receiver. If it has already been done then
	 * return the result, otherwise queue it for decoration.
	 * 
	 * @return String
	 * @param text
	 * @param element
	 * @param adaptedElement.
	 *            The adapted value of element. May be null.
	 */

	public String decorateWithText(String text, Object element,
			Object adaptedElement) {

		DecorationResult decoration = getResult(element, adaptedElement, text);

		if (decoration == null)
			return text;

		return decoration.decorateWithText(text);

	}

	/**
	 * Queue the element and its adapted value if it has not been already.
	 * 
	 * @param element
	 * @param adaptedElement.
	 *            The adapted value of element. May be null.
	 * @param forceUpdate.
	 *            If true then a labelProviderChanged is fired whether
	 *            decoration occured or not.
	 * @param undecoratedText The original text for the element if it is known.
	 */

	synchronized void queueForDecoration(Object element, Object adaptedElement,
			boolean forceUpdate, String undecoratedText) {

		if (awaitingDecorationValues.containsKey(element)) {
			if (forceUpdate) {//Make sure we don't loose a force
				DecorationReference reference = (DecorationReference) awaitingDecorationValues
						.get(element);
				reference.setForceUpdate(forceUpdate);
			}
		} else {
			DecorationReference reference = new DecorationReference(element,
					adaptedElement);
			reference.setForceUpdate(forceUpdate);
			reference.setUndecoratedText(undecoratedText);
			awaitingDecorationValues.put(element, reference);
			awaitingDecoration.add(element);
			if (shutdown)
				return;
			if (decorationJob.getState() == Job.SLEEPING)
				decorationJob.wakeUp();
			decorationJob.schedule();
		}

	}

	/**
	 * Decorate the supplied image, element and its adapted value.
	 * 
	 * @return Image
	 * @param image
	 * @param element
	 * @param adaptedElement.
	 *            The adapted value of element. May be null.
	 *  
	 */
	public Image decorateWithOverlays(Image image, Object element,
			Object adaptedElement) {

		DecorationResult decoration = getResult(element, adaptedElement, null);

		if (decoration == null)
			return image;
		return decoration.decorateWithOverlays(image, decoratorManager
				.getLightweightManager().getOverlayCache());
	}

	/**
	 * Return the DecorationResult for element. If there isn't one queue for
	 * decoration and return <code>null</code>.
	 * 
	 * @param element
	 *            The element to be decorated. If it is <code>null</code>
	 *            return <code>null</code>.
	 * @param adaptedElement
	 *            It's adapted value.
	 * @param text
	 *            The inital String or <code>null</code>
	 * @return DecorationResult or <code>null</code>
	 */
	private DecorationResult getResult(Object element, Object adaptedElement,
			String text) {

		//We do not support decoration of null
		if (element == null)
			return null;

		DecorationResult decoration = (DecorationResult) resultCache
				.get(element);

		if (decoration == null) {
			queueForDecoration(element, adaptedElement, false, null);
			return null;
		}
		return decoration;

	}

	/**
	 * Execute a label update using the pending decorations.
	 */
	void decorated() {

		//Don't bother if we are shutdown now
		if (shutdown)
			return;

		//Lazy initialize the job
		if (updateJob == null) {
			updateJob = getUpdateJob();
			updateJob.setPriority(Job.DECORATE);
		}

		//Give it a big of a lag for other updates to occur
		updateJob.schedule(100);
	}

	/**
	 * Shutdown the decoration.
	 */
	void shutdown() {
		shutdown = true;
	}

	/**
	 * Get the next resource to be decorated.
	 * 
	 * @return IResource
	 */
	synchronized DecorationReference nextElement() {

		if (shutdown || awaitingDecoration.isEmpty()) {
			return null;
		}
		Object element = awaitingDecoration.remove(0);

		return (DecorationReference) awaitingDecorationValues.remove(element);
	}

	/**
	 * Create the Thread used for running decoration.
	 */
	private void createDecorationJob() {
		decorationJob = new Job(WorkbenchMessages.DecorationScheduler_CalculationJobName) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus run(IProgressMonitor monitor) {
				
				if(shutdown)//Cancelled on shutdown
					return Status.CANCEL_STATUS;
				
				monitor.beginTask(WorkbenchMessages.DecorationScheduler_CalculatingTask, 100);
				//will block if there are no resources to be decorated
				DecorationReference reference;
				monitor.worked(5);
				int workCount = 5;
				while ((reference = nextElement()) != null) {

					//Count up to 90 to give the appearance of updating
					if (workCount < 90) {
						monitor.worked(1);
						workCount++;
					}

					DecorationBuilder cacheResult = new DecorationBuilder();

					monitor.subTask(reference.getSubTask()); //$NON-NLS-1$
					//Don't decorate if there is already a pending result
					Object element = reference.getElement();
					Object adapted = reference.getAdaptedElement();

					boolean elementIsCached = true;
					DecorationResult adaptedResult = null;

					//Synchronize on the result lock as we want to
					//be sure that we do not try and decorate during
					//label update servicing.
					
					elementIsCached = resultCache.containsKey(element);
					if (elementIsCached) {
						pendingUpdate.add(element);
					}
					if (adapted != null) {
						adaptedResult = (DecorationResult) resultCache
								.get(adapted);
					}
					
					if (!elementIsCached) {
						//Just build for the resource first
						if (adapted != null) {
							if (adaptedResult == null) {
								decoratorManager.getLightweightManager()
										.getDecorations(adapted, cacheResult,
												true);
								if (cacheResult.hasValue()) {
									adaptedResult = cacheResult.createResult();
								}
							} else {
								// If we already calculated the decoration
								// for the adapted element, reuse the result.
								cacheResult.applyResult(adaptedResult);
								// Set adaptedResult to null to indicate that
								// we do not need to cache the result again.
								adaptedResult = null;
							}
						}

						//Now add in the results for the main object

						decoratorManager.getLightweightManager()
								.getDecorations(element, cacheResult, false);

						//If we should update regardless then put a result
						// anyways
						if (cacheResult.hasValue()
								|| reference.shouldForceUpdate()) {

							//Synchronize on the result lock as we want to
							//be sure that we do not try and decorate during
							//label update servicing.
							//Note: resultCache and pendingUpdate modifications
							//must be done atomically.
							if (adaptedResult != null) 
								resultCache.put(adapted, adaptedResult);
							
							// Add the decoration even if it's empty in
							// order to indicate that the decoration is
							// ready
							resultCache.put(element, cacheResult
									.createResult());

							//Add an update for only the original element
							// to
							//prevent multiple updates and clear the cache.
							pendingUpdate.add(element);
							
						}
					}

					// Only notify listeners when we have exhausted the
					// queue of decoration requests.
					if (awaitingDecoration.isEmpty()) {
						decorated();
					}
				}
				monitor.worked(100 - workCount);
				monitor.done();
				return Status.OK_STATUS;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
			 */
			public boolean belongsTo(Object family) {
				return DecoratorManager.FAMILY_DECORATE == family;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
			 */
			public boolean shouldRun() {
				return PlatformUI.isWorkbenchRunning();
			}
		};

		decorationJob.setRule(updateRule);
		decorationJob.setSystem(true);
		decorationJob.setPriority(Job.DECORATE);
		decorationJob.schedule();
	}

	/**
	 * An external update request has been made. Clear the results as they are
	 * likely obsolete now.
	 */
	void clearResults() {
		
		Job clearJob = new Job(WorkbenchMessages.DecorationScheduler_ClearResultsJob){ 

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			protected IStatus run(IProgressMonitor monitor) {
				resultCache.clear();
				return Status.OK_STATUS;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
			 */
			public boolean shouldRun() {
				return PlatformUI.isWorkbenchRunning();
			}
			
		};
		clearJob.setSystem(true);
		clearJob.setRule(updateRule);
		clearJob.schedule();
	}

	/**
	 * Get the update WorkbenchJob.
	 * 
	 * @return WorkbenchJob
	 */
	private WorkbenchJob getUpdateJob() {
		WorkbenchJob job = new WorkbenchJob(WorkbenchMessages.DecorationScheduler_UpdateJobName) {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				
				if(shutdown)//Cancelled on shutdown
					return Status.CANCEL_STATUS;
				
				//Check again in case someone has already cleared it out.
				if (pendingUpdate.isEmpty())
						return Status.OK_STATUS;

					//Get the elements awaiting update and then
					//clear the list
					Object[] elements = pendingUpdate
							.toArray(new Object[pendingUpdate.size()]);
					monitor
							.beginTask(
									WorkbenchMessages.DecorationScheduler_UpdatingTask, elements.length + 20); 
					pendingUpdate.clear();
					monitor.worked(15);
					decoratorManager
							.fireListeners(new LabelProviderChangedEvent(
									decoratorManager, elements));
					monitor.worked(elements.length);
					//Other decoration requests may have occured due to
					//updates. Only clear the results if there are none
					// pending.
					if (awaitingDecoration.isEmpty())
						resultCache.clear();
					monitor.worked(5);
					monitor.done();
				
				if (!pendingUpdate.isEmpty())
					decorated();
				return Status.OK_STATUS;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
			 */
			public boolean belongsTo(Object family) {
				return DecoratorManager.FAMILY_DECORATE == family;
			}
		};

		job.setRule(updateRule);
		job.setSystem(true);
		return job;
	}

	/**
	 * Return whether or not there is a decoration for this element ready.
	 * 
	 * @param element
	 * @return boolean true if the element is ready.
	 */
	public boolean isDecorationReady(Object element) {
		return resultCache.get(element) != null;
	}

	/**
	 * Return the background Color for element. If there is no result cue for
	 * decoration and return null, otherwise return the value in the result.
	 * 
	 * @param element
	 *            The Object to be decorated
	 * @param adaptedElement
	 * @return Color or <code>null</code> if there is no value or if it is has
	 *         not been decorated yet.
	 */
	public Color getBackgroundColor(Object element, Object adaptedElement) {
		DecorationResult decoration = getResult(element, adaptedElement, null);

		if (decoration == null)
			return null;
		return decoration.getBackgroundColor();
	}

	/**
	 * Return the font for element. If there is no result cue for
	 * decoration and return null, otherwise return the value in the result.
	 * 
	 * @param element
	 *            The Object to be decorated
	 * @param adaptedElement
	 * @return Font or <code>null</code> if there is no value or if it is has
	 *         not been decorated yet.
	 */
	public Font getFont(Object element, Object adaptedElement) {
		DecorationResult decoration = getResult(element, adaptedElement, null);

		if (decoration == null)
			return null;
		return decoration.getFont();
	}

	/**
	 * Return the foreground Color for element. If there is no result cue for
	 * decoration and return null, otherwise return the value in the result.
	 * 
	 * @param element
	 *            The Object to be decorated
	 * @param adaptedElement
	 * @return Color or <code>null</code> if there is no value or if it is has
	 *         not been decorated yet.
	 */
	public Color getForegroundColor(Object element, Object adaptedElement) {
		DecorationResult decoration = getResult(element, adaptedElement, null);

		if (decoration == null)
			return null;
		return decoration.getForegroundColor();
	}

	/**
	 * Return whether or not any updates are being
	 * processed/
	 * @return boolean
	 */
	public boolean processingUpdates() {
		return !pendingUpdate.isEmpty() && !awaitingDecoration.isEmpty();
	}
}