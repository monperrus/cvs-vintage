/*
 * @(#)NodeFigure.java 5.2
 *
 */

package org.jhotdraw.samples.offsetConnectors;

import java.awt.*;
import java.util.*;
import java.util.List;

import java.io.IOException;
import org.jhotdraw.framework.*;
import org.jhotdraw.standard.*;
import org.jhotdraw.figures.*;
import org.jhotdraw.util.*;


public class NodeFigure extends TextFigure {
    private static final int BORDER = 6;
    private Vector      fConnectors;
    private boolean    fConnectorsVisible;
    public NodeFigure() {
        initialize();
        fConnectors = null;
    }

    public Rectangle displayBox() {
        Rectangle box = super.displayBox();
        int d = BORDER;
        box.grow(d, d);
        return box;
    }

 


    public boolean containsPoint(int x, int y) {
        // add slop for connectors
        if (fConnectorsVisible) {
            Rectangle r = displayBox();
            int d = LocatorConnector.SIZE/2;
            r.grow(d, d);
            return r.contains(x, y);
        }
        return super.containsPoint(x, y);
    }

    private void drawBorder(Graphics g) {
        Rectangle r = displayBox();
        g.setColor(getFrameColor());
        g.drawRect(r.x, r.y, r.width-1, r.height-1);
    }

    public void draw(Graphics g) {
        super.draw(g);
        drawBorder(g);
        drawConnectors(g);
    }

    public HandleEnumeration handles() {
         ConnectionFigure prototype = new LineConnection();
         List handles = CollectionsFactory.current().createList();
         handles.add(new ConnectionHandle(this, RelativeLocator.east(), prototype));
         handles.add(new ConnectionHandle(this, RelativeLocator.west(), prototype));
         handles.add(new ConnectionHandle(this, RelativeLocator.south(), prototype));
         handles.add(new ConnectionHandle(this, RelativeLocator.north(), prototype));

         handles.add(new NullHandle(this, RelativeLocator.southEast()));
         handles.add(new NullHandle(this, RelativeLocator.southWest()));
         handles.add(new NullHandle(this, RelativeLocator.northEast()));
         handles.add(new NullHandle(this, RelativeLocator.northWest()));
         return new HandleEnumerator(handles);
     }

    private void drawConnectors(Graphics g) {
        if (fConnectorsVisible) {
            Enumeration e = connectors().elements();
            while (e.hasMoreElements())
                ((Connector) e.nextElement()).draw(g);
        }
    }

    /**
     */
    public void connectorVisibility(boolean isVisible) {
        fConnectorsVisible = isVisible;
        invalidate();
    }

    /**
     */
    public Connector connectorAt(int x, int y) {
        return findConnector(x, y);
    }

    /**
     */
    private Vector connectors() {
        if (fConnectors == null)
            createConnectors();
        return fConnectors;
    }

    private void createConnectors() {
        fConnectors = new Vector(4);
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.north()));
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.south()));
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.west()));
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.east()));
    }

    private Connector findConnector(int x, int y) {
        // return closest connector
        long min = Long.MAX_VALUE;
        Connector closest = null;
        Enumeration e = connectors().elements();
        while (e.hasMoreElements()) {
            Connector c = (Connector)e.nextElement();
            Point p2 = Geom.center(c.displayBox());
            long d = Geom.length2(x, y, p2.x, p2.y);
            if (d < min) {
                min = d;
                closest = c;
            }
        }
        return closest;
    }

    private void initialize() {
        setText("node");
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        setFont(fb);
        createConnectors();
    }
}
