/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.aspect.interceptors;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jboss.aspect.AspectInitizationException;
import org.jboss.aspect.internal.AspectSupport;
import org.jboss.aspect.spi.AspectInterceptor;
import org.jboss.aspect.spi.AspectInvocation;
import org.jboss.util.Classes;

/**
 * The DelegatingInterceptor allows you delegate method calls to 
 * another class, the delegate, instead of sending method calls 
 * to the base class.
 * 
 * The delegate object is layz loaded the first time a method is 
 * invoked on the delegate.
 * 
 * This Interceptor will add all the interfaces of the delegate to
 * the aspect object.
 * 
 * This interceptor uses the following configuration attributes:
 * <ul>
 * <li>delegate - class name of the object that will be used to delegate
 *                method calls to.  This is a required attribute.
 * <li>singleton - if set to "true", then the method calls of multiple
 *                aspect object will be directed to a single instance of
 *                the delegate.  This makes the delegate a singleton. 
 * </ul>
 * 
 * @author <a href="mailto:hchirino@jboss.org">Hiram Chirino</a>
 * 
 */
public class DelegatingInterceptor implements AspectInterceptor, Serializable
{

    public static final Namespace NAMESPACE = Namespace.get(DelegatingInterceptor.class.getName());
    public static final QName ATTR_DELEGATE = new QName("delegate", NAMESPACE);
    public static final QName ATTR_SIGLETON = new QName("singleton", NAMESPACE);

    public Class implementingClass;
    transient public Object singeltonObject;
    transient public Class[] interfaces;
    transient public Set exposedMethods;

    /**
     * @see com.chirino.aspect.AspectInterceptor#invoke(AspectInvocation)
     */
    public Object invoke(AspectInvocation invocation) throws Throwable
    {

        Object delegate = null;
        if (singeltonObject != null)
        {
            delegate = singeltonObject;
        }
        else
        {
            Map attachments = invocation.attachments;
            delegate = attachments.get(this);
            if (delegate == null)
            {
                delegate = implementingClass.newInstance();
                attachments.put(this, delegate);
            }
        }

        try
        {
            return invocation.method.invoke(delegate, invocation.args);
        }
        catch (InvocationTargetException e)
        {
            throw e.getTargetException();
        }
    }

    /**
     * @see com.chirino.aspect.AspectInterceptor#init(Map)
     */
    public void init(Element xml) throws AspectInitizationException
    {
        try
        {

            String className = xml.attribute(ATTR_DELEGATE).getValue();
            implementingClass = Classes.loadClass(className);
            interfaces = implementingClass.getInterfaces();
            exposedMethods = AspectSupport.getExposedMethods(interfaces);

            String singlton = xml.attribute(ATTR_SIGLETON) == null ? null : xml.attribute(ATTR_SIGLETON).getValue();

            if ("true".equals(singlton))
                singeltonObject = implementingClass.newInstance();

        }
        catch (Exception e)
        {
            throw new AspectInitizationException("Aspect Interceptor missconfigured.", e);
        }
    }

    /**
     * @see AspectInterceptor#getInterfaces()
     */
    public Class[] getInterfaces()
    {
        return interfaces;
    }

    public boolean isIntrestedInMethodCall(Method method)
    {
        return exposedMethods.contains(method);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
    	out.writeObject(implementingClass);
    	out.writeBoolean(singeltonObject!=null);
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
    	implementingClass = (Class)in.readObject();
        interfaces = implementingClass.getInterfaces();
        exposedMethods = AspectSupport.getExposedMethods(interfaces);
		if( in.readBoolean() ) {
            try
            {
                singeltonObject = implementingClass.newInstance();
            }
            catch (Exception e) 
            {
            	throw new IOException(e.getMessage());
            }
		}    	
    }

}
