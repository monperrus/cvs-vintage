/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.activities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityEvent;
import org.eclipse.ui.activities.IActivityListener;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IObjectActivityManager;
import org.eclipse.ui.activities.IObjectContributionRecord;
import org.eclipse.ui.roles.IRoleManager;

/**
 * Provides a registry of id-&gt;object mappings (likely derived from extension
 * point contributions), id-&gt;activity mappings, and a means of filtering the
 * object registry based on the currently enabled activities.
 * 
 * This functionality is currently implemented by calculating the filtered set
 * only when activity changes dictate that the cache is invalid.  In a stable
 * system (one in which activities of interest are not enabling and disabling 
 * themselves with any great rate and in which new objects and bindings are not 
 * being added often) then this calculation should need to be performed 
 * infrequently. 
 * 
 * @since 3.0
 */
public class ObjectActivityManager implements IObjectActivityManager {

    /**
	 * The map of all known managers.
	 */
    private static Map managersMap = new HashMap(17);

    /**
	 * Get the manager for a given id, optionally creating it if it doesn't
	 * exist.
	 * 
	 * @param id the unique ID of the manager that is being sought.
	 * @param create force creation if the manager does not yet exist.
	 * @return @since 3.0
	 */
    public static ObjectActivityManager getManager(String id, boolean create) {
        ObjectActivityManager manager = (ObjectActivityManager) managersMap.get(id);
        if (manager == null && create) {
            manager = new ObjectActivityManager(id, PlatformUI.getWorkbench().getActivityManager(), PlatformUI.getWorkbench().getRoleManager());
            managersMap.put(id, manager);
        }
        return manager;
    }

    /**
	 * The cache of currently active objects. This is also the synchronization
	 * lock for all cache operations.
	 */
    private Collection activeObjects = new HashSet(17);

    /**
     * Listener that is responsible for invalidating the cache on messages from 
     * IActivity objects.
     */
    private IActivityListener activityListener = new IActivityListener() {

        public void activityChanged(IActivityEvent activityEvent) {
            invalidateCache();
        }

    };

    /**
     * The <code>IActivityManager</code> to which this manager is bound.
     */
    private IActivityManager activityManager;

    /**
	 * Map of id-&gt;list&lt;activity&gt;.
	 */
    private Map activityMap = new HashMap();

    /**
	 * Whether the active objects set is stale due to Activity enablement
	 * changes or object/binding additions.
	 */
    private boolean dirty = true;

    /**
	 * Unique ID for this manager.
	 */
    private String managerId;

    /**
	 * Map of id-&gt;object.
	 */
    private Map objectMap = new HashMap();

    /**
     * The <code>IRoleManager</code> to which this manager is bound.
     */
    private IRoleManager roleManager;

    /**
     * Create an instance with the given id that is bound to the provided 
     * managers.
     * 
     * @param id the unique identifier for this  manager.
     * @param activityManager the <code>IActivityManager</code> to bind to.
     * @param roleManager the <code>IRoleManager</code> to bind to.
     * @since 3.0
     */
    public ObjectActivityManager(String id, IActivityManager activityManager, IRoleManager roleManager) {
        super();
        if (id == null) {
            throw new IllegalArgumentException();
        }

        managerId = id;

        this.activityManager = activityManager;
        this.roleManager = roleManager;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#addActivityBinding(org.eclipse.ui.activities.IObjectContributionRecord,
	 *      java.lang.String)
	 */
    public void addActivityBinding(IObjectContributionRecord record, String activityId) {
        if (record == null || activityId == null) {
            throw new IllegalArgumentException();
        }

        IActivity activity = activityManager.getActivity(activityId);
        if (activity == null || !activity.isDefined()) {
            return;
        }

        Collection bindings = getActivityIdsFor(record, true);
        if (bindings.add(activityId)) {
            // if we havn't already bound this activity do so and invalidate
			// the
            // cache
            activity.addActivityListener(activityListener);
            invalidateCache();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#addObject(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
    public IObjectContributionRecord addObject(String pluginId, String localId, Object object) {
        if (pluginId == null || localId == null || object == null) {
            throw new IllegalArgumentException();
        }

        IObjectContributionRecord record = new ObjectContributionRecord(pluginId, localId);
        Object oldObject = objectMap.put(record, object);

        if (!object.equals(oldObject)) {
            // dirty the cache if the old entry is not the same as the new one.
            invalidateCache();
        }
        return record;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#applyPatternBindings()
	 */
    public void applyPatternBindings() {
        applyPatternBindings(getObjectIds());
    }

    /**
     * Apply pattern bindings to a collection of objects within this manager.
     * @param objectIds a collection containing 
     * <code>IObjectContributionRecords</code>
     * @since 3.0
     */
    void applyPatternBindings(Collection objectIds) {
        Collection activities = activityManager.getDefinedActivityIds();
        
        for (Iterator actItr = activities.iterator(); actItr.hasNext();) {
            IActivity activity = activityManager.getActivity((String) actItr.next());
            
            for (Iterator objItr = objectIds.iterator(); objItr.hasNext();) {
                IObjectContributionRecord objectId = (IObjectContributionRecord) objItr.next();
            
                if (activity.match(objectId.toString()))
                    addActivityBinding(objectId, activity.getId());
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#applyPatternBindings(org.eclipse.ui.activities.IObjectContributionRecord)
	 */
    public void applyPatternBindings(IObjectContributionRecord record) {
        applyPatternBindings(Collections.singleton(record));
    }

    /**
	 * Find the (first) ObjectContributionRecord that maps to the given object,
	 * or null.
	 * 
	 * @param objectOfInterest
	 * @return ObjectContributionRecord or <code>null</code>
	 */
    private IObjectContributionRecord findObjectContributionRecord(Object objectOfInterest) {
        for (Iterator i = objectMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Entry) i.next();
            if (entry.getValue().equals(objectOfInterest)) {
                return (IObjectContributionRecord) entry.getKey();
            }
        }
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#getActiveObjects()
	 */
    public Collection getActiveObjects() {
        synchronized (activeObjects) {
            if (!roleManager.getDefinedRoleIds().isEmpty()) {
                if (dirty) {
                    activeObjects.clear();
                    Collection activeActivities = activityManager.getEnabledActivityIds();
                    for (Iterator iter = objectMap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entry = (Entry) iter.next();
                        IObjectContributionRecord record = (IObjectContributionRecord) entry.getKey();
                        Collection activitiesForId = getActivityIdsFor(record, false);
                        if (activitiesForId == null) {
                            activeObjects.add(entry.getValue());
                        }
                        else {
                            Set activitiesForIdCopy = new HashSet(activitiesForId);
                            activitiesForIdCopy.retainAll(activeActivities);
                            if (!activitiesForIdCopy.isEmpty()) {
                                activeObjects.add(entry.getValue());
                            }
                        }
                    }

                    dirty = false;
                }
                return Collections.unmodifiableCollection(activeObjects);
            }
            else {
                return Collections.unmodifiableCollection(objectMap.values());
            }
        }
    }

    /**
	 * Return the activity set for the given record, creating and inserting one
	 * if requested.
	 * 
	 * @param record
	 * @param create
	 * @return Set
	 */
    private Set getActivityIdsFor(IObjectContributionRecord record, boolean create) {
        Set set = (Set) activityMap.get(record);
        if (set == null && create) {
            set = new HashSet();
            activityMap.put(record, set);
        }
        return set;
    }

    /**
	 * Get the unique identifier for this manager.
	 * 
	 * @return the unique identifier for this manager.
	 * @since 3.0
	 */
    public String getId() {
        return managerId;
    }

    /**
	 * Get the Set of ObjectContributionRecord keys from the object store. This
	 * Set is read only.
	 * 
	 * @return
	 */
    Set getObjectIds() {
        return Collections.unmodifiableSet(objectMap.keySet());
    }

    /**
	 * Mark the cache for recalculation.
	 */
    void invalidateCache() {
        synchronized (activeObjects) {
            dirty = true;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IObjectActivityManager#setEnablementFor(java.lang.Object,
	 *      boolean)
	 */
    public void setEnablementFor(Object objectOfInterest, boolean enablement) {
        IObjectContributionRecord record = findObjectContributionRecord(objectOfInterest);
        if (record != null) {
            Set activities = getActivityIdsFor(record, false);
            if (activities != null && activities.size() > 0) {
                Set oldActivities = activityManager.getEnabledActivityIds();
                Set newActivities = new HashSet(oldActivities);
                if (enablement) {
                    newActivities.addAll(activities);
                }
                else {
                    newActivities.removeAll(activities);
                }
                activityManager.setEnabledActivityIds(newActivities);
            }
        }
    }
}
