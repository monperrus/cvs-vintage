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

package org.eclipse.ui.internal.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.activities.ActivityEvent;
import org.eclipse.ui.activities.ActivityManagerEvent;
import org.eclipse.ui.activities.CategoryEvent;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityActivityBinding;
import org.eclipse.ui.activities.IActivityPatternBinding;
import org.eclipse.ui.activities.ICategory;
import org.eclipse.ui.activities.ICategoryActivityBinding;
import org.eclipse.ui.activities.IMutableActivityManager;
import org.eclipse.ui.internal.util.Util;

public final class MutableActivityManager
	extends AbstractActivityManager
	implements IMutableActivityManager {

	static boolean isActivityDefinitionChildOf(
		String ancestor,
		String id,
		Map activityDefinitionsById) {
		Collection visited = new HashSet();

		while (id != null && !visited.contains(id)) {
			IActivityDefinition activityDefinition =
				(IActivityDefinition) activityDefinitionsById.get(id);
			visited.add(id);

			if (activityDefinition != null
				&& Util.equals(id = activityDefinition.getParentId(), ancestor))
				return true;
		}

		return false;
	}

	private Map activitiesById = new WeakHashMap();
	private Set activitiesWithListeners = new HashSet();
	private Map activityActivityBindingsByParentActivityId = new HashMap();
	private Map activityDefinitionsById = new HashMap();
	private Map activityPatternBindingsByActivityId = new HashMap();
	private IActivityRegistry activityRegistry;
	private Map categoriesById = new WeakHashMap();
	private Set categoriesWithListeners = new HashSet();
	private Map categoryActivityBindingsByCategoryId = new HashMap();
	private Map categoryDefinitionsById = new HashMap();
	private Set definedActivityIds = new HashSet();
	private Set definedCategoryIds = new HashSet();
	private Set enabledActivityIds = new HashSet();
	private Set enabledCategoryIds = new HashSet();

	public MutableActivityManager() {
		this(new ExtensionActivityRegistry(Platform.getExtensionRegistry()));
	}

	public MutableActivityManager(IActivityRegistry activityRegistry) {
		if (activityRegistry == null)
			throw new NullPointerException();

		this.activityRegistry = activityRegistry;

		this
			.activityRegistry
			.addActivityRegistryListener(new IActivityRegistryListener() {
			public void activityRegistryChanged(ActivityRegistryEvent activityRegistryEvent) {
				readRegistry();
			}
		});

		readRegistry();
	}

	Set getActivitiesWithListeners() {
		return activitiesWithListeners;
	}

	public IActivity getActivity(String activityId) {
		if (activityId == null)
			throw new NullPointerException();

		Activity activity = (Activity) activitiesById.get(activityId);

		if (activity == null) {
			activity = new Activity(this, activityId);
			updateActivity(activity);
			activitiesById.put(activityId, activity);
		}

		return activity;
	}

	Set getCategoriesWithListeners() {
		return categoriesWithListeners;
	}

	public ICategory getCategory(String categoryId) {
		if (categoryId == null)
			throw new NullPointerException();

		Category category = (Category) categoriesById.get(categoryId);

		if (category == null) {
			category = new Category(this, categoryId);
			updateCategory(category);
			categoriesById.put(categoryId, category);
		}

		return category;
	}

	public Set getDefinedActivityIds() {
		return Collections.unmodifiableSet(definedActivityIds);
	}

	public Set getDefinedCategoryIds() {
		return Collections.unmodifiableSet(definedCategoryIds);
	}

	public Set getEnabledActivityIds() {
		return Collections.unmodifiableSet(enabledActivityIds);
	}

	public Set getEnabledCategoryIds() {
		return Collections.unmodifiableSet(enabledCategoryIds);
	}

	public boolean isMatch(String string, Set activityIds) {
		activityIds = Util.safeCopy(activityIds, String.class);

		for (Iterator iterator = activityIds.iterator(); iterator.hasNext();) {
			String activityId = (String) iterator.next();
			IActivity activity = getActivity(activityId);

			if (activity.isMatch(string))
				return true;
		}

		return false;
	}

	public Set getMatches(String string, Set activityIds) {
		Set matches = new HashSet();
		activityIds = Util.safeCopy(activityIds, String.class);

		for (Iterator iterator = activityIds.iterator(); iterator.hasNext();) {
			String activityId = (String) iterator.next();
			IActivity activity = getActivity(activityId);

			if (activity.isMatch(string))
				matches.add(activityId);
		}

		return Collections.unmodifiableSet(matches);
	}

	public boolean match(String string, Set activityIds) {
		return isMatch(string, activityIds);
	}
	
	private void notifyActivities(Map activityEventsByActivityId) {
		for (Iterator iterator =
			activityEventsByActivityId.entrySet().iterator();
			iterator.hasNext();
			) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String activityId = (String) entry.getKey();
			ActivityEvent activityEvent = (ActivityEvent) entry.getValue();
			Activity activity = (Activity) activitiesById.get(activityId);

			if (activity != null)
				activity.fireActivityChanged(activityEvent);
		}
	}

	private void notifyCategories(Map categoryEventsByCategoryId) {
		for (Iterator iterator =
			categoryEventsByCategoryId.entrySet().iterator();
			iterator.hasNext();
			) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String categoryId = (String) entry.getKey();
			CategoryEvent categoryEvent = (CategoryEvent) entry.getValue();
			Category category = (Category) categoriesById.get(categoryId);

			if (category != null)
				category.fireCategoryChanged(categoryEvent);
		}
	}

	private void readRegistry() {
		Collection activityDefinitions = new ArrayList();
		activityDefinitions.addAll(activityRegistry.getActivityDefinitions());
		Map activityDefinitionsById =
			new HashMap(
				ActivityDefinition.activityDefinitionsById(
					activityDefinitions,
					false));

		for (Iterator iterator = activityDefinitionsById.values().iterator();
			iterator.hasNext();
			) {
			IActivityDefinition activityDefinition =
				(IActivityDefinition) iterator.next();
			String name = activityDefinition.getName();

			if (name == null || name.length() == 0)
				iterator.remove();
		}

		for (Iterator iterator = activityDefinitionsById.keySet().iterator();
			iterator.hasNext();
			)
			if (!isActivityDefinitionChildOf(null,
				(String) iterator.next(),
				activityDefinitionsById))
				iterator.remove();

		Collection categoryDefinitions = new ArrayList();
		categoryDefinitions.addAll(activityRegistry.getCategoryDefinitions());
		Map categoryDefinitionsById =
			new HashMap(
				CategoryDefinition.categoryDefinitionsById(
					categoryDefinitions,
					false));

		for (Iterator iterator = categoryDefinitionsById.values().iterator();
			iterator.hasNext();
			) {
			ICategoryDefinition categoryDefinition =
				(ICategoryDefinition) iterator.next();
			String name = categoryDefinition.getName();

			if (name == null || name.length() == 0)
				iterator.remove();
		}

		Map activityActivityBindingDefinitionsByParentActivityId =
			ActivityActivityBindingDefinition
				.activityActivityBindingDefinitionsByParentActivityId(
				activityRegistry.getActivityActivityBindingDefinitions());
		Map activityActivityBindingsByParentActivityId = new HashMap();

		for (Iterator iterator =
			activityActivityBindingDefinitionsByParentActivityId
				.entrySet()
				.iterator();
			iterator.hasNext();
			) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String parentActivityId = (String) entry.getKey();

			if (activityActivityBindingsByParentActivityId
				.containsKey(parentActivityId)) {
				Collection activityActivityBindingDefinitions =
					(Collection) entry.getValue();

				if (activityActivityBindingDefinitions != null)
					for (Iterator iterator2 =
						activityActivityBindingDefinitions.iterator();
						iterator2.hasNext();
						) {
						IActivityActivityBindingDefinition activityActivityBindingDefinition =
							(IActivityActivityBindingDefinition) iterator2
								.next();
						String childActivityId =
							activityActivityBindingDefinition
								.getChildActivityId();

						if (childActivityId != null) {
							IActivityActivityBinding activityActivityBinding =
								new ActivityActivityBinding(
									childActivityId,
									parentActivityId);
							Set activityActivityBindings =
								(
									Set) activityActivityBindingsByParentActivityId
										.get(
									parentActivityId);

							if (activityActivityBindings == null) {
								activityActivityBindings = new HashSet();
								activityActivityBindingsByParentActivityId.put(
									parentActivityId,
									activityActivityBindings);
							}

							activityActivityBindings.add(
								activityActivityBinding);
						}
					}
			}
		}

		Map activityPatternBindingDefinitionsByActivityId =
			ActivityPatternBindingDefinition
				.activityPatternBindingDefinitionsByActivityId(
				activityRegistry.getActivityPatternBindingDefinitions());
		Map activityPatternBindingsByActivityId = new HashMap();

		for (Iterator iterator =
			activityPatternBindingDefinitionsByActivityId.entrySet().iterator();
			iterator.hasNext();
			) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String activityId = (String) entry.getKey();

			if (activityDefinitionsById.containsKey(activityId)) {
				Collection activityPatternBindingDefinitions =
					(Collection) entry.getValue();

				if (activityPatternBindingDefinitions != null)
					for (Iterator iterator2 =
						activityPatternBindingDefinitions.iterator();
						iterator2.hasNext();
						) {
						IActivityPatternBindingDefinition activityPatternBindingDefinition =
							(IActivityPatternBindingDefinition) iterator2
								.next();
						String pattern =
							activityPatternBindingDefinition.getPattern();

						if (pattern != null && pattern.length() != 0) {
							IActivityPatternBinding activityPatternBinding =
								new ActivityPatternBinding(
									activityId,
									Pattern.compile(pattern));
							Set activityPatternBindings =
								(Set) activityPatternBindingsByActivityId.get(
									activityId);

							if (activityPatternBindings == null) {
								activityPatternBindings = new HashSet();
								activityPatternBindingsByActivityId.put(
									activityId,
									activityPatternBindings);
							}

							activityPatternBindings.add(activityPatternBinding);
						}
					}
			}
		}

		Map categoryActivityBindingDefinitionsByCategoryId =
			CategoryActivityBindingDefinition
				.categoryActivityBindingDefinitionsByCategoryId(
				activityRegistry.getCategoryActivityBindingDefinitions());
		Map categoryActivityBindingsByCategoryId = new HashMap();

		for (Iterator iterator =
			categoryActivityBindingDefinitionsByCategoryId
				.entrySet()
				.iterator();
			iterator.hasNext();
			) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String categoryId = (String) entry.getKey();

			if (categoryActivityBindingsByCategoryId.containsKey(categoryId)) {
				Collection categoryActivityBindingDefinitions =
					(Collection) entry.getValue();

				if (categoryActivityBindingDefinitions != null)
					for (Iterator iterator2 =
						categoryActivityBindingDefinitions.iterator();
						iterator2.hasNext();
						) {
						ICategoryActivityBindingDefinition categoryActivityBindingDefinition =
							(ICategoryActivityBindingDefinition) iterator2
								.next();
						String activityId =
							categoryActivityBindingDefinition.getActivityId();

						if (activityId != null) {
							ICategoryActivityBinding categoryActivityBinding =
								new CategoryActivityBinding(
									activityId,
									categoryId);
							Set categoryActivityBindings =
								(Set) categoryActivityBindingsByCategoryId.get(
									categoryId);

							if (categoryActivityBindings == null) {
								categoryActivityBindings = new HashSet();
								categoryActivityBindingsByCategoryId.put(
									categoryId,
									categoryActivityBindings);
							}

							categoryActivityBindings.add(
								categoryActivityBinding);
						}
					}
			}
		}

		this.activityActivityBindingsByParentActivityId =
			activityActivityBindingsByParentActivityId;
		this.activityDefinitionsById = activityDefinitionsById;
		this.activityPatternBindingsByActivityId =
			activityPatternBindingsByActivityId;
		this.categoryActivityBindingsByCategoryId =
			categoryActivityBindingsByCategoryId;
		this.categoryDefinitionsById = categoryDefinitionsById;
		boolean definedActivityIdsChanged = false;
		Set definedActivityIds = new HashSet(activityDefinitionsById.keySet());

		if (!definedActivityIds.equals(this.definedActivityIds)) {
			this.definedActivityIds = definedActivityIds;
			definedActivityIdsChanged = true;
		}

		boolean definedCategoryIdsChanged = false;
		Set definedCategoryIds = new HashSet(categoryDefinitionsById.keySet());

		if (!definedCategoryIds.equals(this.definedCategoryIds)) {
			this.definedCategoryIds = definedCategoryIds;
			definedCategoryIdsChanged = true;
		}

		Map activityEventsByActivityId =
			updateActivities(activitiesById.keySet());

		Map categoryEventsByCategoryId =
			updateCategories(categoriesById.keySet());

		if (definedActivityIdsChanged || definedCategoryIdsChanged)
			fireActivityManagerChanged(
				new ActivityManagerEvent(
					this,
					definedActivityIdsChanged,
					definedCategoryIdsChanged,
					false,
					false));

		if (activityEventsByActivityId != null)
			notifyActivities(activityEventsByActivityId);

		if (categoryEventsByCategoryId != null)
			notifyCategories(categoryEventsByCategoryId);
	}

	public void setEnabledActivityIds(Set enabledActivityIds) {
		enabledActivityIds = Util.safeCopy(enabledActivityIds, String.class);
		boolean activityManagerChanged = false;
		Map activityEventsByActivityId = null;

		if (!this.enabledActivityIds.equals(enabledActivityIds)) {
			this.enabledActivityIds = enabledActivityIds;
			activityManagerChanged = true;
			activityEventsByActivityId =
				updateActivities(this.definedActivityIds);
		}

		if (activityManagerChanged)
			fireActivityManagerChanged(
				new ActivityManagerEvent(this, false, false, true, false));

		if (activityEventsByActivityId != null)
			notifyActivities(activityEventsByActivityId);
	}

	public void setEnabledCategoryIds(Set enabledCategoryIds) {
		enabledCategoryIds = Util.safeCopy(enabledCategoryIds, String.class);
		boolean activityManagerChanged = false;
		Map categoryEventsByCategoryId = null;

		if (!this.enabledCategoryIds.equals(enabledCategoryIds)) {
			this.enabledCategoryIds = enabledCategoryIds;
			activityManagerChanged = true;
			categoryEventsByCategoryId =
				updateCategories(this.definedCategoryIds);
		}

		if (activityManagerChanged)
			fireActivityManagerChanged(
				new ActivityManagerEvent(this, false, false, false, true));

		if (categoryEventsByCategoryId != null)
			notifyCategories(categoryEventsByCategoryId);
	}

	private Map updateActivities(Collection activityIds) {
		Map activityEventsByActivityId = new TreeMap();

		for (Iterator iterator = activityIds.iterator(); iterator.hasNext();) {
			String activityId = (String) iterator.next();
			Activity activity = (Activity) activitiesById.get(activityId);

			if (activity != null) {
				ActivityEvent activityEvent = updateActivity(activity);

				if (activityEvent != null)
					activityEventsByActivityId.put(activityId, activityEvent);
			}
		}

		return activityEventsByActivityId;
	}

	private ActivityEvent updateActivity(Activity activity) {
		Set activityActivityBindings =
		(Set) activityActivityBindingsByParentActivityId.get(activity.getId());
		boolean activityActivityBindingsChanged =
		activity.setActivityActivityBindings(
				activityActivityBindings != null
				? activityActivityBindings
				: Collections.EMPTY_SET);
		Set activityPatternBindings =
			(Set) activityPatternBindingsByActivityId.get(activity.getId());
		boolean activityPatternBindingsChanged =
			activity.setActivityPatternBindings(
				activityPatternBindings != null
					? activityPatternBindings
					: Collections.EMPTY_SET);
		IActivityDefinition activityDefinition =
			(IActivityDefinition) activityDefinitionsById.get(activity.getId());
		boolean definedChanged =
			activity.setDefined(activityDefinition != null);
		boolean descriptionChanged =
			activity.setDescription(
				activityDefinition != null
					? activityDefinition.getDescription()
					: null);
		boolean enabledChanged =
			activity.setEnabled(enabledActivityIds.contains(activity.getId()));
		boolean nameChanged =
			activity.setName(
				activityDefinition != null
					? activityDefinition.getName()
					: null);
		boolean parentIdChanged =
			activity.setParentId(
				activityDefinition != null
					? activityDefinition.getParentId()
					: null);

		if (activityActivityBindingsChanged
			|| activityPatternBindingsChanged
			|| definedChanged
			|| descriptionChanged
			|| enabledChanged
			|| nameChanged
			|| parentIdChanged)
			return new ActivityEvent(
				activity,
				activityActivityBindingsChanged,
				activityPatternBindingsChanged,
				definedChanged,
				descriptionChanged,
				enabledChanged,
				nameChanged,
				parentIdChanged);
		else
			return null;
	}

	private Map updateCategories(Collection categoryIds) {
		Map categoryEventsByCategoryId = new TreeMap();

		for (Iterator iterator = categoryIds.iterator(); iterator.hasNext();) {
			String categoryId = (String) iterator.next();
			Category category = (Category) categoriesById.get(categoryId);

			if (category != null) {
				CategoryEvent categoryEvent = updateCategory(category);

				if (categoryEvent != null)
					categoryEventsByCategoryId.put(categoryId, categoryEvent);
			}
		}

		return categoryEventsByCategoryId;
	}

	private CategoryEvent updateCategory(Category category) {
		Set categoryActivityBindings =
			(Set) categoryActivityBindingsByCategoryId.get(category.getId());
		boolean categoryActivityBindingsChanged =
			category.setCategoryActivityBindings(
				categoryActivityBindings != null
					? categoryActivityBindings
					: Collections.EMPTY_SET);
		ICategoryDefinition categoryDefinition =
			(ICategoryDefinition) categoryDefinitionsById.get(category.getId());
		boolean definedChanged =
			category.setDefined(categoryDefinition != null);
		boolean descriptionChanged =
			category.setDescription(
				categoryDefinition != null
					? categoryDefinition.getDescription()
					: null);
		boolean nameChanged =
			category.setName(
				categoryDefinition != null
					? categoryDefinition.getName()
					: null);

		if (categoryActivityBindingsChanged
			|| definedChanged
			|| descriptionChanged
			|| nameChanged)
			return new CategoryEvent(
				category,
				categoryActivityBindingsChanged,
				definedChanged,
				descriptionChanged,
				nameChanged);
		else
			return null;
	}
}
