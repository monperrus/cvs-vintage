/******************************************************************************* 
 * Copyright (c) 2000, 2003 IBM Corporation and others. 
 * All rights reserved. This program and the accompanying materials! 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html 
 * 
 * Contributors: 
 *   IBM Corporation - initial API and implementation 
 *   Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog font should be activated and used by other components.
******************************************************************************/ 
package org.eclipse.ui.dialogs;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ILabelProvider;

import org.eclipse.ui.progress.WorkbenchJob;

import org.eclipse.ui.internal.misc.StringMatcher;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * A composite widget which holds a list of elements for user selection.
 * The elements are sorted alphabetically.
 * Optionally, the elements can be filtered and duplicate entries can
 * be hidden (folding).
 * 
 * @since 2.0
 */
public class FilteredList extends Composite {

	public interface FilterMatcher {
		/**
		 * Sets the filter.
		 * 
		 * @param pattern         the filter pattern.
		 * @param ignoreCase      a flag indicating whether pattern matching is case insensitive or not.
		 * @param ignoreWildCards a flag indicating whether wildcard characters are interpreted or not.
		 */
		void setFilter(
			String pattern,
			boolean ignoreCase,
			boolean ignoreWildCards);

		/**
		 * Returns <code>true</code> if the object matches the pattern, <code>false</code> otherwise.
		 * <code>setFilter()</code> must have been called at least once prior to a call to this method.
		 */
		boolean match(Object element);
	}

	private class DefaultFilterMatcher implements FilterMatcher {
		private StringMatcher fMatcher;

		public void setFilter(
			String pattern,
			boolean ignoreCase,
			boolean ignoreWildCards) {
			fMatcher =
				new StringMatcher(pattern + '*', ignoreCase, ignoreWildCards);
		}

		public boolean match(Object element) {
			return fMatcher.match(fLabelProvider.getText(element));
		}
	}

	private Table fList;
	ILabelProvider fLabelProvider;
	private boolean fMatchEmptyString = true;
	private boolean fIgnoreCase;
	private boolean fAllowDuplicates;
	private String fFilter = ""; //$NON-NLS-1$
	private TwoArrayQuickSorter fSorter;

	Object[] fElements = new Object[0];
	Label[] fLabels;
	Vector fImages = new Vector();

	int[] fFoldedIndices;
	int fFoldedCount;

	int[] fFilteredIndices;
	int fFilteredCount;

	private FilterMatcher fFilterMatcher = new DefaultFilterMatcher();
	Comparator fComparator;

	TableUpdateJob fUpdateJob;

	private static class Label {
		public final String string;
		public final Image image;

		public Label(String string, Image image) {
			this.string = string;
			this.image = image;
		}

		public boolean equals(Label label) {
			if (label == null)
				return false;
			
			// If the string portions match (whether null or not), fall
			// through and check the image portion.
			if (string == null && label.string != null)
					return false;				
			if ((string != null) && (!string.equals(label.string)))
				return false;
			
			if (image == null)
				return label.image == null;
			return image.equals(label.image);
		}
	}

	private final class LabelComparator implements Comparator {
		private boolean labelIgnoreCase;

		LabelComparator(boolean ignoreCase) {
			labelIgnoreCase = ignoreCase;
		}

		public int compare(Object left, Object right) {
			Label leftLabel = (Label) left;
			Label rightLabel = (Label) right;

			int value;

			if (fComparator == null) {
				value =
					labelIgnoreCase
						? leftLabel.string.compareToIgnoreCase(rightLabel.string)
						: leftLabel.string.compareTo(rightLabel.string);
			} else {
				value =
					fComparator.compare(leftLabel.string, rightLabel.string);
			}

			if (value != 0)
				return value;

			// images are allowed to be null
			if (leftLabel.image == null) {
				return (rightLabel.image == null) ? 0 : -1;
			} else if (rightLabel.image == null) {
				return +1;
			} else {
				return fImages.indexOf(leftLabel.image)
					- fImages.indexOf(rightLabel.image);
			}
		}

	}

	/**
	 * Constructs a new filtered list.
	 * 
	 * @param parent           the parent composite
	 * @param style            the widget style
	 * @param labelProvider    the label renderer
	 * @param ignoreCase       specifies whether sorting and folding is case sensitive
	 * @param allowDuplicates  specifies whether folding of duplicates is desired
	 * @param matchEmptyString specifies whether empty filter strings should filter everything or nothing
	 */
	public FilteredList(
		Composite parent,
		int style,
		ILabelProvider labelProvider,
		boolean ignoreCase,
		boolean allowDuplicates,
		boolean matchEmptyString) {
		super(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		fList = new Table(this, style);
		fList.setLayoutData(new GridData(GridData.FILL_BOTH));
		fList.setFont(parent.getFont());
		fList.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fLabelProvider.dispose();
				if (fUpdateJob != null)
					fUpdateJob.cancel();
			}
		});

		fLabelProvider = labelProvider;
		fIgnoreCase = ignoreCase;
		fSorter = new TwoArrayQuickSorter(new LabelComparator(ignoreCase));
		fAllowDuplicates = allowDuplicates;
		fMatchEmptyString = matchEmptyString;
	}

	/**
	 * Sets the list of elements.
	 * @param elements the elements to be shown in the list.
	 */
	public void setElements(Object[] elements) {
		if (elements == null) {
			fElements = new Object[0];
		} else {
			// copy list for sorting
			fElements = new Object[elements.length];
			System.arraycopy(elements, 0, fElements, 0, elements.length);
		}

		int length = fElements.length;

		// fill labels			
		fLabels = new Label[length];
		Set imageSet = new HashSet();
		for (int i = 0; i != length; i++) {
			String text = fLabelProvider.getText(fElements[i]);
			Image image = fLabelProvider.getImage(fElements[i]);

			fLabels[i] = new Label(text, image);
			imageSet.add(image);
		}
		fImages.clear();
		fImages.addAll(imageSet);

		fSorter.sort(fLabels, fElements);

		fFilteredIndices = new int[length];
		fFoldedIndices = new int[length];

		updateList();
	}

	/**
	 * Tests if the list (before folding and filtering) is empty.
	 * @return returns <code>true</code> if the list is empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return (fElements == null) || (fElements.length == 0);
	}

	/**
	 * Sets the filter matcher.
	 */
	public void setFilterMatcher(FilterMatcher filterMatcher) {
		Assert.isNotNull(filterMatcher);
		fFilterMatcher = filterMatcher;
	}

	/**
	 * Sets a custom comparator for sorting the list.
	 */
	public void setComparator(Comparator comparator) {
		Assert.isNotNull(comparator);
		fComparator = comparator;
	}

	/**
	 * Adds a selection listener to the list.
	 * @param listener the selection listener to be added.
	 */
	public void addSelectionListener(SelectionListener listener) {
		fList.addSelectionListener(listener);
	}

	/**
	 * Removes a selection listener from the list.
	 * @param listener the selection listener to be removed.
	 */
	public void removeSelectionListener(SelectionListener listener) {
		fList.removeSelectionListener(listener);
	}

	/**
	 * Sets the selection of the list.
	 * Empty or null array removes selection.
	 * @param selection an array of indices specifying the selection.
	 */
	public void setSelection(int[] selection) {
		if (selection == null || selection.length == 0)
			fList.deselectAll();
		else {
			//If there is a current working update defer the setting
			if (fUpdateJob == null || fUpdateJob.getState() != Job.RUNNING) {
				fList.setSelection(selection);
				fList.notifyListeners(SWT.Selection, new Event());
			} else
				fUpdateJob.updateSelection(selection);
		}
	}

	/**
	 * Returns the selection of the list.
	 * @return returns an array of indices specifying the current selection.
	 */
	public int[] getSelectionIndices() {
		return fList.getSelectionIndices();
	}

	/**
	 * Returns the selection of the list.
	 * This is a convenience function for <code>getSelectionIndices()</code>.
	 * @return returns the index of the selection, -1 for no selection.
	 */
	public int getSelectionIndex() {
		return fList.getSelectionIndex();
	}

	/**
	 * Sets the selection of the list.
	 * Empty or null array removes selection.
	 * @param elements the array of elements to be selected.
	 */
	public void setSelection(Object[] elements) {
		if (elements == null || elements.length == 0) {
			fList.deselectAll();
			return;
		}

		if (fElements == null)
			return;

		// fill indices
		int[] indices = new int[elements.length];
		for (int i = 0; i != elements.length; i++) {
			int j;
			for (j = 0; j != fFoldedCount; j++) {
				int max =
					(j == fFoldedCount - 1)
						? fFilteredCount
						: fFoldedIndices[j + 1];

				int l;
				for (l = fFoldedIndices[j]; l != max; l++) {
					// found matching element?
					if (fElements[fFilteredIndices[l]].equals(elements[i])) {
						indices[i] = j;
						break;
					}
				}

				if (l != max)
					break;
			}

			// not found
			if (j == fFoldedCount)
				indices[i] = 0;
		}

		setSelection(indices);
	}

	/**
	 * Returns an array of the selected elements. The type of the elements
	 * returned in the list are the same as the ones passed with
	 * <code>setElements</code>. The array does not contain the rendered strings.
	 * @return returns the array of selected elements.
	 */
	public Object[] getSelection() {
		if (fList.isDisposed() || (fList.getSelectionCount() == 0))
			return new Object[0];

		int[] indices = fList.getSelectionIndices();
		Object[] elements = new Object[indices.length];

		for (int i = 0; i != indices.length; i++)
			elements[i] =
				fElements[fFilteredIndices[fFoldedIndices[indices[i]]]];

		return elements;
	}

	/**
	 * Sets the filter pattern. Current only prefix filter patterns are supported.
	 * @param filter the filter pattern.
	 */
	public void setFilter(String filter) {
		fFilter = (filter == null) ? "" : filter; //$NON-NLS-1$

		updateList();
	}

	private void updateList() {
		fFilteredCount = filter();
		fFoldedCount = fold();

		if (fUpdateJob != null)
			fUpdateJob.cancel();
		fUpdateJob = new TableUpdateJob(fList, fFoldedCount);
		fUpdateJob.schedule();
	}

	/**
	 * Returns the filter pattern.
	 * @return returns the filter pattern.
	 */
	public String getFilter() {
		return fFilter;
	}

	/**
	 * Returns all elements which are folded together to one entry in the list.
	 * @param  index the index selecting the entry in the list.
	 * @return returns an array of elements folded together, <code>null</code> if index is out of range.
	 */
	public Object[] getFoldedElements(int index) {
		if ((index < 0) || (index >= fFoldedCount))
			return null;

		int start = fFoldedIndices[index];
		int count =
			(index == fFoldedCount - 1)
				? fFilteredCount - start
				: fFoldedIndices[index + 1] - start;

		Object[] elements = new Object[count];
		for (int i = 0; i != count; i++)
			elements[i] = fElements[fFilteredIndices[start + i]];

		return elements;
	}

	/*
	 * Folds duplicate entries. Two elements are considered as a pair of
	 * duplicates if they coiincide in the rendered string and image.
	 * @return returns the number of elements after folding.
	 */
	private int fold() {
		if (fAllowDuplicates) {
			for (int i = 0; i != fFilteredCount; i++)
				fFoldedIndices[i] = i; // identity mapping

			return fFilteredCount;

		} else {
			int k = 0;
			Label last = null;
			for (int i = 0; i != fFilteredCount; i++) {
				int j = fFilteredIndices[i];

				Label current = fLabels[j];
				if (!current.equals(last)) {
					fFoldedIndices[k] = i;
					k++;
					last = current;
				}
			}
			return k;
		}
	}

	/*
	 * Filters the list with the filter pattern.
	 * @return returns the number of elements after filtering.
	 */
	private int filter() {
		if (((fFilter == null) || (fFilter.length() == 0))
			&& !fMatchEmptyString)
			return 0;

		fFilterMatcher.setFilter(fFilter.trim(), fIgnoreCase, false);

		int k = 0;
		for (int i = 0; i != fElements.length; i++) {
			if (fFilterMatcher.match(fElements[i]))
				fFilteredIndices[k++] = i;
		}

		return k;
	}

	private class TableUpdateJob extends WorkbenchJob {
		final Table fTable;
		final int fCount;
		private int currentIndex = 0 ;
		int [] indicesToSelect;

		public TableUpdateJob(Table table, int count) {
			super(WorkbenchMessages.getString("FilteredList.UpdateJobName")); //$NON-NLS-1$
			setSystem(true);
			fTable = table;
			fCount = count;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus runInUIThread(IProgressMonitor monitor) {

			if (fTable.isDisposed())
				return Status.CANCEL_STATUS;

			int itemCount = fTable.getItemCount();
			
			//Remove excess items
			if (fCount < itemCount) {
				fTable.setRedraw(false);
				fTable.remove(fCount, itemCount - 1);
				fTable.setRedraw(true);
				itemCount = fTable.getItemCount();
			}

			// table empty -> no selection
			if (fCount == 0){
				fTable.notifyListeners(SWT.Selection, new Event());
				return Status.OK_STATUS;
			}
		
			
			//How many we are going to do this time.
			int iterations = Math.min(10, fCount - currentIndex);
			
			for (int i  = 0; i < iterations; i++) {
				
				final TableItem item =
					(currentIndex < itemCount)
						? fTable.getItem(currentIndex)
						: new TableItem(fTable, SWT.NONE);

				final Label label =
					fLabels[fFilteredIndices[fFoldedIndices[currentIndex]]];

				item.setText(label.string);
				item.setImage(label.image);

				currentIndex ++;
			}		
			
			if(currentIndex < fCount)
				schedule(100);
			else{
				if (indicesToSelect == null) {
					if (fCount > 0)
						defaultSelect();
				} else
					updateSelection(indicesToSelect);
			}
				
			return Status.OK_STATUS;
		
		}
		
		/**
		 * Update the selection for the supplied indices.
		 */
		void updateSelection(final int[] indices) {

			indicesToSelect = indices;
		}

		/**
		 * Select the first element if there is no selection
		 */
		private void defaultSelect() {

			/**
			* Reset to the first selection if no
			* index has been queued.
			*/

			selectAndNotify(new int[] { 0 });

		}

		/**
		 * Select the supplied indices and notify any listeners
		 * @param indices
		 */
		private void selectAndNotify(final int[] indices) {

			//It is possible that the table was disposed
			//before the update finished. If so then leave
			if (fTable.isDisposed())
				return;
			fTable.setSelection(indices);
			fTable.notifyListeners(SWT.Selection, new Event());
				

		}

	}

	
	/**
	 * Returns whether or not duplicates are allowed.
	 * 
	 * @return <code>true</code> indicates duplicates are allowed
	 */
	public boolean getAllowDuplicates() {
		return fAllowDuplicates;
	}

	/**
	 * Sets whether or not duplicates are allowed.
	 * If this value is set the items should be set again for this value
	 * to take effect.
	 *
	 * @param allowDuplicates <code>true</code> indicates duplicates are allowed
	 */
	public void setAllowDuplicates(boolean allowDuplicates) {
		this.fAllowDuplicates = allowDuplicates;
	}

	/**
	 * Returns whether or not case should be ignored.
	 * 
	 * @return <code>true</code> if case should be ignored
	 */
	public boolean getIgnoreCase() {
		return fIgnoreCase;
	}

	/**
	 * Sets whether or not case should be ignored
	 * If this value is set the items should be set again for this value 
	 * to take effect.
	 * 
	 * @param ignoreCase <code>true</code> if case should be ignored
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.fIgnoreCase = ignoreCase;
	}

	/**
	 * Returns whether empty filter strings should filter everything or nothing.
	 * 
	 * @return <code>true</code> for the empty string to
	 *   match all items, <code>false</code> to match none
	 */
	public boolean getMatchEmptyString() {
		return fMatchEmptyString;
	}

	/**
	 * Sets whether empty filter strings should filter everything or nothing.
	 * If this value is set the items should be set again for this value 
	 * to take effect.
	 * 
	 * @param matchEmptyString <code>true</code> for the empty string to
	 *   match all items, <code>false</code> to match none
	 */
	public void setMatchEmptyString(boolean matchEmptyString) {
		this.fMatchEmptyString = matchEmptyString;
	}

	/**
	 * Returns the label provider for the items.
	 * 
	 * @return the label provider
	 */
	public ILabelProvider getLabelProvider() {
		return fLabelProvider;
	}

	/**
	 * Sets the label provider.
	 * If this value is set the items should be set again for this value 
	 * to take effect.
	 * 
	 * @param labelProvider the label provider
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.fLabelProvider = labelProvider;
	}

}