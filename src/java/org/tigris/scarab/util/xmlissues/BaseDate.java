package org.tigris.scarab.util.xmlissues;

/* ================================================================
 * Copyright (c) 2000-2003 CollabNet.  All rights reserved.
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
 * software developed by CollabNet <http://www.Collab.Net/>."
 * Alternately, this acknowlegement may appear in the software itself, if
 * and wherever such third-party acknowlegements normally appear.
 *
 * 4. The hosted project names must not be used to endorse or promote
 * products derived from this software without prior written
 * permission. For written permission, please contact info@collab.net.
 *
 * 5. Products derived from this software may not use the "Tigris" or
 * "Scarab" names nor may "Tigris" or "Scarab" appear in their names without
 * prior written permission of CollabNet.
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
 * individuals on behalf of CollabNet.
 */

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * <p><code>BaseDate</code> is a base class for Modified and Created dates.</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon Scott Stevens</a>
 * @version $Id: BaseDate.java,v 1.4 2003/03/15 21:56:59 jon Exp $
 */
public class BaseDate implements java.io.Serializable
{
    private String format = null;
    private String timestamp = null;

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getFormat()
    {
        return this.format;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }

    public Date getDate()
        throws java.text.ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(getFormat());
        return sdf.parse(getTimestamp());
    }

    public String toString()
    {
        return "format=" + format + ";timestamp=" + timestamp;
    }
}
