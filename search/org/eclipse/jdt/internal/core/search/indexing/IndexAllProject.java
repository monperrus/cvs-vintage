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
package org.eclipse.jdt.internal.core.search.indexing;

import java.io.IOException;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jdt.internal.core.util.Util;

public class IndexAllProject extends IndexRequest {
	IProject project;

	public IndexAllProject(IProject project, IndexManager manager) {
		super(project.getFullPath(), manager);
		this.project = project;
	}
	public boolean equals(Object o) {
		if (o instanceof IndexAllProject)
			return this.project.equals(((IndexAllProject) o).project);
		return false;
	}
	/**
	 * Ensure consistency of a project index. Need to walk all nested resources,
	 * and discover resources which have either been changed, added or deleted
	 * since the index was produced.
	 */
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;
		if (!project.isAccessible()) return true; // nothing to do

		Index index = this.manager.getIndexForUpdate(this.containerPath, true, /*reuse index file*/ true /*create if none*/);
		if (index == null) return true;
		ReadWriteMonitor monitor = index.monitor;
		if (monitor == null) return true; // index got deleted since acquired

		try {
			monitor.enterRead(); // ask permission to read

			String[] paths = index.queryDocumentNames(""); // all file names //$NON-NLS-1$
			int max = paths == null ? 0 : paths.length;
			final SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
			final String OK = "OK"; //$NON-NLS-1$
			final String DELETED = "DELETED"; //$NON-NLS-1$
			for (int i = 0; i < max; i++)
				indexedFileNames.put(paths[i], DELETED);
			final long indexLastModified = max == 0 ? 0L : index.getIndexFile().lastModified();

			JavaProject javaProject = (JavaProject)JavaCore.create(this.project);
			// Do not create marker nor log problems while getting raw classpath (see bug 41859)
			IClasspathEntry[] entries = javaProject.getRawClasspath(false, false);
			IWorkspaceRoot root = this.project.getWorkspace().getRoot();
			for (int i = 0, length = entries.length; i < length; i++) {
				if (this.isCancelled) return false;

				IClasspathEntry entry = entries[i];
				if ((entry.getEntryKind() == IClasspathEntry.CPE_SOURCE)) { // Index only source folders. Libraries are done as a separate job
					IResource sourceFolder = root.findMember(entry.getPath());
					if (sourceFolder != null) {
						
						// collect output locations if source is project (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=32041)
						final HashSet outputs = new HashSet();
						if (sourceFolder.getType() == IResource.PROJECT) {
							// Do not create marker nor log problems while getting output location (see bug 41859)
							outputs.add(javaProject.getOutputLocation(false, false));
							for (int j = 0; j < length; j++) {
								IPath output = entries[j].getOutputLocation();
								if (output != null) {
									outputs.add(output);
								}
							}
						}
						final boolean hasOutputs = !outputs.isEmpty();
						
						final char[][] inclusionPatterns = ((ClasspathEntry) entry).fullInclusionPatternChars();
						final char[][] exclusionPatterns = ((ClasspathEntry) entry).fullExclusionPatternChars();
						if (max == 0) {
							sourceFolder.accept(
								new IResourceProxyVisitor() {
									public boolean visit(IResourceProxy proxy) {
										if (isCancelled) return false;
										switch(proxy.getType()) {
											case IResource.FILE :
												if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName())) {
													IFile file = (IFile) proxy.requestResource();
													if (file.getLocation() == null) return false;
													if (exclusionPatterns != null || inclusionPatterns != null)
														if (Util.isExcluded(file, inclusionPatterns, exclusionPatterns))
															return false;
													indexedFileNames.put(file.getFullPath().toString(), file);
												}
												return false;
											case IResource.FOLDER :
												if (exclusionPatterns != null && inclusionPatterns == null) {
													// if there are inclusion patterns then we must walk the children
													if (Util.isExcluded(proxy.requestFullPath(), inclusionPatterns, exclusionPatterns, true)) 
													    return false;
												}
												if (hasOutputs && outputs.contains(proxy.requestFullPath()))
													return false;
										}
										return true;
									}
								},
								IResource.NONE
							);
						} else {
							sourceFolder.accept(
								new IResourceProxyVisitor() {
									public boolean visit(IResourceProxy proxy) {
										if (isCancelled) return false;
										switch(proxy.getType()) {
											case IResource.FILE :
												if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName())) {
													IFile file = (IFile) proxy.requestResource();
													IPath location = file.getLocation();
													if (location == null) return false;
													if (exclusionPatterns != null || inclusionPatterns != null)
														if (Util.isExcluded(file, inclusionPatterns, exclusionPatterns))
															return false;
													String path = file.getFullPath().toString();
													indexedFileNames.put(path,
														indexedFileNames.get(path) == null || indexLastModified < location.toFile().lastModified()
															? (Object) file
															: (Object) OK);
												}
												return false;
											case IResource.FOLDER :
												if (exclusionPatterns != null || inclusionPatterns != null)
													if (Util.isExcluded(proxy.requestResource(), inclusionPatterns, exclusionPatterns))
														return false;
												if (hasOutputs && outputs.contains(proxy.requestFullPath()))
													return false;
										}
										return true;
									}
								},
								IResource.NONE
							);
						}
					}
				}
			}

			Object[] names = indexedFileNames.keyTable;
			Object[] values = indexedFileNames.valueTable;
			for (int i = 0, length = names.length; i < length; i++) {
				String name = (String) names[i];
				if (name != null) {
					if (this.isCancelled) return false;

					Object value = values[i];
					if (value != OK) {
						if (value == DELETED)
							this.manager.remove(name, this.containerPath);
						else
							this.manager.addSource((IFile) value, this.containerPath);
					}
				}
			}

			// request to save index when all cus have been indexed... also sets state to SAVED_STATE
			this.manager.request(new SaveIndex(this.containerPath, this.manager));
		} catch (CoreException e) {
			if (JobManager.VERBOSE) {
				Util.verbose("-> failed to index " + this.project + " because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.containerPath);
			return false;
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				Util.verbose("-> failed to index " + this.project + " because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.containerPath);
			return false;
		} finally {
			monitor.exitRead(); // free read lock
		}
		return true;
	}
	public int hashCode() {
		return this.project.hashCode();
	}
	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}
	public String toString() {
		return "indexing project " + this.project.getFullPath(); //$NON-NLS-1$
	}
}
