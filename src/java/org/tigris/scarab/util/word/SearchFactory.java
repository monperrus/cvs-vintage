package org.tigris.scarab.util.word;

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

import org.apache.turbine.Turbine;
import org.apache.turbine.Log;

/**
 *  Returns an instance of the SearchIndex specified in Scarab.properties
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id: SearchFactory.java,v 1.12 2002/03/14 23:58:41 jon Exp $
 */
public class SearchFactory
{
    private static Class searchIndex;

    static
    {
        String className = Turbine.getConfiguration()
            .getString(SearchIndex.CLASS_NAME);
        Class si = null;
        try
        {
            si = Class.forName(className);
        }
        catch (Exception e)
        {
            String err;
            if (className == null || className.trim().length() == 0)
            {
                err = "An indexer and search engine has not been specified";
            }
            else
            {
                err = "Unable to to create '" + className + '\'';
            }
            err += ": Text will not be searchable: " + e;
            Log.warn(err);
        }
        searchIndex = si;
    }

    public static SearchIndex getInstance()
        throws InstantiationException
    {
        SearchIndex si = null;
        if (searchIndex != null)
        {
            try
            {
                si = (SearchIndex) searchIndex.newInstance();
            }
            catch (Exception e)
            {
                String str = "Could not create new instance of SearchIndex. " +
                    "Could be a result of insufficient permission " +
                    "to write the Index to the disk. The default is to " +
                    "write the Index into the WEB-INF/index directory.";
                Log.error(str, e);
                throw new InstantiationException(str);
            }
        }
        return si;
    }
}
