package org.argouml.swingext;

import java.awt.*;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author Bob Tarling
 * @version 1.0
 */

public class ProportionalLayout extends LineLayout {

    protected Hashtable componentTable;

    public ProportionalLayout() {
        this(HORIZONTAL);
    }

    public ProportionalLayout(int orientation) {
        super(orientation);
        componentTable = new Hashtable();
    }

    public final void addLayoutComponent(Component comp, Object constraints) {
        if (constraints == null) constraints = "";
        addLayoutComponent((String)constraints, comp);
    }

    public void addLayoutComponent(String name, Component comp) {
        try {
          componentTable.put(comp, name.toString());
        }
        catch (Exception e) {
          componentTable.put(comp, "");
        }
    }

    public void removeLayoutComponent(Component comp) {
        componentTable.remove(comp);
    }

    public void layoutContainer(Container parent) {
        // Find the total proportional size of all visible components
        double totalProportionalLength = 0;
        int totalLength;

        totalLength = orientation.getLengthMinusInsets(parent);

        Enumeration enumKeys = componentTable.keys();
        while (enumKeys.hasMoreElements()) {
            Component comp = (Component)enumKeys.nextElement();
            if (comp.isVisible()) {
                String size = (String)(componentTable.get(comp));
                if (size.length() != 0) {
                    totalProportionalLength += Double.parseDouble(size);
                }
                else {
                    totalLength -= orientation.getLength(comp);
                }
            }
        }

        Insets insets = parent.getInsets();
        Point loc = new Point(insets.top, insets.left);
        int length = 0;
        int nComps = parent.getComponentCount();
        for (int i = 0 ; i < nComps ; i++) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
                String proportionalLength = (String)(componentTable.get(comp));
                if (proportionalLength.length() != 0) {
                    length = (int) ((totalLength * Double.parseDouble(proportionalLength)) / totalProportionalLength);
                    if (length < 0) length = 0;
                }
                else {
                    length = orientation.getLength(comp);
                }
                comp.setSize(orientation.setLength(parent.getSize(), length));
                comp.setLocation(loc);
                loc = orientation.addLength(loc, length);
            }
        }
    }
}
