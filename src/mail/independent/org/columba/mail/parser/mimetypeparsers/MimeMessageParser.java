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

package org.columba.mail.parser.mimetypeparsers;

import org.columba.mail.message.Message;
import org.columba.mail.message.MimeHeader;
import org.columba.mail.message.MimePart;
import org.columba.mail.parser.MimeTypeParser;
import org.columba.mail.parser.Rfc822Parser;

public class MimeMessageParser extends MimeTypeParser
{
	public String getRegisterString() {
		return "message";	
	}
	
    public synchronized MimePart parse(MimeHeader header, String input)
    {
    	Message subMessage;
    	
        Rfc822Parser messageParser = new Rfc822Parser();		
		subMessage = messageParser.parse( input, true, null, 0 );
		
		return new MimePart( header, subMessage );
    }
}
