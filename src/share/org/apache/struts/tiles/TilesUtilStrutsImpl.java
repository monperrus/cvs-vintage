/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/tiles/TilesUtilStrutsImpl.java,v 1.7 2004/03/14 06:23:43 sraeburn Exp $
 * $Revision: 1.7 $
 * $Date: 2004/03/14 06:23:43 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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

package org.apache.struts.tiles;

import javax.servlet.ServletContext;

import org.apache.struts.config.ModuleConfig;

/**
 * TilesUtil implementation for Struts 1.1 with one single factory.
 * This class contains default implementation of utilities. This implementation
 * is intended to be used with Struts 1.1.
 * This class is used as the base class for all Struts 1.1 implementations of TilesUtil.
 */
public class TilesUtilStrutsImpl extends TilesUtilImpl {

    /**
     * Get definition factory for the module attached to the specified moduleConfig.
     * @param servletContext Current servlet context
     * @param moduleConfig Module config of the module for which the factory is requested.
     * @return Definitions factory or null if not found.
     */
    public DefinitionsFactory getDefinitionsFactory(
        ServletContext servletContext,
        ModuleConfig moduleConfig) {
            
        return (DefinitionsFactory) servletContext.getAttribute(DEFINITIONS_FACTORY);
    }

}