/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/action/ActionMessages.java,v 1.16 2003/12/21 22:55:38 husted Exp $
 * $Revision: 1.16 $
 * $Date: 2003/12/21 22:55:38 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
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
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

/**
 * <p>A class that encapsulates messages. Messages can be either global
 * or they are specific to a particular bean property.</p>
 *
 * <p>Each individual message is described by an <code>ActionMessage</code>
 * object, which contains a message key (to be looked up in an appropriate
 * message resources database), and up to four placeholder arguments used for
 * parametric substitution in the resulting message.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - It is assumed that these objects
 * are created and manipulated only within the context of a single thread.
 * Therefore, no synchronization is required for access to internal
 * collections.</p>
 *
 * @author David Geary
 * @author Craig R. McClanahan
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.16 $ $Date: 2003/12/21 22:55:38 $
 * @since Struts 1.1
 */
public class ActionMessages implements Serializable {


    /**
     * <p>Compares ActionMessageItem objects.</p>
     */
    private static final Comparator actionItemComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((ActionMessageItem) o1).getOrder()
                - ((ActionMessageItem) o2).getOrder();
        }
    };


    // ----------------------------------------------------- Manifest Constants


    /**
     * <p>The "property name" marker to use for global messages, as opposed to
     * those related to a specific property.</p>
     */
	public static final String GLOBAL_MESSAGE =
		"org.apache.struts.action.GLOBAL_MESSAGE";


    // ----------------------------------------------------- Instance Variables


    /**
     * <p>Have the messages been retrieved from this object?</p>
     *
     * <p>The controller uses this property to determine if session-scoped
     * messages can be removed.</p>
     *
     * @since Struts 1.2
     */
    protected boolean accessed = false;


    /**
     * <p>The accumulated set of <code>ActionMessage</code> objects (represented
     * as an ArrayList) for each property, keyed by property name.</p>
     */
    protected HashMap messages = new HashMap();


    /**
     * <p>The current number of the property/key being added. This is used
     * to maintain the order messages are added.</p>
     */
    protected int iCount = 0;


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Create an empty <code>ActionMessages</code> object.</p>
     */
    public ActionMessages() {

        super();

    }


    /**
     * <p>Create an <code>ActionMessages</code> object initialized with the given
     * messages.</p>
     * 
     * @param messages The messages to be initially added to this object.
     * This parameter can be <code>null</code>.
     * @since Struts 1.1
     */
    public ActionMessages(ActionMessages messages) {
        super();
        this.add(messages);
    }


    /**
     * <p>Add a message to the set of messages for the specified property. An
     * order of the property/key is maintained based on the initial addition
     * of the property/key.</p>
     *
     * @param property  Property name (or ActionMessages.GLOBAL_MESSAGE)
     * @param message   The message to be added
     */
    public void add(String property, ActionMessage message) {

        ActionMessageItem item = (ActionMessageItem) messages.get(property);
        List list = null;

        if (item == null) {
            list = new ArrayList();
            item = new ActionMessageItem(list, iCount++);

            messages.put(property, item);
        } else {
            list = item.getList();
        }

        list.add(message);

    }


    /**
     * <p>Adds the messages from the given <code>ActionMessages</code> object to
     * this set of messages. The messages are added in the order they are returned from
     * the <code>properties</code> method. If a message's property is already in the current
     * <code>ActionMessages</code> object, it is added to the end of the list for that
     * property. If a message's property is not in the current list it is added to the end
     * of the properties.</p>
     * 
     * @param messages The <code>ActionMessages</code> object to be added.  
     * This parameter can be <code>null</code>.
     * @since Struts 1.1
     */
    public void add(ActionMessages messages) {

        if (messages == null) {
            return;
        }
        
        // loop over properties
        Iterator props = messages.properties();
        while (props.hasNext()) {
            String property = (String) props.next();

            // loop over messages for each property
            Iterator msgs = messages.get(property);
            while (msgs.hasNext()) {
                ActionMessage msg = (ActionMessage) msgs.next();
                this.add(property, msg);
            }
        }
    }


    /**
     * <p>Clear all messages recorded by this object.</p>
     */
    public void clear() {

        messages.clear();

    }


    /**
     * <p>Return <code>true</code> if there are no messages recorded
     * in this collection, or <code>false</code> otherwise.</p>
     *
     * @since Struts 1.1
     */
    public boolean isEmpty(){

        return (messages.isEmpty());


    }


    /**
     * <p>Return the set of all recorded messages, without distinction
     * by which property the messages are associated with. If there are
     * no messages recorded, an empty enumeration is returned.</p>
     */
    public Iterator get() {

        this.accessed = true;
        
        if (messages.isEmpty()) {
            return Collections.EMPTY_LIST.iterator();
        }

        ArrayList results = new ArrayList();
        ArrayList actionItems = new ArrayList();

        for (Iterator i = messages.values().iterator(); i.hasNext();) {
            actionItems.add(i.next());
        }

        // Sort ActionMessageItems based on the initial order the
        // property/key was added to ActionMessages.
        Collections.sort(actionItems, actionItemComparator);

        for (Iterator i = actionItems.iterator(); i.hasNext();) {
            ActionMessageItem ami = (ActionMessageItem) i.next();

            for (Iterator messages = ami.getList().iterator(); messages.hasNext();) {
                results.add(messages.next());
            }
        }

        return results.iterator();
    }


    /**
     * <p>Return the set of messages related to a specific property.
     * If there are no such messages, an empty enumeration is returned.</p>
     *
     * @param property Property name (or ActionMessages.GLOBAL_MESSAGE)
     */
    public Iterator get(String property) {

        this.accessed = true;
        
        ActionMessageItem item = (ActionMessageItem) messages.get(property);

        if (item == null) {
            return (Collections.EMPTY_LIST.iterator());
        } else {
            return (item.getList().iterator());
        }
        
    }


    /**
     * <p>Returns <code>true</code> if the <code>get()</code> or
     * <code>get(String)</code> methods are called.</p>
     *
     * @return <code>true</code> if the messages have been accessed one or more
     * times.
     * @since Struts 1.2
     */
    public boolean isAccessed() {

        return this.accessed;

    }


    /**
     * <p>Return the set of property names for which at least one message has
     * been recorded. If there are no messages, an empty <code>Iterator</code> is returned.
     * If you have recorded global messages, the <code>String</code> value of
     * <code>ActionMessages.GLOBAL_MESSAGE</code> will be one of the returned
     * property names.</p>
     */
    public Iterator properties() {

        return this.messages.keySet().iterator();

    }


    /**
     * <p>Return the number of messages recorded for all properties (including
     * global messages). <strong>NOTE</strong> - it is more efficient to call
     * <code>isEmpty</code> if all you care about is whether or not there are
     * any messages at all.</p>
     */
    public int size() {

        int total = 0;

        for (Iterator i = messages.values().iterator(); i.hasNext();) {
            ActionMessageItem ami = (ActionMessageItem) i.next();
            total += ami.getList().size();
        }

        return (total);

    }


    /**
     * <p>Return the number of messages associated with the specified property.</p>
     *
     * @param property Property name (or ActionMessages.GLOBAL_MESSAGE)
     */
	public int size(String property) {

		ActionMessageItem item = (ActionMessageItem) messages.get(property);

		return (item == null) ? 0 : item.getList().size();
	}


    /**
     * <p>Returns a String representation of this ActionMessages'
     * property name=message list mapping.</p>
     * @see java.lang.Object#toString()
     */    
	public String toString() {
        return this.messages.toString();
	}


    /**
     * <p>This class is used to store a set of messages associated with a
     * property/key and the position it was initially added to list.</p>
     */
    protected class ActionMessageItem implements Serializable {


        /**
         * <p>The list of <code>ActionMessage</code>s.</p>
         */
        protected List list = null;


        /**
         * <p>The position in the list of messages.</p>
         */
        protected int iOrder = 0;


        public ActionMessageItem(List list, int iOrder) {
            this.list = list;
            this.iOrder = iOrder;
        }


        public List getList() {
            return list;
        }


        public void setList(List list) {
            this.list = list;
        }


        public int getOrder() {
            return iOrder;
        }


        public void setOrder(int iOrder) {
            this.iOrder = iOrder;
        }


        public String toString() {
            return this.list.toString();
        }

    }

}
