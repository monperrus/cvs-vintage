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

package org.eclipse.ui.commands;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.ui.internal.util.Util;

/**
 * <p>
 * JAVADOC
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 */
public class KeyStroke {

	private final static char KEY_DELIMITER = '+'; //$NON-NLS-1$
	private final static String KEY_DELIMITERS = "+"; //$NON-NLS-1$

	private final static String BS = "BS"; //$NON-NLS-1$
	private final static String CR = "CR"; //$NON-NLS-1$
	private final static String DEL = "DEL"; //$NON-NLS-1$
	private final static String ESC = "ESC"; //$NON-NLS-1$
	private final static String PLUS = "PLUS"; //$NON-NLS-1$
	private final static String SPACE = "SPACE"; //$NON-NLS-1$
	private final static String TAB = "TAB"; //$NON-NLS-1$

	private final static String ALT = "ALT"; //$NON-NLS-1$
	private final static String COMMAND = "COMMAND"; //$NON-NLS-1$
	private final static String CTRL = "CTRL"; //$NON-NLS-1$
	private final static String SHIFT = "SHIFT"; //$NON-NLS-1$

	private final static String ARROW_DOWN = "ARROW_DOWN"; //$NON-NLS-1$
	private final static String ARROW_LEFT = "ARROW_LEFT"; //$NON-NLS-1$
	private final static String ARROW_RIGHT = "ARROW_RIGHT"; //$NON-NLS-1$
	private final static String ARROW_UP = "ARROW_UP"; //$NON-NLS-1$
	private final static String END = "END"; //$NON-NLS-1$
	private final static String F1 = "F1"; //$NON-NLS-1$
	private final static String F10 = "F10"; //$NON-NLS-1$
	private final static String F11 = "F11"; //$NON-NLS-1$
	private final static String F12 = "F12"; //$NON-NLS-1$
	private final static String F2 = "F2"; //$NON-NLS-1$
	private final static String F3 = "F3"; //$NON-NLS-1$
	private final static String F4 = "F4"; //$NON-NLS-1$
	private final static String F5 = "F5"; //$NON-NLS-1$
	private final static String F6 = "F6"; //$NON-NLS-1$
	private final static String F7 = "F7"; //$NON-NLS-1$
	private final static String F8 = "F8"; //$NON-NLS-1$
	private final static String F9 = "F9"; //$NON-NLS-1$
	private final static String HOME = "HOME"; //$NON-NLS-1$
	private final static String INSERT = "INSERT"; //$NON-NLS-1$
	private final static String PAGE_DOWN = "PAGE_DOWN"; //$NON-NLS-1$
	private final static String PAGE_UP = "PAGE_UP"; //$NON-NLS-1$
	
	private static SortedMap escapeKeyLookup = new TreeMap();
	private static SortedMap modifierKeyLookup = new TreeMap();
	private static SortedMap specialKeyLookup = new TreeMap();
	
	static {
		escapeKeyLookup.put(BS, CharacterKey.create('\b'));
		escapeKeyLookup.put(CR, CharacterKey.create('\r'));
		escapeKeyLookup.put(DEL, CharacterKey.create('\u007F'));
		escapeKeyLookup.put(ESC, CharacterKey.create('\u001b'));
		escapeKeyLookup.put(PLUS, CharacterKey.create('+'));
		escapeKeyLookup.put(SPACE, CharacterKey.create(' '));
		escapeKeyLookup.put(TAB, CharacterKey.create('\t'));

		modifierKeyLookup.put(ALT, ModifierKey.ALT);
		modifierKeyLookup.put(COMMAND, ModifierKey.COMMAND);
		modifierKeyLookup.put(CTRL, ModifierKey.CTRL);
		modifierKeyLookup.put(SHIFT, ModifierKey.SHIFT);

		specialKeyLookup.put(ARROW_DOWN, SpecialKey.ARROW_DOWN);
		specialKeyLookup.put(ARROW_LEFT, SpecialKey.ARROW_LEFT);
		specialKeyLookup.put(ARROW_RIGHT, SpecialKey.ARROW_RIGHT);
		specialKeyLookup.put(ARROW_UP, SpecialKey.ARROW_UP);		
		specialKeyLookup.put(END, SpecialKey.END);
		specialKeyLookup.put(F1, SpecialKey.F1);
		specialKeyLookup.put(F10, SpecialKey.F10);
		specialKeyLookup.put(F11, SpecialKey.F11);		
		specialKeyLookup.put(F12, SpecialKey.F12);
		specialKeyLookup.put(F2, SpecialKey.F2);
		specialKeyLookup.put(F3, SpecialKey.F3);
		specialKeyLookup.put(F4, SpecialKey.F4);		
		specialKeyLookup.put(F5, SpecialKey.F5);
		specialKeyLookup.put(F6, SpecialKey.F6);
		specialKeyLookup.put(F7, SpecialKey.F7);
		specialKeyLookup.put(F8, SpecialKey.F8);		
		specialKeyLookup.put(F9, SpecialKey.F9);
		specialKeyLookup.put(HOME, SpecialKey.HOME);
		specialKeyLookup.put(INSERT, SpecialKey.INSERT);
		specialKeyLookup.put(PAGE_DOWN, SpecialKey.PAGE_DOWN);		
		specialKeyLookup.put(PAGE_UP, SpecialKey.PAGE_UP);
	}

	/**
	 * JAVADOC
	 * 
	 * @param modifierKeys
	 * @param nonModifierKey
	 * @return
	 */		
	public static KeyStroke create(Set modifierKeys, NonModifierKey nonModifierKey) {
		return new KeyStroke(modifierKeys, nonModifierKey);
	}

	/**
	 * JAVADOC
	 * 
	 * @param string
	 * @return
	 * @throws ParseException
	 */
	public static KeyStroke parse(String string)
		throws ParseException {
		if (string == null)
			throw new NullPointerException();

		Set modifierKeys = new TreeSet();
		NonModifierKey nonModifierKey = null;
		StringTokenizer stringTokenizer = new StringTokenizer(string, KEY_DELIMITERS);
		
		while (stringTokenizer.hasMoreTokens()) {
			String name = stringTokenizer.nextToken();
			
			if (stringTokenizer.hasMoreTokens()) {
				name = name.toUpperCase();
				ModifierKey modifierKey = (ModifierKey) modifierKeyLookup.get(name);
				
				if (modifierKey == null || !modifierKeys.add(modifierKey))
					throw new ParseException();
			} else if (name.length() == 1) {
				nonModifierKey = CharacterKey.create(name.charAt(0));				
				break;
			} else {
				name = name.toUpperCase();
				nonModifierKey = (NonModifierKey) escapeKeyLookup.get(name);
				
				if (nonModifierKey == null)
					nonModifierKey = (NonModifierKey) specialKeyLookup.get(name);

				if (nonModifierKey == null)
					throw new ParseException();
				
				break;
			} 					
		}
		
		return new KeyStroke(modifierKeys, nonModifierKey);
	}

	private Set modifierKeys;
	private NonModifierKey nonModifierKey;
	
	private KeyStroke(Set modifierKeys, NonModifierKey nonModifierKey) {
		super();
		if (nonModifierKey == null)
			throw new IllegalArgumentException();

		this.modifierKeys = Util.safeCopy(modifierKeys, ModifierKey.class);
		this.nonModifierKey = nonModifierKey;		
	}

	/**
	 * JAVADOC
	 * 
	 * @return
	 */
	public String format() {
		Iterator iterator = modifierKeys.iterator();
		StringBuffer stringBuffer = new StringBuffer();
	
		while (iterator.hasNext()) {
			stringBuffer.append(iterator.next().toString());
			stringBuffer.append(KEY_DELIMITER);
		}

		String name = nonModifierKey.toString();

		if ("\b".equals(name)) //$NON-NLS-1$
			stringBuffer.append(BS);
		else if ("\t".equals(name)) //$NON-NLS-1$
			stringBuffer.append(TAB);
		else if ("\r".equals(name)) //$NON-NLS-1$	
			stringBuffer.append(CR);
		else if ("\u001b".equals(name)) //$NON-NLS-1$	
			stringBuffer.append(ESC);
		else if (" ".equals(name)) //$NON-NLS-1$	
			stringBuffer.append(SPACE);
		else if ("+".equals(name)) //$NON-NLS-1$	
			stringBuffer.append(PLUS);
		else if ("\u007F".equals(name)) //$NON-NLS-1$	
			stringBuffer.append(DEL);
		else
			stringBuffer.append(name);
		
		return stringBuffer.toString();
	}

	/**
	 * JAVADOC
	 * 
	 * @return
	 */
	public Set getModifierKeys() {
		return Collections.unmodifiableSet(modifierKeys);
	}

	/**
	 * JAVADOC
	 * 
	 * @return
	 */
	public NonModifierKey getNonModifierKey() {
		return nonModifierKey;
	}
}
