/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.or;

import java.util.Hashtable;

/**
   Map class objects to an {@link ObjectRenderer}.

   @author Ceki G&uuml;lc&uuml;
   @since version 1.0 */
public class RendererMap {

  Hashtable map;

  static ObjectRenderer defaultRenderer = new DefaultRenderer();

  public
  RendererMap() {
    map = new Hashtable();
  }

  /**
     Find the appropriate renderer for the class type of the
     <code>o</code> parameter. This is accomplished by calling the
     {@link #get(Class)} method. Once a renderer is found, it is
     applied on the object <code>o</code> and the result is returned
     as a {@link String}. */
  public
  String findAndRender(Object o) {
    if(o == null)
      return null;
    else 
      return get(o.getClass()).doRender(o);
  }


  /**
     Syntactic sugar method that calls {@link #get(Class)} with the
     class of the object parameter. */
  public 
  ObjectRenderer get(Object o) {
    if(o == null) 
      return null;
    else
      return get(o.getClass());
  }
  

  /**
     Search the parents of <code>clazz</code> for a renderer. The
     renderer closest in the hierarchy will be returned. If no
     renderers could be found, then the default renderer is returned.          

     <p>The search first looks for a renderer configured for
     <code>clazz</code>. If a renderer could not be found, then the
     search continues by looking at all the interfaces implemented by
     <code>clazz</code> including the super-interfaces of each
     interface.  If a renderer cannot be found, then the search looks
     for a renderer defined for the parent (superclass) of
     <code>clazz</code>. If that fails, then all the interfaces
     implemented by the parent of <code>clazz</code> are searched and
     so on.

     <p>For example, if A0, A1, A2 are classes and X0, X1, X2, Y0, Y1
     are interfaces where A2 extends A1 which in turn extends A0 and
     similarly X2 extends X1 which extends X0 and Y1 extends Y0. Let
     us also assume that A1 implements the Y0 interface and that A2
     implements the X2 interface.

     <p>The table below shows the results returned by the
     <code>get(A2.class)</code> method depending on the renderers
     added to the map.

     <p><table border="1">
     <tr><th>Added renderers</th><th>Value returned by <code>get(A2.class)</code></th>

     <tr><td><code>A0Renderer</code>
         <td align="center"><code>A0Renderer</code>  

     <tr><td><code>A0Renderer, A1Renderer</code>
         <td align="center"><code>A1Renderer</code>  

     <tr><td><code>X0Renderer</code>
         <td align="center"><code>X0Renderer</code>  

     <tr><td><code>A1Renderer, X0Renderer</code>
         <td align="center"><code>X0Renderer</code>  

     </table>
     
     <p>This search algorithm is not the most natural, although it is
     particularly easy to implement. Future log4j versions
     <em>may</em> implement a more intuitive search
     algorithm. However, the present algorithm should be acceptable in
     the vast majority of circumstances.

 */
  public
  ObjectRenderer get(Class clazz) {
    //System.out.println("\nget: "+clazz);    
    ObjectRenderer r = null;
    for(Class c = clazz; c != null; c = c.getSuperclass()) {
      //System.out.println("Searching for class: "+c);
      r = (ObjectRenderer) map.get(c);
      if(r != null) {
	return r;
      }      
      r = searchInterfaces(c);
      if(r != null)
	return r;
    }
    return defaultRenderer;
  }  
  
  ObjectRenderer searchInterfaces(Class c) {
    //System.out.println("Searching interfaces of class: "+c);
    
    ObjectRenderer r = (ObjectRenderer) map.get(c);
    if(r != null) {
      return r;
    } else {
      Class[] ia = c.getInterfaces();
      for(int i = 0; i < ia.length; i++) {
	r = searchInterfaces(ia[i]);
	if(r != null)
	  return r; 
      }
    }
    return null;
  }


  public
  ObjectRenderer getDefaultRenderer() {
    return defaultRenderer;
  }


  public
  void clear() {
    map.clear();
  }

  /**
     Register an {@link ObjectRenderer} for <code>clazz</code>.     
  */
  public
  void put(Class clazz, ObjectRenderer or) {
    map.put(clazz, or);
  }
}
