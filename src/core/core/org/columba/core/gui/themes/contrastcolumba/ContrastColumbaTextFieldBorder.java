// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.core.gui.themes.contrastcolumba;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import javax.swing.border.*;

public class ContrastColumbaTextFieldBorder extends AbstractBorder implements UIResource
{

    private static final Insets insets = new Insets(2, 2, 1, 1);
    
    public void paintBorder(Component c, Graphics g, int x, int y,
			  int w, int h) {

          /*
            JTextField scroll = (JTextField)c;
            JComponent colHeader = scroll.getColumnHeader();
            int colHeaderHeight = 0;
            if (colHeader != null)
               colHeaderHeight = colHeader.getHeight();

            JComponent rowHeader = scroll.getRowHeader();
            int rowHeaderWidth = 0;
            if (rowHeader != null)
               rowHeaderWidth = rowHeader.getWidth();
          */
            g.translate( x, y);

            

            g.setColor( MetalLookAndFeel.getControlDarkShadow() );
            g.drawLine( 1,1,1, h-2 );
            g.drawLine( 1,1,w-2,1 );

            g.setColor( Color.black );
            g.drawRect( 0,0,w-1,h-1 );
            

              /*
            g.setColor( MetalLookAndFeel.getControlHighlight() );
            g.drawLine( 1, h-1, w-1, h-1 );
            g.drawLine( w-1, 0, w-1, h-1 );
              */
              //g.drawLine( 0, h, w, h );
              //g.drawLine( w, 0, w, h );
            
                        
            g.setColor( MetalLookAndFeel.getControl() );
            
            g.translate( -x, -y);

            
        }
    
    public Insets getBorderInsets( Component c )
    {
        return insets;
    }
   


    
}
