/*
 * Copyright (C) 2002-2003, Simon Nieuviarts
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 */
package org.objectweb.carol.cmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import java.io.Serializable;

/**
 * Manage equivalences between objects in the cluster. Two objects are equivalent if
 * their keys have the same value (key1.equals(key2)).
 * The keys prefixed with "REG_" are reserved by the ClusterRegistryImpl. No other
 * module should generate such keys. 
 */
public final class DistributedEquiv {
    private static Object lock = new Object();
    private static DistributedEquivSystem des = null;

    /**
     * The first one which calls this method starts the DistributedEquiv system.
     * It can be the only one to stop it with the ref it gets.
     */
    public static DistributedEquiv start() throws ConfigException {
        synchronized (lock) {
            if (des != null)
                throw new ConfigException("DistributedEquiv already started");
            try {
                des = new DistributedEquivSystem();
            } catch (ConfigException e) {
                throw e;
            } catch (Exception e) {
                throw new ConfigException(
                    e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return new DistributedEquiv();
    }

    public void stop() throws ConfigException {
        synchronized (lock) {
            des.terminate();
            des = null;
        }
    }

    /**
     * @return true if succesfully exported
     */
    static boolean exportObject(Serializable key, byte[] obj)
        throws ConfigException, RemoteException {
        DistributedEquivSystem d = des;
        if (d == null)
            throw new ConfigException("DistributedEquiv not started");
        return d.exportObject(key, obj);
    }

    /**
     * @return true if succesfully unexported
     */
    static boolean unexportObject(Serializable key) throws ConfigException {
        DistributedEquivSystem d = des;
        if (d == null)
            throw new ConfigException("DistributedEquiv not started");
        return d.unexportObject(key);
    }

    /**
     * @return <code>null<code> if not exported.
     */
    static ClusterStubData getGlobal(Serializable key)
        throws ConfigException, RemoteException {
        DistributedEquivSystem d = des;
        if (d == null)
            throw new ConfigException("DistributedEquiv not started");
        return d.getGlobal(key);
    }

    /**
     * @return <code>null<code> if not exported.
     */
    static Remote getLocal(Serializable key) throws ConfigException {
        DistributedEquivSystem d = des;
        if (d == null)
            throw new ConfigException("DistributedEquiv not started");
        return d.getLocal(key);
    }

    static Set keySet() throws ConfigException {
        DistributedEquivSystem d = des;
        if (d == null)
            throw new ConfigException("DistributedEquiv not started");
        return d.keySet();
    }
}
