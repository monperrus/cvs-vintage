/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.logging;

import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.SortedSet;
import java.util.Comparator;
import java.util.TreeSet;
import javax.management.*;

import org.jboss.util.ServiceMBeanSupport;

/**
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.5 $
 */
public class ConsoleLogging
   extends ServiceMBeanSupport
   implements ConsoleLoggingMBean, NotificationListener
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   PrintStream out, err;
   String format = "<{0,date,yyyy-MM-dd} {0,time,hh.mm}><{2}> {4}";
   MessageFormat msgFmt = new MessageFormat(format);
   
   boolean verbose = false;
   
   Log log = new Log("Console logging");
   
   String filter = "Information,Debug,Warning,Error";
   
   ObjectName name;
   MBeanServer server;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   public ConsoleLogging()
   {
   }
   
   public ConsoleLogging(String filter, String format)
   {
      this.filter = filter;
      setFormat(format);
   }
   
   // Public --------------------------------------------------------
   public void setFormat(String format) 
   { 
      this.format = format; 
      msgFmt = new MessageFormat(format);
   }
   public String getFormat() { return format; }
   
   // NotificationListener implementation ---------------------------
   public synchronized void handleNotification(Notification n,
                                  java.lang.Object handback)
   {
            Object[] args = new Object[] { new Date(n.getTimeStamp()), new Long(n.getSequenceNumber()), n.getUserData(), n.getType(), n.getMessage() };
      if (n.getType().equals("Error") || n.getType().equals("Warning"))
         err.println(msgFmt.format(args));
      else         
         out.println(msgFmt.format(args));
   }
   
   // Service implementation ------------------------------
   public ObjectName getObjectName(MBeanServer server, ObjectName name)
      throws javax.management.MalformedObjectNameException
   {
      this.server = server;
      return name == null ? new ObjectName(OBJECT_NAME) : name;
   }
   
   public String getName()
   {
      return "Console logging";
   }
   
   public void initService()
      throws Exception
   {
      out = System.out;
      err = System.err;
      
      NotificationFilterSupport f = new NotificationFilterSupport();
      StringTokenizer types = new StringTokenizer(filter, ",");
      while (types.hasMoreTokens())
         f.enableType(types.nextToken());
      
      server.addNotificationListener(new ObjectName(server.getDefaultDomain(),"service","Log"),this,f,null);
      
      LogStream outLog = new LogStream("Information");
      LogStream errLog = new LogStream("Error");
      System.setOut(outLog);
      System.setErr(errLog);
   }
}

