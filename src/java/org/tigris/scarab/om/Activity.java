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

// Turbine classes
import org.apache.torque.om.Persistent;
import org.apache.torque.om.NumberKey;

// import org.apache.turbine.om.peer.BasePeer;
// import org.apache.turbine.util.Log;
// import org.apache.turbine.util.db.pool.DBConnection;
import org.tigris.scarab.om.Attribute;

/** 
  * The skeleton for this class was autogenerated by Torque on:
  *
  * [Wed Feb 28 16:36:26 PST 2001]
  *
  * You should add additional methods to this class to meet the
  * application requirements.  This class will only be generated as
  * long as it does not already exist in the output directory.

  */
public class Activity 
    extends BaseActivity
    implements Persistent
{
    private Attribute aAttribute;                 
    private Transaction aTransaction;                 
    private Attachment aAttachment;                 
    private AttributeOption oldAttributeOption;                 
    private AttributeOption newAttributeOption;                 


    /**
     * Gets the Attribute that was changed for this Activity record.
     */
    public Attribute getAttribute() throws Exception
    {
        if ( aAttribute==null && (getAttributeId() != null) )
        {
            aAttribute = Attribute.getInstance(getAttributeId());
            
            // make sure the parent attribute is in synch.
            super.setAttribute(aAttribute);            
        }
        return aAttribute;
    }

    /**
     * Sets the Attribute that was changed for this Activity record.
     */
    public void setAttribute(Attribute v) throws Exception
    {
        aAttribute = v;
        super.setAttribute(v);
    }

    /**
     * Populates a new Activity object.
     */
    public void create(Issue issue, Attribute attribute, 
                       String desc, Transaction transaction,
                       Attachment attachment,
                       NumberKey oldOptionId, NumberKey newOptionId,
                       String oldValue, String newValue)
         throws Exception
    {
            setIssue(issue);
            if (attribute == null)
            {
                attribute = Attribute.getInstance(0);
            }
            setAttribute(attribute);
            setDescription(desc);
            setTransaction(transaction);
            if (attachment != null) setAttachment(attachment);
            setOldOptionId(oldOptionId);
            setNewOptionId(newOptionId);
            setOldValue(oldValue);
            setNewValue(newValue);
            save();
    }

    /**
     * Gets the Transaction object associated with this Activity record
     */
    public Transaction getTransaction() throws Exception
    {
        if ( aTransaction==null && (getTransactionId() != null) )
        {
            aTransaction = TransactionPeer.retrieveByPK(new NumberKey(getTransactionId()));
            
            // make sure the parent attribute is in synch.
            super.setTransaction(aTransaction);            
        }
        return aTransaction;
    }

    /**
     * Sets the Transaction object associated with this Activity record
     */
    public void setTransaction(Transaction v) throws Exception
    {
        aTransaction = v;
        super.setTransaction(v);
    }

    /**
     * Gets the Attachment associated with this Activity record
     */
    public Attachment getAttachment() throws Exception
    {
      try{
        if ( aAttachment==null && (getAttachmentId() != null) )
        {
            aAttachment = AttachmentPeer.retrieveByPK(new NumberKey(getAttachmentId()));
            
            // make sure the parent attribute is in synch.
            super.setAttachment(aAttachment);            
        }
      } catch (Exception e) {
         aAttachment = null;
      }
        return aAttachment;
    }


    /**
     * Sets the Attachment associated with this Activity record
     */
    public void setAttachment(Attachment v) throws Exception
    {
        aAttachment = v;
        super.setAttachment(v);
    }


    /**
     * Gets the AttributeOption object associated with the Old Value field
     * (i.e., the old value for the attribute before the change.)
     */
    public AttributeOption getOldAttributeOption() throws Exception
    {
        if ( oldAttributeOption==null && (getOldValue() != null) )
        {
            oldAttributeOption = AttributeOptionPeer.retrieveByPK(new NumberKey(getOldValue()));
        }
        return oldAttributeOption;
    }

    /**
     * Sets the Old Attribute Option associated with this Activity record
    public void setOldAttributeOption(AttributeOption v) throws Exception
    {
        oldAttributeOption  = v;
        super.setOldValue(v);
    }
     */

    /**
     * Gets the AttributeOption object associated with the New Value field
     * (i.e., the new value for the attribute after the change.)
     */
    public AttributeOption getNewAttributeOption() throws Exception
    {
        if ( newAttributeOption==null && (getNewValue() != null) )
        {
            newAttributeOption = AttributeOptionPeer.retrieveByPK(new NumberKey(getNewValue()));
        }
        return newAttributeOption;
    }

    /**
     * Sets the New Attribute Option associated with this Activity record
    public void setNewAttributeOption(AttributeOption v) throws Exception
    {
        newAttributeOption  = v;
        super.setNewValue(v);
    }
     */

    
}



