package org.tigris.scarab.om;

/* ================================================================
 * Copyright (c) 2000-2001 CollabNet.  All rights reserved.
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

// JDK classes
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

// Turbine classes
import org.apache.torque.om.Persistent;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.Criteria;
import org.apache.fulcrum.cache.TurbineGlobalCacheService;
import org.apache.fulcrum.cache.ObjectExpiredException;
import org.apache.fulcrum.cache.CachedObject;
import org.apache.fulcrum.cache.GlobalCacheService;
import org.apache.fulcrum.TurbineServices;

import org.tigris.scarab.util.ScarabException;

/** 
  * This class represents the SCARAB_R_OPTION_OPTION table.
  *
  * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
  * @version $Id: Attribute.java,v 1.27 2001/09/11 03:41:45 jon Exp $
  */
public class Attribute 
    extends BaseAttribute
    implements Persistent
{
    private static final String className = "Attribute";
    
    private static final String SELECT_ONE = "select-one";
    
    /** should be cloned to use */
    private static Criteria moduleOptionsCriteria;

    private HashMap optionsMap;
    private List attributeOptionsWithDeleted;
    private List attributeOptionsWithoutDeleted;
    private static HashMap optionAttributeMap = new HashMap();

    static
    {
        moduleOptionsCriteria = new Criteria();
        moduleOptionsCriteria
            .addAscendingOrderByColumn(RModuleOptionPeer.PREFERRED_ORDER);
        moduleOptionsCriteria
            .addAscendingOrderByColumn(RModuleOptionPeer.DISPLAY_VALUE);
    }

    /**
     * Must call getInstance()
     */
    protected Attribute()
    {
    }

    static String getCacheKey(ObjectKey key)
    {
         String keyString = key.getValue().toString();
         return new StringBuffer(className.length() + keyString.length())
             .append(className).append(keyString).toString();
    }

    /**
     * A new Attribute
     */
    public static Attribute getInstance() 
    {
        return new Attribute();
    }

    /**
     * Return an instance of Attribute based on the passed in attribute id
     */
    public static Attribute getInstance(ObjectKey attId) 
        throws Exception
    {
        TurbineGlobalCacheService tgcs = 
            (TurbineGlobalCacheService)TurbineServices
            .getInstance().getService(GlobalCacheService.SERVICE_NAME);

        String key = getCacheKey(attId);
        Attribute attribute = null;
        try
        {
            attribute = (Attribute)tgcs.getObject(key).getContents();
        }
        catch (ObjectExpiredException oee)
        {
            try
            {
                attribute = AttributePeer.retrieveByPK(attId);
            }
            catch (Exception e)
            {
                throw new ScarabException("Attribute with ID " + attId + 
                                          " can not be found");
            }
            tgcs.addObject(key, new CachedObject(attribute));
        }
        return attribute;
    }

    /**
     * Return an instance based on the passed in attribute id as an int
     */
    public static Attribute getInstance(int id) 
        throws Exception
    {
        return getInstance((ObjectKey)new NumberKey(id));
    }

    /**
     * Little method to return a List of all Attribute Type's.
     * It is here for convenience with regards to needing this
     * functionality from within a Template.
     */
    public static List getAllAttributeTypes()
        throws Exception
    {
        return AttributeTypePeer.doSelect(new Criteria());
    }

    /**
     * get a list of all of the Attributes in the database
     */
    public static List getAllAttributes()
        throws Exception
    {
        return AttributePeer.doSelect(new Criteria());
    }

    /**
     * This method is special. Don't use it. 
     * It is used to generate the mappings for r_option_option
     * table mappings because AO's have to have a mapping in 
     * there in order to be looked up using the getOrderedROptionOptionList()
     * method. This method has already been run and the output was
     * used to copy/paste into the scarab-default-data.sql file.
     * It is being kept here in case it is needed again someday.
    public static void createROptionOptionMapping()
        throws Exception
    {
        List attributes = Attribute.getAllAttributes();
        for (int i=0; i<attributes.size();i++)
        {
            Attribute attr = (Attribute) attributes.get(i);
            if (attr.getName().equals("Operating System") || 
                attr.getName().equals("Null Attribute"))
            {
                continue;
            }
            System.out.println ("Attribute: " + attr.getName());
            List attributeOptions = attr.getAttributeOptions();
            Iterator itr = attributeOptions.iterator();
            int counter = 1;
            while (itr.hasNext())
            {
                AttributeOption ao = (AttributeOption) itr.next();
                System.out.println ("\tAttribute Option: " + ao.getName());
                ROptionOption roo = ROptionOption.getInstance();
                roo.setOption1Id(new NumberKey(0));
                roo.setOption2Id(ao.getOptionId());
                roo.setRelationshipId(new NumberKey(1));
                roo.setPreferredOrder(counter++);
                roo.setDeleted(false);
                roo.save();
            }
        }
    }
     */

/****************************************************************************/
/* Attribute Option Methods                                                 */
/****************************************************************************/

    /**
     * For a given Attribute Option id, get the matching Attribute
     */
    public static Attribute getAttributeForOption(NumberKey optionId)
    {
        return (Attribute)optionAttributeMap.get(optionId);
    }

    /**
     * Gets one of the options belonging to this attribute. if the 
     * PrimaryKey does not belong to an option in this attribute
     * null is returned.
     *
     * @param pk a <code>NumberKey</code> value
     * @return an <code>AttributeOption</code> value
     */
    public AttributeOption getAttributeOption(NumberKey pk)
        throws Exception
    {
        if (optionsMap == null)
        {
            buildOptionsMap();
        }
        return (AttributeOption)optionsMap.get(pk);
    }

    /**
     * Get an option by String id
     */
    public AttributeOption getAttributeOption(String optionID)
        throws Exception
    {
        return getAttributeOption(new NumberKey(optionID));
    }

    /**
     * Used internally to get a list of Attribute Options
     */
    private List getAllAttributeOptions()
        throws Exception
    {
        return new ArrayList(super.getAttributeOptions());    
    }

    /**
     * 
     */
    public List getParentChildAttributeOptions()
        throws Exception
    {
        List rooList = getOrderedROptionOptionList();
        List aoList = getOrderedAttributeOptionList();
        List pcaoList = new ArrayList(rooList.size());
        for (int i=0; i<rooList.size();i++)
        {
            ROptionOption roo = (ROptionOption)rooList.get(i);
            AttributeOption ao = (AttributeOption)aoList.get(i);

            ParentChildAttributeOption pcao = ParentChildAttributeOption
                    .getInstance(roo.getOption1Id(), roo.getOption2Id());
            pcao.setParentId(roo.getOption1Id());
            pcao.setOptionId(roo.getOption2Id());
            pcao.setPreferredOrder(roo.getPreferredOrder());
            pcao.setName(ao.getName());
            pcao.setDeleted(ao.getDeleted());
            pcao.setWeight(ao.getWeight());
            pcao.setAttributeId(this.getAttributeId());
            pcaoList.add(pcao);
        }
        return pcaoList;
    }

    /**
     * Creates an ordered List of ROptionOption which are
     * children within this AO. The list is ordered according 
     * to the preferred order.
     *
     * @return a List of ROptionOption's
     */
    public List getOrderedROptionOptionList()
        throws Exception
    {
        Criteria crit = new Criteria();
        crit.addJoin(AttributeOptionPeer.OPTION_ID, ROptionOptionPeer.OPTION2_ID);
        crit.add(AttributeOptionPeer.ATTRIBUTE_ID, this.getAttributeId());
        crit.addAscendingOrderByColumn(ROptionOptionPeer.PREFERRED_ORDER);
        List rooList = ROptionOptionPeer.doSelect(crit);
        return rooList;
    }

    /**
     * Creates an ordered List of AttributeOptions which are
     * children within this AO. The list is ordered according 
     * to the preferred order.
     *
     * @return a List of AttributeOption's
     */
    public List getOrderedAttributeOptionList()
        throws Exception
    {
        Criteria crit = new Criteria();
        crit.addJoin(AttributeOptionPeer.OPTION_ID, ROptionOptionPeer.OPTION2_ID);
        crit.add(AttributeOptionPeer.ATTRIBUTE_ID, this.getAttributeId());
        crit.addAscendingOrderByColumn(ROptionOptionPeer.PREFERRED_ORDER);
        return AttributeOptionPeer.doSelect(crit);
    }

    /**
     * Get a list of all attribute options or just the ones
     * that have not been marked as deleted.
     */
    public List getAttributeOptions(boolean includeDeleted)
        throws Exception
    {
        if ( includeDeleted ) 
        {
            if (attributeOptionsWithDeleted == null)
            {
                buildOptionsMap();
            }
            return attributeOptionsWithDeleted;
        }
        else 
        {
            if (attributeOptionsWithoutDeleted == null)
            {
                buildOptionsMap();
            }
            return attributeOptionsWithoutDeleted; 
        }
    }

    /**
     * Build a list of options.
     */
    public synchronized void buildOptionsMap()
        throws Exception
    {
        if ( this.getAttributeType().getAttributeClass().getName()
             .equals(SELECT_ONE) ) 
        {
            // synchronized method due to getattributeOptionsWithDeleted, this needs
            // further investigation !FIXME!
            attributeOptionsWithDeleted = this.getAllAttributeOptions();
            optionsMap = new HashMap((int)(1.25*attributeOptionsWithDeleted.size()+1));
    
            attributeOptionsWithoutDeleted = new ArrayList(attributeOptionsWithDeleted.size());
            for ( int i=0; i<attributeOptionsWithDeleted.size(); i++ ) 
            {
                AttributeOption option = (AttributeOption)attributeOptionsWithDeleted.get(i);
                optionsMap.put(option.getOptionId(), option);
                optionAttributeMap.put(option.getOptionId(), this);
                if ( !option.getDeleted() ) 
                {
                    attributeOptionsWithoutDeleted.add(attributeOptionsWithDeleted.get(i));
                }
            }
        }
    }
}
