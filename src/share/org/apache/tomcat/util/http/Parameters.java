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

package org.apache.tomcat.util.http;

import  org.apache.tomcat.util.*;
import  org.apache.tomcat.util.collections.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * 
 * @author Costin Manolache
 */
public final class Parameters extends MultiMap {
    // Transition: we'll use the same Hashtable( String->String[] )
    // for the beginning. When we are sure all accesses happen through
    // this class - we can switch to MultiMap
    private Hashtable paramHashStringArray=new Hashtable();
    private boolean didQueryParameters=false;
    private boolean didMerge=false;
    
    MessageBytes queryMB;
    MimeHeaders  headers;
    
    public static final int INITIAL_SIZE=4;

    // Garbage-less parameter merging.
    // In a sub-request with parameters, the new parameters
    // will be stored in child. When a getParameter happens,
    // the 2 are merged togheter. The child will be altered
    // to contain the merged values - the parent is allways the
    // original request.
    private Parameters child=null;
    private Parameters parent=null;
    private Parameters currentChild=null;

    /**
     * 
     */
    public Parameters() {
	super( INITIAL_SIZE );
    }

    public void setQuery( MessageBytes queryMB ) {
	this.queryMB=queryMB;
    }

    public void setHeaders( MimeHeaders headers ) {
	this.headers=headers;
    }

    public void recycle() {
	super.recycle();
	paramHashStringArray.clear();
	didQueryParameters=false;
	currentChild=null;
	didMerge=false;
    }
    
    // -------------------- Sub-request support --------------------

    public Parameters getCurrentSet() {
	if( currentChild==null )
	    return this;
	return currentChild;
    }
    
    /** Create ( or reuse ) a child that will be used during a sub-request.
	All future changes ( setting query string, adding parameters )
	will affect the child ( the parent request is never changed ).
	Both setters and getters will return the data from the deepest
	child, merged with data from parents.
    */
    public void push() {
	// We maintain a linked list, that will grow to the size of the
	// longest include chain.
	// The list has 2 points of interest:
	// - request.parameters() is the original request and head,
	// - request.parameters().currentChild() is the current set.
	// The ->child and parent<- links are preserved ( currentChild is not
	// the last in the list )
	
	// create a new element in the linked list
	// note that we reuse the child, if any - pop will not
	// set child to null !
	if( currentChild.child==null ) {
	    currentChild.child=new Parameters();
	    currentChild.child.parent=currentChild;
	} // it is not null if this object already had a child
	// i.e. a deeper include() ( we keep it )

	// the head will be the new element.
	currentChild=currentChild.child;
    }

    /** Discard the last child. This happens when we return from a
	sub-request and the parameters are locally modified.
     */
    public void pop() {
	if( currentChild==null ) {
	    throw new RuntimeException( "Attempt to pop without a push" );
	}
	currentChild.recycle();
	currentChild=currentChild.parent;
	// don't remove the top.
    }
    
    // -------------------- Data access --------------------
    // Access to the current name/values, no side effect ( processing ).
    // You must explicitely call handleQueryParameters and the post methods.
    
    // This is the original data representation ( hash of String->String[])

    public String[] getParameterValues(String name) {
	handleQueryParameters();
	// sub-request
	if( currentChild!=null ) {
	    currentChild.merge();
	    return (String[])currentChild.paramHashStringArray.get(name);
	}

	// no "facade"
	String values[]=(String[])paramHashStringArray.get(name);
	return values;
    }
 
    public Enumeration getParameterNames() {
	handleQueryParameters();
	// Slow - the original code
	if( currentChild!=null ) {
	    currentChild.merge();
	    return currentChild.paramHashStringArray.keys();
	}

	// merge in child
        return paramHashStringArray.keys();
    }

    /** Combine the parameters from parent with our local ones
     */
    private void merge() {
	// recursive

	// Local parameters first - they take precedence as in spec.
	handleQueryParameters();

	// we already merged with the parent
	if( didMerge ) return;

	// we are the top level
	if( parent==null ) return;

	// Add the parent props to the child ( lower precedence )
	Hashtable parentProps=parent.paramHashStringArray;
	merge2( paramHashStringArray , parentProps);
	didMerge=true;
    }


    // Shortcut.
    public String getParameter(String name ) {
	String[] values = getParameterValues(name);
        if (values != null) {
	    if( values.length==0 ) return "";
	    //System.out.println("XXX " + name + "=" + values[0] );
            return values[0];
        } else {
	    //	    System.out.println("XXX " + name + "=null" );
	    return null;
        }
    }
    // -------------------- Processing --------------------

    /** Process the query string into parameters
     */
    public void handleQueryParameters() {
	if( didQueryParameters ) return;
	
	didQueryParameters=true;
	String qString=queryMB.toString();
	if(qString!=null) {
	    processFormData( qString );
	}
    }

    public void processParameters(String data) {
	processFormData( data );
    }
    
    // XXX ENCODING !!
    public void processData(byte data[]) {
	// make sure the request line query is processed
	handleQueryParameters();
	
	try {
	    String postedBody = new String(data, 0, data.length,
					   "8859_1");
	    // XXX encoding !!!

	    processFormData( postedBody );
	    
	    //Hashtable postParameters=new Hashtable();
	    //Parameters.processFormData( postedBody, postParameters);
	    //	    Parameters.merge2(paramHashStringArray,  postParameters);

	} catch( UnsupportedEncodingException ex ) {
	    //	    return postParameters;
	}
    }
    
    // --------------------
    
    /** Combine 2 hashtables into a new one.
     *  ( two will be added to one ).
     *  Used to combine child parameters ( RequestDispatcher's query )
     *  with parent parameters ( original query or parent dispatcher )
     */
    private static void merge2(Hashtable one, Hashtable two ) {
        Enumeration e = two.keys();

	while (e.hasMoreElements()) {
	    String name = (String) e.nextElement();
	    String[] oneValue = (String[]) one.get(name);
	    String[] twoValue = (String[]) two.get(name);
	    String[] combinedValue;

	    if (twoValue == null) {
		continue;
	    } else {
		if( oneValue==null ) {
		    combinedValue = new String[twoValue.length];
		    System.arraycopy(twoValue, 0, combinedValue,
				     0, twoValue.length);
		} else {
		    combinedValue = new String[oneValue.length +
					       twoValue.length];
		    System.arraycopy(oneValue, 0, combinedValue, 0,
				     oneValue.length);
		    System.arraycopy(twoValue, 0, combinedValue,
				     oneValue.length, twoValue.length);
		}
		one.put(name, combinedValue);
	    }
	}
    }

    // XXX XXX Optimize
    private void processFormData(String data) {
        // there's got to be a faster way of doing this.
	if( data==null ) return; // no parameters
	
        StringTokenizer tok = new StringTokenizer(data, "&", false);
        while (tok.hasMoreTokens()) {
            String pair = tok.nextToken();
	    int pos = pair.indexOf('=');
	    if (pos != -1) {
		String key = CharChunk.unescapeURL(pair.substring(0, pos));
		String value = CharChunk.unescapeURL(pair.substring(pos+1,
							     pair.length()));
		String values[];
		if (paramHashStringArray.containsKey(key)) {
		    String oldValues[] = (String[])paramHashStringArray.
			get(key);
		    values = new String[oldValues.length + 1];
		    for (int i = 0; i < oldValues.length; i++) {
			values[i] = oldValues[i];
		    }
		    values[oldValues.length] = value;
		} else {
		    values = new String[1];
		    values[0] = value;
		}
		paramHashStringArray.put(key, values);
	    } else {
		// we don't have a valid chunk of form data, ignore
	    }
        }
    }

    // -------------------- Parameter parsing --------------------

    // This code is not used right now - it's the optimized version
    // of the above. 
    
    /**
     * 
     */
    public void processParameters( byte bytes[], int start, int len ) {
	int end=start+len;
	int pos=start;
	
        do {
	    int nameStart=pos;
	    int nameEnd=ByteChunk.indexOf(bytes, nameStart, end, '=' );
	    if( nameEnd== -1 ) nameEnd=end;
	    
	    int valStart=nameEnd+1;
	    int valEnd=ByteChunk.indexOf(bytes, valStart, end, '&');
	    if( valEnd== -1 ) valEnd=end;
	    
	    pos=valEnd+1;
	    
	    if( nameEnd<=nameStart ) {
		continue;
		// invalid chunk - it's better to ignore
		// XXX log it ?
	    }
	    
	    int field=this.addField();
	    this.getName( field ).setBytes( bytes,
					    nameStart, nameEnd );
	    this.getValue( field ).setBytes( bytes,
					     valStart, valEnd );
	} while( pos<end );
    }

    public void processParameters( char chars[], int start, int len ) {
	int end=start+len;
	int pos=start;
	
        do {
	    int nameStart=pos;
	    int nameEnd=CharChunk.indexOf(chars, nameStart, end, '=' );
	    if( nameEnd== -1 ) nameEnd=end;

	    int valStart=nameEnd+1;
	    int valEnd=CharChunk.indexOf(chars, valStart, end, '&');
	    if( valEnd== -1 ) valEnd=end;
	    pos=valEnd+1;
	    
	    if( nameEnd<=nameStart ) {
		continue;
		// invalid chunk - no name, it's better to ignore
		// XXX log it ?
	    }
	    
	    int field=this.addField();
	    this.getName( field ).setChars( chars,
					    nameStart, nameEnd );
	    this.getValue( field ).setChars( chars,
					     valStart, valEnd );
	} while( pos<end );
    }

    
    public void processParameters( MessageBytes data ) {
	if( data==null || data.getLength() <= 0 ) return;

	if( data.getType() == MessageBytes.T_BYTES ) {
	    ByteChunk bc=data.getByteChunk();
	    processParameters( bc.getBytes(), bc.getOffset(),
			       bc.getLength());
	} else {
	    if (data.getType()!= MessageBytes.T_CHARS ) 
		data.toChars();
	    CharChunk cc=data.getCharChunk();
	    processParameters( cc.getChars(), cc.getOffset(),
			       cc.getLength());
	}
    }

}
