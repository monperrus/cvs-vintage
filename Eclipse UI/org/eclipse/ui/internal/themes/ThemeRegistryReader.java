/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.themes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.RegistryReader;

/**
 * Registry reader for themes.
 *
 * @since 3.0
 */
public class ThemeRegistryReader extends RegistryReader {
	public static final String ATT_CATEGORYID = "categoryId"; //$NON-NLS-1$
    public static String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String ATT_DEFAULTS_TO = "defaultsTo"; //$NON-NLS-1$
	public static final String ATT_DIRECTION = "direction"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_INITIALVALUE = "initialValue"; //$NON-NLS-1$
	public static final String ATT_LABEL = "label"; //$NON-NLS-1$
	public static final String ATT_PERCENTAGE = "percentage"; //$NON-NLS-1$
	public static final String ATT_VALUE = "value"; //$NON-NLS-1$
	
	/**
	 * The translation bundle in which to look up internationalized text.
	 */
	private final static ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(ThemeRegistryReader.class.getName());
	public static final String TAG_CATEGORYDEFINITION = "themeElementCategory"; //$NON-NLS-1$
	public static final String TAG_COLORDEFINITION = "colorDefinition"; //$NON-NLS-1$
	public static final String TAG_COLOROVERRIDE = "colorOverride"; //$NON-NLS-1$    
	public static final String TAG_FONTDEFINITION = "fontDefinition"; //$NON-NLS-1$
	public static final String TAG_FONTOVERRIDE = "fontOverride"; //$NON-NLS-1$
	public static final String TAG_GRADIENTDEFINITION = "gradientDefinition"; //$NON-NLS-1$
	public static final String TAG_GRADIENTOVERRIDE = "gradientOverride"; //$NON-NLS-1$
	public static final String TAG_GRADIENTPART = "gradientPart"; //$NON-NLS-1$
	public static final String TAG_THEME="theme";//$NON-NLS-1$

    private Collection categoryDefinitions = new ArrayList();
	
	private Collection colorDefinitions = new ArrayList();
    
	private Collection fontDefinitions = new ArrayList();
    
	private Collection gradientDefinitions = new ArrayList();
	
	private ThemeDescriptor themeDescriptor = null;
	private ThemeRegistry themeRegistry;
	
	/**
	 * ThemeRegistryReader constructor comment.
	 */
	public ThemeRegistryReader() {
		super();
	}

    /**
     * Returns the category definitions.
     *
     * @return the category definitions
     */
    public Collection getCategoryDefinitions() {        
        return categoryDefinitions;
    }

    /**
     * Returns the color definitions.
     *
     * @return the color definitions
     */
    public Collection getColorDefinitions() {        
        return colorDefinitions;
    }

    /**     
	 * Returns the font definitions.
     *
     * @return the font definitions
     */
    public Collection getFontDefinitions() {        
        return fontDefinitions;
    }

    /**     
	 * Returns the gradient definitions.
     *
     * @return the gradient definitions
     */
    public Collection getGradientDefinitions() {        
        return gradientDefinitions;
    }
	
    /**
     * Read a category.
     * 
     * @param element the element to read
     * @return the new category
     */
	private ThemeElementCategory readCategory(IConfigurationElement element) {
		String name = element.getAttribute(ATT_LABEL);
		
		String id = element.getAttribute(ATT_ID);
	
		String description = null;

		IConfigurationElement[] descriptions =
			element.getChildren(TAG_DESCRIPTION);

		if (descriptions.length > 0)
			description = descriptions[0].getValue();

		return new ThemeElementCategory(
				name,
				id,
				description,
				element
					.getDeclaringExtension()
					.getDeclaringPluginDescriptor()
					.getUniqueIdentifier(),
				element);
	}
	
    /**
     * Read a color.
     * 
     * @param element the element to read
     * @return the new color
     */
    private ColorDefinition readColor(IConfigurationElement element) {
		String name = element.getAttribute(ATT_LABEL);
		
		String id = element.getAttribute(ATT_ID);
		
		String defaultMapping = element.getAttribute(ATT_DEFAULTS_TO);

		String value = element.getAttribute(ATT_VALUE);

		if ((value == null && defaultMapping == null)
			|| (value != null && defaultMapping != null)) {
			logError(element, RESOURCE_BUNDLE.getString("Colors.badDefault")); //$NON-NLS-1$
			return null;
		}

		String categoryId = element.getAttribute(ATT_CATEGORYID);
		
		String description = null;

		IConfigurationElement[] descriptions =
			element.getChildren(TAG_DESCRIPTION);

		if (descriptions.length > 0)
			description = descriptions[0].getValue();

		
		return new ColorDefinition(
				name,
				id,
				defaultMapping,
				value,
				categoryId,
				description,
				element
					.getDeclaringExtension()
					.getDeclaringPluginDescriptor()
					.getUniqueIdentifier());
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.registry.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
	    String elementName = element.getName();
        if (themeDescriptor == null && elementName.equals(TAG_COLORDEFINITION)) {
            ColorDefinition definition = readColor(element);
            if (definition != null) {
                colorDefinitions.add(definition);
                themeRegistry.add(definition);
            }
			return true;
	    }
        else if (themeDescriptor != null && elementName.equals(TAG_COLOROVERRIDE)) {
            ColorDefinition definition = readColor(element);
            if (definition != null) {
                themeDescriptor.add(definition);
            }
			return true;
	    }
        else if (themeDescriptor == null && elementName.equals(TAG_GRADIENTDEFINITION)) {
            GradientDefinition definition = readGradient(element);
            if (definition != null) {
                gradientDefinitions.add(definition);
                themeRegistry.add(definition);
            }
			return true;
	    }
        else if (themeDescriptor != null && elementName.equals(TAG_GRADIENTOVERRIDE)) {
            GradientDefinition definition = readGradient(element);
            if (definition != null) {
                themeDescriptor.add(definition);
            }
			return true;
	    }
        else if (themeDescriptor == null && elementName.equals(TAG_FONTDEFINITION)) {
            FontDefinition definition = readFont(element);
            if (definition != null) {
                fontDefinitions.add(definition);
                themeRegistry.add(definition);
            }
			return true;
	    }
        else if (themeDescriptor != null && elementName.equals(TAG_FONTOVERRIDE)) {
            FontDefinition definition = readFont(element);
            if (definition != null) {
                themeDescriptor.add(definition);
            }
			return true;
	    }       
        else if (themeDescriptor == null && elementName.equals(TAG_CATEGORYDEFINITION)) {
            ThemeElementCategory definition = readCategory(element);
            if (definition != null) {
                categoryDefinitions.add(definition);
                themeRegistry.add(definition);
            }
			return true;
	    }
		else if (element.getName().equals(TAG_THEME)) {
		    if (themeDescriptor != null)
		        logError(element, "Cannot have nested themes.");
		    else {
				themeDescriptor = readTheme(element);
				if (themeDescriptor != null) {
				    readElementChildren(element);
				    themeDescriptor = null;
				}
				return true;
		    }
		}
        else if (themeDescriptor != null && elementName.equals(TAG_DESCRIPTION)) {
            themeDescriptor.setDescription(element.getValue());
			return true;
	    }
		
		return false;
	}
	
    /**
     * Read a font.
     * 
     * @param element the element to read
     * @return the new font
     */	
	private FontDefinition readFont(IConfigurationElement element) {
		String name = element.getAttribute(ATT_LABEL);

		String id = element.getAttribute(ATT_ID);

		String defaultMapping = element.getAttribute(ATT_DEFAULTS_TO);

		String value = element.getAttribute(ATT_VALUE);
		
		if (value != null && defaultMapping != null) {
			// TODO logError(element, RESOURCE_BUNDLE.getString("badDefault")); //$NON-NLS-1$
			return null;
		}
		
		String categoryId = element.getAttribute(ATT_CATEGORYID);
		
		String description = null;

		IConfigurationElement[] descriptions =
			element.getChildren(TAG_DESCRIPTION);

		if (descriptions.length > 0)
			description = descriptions[0].getValue();

		return
			new FontDefinition(
				name,
				id,
				defaultMapping,
				value,
				categoryId,
				description);	    
	}
	
    /**
     * Read a gradient.
     * 
     * @param element the element to read
     * @return the new gradient
     */	
	private GradientDefinition readGradient(IConfigurationElement element) {	
		String name = element.getAttribute(ATT_LABEL);
		
		String id = element.getAttribute(ATT_ID);
		
		int direction = SWT.HORIZONTAL;
		String directionString = element.getAttribute(ATT_DIRECTION);

		if (directionString != null && directionString.equalsIgnoreCase("VERTICAL")) //$NON-NLS-1$ 
		    direction = SWT.VERTICAL;
					
        
		IConfigurationElement[] children = element.getChildren(TAG_GRADIENTPART);			
        
		String values [] = new String[children.length + 1];		
		
        values[0] = element.getAttribute(ATT_INITIALVALUE);
        if (values[0] == null || values[0].equals("")) { //$NON-NLS-1$
            logError(element, RESOURCE_BUNDLE.getString("Gradient.badGradientValue")); //$NON-NLS-1$
            return null;                    
        }                        

        int [] percentages = new int[children.length];
        
        for (int i = 0; i < children.length; i++) {      
            String value = children[i].getAttribute(ATT_VALUE);
            if (value == null || value.equals("")) { //$NON-NLS-1$
                logError(element, RESOURCE_BUNDLE.getString("Gradient.badGradientValue")); //$NON-NLS-1$
                return null;                    
            }
            values[i + 1] = value;
            
            try {
                int percentage = Integer.parseInt(children[i].getAttribute(ATT_PERCENTAGE));
                if (percentage < 0) {
                    logError(element, RESOURCE_BUNDLE.getString("Gradient.badPercentage")); //$NON-NLS-1$
                    return null;
                }
                percentages[i] = percentage;
            }
            catch (NumberFormatException e) {
                logError(element, RESOURCE_BUNDLE.getString("Gradient.badPercentage")); //$NON-NLS-1$
                return null;
            }               
        }

		String categoryId = element.getAttribute(ATT_CATEGORYID);
		
		String description = null;

		IConfigurationElement[] descriptions =
			element.getChildren(TAG_DESCRIPTION);

		if (descriptions.length > 0)
			description = descriptions[0].getValue();

		return new GradientDefinition(
				name,
				id,
				direction,
				values,
				percentages,
				categoryId,
				description,
				element
					.getDeclaringExtension()
					.getDeclaringPluginDescriptor()
					.getUniqueIdentifier());
	}	
	
    /**
     * Read a theme.
     * 
     * @param element the element to read
     * @return the new theme
     */
	protected ThemeDescriptor readTheme(IConfigurationElement element) {
	    ThemeDescriptor desc = null;
		try {
			desc = new ThemeDescriptor(element);
			themeRegistry.add(desc);			
		} catch (CoreException e) {
			// log an error since its not safe to open a dialog here
			WorkbenchPlugin.log("Unable to create theme descriptor." , e.getStatus());//$NON-NLS-1$
		}
		return desc;
	}

    /**
	 * Read the theme extensions within a registry.
	 * 
	 * @param in the registry to read
	 * @param out the registry to write to
	 */
	public void readThemes(IPluginRegistry in, ThemeRegistry out)
		throws CoreException {
		// this does not seem to really ever be throwing an the exception
		setRegistry(out);
		readRegistry(in, PlatformUI.PLUGIN_ID, IWorkbenchConstants.PL_THEMES);

		// support for old font definitions
		readRegistry(in, PlatformUI.PLUGIN_ID, IWorkbenchConstants.PL_FONT_DEFINITIONS);
	}
	
	/**
	 * Set the output registry.
	 * 
	 * @param out the output registry
	 */
	public void setRegistry(ThemeRegistry out) {
	    themeRegistry = out;
	}
}
