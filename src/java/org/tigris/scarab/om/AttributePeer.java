package org.tigris.scarab.om;

/* ================================================================
 * Copyright (c) 2000-2002 CollabNet.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement: "This product includes
 * software developed by Collab.Net <http://www.Collab.Net/>."
 * Alternately, this acknowlegement may appear in the software itself, if
 * and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The hosted project names must not be used to endorse or promote
 * products derived from this software without prior written
 * permission. For written permission, please contact info@collab.net.
 * 
 * 5. Products derived from this software may not use the "Tigris" or 
 * "Scarab" names nor may "Tigris" or "Scarab" appear in their names without 
 * prior written permission of Collab.Net.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL COLLAB.NET OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Collab.Net.
 */ 

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import org.apache.torque.om.NumberKey;

import org.apache.torque.util.Criteria;
import org.tigris.scarab.services.cache.ScarabCache;

/** 
  * The skeleton for this class was autogenerated by Torque on:
  *
  * [Wed Feb 28 16:36:26 PST 2001]
  *
  *  You should add additional methods to this class to meet the
  *  application requirements.  This class will only be generated as
  *  long as it does not already exist in the output directory.
  */
public class AttributePeer 
    extends org.tigris.scarab.om.BaseAttributePeer
{
    public static final NumberKey ASSIGNED_TO__PK = new NumberKey("2");
    public static final NumberKey STATUS__PK = new NumberKey("3");
    public static final NumberKey RESOLUTION__PK = new NumberKey("4");
    public static final NumberKey TOTAL_VOTES__PK = new NumberKey("13");
    public static final String EMAIL_TO = "to";
    public static final String CC_TO = "cc";

    private static final String ATTRIBUTE_PEER = 
        "AttributePeer";
    private static final String GET_ATTRIBUTES = 
        "getAttributes";
    private static final String GET_ALL_ATTRIBUTES = 
        "getAllAttributes";

    /**
     *  Gets a List of all of the Attribute objects in the database.
     */
    public static List getAllAttributes(String sortColumn, 
                                        String sortPolarity)
        throws Exception
    {
        List result = null;
        Object obj = ScarabCache.get(ATTRIBUTE_PEER, GET_ALL_ATTRIBUTES); 
        if ( obj == null ) 
        {        
            Criteria crit = new Criteria();
            crit.add(AttributePeer.ATTRIBUTE_ID, 0, Criteria.NOT_EQUAL);
            if (sortColumn.equals("desc"))
            {
                addSortOrder(crit, AttributePeer.DESCRIPTION, 
                             sortPolarity);
            }
            else if (sortColumn.equals("date"))
            {
                addSortOrder(crit, AttributePeer.CREATED_DATE, 
                             sortPolarity);
            }
            else if (sortColumn.equals("type"))
            {
                crit.addJoin(AttributePeer.ATTRIBUTE_TYPE_ID, 
                             AttributeTypePeer.ATTRIBUTE_TYPE_ID);
                addSortOrder(crit, AttributeTypePeer .ATTRIBUTE_TYPE_NAME, 
                             sortPolarity);
            }
            else
            {
                addSortOrder(crit, AttributePeer.ATTRIBUTE_NAME, 
                             sortPolarity);
            }
            result = doSelect(crit);
        }
        else 
        {
            result = (List)obj;
        }
        if (sortColumn.equals("user"))
        {
            result = sortAttributesByCreatingUser(result, sortPolarity);
        }
                
        ScarabCache.put(result, ATTRIBUTE_PEER, GET_ALL_ATTRIBUTES);
        return result;
    }

    private static List sortAttributesByCreatingUser(List result,
                                                     String sortPolarity)
        throws Exception
    {
        final int polarity = ("asc".equals(sortPolarity)) ? 1 : -1;   
        Comparator c = new Comparator() 
        {
            public int compare(Object o1, Object o2) 
            {
                int i = 0;
                try
                {
                    i = polarity * 
                        ((Attribute)o1).getCreatedUserName()
                         .compareTo(((Attribute)o2).getCreatedUserName());
                }
                catch (Exception e)
                {
                    //
                }
                return i;
             }
        };
        Collections.sort(result, c);
        return result;
    }

    private static Criteria addSortOrder(Criteria crit, 
                       String sortColumn, String sortPolarity)
    {
        if (sortPolarity.equals("desc"))
        {
            crit.addDescendingOrderByColumn(sortColumn);
        }
        else
        {
            crit.addAscendingOrderByColumn(sortColumn);
        }
        return crit;
    }

    /**
     *  Gets a List of all of the Attribute objects in the database.
     *  Sorts on selected column.
     */
    public static List getAllAttributes()
        throws Exception
    {
        return getAllAttributes(AttributePeer.ATTRIBUTE_NAME, "asc");
    }

    /**
     *  Gets a List of all of the Attribute objects in the database.
     */
    public static List getAttributes(String attributeType)
        throws Exception
    {
        List result = null;
        Object obj = ScarabCache.get(ATTRIBUTE_PEER, GET_ATTRIBUTES, 
                                     attributeType); 
        if ( obj == null ) 
        {        
            Criteria crit = new Criteria();
            crit.add(AttributePeer.ATTRIBUTE_ID, 0, Criteria.NOT_EQUAL);
            if (attributeType.equals("user"))
            {
                crit.add(AttributePeer.ATTRIBUTE_TYPE_ID, 
                         AttributeTypePeer.USER_TYPE_KEY);
            }
            else
            {
                crit.add(AttributePeer.ATTRIBUTE_TYPE_ID, 
                         AttributeTypePeer.USER_TYPE_KEY, Criteria.NOT_EQUAL);
            }
            result = doSelect(crit);
            ScarabCache.put(result, ATTRIBUTE_PEER, GET_ATTRIBUTES, 
                                     attributeType);
        }
        else 
        {
            result = (List)obj;
        }
        return result;
    }
}
