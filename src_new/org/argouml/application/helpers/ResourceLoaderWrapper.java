// Copyright (c) 1996-2002 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// $header$
package org.argouml.application.helpers;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.tigris.gef.util.ResourceLoader;

/**
 * Wrapper around org.tigris.gef.util.ResourceLoader. Necessary since ArgoUML needs
 * some extra init
 * @since Nov 24, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public final class ResourceLoaderWrapper {

    /**
     * Singleton implementation
     */
    private static ResourceLoaderWrapper _instance;
    
    /**
     * Returns the singleton instance
     * @return ResourceLoaderWrapper
     */
    public static ResourceLoaderWrapper getResourceLoaderWrapper() {
        if (_instance == null) {
            _instance = new ResourceLoaderWrapper();
        }
        return _instance;
    }
    
    /**
     * Constructor for ResourceLoaderWrapper.
     */
    public ResourceLoaderWrapper() {
        super();
        initResourceLoader();
    }
    
    /**
     * Initializes the resourceloader. LookupIconResource checks if there are locations 
     * and extensions known. If there are none, this method is called to initialize
     * the resource loader. Originally, this method was placed within Main but
     * this coupled Main and the resourceLoader to much.
     */
    private void initResourceLoader() {
        String lookAndFeelClassName;
        if ("true".equals(System.getProperty("force.nativelaf","false"))) {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        }
        else {
            lookAndFeelClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
        }
        String lookAndFeelGeneralImagePath = "/org/argouml/Images/plaf/" + lookAndFeelClassName.replace('.', '/') + "/toolbarButtonGraphics/general";
        String lookAndFeelNavigationImagePath = "/org/argouml/Images/plaf/" + lookAndFeelClassName.replace('.', '/') + "/toolbarButtonGraphics/navigation";
        String lookAndFeelDiagramImagePath = "/org/argouml/Images/plaf/" + lookAndFeelClassName.replace('.', '/') + "/toolbarButtonGraphics/argouml/diagrams";
        String lookAndFeelElementImagePath = "/org/argouml/Images/plaf/" + lookAndFeelClassName.replace('.', '/') + "/toolbarButtonGraphics/argouml/elements";
        String lookAndFeelArgoUmlImagePath = "/org/argouml/Images/plaf/" + lookAndFeelClassName.replace('.', '/') + "/toolbarButtonGraphics/argouml";
        ResourceLoader.addResourceExtension("gif");
        ResourceLoader.addResourceLocation(lookAndFeelGeneralImagePath);
        ResourceLoader.addResourceLocation(lookAndFeelNavigationImagePath);
        ResourceLoader.addResourceLocation(lookAndFeelDiagramImagePath);
        ResourceLoader.addResourceLocation(lookAndFeelElementImagePath);
        ResourceLoader.addResourceLocation(lookAndFeelArgoUmlImagePath);
        ResourceLoader.addResourceLocation("/org/argouml/Images");
        ResourceLoader.addResourceLocation("/org/tigris/gef/Images");
    }
    
    /**
     * Wrapped method
     * @param extension
     */
    public void addResourceExtension(String extension) {
        ResourceLoader.addResourceExtension(extension);
    }
    
    /**
     * Wrapped method
     * @param location
     */
    public void addResourceLocation(String location) {
        ResourceLoader.addResourceLocation(location);
    }
    
    /**
     * Wrapped method
     * @param extension
     * @return boolean
     */
    public boolean containsExtension(String extension) {
        return ResourceLoader.containsExtension(extension);
    }
    
    /**
     * Wrapped method
     * @param location
     * @return boolean
     */
    public boolean containsLocation(String location) {
        return ResourceLoader.containsLocation(location);
    }
    
    /**
     * Wrapped method
     * @param resource
     * @return boolean
     */
    public boolean isInCache(String resource) {
        return ResourceLoader.isInCache(resource);
    }
    
    /**
     * Wrapped method
     * @param resource
     * @return ImageIcon
     */
    public ImageIcon lookupIconResource(String resource) {
        return ResourceLoader.lookupIconResource(resource);
    }
    
    /**
     * Wrapped method
     * @param resource
     * @param loader
     * @return ImageIcon
     */
    public ImageIcon lookupIconResource(String resource, ClassLoader loader) {
        return ResourceLoader.lookupIconResource(resource, loader);
    }
    
    /**
     * Wrapped method
     * @param resource
     * @param desc
     * @return ImageIcon
     */
    public ImageIcon lookupIconResource(String resource, String desc) {
        return ResourceLoader.lookupIconResource(resource, desc);
    }
    
    /**
     * Wrapped method
     * @param resource
     * @param desc
     * @param loader
     * @return ImageIcon
     */
    public ImageIcon lookupIconResource(String resource, String desc, ClassLoader loader) {      
        return ResourceLoader.lookupIconResource(resource, desc, loader);
    }
    
    /** 
     * Wrapped method
     * @param extension
     */
    public void removeResourceExtension(String extension) {
        ResourceLoader.removeResourceExtension(extension);
    }
    
    /**
     * Wrapped method
     * @param location
     */
    public void removeResourceLocation(String location) {
        ResourceLoader.removeResourceExtension(location);
    }
}
