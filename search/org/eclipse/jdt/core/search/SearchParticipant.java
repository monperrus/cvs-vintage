/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.search;

import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.core.JavaModelManager;

/**
 * A search participant describes a particular extension to a generic search mechanism, permitting 
 * combined search actions which will involve all required participants.
 * 
 * A search scope defines which participants are involved. 
 * 
 * A search participant is responsible for holding index files, and selecting the appropriate ones to feed to
 * index queries. It also can map a document path to an actual document (note that documents could live outside
 * the workspace or not exist yet, and thus aren't just resources).
 * @since 3.0
 */
public abstract class SearchParticipant {

	public static final SearchParticipant[] NO_PARTICIPANT = {};

	// A service provided for participants. Its called from indexDocument(SearchDocument document, IPath indexPath)
	public static void addIndexEntry(char[] category, char[] key, SearchDocument document) {
		if (document.index != null)
			document.index.addIndexEntry(category, key, document);
	}
	// A service provided for participants. Its called from indexDocument(SearchDocument document, IPath indexPath)
	public static void removeAllIndexEntries(SearchDocument document) {
// is no-op until new index implementation
//		if (document.index != null)
//			document.index.remove(document.getPath());
	}

	/**
	 * Intermediate notification sent when a given participant is getting involved.
	 */
	public abstract void beginSearching();

	/**
	 * Intermediate notification sent when a given participant is finished to be involved.
	 */
	public abstract void doneSearching();

	/**
	 * Returns a displayable name of this search participant. e.g. "Java".
	 */
	public abstract String getDescription();

	/**
	 * Bind a file to an actual document.
	 */
	public abstract SearchDocument getDocument(IFile file);

	/**
	 * Bind a document path to an actual document. A document path is interpreted by a participant.
	 */
	public abstract SearchDocument getDocument(String documentPath);

	/**
	 * Index the given document.
	 * Implementation should call addIndexEntry(...)
	 * TODO (jerome) improve spec
	 */
	public abstract void indexDocument(SearchDocument document, IPath indexPath);

	/**
	 * Locate the matches in the given documents and report them using the search requestor. 
	 * Note: permits combined match locators (e.g. jsp match locator can preprocess jsp unit contents and feed them
	 * to Java match locator asking for virtual matches by contributing document implementations which do the conversion).
	 * It is assumed that virtual matches are rearranged by requestor for adapting line/source positions before submitting
	 * final results so the provided searchRequestor should intercept virtual matches and do appropriate conversions.
	 */
	public abstract void locateMatches(SearchDocument[] indexMatches, SearchPattern pattern, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException;

	/**
	 * Schedules the indexing of the given document.
	 * Once the document is ready to be indexed, indexDocument(document, indexPath) is called.
	 */
	public void scheduleDocumentIndexing(SearchDocument document, IPath indexPath) {
		JavaModelManager.getJavaModelManager().getIndexManager().scheduleDocumentIndexing(document, indexPath, this);
	}

	/**
	 * Returns the collection of index paths to consider when performing a given search query in a given scope.
	 */
	public abstract IPath[] selectIndexes(SearchPattern query, IJavaSearchScope scope);
}
