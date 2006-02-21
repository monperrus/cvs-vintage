/******************************************************************************* * Copyright (c) 2000, 2004 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation *******************************************************************************/package org.eclipse.ui.internal.expressions;import java.util.Iterator;import org.eclipse.core.expressions.EvaluationResult;import org.eclipse.core.expressions.Expression;import org.eclipse.core.expressions.IEvaluationContext;import org.eclipse.core.runtime.CoreException;import org.eclipse.ui.internal.util.Util;/** * Copied from org.eclipse.core.internal.expressions. */public final class AndExpression extends CompositeExpression {	/**	 * The constant integer hash code value meaning the hash code has not yet	 * been computed.	 */	private static final int HASH_CODE_NOT_COMPUTED = -1;	/**	 * A factor for computing the hash code for all schemes.	 */	private static final int HASH_FACTOR = 89;	/**	 * The seed for the hash code for all schemes.	 */	private static final int HASH_INITIAL = AndExpression.class.getName()			.hashCode();	/**	 * The hash code for this object. This value is computed lazily, and marked	 * as invalid when one of the values on which it is based changes.	 */	private transient int hashCode = HASH_CODE_NOT_COMPUTED;	public final boolean equals(final Object object) {		if (object instanceof AndExpression) {			final AndExpression that = (AndExpression) object;			return Util.equals(this.fExpressions, that.fExpressions);		}		return false;	}	public final EvaluationResult evaluate(final IEvaluationContext context)			throws CoreException {		return evaluateAnd(context);	}	/**	 * Computes the hash code for this object based on the id.	 * 	 * @return The hash code for this object.	 */	public final int hashCode() {		if (hashCode == HASH_CODE_NOT_COMPUTED) {			hashCode = HASH_INITIAL * HASH_FACTOR + Util.hashCode(fExpressions);			if (hashCode == HASH_CODE_NOT_COMPUTED) {				hashCode++;			}		}		return hashCode;	}	public final String toString() {		final StringBuffer buffer = new StringBuffer();		buffer.append("AndExpression("); //$NON-NLS-1$		if (fExpressions != null) {			final Iterator itr = fExpressions.iterator();			while (itr.hasNext()) {				final Expression expression = (Expression) itr.next();				buffer.append(expression.toString());				if (itr.hasNext()) {					buffer.append(',');				}			}		}		buffer.append(')');		return buffer.toString();	}}