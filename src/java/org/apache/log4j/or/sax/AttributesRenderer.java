/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.or;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;

import java.util.Enumeration;
import org.xml.sax.Attributes;

/**
   Render <code>javax.jms.Message</code> objects.

   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
public class AttributesRenderer implements ObjectRenderer {

  public
  AttributesRenderer() {
  }

   
  /**
     Render a {@link Message}.
  */
  public
  String  doRender(Object o) {
    if(o instanceof Attributes) {  
      StringBuffer sbuf = new StringBuffer();
      Attributes a = (Attributes) o;
      int len = a.getLength();
      boolean first = true;
      for(int i = 0; i < len; i++) {
	if(first) {
	  first = false;
	} else {
	  sbuf.append(", ");
	}
	sbuf.append(a.getURI(i));
	sbuf.append(':');
	sbuf.append(a.getType(i));
	sbuf.append('=');
	sbuf.append(a.getValue(i));
	sbuf.append(a.getValue(i));
      }
      return sbuf.toString();
    } else {
      return o.toString();
    }
  }
}

