/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/taglib/html/MessagesTei.java,v 1.4 2004/03/14 06:23:46 sraeburn Exp $
 * $Revision: 1.4 $
 * $Date: 2004/03/14 06:23:46 $
 *
 * Copyright 1999,2004 The Apache Software Foundation.
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


package org.apache.struts.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;


/**
 * Implementation of <code>TagExtraInfo</code> for the <b>messages</b>
 * tag, identifying the scripting object(s) to be made visible.
 *
*/
public class MessagesTei extends TagExtraInfo {


    /**
     * Return information about the scripting variables to be created.
     */
    public VariableInfo[] getVariableInfo(TagData data) {
        String type = "java.lang.String";

	return new VariableInfo[] {
	  new VariableInfo(data.getAttributeString("id"),
	                   type,
	                   true,
	                   VariableInfo.NESTED)
	};

    }


}
