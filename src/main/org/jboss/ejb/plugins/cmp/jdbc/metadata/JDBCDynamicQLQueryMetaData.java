/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc.metadata;

import java.lang.reflect.Method;
import org.w3c.dom.Element;
import org.jboss.deployment.DeploymentException;
import org.jboss.metadata.MetaData;
import org.jboss.metadata.QueryMetaData;

/**
 * This class which contains information about an DynamicQL query.
 *
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 * @version $Revision: 1.5 $
 */
public final class JDBCDynamicQLQueryMetaData implements JDBCQueryMetaData
{
   /**
    * The method to which this query is bound.
    */
   private final Method method;

   /**
    * Should the query return Local or Remote beans.
    */
   private boolean resultTypeMappingLocal;

   /**
    * Read ahead meta data.
    */
   private JDBCReadAheadMetaData readAhead;

   // Jeremy's hack
   private boolean useNewCompiler;
   
   /**
    * Constructs a JDBCDynamicQLQueryMetaData which is invoked by the 
    * specified method.
    * @param method the method which invokes this query
    */
   public JDBCDynamicQLQueryMetaData(Method method) throws DeploymentException
   {
      this.method = method;
      readAhead = JDBCReadAheadMetaData.DEFAULT;
      resultTypeMappingLocal = true;
      useNewCompiler = false;

      Class[] parameterTypes = method.getParameterTypes();
      if(parameterTypes.length != 2 ||
            !parameterTypes[0].equals(String.class) ||
            !parameterTypes[1].equals(Object[].class)) {
         throw new DeploymentException("Dynamic-ql method must have two " +
               "parameters of type String and Object[].");
      }
   }

   public void loadXML(Element queryElement) throws DeploymentException
   {
      useNewCompiler = "true".equals(queryElement.getAttribute("useNewCompiler"));
   }

   // javadoc in parent class
   public Method getMethod()
   {
      return method;
   }

   // javadoc in interface
   public boolean isResultTypeMappingLocal()
   {
      return resultTypeMappingLocal;
   }

   // javadoc in interface
   public void setResultTypeMappingLocal(boolean resultTypeMappingLocal)
   {
      this.resultTypeMappingLocal = resultTypeMappingLocal;
   }
      
   // javadoc in interface
   public JDBCReadAheadMetaData getReadAhead()
   {
      return readAhead;
   }

   // javadoc in interface
   public void setReadAhead(JDBCReadAheadMetaData readAhead)
   {
      this.readAhead = readAhead;
   }

   public boolean useNewCompiler() {
      return useNewCompiler;
   }

   /**
    * Compares this JDBCDynamicQLQueryMetaData against the specified object.
    * Returns true if the objects are the same. Two JDBCDynamicQLQueryMetaData
    * are the same if they are both invoked by the same method.
    * @param o the reference object with which to compare
    * @return true if this object is the same as the object argument; 
    *    false otherwise
    */
   public boolean equals(Object o)
   {
      if(o instanceof JDBCDynamicQLQueryMetaData)
      {
         return ((JDBCDynamicQLQueryMetaData)o).method.equals(method);
      }
      return false;
   }

   /**
    * Returns a hashcode for this JDBCDynamicQLQueryMetaData. The hashcode is
    * computed by the method which invokes this query.
    * @return a hash code value for this object
    */
   public int hashCode()
   {
      return method.hashCode();
   }
   /**
    * Returns a string describing this JDBCDynamicQLQueryMetaData. The exact
    * details of the representation are unspecified and subject to change, but
    * the following may be regarded as typical:
    * 
    * "[JDBCDynamicQLQueryMetaData: method=public org.foo.User
    *       findByName(java.lang.String)]"
    *
    * @return a string representation of the object
    */
   public String toString()
   {
      return "[JDBCDynamicQLQueryMetaData : method=" + method + "]";
   }
}
