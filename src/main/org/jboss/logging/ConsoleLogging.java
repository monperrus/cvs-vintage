/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.logging;

import java.io.*;
import java.text.*;
import java.util.StringTokenizer;
import java.util.SortedSet;
import java.util.Comparator;
import java.util.TreeSet;
import javax.management.*;

/**
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.1 $
 */
public class ConsoleLogging
   implements ConsoleLoggingMBean, MBeanRegistration, NotificationListener
{
   // Constants -----------------------------------------------------
   public static final String OBJECT_NAME = "DefaultDomain:service=Logging,type=Console";
    
   // Attributes ----------------------------------------------------
   PrintStream out, err;
   DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd hh.mm");
   String format = "<{0}><{2}> {4}";
   MessageFormat msgFmt = new MessageFormat(format);
   
   boolean verbose = false;
   
   Log log = new Log("Console logging");
   
   String filter = "Information,Debug,Warning,Error";
   
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
      Object[] args = new Object[] { dateFmt.format(n.getTimeStamp()), new Long(n.getSequenceNumber()), n.getUserData(), n.getType(), n.getMessage() };
      if (n.getType().equals("Error") || n.getType().equals("Warning"))
         err.println(msgFmt.format(args));
      else         
         out.println(msgFmt.format(args));
   }
   
   // MBeanRegistration implementation ------------------------------
   public ObjectName preRegister(MBeanServer server, ObjectName name)
      throws java.lang.Exception
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
      
      log.log("Logging started");
      
      return new ObjectName(OBJECT_NAME);
   }
   
   public void postRegister(java.lang.Boolean registrationDone) 
   {
   }
   
   public void preDeregister()
      throws java.lang.Exception 
   {}
   
   public void postDeregister() {}
}
