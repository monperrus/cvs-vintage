/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 *   ClassLoader that one can attach thread-specific data to
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.2 $
 */
public class BeanClassLoader
   extends URLClassLoader
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   Object jndiRoot;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   public BeanClassLoader(ClassLoader parent)
   {
      super(new URL[0], parent);
   }
   
   // Public --------------------------------------------------------
   public void setJNDIRoot(Object root) { this.jndiRoot = root; }
   public Object getJNDIRoot() { return jndiRoot; }
}

