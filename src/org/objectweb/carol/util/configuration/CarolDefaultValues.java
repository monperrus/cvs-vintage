/*
 * @(#) DefaultCarolValues.java	1.0 02/07/15
 *
 * Copyright (C) 2002 - INRIA (www.inria.fr)
 *
 * CAROL: Common Architecture for RMI ObjectWeb Layer
 *
 * This library is developed inside the ObjectWeb Consortium,
 * http://www.objectweb.org
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
 * 
 *
 */
package org.objectweb.carol.util.configuration;

//java import 
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * Class <code>DefaultCarolValues</code> get default carol value for the properties file and
 * get carol properties with defaults from jndi Standard properties
 */
public class CarolDefaultValues {

    /**
     * Carol prefix
     */
    public static String CAROL_PREFIX="carol";

    /**
     * RMI Prefix 
     */
    public static String RMI_PREFIX="rmi";

    /**
     * JNDI Prefix
     */
    public static String JNDI_PREFIX="jndi";

    /**
     * JVM Prefix
     */
    public static String JVM_PREFIX="jvm";

    /**
     * activation Prefix
     */
    public static String ACTIVATION_PREFIX="activated";

    /**
     * portable remote object Prefix
     */
    public static String PRO_PREFIX="PortableRemoteObjectClass";

    /**
     * jndi factory Prefix
     */
    public static String FACTORY_PREFIX="java.naming.factory.initial";

    /**
     * jndi url  Prefix
     */
    public static String URL_PREFIX="java.naming.provider.url";

    
    /**
     * default CAROL Properties with all configuration
     */ 
    private Properties defaultProperties;


    // default for jrmp
    public static String jrmpName="jrmp";
    public static String jrmpJNDIPrefix="rmi";
    public static String jrmpPROD="org.objectweb.carol.rmi.multi.JrmpPRODelegate";
    public static Properties jrmpProps = new Properties();

    // default for iiop
    public static String iiopName="iiop";   
    public static String iiopJNDIPrefix="iiop";
    public static String iiopPROD="com.sun.corba.se.internal.javax.rmi.PortableRemoteObject";
    public static Properties iiopProps = new Properties();

    //default for jeremie
    public static String jeremieName="jeremie";
    public static String jeremieJNDIPrefix="jrmi";
    public static String jeremiePROD="org.objectweb.carol.rmi.multi.JeremiePRODelegate";
    public static Properties jeremieProps = new Properties();

    static {

	// add jrmp default configuration 
	jrmpProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+jrmpName+".PortableRemoteObjectClass",jrmpPROD); 
	jrmpProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+ACTIVATION_PREFIX, jrmpName); 
	
	// add iiop default configuration
	iiopProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+iiopName+".PortableRemoteObjectClass",iiopPROD); 
	iiopProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+ACTIVATION_PREFIX, iiopName);
		
	// add jeremie default configuration
	jeremieProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+jeremieName+".PortableRemoteObjectClass",jeremiePROD); 
	jeremieProps.setProperty(CAROL_PREFIX+"."+RMI_PREFIX+"."+ACTIVATION_PREFIX, jeremieName);
    }

    /**
     * Return a default carol properties link toi the jndi properties
     * @param carol properties (can be null) 
     * @param jndi properties
     * @return carol properties (without the jndi properties)
     * @throws RMIConfigurationException if the jndi property url java.naming.provider.url is not set to one of the default *
     * protocol (iiop, jrmp or jeremie)
     */
    public static Properties getCarolProperties(Properties rmiP, Properties jndiP) throws RMIConfigurationException {
	if (rmiP == null) {
	    String url = jndiP.getProperty(URL_PREFIX);
	    if (url != null) {
		String protocol = getRMIProtocol(url);
		if (protocol==jrmpName) {
		    return jrmpProps;
		} else if (protocol==iiopName) {
		    return iiopProps;
		} else if (protocol==jeremieName){
		    return jeremieProps;
		} else  {
		    throw new RMIConfigurationException("Can not load default protocol configuration, rmi protocol unknow:" + protocol);   		} 
	    } else {
	    	throw new RMIConfigurationException("Rmi protocol unknow, the jndi property " + URL_PREFIX + " is not set");  
	    }
	} else {
	    return rmiP;
	}
		
    }

    /**
     * return protocol name from url
     * @return protocol name
     * @param protocol jndi url
     */
    public static String getRMIProtocol(String url) {
	StringTokenizer st = new StringTokenizer(url, "://");
	String pref = st.nextToken().trim();
	if (pref==jrmpJNDIPrefix) {
	    return jrmpName;
	} else if (pref==iiopJNDIPrefix) {
	    return iiopName;
	} else if (pref==jeremieJNDIPrefix) {
	    return jeremieName;
	} else {
	    return pref;
	}
    }
}
