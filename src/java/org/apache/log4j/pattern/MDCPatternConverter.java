/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

import java.util.Iterator;
import java.util.Set;


/**
 * Return the events thread (usually the current thread) in a StringBuffer.
 * This buffer is recycled!
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class MDCPatternConverter extends PatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  StringBuffer buf;

  public MDCPatternConverter() {
    super();
    this.buf = new StringBuffer(32);
  }

  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);

    /**
      * if there is no additional options, we output every single
      * Key/Value pair for the MDC in a similar format to Hashtable.toString()
      */
    if (option == null) {
      buf.append("{");

      Set keySet = event.getMDCKeySet();

      for (Iterator i = keySet.iterator(); i.hasNext();) {
        Object item = i.next();
        Object val = event.getMDC(item.toString());
        buf.append("{").append(item).append(",").append(val).append("}");
      }

      buf.append("}");

      return buf;
    }

    /**
     * otherwise they just want a single key output
     */
    Object val = event.getMDC(option);

    if (val != null) {
      return buf.append(val);
    }

    return buf;
  }
}
