/*******************************************************************************
 * Copyright (c) 2002, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.util;

import org.eclipse.jdt.core.compiler.IProblem;

/**
 * Use to keep track of recorded information during the parsing like comment positions,
 * line ends or problems.
 */
public class RecordedParsingInformation {
	public IProblem[] problems;
	public int problemsCount;
	public int[] lineEnds;
	public int[][] commentPositions;
	
	public RecordedParsingInformation(IProblem[] problems, int[] lineEnds, int[][] commentPositions) {
		this.problems = problems;
		this.lineEnds = lineEnds;
		this.commentPositions = commentPositions;
		this.problemsCount = problems != null ? problems.length : 0;
	}
}
