/*
 * $Header: /tmp/cvs-vintage/struts/src/test/org/apache/struts/mock/MockActionServlet.java,v 1.2 2004/01/10 21:03:39 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/10 21:03:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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
 */


package org.apache.struts.mock;


import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;


import org.apache.struts.action.ActionServlet;

/**
 * <p>Mock <strong>ActionServlet</strong> object for low-level unit tests
 * of Struts controller components.  Coarser grained tests should be
 * implemented in terms of the Cactus framework, instead of the mock
 * object classes.</p>
 *
 * <p><strong>WARNING</strong> - Only getter methods for servletContext and
 * servletConfig are provided, plus additional methods to configure this
 * object as necessary.  Methods for unsupported operations will throw
 * <code>UnsupportedOperationException</code>.</p>
 *
 * <p><strong>WARNING</strong> - Because unit tests operate in a single
 * threaded environment, no synchronization is performed.</p>
 *
 * @version $Revision: 1.2 $ $Date: 2004/01/10 21:03:39 $
 */
public class MockActionServlet extends ActionServlet
{
  protected ServletContext servletContext;
  protected ServletConfig servletConfig;

    /**
     * Constructor.
     */
  public MockActionServlet( ServletContext servletContext, ServletConfig servletConfig  )
  {
  this.servletContext = servletContext;
  this.servletConfig = servletConfig;
  }

    /**
     * Constructor.
     */
  public MockActionServlet( )
  {
  }

    /**
     * Set property
     * @param servletContext
     */
  public void setServletContext( ServletContext servletContext )
  {
  this.servletContext = servletContext;
  }

    /**
     * Get property
     * @return
     */
  public ServletContext getServletContext(  )
  {
  return servletContext;
  }

    /**
     * Set property
     * @param servletConfig
     */
  public void setServletConfig( ServletConfig servletConfig )
  {
  this.servletConfig = servletConfig;
  }

    /**
     * Get property
     * @return
     */
  public ServletConfig getServletConfig(  )
  {
  return servletConfig;
  }
}
