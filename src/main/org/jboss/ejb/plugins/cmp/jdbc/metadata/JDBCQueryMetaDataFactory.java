/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc.metadata;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;

import org.jboss.ejb.DeploymentException;
import org.jboss.metadata.MetaData;
import org.jboss.metadata.QueryMetaData;

/**
 * JDBCQueryMetaDataFactory constructs a JDBCQueryMetaData object based
 * on the query specifiection type.
 *    
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 *	@version $Revision: 1.1 $
 */
public class JDBCQueryMetaDataFactory {
	private JDBCEntityMetaData entity;
	
	public JDBCQueryMetaDataFactory(JDBCEntityMetaData entity) {
		this.entity = entity;
	}
	
	public JDBCQueryMetaData createJDBCQueryMetaData(QueryMetaData queryMetaData, Method method) throws DeploymentException  {
		return new JDBCQlQueryMetaData(queryMetaData, method, entity);		
	}

	public JDBCQueryMetaData createJDBCQueryMetaData(JDBCQueryMetaData jdbcQueryMetaData, Element queryElement, Method method) throws DeploymentException {
		Element rawSql = MetaData.getOptionalChild(queryElement, "raw-sql");
		if(rawSql != null) {
			return new JDBCRawSqlQueryMetaData(jdbcQueryMetaData, rawSql, method, entity);
		}
		
		Element delcaredSql = MetaData.getOptionalChild(queryElement, "declared-sql");
		if(delcaredSql != null) {
			return new JDBCDeclaredQueryMetaData(jdbcQueryMetaData, delcaredSql, method, entity);
		}
		
		Element ejbQl = MetaData.getOptionalChild(queryElement, "ejb-ql");
		if(ejbQl != null) {
			return new JDBCQlQueryMetaData(jdbcQueryMetaData, ejbQl, method, entity);
		}
		
		if(jdbcQueryMetaData == null) {
			throw new DeploymentException("Error in query spedification for method " + method.getName());
		}
		return jdbcQueryMetaData;
	}

	public Method[] getQueryMethods(Element queryElement) throws DeploymentException {
		// query-method sub-element
		Element queryMethod = MetaData.getUniqueChild(queryElement, "query-method");
		
		// method name
		String methodName = MetaData.getElementContent(MetaData.getUniqueChild(queryMethod, "method-name"));
		
		// method interface
		String methodIntf = MetaData.getElementContent(MetaData.getOptionalChild(queryMethod, "method-intf"));
		if(methodIntf!=null && !QueryMetaData.HOME.equals(methodIntf) &&
				!QueryMetaData.LOCAL_HOME.equals(methodIntf)) {
			throw new DeploymentException("result-type-mapping must be '" + 
							QueryMetaData.HOME + "', '" + 
							QueryMetaData.LOCAL_HOME + "', if specified");
		}

		// method params
		ArrayList methodParams = new ArrayList();
		Element methodParamsElement = MetaData.getUniqueChild(queryMethod, "method-params");
		Iterator iterator = MetaData.getChildrenByTagName(methodParamsElement, "method-param");			
		while (iterator.hasNext()) {
			methodParams.add(MetaData.getElementContent((Element)iterator.next()));
		}
      Class[] parameters = convertToJavaClasses(methodParams.iterator());
		
		return getQueryMethods(methodName, parameters, methodIntf);
	}

   public Method[] getQueryMethods(QueryMetaData queryData) throws DeploymentException {
		String methodName = queryData.getMethodName();
		Class[] parameters = convertToJavaClasses(queryData.getMethodParams());
		String methodIntf = queryData.getMethodName();
		return getQueryMethods(methodName, parameters, methodIntf);
	}

	public Method[] getQueryMethods(String methodName, Class parameters[], String methodIntf) 
			throws DeploymentException {
				
		// find the query and load the xml
		ArrayList methods = new ArrayList(2);
		if(methodName.startsWith("ejbSelect")) {
			// bean method
			methods.add(getQueryMethod(methodName, parameters, entity.getEntityClass()));
		} else {
			// interface element
			if(methodIntf == null || methodIntf.equals(QueryMetaData.HOME)) {
				methods.add(getQueryMethod(methodName, parameters, entity.getHomeClass()));
			} else if(methodIntf == null || methodIntf.equals(QueryMetaData.LOCAL_HOME)) {
				methods.add(getQueryMethod(methodName, parameters, entity.getLocalHomeClass()));
			}
		}          

		if(methods.size() == 0) {
			throw new DeploymentException("Query method not found: " + methodName);
		}
		return (Method[])methods.toArray(new Method[methods.size()]);
	}
		
	protected Method getQueryMethod(String queryName, Class[] parameters, Class clazz) {
		try {
			Method method  = clazz.getMethod(queryName, parameters);

			// is the method abstract?  (remember interface methods are always abstract)
			if(Modifier.isAbstract(method.getModifiers())) {
				return method;
			}
		} catch(NoSuchMethodException e) {
			// that's cool
		}
		return null;
	}
   
	protected Class[] convertToJavaClasses(Iterator iter) throws DeploymentException {
		ArrayList classes = new ArrayList();
		while(iter.hasNext()) {
			classes.add(convertToJavaClass((String)iter.next()));
		}
		return (Class[]) classes.toArray(new Class[classes.size()]);
	}
	
	protected static final String[] PRIMITIVES = {
			"boolean",
			"byte",
			"char",
			"short",
			"int",
			"long",
			"float",
			"double"};
	
	protected static final Class[] PRIMITIVE_CLASSES = {
			Boolean.TYPE,
			Byte.TYPE,
			Character.TYPE,
			Short.TYPE,
			Integer.TYPE,
			Long.TYPE,
			Float.TYPE,
			Double.TYPE};

	protected Class convertToJavaClass(String name) throws DeploymentException {
		// Check primitive first
		for (int i = 0; i < PRIMITIVES.length; i++) {
			if(name.equals(PRIMITIVES[i])) {
				return PRIMITIVE_CLASSES[i];
			}
		}
		
		try {
			return entity.getClassLoader().loadClass(name);
		} catch(ClassNotFoundException e) {
			throw new DeploymentException("Parameter class not found: " + name);
		}
	}
}
