/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc.metadata;

import java.lang.reflect.Method;
import org.jboss.metadata.QueryMetaData;
import org.w3c.dom.Element;

/**
 * Imutable class which contains information about an EJB QL query. 
 *   
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 *	@version $Revision: 1.2 $
 */
public final class JDBCQlQueryMetaData implements JDBCQueryMetaData {
   private final Method method;
	private final String ejbQl;
	
	/**
	 * Constructs a JDBCQlQueryMetaData which is defined by the queryMetaData
	 * and is invoked by the specified method.
	 * @param queryMetaData the metadata about this query which was loaded from the ejb-jar.xml file
	 * @param method the method which invokes this query
	 */
	public JDBCQlQueryMetaData(QueryMetaData queryMetaData, Method method) {
      this.method = method;
		ejbQl = queryMetaData.getEjbQl();
	}

	/**
	 * Constructs a JDBCQlQueryMetaData with data from the jdbcQueryMetaData
	 * and additional data from the xml element
	 * @param queryMetaData the metadata about this query 
	 * @param queryElement the ejb-ql element which contains additional information about this query
	 */
	public JDBCQlQueryMetaData(JDBCQlQueryMetaData jdbcQueryMetaData, Element queryElement, Method method) {
      this.method = method;
		ejbQl = jdbcQueryMetaData.getEjbQl();
	}
	
	public Method getMethod() {
		return method;
	}

	/**
	 * Gets the EJB QL query which will be invoked.
	 * @return the ejb ql String for this query
	 */
	public String getEjbQl() {
		return ejbQl;
	}
	
	/**
	 * Compares this JDBCQlQueryMetaData against the specified object. Returns
	 * true if the objects are the same. Two JDBCQlQueryMetaData are the same 
	 * if they are both invoked by the same method.
	 * @param o the reference object with which to compare
	 * @return true if this object is the same as the object argument; false otherwise
	 */
	public boolean equals(Object o) {
		if(o instanceof JDBCQlQueryMetaData) {
			return ((JDBCQlQueryMetaData)o).method.equals(method);
		}
		return false;
	}
	
	/**
	 * Returns a hashcode for this JDBCQlQueryMetaData. The hashcode is computed
	 * by the method which invokes this query.
	 * @return a hash code value for this object
	 */
	public int hashCode() {
		return method.hashCode();
	}
	/**
	 * Returns a string describing this JDBCQlQueryMetaData. The exact details
	 * of the representation are unspecified and subject to change, but the following
	 * may be regarded as typical:
	 * 
	 * "[JDBCQlQueryMetaData: method=public org.foo.User findByName(java.lang.String)]"
	 *
	 * @return a string representation of the object
	 */
	public String toString() {
		return "[JDBCQlQueryMetaData : method=" + method + "]";
	}
}
