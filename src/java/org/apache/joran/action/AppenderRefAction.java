/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.AppenderAttachable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.HashMap;


public class AppenderRefAction extends Action {
  static final Logger logger = Logger.getLogger(AppenderRefAction.class);

  public void begin(ExecutionContext ec, Element appenderRef) {
		// Let us forget about previous errors (in this object)
		inError = false; 

    logger.debug("begin called");

    Object o = ec.peekObject();

    if (!(o instanceof AppenderAttachable)) {
      logger.warn(
        "Could not find an AppenderAttachable object at the top of execution stack.");
      inError = true;
      ec.addError(
        "For element <appender-ref>, could not find a  AppenderAttachable object at the top of execution stack.");

      return;
    }

    AppenderAttachable appenderAttachable = (AppenderAttachable) o;

    String appenderName = appenderRef.getAttribute(ActionConst.REF_ATTRIBUTE);

    if (appenderName == null) {
      // print a meaningful error message and return
      Node parentNode = appenderRef.getParentNode();
      String errMsg = "Missing appender ref attribute in <appender-ref> tag.";

      if (parentNode instanceof Element) {
        Element parentElement = (Element) parentNode;
        String parentTag = parentElement.getTagName();

        if ("logger".equals(parentTag)) {
          String loggerName = parentElement.getAttribute("name");
          errMsg =
            errMsg + " Within <" + parentTag + ">" + " named [" + loggerName
            + "].";
        }

        errMsg = errMsg + " Within <" + parentTag + ">";
      }

      parentNode.getAttributes();
      logger.warn(errMsg);
      inError = true;
      ec.addError(errMsg);

      return;
    }

    HashMap appenderBag =
      (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
    Appender appender = (Appender) appenderBag.get(appenderName);

    if (appender == null) {
      logger.warn("Could not find an appender named [" + appenderName + "]");
      inError = true;
      ec.addError("Could not find an appender named [" + appenderName + "]");

      return;
    }


    if(appenderAttachable instanceof Logger) {
    logger.debug(
      "Attaching appender named [" + appenderName + "] to logger named ["
      + ((Logger)appenderAttachable).getName() +"].");
    } else {
			logger.debug(
					 "Attaching appender named [" + appenderName + "] to "
					 + appenderAttachable);
    }
    appenderAttachable.addAppender(appender);
  }

  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
