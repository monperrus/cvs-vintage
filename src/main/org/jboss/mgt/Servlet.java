/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.mgt;

import java.util.Map;

/**
 * Contains the management information about a Servlet
 *
 * @author Andreas "Mad" Schaefer (andreas.schaefer@madplanet.com)
 **/
public class Servlet
   extends Item
{
   // -------------------------------------------------------------------------
   // Members
   // -------------------------------------------------------------------------  

   private String mClass;
   private Map mParameters;

   // -------------------------------------------------------------------------
   // Constructors
   // -------------------------------------------------------------------------

   /**
    * @param pName Name of the Servlet
    * @param pClassName Name of the class the servlet is based on
    * @param pParameters Map of the Servlet Parameters
    **/
   public Servlet(
      String pName,
      String pClassName,
      Map pParameters
   ) {
      super( pName );
      mClass = pClassName;
      mParameters = pParameters;
   }

   // -------------------------------------------------------------------------
   // Properties (Getters/Setters)
   // -------------------------------------------------------------------------  

   /**
    * @return Class of the Servlet is based on
    **/
   public String getClassName() {
      return mClass;
   }

   /**
    * @return Map of the Servlet Parameters where the key
    *         is the Name and the value is the Value of
    *         the parameter
    **/
   public Map getParameters() {
      return mParameters;
   }

   public String toString() {
      return "Servlet [ " + getName() + 
         ", " + getClassName() +
         ", " + getParameters() +
         " ]";
   }
}
