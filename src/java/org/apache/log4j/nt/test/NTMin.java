/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.nt.test;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.nt.NTEventLogAppender;
import org.apache.log4j.Level;
import org.apache.log4j.NDC;


public class NTMin {

  static Logger logger = Logger.getLogger(NTMin.class.getName());

  public
  static
  void main(String argv[]) {

    //if(argv.length == 1) {
    init();
    //}
    //else {
    //Usage("Wrong number of arguments.");
    //}
      test("someHost");
  }


  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + NTMin.class + "");
    System.exit(1);
  }


  static
  void init() {

    BasicConfigurator.configure(new NTEventLogAppender());
  }

  static
  void test(String host) {
    NDC.push(host);
    int i  = 0;
    logger.debug( "Message " + i++);
    logger.info( "Message " + i++);
    logger.warn( "Message " + i++);
    logger.error( "Message " + i++);
    logger.log(Level.FATAL, "Message " + i++);
    logger.debug("Message " + i++,  new Exception("Just testing."));
  }
}
