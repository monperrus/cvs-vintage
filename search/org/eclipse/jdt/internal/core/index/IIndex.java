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
package org.eclipse.jdt.internal.core.index;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.internal.core.index.impl.EntryResult;

/**
 * An IIndex is the interface used to generate an index file, and to make queries on
 * this index.
 */

public interface IIndex {
	/**
	 * Adds the given document to the index.
	 */
	void add(SearchDocument document, IIndexer indexer) throws IOException;
	/**
	 * Returns the index file on the disk.
	 */
	File getIndexFile();
	/**
	 * Returns the path corresponding to a given document number
	 */
	String getPath(int documentNumber) throws IOException;
	/**
	 * Ansers true if has some changes to save.
	 */
	boolean hasChanged();
	/**
	 * Returns the paths of the documents containing the given word.
	 */
	String[] query(String word) throws IOException;
	/**
	 * Returns all entries for a given word.
	 */
	EntryResult[] queryEntries(char[] pattern) throws IOException;
	/**
	 * Returns the paths of the documents whose names contain the given word.
	 */
	String[] queryInDocumentNames(String word) throws IOException;
	/**
	 * Returns the paths of the documents containing the given word prefix.
	 */
	String[] queryPrefix(char[] prefix) throws IOException;
	/**
	 * Removes the corresponding document from the index.
	 */
	void remove(String documentName) throws IOException;
	/**
	 * Saves the index on the disk.
	 */
	void save() throws IOException;
}
