/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.log4j.helpers;

import java.io.File;
import org.apache.log4j.helpers.LogLog;

/**
   Check every now and then that a certain file has not changed. If it
   has, then call the {@link #doOnChange} method.


   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.1 */
public abstract class FileWatchdog extends Thread {

  /**
     The default delay between every file modification check, set to 60
     seconds.  */
  static final public long DEFAULT_DELAY = 60000; 
  /**
     The name of the file to observe  for changes.
   */
  protected String filename;
  
  /**
     The delay to observe between every check. By default set {@link
     #DEFAULT_DELAY}. */
  protected long delay = DEFAULT_DELAY; 
  
  File file;
  long lastModif = 0; 
  boolean warnedAlready = false;

  protected
  FileWatchdog(String filename) {
    this.filename = filename;
    file = new File(filename);
    setDaemon(true);
    checkAndConfigure();
  }

  /**
     Set the delay to observe between each check of the file changes.
   */
  public
  void setDelay(long delay) {
    this.delay = delay;
  }

  abstract 
  protected 
  void doOnChange();

  protected
  void checkAndConfigure() {
    if(file.exists()) {
      long l = file.lastModified();
      if(l > lastModif) {
	lastModif = l;
	doOnChange();
	warnedAlready = false;
      }
    } else {
      if(!warnedAlready) {
	LogLog.debug("["+filename+"] does not exist.");
	warnedAlready = true;
      }
    }
  }

  public
  void run() {    
    while(!interrupted()) {
      try {
	Thread.currentThread().sleep(delay);
      } catch(InterruptedException e) {
	// no interruption expected
      }
      checkAndConfigure();
    }
  }
}
