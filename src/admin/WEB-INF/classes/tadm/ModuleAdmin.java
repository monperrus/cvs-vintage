/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.tomcat.core.BaseInterceptor;
import org.apache.tomcat.core.Container;
import org.apache.tomcat.core.Context;
import org.apache.tomcat.core.ContextManager;

/**
 * A context administration class. Contexts can be
 * viewed, added, and removed from the context manager.
 *
 */
public class ModuleAdmin extends TagSupport {
    private ContextManager cm;

    String var;
    String moduleName;
    
    public ModuleAdmin() {}

    /** Will set the "module" attribute, with the instance of the
     *  named interceptor. If "ctx" attribute is set, look in that
     *  context
     */
    public int doStartTag() throws JspException {
	try {
	    cm=(ContextManager)pageContext.getAttribute("cm");
	    if( cm==null )
		throw new JspException( "Can't find context manager" );

	    Context ctx=(Context)pageContext.getAttribute("ctx");

	    Container ct=(ctx==null)? cm.getContainer():ctx.getContainer();
	    BaseInterceptor bi[]=ct.getInterceptors();
	    BaseInterceptor found=null;
	    for( int i=0; i<bi.length; i++ ) {
		String cn=bi[i].getClass().getName();
		if( cn.equals( moduleName )) {
		    found=bi[i];
		    pageContext.setAttribute( var,bi[i],
					      PageContext.PAGE_SCOPE );
		    break;
		}
	    }
	    if( found==null )
		throw new JspException("Can't find module " + moduleName +
				       " in " + ctx );
	} catch (Exception ex ) {
	    ex.printStackTrace();
	}
	return EVAL_BODY_INCLUDE;
    }

    // -------------------- Properties --------------------
    
    public void setVar(String m) {
	var=m;
    }

    public void setType( String m ) {
	moduleName=m;
    }

    // --------------------
    private static int debug=0;
    
    private void log(String s ) {
	System.out.println(s );
    }
}
