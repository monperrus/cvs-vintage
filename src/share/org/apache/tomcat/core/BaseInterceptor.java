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


package org.apache.tomcat.core;

import org.apache.tomcat.core.*;
import org.apache.tomcat.util.*;
import org.apache.tomcat.util.log.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

/** Implement "Chain of Responsiblity" pattern ( == hooks ).
 *
 *  You can extend this class and implement a number of hooks.
 *
 */
public class BaseInterceptor
{
    protected ContextManager cm;
    protected Container ct;
    protected String methods[]=new String[0];
    protected int debug=0;
    protected String name=null;

    public static final int OK=0;
    
    //  loghelper will use name of actual impl subclass
    protected Log loghelper = new Log("tc_log", this);

    public BaseInterceptor() {
    }

    // -------------------- Helpers -------------------- 
    public void setDebug( int d ) {
	debug=d;
    }

    public static final String BASE_I="org.apache.tomcat.core.BaseInterceptor";

    /** Test if the interceptor implements a particular
	method
    */
    public boolean hasHook( String methodN ) {
	// all interceptors will participate in all context-level
	// hooks - no need to exagerate
	if( "engineInit".equals( methodN ) )
	    return true;
	try {
	    Method myMethods[]=this.getClass().getMethods();
	    for( int i=0; i< myMethods.length; i++ ) {
		if( methodN.equals ( myMethods[i].getName() )) {
		    // check if it's overriden
		    Class declaring=myMethods[i].getDeclaringClass();
		    if( ! BASE_I.equals(declaring.getName() )) {
			//log( "Found overriden method " + methodN); 
			return true;
		    }
		}
	    }
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
	return false;
    }

    public void setContextManager( ContextManager cm ) {
	this.cm=cm;
	this.ct=cm.getContainer();
	loghelper.setLogger(cm.getLogger());
    }

    public void setContext( Context ctx ) {
	this.ct=ctx.getContainer();
	loghelper.setLogger(ctx.getLog().getLogger());
    }

    protected void log( String s ) {
	loghelper.log(s);
    }

    protected void log( String s, Throwable t ) {
	loghelper.log(s, t);
    }
    
    protected void log( String s, int level ) {
	loghelper.log(s, level);
    }
    
    protected void log( String s, Throwable t, int level ) {
	loghelper.log(s, t, level);
    }
    
    // -------------------- Request notifications --------------------
    
    /** Handle mappings inside a context.
     *  You are required to respect the mappings in web.xml.
     */
    public int requestMap(Request request ) {
	return 0;
    }
    /** Will detect the context path for a request.
     *  It need to set: context, contextPath, lookupPath
     *
     *  A possible use for this would be a "user-home" interceptor
     *  that will implement ~costin servlets ( add and map them at run time).
     */
    public int contextMap( Request rrequest ) {
	return 0;
    }

    /** 
     *  This callback is used to extract and verify the user identity
     *  and credentials.
     *
     *  It will set the RemoteUser field if it can authenticate.
     *  The auth event is generated by a user asking for the remote
     *  user field of by tomcat if a request requires authenticated
     *  id.
     */
    public int authenticate(Request request, Response response) {
	return 0;
    }

    /**
     *  Will check if the current ( authenticated ) user is authorized
     *  to access a resource, by checking if it have one of the
     *  required roles.
     *
     *  This is used by tomcat to delegate the authorization to modules.
     *  The authorize is called by isUserInRole() and by ContextManager
     *  if the request have security constraints.
     *
     *  @returns 0 if the module can't take a decision
     *           401 If the user is not authorized ( doesn't have
     *               any of the required roles )
     *           200 If the user have the right roles. No further module
     *               will be called.
     */
    public int authorize(Request request, Response response,
			 String reqRoles[]) {
	return 0;
    }

    /** Called before service method is invoked. 
     */
    public int preService(Request request, Response response) {
	return 0;
    }

    /** Called before the first body write, and before sending
     *  the headers. The interceptor have a chance to change the
     *  output headers.
     *
     *  Before body allows you do do various
     *	actions before the first byte of the response is sent. After all
     *  those callbacks are called tomcat may send the status and headers
    */
    public int beforeBody( Request rrequest, Response response ) {
	return 0;
    }

    /** New Session notification - called when the servlet
	asks for a new session. You can do all kind of stuff with
	this notification - the most important is create a session
	object. This will be the base for controling the
	session allocation.
    */
    public int newSessionRequest( Request request, Response response) {
	return 0;
    }
    
    /** Called before the output buffer is commited.
     */
    public int beforeCommit( Request request, Response response) {
	return 0;
    }


    /** Called after the output stream is closed ( either by servlet
     *  or automatically at end of service ).
     *
     * It is called after the servlet finished
     * sending the response ( either closeing the stream or ending ). You
     * can deal with connection reuse or do other actions
    */
    public int afterBody( Request request, Response response) {
	return 0;
    }

    /** Called after service method ends. Log is a particular use.
     */
    public int postService(Request request, Response response) {
	return 0;
    }

    /** Experimental hook: called after the request is finished,
	before returning to the caller. This will be called only
	on the main request, and will give interceptors a chance
	to clean up - that would be difficult in postService,
	that is called after included servlets too.

	Don't use this hook until it's marked final, I added it
	to deal with recycle() in facades - if we find a better
	solution this can go. ( unless people find it
	useful
     */
    public int postRequest(Request request, Response response) {
	return 0;
    }


    /** This callback is called whenever an exception happen.
     *  If t is null assume this is a "status" report ( 500, 404, etc).
     *
     *  During this hook it is possible to create a sub-request
     *  and call the handler, and it is possible that the sub-request
     *  will also generate an exception. The handler must insure
     *  no loops will happen - but it's free to choose whatever method
     *  it wants.
     *
     *  It's also the handler responsiblity to insure correct
     *  servlet API semantics - if the spec becomes incopmatible
     *  with previous versions ( or multiple interpretations are
     *  possible) that can be made a context-specific handler.
     *
     *  @returns 200 if the error was handled ( similar with Apache's
     *           OK )
     *           0   if this handler can't deal with the error ( to
     *           allow chaining )
     */
    public int handleError( Request request, Response response, Throwable t) {
	return 0;
    }
    
    // -------------------- Context notifications --------------------
    /** Notify when a context is initialized.
     *  The first interceptor in the chain for contextInit must read web.xml
     *  and set the context. When this method is called you can expect the
     *  context to be filled in with all the informations from web.xml.
     * 
     *  WebXmlReader needs to be the first interceptor in
     *  the contextInit chain.
     */
    public void contextInit(Context ctx)
	throws TomcatException
    {
    }

    /** Called when a context is stoped, before removeContext.
     *  You must free all resources.
     * XXX  - do we need this or removeContext is enough ?? ( will be
     * removed from 3.1 if nobody asks for it)
     */
    public void contextShutdown(Context ctx)
	throws TomcatException
    {
    }

    /** Notify that certain properties are defined for a URL pattern.
     *  Properties can be a "handler" that will be called for URLs
     *  matching the pattern or "security constraints" ( or any other
     *  properties that can be associated with URL patterns )
     *
     *  Interceptors will maintain their own mapping tables if they are
     *  interested in a certain property. General-purpose mapping
     *  code is provided in utils.
     *
     *  The method will be called once for every properties associated
     *  with a URL - it's up to the interceptor to interpret the URL
     *  and deal with "merging".
     * 
     *  A Container that defines a servlet mapping ( handler ) will have
     *  the handlerName set to the name of the handler. The Handler
     *  ( getHandler) can be null for dynamically added servlets, and
     *  will be set by a facade interceptor.
     *
     *   XXX We use this hook to create ServletWrappers for dynamically
     *  added servlets in InvokerInterceptor ( JspInterceptor is JDK1.2
     *  specific ). It may be good to add a new hook specifically for that
     */
    public void addContainer(Container container)
	throws TomcatException
    {
    }

    /** A rule was removed, update the internal strucures. You can also
     *  clean up and reload everything using Context.getContainers()
     */

    public void removeContainer(Container container)
	throws TomcatException
    {
    }

    /** 
     */
    public void addSecurityConstraint( Context ctx, String path, Container ct )
	throws TomcatException
    {
    }

    /** Called when the ContextManger is started
     */
    public void engineInit(ContextManager cm)
	throws TomcatException
    {
	this.cm=cm;
    }

    /** Called before the ContextManager is stoped.
     *  You need to stop any threads and remove any resources.
     */
    public void engineShutdown(ContextManager cm)
	throws TomcatException
    {
    }


    /**
     *  Called when a context is added to a CM. The context is probably not
     *  initialized yet, only path, docRoot, host, and properties set before adding
     *  the context ( in server.xml for example ) are available.
     * 
     *  At this stage mappers can start creating structures for the
     *  context ( the actual loading of the context may be delayed in
     *  future versions of tomcat until the first access ).
     *
     *  DefaultCMSetter will also adjust the logger and paths
     *  based on context manager properties.
     *
     *  Any activity that depends on web.xml must be done at
     *  init time.
     */
    public void addContext( ContextManager cm, Context ctx )
	throws TomcatException
    {
    }

    /** Called when a context is removed from a CM. A context is removed
     *  either as a result of admin ( remove or update), to support "clean"
     *  servlet reloading or at shutdown.
     */
    public void removeContext( ContextManager cm, Context ctx )
	throws TomcatException
    {
    }

    /** Reload notification - called whenever a reload is done.
	This can be used to serialize sessions, log the event,
	remove any resource that was class-loader dependent.
     */
    public void reload( Request req, Context ctx)
	throws TomcatException
    {
    }

    /** Servlet Init  notification
     */
    public void preServletInit( Context ctx, Handler sw )
	throws TomcatException
    {
    }

    
    public void postServletInit( Context ctx, Handler sw )
	throws TomcatException
    {
    }

    /** Servlet Destroy  notification
     */
    public void preServletDestroy( Context ctx, Handler sw )
	throws TomcatException
    {
    }

    
    public void postServletDestroy( Context ctx, Handler sw )
	throws TomcatException
    {
    }

}
