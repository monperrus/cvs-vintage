/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.progress;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * The AnimationManager is the class that keeps track of the animation items
 * to update.
 */
class AnimationManager {

	private static final String PROGRESS_FOLDER = "icons/full/progress/"; //$NON-NLS-1$
	private static final String RUNNING_ICON = "running.gif"; //$NON-NLS-1$
	private static final String BACKGROUND_ICON = "back.gif"; //$NON-NLS-1$
	private static final String ERROR_ICON = "error.gif"; //$NON-NLS-1$

	private static AnimationManager singleton;
	
	private ImageData[] animatedData;
	private ImageData[] disabledData;
	private ImageData[] errorData;

	private Image disabledImage;
	private Image animatedImage;
	private Image errorImage;

	Color background;

	private ImageLoader runLoader = new ImageLoader();
	private ImageLoader errorLoader = new ImageLoader();
	boolean animated = false;
	Job animateJob;
	boolean showingError = false;
	private IJobProgressManagerListener listener;

	List items = Collections.synchronizedList(new ArrayList());
	
	static AnimationManager getInstance(){
		if(singleton == null)
			singleton = new AnimationManager();
		return singleton;
	}

	AnimationManager() {
		URL iconsRoot =
			Platform.getPlugin(PlatformUI.PLUGIN_ID).find(
				new Path(PROGRESS_FOLDER));

		try {
			URL runningRoot = new URL(iconsRoot, RUNNING_ICON);
			URL backRoot = new URL(iconsRoot, BACKGROUND_ICON);
			URL errorRoot = new URL(iconsRoot, ERROR_ICON);

			animatedData = getImageData(runningRoot, runLoader);
			if (animatedData != null)
				animatedImage = getImage(animatedData[0]);

			disabledData = getImageData(backRoot, runLoader);
			if (disabledData != null)
				disabledImage = getImage(disabledData[0]);

			errorData = getImageData(errorRoot, errorLoader);
			if (errorData != null)
				errorImage = getImage(errorData[0]);

			getImageData(backRoot, errorLoader);

			listener = getProgressListener();
			JobProgressManager.getInstance().addListener(listener);
		} catch (MalformedURLException exception) {
			ProgressUtil.logException(exception);
		}
	}

	/**
	 * Add an items to the list
	 * @param item
	 */
	void addItem(final AnimationItem item) {
		items.add(item);
		if(background == null)
			background = item.getControl().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		item.getControl().addDisposeListener(new DisposeListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
			 */
			public void widgetDisposed(DisposeEvent e) {
				AnimationManager.this.items.remove(item);
			}
		});
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 * @param source
	 * @return Image
	 */
	private Image getImage(ImageData source) {
		ImageData mask = source.getTransparencyMask();
		return new Image(null, source, mask);
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 * @param fileSystemPath The URL for the file system to the image.
	 * @param loader - the loader used to get this data
	 * @return ImageData[]
	 */
	ImageData[] getImageData(URL fileSystemPath, ImageLoader loader) {
		try {
			InputStream stream = fileSystemPath.openStream();
			ImageData[] result = loader.load(stream);
			stream.close();
			return result;
		} catch (FileNotFoundException exception) {
			ProgressUtil.logException(exception);
			return null;
		} catch (IOException exception) {
			ProgressUtil.logException(exception);
			return null;
		}
	}

	/**
	 * Get the current ImageData for the receiver.
	 * @return ImageData[]
	 */
	ImageData[] getImageData() {
		if (animated) {
			if (showingError)
				return errorData;
			else
				return animatedData;
		} else
			return disabledData;
	}

	/**
	 * Get the current Image for the receiver.
	 * @return Image
	 */
	Image getImage() {
		if (animated) {
			if (showingError)
				return errorImage;
			else
				return animatedImage;
		} else
			return disabledImage;
	}

	/**
	 * Return whether or not the current state is animated.
	 * @return boolean
	 */
	boolean isAnimated() {
		return animated;
	}

	/**
	 * Set whether or not the receiver is animated.
	 * @param boolean
	 */
	void setAnimated(final boolean bool) {

		animated = bool;
		if (bool) {
			ImageData[] imageDataArray = getImageData();
			if (isAnimated() && imageDataArray.length > 1) {
				getAnimateJob().schedule();
			}
		}
	}

	/**
	 * Get the SWT control for the receiver.
	 * @return Control
	 */
	//	public Control getControl() {
	//		return imageCanvas;
	//	}

	/**
	 * Dispose the images in the receiver.
	 */
	void dispose() {
		disabledImage.dispose();
		errorImage.dispose();
		animatedImage.dispose();
		JobProgressManager.getInstance().removeListener(listener);
	}

	/**
	 * Loop through all of the images in a multi-image file
	 * and display them one after another.
	 * @param monitor The monitor supplied to the job
	 */
	void animateLoop(IProgressMonitor monitor) {
		// Create an off-screen image to draw on, and a GC to draw with.
		// Both are disposed after the animation.

		if (items.size() == 0)
			return;

		AnimationItem[] animationItems = getAnimationItems();

		boolean startErrorState = showingError;
		Display display = animationItems[0].getControl().getDisplay();
		ImageData[] imageDataArray = getImageData();
		ImageData imageData = imageDataArray[0];
		Image image = getImage(imageData);
		int imageDataIndex = 0;

		ImageLoader loader = getLoader();

		Image offScreenImage =
			new Image(
				display,
				loader.logicalScreenWidth,
				loader.logicalScreenHeight);
		GC offScreenImageGC = new GC(offScreenImage);

		try {

			// Fill the off-screen image with the background color of the canvas.
			offScreenImageGC.setBackground(background);
			offScreenImageGC.fillRectangle(
				0,
				0,
				loader.logicalScreenWidth,
				loader.logicalScreenHeight);

			// Draw the current image onto the off-screen image.
			offScreenImageGC.drawImage(
				image,
				0,
				0,
				imageData.width,
				imageData.height,
				imageData.x,
				imageData.y,
				imageData.width,
				imageData.height);

			if (loader.repeatCount > 0) {
				while (isAnimated()
					&& !monitor.isCanceled()
					&& (startErrorState == showingError)) {

					if (imageData.disposalMethod == SWT.DM_FILL_BACKGROUND) {
						// Fill with the background color before drawing.
						Color bgColor = null;
						int backgroundPixel = loader.backgroundPixel;
						if (backgroundPixel != -1) {
							// Fill with the background color.
							RGB backgroundRGB =
								imageData.palette.getRGB(backgroundPixel);
							bgColor = new Color(null, backgroundRGB);
						}
						try {
							offScreenImageGC.setBackground(
								bgColor != null ? bgColor : background);
							offScreenImageGC.fillRectangle(
								imageData.x,
								imageData.y,
								imageData.width,
								imageData.height);
						} finally {
							if (bgColor != null)
								bgColor.dispose();
						}
					} else if (
						imageData.disposalMethod == SWT.DM_FILL_PREVIOUS) {
						// Restore the previous image before drawing.
						offScreenImageGC.drawImage(
							image,
							0,
							0,
							imageData.width,
							imageData.height,
							imageData.x,
							imageData.y,
							imageData.width,
							imageData.height);
					}

					// Get the next image data.
					imageDataIndex =
						(imageDataIndex + 1) % imageDataArray.length;
					imageData = imageDataArray[imageDataIndex];
					image.dispose();
					image = new Image(display, imageData);

					// Draw the new image data.
					offScreenImageGC.drawImage(
						image,
						0,
						0,
						imageData.width,
						imageData.height,
						imageData.x,
						imageData.y,
						imageData.width,
						imageData.height);
					boolean refreshItems = false;
					for (int i = 0; i < animationItems.length; i++) {
						AnimationItem item = animationItems[i];
						if (item.imageCanvasGC.isDisposed()) {
							refreshItems = true;
							continue;
						}
						// Draw the off-screen image to the screen.
						item.imageCanvasGC.drawImage(offScreenImage, 0, 0);
					}

					if (refreshItems)
						animationItems = getAnimationItems();
					// Sleep for the specified delay time before drawing again.
					try {
						Thread.sleep(visibleDelay(imageData.delayTime * 10));
					} catch (InterruptedException e) {
					}

				}
			}
		} finally {
			image.dispose();
			offScreenImage.dispose();
			offScreenImageGC.dispose();
		}
	}

	/**
	 * Get the animation items currently registered for the receiver.
	 * @return
	 */
	AnimationItem[] getAnimationItems() {
		AnimationItem[] animationItems = new AnimationItem[items.size()];
		items.toArray(animationItems);
		return animationItems;
	}

	/**
	 * Return the specified number of milliseconds.
	 * If the specified number of milliseconds is too small
	 * to see a visual change, then return a higher number.
	 * @param ms The suggested delay
	 * @return int
	 */
	int visibleDelay(int ms) {
		if (ms < 20)
			return ms + 30;
		if (ms < 30)
			return ms + 10;
		return ms;
	}

	/**
	 * Get the bounds of the image being displayed here.
	 * @return Rectangle
	 */
	public Rectangle getImageBounds() {
		return disabledImage.getBounds();
	}

	private IJobProgressManagerListener getProgressListener() {
		return new IJobProgressManagerListener() {

			HashSet jobs = new HashSet();

			/* (non-Javadoc)
			 * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#add(org.eclipse.ui.internal.progress.JobInfo)
			 */
			public void add(JobInfo info) {
				incrementJobCount(info.getJob());

			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refresh(org.eclipse.ui.internal.progress.JobInfo)
			 */
			public void refresh(JobInfo info) {
				if (info.getErrorStatus() != null)
					showingError = true;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshAll()
			 */
			public void refreshAll() {
				JobProgressManager manager = JobProgressManager.getInstance();
				showingError = manager.hasErrorsDisplayed();
				jobs.clear();
				Object[] currentJobs = manager.getJobs();
				for (int i = 0; i < currentJobs.length; i++) {
					jobs.add(currentJobs[i]);
				}
				setAnimated(showingError);

			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#remove(org.eclipse.ui.internal.progress.JobInfo)
			 */
			public void remove(JobInfo info) {
				if (jobs.contains(info.getJob())) {
					decrementJobCount(info.getJob());
				}

			}

			private void incrementJobCount(Job job) {
				//Don't count the animate job itself
				if (job.isSystem())
					return;
				if (jobs.size() == 0)
					setAnimated(true);
				jobs.add(job);
			}

			private void decrementJobCount(Job job) {
				//Don't count the animate job itself
				if (job.isSystem())
					return;
				jobs.remove(job);
				if (jobs.isEmpty())
					setAnimated(false);
			}
		};
	}

	private Job getAnimateJob() {
		if (animateJob == null) {
				animateJob = new Job(ProgressMessages.getString("AnimateJob.JobName")) {//$NON-NLS-1$
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
				public IStatus run(IProgressMonitor monitor) {
					try {
						animateLoop(monitor);
						return Status.OK_STATUS;
					} catch (SWTException exception) {
						return ProgressUtil.exceptionStatus(exception);
					}
				}
			};
			animateJob.setSystem(true);
			animateJob.setPriority(Job.DECORATE);
			animateJob.addJobChangeListener(new JobChangeAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
				 */
				public void done(IJobChangeEvent event) {
					if (isAnimated())
						animateJob.schedule();
					else {
						//Clear the image

							UIJob clearJob = new UIJob(ProgressMessages.getString("AnimationItem.RedrawJob")) {//$NON-NLS-1$
							/* (non-Javadoc)
							 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
							 */
							public IStatus runInUIThread(IProgressMonitor monitor) {
								AnimationItem[] animationItems =
									getAnimationItems();
								for (int i = 0; i < animationItems.length; i++)
									if (!animationItems[i]
										.getControl()
										.isDisposed())
										animationItems[i].getControl().redraw();
								return Status.OK_STATUS;
							}
						};
						clearJob.setSystem(true);
						clearJob.schedule();
					}
				}
			});

		}
		return animateJob;
	}

	/**
	 * Return the loader currently in use.
	 * @return ImageLoader
	 */
	ImageLoader getLoader() {
		if (showingError)
			return errorLoader;
		else
			return runLoader;
	}

}
