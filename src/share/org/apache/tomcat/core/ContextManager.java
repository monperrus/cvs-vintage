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
import org.apache.tomcat.net.*;
import org.apache.tomcat.request.*;
import org.apache.tomcat.util.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * A collection class representing the Contexts associated with a particular
 * Server.  The managed Contexts can be accessed by path.
 *
 * It also store global default properties - the server name and port ( returned by
 * getServerName(), etc) and workdir.
 *
 * @author James Duncan Davidson [duncan@eng.sun.com]
 * @author James Todd [gonzo@eng.sun.com]
 * @author Harish Prabandham
 */
public class ContextManager {
    /**
     * The string constants for this ContextManager.
     */
    private StringManager sm =StringManager.getManager("org.apache.tomcat.core");

    int debug=0;
    
    private Vector requestInterceptors = new Vector();
    private Vector contextLifecycleInterceptors = new Vector();
    
    /**
     * The set of Contexts associated with this ContextManager,
     * keyed by context paths.
     */
    private Hashtable contexts = new Hashtable();

    public static final String DEFAULT_HOSTNAME="localhost";
    public static final int DEFAULT_PORT=8080;
    public static final String DEFAULT_WORK_DIR="work";
    
    /**
     * The virtual host name for the Server this ContextManager
     * is associated with.
     */
    String hostname;

    /**
     * The port number being listed to by the Server this ContextManager
     * is associated with.
     */
    int port;

    String workDir;
    
    Vector connectors=new Vector();

    
    /**
     * Construct a new ContextManager instance with default values.
     */
    public ContextManager() {
    }

    // -------------------- Context repository --------------------
    /**
     * Get the names of all the contexts in this server.
     */
    public Enumeration getContextNames() {
        return contexts.keys();
    }

    public void start() throws Exception {

	// set a default connector ( http ) if none defined yet
	if(connectors.size()==0) {
	    // Make the default customizable!
	    addServerConnector(  new org.apache.tomcat.service.http.HttpAdapter() );
	}
	
	for( int i=0; i<connectors.size(); i++ ) {
	    ((ServerConnector)connectors.elementAt(i)).setContextManager( this );
	    ((ServerConnector)connectors.elementAt(i)).start();
	}
	
	// check for default context 
	Context defaultContext=getContext("");
	if (defaultContext == null ||
	    defaultContext.getDocumentBase() == null) {
	    // XXX find a better exception 
	    throw new IllegalArgumentException("No default context " + defaultContext);
	}

	if( requestInterceptors.size() == 0 ) {
	    // nothing set up by starter, add default ones
	    if(debug>0) log("Setting default interceptors " + requestInterceptors);
	    addRequestInterceptor(new ContextMapperInterceptor( this ));
	    addRequestInterceptor(new SessionInterceptor());
	    //	    addRequestInterceptor(new MapperInterceptor());

	    // Use the simplified mapper - revert if too many bugs and
	    addRequestInterceptor(new SimpleMapper());
	}
	
	// init contexts
	Enumeration enum = getContextNames();
	while (enum.hasMoreElements()) {
            Context context = getContext((String)enum.nextElement());
            context.init();
	}
    }

    public void stop() throws Exception {
	System.out.println("Stopping context manager ");
	for (int i=0; i<connectors.size(); i++) {
	    ((ServerConnector)connectors.elementAt(i)).stop();
	}

	Enumeration enum = getContextNames();
	while (enum.hasMoreElements()) {
	    Context context =
	        getContext((String)enum.nextElement());
	    
	    System.out.println("Taking down context: " +
			       context.getPath());
	    
	    context.shutdown();
	}
	// same behavior as in past, because it seems that
	// stopping everything doesn't work - need to figure
	// out what happens with the threads ( XXX )
	System.exit(0);
    }

    /**
     * Gets a context by it's name, or <code>null</code> if there is
     * no such context.
     *
     * @param name Name of the requested context
     */
    public Context getContext(String name) {
	return (Context)contexts.get(name);
    }


    
    /**
     * Adds a new Context to the set managed by this ContextManager.
     *
     * @param ctx context to be added.
     */
    public void addContext( Context ctx ) {
	ctx.setContextManager( this );
	// assert "valid path" 

	// it will replace existing context - it's better than 
	// IllegalStateException.
	String path=ctx.getPath();
	if(debug>0) log(" adding " + ctx + " " + ctx.getPath() + " " +  ctx.getDocBase());
	contexts.put( path, ctx );
    }
    
    /**
     * Shut down and removes a context from service.
     *
     * @param name Name of the Context to be removed
     */
    public void removeContext(String name) {
	if (name.equals("")){
	    throw new IllegalArgumentException(name);
	}

	Context context = (Context)contexts.get(name);

	if(context != null) {
	    context.shutdown();
	    contexts.remove(name);
	}
    }


    // -------------------- Connectors and Interceptors --------------------

    /**
     * Add the specified server connector to the those attached to this server.
     *
     * @param con The new server connector
     */
    public synchronized void addServerConnector( ServerConnector con ) {
	if(debug>0) log(" adding connector " + con.getClass().getName());
	connectors.addElement( con );
    }

    public void addRequestInterceptor( RequestInterceptor ri ) {
	if(debug>0) log(" adding request intereptor " + ri.getClass().getName());
	requestInterceptors.addElement( ri );
    }
    
    // -------------------- Defaults for all contexts --------------------
    
    /**
     * Sets the port number on which this server listens.
     *
     * @param port The new port number
     */
    public void setPort(int port) {
	this.port=port;
    }

    /**
     * Gets the port number on which this server listens.
     */
    public int getPort() {
	if(port==0) port=DEFAULT_PORT;
	return port;
    }

    /**
     * Sets the virtual host name of this server.
     *
     * @param host The new virtual host name
     */
    public void setHostName( String host) {
	this.hostname=host;
    }

    /**
     * Gets the virtual host name of this server.
     */
    public String getHostName() {
	if(hostname==null)
	    hostname=DEFAULT_HOSTNAME;
	return hostname;
    }

    /**
     * WorkDir property - where all temporary files will be created
     */ 
    public void setWorkDir( String wd ) {
	if( debug>0) log( "set work dir " + wd );
	this.workDir=wd;
    }

    public String getWorkDir() {
	if( workDir==null)
	    workDir=DEFAULT_WORK_DIR;
	return workDir;
    }
    
    // -------------------- Request processing / subRequest --------------------
    
    /** Common for all connectors, needs to be shared in order to avoid
	code duplication
    */
    public void service( Request rrequest, Response rresponse ) {
	try {
	    rrequest.setResponse(rresponse);
	    rresponse.setRequest(rrequest);

	    // XXX
	    //    return if an error was detected in processing the
	    //    request line
	    if (rresponse.getStatus() >= 400) {
		rresponse.finish();
		rrequest.recycle();
		rresponse.recycle();
		return;
	    }

	    //    don't do headers if request protocol is http/0.9
	    if (rrequest.getProtocol() == null) {
		rresponse.setOmitHeaders(true);
	    }

	    // XXX Hardcoded - it will be changed in the next step.( costin )

	    processRequest( rrequest );

	    if( rrequest.getWrapper() == null ) {
		System.out.println("ERROR: mapper returned no wrapper ");
		System.out.println(rrequest );
		// XXX send an error - it shouldn't happen, mapper is broken
	    } else {
		// do it
		rrequest.getWrapper().handleRequest(rrequest.getFacade(),
						    rresponse.getFacade());
	    }
	    
	    // finish and clean up
	    rresponse.finish();
	    
	    // protocol notification
	    rresponse.endResponse();
	    
	} catch (Exception e) {
	    // XXX
	    // this isn't what we want, we want to log the problem somehow
	    System.out.println("HANDLER THREAD PROBLEM: " + e);
	    e.printStackTrace();
	}
    }

    // XXX need to be changed to use a full sub-request model (costin)
    
    /** Will find the ServletWrapper for a servlet, assuming we already have
     *  the Context. This is used by Dispatcher and getResource - where the Context
     *  is already known.
     */
    int processRequest( Request req ) {

	if(debug>2) log( "ProcessRequest: ");
	if(debug>2) log( req.toString() );
	if(debug>2) log("");

	for( int i=0; i< requestInterceptors.size(); i++ ) {
	    ((RequestInterceptor)requestInterceptors.elementAt(i)).handleRequest( req );
	}

	if(debug>2) log("After processing: ");
	if(debug>2) log( req.toString() );
	if(debug>2) log("");
	return 0;
    }

    // XXX XXX hack - we need to create a new request !
    ContextMapperInterceptor contextInterceptor=new ContextMapperInterceptor( this );
    public Context getContextByPath(String path ) {
	// XXX XXX XXX need to create a sub-request !!!!
	//
	return contextInterceptor.getContextByPath( path );      
    }


    // -------------------- Sub-Request mechanism --------------------

    // comment from Apache http_request.c
    /*****************************************************************
     *
     * The sub_request mechanism.
     *
     * Fns to look up a relative URI from, e.g., a map file or SSI document.
     * These do all access checks, etc., but don't actually run the transaction
     * ... use run_sub_req below for that.  Also, be sure to use destroy_sub_req
     * as appropriate if you're likely to be creating more than a few of these.
     * (An early Apache version didn't destroy the sub_reqs used in directory
     * indexing.  The result, when indexing a directory with 800-odd files in
     * it, was massively excessive storage allocation).
     *
     * Note more manipulation of protocol-specific vars in the request
     * structure...
     */
    Request createRequest( String method, String uri, Request orig ) {
	//  See: ap_sub_req_method_uri()

	// "clone" the usefull info out of req

	// clean up uri ( remove .., etc )

	// location() - find the context

	//  interceptors

	return null;
    }

    /** Create a new sub-request in a given context, set the context "hint"
     *  This is a particular case of sub-request that can't get out of
     *  a context ( and we know the context before - so no need to compute it again)
     *
     *  Note that session and all stuff will still be computed.
     */
    Request createRequest( Context ctx, String urlPath ) {
	// assert urlPath!=null

	// deal with paths with parameters in it
	String queryString=null;
	int i = urlPath.indexOf("?");
	int len=urlPath.length();
	if (i>-1) {
	    if(i<len)
		queryString =urlPath.substring(i + 1, urlPath.length());
	    urlPath = urlPath.substring(0, i);
	}

	/** Creates an "internal" request
	 */
	RequestImpl lr = new RequestImpl();
	RequestAdapterImpl reqA=new RequestAdapterImpl();
	lr.setRequestAdapter( reqA);
	lr.setLookupPath( urlPath );
	lr.setQueryString( queryString );
	//	lr.processQueryString();

	lr.setContext( ctx );
	
	// XXX set query string too 
	return lr;
    }

    void runSubRequest( Request req ) {
	// invoke_handler

	// finalize_sub_req_protocol
	
    }

    // Debug ( to be replaced with the real thing )
    public void setDebug( int level ) {
	log( "Setting debug " + level );
	debug=level;
    }

    void log( String msg ) {
	System.out.println("CM: " + msg );
    }

    
}
