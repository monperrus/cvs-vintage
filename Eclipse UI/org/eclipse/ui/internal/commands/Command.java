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

package org.eclipse.ui.internal.commands;

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.ui.commands.ICommand;
import org.eclipse.ui.commands.registry.IContextBindingDefinition;
import org.eclipse.ui.commands.registry.IImageBindingDefinition;
import org.eclipse.ui.commands.registry.IKeyBindingDefinition;
import org.eclipse.ui.internal.util.Util;

final class Command implements Comparable, ICommand {

	private final static int HASH_FACTOR = 89;
	private final static int HASH_INITIAL = Command.class.getName().hashCode();

	private static Comparator nameComparator;
	
	static Comparator nameComparator() {
		if (nameComparator == null)
			nameComparator = new Comparator() {
				public int compare(Object left, Object right) {
					return Collator.getInstance().compare(((ICommand) left).getName(), ((ICommand) right).getName());
				}	
			};		
		
		return nameComparator;
	}

	static SortedMap sortedMapById(List commands) {
		if (commands == null)
			throw new NullPointerException();

		SortedMap sortedMap = new TreeMap();			
		Iterator iterator = commands.iterator();
		
		while (iterator.hasNext()) {
			Object object = iterator.next();
			Util.assertInstance(object, ICommand.class);				
			ICommand command = (ICommand) object;
			sortedMap.put(command.getId(), command);									
		}			
		
		return sortedMap;
	}

	static SortedMap sortedMapByName(List commands) {
		if (commands == null)
			throw new NullPointerException();

		SortedMap sortedMap = new TreeMap();			
		Iterator iterator = commands.iterator();
		
		while (iterator.hasNext()) {
			Object object = iterator.next();
			Util.assertInstance(object, ICommand.class);
			ICommand command = (ICommand) object;
			sortedMap.put(command.getName(), command);									
		}			
		
		return sortedMap;
	}

	private boolean active;
	private String categoryId;
	private List contextBindings;
	private String description;
	private String id;
	private List imageBindings;
	private List keyBindings;
	private String name;
	private String pluginId;
	
	private transient int hashCode;
	private transient boolean hashCodeComputed;
	private transient String string;
	
	Command(boolean active, String categoryId, List contextBindings, String description, String id, List imageBindings, List keyBindings, String name, String pluginId) {
		super();
		
		if (id == null || name == null)
			throw new NullPointerException();
		
		this.active = active;
		this.categoryId = categoryId;
		this.contextBindings = Util.safeCopy(contextBindings, IContextBindingDefinition.class);
		this.description = description;
		this.id = id;
		this.imageBindings = Util.safeCopy(imageBindings, IImageBindingDefinition.class);
		this.keyBindings = Util.safeCopy(keyBindings, IKeyBindingDefinition.class);
		this.name = name;
		this.pluginId = pluginId;
	}
	
	public int compareTo(Object object) {
		Command command = (Command) object;
		int compareTo = active == false ? (command.active == true ? -1 : 0) : 1;
		
		if (compareTo == 0) {
			compareTo = Util.compare(categoryId, command.categoryId);

			if (compareTo == 0) {	
				compareTo = Util.compare(contextBindings, command.contextBindings);

				if (compareTo == 0) {		
					compareTo = Util.compare(description, command.description);

					if (compareTo == 0) {
						compareTo = id.compareTo(command.id);

						if (compareTo == 0) {	
							compareTo = Util.compare(imageBindings, command.imageBindings);

							if (compareTo == 0) {	
								compareTo = Util.compare(keyBindings, command.keyBindings);

								if (compareTo == 0) {
									compareTo = name.compareTo(command.name);			
				
									if (compareTo == 0)
										compareTo = Util.compare(pluginId, command.pluginId);								
								}							
							}
						}
					}
				}
			}
		}
		
		return compareTo;	
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Command))
			return false;

		Command command = (Command) object;	
		boolean equals = true;
		equals &= active == command.active;
		equals &= Util.equals(categoryId, command.categoryId);
		equals &= contextBindings.equals(command.contextBindings);
		equals &= Util.equals(description, command.description);
		equals &= id.equals(command.id);
		equals &= imageBindings.equals(command.imageBindings);
		equals &= keyBindings.equals(command.keyBindings);
		equals &= name.equals(command.name);
		equals &= Util.equals(pluginId, command.pluginId);
		return equals;
	}

	public boolean getActive() {
		return active;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public List getContextBindings() {
		return contextBindings;
	}

	public String getDescription() {
		return description;	
	}
	
	public String getId() {
		return id;	
	}

	public List getImageBindings() {
		return imageBindings;
	}

	public List getKeyBindings() {
		return keyBindings;
	}
	
	public String getName() {
		return name;
	}	

	public String getPluginId() {
		return pluginId;
	}

	public int hashCode() {
		if (!hashCodeComputed) {
			hashCode = HASH_INITIAL;
			hashCode = hashCode * HASH_FACTOR + (active ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(categoryId);
			hashCode = hashCode * HASH_FACTOR + contextBindings.hashCode();
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(description);
			hashCode = hashCode * HASH_FACTOR + id.hashCode();
			hashCode = hashCode * HASH_FACTOR + imageBindings.hashCode();
			hashCode = hashCode * HASH_FACTOR + keyBindings.hashCode();
			hashCode = hashCode * HASH_FACTOR + name.hashCode();
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(pluginId);
			hashCodeComputed = true;
		}
			
		return hashCode;
	}

	public String toString() {
		if (string == null) {
			final StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append('[');
			stringBuffer.append(active);
			stringBuffer.append(',');
			stringBuffer.append(categoryId);
			stringBuffer.append(',');
			stringBuffer.append(contextBindings);
			stringBuffer.append(',');
			stringBuffer.append(description);
			stringBuffer.append(',');
			stringBuffer.append(id);
			stringBuffer.append(',');
			stringBuffer.append(imageBindings);
			stringBuffer.append(',');
			stringBuffer.append(keyBindings);
			stringBuffer.append(',');
			stringBuffer.append(name);
			stringBuffer.append(',');
			stringBuffer.append(pluginId);
			stringBuffer.append(']');
			string = stringBuffer.toString();
		}
	
		return string;
	}
}
