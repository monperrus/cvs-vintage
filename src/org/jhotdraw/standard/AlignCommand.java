/*
 * @(#)AlignCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.util.*;
import java.awt.*;

import CH.ifa.draw.framework.*;

/**
 * Align a selection of figures relative to each other.
 */
public class AlignCommand extends AbstractCommand {

    private int fOp;

   /**
    * align left sides
    */
    public final static int LEFTS = 0;
   /**
    * align centers (horizontally)
    */
    public final static int CENTERS = 1;
   /**
    * align right sides
    */
    public final static int RIGHTS = 2;
   /**
    * align tops
    */
    public final static int TOPS = 3;
   /**
    * align middles (vertically)
    */
    public final static int MIDDLES = 4;
   /**
    * align bottoms
    */
    public final static int BOTTOMS = 5;


   /**
    * Constructs an alignment command.
    * @param name the command name
    * @param view the target view
    * @param op the alignment operation (LEFTS, CENTERS, RIGHTS, etc.)
    */
    public AlignCommand(String name, DrawingView view, int op) {
        super(name, view);
        fOp = op;
    }

    public boolean isExecutable() {
        return view().selectionCount() > 1;
    }

    public void execute() {
        FigureEnumeration selection = view().selectionElements();
        Figure anchorFigure = selection.nextFigure();
        Rectangle r = anchorFigure.displayBox();

        while (selection.hasMoreElements()) {
            Figure f = selection.nextFigure();
            Rectangle rr = f.displayBox();
            switch (fOp) {
            case LEFTS:
                f.moveBy(r.x-rr.x, 0);
                break;
            case CENTERS:
                f.moveBy((r.x+r.width/2) - (rr.x+rr.width/2), 0);
                break;
            case RIGHTS:
                f.moveBy((r.x+r.width) - (rr.x+rr.width), 0);
                break;
            case TOPS:
                f.moveBy(0, r.y-rr.y);
                break;
            case MIDDLES:
                f.moveBy(0, (r.y+r.height/2) - (rr.y+rr.height/2));
                break;
            case BOTTOMS:
                f.moveBy(0, (r.y+r.height) - (rr.y+rr.height));
                break;
            }
        }
        view().checkDamage();
    }
}


