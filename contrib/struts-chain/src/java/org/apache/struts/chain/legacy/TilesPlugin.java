/*
 * \$Header\$
 * \$Revision\$
 * \$Date\$
 *
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.chain.legacy;

/**
 * Permissive version of TilesPlugin which doesn't mess with processor class.
 * Completely extends TilesPlugIn except for overriding protected method
 * <code>initRequestProcessorClass(ModuleConfig)</code> to a no-op.
 * Note class name capitalization consistent with org.apache.struts.tiles.TilesPlugin
 * rather than most other Struts plug-ins to avoid snagging folks who quickly
 * edit their struts-config.
 */
public class TilesPlugin extends org.apache.struts.tiles.TilesPlugin
{
    protected void initRequestProcessorClass(org.apache.struts.config.ModuleConfig config)
    {
        log.debug("Chain version of TilesPlugIn doesn't care what RequestProcessor you use!");
    }
}