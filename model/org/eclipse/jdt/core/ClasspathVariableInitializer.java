/*******************************************************************************
 * Copyright (c) 2001, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jdt.core;

/**
 * Abstract base implementation of all classpath variable initializers.
 * Classpath variable initializers are used in conjunction with the
 * "org.eclipse.jdt.core.classpathVariableInitializer" extension point.
 * <p>
 * Clients should subclass this class to implement a specific classpath
 * variable initializer. The subclass must have a public 0-argument
 * constructor and a concrete implementation of <code>initialize</code>.
 * </p>
 * @since 2.0
 */
public abstract class ClasspathVariableInitializer {

    /**
     * Creates a new classpath variable initializer.
     */
    public ClasspathVariableInitializer() {
    }

    /**
     * Binds a value to the workspace classpath variable with the given name,
     * or fails silently if this cannot be done.
     *
     * @param variable the name of the workspace classpath variable
     *    that requires a binding
     * @see JavaCore#setClasspathVariable     
     */
    public abstract void initialize(String variable);
}
