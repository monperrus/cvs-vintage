package org.junit.internal.deprecated;

import org.junit.internal.runners.BlockJUnit4ClassRunner;
import org.junit.internal.runners.JUnit4ClassRunner;

/**
 * @deprecated Included for backwards compatibility with JUnit 4.4. Will be
 *             removed in the next release. Please use
 *             {@link BlockJUnit4ClassRunner} in place of {@link JUnit4ClassRunner}.
 */
@Deprecated
class FailedBefore extends Exception {
	private static final long serialVersionUID= 1L;
}