//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.

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
