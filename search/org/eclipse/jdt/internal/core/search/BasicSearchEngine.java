/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.jdt.internal.core.search.indexing.*;
import org.eclipse.jdt.internal.core.search.matching.*;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;

/**
 * Search basic engine. Public search engine (see {@link org.eclipse.jdt.core.search.SearchEngine}
 * for detailed comment), now uses basic engine functionalities.
 * Note that search basic engine does not implement deprecated functionalities...
 */
public class BasicSearchEngine {

	/*
	 * A default parser to parse non-reconciled working copies
	 */
	private Parser parser;
	private CompilerOptions compilerOptions;

	/*
	 * A list of working copies that take precedence over their original
	 * compilation units.
	 */
	private ICompilationUnit[] workingCopies;

	/*
	 * A working copy owner whose working copies will take precedent over
	 * their original compilation units.
	 */
	private WorkingCopyOwner workingCopyOwner;

	/**
	 * For tracing purpose.
	 */
	public static boolean VERBOSE = false;

	/*
	 * Creates a new search basic engine.
	 */
	public BasicSearchEngine() {
		// will use working copies of PRIMARY owner
	}

	/**
	 * @see SearchEngine#SearchEngine(ICompilationUnit[]) for detailed comment.
	 */
	public BasicSearchEngine(ICompilationUnit[] workingCopies) {
		this.workingCopies = workingCopies;
	}

	char convertTypeKind(int typeDeclarationKind) {
		switch(typeDeclarationKind) {
			case TypeDeclaration.CLASS_DECL : return IIndexConstants.CLASS_SUFFIX;
			case TypeDeclaration.INTERFACE_DECL : return IIndexConstants.INTERFACE_SUFFIX;
			case TypeDeclaration.ENUM_DECL : return IIndexConstants.ENUM_SUFFIX;
			case TypeDeclaration.ANNOTATION_TYPE_DECL : return IIndexConstants.ANNOTATION_TYPE_SUFFIX;
			default : return IIndexConstants.TYPE_SUFFIX;
		}
	}
	/**
	 * @see SearchEngine#SearchEngine(WorkingCopyOwner) for detailed comment.
	 */
	public BasicSearchEngine(WorkingCopyOwner workingCopyOwner) {
		this.workingCopyOwner = workingCopyOwner;
	}

	/**
	 * @see SearchEngine#createHierarchyScope(IType) for detailed comment.
	 */
	public static IJavaSearchScope createHierarchyScope(IType type) throws JavaModelException {
		return createHierarchyScope(type, DefaultWorkingCopyOwner.PRIMARY);
	}

	/**
	 * @see SearchEngine#createHierarchyScope(IType,WorkingCopyOwner) for detailed comment.
	 */
	public static IJavaSearchScope createHierarchyScope(IType type, WorkingCopyOwner owner) throws JavaModelException {
		return new HierarchyScope(type, owner);
	}

	/**
	 * @see SearchEngine#createJavaSearchScope(IJavaElement[]) for detailed comment.
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements) {
		return createJavaSearchScope(elements, true);
	}

	/**
	 * @see SearchEngine#createJavaSearchScope(IJavaElement[], boolean) for detailed comment.
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements, boolean includeReferencedProjects) {
		int includeMask = IJavaSearchScope.SOURCES | IJavaSearchScope.APPLICATION_LIBRARIES | IJavaSearchScope.SYSTEM_LIBRARIES;
		if (includeReferencedProjects) {
			includeMask |= IJavaSearchScope.REFERENCED_PROJECTS;
		}
		return createJavaSearchScope(elements, includeMask);
	}

	/**
	 * @see SearchEngine#createJavaSearchScope(IJavaElement[], int) for detailed comment.
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements, int includeMask) {
		HashSet projectsToBeAdded = new HashSet(2);
		for (int i = 0, length = elements.length; i < length; i++) {
			IJavaElement element = elements[i];
			if (element instanceof JavaProject) {
				projectsToBeAdded.add(element);
			}
		}
		JavaSearchScope scope = new JavaSearchScope();
		for (int i = 0, length = elements.length; i < length; i++) {
			IJavaElement element = elements[i];
			if (element != null) {
				try {
					if (projectsToBeAdded.contains(element)) {
						scope.add((JavaProject)element, includeMask, projectsToBeAdded);
					} else {
						scope.add(element);
					}
				} catch (JavaModelException e) {
					// ignore
				}
			}
		}
		return scope;
	}

	/**
	 * @see SearchEngine#createTypeNameMatch(IType, int) for detailed comment.
	 */
	public static TypeNameMatch createTypeNameMatch(IType type, int modifiers) {
		return new JavaSearchTypeNameMatch(type, modifiers);
	}

	/**
	 * @see SearchEngine#createWorkspaceScope() for detailed comment.
	 */
	public static IJavaSearchScope createWorkspaceScope() {
		return JavaModelManager.getJavaModelManager().getWorkspaceScope();
	}

	/**
	 * Searches for matches to a given query. Search queries can be created using helper
	 * methods (from a String pattern or a Java element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @param scope the search result has to be limited to the given scope
	 * @param requestor a callback object to which each match is reported
	 */
	void findMatches(SearchPattern pattern, SearchParticipant[] participants, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
		try {
			if (VERBOSE) {
				Util.verbose("Searching for pattern: " + pattern.toString()); //$NON-NLS-1$
				Util.verbose(scope.toString());
			}
			if (participants == null) {
				if (VERBOSE) Util.verbose("No participants => do nothing!"); //$NON-NLS-1$
				return;
			}

			/* initialize progress monitor */
			int length = participants.length;
			if (monitor != null)
				monitor.beginTask(Messages.engine_searching, 100 * length);
			IndexManager indexManager = JavaModelManager.getIndexManager();
			requestor.beginReporting();
			for (int i = 0; i < length; i++) {
				if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();

				SearchParticipant participant = participants[i];
				try {
					if (monitor != null) monitor.subTask(Messages.bind(Messages.engine_searching_indexing, new String[] {participant.getDescription()}));
					participant.beginSearching();
					requestor.enterParticipant(participant);
					PathCollector pathCollector = new PathCollector();
					indexManager.performConcurrentJob(
						new PatternSearchJob(pattern, participant, scope, pathCollector),
						IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						monitor==null ? null : new SubProgressMonitor(monitor, 50));
					if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();

					// locate index matches if any (note that all search matches could have been issued during index querying)
					if (monitor != null) monitor.subTask(Messages.bind(Messages.engine_searching_matching, new String[] {participant.getDescription()}));
					String[] indexMatchPaths = pathCollector.getPaths();
					if (indexMatchPaths != null) {
						pathCollector = null; // release
						int indexMatchLength = indexMatchPaths.length;
						SearchDocument[] indexMatches = new SearchDocument[indexMatchLength];
						for (int j = 0; j < indexMatchLength; j++) {
							indexMatches[j] = participant.getDocument(indexMatchPaths[j]);
						}
						SearchDocument[] matches = MatchLocator.addWorkingCopies(pattern, indexMatches, getWorkingCopies(), participant);
						participant.locateMatches(matches, pattern, scope, requestor, monitor==null ? null : new SubProgressMonitor(monitor, 50));
					}
				} finally {
					requestor.exitParticipant(participant);
					participant.doneSearching();
				}
			}
		} finally {
			requestor.endReporting();
			if (monitor != null)
				monitor.done();
		}
	}
	/**
	 * Returns a new default Java search participant.
	 *
	 * @return a new default Java search participant
	 * @since 3.0
	 */
	public static SearchParticipant getDefaultSearchParticipant() {
		return new JavaSearchParticipant();
	}

	/**
	 * @param matchRule
	 */
	public static String getMatchRuleString(final int matchRule) {
		if (matchRule == 0) {
			return "R_EXACT_MATCH"; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		for (int i=1; i<=16; i++) {
			int bit = matchRule & (1<<(i-1));
			if (bit != 0 && buffer.length()>0) buffer.append(" | "); //$NON-NLS-1$
			switch (bit) {
				case SearchPattern.R_PREFIX_MATCH:
					buffer.append("R_PREFIX_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_CASE_SENSITIVE:
					buffer.append("R_CASE_SENSITIVE"); //$NON-NLS-1$
					break;
				case SearchPattern.R_EQUIVALENT_MATCH:
					buffer.append("R_EQUIVALENT_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_ERASURE_MATCH:
					buffer.append("R_ERASURE_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_FULL_MATCH:
					buffer.append("R_FULL_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_PATTERN_MATCH:
					buffer.append("R_PATTERN_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_REGEXP_MATCH:
					buffer.append("R_REGEXP_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_CAMELCASE_MATCH:
					buffer.append("R_CAMELCASE_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH:
					buffer.append("R_CAMELCASE_SAME_PART_COUNT_MATCH"); //$NON-NLS-1$
					break;
			}
		}
		return buffer.toString();
	}

	/**
	 * Return kind of search corresponding to given value.
	 *
	 * @param searchFor
	 */
	public static String getSearchForString(final int searchFor) {
		switch (searchFor) {
			case IJavaSearchConstants.TYPE:
				return ("TYPE"); //$NON-NLS-1$
			case IJavaSearchConstants.METHOD:
				return ("METHOD"); //$NON-NLS-1$
			case IJavaSearchConstants.PACKAGE:
				return ("PACKAGE"); //$NON-NLS-1$
			case IJavaSearchConstants.CONSTRUCTOR:
				return ("CONSTRUCTOR"); //$NON-NLS-1$
			case IJavaSearchConstants.FIELD:
				return ("FIELD"); //$NON-NLS-1$
			case IJavaSearchConstants.CLASS:
				return ("CLASS"); //$NON-NLS-1$
			case IJavaSearchConstants.INTERFACE:
				return ("INTERFACE"); //$NON-NLS-1$
			case IJavaSearchConstants.ENUM:
				return ("ENUM"); //$NON-NLS-1$
			case IJavaSearchConstants.ANNOTATION_TYPE:
				return ("ANNOTATION_TYPE"); //$NON-NLS-1$
			case IJavaSearchConstants.CLASS_AND_ENUM:
				return ("CLASS_AND_ENUM"); //$NON-NLS-1$
			case IJavaSearchConstants.CLASS_AND_INTERFACE:
				return ("CLASS_AND_INTERFACE"); //$NON-NLS-1$
			case IJavaSearchConstants.INTERFACE_AND_ANNOTATION:
				return ("INTERFACE_AND_ANNOTATION"); //$NON-NLS-1$
		}
		return "UNKNOWN"; //$NON-NLS-1$
	}

	private Parser getParser() {
		if (this.parser == null) {
			this.compilerOptions = new CompilerOptions(JavaCore.getOptions());
			ProblemReporter problemReporter =
				new ProblemReporter(
					DefaultErrorHandlingPolicies.proceedWithAllProblems(),
					this.compilerOptions,
					new DefaultProblemFactory());
			this.parser = new Parser(problemReporter, true);
		}
		return this.parser;
	}

	/*
	 * Returns the list of working copies used by this search engine.
	 * Returns null if none.
	 */
	private ICompilationUnit[] getWorkingCopies() {
		ICompilationUnit[] copies;
		if (this.workingCopies != null) {
			if (this.workingCopyOwner == null) {
				copies = JavaModelManager.getJavaModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
				if (copies == null) {
					copies = this.workingCopies;
				} else {
					HashMap pathToCUs = new HashMap();
					for (int i = 0, length = copies.length; i < length; i++) {
						ICompilationUnit unit = copies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					for (int i = 0, length = this.workingCopies.length; i < length; i++) {
						ICompilationUnit unit = this.workingCopies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					int length = pathToCUs.size();
					copies = new ICompilationUnit[length];
					pathToCUs.values().toArray(copies);
				}
			} else {
				copies = this.workingCopies;
			}
		} else if (this.workingCopyOwner != null) {
			copies = JavaModelManager.getJavaModelManager().getWorkingCopies(this.workingCopyOwner, true/*add primary WCs*/);
		} else {
			copies = JavaModelManager.getJavaModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
		}
		if (copies == null) return null;

		// filter out primary working copies that are saved
		ICompilationUnit[] result = null;
		int length = copies.length;
		int index = 0;
		for (int i = 0; i < length; i++) {
			CompilationUnit copy = (CompilationUnit)copies[i];
			try {
				if (!copy.isPrimary()
						|| copy.hasUnsavedChanges()
						|| copy.hasResourceChanged()) {
					if (result == null) {
						result = new ICompilationUnit[length];
					}
					result[index++] = copy;
				}
			}  catch (JavaModelException e) {
				// copy doesn't exist: ignore
			}
		}
		if (index != length && result != null) {
			System.arraycopy(result, 0, result = new ICompilationUnit[index], 0, index);
		}
		return result;
	}

	/*
	 * Returns the working copy to use to do the search on the given Java element.
	 */
	private ICompilationUnit[] getWorkingCopies(IJavaElement element) {
		if (element instanceof IMember) {
			ICompilationUnit cu = ((IMember)element).getCompilationUnit();
			if (cu != null && cu.isWorkingCopy()) {
				return new ICompilationUnit[] { cu };
			}
		} else if (element instanceof ICompilationUnit) {
			return new ICompilationUnit[] { (ICompilationUnit) element };
		}

		return null;
	}

	boolean match(char patternTypeSuffix, int modifiers) {
		switch(patternTypeSuffix) {
			case IIndexConstants.CLASS_SUFFIX :
				return (modifiers & (Flags.AccAnnotation | Flags.AccInterface | Flags.AccEnum)) == 0;
			case IIndexConstants.CLASS_AND_INTERFACE_SUFFIX:
				return (modifiers & (Flags.AccAnnotation | Flags.AccEnum)) == 0;
			case IIndexConstants.CLASS_AND_ENUM_SUFFIX:
				return (modifiers & (Flags.AccAnnotation | Flags.AccInterface)) == 0;
			case IIndexConstants.INTERFACE_SUFFIX :
				return (modifiers & Flags.AccInterface) != 0;
			case IIndexConstants.INTERFACE_AND_ANNOTATION_SUFFIX:
				return (modifiers & (Flags.AccInterface | Flags.AccAnnotation)) != 0;
			case IIndexConstants.ENUM_SUFFIX :
				return (modifiers & Flags.AccEnum) != 0;
			case IIndexConstants.ANNOTATION_TYPE_SUFFIX :
				return (modifiers & Flags.AccAnnotation) != 0;
		}
		return true;
	}
	
	boolean match(char[] patternPkg, char[] patternTypeName, int matchRule, char[] pkg, char[] typeName) {
		
		boolean isCaseSensitive = (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
		
		if (patternPkg != null && !CharOperation.equals(patternPkg, pkg, true))
			return false;
		
		if (patternTypeName != null) {
			boolean isCamelCase = (matchRule & (SearchPattern.R_CAMELCASE_MATCH | SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH)) != 0;
			int matchMode = matchRule & JavaSearchPattern.MATCH_MODE_MASK;
			if (!isCaseSensitive && !isCamelCase) {
				patternTypeName = CharOperation.toLowerCase(patternTypeName);
			}
			boolean matchFirstChar = !isCaseSensitive || patternTypeName[0] == typeName[0];
			switch(matchMode) {
				case SearchPattern.R_EXACT_MATCH :
					return matchFirstChar && CharOperation.equals(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_PREFIX_MATCH :
					return matchFirstChar && CharOperation.prefixEquals(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_PATTERN_MATCH :
					return CharOperation.match(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_REGEXP_MATCH :
					// TODO (frederic) implement regular expression match
					break;
				case SearchPattern.R_CAMELCASE_MATCH:
					if (matchFirstChar && CharOperation.camelCaseMatch(patternTypeName, typeName, false)) {
						return true;
					}
					return !isCaseSensitive && matchFirstChar && CharOperation.prefixEquals(patternTypeName, typeName, false);
				case SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH:
					return matchFirstChar && CharOperation.camelCaseMatch(patternTypeName, typeName, true);
			}
		}
		return true;

	}

	boolean match(char patternTypeSuffix, char[] patternPkg, char[] patternTypeName, int matchRule, int typeKind, char[] pkg, char[] typeName) {
		switch(patternTypeSuffix) {
			case IIndexConstants.CLASS_SUFFIX :
				if (typeKind != TypeDeclaration.CLASS_DECL) return false;
				break;
			case IIndexConstants.CLASS_AND_INTERFACE_SUFFIX:
				if (typeKind != TypeDeclaration.CLASS_DECL && typeKind != TypeDeclaration.INTERFACE_DECL) return false;
				break;
			case IIndexConstants.CLASS_AND_ENUM_SUFFIX:
				if (typeKind != TypeDeclaration.CLASS_DECL && typeKind != TypeDeclaration.ENUM_DECL) return false;
				break;
			case IIndexConstants.INTERFACE_SUFFIX :
				if (typeKind != TypeDeclaration.INTERFACE_DECL) return false;
				break;
			case IIndexConstants.INTERFACE_AND_ANNOTATION_SUFFIX:
				if (typeKind != TypeDeclaration.INTERFACE_DECL && typeKind != TypeDeclaration.ANNOTATION_TYPE_DECL) return false;
				break;
			case IIndexConstants.ENUM_SUFFIX :
				if (typeKind != TypeDeclaration.ENUM_DECL) return false;
				break;
			case IIndexConstants.ANNOTATION_TYPE_SUFFIX :
				if (typeKind != TypeDeclaration.ANNOTATION_TYPE_DECL) return false;
				break;
			case IIndexConstants.TYPE_SUFFIX : // nothing
		}

		boolean isCaseSensitive = (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
		if (patternPkg != null && !CharOperation.equals(patternPkg, pkg, isCaseSensitive))
				return false;

		if (patternTypeName != null) {
			boolean isCamelCase = (matchRule & (SearchPattern.R_CAMELCASE_MATCH | SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH)) != 0;
			int matchMode = matchRule & JavaSearchPattern.MATCH_MODE_MASK;
			if (!isCaseSensitive && !isCamelCase) {
				patternTypeName = CharOperation.toLowerCase(patternTypeName);
			}
			boolean matchFirstChar = !isCaseSensitive || patternTypeName[0] == typeName[0];
			switch(matchMode) {
				case SearchPattern.R_EXACT_MATCH :
					return matchFirstChar && CharOperation.equals(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_PREFIX_MATCH :
					return matchFirstChar && CharOperation.prefixEquals(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_PATTERN_MATCH :
					return CharOperation.match(patternTypeName, typeName, isCaseSensitive);
				case SearchPattern.R_REGEXP_MATCH :
					// TODO (frederic) implement regular expression match
					break;
				case SearchPattern.R_CAMELCASE_MATCH:
					if (matchFirstChar && CharOperation.camelCaseMatch(patternTypeName, typeName, false)) {
						return true;
					}
					return !isCaseSensitive && matchFirstChar && CharOperation.prefixEquals(patternTypeName, typeName, false);
				case SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH:
					return matchFirstChar && CharOperation.camelCaseMatch(patternTypeName, typeName, true);
			}
		}
		return true;

	}

	/**
	 * Searches for matches of a given search pattern. Search patterns can be created using helper
	 * methods (from a String pattern or a Java element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @see SearchEngine#search(SearchPattern, SearchParticipant[], IJavaSearchScope, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void search(SearchPattern pattern, SearchParticipant[] participants, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.search(SearchPattern, SearchParticipant[], IJavaSearchScope, SearchRequestor, IProgressMonitor)"); //$NON-NLS-1$
		}
		findMatches(pattern, participants, scope, requestor, monitor);
	}
	
	public void searchAllConstructorDeclarations(
		final char[] packageName,
		final char[] typeName,
		final int typeMatchRule,
		IJavaSearchScope scope,
		final IRestrictedAccessConstructorRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws JavaModelException {

		// Validate match rule first
		final int validatedTypeMatchRule = SearchPattern.validateMatchRule(typeName == null ? null : new String (typeName), typeMatchRule);

		// Debug
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchAllConstructorDeclarations(char[], char[], int, IJavaSearchScope, IRestrictedAccessConstructorRequestor, int, IProgressMonitor)"); //$NON-NLS-1$
			Util.verbose("	- package name: "+(packageName==null?"null":new String(packageName))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- type name: "+(typeName==null?"null":new String(typeName))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- type match rule: "+getMatchRuleString(typeMatchRule)); //$NON-NLS-1$
			if (validatedTypeMatchRule != typeMatchRule) {
				Util.verbose("	- validated type match rule: "+getMatchRuleString(validatedTypeMatchRule)); //$NON-NLS-1$
			}
			Util.verbose("	- scope: "+scope); //$NON-NLS-1$
		}
		if (validatedTypeMatchRule == -1) return; // invalid match rule => return no results

		// Create pattern
		IndexManager indexManager = JavaModelManager.getIndexManager();
		final ConstructorDeclarationPattern pattern = new ConstructorDeclarationPattern(
				packageName,
				typeName,
				validatedTypeMatchRule);

		// Get working copy path(s). Store in a single string in case of only one to optimize comparison in requestor
		final HashSet workingCopyPaths = new HashSet();
		String workingCopyPath = null;
		ICompilationUnit[] copies = getWorkingCopies();
		final int copiesLength = copies == null ? 0 : copies.length;
		if (copies != null) {
			if (copiesLength == 1) {
				workingCopyPath = copies[0].getPath().toString();
			} else {
				for (int i = 0; i < copiesLength; i++) {
					ICompilationUnit workingCopy = copies[i];
					workingCopyPaths.add(workingCopy.getPath().toString());
				}
			}
		}
		final String singleWkcpPath = workingCopyPath;

		// Index requestor
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor(){
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant, AccessRuleSet access) {
				// Filter unexpected types
				ConstructorDeclarationPattern record = (ConstructorDeclarationPattern)indexRecord;
				
				if ((record.extraFlags & ExtraFlags.IsMemberType) != 0) {
					return true; // filter out member classes
				}
				if ((record.extraFlags & ExtraFlags.IsLocalType) != 0) {
					return true; // filter out local and anonymous classes
				}
				switch (copiesLength) {
					case 0:
						break;
					case 1:
						if (singleWkcpPath.equals(documentPath)) {
							return true; // filter out *the* working copy
						}
						break;
					default:
						if (workingCopyPaths.contains(documentPath)) {
							return true; // filter out working copies
						}
						break;
				}

				// Accept document path
				AccessRestriction accessRestriction = null;
				if (access != null) {
					// Compute document relative path
					int pkgLength = (record.declaringPackageName==null || record.declaringPackageName.length==0) ? 0 : record.declaringPackageName.length+1;
					int nameLength = record.declaringSimpleName==null ? 0 : record.declaringSimpleName.length;
					char[] path = new char[pkgLength+nameLength];
					int pos = 0;
					if (pkgLength > 0) {
						System.arraycopy(record.declaringPackageName, 0, path, pos, pkgLength-1);
						CharOperation.replace(path, '.', '/');
						path[pkgLength-1] = '/';
						pos += pkgLength;
					}
					if (nameLength > 0) {
						System.arraycopy(record.declaringSimpleName, 0, path, pos, nameLength);
						pos += nameLength;
					}
					// Update access restriction if path is not empty
					if (pos > 0) {
						accessRestriction = access.getViolatedRestriction(path);
					}
				}
				nameRequestor.acceptConstructor(
						record.modifiers,
						record.declaringSimpleName,
						record.parameterCount,
						record.signature,
						record.parameterTypes,
						record.parameterNames,
						record.declaringTypeModifiers,
						record.declaringPackageName,
						record.extraFlags,
						documentPath,
						accessRestriction);
				return true;
			}
		};

		try {
			if (progressMonitor != null) {
				progressMonitor.beginTask(Messages.engine_searching, 1000);
			}
			// add type names from indexes
			indexManager.performConcurrentJob(
				new PatternSearchJob(
					pattern,
					getDefaultSearchParticipant(), // Java search only
					scope,
					searchRequestor),
				waitingPolicy,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 1000-copiesLength));

			// add type names from working copies
			if (copies != null) {
				for (int i = 0; i < copiesLength; i++) {
					final ICompilationUnit workingCopy = copies[i];
					if (!scope.encloses(workingCopy)) continue;
					final String path = workingCopy.getPath().toString();
					if (workingCopy.isConsistent()) {
						IPackageDeclaration[] packageDeclarations = workingCopy.getPackageDeclarations();
						char[] packageDeclaration = packageDeclarations.length == 0 ? CharOperation.NO_CHAR : packageDeclarations[0].getElementName().toCharArray();
						IType[] allTypes = workingCopy.getAllTypes();
						for (int j = 0, allTypesLength = allTypes.length; j < allTypesLength; j++) {
							IType type = allTypes[j];
							char[] simpleName = type.getElementName().toCharArray();
							if (match(packageName, typeName, validatedTypeMatchRule, packageDeclaration, simpleName) && !type.isMember()) {
								
								int extraFlags = ExtraFlags.getExtraFlags(type);
								
								boolean hasConstructor = false;
								
								IMethod[] methods = type.getMethods();
								for (int k = 0; k < methods.length; k++) {
									IMethod method = methods[k];
									if (method.isConstructor()) {
										hasConstructor = true;
										
										String[] stringParameterNames = method.getParameterNames();
										String[] stringParameterTypes = method.getParameterTypes();
										int length = stringParameterNames.length;
										char[][] parameterNames = new char[length][];
										char[][] parameterTypes = new char[length][];
										for (int l = 0; l < length; l++) {
											parameterNames[l] = stringParameterNames[l].toCharArray();
											parameterTypes[l] = Signature.toCharArray(Signature.getTypeErasure(stringParameterTypes[l]).toCharArray());
										}
										
										nameRequestor.acceptConstructor(
												method.getFlags(),
												simpleName,
												parameterNames.length,
												null,// signature is not used for source type
												parameterTypes, 
												parameterNames,
												type.getFlags(),
												packageDeclaration,
												extraFlags,
												path,
												null);
									}
								}
								
								if (!hasConstructor) {
									nameRequestor.acceptConstructor(
											Flags.AccPublic,
											simpleName,
											-1,
											null, // signature is not used for source type
											CharOperation.NO_CHAR_CHAR,
											CharOperation.NO_CHAR_CHAR,
											type.getFlags(),
											packageDeclaration,
											extraFlags,
											path,
											null);
								}
							}
						}
					} else {
						Parser basicParser = getParser();
						org.eclipse.jdt.internal.compiler.env.ICompilationUnit unit = (org.eclipse.jdt.internal.compiler.env.ICompilationUnit) workingCopy;
						CompilationResult compilationUnitResult = new CompilationResult(unit, 0, 0, this.compilerOptions.maxProblemsPerUnit);
						CompilationUnitDeclaration parsedUnit = basicParser.dietParse(unit, compilationUnitResult);
						if (parsedUnit != null) {
							final char[] packageDeclaration = parsedUnit.currentPackage == null ? CharOperation.NO_CHAR : CharOperation.concatWith(parsedUnit.currentPackage.getImportName(), '.');
							class AllConstructorDeclarationsVisitor extends ASTVisitor {
								private TypeDeclaration[] declaringTypes = new TypeDeclaration[0];
								private int declaringTypesPtr = -1;
								
								private void endVisit(TypeDeclaration typeDeclaration) {
									if (!hasConstructor(typeDeclaration) && typeDeclaration.enclosingType == null) {
									
										if (match(packageName, typeName, validatedTypeMatchRule, packageDeclaration, typeDeclaration.name)) {
											nameRequestor.acceptConstructor(
													Flags.AccPublic,
													typeName,
													-1,
													null, // signature is not used for source type
													CharOperation.NO_CHAR_CHAR,
													CharOperation.NO_CHAR_CHAR,
													typeDeclaration.modifiers,
													packageDeclaration,
													ExtraFlags.getExtraFlags(typeDeclaration),
													path,
													null);
										}
									}
									
									this.declaringTypes[this.declaringTypesPtr] = null;
									this.declaringTypesPtr--;
								}
								
								public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope s) {
									endVisit(typeDeclaration);
								}
								
								public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope s) {
									endVisit(memberTypeDeclaration);
								}
								
								private boolean hasConstructor(TypeDeclaration typeDeclaration) {
									AbstractMethodDeclaration[] methods = typeDeclaration.methods;
									int length = methods == null ? 0 : methods.length;
									for (int j = 0; j < length; j++) {
										if (methods[j].isConstructor()) {
											return true;
										}
									}
									
									return false;
								}
								public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope classScope) {
									TypeDeclaration typeDeclaration = this.declaringTypes[this.declaringTypesPtr];
									if (match(packageName, typeName, validatedTypeMatchRule, packageDeclaration, typeDeclaration.name)) {
										Argument[] arguments = constructorDeclaration.arguments;
										int length = arguments == null ? 0 : arguments.length;
										char[][] parameterNames = new char[length][];
										char[][] parameterTypes = new char[length][];
										for (int l = 0; l < length; l++) {
											Argument argument = arguments[l];
											parameterNames[l] = argument.name;
											if (argument.type instanceof SingleTypeReference) {
												parameterTypes[l] = ((SingleTypeReference)argument.type).token;
											} else {
												parameterTypes[l] = CharOperation.concatWith(((QualifiedTypeReference)argument.type).tokens, '.');
											}
										}
										
										TypeDeclaration enclosing = typeDeclaration.enclosingType;
										char[][] enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
										while (enclosing != null) {
											enclosingTypeNames = CharOperation.arrayConcat(new char[][] {enclosing.name}, enclosingTypeNames);
											if ((enclosing.bits & ASTNode.IsMemberType) != 0) {
												enclosing = enclosing.enclosingType;
											} else {
												enclosing = null;
											}
										}
										
										nameRequestor.acceptConstructor(
												constructorDeclaration.modifiers,
												typeName,
												parameterNames.length,
												null, // signature is not used for source type
												parameterTypes,
												parameterNames,
												typeDeclaration.modifiers,
												packageDeclaration,
												ExtraFlags.getExtraFlags(typeDeclaration),
												path,
												null);
									}
									return false; // no need to find constructors from local/anonymous type
								}
								public boolean visit(TypeDeclaration typeDeclaration, BlockScope blockScope) {
									return false; 
								}
								
								private boolean visit(TypeDeclaration typeDeclaration) {
									if(this.declaringTypes.length <= ++this.declaringTypesPtr) {
										int length = this.declaringTypesPtr;
										System.arraycopy(this.declaringTypes, 0, this.declaringTypes = new TypeDeclaration[length * 2 + 1], 0, length);
									}
									this.declaringTypes[this.declaringTypesPtr] = typeDeclaration;
									return true;
								}
								
								public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope s) {
									return visit(typeDeclaration);
								}
								
								public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope s) {
									return visit(memberTypeDeclaration);
								}
							}
							parsedUnit.traverse(new AllConstructorDeclarationsVisitor(), parsedUnit.scope);
						}
					}
					if (progressMonitor != null) {
						if (progressMonitor.isCanceled()) throw new OperationCanceledException();
						progressMonitor.worked(1);
					}
				}
			}
		} finally {
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		}
	}

	/**
	 * Searches for all secondary types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes).
	 */
	public void searchAllSecondaryTypeNames(
			IPackageFragmentRoot[] sourceFolders,
			final IRestrictedAccessTypeRequestor nameRequestor,
			boolean waitForIndexes,
			IProgressMonitor progressMonitor)  throws JavaModelException {

		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchAllSecondaryTypeNames(IPackageFragmentRoot[], IRestrictedAccessTypeRequestor, boolean, IProgressMonitor)"); //$NON-NLS-1$
			StringBuffer buffer = new StringBuffer("	- source folders: "); //$NON-NLS-1$
			int length = sourceFolders.length;
			for (int i=0; i<length; i++) {
				if (i==0) {
					buffer.append('[');
				} else {
					buffer.append(',');
				}
				buffer.append(sourceFolders[i].getElementName());
			}
			buffer.append("]\n	- waitForIndexes: "); //$NON-NLS-1$
			buffer.append(waitForIndexes);
			Util.verbose(buffer.toString());
		}

		IndexManager indexManager = JavaModelManager.getIndexManager();
		final TypeDeclarationPattern pattern = new SecondaryTypeDeclarationPattern();

		// Get working copy path(s). Store in a single string in case of only one to optimize comparison in requestor
		final HashSet workingCopyPaths = new HashSet();
		String workingCopyPath = null;
		ICompilationUnit[] copies = getWorkingCopies();
		final int copiesLength = copies == null ? 0 : copies.length;
		if (copies != null) {
			if (copiesLength == 1) {
				workingCopyPath = copies[0].getPath().toString();
			} else {
				for (int i = 0; i < copiesLength; i++) {
					ICompilationUnit workingCopy = copies[i];
					workingCopyPaths.add(workingCopy.getPath().toString());
				}
			}
		}
		final String singleWkcpPath = workingCopyPath;

		// Index requestor
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor(){
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant, AccessRuleSet access) {
				// Filter unexpected types
				TypeDeclarationPattern record = (TypeDeclarationPattern)indexRecord;
				if (!record.secondary) {
					return true; // filter maint types
				}
				if (record.enclosingTypeNames == IIndexConstants.ONE_ZERO_CHAR) {
					return true; // filter out local and anonymous classes
				}
				switch (copiesLength) {
					case 0:
						break;
					case 1:
						if (singleWkcpPath.equals(documentPath)) {
							return true; // fliter out *the* working copy
						}
						break;
					default:
						if (workingCopyPaths.contains(documentPath)) {
							return true; // filter out working copies
						}
						break;
				}

				// Accept document path
				AccessRestriction accessRestriction = null;
				if (access != null) {
					// Compute document relative path
					int pkgLength = (record.pkg==null || record.pkg.length==0) ? 0 : record.pkg.length+1;
					int nameLength = record.simpleName==null ? 0 : record.simpleName.length;
					char[] path = new char[pkgLength+nameLength];
					int pos = 0;
					if (pkgLength > 0) {
						System.arraycopy(record.pkg, 0, path, pos, pkgLength-1);
						CharOperation.replace(path, '.', '/');
						path[pkgLength-1] = '/';
						pos += pkgLength;
					}
					if (nameLength > 0) {
						System.arraycopy(record.simpleName, 0, path, pos, nameLength);
						pos += nameLength;
					}
					// Update access restriction if path is not empty
					if (pos > 0) {
						accessRestriction = access.getViolatedRestriction(path);
					}
				}
				nameRequestor.acceptType(record.modifiers, record.pkg, record.simpleName, record.enclosingTypeNames, documentPath, accessRestriction);
				return true;
			}
		};

		// add type names from indexes
		try {
			if (progressMonitor != null) {
				progressMonitor.beginTask(Messages.engine_searching, 100);
			}
			indexManager.performConcurrentJob(
				new PatternSearchJob(
					pattern,
					getDefaultSearchParticipant(), // Java search only
					createJavaSearchScope(sourceFolders),
					searchRequestor),
				waitForIndexes
					? IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH
					: IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 100));
		} catch (OperationCanceledException oce) {
			// do nothing
		} finally {
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		}
	}

	/**
	 * Searches for all top-level types and member types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes).
	 *
	 * @see SearchEngine#searchAllTypeNames(char[], int, char[], int, int, IJavaSearchScope, TypeNameRequestor, int, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void searchAllTypeNames(
		final char[] packageName,
		final int packageMatchRule,
		final char[] typeName,
		final int typeMatchRule,
		int searchFor,
		IJavaSearchScope scope,
		final IRestrictedAccessTypeRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws JavaModelException {

		// Validate match rule first
		final int validatedTypeMatchRule = SearchPattern.validateMatchRule(typeName == null ? null : new String (typeName), typeMatchRule);

		// Debug
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchAllTypeNames(char[], char[], int, int, IJavaSearchScope, IRestrictedAccessTypeRequestor, int, IProgressMonitor)"); //$NON-NLS-1$
			Util.verbose("	- package name: "+(packageName==null?"null":new String(packageName))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- package match rule: "+getMatchRuleString(packageMatchRule)); //$NON-NLS-1$
			Util.verbose("	- type name: "+(typeName==null?"null":new String(typeName))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- type match rule: "+getMatchRuleString(typeMatchRule)); //$NON-NLS-1$
			if (validatedTypeMatchRule != typeMatchRule) {
				Util.verbose("	- validated type match rule: "+getMatchRuleString(validatedTypeMatchRule)); //$NON-NLS-1$
			}
			Util.verbose("	- search for: "+searchFor); //$NON-NLS-1$
			Util.verbose("	- scope: "+scope); //$NON-NLS-1$
		}
		if (validatedTypeMatchRule == -1) return; // invalid match rule => return no results

		// Create pattern
		IndexManager indexManager = JavaModelManager.getIndexManager();
		final char typeSuffix;
		switch(searchFor){
			case IJavaSearchConstants.CLASS :
				typeSuffix = IIndexConstants.CLASS_SUFFIX;
				break;
			case IJavaSearchConstants.CLASS_AND_INTERFACE :
				typeSuffix = IIndexConstants.CLASS_AND_INTERFACE_SUFFIX;
				break;
			case IJavaSearchConstants.CLASS_AND_ENUM :
				typeSuffix = IIndexConstants.CLASS_AND_ENUM_SUFFIX;
				break;
			case IJavaSearchConstants.INTERFACE :
				typeSuffix = IIndexConstants.INTERFACE_SUFFIX;
				break;
			case IJavaSearchConstants.INTERFACE_AND_ANNOTATION :
				typeSuffix = IIndexConstants.INTERFACE_AND_ANNOTATION_SUFFIX;
				break;
			case IJavaSearchConstants.ENUM :
				typeSuffix = IIndexConstants.ENUM_SUFFIX;
				break;
			case IJavaSearchConstants.ANNOTATION_TYPE :
				typeSuffix = IIndexConstants.ANNOTATION_TYPE_SUFFIX;
				break;
			default :
				typeSuffix = IIndexConstants.TYPE_SUFFIX;
				break;
		}
		final TypeDeclarationPattern pattern = packageMatchRule == SearchPattern.R_EXACT_MATCH
			? new TypeDeclarationPattern(
				packageName,
				null,
				typeName,
				typeSuffix,
				validatedTypeMatchRule)
			: new QualifiedTypeDeclarationPattern(
				packageName,
				packageMatchRule,
				typeName,
				typeSuffix,
				validatedTypeMatchRule);

		// Get working copy path(s). Store in a single string in case of only one to optimize comparison in requestor
		final HashSet workingCopyPaths = new HashSet();
		String workingCopyPath = null;
		ICompilationUnit[] copies = getWorkingCopies();
		final int copiesLength = copies == null ? 0 : copies.length;
		if (copies != null) {
			if (copiesLength == 1) {
				workingCopyPath = copies[0].getPath().toString();
			} else {
				for (int i = 0; i < copiesLength; i++) {
					ICompilationUnit workingCopy = copies[i];
					workingCopyPaths.add(workingCopy.getPath().toString());
				}
			}
		}
		final String singleWkcpPath = workingCopyPath;

		// Index requestor
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor(){
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant, AccessRuleSet access) {
				// Filter unexpected types
				TypeDeclarationPattern record = (TypeDeclarationPattern)indexRecord;
				if (record.enclosingTypeNames == IIndexConstants.ONE_ZERO_CHAR) {
					return true; // filter out local and anonymous classes
				}
				switch (copiesLength) {
					case 0:
						break;
					case 1:
						if (singleWkcpPath.equals(documentPath)) {
							return true; // filter out *the* working copy
						}
						break;
					default:
						if (workingCopyPaths.contains(documentPath)) {
							return true; // filter out working copies
						}
						break;
				}

				// Accept document path
				AccessRestriction accessRestriction = null;
				if (access != null) {
					// Compute document relative path
					int pkgLength = (record.pkg==null || record.pkg.length==0) ? 0 : record.pkg.length+1;
					int nameLength = record.simpleName==null ? 0 : record.simpleName.length;
					char[] path = new char[pkgLength+nameLength];
					int pos = 0;
					if (pkgLength > 0) {
						System.arraycopy(record.pkg, 0, path, pos, pkgLength-1);
						CharOperation.replace(path, '.', '/');
						path[pkgLength-1] = '/';
						pos += pkgLength;
					}
					if (nameLength > 0) {
						System.arraycopy(record.simpleName, 0, path, pos, nameLength);
						pos += nameLength;
					}
					// Update access restriction if path is not empty
					if (pos > 0) {
						accessRestriction = access.getViolatedRestriction(path);
					}
				}
				if (match(record.typeSuffix, record.modifiers)) {
					nameRequestor.acceptType(record.modifiers, record.pkg, record.simpleName, record.enclosingTypeNames, documentPath, accessRestriction);
				}
				return true;
			}
		};

		try {
			if (progressMonitor != null) {
				progressMonitor.beginTask(Messages.engine_searching, 1000);
			}
			// add type names from indexes
			indexManager.performConcurrentJob(
				new PatternSearchJob(
					pattern,
					getDefaultSearchParticipant(), // Java search only
					scope,
					searchRequestor),
				waitingPolicy,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 1000-copiesLength));

			// add type names from working copies
			if (copies != null) {
				for (int i = 0; i < copiesLength; i++) {
					final ICompilationUnit workingCopy = copies[i];
					if (!scope.encloses(workingCopy)) continue;
					final String path = workingCopy.getPath().toString();
					if (workingCopy.isConsistent()) {
						IPackageDeclaration[] packageDeclarations = workingCopy.getPackageDeclarations();
						char[] packageDeclaration = packageDeclarations.length == 0 ? CharOperation.NO_CHAR : packageDeclarations[0].getElementName().toCharArray();
						IType[] allTypes = workingCopy.getAllTypes();
						for (int j = 0, allTypesLength = allTypes.length; j < allTypesLength; j++) {
							IType type = allTypes[j];
							IJavaElement parent = type.getParent();
							char[][] enclosingTypeNames;
							if (parent instanceof IType) {
								char[] parentQualifiedName = ((IType)parent).getTypeQualifiedName('.').toCharArray();
								enclosingTypeNames = CharOperation.splitOn('.', parentQualifiedName);
							} else {
								enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
							}
							char[] simpleName = type.getElementName().toCharArray();
							int kind;
							if (type.isEnum()) {
								kind = TypeDeclaration.ENUM_DECL;
							} else if (type.isAnnotation()) {
								kind = TypeDeclaration.ANNOTATION_TYPE_DECL;
							}	else if (type.isClass()) {
								kind = TypeDeclaration.CLASS_DECL;
							} else /*if (type.isInterface())*/ {
								kind = TypeDeclaration.INTERFACE_DECL;
							}
							if (match(typeSuffix, packageName, typeName, validatedTypeMatchRule, kind, packageDeclaration, simpleName)) {
								if (nameRequestor instanceof TypeNameMatchRequestorWrapper) {
									((TypeNameMatchRequestorWrapper)nameRequestor).requestor.acceptTypeNameMatch(new JavaSearchTypeNameMatch(type, type.getFlags()));
								} else {
									nameRequestor.acceptType(type.getFlags(), packageDeclaration, simpleName, enclosingTypeNames, path, null);
								}
							}
						}
					} else {
						Parser basicParser = getParser();
						org.eclipse.jdt.internal.compiler.env.ICompilationUnit unit = (org.eclipse.jdt.internal.compiler.env.ICompilationUnit) workingCopy;
						CompilationResult compilationUnitResult = new CompilationResult(unit, 0, 0, this.compilerOptions.maxProblemsPerUnit);
						CompilationUnitDeclaration parsedUnit = basicParser.dietParse(unit, compilationUnitResult);
						if (parsedUnit != null) {
							final char[] packageDeclaration = parsedUnit.currentPackage == null ? CharOperation.NO_CHAR : CharOperation.concatWith(parsedUnit.currentPackage.getImportName(), '.');
							class AllTypeDeclarationsVisitor extends ASTVisitor {
								public boolean visit(TypeDeclaration typeDeclaration, BlockScope blockScope) {
									return false; // no local/anonymous type
								}
								public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope compilationUnitScope) {
									if (match(typeSuffix, packageName, typeName, validatedTypeMatchRule, TypeDeclaration.kind(typeDeclaration.modifiers), packageDeclaration, typeDeclaration.name)) {
										if (nameRequestor instanceof TypeNameMatchRequestorWrapper) {
											IType type = workingCopy.getType(new String(typeName));
											((TypeNameMatchRequestorWrapper)nameRequestor).requestor.acceptTypeNameMatch(new JavaSearchTypeNameMatch(type, typeDeclaration.modifiers));
										} else {
											nameRequestor.acceptType(typeDeclaration.modifiers, packageDeclaration, typeDeclaration.name, CharOperation.NO_CHAR_CHAR, path, null);
										}
									}
									return true;
								}
								public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope classScope) {
									if (match(typeSuffix, packageName, typeName, validatedTypeMatchRule, TypeDeclaration.kind(memberTypeDeclaration.modifiers), packageDeclaration, memberTypeDeclaration.name)) {
										// compute enclosing type names
										TypeDeclaration enclosing = memberTypeDeclaration.enclosingType;
										char[][] enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
										while (enclosing != null) {
											enclosingTypeNames = CharOperation.arrayConcat(new char[][] {enclosing.name}, enclosingTypeNames);
											if ((enclosing.bits & ASTNode.IsMemberType) != 0) {
												enclosing = enclosing.enclosingType;
											} else {
												enclosing = null;
											}
										}
										// report
										if (nameRequestor instanceof TypeNameMatchRequestorWrapper) {
											IType type = workingCopy.getType(new String(enclosingTypeNames[0]));
											for (int j=1, l=enclosingTypeNames.length; j<l; j++) {
												type = type.getType(new String(enclosingTypeNames[j]));
											}
											((TypeNameMatchRequestorWrapper)nameRequestor).requestor.acceptTypeNameMatch(new JavaSearchTypeNameMatch(type, 0));
										} else {
											nameRequestor.acceptType(memberTypeDeclaration.modifiers, packageDeclaration, memberTypeDeclaration.name, enclosingTypeNames, path, null);
										}
									}
									return true;
								}
							}
							parsedUnit.traverse(new AllTypeDeclarationsVisitor(), parsedUnit.scope);
						}
					}
					if (progressMonitor != null) {
						if (progressMonitor.isCanceled()) throw new OperationCanceledException();
						progressMonitor.worked(1);
					}
				}
			}
		} finally {
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		}
	}

	/**
	 * Searches for all top-level types and member types in the given scope using  a case sensitive exact match
	 * with the given qualified names and type names.
	 *
	 * @see SearchEngine#searchAllTypeNames(char[][], char[][], IJavaSearchScope, TypeNameRequestor, int, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void searchAllTypeNames(
		final char[][] qualifications,
		final char[][] typeNames,
		int matchRule,
		int searchFor,
		IJavaSearchScope scope,
		final IRestrictedAccessTypeRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws JavaModelException {

		// Validate match rule first
		int namesLength = typeNames == null ? 0 : typeNames.length;
		for (int i=0; i<namesLength; i++) {
			matchRule &= SearchPattern.validateMatchRule(new String(typeNames[i]), matchRule);
		}
		final int validatedMatchRule = matchRule;

		// Debug
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchAllTypeNames(char[][], char[][], int, int, IJavaSearchScope, IRestrictedAccessTypeRequestor, int, IProgressMonitor)"); //$NON-NLS-1$
			Util.verbose("	- package name: "+(qualifications==null?"null":new String(CharOperation.concatWith(qualifications, ',')))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- type name: "+(typeNames==null?"null":new String(CharOperation.concatWith(typeNames, ',')))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- match rule: "+matchRule); //$NON-NLS-1$
			if (validatedMatchRule != matchRule) {
				Util.verbose("	- validated match rule: "+getMatchRuleString(validatedMatchRule)); //$NON-NLS-1$
			}
			Util.verbose("	- search for: "+searchFor); //$NON-NLS-1$
			Util.verbose("	- scope: "+scope); //$NON-NLS-1$
		}
		IndexManager indexManager = JavaModelManager.getIndexManager();

		// Create pattern
		final char typeSuffix;
		switch(searchFor){
			case IJavaSearchConstants.CLASS :
				typeSuffix = IIndexConstants.CLASS_SUFFIX;
				break;
			case IJavaSearchConstants.CLASS_AND_INTERFACE :
				typeSuffix = IIndexConstants.CLASS_AND_INTERFACE_SUFFIX;
				break;
			case IJavaSearchConstants.CLASS_AND_ENUM :
				typeSuffix = IIndexConstants.CLASS_AND_ENUM_SUFFIX;
				break;
			case IJavaSearchConstants.INTERFACE :
				typeSuffix = IIndexConstants.INTERFACE_SUFFIX;
				break;
			case IJavaSearchConstants.INTERFACE_AND_ANNOTATION :
				typeSuffix = IIndexConstants.INTERFACE_AND_ANNOTATION_SUFFIX;
				break;
			case IJavaSearchConstants.ENUM :
				typeSuffix = IIndexConstants.ENUM_SUFFIX;
				break;
			case IJavaSearchConstants.ANNOTATION_TYPE :
				typeSuffix = IIndexConstants.ANNOTATION_TYPE_SUFFIX;
				break;
			default :
				typeSuffix = IIndexConstants.TYPE_SUFFIX;
				break;
		}
		final MultiTypeDeclarationPattern pattern = new MultiTypeDeclarationPattern(qualifications, typeNames, typeSuffix, validatedMatchRule);

		// Get working copy path(s). Store in a single string in case of only one to optimize comparison in requestor
		final HashSet workingCopyPaths = new HashSet();
		String workingCopyPath = null;
		ICompilationUnit[] copies = getWorkingCopies();
		final int copiesLength = copies == null ? 0 : copies.length;
		if (copies != null) {
			if (copiesLength == 1) {
				workingCopyPath = copies[0].getPath().toString();
			} else {
				for (int i = 0; i < copiesLength; i++) {
					ICompilationUnit workingCopy = copies[i];
					workingCopyPaths.add(workingCopy.getPath().toString());
				}
			}
		}
		final String singleWkcpPath = workingCopyPath;

		// Index requestor
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor(){
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant, AccessRuleSet access) {
				// Filter unexpected types
				QualifiedTypeDeclarationPattern record = (QualifiedTypeDeclarationPattern) indexRecord;
				if (record.enclosingTypeNames == IIndexConstants.ONE_ZERO_CHAR) {
					return true; // filter out local and anonymous classes
				}
				switch (copiesLength) {
					case 0:
						break;
					case 1:
						if (singleWkcpPath.equals(documentPath)) {
							return true; // filter out *the* working copy
						}
						break;
					default:
						if (workingCopyPaths.contains(documentPath)) {
							return true; // filter out working copies
						}
						break;
				}

				// Accept document path
				AccessRestriction accessRestriction = null;
				if (access != null) {
					// Compute document relative path
					int qualificationLength = (record.qualification == null || record.qualification.length == 0) ? 0 : record.qualification.length + 1;
					int nameLength = record.simpleName == null ? 0 : record.simpleName.length;
					char[] path = new char[qualificationLength + nameLength];
					int pos = 0;
					if (qualificationLength > 0) {
						System.arraycopy(record.qualification, 0, path, pos, qualificationLength - 1);
						CharOperation.replace(path, '.', '/');
						path[qualificationLength-1] = '/';
						pos += qualificationLength;
					}
					if (nameLength > 0) {
						System.arraycopy(record.simpleName, 0, path, pos, nameLength);
						pos += nameLength;
					}
					// Update access restriction if path is not empty
					if (pos > 0) {
						accessRestriction = access.getViolatedRestriction(path);
					}
				}
				nameRequestor.acceptType(record.modifiers, record.pkg, record.simpleName, record.enclosingTypeNames, documentPath, accessRestriction);
				return true;
			}
		};

		try {
			if (progressMonitor != null) {
				progressMonitor.beginTask(Messages.engine_searching, 100);
			}
			// add type names from indexes
			indexManager.performConcurrentJob(
				new PatternSearchJob(
					pattern,
					getDefaultSearchParticipant(), // Java search only
					scope,
					searchRequestor),
				waitingPolicy,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 100));

			// add type names from working copies
			if (copies != null) {
				for (int i = 0, length = copies.length; i < length; i++) {
					ICompilationUnit workingCopy = copies[i];
					final String path = workingCopy.getPath().toString();
					if (workingCopy.isConsistent()) {
						IPackageDeclaration[] packageDeclarations = workingCopy.getPackageDeclarations();
						char[] packageDeclaration = packageDeclarations.length == 0 ? CharOperation.NO_CHAR : packageDeclarations[0].getElementName().toCharArray();
						IType[] allTypes = workingCopy.getAllTypes();
						for (int j = 0, allTypesLength = allTypes.length; j < allTypesLength; j++) {
							IType type = allTypes[j];
							IJavaElement parent = type.getParent();
							char[][] enclosingTypeNames;
							char[] qualification = packageDeclaration;
							if (parent instanceof IType) {
								char[] parentQualifiedName = ((IType)parent).getTypeQualifiedName('.').toCharArray();
								enclosingTypeNames = CharOperation.splitOn('.', parentQualifiedName);
								qualification = CharOperation.concat(qualification, parentQualifiedName);
							} else {
								enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
							}
							char[] simpleName = type.getElementName().toCharArray();
							char suffix = IIndexConstants.TYPE_SUFFIX;
							if (type.isClass()) {
								suffix = IIndexConstants.CLASS_SUFFIX;
							} else if (type.isInterface()) {
								suffix = IIndexConstants.INTERFACE_SUFFIX;
							} else if (type.isEnum()) {
								suffix = IIndexConstants.ENUM_SUFFIX;
							} else if (type.isAnnotation()) {
								suffix = IIndexConstants.ANNOTATION_TYPE_SUFFIX;
							}
							if (pattern.matchesDecodedKey(new QualifiedTypeDeclarationPattern(qualification, simpleName, suffix, matchRule))) {
								nameRequestor.acceptType(type.getFlags(), packageDeclaration, simpleName, enclosingTypeNames, path, null);
							}
						}
					} else {
						Parser basicParser = getParser();
						org.eclipse.jdt.internal.compiler.env.ICompilationUnit unit = (org.eclipse.jdt.internal.compiler.env.ICompilationUnit) workingCopy;
						CompilationResult compilationUnitResult = new CompilationResult(unit, 0, 0, this.compilerOptions.maxProblemsPerUnit);
						CompilationUnitDeclaration parsedUnit = basicParser.dietParse(unit, compilationUnitResult);
						if (parsedUnit != null) {
							final char[] packageDeclaration = parsedUnit.currentPackage == null
								? CharOperation.NO_CHAR
								: CharOperation.concatWith(parsedUnit.currentPackage.getImportName(), '.');
							class AllTypeDeclarationsVisitor extends ASTVisitor {
								public boolean visit(TypeDeclaration typeDeclaration, BlockScope blockScope) {
									return false; // no local/anonymous type
								}
								public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope compilationUnitScope) {
									SearchPattern decodedPattern =
										new QualifiedTypeDeclarationPattern(packageDeclaration, typeDeclaration.name, convertTypeKind(TypeDeclaration.kind(typeDeclaration.modifiers)), validatedMatchRule);
									if (pattern.matchesDecodedKey(decodedPattern)) {
										nameRequestor.acceptType(typeDeclaration.modifiers, packageDeclaration, typeDeclaration.name, CharOperation.NO_CHAR_CHAR, path, null);
									}
									return true;
								}
								public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope classScope) {
									// compute enclosing type names
									char[] qualification = packageDeclaration;
									TypeDeclaration enclosing = memberTypeDeclaration.enclosingType;
									char[][] enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
									while (enclosing != null) {
										qualification = CharOperation.concat(qualification, enclosing.name, '.');
										enclosingTypeNames = CharOperation.arrayConcat(new char[][] {enclosing.name}, enclosingTypeNames);
										if ((enclosing.bits & ASTNode.IsMemberType) != 0) {
											enclosing = enclosing.enclosingType;
										} else {
											enclosing = null;
										}
									}
									SearchPattern decodedPattern =
										new QualifiedTypeDeclarationPattern(qualification, memberTypeDeclaration.name, convertTypeKind(TypeDeclaration.kind(memberTypeDeclaration.modifiers)), validatedMatchRule);
									if (pattern.matchesDecodedKey(decodedPattern)) {
										nameRequestor.acceptType(memberTypeDeclaration.modifiers, packageDeclaration, memberTypeDeclaration.name, enclosingTypeNames, path, null);
									}
									return true;
								}
							}
							parsedUnit.traverse(new AllTypeDeclarationsVisitor(), parsedUnit.scope);
						}
					}
				}
			}
		} finally {
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		}
	}

	public void searchDeclarations(IJavaElement enclosingElement, SearchRequestor requestor, SearchPattern pattern, IProgressMonitor monitor) throws JavaModelException {
		if (VERBOSE) {
			Util.verbose("	- java element: "+enclosingElement); //$NON-NLS-1$
		}
		IJavaSearchScope scope = createJavaSearchScope(new IJavaElement[] {enclosingElement});
		IResource resource = ((JavaElement) enclosingElement).resource();
		if (enclosingElement instanceof IMember) {
			IMember member = (IMember) enclosingElement;
			ICompilationUnit cu = member.getCompilationUnit();
			if (cu != null) {
				resource = cu.getResource();
			} else if (member.isBinary()) {
				// binary member resource cannot be used as this
				// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=148215
				resource = null;
			}
		}
		try {
			if (resource instanceof IFile) {
				try {
					requestor.beginReporting();
					if (VERBOSE) {
						Util.verbose("Searching for " + pattern + " in " + resource.getFullPath()); //$NON-NLS-1$//$NON-NLS-2$
					}
					SearchParticipant participant = getDefaultSearchParticipant();
					SearchDocument[] documents = MatchLocator.addWorkingCopies(
						pattern,
						new SearchDocument[] {new JavaSearchDocument(enclosingElement.getPath().toString(), participant)},
						getWorkingCopies(enclosingElement),
						participant);
					participant.locateMatches(
						documents,
						pattern,
						scope,
						requestor,
						monitor);
				} finally {
					requestor.endReporting();
				}
			} else {
				search(
					pattern,
					new SearchParticipant[] {getDefaultSearchParticipant()},
					scope,
					requestor,
					monitor);
			}
		} catch (CoreException e) {
			if (e instanceof JavaModelException)
				throw (JavaModelException) e;
			throw new JavaModelException(e);
		}
	}

	/**
	 * Searches for all declarations of the fields accessed in the given element.
	 * The element can be a compilation unit or a source type/method/field.
	 * Reports the field declarations using the given requestor.
	 *
	 * @see SearchEngine#searchDeclarationsOfAccessedFields(IJavaElement, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void searchDeclarationsOfAccessedFields(IJavaElement enclosingElement, SearchRequestor requestor, IProgressMonitor monitor) throws JavaModelException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchDeclarationsOfAccessedFields(IJavaElement, SearchRequestor, SearchPattern, IProgressMonitor)"); //$NON-NLS-1$
		}
		// Do not accept other kind of element type than those specified in the spec
		switch (enclosingElement.getElementType()) {
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
			case IJavaElement.TYPE:
			case IJavaElement.COMPILATION_UNIT:
				// valid element type
				break;
			default:
				throw new IllegalArgumentException();
		}
		SearchPattern pattern = new DeclarationOfAccessedFieldsPattern(enclosingElement);
		searchDeclarations(enclosingElement, requestor, pattern, monitor);
	}

	/**
	 * Searches for all declarations of the types referenced in the given element.
	 * The element can be a compilation unit or a source type/method/field.
	 * Reports the type declarations using the given requestor.
	 *
	 * @see SearchEngine#searchDeclarationsOfReferencedTypes(IJavaElement, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void searchDeclarationsOfReferencedTypes(IJavaElement enclosingElement, SearchRequestor requestor, IProgressMonitor monitor) throws JavaModelException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchDeclarationsOfReferencedTypes(IJavaElement, SearchRequestor, SearchPattern, IProgressMonitor)"); //$NON-NLS-1$
		}
		// Do not accept other kind of element type than those specified in the spec
		switch (enclosingElement.getElementType()) {
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
			case IJavaElement.TYPE:
			case IJavaElement.COMPILATION_UNIT:
				// valid element type
				break;
			default:
				throw new IllegalArgumentException();
		}
		SearchPattern pattern = new DeclarationOfReferencedTypesPattern(enclosingElement);
		searchDeclarations(enclosingElement, requestor, pattern, monitor);
	}

	/**
	 * Searches for all declarations of the methods invoked in the given element.
	 * The element can be a compilation unit or a source type/method/field.
	 * Reports the method declarations using the given requestor.
	 *
	 * @see SearchEngine#searchDeclarationsOfSentMessages(IJavaElement, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void searchDeclarationsOfSentMessages(IJavaElement enclosingElement, SearchRequestor requestor, IProgressMonitor monitor) throws JavaModelException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchDeclarationsOfSentMessages(IJavaElement, SearchRequestor, SearchPattern, IProgressMonitor)"); //$NON-NLS-1$
		}
		// Do not accept other kind of element type than those specified in the spec
		switch (enclosingElement.getElementType()) {
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
			case IJavaElement.TYPE:
			case IJavaElement.COMPILATION_UNIT:
				// valid element type
				break;
			default:
				throw new IllegalArgumentException();
		}
		SearchPattern pattern = new DeclarationOfReferencedMethodsPattern(enclosingElement);
		searchDeclarations(enclosingElement, requestor, pattern, monitor);
	}
}
