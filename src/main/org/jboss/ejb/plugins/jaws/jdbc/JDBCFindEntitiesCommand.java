/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.ejb.plugins.jaws.jdbc;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.plugins.jaws.JPMFindEntitiesCommand;
import org.jboss.ejb.plugins.jaws.metadata.FinderMetaData;

/**
 * Keeps a map from finder name to specific finder command, and
 * delegates to the relevant specific finder command.
 *
 * @see org.jboss.ejb.plugins.jaws.JPMFindEntitiesCommand
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
 * @author <a href="mailto:shevlandj@kpi.com.au">Joe Shevland</a>
 * @author <a href="mailto:justin@j-m-f.demon.co.uk">Justin Forder</a>
 * @version $Revision: 1.4 $
 */
public class JDBCFindEntitiesCommand implements JPMFindEntitiesCommand
{
   // Attributes ----------------------------------------------------
   
   private JDBCCommandFactory factory;
   private HashMap knownFinderCommands = new HashMap();
   
   // Constructors --------------------------------------------------
   
   public JDBCFindEntitiesCommand(JDBCCommandFactory factory)
   {
      this.factory = factory;
      
      // Make commands for the defined finders
      
      Iterator definedFinders = factory.getMetaData().getFinders();
      while(definedFinders.hasNext())
      {
         FinderMetaData f = (FinderMetaData)definedFinders.next();
         
         if ( !knownFinderCommands.containsKey(f.getName()) )
         {
            JPMFindEntitiesCommand finderCommand =
               factory.createDefinedFinderCommand(f);
               
            knownFinderCommands.put(f.getName(), finderCommand);
         }
      }
      
      // Make commands for any autogenerated finders required
      
      Method[] homeMethods = factory.getContainer().getHomeClass().getMethods();
      
      for (int i = 0; i < homeMethods.length; i++)
      {
         Method m = homeMethods[i];
         String name = m.getName();
         
         if (!knownFinderCommands.containsKey(name))
         {
            if (name.equals("findAll"))
            {
               knownFinderCommands.put(name, factory.createFindAllCommand());
            } else if (name.startsWith("findBy")  && !name.equals("findByPrimaryKey"))
            {
               try
               {
                  knownFinderCommands.put(name, factory.createFindByCommand(m));
               } catch (IllegalArgumentException e)
               {
                  factory.getLog().debug("Could not create the finder " + name +
                                         ", because no matching CMP field was found.");
               }
            }
         }
      }
   }
   
   // JPMFindEntitiesCommand implementation -------------------------
   
   public Collection execute(Method finderMethod,
                             Object[] args,
                             EntityEnterpriseContext ctx)
      throws RemoteException, FinderException
   {
      String finderName = finderMethod.getName();
      
      JPMFindEntitiesCommand finderCommand = null;
      
      finderCommand = 
         (JPMFindEntitiesCommand)knownFinderCommands.get(finderName);
      
      // If we found a finder command, delegate to it,
      // otherwise return an empty collection.
      
      // JF: Shouldn't tolerate the "not found" case!
      
      return (finderCommand != null) ?
         finderCommand.execute(finderMethod, args, ctx) : new ArrayList();
   }
}
