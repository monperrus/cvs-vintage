/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.management.j2ee;

import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import javax.management.j2ee.EJB;
import javax.management.j2ee.J2EEApplication;
import javax.management.j2ee.J2EEServer;
import javax.management.j2ee.JVM;

import java.security.InvalidParameterException;

/**
 * Root class of the JBoss JSR-77 implementation of
 * {@link javax.management.j2ee.EjbModule EjbModule}.
 *
 * @author  <a href="mailto:andreas@jboss.org">Andreas Schaefer</a>.
 * @version $Revision: 1.3 $
 *   
 * <p><b>Revisions:</b>
 *
 * <p><b>20011123 Andreas Schaefer:</b>
 * <ul>
 * <li> Adjustments to the JBoss Guidelines and implementing of the
 *      the create() and destroy() helper method
 * </ul>
 **/
public class EjbModule
  extends J2EEModule
  implements EjbModuleMBean
{

   // Constants -----------------------------------------------------
   
   // Attributes ----------------------------------------------------

   private List mEJBs = new ArrayList();

   // Static --------------------------------------------------------
   
   public static ObjectName create( MBeanServer pServer, String pApplicationName, String pName, URL pURL ) {
      String lDD = null;
      ObjectName lApplication = null;
      try {
         ObjectName lServer = (ObjectName) pServer.queryNames(
             new ObjectName( J2EEManagedObject.getDomainName() + ":type=J2EEServer,*" ),
             null
         ).iterator().next();
         String lServerName = lServer.getKeyPropertyList().get( "type" ) + "=" +
                              lServer.getKeyPropertyList().get( "name" );
         System.out.println( "EjbModule.create(), server name: " + lServerName );
         lApplication = (ObjectName) pServer.queryNames(
             new ObjectName( J2EEManagedObject.getDomainName() + ":type=J2EEApplication" +
                ",name=" + pApplicationName + "," + lServerName + ",*"
             ),
             null
         ).iterator().next();
         // First get the deployement descriptor
         lDD = J2EEDeployedObject.getDeploymentDescriptor( pURL, J2EEDeployedObject.EJB );
      }
      catch( Exception e ) {
         e.printStackTrace();
      }
      try {
         // Now create the J2EEApplication
         return pServer.createMBean(
            "org.jboss.management.j2ee.EjbModule",
            null,
            new Object[] {
               pName,
               lApplication,
               lDD
            },
            new String[] {
               String.class.getName(),
               ObjectName.class.getName(),
               String.class.getName()
            }
         ).getObjectName();
      }
      catch( Exception e ) {
         e.printStackTrace();
         return null;
      }
   }
   
   public static void destroy( MBeanServer pServer, String pModuleName ) {
      try {
/*
         // Find the Object to be destroyed
         ObjectName lSearch = new ObjectName(
            J2EEManagedObject.getDomainName() + ":type=EJBModule,name=" + pName + ",*"
         );
         ObjectName lEjbModule = (ObjectName) pServer.queryNames(
            lSearch,
            null
         ).iterator().next();
*/
         // Now remove the EjbModule
         pServer.unregisterMBean( new ObjectName( pModuleName ) );
      }
      catch( Exception e ) {
         e.printStackTrace();
      }
   }
   
   // Constructors --------------------------------------------------
   
   /**
   * Constructor taking the Name of this Object
   *
   * @param pName Name to be set which must not be null
   * @param pDeploymentDescriptor
   *
   * @throws InvalidParameterException If the given Name is null
   **/
   public EjbModule( String pName, ObjectName pApplication, String pDeploymentDescriptor )
      throws
         MalformedObjectNameException,
         InvalidParentException
   {
      super( "EJBModule", pName, pApplication, pDeploymentDescriptor );
/*
      if( pEJBs == null || pEJBs.length == 0 ) {
         throw new InvalidParameterException( "EJB list may not be null or empty" );
      }
      mEJBs = new ArrayList( Arrays.asList( pEJBs ) );
*/
   }

   // Public --------------------------------------------------------
   
   // EJBModule implementation --------------------------------------
   
   public EJB[] getEjbs() {
      return (EJB[]) mEJBs.toArray( new EJB[ 0 ] );
   }
   
   public EJB getEjb( int pIndex ) {
      if( pIndex >= 0 && pIndex < mEJBs.size() )
      {
         return (EJB) mEJBs.get( pIndex );
      }
      else
      {
         return null;
      }
   }
   
   // Object overrides ---------------------------------------------------
   
   public String toString() {
      return "EJBModule[ " + super.toString() +
         "EJBs: " + mEJBs +
         " ]";
   }
   
   // Package protected ---------------------------------------------
   
   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
