/*
 * @(#) MultiOrbInitialContext.java	1.0 02/07/15
 *
 * Copyright (C) 2002 - INRIA (www.inria.fr)
 *
 * CAROL: Common Architecture for RMI ObjectWeb Layer
 *
 * This library is developed inside the ObjectWeb Consortium,
 * http://www.objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 *
 */
package org.objectweb.carol.jndi.spi;

//java import
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.objectweb.carol.util.configuration.TraceCarol;
import org.objectweb.carol.util.multi.ProtocolCurrent;
/*
 * Class <code>MultiOrbInitialContext</code> is the CAROL JNDI SPI Context for multi Context management.
 * this class use the protocol current for management of multi protocol 
 * 
 * @author  Guillaume Riviere (Guillaume.Riviere@inrialpes.fr)
 * @see javax.naming.Context
 * @see javax.naming.InitialContext
 * @see org.objectweb.util.multi.ProtocolCurrent
 * @version 1.0, 15/07/2002
 */
public class MultiOrbInitialContext implements Context {

	/**
	 * The ProtocolCurrent for management of active Context
	 */
	 private ProtocolCurrent pcur = null; 
	 
    /**
     * Active Contexts, this variable is just a cache of the protocol current context array 
     */
    private Hashtable activesInitialsContexts = null;

    /**
     * String for rmi name 
     */
    private String rmiName = null;
    
    /**
     * Constructor,
     * load communication framework
     * and instaciate initial contexts
     */
    public MultiOrbInitialContext (Hashtable env) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.MultiOrbInitialContext()");
        }
	try {
	    pcur = ProtocolCurrent.getCurrent();
	    activesInitialsContexts = pcur.getNewContextHashtable(env);
	} catch (Exception e) {
	    String msg = "MultiOrbInitialContext.MultiOrbInitialContext() failed: " + e; 
	    throw new NamingException(msg);
	}
    }


    // Inital context wrapper see the Context documentation for this methods
    public Object  lookup( String name ) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.lookup(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().lookup(encode(name));
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
            String msg = "MultiOrbInitialContext.lookup(\""+name+"\") failed: " + e; 
            TraceCarol.debugJndiCarol("Error: " + msg);
		}
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
		throw new NamingException(e.toString() + "\nCaused by: " + sw.toString() + "\n<End of Cause>");
	}
	
    }
    
    public Object lookup(Name name) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.lookup(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().lookup(encode(name) );
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.lookup(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public void bind(String name, Object obj) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.bind(\""+name+"\","+simpleClass(obj.getClass().getName())+" object)");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);
		((Context)activesInitialsContexts.get(rmiName)).bind(encode(name), obj);	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.bind(\""+name+"\","+simpleClass(obj.getClass().getName())+" object) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public void bind(Name name, Object obj) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.bind(\""+name+"\","+simpleClass(obj.getClass().getName())+" object)");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).bind(encode(name), obj);	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.bind(Name name, Object obj) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }

    public void rebind(String name, Object obj) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.rebind(\""+name+"\","+simpleClass(obj.getClass().getName())+" object)");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);
		((Context)activesInitialsContexts.get(rmiName)).rebind(encode(name), obj);	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.rebind(String name, Object obj) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }

    public void rebind(Name name, Object obj) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.rebind(\""+name+"\","+simpleClass(obj.getClass().getName())+" object)");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).rebind(encode(name), obj);	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.rebind(Name name, Object obj) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}	
    }
    
    public void unbind(String name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.unbind(\""+name+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).unbind(encode(name));	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.unbind(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}	
    }

    public void unbind(Name name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.unbind(\""+name+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).unbind(encode(name));	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.unbind(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}	
    }

    public void rename(String oldName, String newName) throws NamingException {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.rename(\""+oldName+"\",\""+newName+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).rename(encode(oldName), encode(newName));	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.rename(String oldName, String newName) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}	
    }

    public void rename(Name oldName, Name newName) throws NamingException  {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.rename(\""+oldName+"\",\""+newName+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName))
			.rename(encode(oldName), encode(newName));	
		pcur.setDefault();
	    }
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.rename(Name oldName, Name newName) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}	
    }
    
    public NamingEnumeration list(String name) throws NamingException {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.list(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
		// must decode the individually returned names
		return(
			new WrapEnum(
				pcur.getCurrentInitialContext().list(encode(name)) )
		);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.list(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public NamingEnumeration list(Name name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.list(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
		// must decode the individually returned names
		return(
			new WrapEnum(
				pcur.getCurrentInitialContext().list(encode(name)) )
		);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.list(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public NamingEnumeration listBindings(String name)
	throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.listBindings(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
		// must decode the individually returned names
		return(
			new WrapEnum(
				pcur.getCurrentInitialContext().list(encode(name)) )
		);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.listBindings(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public NamingEnumeration listBindings(Name name)
	throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.listBindings(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
		// must decode the individually returned names
		return(
			new WrapEnum(
				pcur.getCurrentInitialContext().list(encode(name)) )
		);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.listBindings(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }

    public void destroySubcontext(String name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.destroySubcontext(\""+name+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).destroySubcontext(encode(name));	
		pcur.setDefault();
	    } 
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.destroySubcontext(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}   
    }

    public void destroySubcontext(Name name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.destroySubcontext(\""+name+"\")");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).destroySubcontext(encode(name.toString()));	
		pcur.setDefault();
	    }  
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.destroySubcontext(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	} 
    }
    
    public Context createSubcontext(String name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.createSubcontext(\""+name+"\")");
        }
	try {
	    return pcur.getCurrentInitialContext().createSubcontext(encode(name));
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg ="MultiOrbInitialContext.createSubcontext(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }
    
    public Context createSubcontext(Name name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.createSubcontext(\""+name+"\")");
        }
	try {
	    return pcur.getCurrentInitialContext().createSubcontext(encode(name));
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.createSubcontext(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }
    
    public Object lookupLink(String name) throws NamingException  {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.lookupLink(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().lookupLink(encode(name));
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.lookupLink(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}
	    throw e;
	}
    }

    public Object lookupLink(Name name) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.lookupLink(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().lookupLink(encode(name));
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.lookupLink(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);	 
		}   
	    throw e;
	}
    }

    public NameParser getNameParser(String name) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.getNameParser(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().getNameParser(name);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.getNameParser(String name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);	
		}    
	    throw e;
	}
    } 
    
    public NameParser getNameParser(Name name) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.getNameParser(\""+name+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().getNameParser(name);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.getNameParser(Name name) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }
    
    public String composeName(String name, String prefix)
	throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.composeName("+name+","+prefix+")/rmi name="+pcur.getCurrentRMIName());
        }
	return name;
    }
    
    public Name composeName(Name name, Name prefix) throws NamingException {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.composeName(\""+name+","+prefix+"\")/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	return pcur.getCurrentInitialContext().composeName(name, prefix);
    }

    public Object addToEnvironment(String propName, Object propVal) 
	throws NamingException {
	try {
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.addToEnvironment(\""+propName+"\","+simpleClass(propVal.getClass().getName())+" object)");
        }
	    return pcur.getCurrentInitialContext().addToEnvironment(propName, propVal);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.addToEnvironment(String propName, Object propVal)  failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }
    
    public Object removeFromEnvironment(String propName) 
	throws NamingException {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.removeFromEnvironment(\""+propName+"\")");
        }
	try {
	    return pcur.getCurrentInitialContext().removeFromEnvironment(propName);
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.removeFromEnvironment(String propName) failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }
    
    public Hashtable getEnvironment() throws NamingException {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.getEnvironment()/rmi name=\""+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().getEnvironment();
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.getEnvironment() failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);	   
		}
	    throw e;
	}
    }
    
    public void close() throws NamingException {	
	if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.close()");
        }
	try {
	    for (Enumeration e = activesInitialsContexts.keys() ; e.hasMoreElements() ;) {
		rmiName =  (String)e.nextElement();
		pcur.setRMI(rmiName);	    
		((Context)activesInitialsContexts.get(rmiName)).close();	
		pcur.setDefault();
	    } 
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.close() failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }
    
    public String getNameInNamespace() throws NamingException {	
		if (TraceCarol.isDebugJndiCarol()) {
            TraceCarol.debugJndiCarol("MultiOrbInitialContext.getNameInNamespace()/rmi name="+pcur.getCurrentRMIName()+"\"");
        }
	try {
	    return pcur.getCurrentInitialContext().getNameInNamespace();
	} catch (NamingException e) {
		if (TraceCarol.isDebugJndiCarol()) {
	    String msg = "MultiOrbInitialContext.getNameInNamespace() failed: " + e; 
	    TraceCarol.debugJndiCarol("Error: " + msg + " " + e);
		}	    
	    throw e;
	}
    }

    /**
     * Just the name of the class without the package
     */
    private String simpleClass(String c) {
	return c.substring(c.lastIndexOf('.') +1);
    }
    
	

	private String  encode( String name )
	{
		// Hide special chrs from flat namespace registry.
		// Escape forward and backward slashes, and leading quote character.

		if( name.length()<1 ) { return name; }
		StringBuffer	newname = new StringBuffer(name);
		int				i = 0;
		while( i < newname.length() )
		{
			char c = newname.charAt(i);
			if( c=='/' || c=='\\' )
			{
				newname.insert(i,'\\');
				i++;
			}
			i++;
		}
		if( newname.charAt(0)=='"'||newname.charAt(0)=='\'' ) {
			newname.insert(0,'\\');
		}
		return newname.toString();
	}


	private String  decode( String name )	// undo what encode() does
	{

		StringBuffer	newname = new StringBuffer(name);
		if( newname.length()>=2
		&& (newname.charAt(0)=='"' || newname.charAt(0)=='\'')
		&&  newname.charAt(0)==newname.charAt(newname.length()-1)
		) {
			// we have a quoted string: remove the enclosing quotes
			newname.deleteCharAt(0);
			newname.deleteCharAt(newname.length()-1);
		}
		else {
			if( name.indexOf('\\')<0 ) { return name; }	// nothing to decode
			int				i = 0;
			while( i < newname.length() )
			{
				if( newname.charAt(i)=='\\' )
				{
					newname.deleteCharAt(i);
					i++;
					continue;
				}
				i++;
			}
		}
		return newname.toString();
	}


	private Name  encode( Name name )
	{
		try
		{
			return new CompositeName(encode(name.toString()));
		} catch (InvalidNameException e)
		{
			return name;
		}
	}


	private Name  decode( Name name )
	{		try
		{
			return new CompositeName(decode(name.toString()));
		} catch (InvalidNameException e)
		{
			return name;
		}
	}


	
	class  WrapEnum  implements NamingEnumeration	// Class to wrap enumerations
	{
		
		NamingEnumeration		enum;

		WrapEnum( NamingEnumeration names )
		{
			this.enum = names;
		}

		public boolean hasMoreElements()
		{
			return enum.hasMoreElements();
		}

		public boolean hasMore() throws NamingException
		{
			return enum.hasMore();
		}

		public Object nextElement()
		{
			javax.naming.NameClassPair		ncp;
			ncp = (javax.naming.NameClassPair)enum.nextElement();
			ncp.setName( decode(ncp.getName() ));
			return ncp;
		}

		public Object next() throws NamingException
		{
			javax.naming.NameClassPair		ncp;
			ncp = (javax.naming.NameClassPair)enum.next();
			ncp.setName( decode(ncp.getName() ));
			return ncp;
		} 
    
		public void close() throws NamingException
		{
			enum = null;
		}
	}
    
}


