/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/tiles/PathAttribute.java,v 1.7 2004/03/14 06:23:43 sraeburn Exp $
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

/**
 * Component attribute.
 * Attribute value represents a path used to include a JSP.
 */
public class PathAttribute extends UntypedAttribute {

    public PathAttribute(String value) {
        super(value);
    }

}
