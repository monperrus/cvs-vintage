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

package org.columba.mail.imap.parser;

import java.util.StringTokenizer;
import java.util.Vector;

import org.columba.mail.folder.MessageFolderInfo;
import org.columba.mail.message.ColumbaHeader;
import org.columba.mail.message.Flags;
import org.columba.mail.message.Message;
import org.columba.mail.message.MimeHeader;
import org.columba.mail.message.MimePart;
import org.columba.mail.message.MimePartTree;
import org.columba.mail.parser.Rfc822Parser;

public class Imap4Parser {

	public Imap4Parser() {
	}

	// prepare header for parsing with Rfc822Parser-class
	public ColumbaHeader parseHeader(String s) {
		Rfc822Parser parser = new Rfc822Parser();

		return parser.parseHeader(s);
	}

	protected String removeClubs(String s) {
		String str = s;

		if (s.startsWith("\"")) {
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}

	public String parseBodyText(String s) {
		return s;
		/*int index = s.indexOf( "}" );
		String result;
		
		
		if ( s.length() == 0 ) return new String("");
		
		result = s.substring( index+1 , s.length()-2 );
		return result;*/
	}

	

	/*
	public Flags parseAttributs( String s )
	{
	    String str,str2;
	    StringBuffer result;
	
	    StringTokenizer tok = new StringTokenizer( s, " " );
	    StringTokenizer tok2;
	
	    Flags flags = new Flags();
	
	
	
	    while ( tok.hasMoreElements() )
	    {
	        str = (String) tok.nextElement();
	
	        if ( str.indexOf("FLAGS") != -1 )
	        {
	            result = new StringBuffer();
	            while (  tok.hasMoreElements() )
	            {
	                    str = (String) tok.nextElement();
	                    result.append( str );
	            }
	
	            flags = parseFlags( result.toString()  );
	
	        }
	
	    }
	    return flags;
	}
	*/
	public Integer parseSize(String s) {
		//System.out.println("size: "+ s );
		String str, str2;
		StringBuffer result;

		StringTokenizer tok = new StringTokenizer(s, " ");
		StringTokenizer tok2;

		Integer i = new Integer(0);

		while (tok.hasMoreElements()) {
			str = (String) tok.nextElement();

			if (str.indexOf("SIZE") != -1) {
				str = (String) tok.nextElement();
				str = str.substring(0,str.length()-1);
				
				i = new Integer(str);

				//System.out.println("found size: "+i);
			}
		}
		return i;
	}

	public Vector parseUids(String s) {
		String str, str2;
		StringBuffer result;

		StringTokenizer tok = new StringTokenizer(s, "\n");
		StringTokenizer tok2;

		Vector v = new Vector();

		//  System.out.println("parsing for UIDs");

		while (tok.hasMoreElements()) {
			str = (String) tok.nextElement();
			tok2 = new StringTokenizer(str, " ");

			while (tok2.hasMoreElements()) {
				str2 = (String) tok2.nextElement();

				// if token equals UID parse number
				if (str2.indexOf("UID") != -1) {
					str2 = (String) tok2.nextElement();
					//System.out.println("str: "+str2);
					str2 = str2.substring(0, str2.length() - 1);
					//System.out.println("str: "+str2);
					//Integer i = new Integer( str2 );

					v.add(str2);
					//System.out.println("found UID: "+i);
				}

			}
		}

		//for ( int i=0; i< v.size(); i++)
		// {
		//  System.out.println("uid: "+ v.get(i));
		// }

		return v;
	}

	public static Vector parseLsub(String s) {
		StringTokenizer tok = new StringTokenizer(s, "\n");

		String str;

		Vector v = new Vector();
		boolean firstTime = false;

		while (tok.hasMoreElements()) {
			str = (String) tok.nextElement();

			String path = null;

			path = extractPath(str);
			System.out.println("path:" + path);
			if (path != null)
				v.add(path);

		}

		return v;
	}

	protected static boolean validDelimiter(int pos, String path) {
		System.out.println("validate string:" + path);
		System.out.println("pos:" + pos + " - char:" + path.charAt(pos));

		char left = path.charAt(pos - 1);
		char right = path.charAt(pos + 1);

		if ((left == '"') && (right == '"'))
			return true;

		return false;
	}

	public static String parseDelimiter(String list) {
		StringTokenizer tok = new StringTokenizer(list, "\n");

		String s;

		String delimiter = new String(".");

		while (tok.hasMoreElements()) {
			s = (String) tok.nextElement();
			int index;
			String result;
			int delimiterIndex;

			if (s.endsWith("\"")) {
				// string ends with "
				// this means the path is enclosed in ""
				// for example: "My Files"

				index = s.lastIndexOf("\"", s.length() - 2);
				result = s.substring(index + 1, s.length() - 1);
				System.out.println("result:" + result);

				delimiterIndex = index - 3;
				delimiter = s.substring(delimiterIndex, delimiterIndex + 1);
			} else {
				// path is not enclosed in ""
				// this means it does not contain whitespaces
				// for example: testorder but not "test order"

				index = s.lastIndexOf(" ");
				result = s.substring(index + 1, s.length());
				delimiterIndex = index - 2;
				delimiter = s.substring(delimiterIndex, delimiterIndex + 1);
			}

			if (validDelimiter(delimiterIndex, s) == true)
				return delimiter;
		}

		return delimiter;
	}

	protected static String extractPath(String s) {
		String result = new String();

		if (s.endsWith("\"")) {
			// string ends with "
			// this means the path is enclosed in ""
			// for example: "My Files"

			int index = s.lastIndexOf("\"", s.length() - 2);
			result = s.substring(index + 1, s.length() - 1);

		} else if (s.endsWith("}")) {
			// cyrus-imapd hack
			return null;
		} else {
			// path is not enclosed in ""
			// this means it does not contain whitespaces
			// for example: testorder but not "test order"

			result = s.substring(s.lastIndexOf(" ") + 1, s.length());
		}

		return result;
	}

	// FIXME
	/*
	public static Vector parseList(String s) {
		//System.out.println("parsing:\n" + s);

		StringTokenizer tok = new StringTokenizer(s, "\n");
		StringTokenizer tok2;

		String str;
		String str2 = new String();

		Vector v = new Vector();

		boolean firstTime = true;

		while (tok.hasMoreElements()) {
			str = (String) tok.nextElement();

			//str2 = str.substring( str.lastIndexOf(" ")+1, str.length() );
			str2 = extractPath(str);

			// System.out.println("str2: "+ str2 );

			String str3 = str2;
			int index = str2.lastIndexOf("/");
			if (index != -1) {
				if (index != str.length() - 1)
					str3 = str2.substring(index + 1, str2.length());
			}

			// System.out.println("str3: "+ str3 );

			SubscribeTreeNode treeNode = new SubscribeTreeNode(str3);
			treeNode.setDirectoryPath(str2);

			str = str.toLowerCase();

			if (str.indexOf("noselect") != -1) {
				// this is a directory
				treeNode.setDirectory(true);
			} else {
				treeNode.setDirectory(false);
			}

			if (str.indexOf("noinferiors") != -1) {
				// this is a directory
				treeNode.setSubFolder(true);
			} else {
				treeNode.setSubFolder(false);
			}

			v.add(treeNode);

		}
		return v;
	}
	*/
	
	
	/*
	public static Vector parseSubList( String s )
	{
	    StringTokenizer tok = new StringTokenizer( s, "\n" );
	    StringTokenizer tok2;
	
	    String str;
	    String str2 = new String();
	
	    Vector v = new Vector();
	
	    boolean firstTime = true;
	
	    while ( tok.hasMoreElements() )
	    {
	        str = (String) tok.nextElement();
	        System.out.println( "token: "+ str );
	
	        if ( firstTime )
	        {
	            firstTime = false;
	            continue;
	        }
	
	        //str2 = str.substring( str.lastIndexOf("/")+1, str.length() );
	        str2 = extractPath( str );
	
	
	
	        SubscribeTreeNode treeNode = new SubscribeTreeNode( str2 );
	        treeNode.setDirectoryPath( str2 );
	
	        str = str.toLowerCase();
	
	        if ( str.indexOf("noselect") != -1 )
	        {
	            // this is a directory
	            treeNode.setDirectory( true );
	        }
	        else
	        {
	            treeNode.setDirectory( false );
	        }
	
	        if ( str.indexOf("noinferiors") != -1 )
	        {
	            // this is a directory
	            treeNode.setSubFolder( true );
	        }
	        else
	        {
	            treeNode.setSubFolder( false );
	        }
	
	
	
	        v.add( treeNode );
	
	
	    }
	    return v;
	}
	*/

	public static MessageFolderInfo parseMessageFolderInfo(String s) {
		MessageFolderInfo info = new MessageFolderInfo();

		StringTokenizer tok = new StringTokenizer(s, "\n");

		String str, str2;
		Integer i;

		while (tok.hasMoreElements()) {
			str = (String) tok.nextElement();
			
			if (str.indexOf("EXISTS") != -1) {

				str2 =
					str.substring(
						str.indexOf("*") + 2,
						str.indexOf("EXISTS") - 1);

				i = new Integer(str2);

				info.setExists(i.intValue());

			} else if (str.indexOf("RECENT") != -1) {

				str2 =
					str.substring(
						str.indexOf("*") + 2,
						str.indexOf("RECENT") - 1);

				i = new Integer(str2);

				//info.setRecent(i.intValue());

			} else if (str.indexOf("UIDVALIDITY") != -1) {

				str2 =
					str.substring(
						str.indexOf("UIDVALIDITY") + 11 + 1,
						str.indexOf("]"));

				i = new Integer(str2);

				info.setUidValidity(i.intValue());

			} else if (str.indexOf("UIDNEXT") != -1) {

				str2 =
					str.substring(
						str.indexOf("UIDNEXT") + 7 + 1,
						str.indexOf("]"));

				i = new Integer(str2);

				info.setUidNext(i.intValue());

			} else if (str.indexOf("FLAGS") != -1) {

				str2 = str.substring(str.indexOf("(") + 1, str.indexOf(")"));

				//info.setFlags( parseFlags(str2) );
			} else if (str.indexOf("PERMANENTFLAGS") != -1) {

				str2 = str.substring(str.indexOf("(") + 1, str.indexOf(")"));

				//info.setPermanentFlags(parseFlags(str2) );
			} else if (str.indexOf("UNSEEN") != -1) {

				str2 =
					str.substring(
						str.indexOf("UNSEEN") + 6 + 1,
						str.indexOf("]"));

				i = new Integer(str2);

				//info.setUnseen(i.intValue());

			} else if (str.indexOf("READ-WRITE") != -1) {

				info.setReadWrite(true);

			}

		}

		return info;
	}

	public static void parseFlags(String str, Message message) {
		Flags flags = message.getFlags();

		//System.out.println("flags: "+ str );

		if (str.indexOf("Seen") != -1) {
			//System.out.println("seen is true ");
			flags.setSeen(true);
		}
		if (str.indexOf("Answered") != -1) {
			//System.out.println("answered is true ");
			flags.setAnswered(true);
		}
		if (str.indexOf("Flagged") != -1) {
			//System.out.println("flagged is true ");
			flags.setFlagged(true);
		}
		if (str.indexOf("Deleted") != -1) {
			//System.out.println("deleted is true ");
			flags.setDeleted(true);
		}
		if (str.indexOf("Draft") != -1) {
			flags.setDraft(true);
		}

	}

	public MimePartTree parseBodyStructure(String input) {
		int start = input.indexOf("BODYSTRUCTURE");

		if (start == -1) {
			System.out.println("String from Server / not expected: " + input);
			return null;
		}
		
		int openParenthesis = input.indexOf("(", start);

		String bodystructure =
			input.substring(
				openParenthesis + 1,
				BodystructureTokenizer.getClosingParenthesis(input, openParenthesis));

		return new MimePartTree(parseBS(bodystructure));
	}

	private MimePart parseBS(String input) {
		MimePart result;

		if (input.charAt(0) == '(') {
			result = new MimePart(new MimeHeader("multipart", null));

			BodystructureTokenizer tokenizer =
				new BodystructureTokenizer(input);
			BodystructureTokenizer subtokenizer;

			Item nextItem = tokenizer.getNextItem();
			Item valueItem;

			while (nextItem != null) {
				if (nextItem.getType() != Item.PARENTHESIS)
					break;

				result.addChild(parseBS((String) nextItem.getValue()));
				nextItem = tokenizer.getNextItem();
			}

			// Parse the Rest of the Multipart

			// Subtype / nextItem is already filled from break in while-block
			result.getHeader().contentSubtype = ((String) nextItem.getValue()).toLowerCase();

			// Search for any ContentParameters
			nextItem = tokenizer.getNextItem();
			if (nextItem.getType() == Item.PARENTHESIS) {
				subtokenizer =
					new BodystructureTokenizer((String) nextItem.getValue());

				nextItem = subtokenizer.getNextItem();
				while (nextItem != null) {
					valueItem = subtokenizer.getNextItem();

					result.getHeader().putContentParameter(
						((String) nextItem.getValue()).toLowerCase(),
						(String) valueItem.getValue());

					nextItem = subtokenizer.getNextItem();
				}
			}

			// ID
			nextItem = tokenizer.getNextItem();
			if( nextItem == null ) return result;
			if (nextItem.getType() == Item.STRING)
				result.getHeader().contentID =
					((String) nextItem.getValue()).toLowerCase();

			// Description
			nextItem = tokenizer.getNextItem();
			if( nextItem == null ) return result;
			if (nextItem.getType() == Item.STRING)
				result.getHeader().contentDescription =
					((String) nextItem.getValue()).toLowerCase();

		} else {

			result = parseMimeStructure(input);
		}

		return result;
	}

	private ColumbaHeader parseEnvelope(String envelope) {
		
		ColumbaHeader result = new ColumbaHeader();
		
		BodystructureTokenizer tokenizer = new BodystructureTokenizer( envelope );
		Item nextItem;
		
		// Date
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.STRING )
			result.set( "date", (String) nextItem.getValue() );

		// Subject
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.STRING )
			result.set( "subject", (String) nextItem.getValue() );
		
		// From
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );

		// Sender
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );

		// Reply-To
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );
		
		// To
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );

		// Cc
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );

		// Bcc
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.PARENTHESIS );

		// In-Reply-To
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.STRING )
			result.set( "in-reply-to", (String) nextItem.getValue() );
		
		// Message-ID
		nextItem = tokenizer.getNextItem();
		if( nextItem.getType() == Item.STRING )
			result.set( "message-id", (String) nextItem.getValue() );
				
			
		return result;
	}

	private MimePart parseMimeStructure(String structure) {
		MimeHeader header = new MimeHeader();
		MimePart result = new MimePart( header );
		BodystructureTokenizer tokenizer =
			new BodystructureTokenizer(structure);
		BodystructureTokenizer subtokenizer;
		Item nextItem, valueItem;

		// Content-Type    	
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.STRING)
			header.contentType = ((String) nextItem.getValue()).toLowerCase();

		// ContentSubtype
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.STRING)
			header.contentSubtype =
				((String) nextItem.getValue()).toLowerCase();

		// are there some Content Parameters ?
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.PARENTHESIS) {
			subtokenizer =
				new BodystructureTokenizer((String) nextItem.getValue());

			nextItem = subtokenizer.getNextItem();
			while (nextItem != null) {
				valueItem = subtokenizer.getNextItem();

				header.putContentParameter(
					((String) nextItem.getValue()).toLowerCase(),
					(String) valueItem.getValue());

				nextItem = subtokenizer.getNextItem();
			}
		}

		// ID
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.STRING)
			header.contentID = ((String) nextItem.getValue()).toLowerCase();

		// Description
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.STRING)
			header.contentDescription =
				((String) nextItem.getValue()).toLowerCase();

		// Encoding
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.STRING)
			header.contentTransferEncoding =
				((String) nextItem.getValue()).toLowerCase();

		// Size
		nextItem = tokenizer.getNextItem();
		if (nextItem.getType() == Item.NUMBER)
			header.size = (Integer) nextItem.getValue();



		// Is this a Message/RFC822 Part ?
		if ((header.contentType.equals("message"))
			& (header.contentSubtype.equals("rfc822"))) {
	
			Message subMessage = new Message();
		
			// Envelope
		
			nextItem = tokenizer.getNextItem();
			if( nextItem.getType() == Item.PARENTHESIS ) {
				subMessage.setHeader( parseEnvelope( (String) nextItem.getValue() ));	
			}
		
			// Bodystrucuture of Sub-Message
			
			nextItem = tokenizer.getNextItem();
			if( nextItem.getType() == Item.PARENTHESIS ) {
				MimePart subStructure = parseBS( (String) nextItem.getValue());
				subStructure.setParent( result );
				subMessage.setMimePartTree( new MimePartTree( subStructure ));
			}			

			result.setContent( subMessage );	
			
			// Number of lines			
			nextItem = tokenizer.getNextItem();
			
		}
		// Is this a Text - Part ?
		else if (header.contentType.equals("text")) {
			// Number of lines
			nextItem = tokenizer.getNextItem();
			// DONT CARE
		}


		// Are there Extensions ?
		// MD5
		nextItem = tokenizer.getNextItem();
		if (nextItem == null)
			return new MimePart(header);
		// DONT CARE

		// are there some Disposition Parameters ?
		nextItem = tokenizer.getNextItem();
		if (nextItem == null)
			return new MimePart(header);

		if (nextItem.getType() == Item.STRING) {
			header.contentDisposition =
				((String) nextItem.getValue()).toLowerCase();
		} else if (nextItem.getType() == Item.PARENTHESIS) {
			subtokenizer =
				new BodystructureTokenizer((String) nextItem.getValue());

			nextItem = subtokenizer.getNextItem();
			header.contentDisposition =
				((String) nextItem.getValue()).toLowerCase();

			nextItem = subtokenizer.getNextItem();
			// Are there Disposition Parameters?
			if (nextItem.getType() == Item.PARENTHESIS) {
				subtokenizer =
					new BodystructureTokenizer((String) nextItem.getValue());

				nextItem = subtokenizer.getNextItem();					
					
				while (nextItem != null) {
					valueItem = subtokenizer.getNextItem();

					header.putDispositionParameter(
						((String) nextItem.getValue()).toLowerCase(),
						(String) valueItem.getValue());

					nextItem = subtokenizer.getNextItem();					
				}
			}
		}

		// WE DO NOT GATHER FURTHER INFORMATION

		return result;
	}

	public static Vector parseSearch(String s) {
		Vector result = new Vector();

		StringTokenizer tok = new StringTokenizer(s, "\n");
		while (tok.hasMoreTokens()) {
			String nextLine = tok.nextToken();

			if (!nextLine.startsWith("* SEARCH"))
				continue;
			if (nextLine.length() <= 9)
				continue;

			String numberString = nextLine.substring(9, nextLine.length());
			System.out.println("<" + numberString + ">");
			numberString = numberString.trim();
			StringTokenizer tok2 = new StringTokenizer(numberString, " ");
			while (tok2.hasMoreTokens()) {
				String nextLine2 = tok2.nextToken();
				//System.out.println("added <"+nextLine2+">");
				result.add(nextLine2);
			}
		}

		return result;
	}

}

class BodystructureTokenizer {

	private String s;
	private Interval i;

	public BodystructureTokenizer(String s) {
		this.s = s;
		i = new Interval();
	}

	public Item getNextItem() {
		Item result = new Item();

		// Search for next Item
		i.a = i.b + 1;
		// ..but Check Bounds!!
		if (i.a >= s.length())
			return null;
		while (s.charAt(i.a) == ' ') {
			i.a++;
			if (i.a >= s.length())
				return null;
		}

		// Quoted

		if (s.charAt(i.a) == '\"') {
			i.b = s.indexOf("\"", i.a + 1);

			result.setType(Item.STRING);
			result.setValue(s.substring(i.a + 1, i.b));
		}

		// Parenthesized

		else if (s.charAt(i.a) == '(') {
			i.b = getClosingParenthesis(s, i.a);

			result.setType(Item.PARENTHESIS);
			result.setValue(s.substring(i.a + 1, i.b));
		}

		// NIL or Number

		else {
			i.b = s.indexOf(" ", i.a + 1);
			if( i.b == -1 ) 
				i.b = s.length();

			String item = s.substring(i.a, i.b);
			i.b--;

			if (item.equals("NIL")) {
				result.setType(Item.NIL);
			} else {
				result.setValue(new Integer(item));
				result.setType(Item.NUMBER);
			}
		}

		return result;
	}

	static public int getClosingParenthesis(String s, int openPos) {
		int nextOpenPos = s.indexOf("(", openPos + 1);
		int nextClosePos = s.indexOf(")", openPos + 1);

		while ((nextOpenPos < nextClosePos) & (nextOpenPos != -1)) {
			nextClosePos = s.indexOf(")", nextClosePos + 1);
			nextOpenPos = s.indexOf("(", nextOpenPos + 1);
		}
		return nextClosePos;
	}
}

class Item {
	public static final int STRING = 0;
	public static final int PARENTHESIS = 1;
	public static final int NIL = 2;
	public static final int NUMBER = 3;

	private Object value;
	private int type;
	/**
	 * Returns the type.
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the value.
	 * @return Object
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}

class Interval {
	public int a, b;
	public int type;

	public Interval(int a, int b) {
		this.a = a;
		this.b = b;
	}

	public Interval() {
		a = -1;
		b = -1;
	}

	public void reset() {
		a = -1;
		b = -2;
	}
}