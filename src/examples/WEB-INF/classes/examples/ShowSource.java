package examples;


import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.io.*;

/**
 * Display the sources of the JSP file.
 */
public class ShowSource
    extends TagSupport
{
    String jspFile;
    
    public void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }

    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();

	if (jspFile.indexOf( ".." ) >= 0) {
	    try {
		out.println("<body><h1>Invalid file " + jspFile + "</h1></body>");
	    } catch (IOException ex) {
		throw new JspTagException("IOException: "+ex.toString());
	    }
	    
	    return EVAL_PAGE;
	}

        InputStream in
            = pageContext.getServletContext().getResourceAsStream(jspFile);

        if (in == null)
            throw new JspTagException("Unable to find JSP file: "+jspFile);
        InputStreamReader reader = new InputStreamReader(in);
        

        try {
            out.println("<body>");
            out.println("<pre>");
            for(int ch = in.read(); ch != -1; ch = in.read())
                if (ch == '<')
                    out.print("&lt;");
                else
                    out.print((char) ch);
            out.println("</pre>");
            out.println("</body>");
        } catch (IOException ex) {
            throw new JspTagException("IOException: "+ex.toString());
        }
        return super.doEndTag();
    }
}

    
        
    
