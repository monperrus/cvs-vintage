/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss;

import java.util.Properties;
import java.util.Map;
import java.util.Collections;

import java.io.InputStream;
import java.io.IOException;

/**
 * Provides access to JBoss version (and build) properties.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision: 1.1 $
 */
public final class Version
{
   public static final String VERSION_MAJOR    = "version.major";
   public static final String VERSION_MINOR    = "version.minor";
   public static final String VERSION_REVISION = "version.revision";
   public static final String VERSION_TAG      = "version.tag";
   public static final String VERSION_NAME     = "version.name";
   public static final String BUILD_NUMBER     = "build.number";
   public static final String BUILD_ID         = "build.id";
   
   /** The single instance. */
   private static Version instance = null;

   /**
    * Get the single <tt>Version</tt> instance.
    *
    * @return    The single <tt>Version</tt> instance.
    */
   public static Version getInstance() {
      if (instance == null) {
         instance = new Version();
      }
      return instance;
   }

   /** The version properties. */
   private Properties props;

   /** Do not allow direct public construction. */
   private Version() {
      props = loadProperties();
   }

   /**
    * Load the version properties from a resource.
    */
   private Properties loadProperties() {
      props = new Properties();

      try {
         InputStream in =
            Version.class.getResourceAsStream("/org/jboss/version.properties");
      
         props.load(in);
         in.close();
      }
      catch (IOException e) {
         throw new Error("missing version.properties");
      }

      return props;
   }

   /**
    * Returns an unmodifiable map of version properties.
    *
    * @return An unmodifiable map of version properties.
    */
   public Map getProperties() {
      return Collections.unmodifiableMap(props);
   }

   /**
    * Returns the value for the given property name.
    *
    * @param name    The name of the property.
    * @return        The property value or null if the property is not set.
    */
   public String getProperty(final String name) {
      return props.getProperty(name);
   }

   /** Returns a property value as an int. */
   private int getIntProperty(final String name) {
      return Integer.valueOf(props.getProperty(name)).intValue();
   }
   
   /**
    * Returns the major number of the version.
    *
    * @return The major number of the version.
    */
   public int getMajor() {
      return getIntProperty(VERSION_MAJOR);
   }

   /**
    * Returns the minor number of the version.
    *
    * @return The minor number of the version.
    */
   public int getMinor() {
      return getIntProperty(VERSION_MINOR);
   }

   /**
    * Returns the revision number of the version.
    *
    * @return The revision number of the version.
    */
   public int getRevision() {
      return getIntProperty(VERSION_REVISION);
   }

   /**
    * Returns the tag of the version.
    *
    * @return The tag of the version.
    */
   public String getTag() {
      return props.getProperty(VERSION_TAG);
   }

   /**
    * Returns the name number of the version.
    *
    * @return The name of the version.
    */
   public String getName() {
      return props.getProperty(VERSION_NAME);
   }

   /**
    * Returns the build identifier for this version.
    *
    * @return The build identifier for this version.
    */
   public String getBuildID() {
      return props.getProperty(BUILD_ID);
   }

   /**
    * Returns the build number for this version.
    *
    * @return The build number for this version.
    */
   public String getBuildNumber() {
      return props.getProperty(BUILD_NUMBER);
   }
   
   /**
    * Returns the version information as a string.
    *
    * @return Basic information as a string.
    */
   public String toString() {
      StringBuffer buff = new StringBuffer();

      buff.append(getMajor()).append(".");
      buff.append(getMinor()).append(".");
      buff.append(getRevision()).append(getTag());
      buff.append("(").append(getBuildID()).append(")");

      return buff.toString();
   }
}

