/*
 *  Copyright 2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package tadm;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class TomcatAdminTEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data) {
	return (new VariableInfo[] {
	    new VariableInfo("cm", "org.apache.tomcat.core.ContextManager",
			     true,  VariableInfo.AT_BEGIN),
	    new VariableInfo("ctx", "org.apache.tomcat.core.Context",
			     true,  VariableInfo.AT_BEGIN),
	    new VariableInfo("module",
			     "org.apache.tomcat.core.BaseInterceptor",
			     true,  VariableInfo.AT_BEGIN)
	});

    }


}
