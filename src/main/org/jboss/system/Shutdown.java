/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.system;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ListIterator;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanRegistration;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Category;

/**
 * Shutdown service.  Installs a hook to cleanly shutdown the server and
 * provides the ability to handle user shutdown requests.
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision: 1.7 $
 */
public class Shutdown implements MBeanRegistration, ShutdownMBean {
    // Constants -----------------------------------------------------
    // Attributes ----------------------------------------------------

    /** Instance logger. */
    private final Category log = Category.getInstance(Shutdown.class);

    /** The MBean server we are attached to. */
    private MBeanServer server;
    // Public  -------------------------------------------------------

    /** Shutdown the virtual machine and run shutdown hooks. */
    public void shutdown() {
        log.info("Shutting down");
        System.exit(0); // This will execute the shutdown hook
    }

    /** Forcibly terminates the currently running Java virtual machine. */
    public void halt() {
        System.err.println("Halting the system now!");
        Runtime.getRuntime().halt(0);
    }
    // MBeanRegistration implementation ------------------------------

    /**
     * Saves a reference to the MBean server for later use and installs a shutdown hook.
     * @param server    The MBean server which we are going to be registered.
     * @param name      The object name we have been configured to use.
     * @return          Our preferred object name.
     * @throws MalformedObjectNameException
     */
    public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
        this.server = server;
        try {
            Runtime.getRuntime().addShutdownHook(
                new Thread("JBoss Shutdown Hook") {
                    public void run() {
                        log.info("Shutting down all services");
                        System.out.println("Shutting down");
                        // Make sure all services are down properly
                        shutdownServices();
                        log.info("Shutdown complete");
                        System.out.println("Shutdown complete");
                    }
                });
            log.info("Shutdown hook added");
        } catch (Throwable e) {
            log.error("Could not add shutdown hook", e);
        }
        return name == null ? new ObjectName(OBJECT_NAME) : name;
    }

    public void postRegister(Boolean registrationDone) {
        // empty
    }

    public void preDeregister() throws Exception {
        // empty
    }

    public void postDeregister() {
        // empty
    }

    /**
     * Attempt to <em>stop</em> and <em>destroy</em> all services
     * running inside of the MBean server which we are attached too by
     * asking the <tt>ServiceControl</tt> to do the dirty work.
     */
    protected void shutdownServices() {
        try {
            // set to true for detailed name printouts
            boolean verbose = false;
            // get the deployed objects from ServiceController
            ObjectName[] deployed = (ObjectName[]) server.invoke(new ObjectName(
                "JBOSS-SYSTEM:spine=ServiceController"), "getDeployed",
                new Object[0], new String[0]);
            List servicesCopy = Arrays.asList(deployed);
            ListIterator enum = servicesCopy.listIterator();
            ListIterator beanEnum = servicesCopy.listIterator();
            ObjectName name = null;
            String[] sig = { "javax.management.ObjectName" };

            // filo ( first in last out )
	        while (enum.hasNext())
	      	{
				enum.next();
	
	            // filter out some services here ?
	
			}


            // Stop / Destroy / Unload all MBeans from ServiceController
            // Stop
            log.info("********************** Stop MBeans ************************************");
            log.info("***********************************************************************");
            //while (enum.hasNext())
            while (enum.hasPrevious())
            {
                //name = (ObjectName)enum.next();
                name = (ObjectName)enum.previous();
                Object[] args = { name };
                if (verbose)
                    log.info("********************** Looking at MBean : " + name.getCanonicalName());
                // Stop services
                if (!name.getCanonicalName().equals("JMX:name=Connector,type=RMI") &&
                    !name.getCanonicalName().equals("Adaptor:name=html") &&
                    !name.getCanonicalName().equals("JBOSS-SYSTEM:service=Naming")) {
                        if (verbose)
                            log.info("********************** Stopping   MBean : " + name.getCanonicalName());
                        server.invoke(new ObjectName("JBOSS-SYSTEM:spine=ServiceController"),
                            "stop", args, sig);
                        // Destroy services
                        // Unload services
                }
            }
            // Destroy
            log.info("********************** Destroy MBeans ************************************");
            log.info("**************************************************************************");
            //while (enum.hasPrevious())
            while (enum.hasNext())
            {
                //name = (ObjectName)enum.previous();
                name = (ObjectName)enum.next();
                Object[] args = { name };
                if (verbose)
                    log.info("********************** Looking at MBean : " + name.getCanonicalName());
                // Destroy services
                if (!name.getCanonicalName().equals("JMX:name=Connector,type=RMI") &&
                    !name.getCanonicalName().equals("Adaptor:name=html") &&
                    !name.getCanonicalName().equals("JBOSS-SYSTEM:service=Naming")) {
                        if (verbose)
                            log.info("********************** Destroying MBean : " + name.getCanonicalName());
                        server.invoke(new ObjectName("JBOSS-SYSTEM:spine=ServiceController"),
                            "destroy", args, sig);
                }
            }
            // Unload all MBeans from MBean Server
            Set allMBeans = server.queryNames(null, null);
            Iterator i = allMBeans.iterator();
            // write the Mbeans Out

	        /*
	         while(i.hasNext())	{
				name = (ObjectName) i.next();
	    		log.info("**********************Looking at MBean : " + name.getCanonicalName());
	         }
	         */

            log.info("********************** Unloading MBeans ************************************");
            log.info("****************************************************************************");
            while (i.hasNext()) {
                name = (ObjectName)i.next();
                if (verbose)
                    log.info("********************** Looking at MBean : " + name.getCanonicalName());
                if (!name.getCanonicalName().equals("JMImplementation:type=MBeanServerDelegate") &&
                    !name.getCanonicalName().equals("JMX:name=Connector,type=RMI") &&
                    !name.getCanonicalName().equals("Adaptor:name=html") &&
                    !name.getCanonicalName().equals("JBOSS-SYSTEM:service=Naming")) {
                        if (verbose)
                            log.info("********************** Unloading  MBean : " + name.getCanonicalName());
                        server.unregisterMBean(name);
                }
            }
        }
        catch (RuntimeMBeanException rmbe) {
            rmbe.getTargetException().printStackTrace();
        }
        catch (Exception e) {
            log.error("failed to destroy services", e);
        }
    }
}
