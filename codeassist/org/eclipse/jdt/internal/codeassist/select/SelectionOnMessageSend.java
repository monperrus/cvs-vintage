package org.eclipse.jdt.internal.codeassist.select;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
/*
 * Selection node build by the parser in any case it was intending to
 * reduce a message send containing the cursor.
 * e.g.
 *
 *	class X {
 *    void foo() {
 *      this.[start]bar[end](1, 2)
 *    }
 *  }
 *
 *	---> class X {
 *         void foo() {
 *           <SelectOnMessageSend:this.bar(1, 2)>
 *         }
 *       }
 *
 */

import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class SelectionOnMessageSend extends MessageSend {

	public TypeBinding resolveType(BlockScope scope) {
		super.resolveType(scope);

		// tolerate non visible match
		if (binding == null || !(binding.isValidBinding() || binding.problemId() == ProblemReasons.NotVisible))
			throw new SelectionNodeFound();
		else
			throw new SelectionNodeFound(binding);
	}

	public String toStringExpression() {
		String s = "<SelectOnMessageSend:"; //$NON-NLS-1$
		if (receiver != ThisReference.ThisImplicit)
			s = s + receiver.toStringExpression() + "."; //$NON-NLS-1$
		s = s + new String(selector) + "("; //$NON-NLS-1$
		if (arguments != null) {
			for (int i = 0; i < arguments.length; i++) {
				s += arguments[i].toStringExpression();
				if (i != arguments.length - 1) {
					s += ", "; //$NON-NLS-1$
				}
			};
		}
		s = s + ")>"; //$NON-NLS-1$
		return s;
	}
}