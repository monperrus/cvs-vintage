/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/bean/ELSizeTagBeanInfo.java,v 1.6 2004/03/14 07:15:04 sraeburn Exp $
 * $Revision: 1.6 $
 * $Date: 2004/03/14 07:15:04 $
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

package org.apache.strutsel.taglib.bean;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.lang.reflect.Method;

/**
 * This is the <code>BeanInfo</code> descriptor for the
 * <code>org.apache.strutsel.taglib.bean.ELSizeTag</code> class.  It is needed
 * to override the default mapping of custom tag attribute names to class
 * attribute names.
 *<p>
 * This is because the value of the unevaluated EL expression has to be kept
 * separately from the evaluated value, which is stored in the base class. This
 * is related to the fact that the JSP compiler can choose to reuse different
 * tag instances if they received the same original attribute values, and the
 * JSP compiler can choose to not re-call the setter methods, because it can
 * assume the same values are already set.
 */
public class ELSizeTagBeanInfo extends SimpleBeanInfo
{
    public  PropertyDescriptor[] getPropertyDescriptors()
    {
        ArrayList proplist = new ArrayList();

        try {
            proplist.add(new PropertyDescriptor("collection", ELSizeTag.class,
                                                null, "setCollectionExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("id", ELSizeTag.class,
                                                null, "setIdExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("name", ELSizeTag.class,
                                                null, "setNameExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("property", ELSizeTag.class,
                                                null, "setPropertyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("scope", ELSizeTag.class,
                                                null, "setScopeExpr"));
        } catch (IntrospectionException ex) {}
        
        PropertyDescriptor[] result =
            new PropertyDescriptor[proplist.size()];
        return ((PropertyDescriptor[]) proplist.toArray(result));
    }
}
