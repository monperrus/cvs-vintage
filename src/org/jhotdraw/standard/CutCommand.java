/*
 * @(#)CutCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Delete the selection and move the selected figures to
 * the clipboard.
 * @see Clipboard
 */
public class CutCommand extends FigureTransferCommand {

   /**
    * Constructs a cut command.
    * @param name the command name
    * @param view the target view
    */
    public CutCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
        copySelection();
        deleteSelection();
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}


