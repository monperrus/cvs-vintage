/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/action/Attic/ActionMappings.java,v 1.2 2000/06/30 01:19:32 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2000/06/30 01:19:32 $
 *
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
 */


package org.apache.struts.action;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Encapsulate a collection of ActionMapping objects that can be
 * administered and searched, while hiding the internal implementation.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2000/06/30 01:19:32 $
 */

public class ActionMappings {


    // ----------------------------------------------------- Instance Variables


    /**
     * The collection of ActionMapping instances, keyed by request path.
     */
    private Hashtable mappings = new Hashtable();


    // --------------------------------------------------------- Public Methods


    /**
     * Register a logical mapping to the set configured for this servlet.
     *
     * @param mapping The mapping to be added
     */
    public void addMapping(ActionMapping mapping) {

	mappings.put(mapping.getPath(), mapping);

    }


    /**
     * Return the mapping associated with the specified logical name,
     * if any; otherwise return <code>null</code>.
     *
     * @param path The request path for which to retrieve a mapping
     */
    public ActionMapping findMapping(String path) {

	return ((ActionMapping) mappings.get(path));

    }


    /**
     * Return the set of paths for mappings defined in this collection.
     * If there are no such mappings, a zero-length array is returned.
     */
    public String[] findMappings() {

	Vector paths = new Vector();
	Enumeration keys = mappings.keys();
	while (keys.hasMoreElements())
	    paths.addElement(keys.nextElement());
	String results[] = new String[paths.size()];
	paths.copyInto(results);
	return (results);

    }


    /**
     * Deregister a mapping from the set configured for this servlet.
     *
     * @param mapping The mapping to be deregistered
     */
    public void removeMapping(ActionMapping mapping) {

	mappings.remove(mapping.getPath());

    }


}
