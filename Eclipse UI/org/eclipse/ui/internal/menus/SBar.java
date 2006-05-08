/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.menus;

import org.eclipse.jface.util.Util;
import org.eclipse.ui.internal.misc.Policy;

/**
 * <p>
 * A location element referring to a specific path within within the menu bar,
 * tool bar or status line.
 * </p>
 * <p>
 * Clients may instantiate this class, but must not extend.
 * </p>
 * <p>
 * <strong>PROVISIONAL</strong>. This class or interface has been added as
 * part of a work in progress. There is a guarantee neither that this API will
 * work nor that it will remain the same. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * <p>
 * This class will eventually exist in <code>org.eclipse.jface.menus</code>.
 * </p>
 * 
 * @since 3.2
 */
public final class SBar extends LeafLocationElement {

	/**
	 * The constant used for a menu bar.
	 */
	public static final String TYPE_MENU = "menu"; //$NON-NLS-1$

	/**
	 * The constant used for the tool bar.
	 */
	public static final String TYPE_TRIM = "trim"; //$NON-NLS-1$

	/**
	 * The type of bar this is making reference to.
	 */
	private final String type;

	/**
	 * Constructs a new instance of <code>SBar</code>. This bar will refer to
	 * the menu type. It will contribute the item to top-most node in the menu
	 * hierarchy.
	 */
	public SBar() {
		this(TYPE_MENU, null);
	}

	/**
	 * Constructs a new instance of <code>SBar</code>. This bar will refer to
	 * the menu type.
	 * 
	 * @param path
	 *            The path to the final location. If this value is
	 *            <code>null</code>, it means that it should be inserted at
	 *            the top-level of the bar.
	 * @see #TYPE_MENU
	 */
	public SBar(final String path) {
		this(TYPE_MENU, path);
	}

	/**
	 * Constructs a new instance of <code>SBar</code>.
	 * 
	 * @param type
	 *            The type of bar this is making reference to.
	 * @param path
	 *            The path to the final location. If this value is
	 *            <code>null</code>, it means that it should be inserted at
	 *            the top-level of the bar.
	 * @see #TYPE_MENU
	 * @see #TYPE_STATUS
	 * @see #TYPE_TRIM
	 */
	public SBar(final String type, final String path) {
		super(path);
		this.type = type;
	}

	public final LocationElement createChild(final String id) {
		final String parentPath = getPath();
		final String path;
		if (parentPath == null) {
			path = id;
		} else {
			path = parentPath + PATH_SEPARATOR + id;
		}
		return new SBar(getType(), path);
	}

	public final ILocationElementTokenizer getTokenizer() {
		if (Policy.EXPERIMENTAL_MENU && getPath() != null
				&& getPath().indexOf(LeafLocationElement.BREAKPOINT_PATH) > -1) {
			System.err.println("getTokenizer: " + getPath()); //$NON-NLS-1$
		}
		return new ILocationElementTokenizer() {
			String remainingPath = getPath();

			String parsedPath = null;

			public final LocationElementToken nextToken() {
				final SLocation location = new SLocation(new SBar(getType(),
						parsedPath));
				final int separator = remainingPath
						.indexOf(LeafLocationElement.PATH_SEPARATOR);
				final String id;
				if (separator == -1) {
					id = remainingPath;
					remainingPath = null;
				} else {
					id = remainingPath.substring(0, separator);
					remainingPath = remainingPath.substring(separator + 1);
					if (remainingPath.length()==0) {
						remainingPath = null;
					}
				}
				if (parsedPath==null) {
					parsedPath = id;
				} else {
					parsedPath = parsedPath + '/' + id;
				}
				return new LocationElementToken(location, id);
			}

			public final boolean hasMoreTokens() {
				return remainingPath != null;
			}
		};
	}

	/**
	 * Returns the type for this bar.
	 * 
	 * @return The type for this bar.
	 */
	public final String getType() {
		return type;
	}

	public final String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("SBar("); //$NON-NLS-1$
		buffer.append(type);
		buffer.append(',');
		buffer.append(getPath());
		buffer.append(')');
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.menus.LeafLocationElement#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj instanceof SBar) {
			SBar bar = (SBar) obj;
			return Util.equals(type, bar.type) && super.equals(obj);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.menus.LeafLocationElement#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(type) + super.hashCode();
	}
}
