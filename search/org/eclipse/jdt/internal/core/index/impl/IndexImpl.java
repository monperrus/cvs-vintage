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
package org.eclipse.jdt.internal.core.index.impl;

import java.io.*;
import java.util.*;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.core.index.*;

/**
 * An Index is used to create an index on the disk, and to make queries. It uses a set of 
 * indexers and a mergeFactory. The index fills an inMemoryIndex up 
 * to it reaches a certain size, and then merges it with a main index on the disk.
 * <br> <br>
 * The changes are only taken into account by the queries after a merge.
 */

public class IndexImpl extends Index {
	/**
	 * Maximum size of the index in memory.
	 */
	public static final int MAX_FOOTPRINT= 10000000;

	/**
	 * Index in memory, who is merged with mainIndex each times it 
	 * reaches a certain size.
	 */
	protected InMemoryIndex addsIndex;
	protected IndexInput addsIndexInput;

	/**
	 * State of the indexGenerator: addsIndex empty <=> MERGED, or
	 * addsIndex not empty <=> CAN_MERGE
	 */
	protected int state;

	/**
	 * Files removed form the addsIndex.
	 */
	protected Map removedInAdds;

	/**
	 * Files removed form the oldIndex.
	 */
	protected Map removedInOld;
	protected static final int CAN_MERGE= 0;
	protected static final int MERGED= 1;
	private File indexFile;

	/**
	 * String representation of this index.
	 */
	public String toString;
	public IndexImpl(File indexDirectory, boolean reuseExistingFile) throws IOException {
		this(indexDirectory,".index", reuseExistingFile); //$NON-NLS-1$
	}
	public IndexImpl(File indexDirectory, String indexName, boolean reuseExistingFile) throws IOException {
		state= MERGED;
		indexFile= new File(indexDirectory, indexName);
		initialize(reuseExistingFile);
	}
	public IndexImpl(String indexName, boolean reuseExistingFile) throws IOException {
		this(indexName, null, reuseExistingFile);
	}
	public IndexImpl(String indexName, String toString, boolean reuseExistingFile) throws IOException {
		state= MERGED;
		indexFile= new File(indexName);
		this.toString = toString;
		initialize(reuseExistingFile);
	}
	public void addIndexEntry(char[] category, char[] key, SearchDocument document) {
		if (document.indexedFile == null)
			throw new IllegalStateException();
		addsIndex.addRef(document.indexedFile, org.eclipse.jdt.core.compiler.CharOperation.concat(category, key));
	}
	/**
	 * Returns true if the index in memory is not empty, so 
	 * merge() can be called to fill the mainIndex with the files and words
	 * contained in the addsIndex. 
	 */
	protected boolean canMerge() {
		return state == CAN_MERGE;
	}
	/**
	 * Initialises the indexGenerator.
	 */
//	public void empty() throws IOException {
//
//		if (indexFile.exists()){
//			indexFile.delete();
//			//initialisation of mainIndex
//			InMemoryIndex mainIndex= new InMemoryIndex();
//			IndexOutput mainIndexOutput= new BlocksIndexOutput(indexFile);
//			if (!indexFile.exists())
//				mainIndex.save(mainIndexOutput);
//		}
//
//		//initialisation of addsIndex
//		addsIndex= new InMemoryIndex();
//		addsIndexInput= new SimpleIndexInput(addsIndex);
//
//		//vectors who keep track of the removed Files
//		removedInAdds= new HashMap(11);
//		removedInOld= new HashMap(11);
//	}
	protected String getDocumentName(int number) {
		// to be supplied by the new Index
		return null;
	}
	public File getIndexFile() {
		return indexFile;
	}
	/**
	 * Returns the path corresponding to a given document number
	 */
//	public String getPath(int documentNumber) throws IOException {
//		//save();
//		IndexInput input= new BlocksIndexInput(indexFile);
//		try {
//			input.open();
//			IndexedFile file = input.getIndexedFile(documentNumber);
//			if (file == null) return null;
//			return file.getPath();
//		} finally {
//			input.close();
//		}		
//	}
	/**
	 * see IIndex.hasChanged
	 */
	public boolean hasChanged() {
		return canMerge();
	}
	/**
	 * Indexes the given document, using the searchParticipant.
	 * If the document already exists in the index, it overrides the previous one.
	 * The changes are taken into account after a merge.
	 */
	public void indexDocument(SearchDocument document, SearchParticipant searchParticipant, IPath indexPath) throws IOException {
		if (timeToMerge())
			merge();

		IndexedFile indexedFile = addsIndex.getIndexedFile(document.getPath());
		if (indexedFile != null)
			remove(indexedFile, MergeFactory.ADDS_INDEX);

		// add the name of the file to the index
		if (document.indexedFile != null)
			throw new IllegalStateException();
		document.indexedFile = addsIndex.addDocument(document);

		searchParticipant.indexDocument(document, indexPath);
		state = CAN_MERGE;
	}
	/**
	 * Initialises the indexGenerator.
	 */
	public void initialize(boolean reuseExistingFile) throws IOException {
		
		//initialisation of addsIndex
		addsIndex= new InMemoryIndex();
		addsIndexInput= new SimpleIndexInput(addsIndex);

		//vectors who keep track of the removed Files
		removedInAdds= new HashMap(11);
		removedInOld= new HashMap(11);

		// check whether existing index file can be read
		if (reuseExistingFile && indexFile.exists()) {
			IndexInput mainIndexInput= new BlocksIndexInput(indexFile);
			try {
				mainIndexInput.open();
			} catch(IOException e) {
				BlocksIndexInput input = (BlocksIndexInput)mainIndexInput;
				try {
					input.opened = true;
					input.close();
				} finally {
					input.opened = false;
				}
				indexFile.delete();
				mainIndexInput = null;
				throw e;
			}
			mainIndexInput.close();
		} else {
			InMemoryIndex mainIndex= new InMemoryIndex();			
			IndexOutput mainIndexOutput= new BlocksIndexOutput(indexFile);
			mainIndex.save(mainIndexOutput);
		}
	}
	/**
	 * Merges the in memory index and the index on the disk, and saves the results on the disk.
	 */
	protected void merge() throws IOException {
		//System.out.println("merge");

		//initialisation of tempIndex
		File tempFile= new File(indexFile.getAbsolutePath() + "TempVA"); //$NON-NLS-1$

		IndexInput mainIndexInput= new BlocksIndexInput(indexFile);
		BlocksIndexOutput tempIndexOutput= new BlocksIndexOutput(tempFile);

		try {
			//invoke a mergeFactory
			new MergeFactory(
				mainIndexInput, 
				addsIndexInput, 
				tempIndexOutput, 
				removedInOld, 
				removedInAdds).merge();
			
			//rename the file created to become the main index
			File mainIndexFile= (File) mainIndexInput.getSource();
			File tempIndexFile= (File) tempIndexOutput.getDestination();
			mainIndexFile.delete();
			tempIndexFile.renameTo(mainIndexFile);
		} finally {		
			//initialise remove vectors and addsindex, and change the state
			removedInAdds.clear();
			removedInOld.clear();
			addsIndex.init();
			addsIndexInput= new SimpleIndexInput(addsIndex);
			state= MERGED;
		}
	}
	public EntryResult[] query(char[][] categories, char[] key, int matchRule) {
		// to be supplied by the new Index
		return new EntryResult[0];
	}
//	public String[] query(String word) throws IOException {
//		//save();
//		IndexInput input= new BlocksIndexInput(indexFile);
//		try {
//			return input.query(word);
//		} finally {
//			input.close();
//		}
//	}
//	public EntryResult[] queryEntries(char[] prefix) throws IOException {
//		//save();
//		IndexInput input= new BlocksIndexInput(indexFile);
//		try {
//			return input.queryEntriesPrefixedBy(prefix);
//		} finally {
//			input.close();
//		}
//	}
	public String[] queryDocumentNames(String word) throws IOException {
		//save();
		IndexInput input= new BlocksIndexInput(indexFile);
		try {
			return input.queryInDocumentNames(word);
		} finally {
			input.close();
		}
	}
//	public String[] queryPrefix(char[] prefix) throws IOException {
//		//save();
//		IndexInput input= new BlocksIndexInput(indexFile);
//		try {
//			return input.queryFilesReferringToPrefix(prefix);
//		} finally {
//			input.close();
//		}
//	}
	public void remove(String documentName) {
		IndexedFile file= addsIndex.getIndexedFile(documentName);
		if (file != null) {
			//the file is in the adds Index, we remove it from this one
			Int lastRemoved= (Int) removedInAdds.get(documentName);
			if (lastRemoved != null) {
				int fileNum= file.getFileNumber();
				if (lastRemoved.value < fileNum)
					lastRemoved.value= fileNum;
			} else
				removedInAdds.put(documentName, new Int(file.getFileNumber()));
		} else {
			//we remove the file from the old index
			removedInOld.put(documentName, new Int(1));
		}
		state= CAN_MERGE;
	}
	/**
	 * Removes the given document from the given index (MergeFactory.ADDS_INDEX for the
	 * in memory index, MergeFactory.OLD_INDEX for the index on the disk).
	 */
	protected void remove(IndexedFile file, int index) {
		String name= file.getPath();
		if (index == MergeFactory.ADDS_INDEX) {
			Int lastRemoved= (Int) removedInAdds.get(name);
			if (lastRemoved != null) {
				if (lastRemoved.value < file.getFileNumber())
					lastRemoved.value= file.getFileNumber();
			} else
				removedInAdds.put(name, new Int(file.getFileNumber()));
		} else if (index == MergeFactory.OLD_INDEX)
			removedInOld.put(name, new Int(1));
		else
			throw new Error();
		state= CAN_MERGE;
	}
	public void save() throws IOException {
		if (canMerge())
			merge();
	}
	/**
	 * Returns true if the in memory index reaches a critical size, 
	 * to merge it with the index on the disk.
	 */
	protected boolean timeToMerge() {
		return (addsIndex.getFootprint() >= MAX_FOOTPRINT);
	}
public String toString() {
	String str = this.toString;
	if (str == null) str = super.toString();
	str += "(length: "+ getIndexFile().length() +")"; //$NON-NLS-1$ //$NON-NLS-2$
	return str;
}
}
