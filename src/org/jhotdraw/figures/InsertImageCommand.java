/*
 * @(#)InsertImageCommand.java 5.2
 *
 */

package CH.ifa.draw.figures;

import java.util.*;
import java.awt.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * Command to insert a named image.
 */
public class InsertImageCommand extends AbstractCommand {

    private String       fImage;

   /**
    * Constructs an insert image command.
    * @param name the command name
    * @param image the pathname of the image
    * @param view the target view
    */
    public InsertImageCommand(String name, String image, DrawingView view) {
        super(name, view);
        fImage = image;
    }

    public void execute() {
        // ugly cast to component, but AWT wants and Component instead of an ImageObserver...
        Image image = Iconkit.instance().registerAndLoadImage((Component)view(), fImage);
        ImageFigure figure = new ImageFigure(image, fImage, view().lastClick());
        view().add(figure);
        view().clearSelection();
        view().addToSelection(figure);
        view().checkDamage();
    }
}



