package org.argouml.swingext;

import java.awt.*;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */


public abstract class LineLayout implements LayoutManager2 {

    final static public int HORIZONTAL = Orientation.HORIZONTAL;
    final static public int VERTICAL = Orientation.VERTICAL;

    protected Orientation orientation;

    public LineLayout(int orientation) {
        this.orientation = Orientation.getOrientation(orientation);
    }

    public LineLayout(Orientation orientation) {
        this.orientation = orientation;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void addLayoutComponent(Component comp, Object constraints) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension preferredSize = new Dimension(0,0);
        for (int i = 0 ; i < nComps ; i++) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
                preferredSize = orientation.addLength(preferredSize, comp.getPreferredSize());
                if (orientation.getBreadth(comp.getPreferredSize()) > orientation.getBreadth(preferredSize)) {
                    preferredSize = orientation.setBreadth(preferredSize, comp.getPreferredSize());
                }
            }
        }
        preferredSize = orientation.add(preferredSize, parent.getInsets());
        return preferredSize;
    }

    public Dimension minimumLayoutSize(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension minimumSize = new Dimension(0,0);
        for (int i = 0 ; i < nComps ; i++) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
                minimumSize = orientation.addLength(minimumSize, orientation.getLength(comp.getMinimumSize()));
                if (orientation.getBreadth(comp.getMinimumSize()) > orientation.getBreadth(minimumSize)) {
                    minimumSize = orientation.setBreadth(minimumSize, comp.getMinimumSize());
                }
            }
        }
        minimumSize = orientation.add(minimumSize, parent.getInsets());
        return minimumSize;
    }

    public Dimension maximumLayoutSize(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension maximumSize = new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE);
        for (int i = 0 ; i < nComps ; i++) {
            Component comp = parent.getComponent(i);
            Dimension componentMaxSize = comp.getMaximumSize();
            if (comp.isVisible() && componentMaxSize != null) {
                maximumSize = orientation.addLength(maximumSize, orientation.getLength(componentMaxSize));
                if (orientation.getBreadth(componentMaxSize) < orientation.getBreadth(maximumSize)) {
                    maximumSize = orientation.setBreadth(maximumSize, componentMaxSize);
                }
            }
        }
        maximumSize = orientation.add(maximumSize, parent.getInsets());
        return maximumSize;
    }

    public void invalidateLayout(Container target) {}
    public float getLayoutAlignmentX(Container target) {return (float)0.5;}
    public float getLayoutAlignmentY(Container target) {return (float)0.5;}
}
