package org.jboss.verifier.strategy;

/*
 * Class org.jboss.verifier.strategy.VerificationContext;
 * Copyright (C) 2000  Juha Lindfors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This package and its source code is available at www.gjt.org
 * $Id: VerificationContext.java,v 1.1 2000/05/29 18:26:29 juha Exp $
 *
 * You can reach the author by sending email to jpl@gjt.org or
 * directly to jplindfo@helsinki.fi.
 */


// standard imports
import java.net.URL;


// non-standard class dependencies
import org.gjt.lindfors.pattern.StrategyContext;

import org.jboss.verifier.event.VerificationEventGenerator;
import org.jboss.verifier.event.VerificationEventFactory;

import org.jboss.ejb.deployment.jBossEjbJar;


/**
 * << DESCRIBE THE CLASS HERE >>
 *
 * For more detailed documentation, refer to the
 * <a href="" << INSERT DOC LINK HERE >> </a>
 *
 * @see     << OTHER RELATED CLASSES >>
 *
 * @author 	Juha Lindfors
 * @version $Revision: 1.1 $
 * @since  	JDK 1.3
 */
public interface VerificationContext extends StrategyContext,
                                             VerificationEventGenerator {
    
    /*
     * Version identifier.
     */
    public final static String VERSION_1_1 =
        "Enterprise JavaBeans v1.1, Final Release";
    
    /*
     * Version identifier.
     */    
    public final static String VERSION_2_0 =
        "No public release yet.";

        
     
    /*
     * Returns the loaded and parsed ejb jar file
     */
    abstract jBossEjbJar getEJBJar();  
    
    /*
     * Returns the location of the ejb jar file
     */
    abstract URL  getJarLocation();

    /*
     * Returns EJB spec version string
     */
    abstract String getEJBVersion();

}

