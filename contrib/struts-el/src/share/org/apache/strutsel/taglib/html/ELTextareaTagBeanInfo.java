/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/html/ELTextareaTagBeanInfo.java,v 1.4 2004/03/14 07:15:01 sraeburn Exp $
 * $Revision: 1.4 $
 * $Date: 2004/03/14 07:15:01 $
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

package org.apache.strutsel.taglib.html;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.beans.SimpleBeanInfo;

/**
 * This is the <code>BeanInfo</code> descriptor for the
 * <code>org.apache.strutsel.taglib.html.ELTextareaTag</code> class.  It is
 * needed to override the default mapping of custom tag attribute names to
 * class attribute names.
 *<p>
 * This is because the value of the unevaluated EL expression has to be kept
 * separately from the evaluated value, which is stored in the base class. This
 * is related to the fact that the JSP compiler can choose to reuse different
 * tag instances if they received the same original attribute values, and the
 * JSP compiler can choose to not re-call the setter methods, because it can
 * assume the same values are already set.
 */
public class ELTextareaTagBeanInfo extends SimpleBeanInfo
{
    public  PropertyDescriptor[] getPropertyDescriptors()
    {
        ArrayList proplist = new ArrayList();

        try {
            proplist.add(new PropertyDescriptor("accesskey", ELTextareaTag.class,
                                                null, "setAccesskeyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("alt", ELTextareaTag.class,
                                                null, "setAltExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("altKey", ELTextareaTag.class,
                                                null, "setAltKeyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("cols", ELTextareaTag.class,
                                                null, "setColsExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("disabled", ELTextareaTag.class,
                                                null, "setDisabledExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("indexed", ELTextareaTag.class,
                                                null, "setIndexedExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("name", ELTextareaTag.class,
                                                null, "setNameExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onblur", ELTextareaTag.class,
                                                null, "setOnblurExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onchange", ELTextareaTag.class,
                                                null, "setOnchangeExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onclick", ELTextareaTag.class,
                                                null, "setOnclickExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("ondblclick", ELTextareaTag.class,
                                                null, "setOndblclickExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onfocus", ELTextareaTag.class,
                                                null, "setOnfocusExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onkeydown", ELTextareaTag.class,
                                                null, "setOnkeydownExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onkeypress", ELTextareaTag.class,
                                                null, "setOnkeypressExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onkeyup", ELTextareaTag.class,
                                                null, "setOnkeyupExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onmousedown", ELTextareaTag.class,
                                                null, "setOnmousedownExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onmousemove", ELTextareaTag.class,
                                                null, "setOnmousemoveExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onmouseout", ELTextareaTag.class,
                                                null, "setOnmouseoutExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onmouseover", ELTextareaTag.class,
                                                null, "setOnmouseoverExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("onmouseup", ELTextareaTag.class,
                                                null, "setOnmouseupExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("property", ELTextareaTag.class,
                                                null, "setPropertyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("readonly", ELTextareaTag.class,
                                                null, "setReadonlyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("rows", ELTextareaTag.class,
                                                null, "setRowsExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("style", ELTextareaTag.class,
                                                null, "setStyleExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("styleClass", ELTextareaTag.class,
                                                null, "setStyleClassExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("styleId", ELTextareaTag.class,
                                                null, "setStyleIdExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("tabindex", ELTextareaTag.class,
                                                null, "setTabindexExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("title", ELTextareaTag.class,
                                                null, "setTitleExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("titleKey", ELTextareaTag.class,
                                                null, "setTitleKeyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("value", ELTextareaTag.class,
                                                null, "setValueExpr"));
        } catch (IntrospectionException ex) {}
        
        PropertyDescriptor[] result =
            new PropertyDescriptor[proplist.size()];
        return ((PropertyDescriptor[]) proplist.toArray(result));
    }
}
