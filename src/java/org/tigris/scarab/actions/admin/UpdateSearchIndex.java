package org.tigris.scarab.actions.admin;

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


// Turbine Stuff 
import org.apache.turbine.TemplateContext;
import org.apache.turbine.RunData;

// Scarab Stuff
import org.tigris.scarab.om.ScarabUser;
import org.tigris.scarab.util.ScarabConstants;
import org.tigris.scarab.tools.ScarabRequestTool;
import org.tigris.scarab.tools.ScarabLocalizationTool;
import org.tigris.scarab.actions.base.RequireLoginFirstAction;
import org.tigris.scarab.util.word.SearchFactory;
import org.tigris.scarab.util.word.SearchIndex;

/**
 * This class allows an admin to update the search index. It performs
 * its magic by creating a background thread which executes until it is
 * finished. The page will continue to refresh until the thread is 
 * done executing.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:jon@collab.net">Jon Scott Stevens</a>
 * @version $Id: UpdateSearchIndex.java,v 1.4 2002/12/20 20:36:55 jon Exp $
 */
public class UpdateSearchIndex extends RequireLoginFirstAction
{
    private static ThreadGroup tg = null;

    public void doPerform( RunData data, TemplateContext context )
        throws Exception
    {
        ScarabRequestTool scarabR = getScarabRequestTool(context);
        ScarabLocalizationTool l10n = getLocalizationTool(context);
        
        synchronized (this)
        {
            if (tg == null)
            {
                try
                {
                    tg = new ThreadGroup("UpdateIndex");
                    Thread updateThread = new Thread(tg, new UpdateThread());
                    updateThread.start();
                    context.put("updateFrequency", "5");
                    scarabR.setConfirmMessage(l10n.get("SearchIndexDoNoteLeavePage"));
                }
                catch (Exception e)
                {
                    tg = null;
                    scarabR.setAlertMessage(e.getMessage());            
                }
            }
            else if (tg.activeCount() == 0)
            {
                tg = null;
                scarabR.setConfirmMessage(l10n.get("SearchIndexUpdated"));
            }
            else
            {
                context.put("updateFrequency", "5");
                scarabR.setConfirmMessage(l10n.get("SearchIndexDoNoteLeavePage"));
            }
        }

        String template = getCurrentTemplate(data, null);
        String nextTemplate = getNextTemplate(data, template);
        setTarget(data, nextTemplate);
    }

    public class UpdateThread implements Runnable
    {
        public UpdateThread()
        {
        }

        public void run()
        {
            try
            {
                SearchIndex indexer = SearchFactory.getInstance();
                indexer.updateIndex();
            }
            catch (Exception e)
            {
                log().debug("Update index failed:", e);
            }
        }
    }
}
