/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/taglib/nested/html/NestedOptionsTag.java,v 1.9 2004/03/14 06:23:50 sraeburn Exp $
 * $Revision: 1.9 $
 * $Date: 2004/03/14 06:23:50 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.struts.taglib.nested.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.OptionsTag;
import org.apache.struts.taglib.nested.NestedNameSupport;
import org.apache.struts.taglib.nested.NestedPropertyHelper;

/**
 * NestedOptionsTag.
 * @since Struts 1.1
 * @version $Revision: 1.9 $ $Date: 2004/03/14 06:23:50 $
 */
public class NestedOptionsTag extends OptionsTag implements NestedNameSupport {

  /**
   * Overriding method of the heart of the matter. Gets the relative property
   * and leaves the rest up to the original tag implementation. Sweet.
   * @return int JSP continuation directive.
   *             This is in the hands of the super class.
   */
  public int doStartTag() throws JspException {
    // get the original properties
    originalName = getName();
    originalProperty = getProperty();
    originalLabelProperty = getLabelProperty();

    // request
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

    // if we have a label property
    if (originalLabelProperty != null) {
      // do the label property first
      if (getName() == null|| Constants.BEAN_KEY.equals(getName())) {
        super.setLabelProperty(NestedPropertyHelper.getAdjustedProperty(request,
                                                         originalLabelProperty));
      } else {
          super.setLabelProperty(originalLabelProperty);
      }
    }

    // set the other properties
    NestedPropertyHelper.setNestedProperties(request, this);



    // let the super do it's thing
    return super.doStartTag();
  }

  /**
   * Complete the processing of the tag. The nested tags here will restore
   * all the original value for the tag itself and the nesting context.
   * @return int to describe the next step for the JSP processor
   * @throws JspException for the bad things JSP's do
   */
  public int doEndTag() throws JspException {
    // do the super's ending part
    int i = super.doEndTag();

    // reset the properties
    setName(originalName);
    setProperty(originalProperty);
    setLabelProperty(originalLabelProperty);

    // continue
    return i;
  }

  /**
   * Release the tag's resources and reset the values.
   */
  public void release() {
    super.release();
    // reset the originals
    originalName = null;
    originalProperty = null;
    originalLabelProperty = null;
  }

  /* the usual private member variables */
  private String originalName = null;
  private String originalProperty = null;
  private String originalLabelProperty = null;
}
