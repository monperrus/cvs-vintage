/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.spi;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;

import org.apache.log4j.helpers.LogLog;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Hashtable;

// Contributors:   Nelson Minar <nelson@monkey.org>
//                 Wolf Siberski

/**
   The internal representation of logging events. When a affirmative
   logging decision is made a <code>LoggingEvent</code> instance is
   created. This instance is passed around the different log4j
   components.

   <p>This class is of concern to those wishing to extend log4j. 

   @author Ceki G&uuml;lc&uuml;
   @author <a href=mailto:jim_cakalic@na.biomerieux.com>Jim Cakalic</a>
   
   @since 0.8.2 */
public class LoggingEvent implements java.io.Serializable {

  private static long startTime = System.currentTimeMillis();

  ///** Category of logging event. Can not be shipped to remote hosts. */
  //transient public Category category;


  /** Fully qualified name of the calling category class. */
  transient public final String fqnOfCategoryClass;

  /** The category name. */
  public String categoryName;
  
  /** Priority of logging event. Priority cannot be serializable
      because it is a flyweight.  Due to its special seralization it
      cannot be declated final either. */
  transient public Priority priority;

  /** The nested diagnostic context (NDC) of logging event. */
  private String ndc;

  /** Have we tried to do an NDC lookup? If we did, there is no need
      to do it again.  Note that its value is always false when
      serialized. Thus, a receiving SocketNode will never use it's own
      (incorrect) NDC. See also writeObject method. */
  private boolean ndcLookupRequired = true;


  /** The application supplied message of logging event. */
  public final String message;
  /** The name of thread in which this logging event was generated. */
  private String threadName;

  /** The throwable associated with this logging event.

      This is field is transient because not all exception are
      serializable. More importantly, the stack information does not
      survive serialization.
  */
  transient public final Throwable throwable;

  /** This variable collects the info on a throwable. This variable
      will be shipped to 
      
   */
  public String throwableInformation;

  /** The number of milliseconds elapsed from 1/1/1970 until logging event
      was created. */
  public final long timeStamp;
  /** Location information for the caller. */
  private LocationInfo locationInfo;

  // Damn serialization
  static final long serialVersionUID = -868428216207166145L;

  static final Integer[] PARAM_ARRAY = new Integer[1];
  static final String TO_PRIORITY = "toPriority";
  static final Class[] TO_PRIORITY_PARAMS = new Class[] {int.class};
  static final Hashtable methodCache = new Hashtable(3); // use a tiny table

  /**
     Instantiate a LoggingEvent from the supplied parameters.
     
     <p>Except {@link #timeStamp} all the other fields of
     <code>LoggingEvent</code> are filled when actually needed.
     <p>
     @param category The category of this event.
     @param priority The priority of this event.
     @param message  The message of this event.
     @param throwable The throwable of this event.  */
  public LoggingEvent(String fqnOfCategoryClass, Category category, 
		      Priority priority, String message, Throwable throwable) {
    this.fqnOfCategoryClass = fqnOfCategoryClass;
    this.categoryName = category.getName();
    this.priority = priority;
    this.message = message;
    this.throwable = throwable;
    timeStamp = System.currentTimeMillis();
  }  

  /**
     Returns the time when the application started, in milliseconds
     elapsed since 01.01.1970.  */
  public
  static 
  long getStartTime() {
    return startTime;
  }

  public
  String getNDC() {
    if(ndcLookupRequired) {
      ndcLookupRequired = false;
      ndc = NDC.get();
    }
    return ndc; 
  }

  

  public
  String getThreadName() {
    if(threadName == null)
      threadName = (Thread.currentThread()).getName();
    return threadName;
  }

  public 
  String getThrowableInformation() {
    if(throwable == null) {
       return null;
    }
 
    if(throwableInformation == null ) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      throwable.printStackTrace(pw);
      throwableInformation = sw.toString();
    }

    return throwableInformation;
  }
	
  private
  void writeObject(ObjectOutputStream oos) throws java.io.IOException {
    // Aside from returning the current thread name the wgetThreadName
    // method sets the threadName variable.
    this.getThreadName();    

    // This call has a side effect of setting this.ndc and
    // setting ndcLookupRequired to false if not already false.
    this.getNDC();

    // This sets the throwableInformation variable to the stack trace
    // of the throwable variable.
    this.getThrowableInformation();

    oos.defaultWriteObject();
    
    // serialize this event's priority
    writePriority(oos);
    

  }

  private 
  void writePriority(ObjectOutputStream oos) throws java.io.IOException {

    oos.writeInt(priority.toInt());

    Class clazz = priority.getClass();
    if(clazz == Priority.class) {
      oos.writeObject(null);
    } else {
      // writing the Class would be nicer, except that serialized
      // classed can not be read back by JDK 1.1.x. We have to resort
      // to this hack instead.
      oos.writeObject(clazz.getName());
    }
  }

  private 
  void readPriority(ObjectInputStream ois) 
                      throws java.io.IOException, ClassNotFoundException {

    int p = ois.readInt();
    try {
      String className = (String) ois.readObject();      
      if(className == null) {
	priority = Priority.toPriority(p);
      } else {
	Method m = (Method) methodCache.get(className);	
	if(m == null) {
	  Class clazz = Class.forName(className);
	  m = clazz.getDeclaredMethod(TO_PRIORITY, TO_PRIORITY_PARAMS);
	  methodCache.put(className, m);
	}      
	PARAM_ARRAY[0] = new Integer(p);
	priority = (Priority) m.invoke(null,  PARAM_ARRAY);
      }
    } catch(Exception e) {
	LogLog.warn("Priority deserialization failed, reverting default.", e);
	priority = Priority.toPriority(p);
    }
  }



  private void readObject(ObjectInputStream ois)
                        throws java.io.IOException, ClassNotFoundException {
    ois.defaultReadObject();    
    readPriority(ois);
    
    // Make sure that no location info is available to Layouts
    if(locationInfo == null)
      locationInfo = new LocationInfo(null, null);
  }


  /**
     Set the location information for this logging event. The collected
     information is cached for future use.
   */
  public
  LocationInfo getLocationInformation() {
    if(locationInfo == null) {
      locationInfo = new LocationInfo(new Throwable(), fqnOfCategoryClass);
    }
    return locationInfo;
  }
}
