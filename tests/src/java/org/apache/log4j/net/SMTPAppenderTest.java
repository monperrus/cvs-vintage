/*
 * Copyright 1999,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.log4j.net;

import org.apache.log4j.AbstractAppenderTest;
import org.apache.log4j.AppenderSkeleton;


/**
 * Test if SMTPAppender honors the Appender contract.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 *
 */
public class SMTPAppenderTest extends AbstractAppenderTest {
  protected AppenderSkeleton getAppender() {
    return new SMTPAppender();
  }

  public AppenderSkeleton getConfiguredAppender() {
    SMTPAppender ca = new SMTPAppender();

    // set a bogus layout
    ca.setLayout(new DummyLayout());
    ca.setFrom("noreply@nowhere.x");
    ca.setTo("noreply@nowhere.x");
    ca.setSMTPHost("localhost");
    return ca;
  }

  public void testPartiallyConfiguredAppender() {
    
  }
}
