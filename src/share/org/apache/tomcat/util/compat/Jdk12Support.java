/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package org.apache.tomcat.util.compat;

import java.net.*;
import java.util.*;
import java.security.*;
import org.apache.tomcat.util.depend.*;
/**
 *  
 */
public class Jdk12Support extends Jdk11Compat {


    /** Return a class loader. For JDK1.2+ will return a URLClassLoader.
     *  For JDK1.1 will return the util.SimepleClassLoader
     */
    public ClassLoader newClassLoaderInstance( URL urls[],
					       ClassLoader parent )
    {
	return URLClassLoader.newInstance( urls, parent );
    }


    public Object doPrivileged( Action action ) throws Exception {
	Object proxy=action.getProxy();
	if( proxy==null ) {
	    proxy=new PrivilegedProxy(action);
	    action.setProxy( proxy );
	}

	try {
	    return AccessController.
		doPrivileged((PrivilegedExceptionAction)proxy);
	} catch( PrivilegedActionException pe ) {
	    Exception e = pe.getException();
	    throw e;
	}
    }

    public void setContextClassLoader( ClassLoader cl ) {
	// we can't doPrivileged here - it'll be a major security
	// problem
	Thread.currentThread().setContextClassLoader(cl);
    }

    public ClassLoader getContextClassLoader() {
	return Thread.currentThread().getContextClassLoader();
    }
    
    public URL[] getURLs(ClassLoader cl,int depth){
        int c=0;
        do{
            while(! (cl instanceof URLClassLoader) && cl != null )
                cl=((DependClassLoader)cl).getParentLoader();
            if (cl==null) break;
            if (depth==c) return ((URLClassLoader)cl).getURLs();
            c++;
            cl=((URLClassLoader)cl).getParent();
        }while((cl!=null) && ( depth >= c ));
        return null;
    }

    public java.util.ResourceBundle getBundle(String name, Locale loc, ClassLoader cl ) {
	if( cl==null )
	    cl=getContextClassLoader();
	if( cl==null )
	    return ResourceBundle.getBundle(name, loc);
	else
	    return ResourceBundle.getBundle(name, loc, cl);
    }
    

    // -------------------- Support --------------------
    static class PrivilegedProxy implements PrivilegedExceptionAction
    {
	Action action;
	PrivilegedProxy( Action act ) {
	    action=act;
	}
	public Object run() throws Exception
	{
	    return action.run();
	}
    }

}
