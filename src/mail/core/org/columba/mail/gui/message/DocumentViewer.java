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

package org.columba.mail.gui.message;

import java.awt.Component;

interface DocumentViewer
{
    abstract public void setDoc( String str );
    abstract public String getDoc();
    abstract public void setActive( boolean b );
    abstract public boolean isActive();
    abstract public void clearDoc();
    abstract public String getSelectedText();
    abstract public void setHeader( Component header );
}
