/*
 * Created on 23-06-2003
 */
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

package org.columba.mail.parser.text;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.columba.core.logging.ColumbaLogger;

/**
 * Contains different utility functions for manipulating Html based
 * text. This includes functionality for removing and restoring
 * special entities (such as &, <, >, ...) and functionality for
 * removing html tags from the text.
 * 
 * @author Karl Peder Olesen (karlpeder), 20030623
 *
 */
public class HtmlParser {

	private static final Pattern breakToNLPattern =
		Pattern.compile("\\<[/]?br\\>", Pattern.CASE_INSENSITIVE);
	private static final Pattern pToDoubleNLPattern =
		Pattern.compile("\\</p\\>", Pattern.CASE_INSENSITIVE);
	private static final Pattern divToDoubleNLPattern =
		Pattern.compile("\\</div\\>", Pattern.CASE_INSENSITIVE);
	private static final Pattern hToDoubleNLPattern =
		Pattern.compile("\\</h\\d\\>", Pattern.CASE_INSENSITIVE);
	private static final Pattern whiteSpaceRemovalPattern =
		Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
	private static final Pattern headerRemovalPattern = 
		Pattern.compile("\\<html(.|\n|\r)*?\\<body(.|\n|\r)*?\\>", 
			Pattern.CASE_INSENSITIVE);
	private static final Pattern stripTagsPattern =
		Pattern.compile("\\<(.|\\n)*?\\>", Pattern.CASE_INSENSITIVE);

	private static final Pattern emailPattern =
		Pattern.compile("([\\w.\\-]*\\@([\\w\\-]+\\.*)+[a-zA-Z0-9]{2,})");
		
	private static String urls = "(http|https|ftp)";
	private static String letters = "\\w";
	private static String gunk = "/#~:;.?+=&@!\\-%";
	private static String punc = ".:?\\-";
	private static String any = "${" + letters + "}${" + gunk + "}${" + punc + "}";		

	/**
	 *
	 *  
	 * \\b  				start at word boundary
	 * (					begin $1
	 * urls:				url can be (http:, https:, ftp:) 
	 * [any]+?				followed by one or more of any valid character
	 * 						(be conservative - take only what you need)
	 * )					end of $1
	 * (?=					look-ahead non-consumptive assertion
	 * [punc]*				either 0 or more punctuation
	 * [^any]				  followed by a non-url char
	 * |					or else
	 * $					  then end of the string
	 * )
	 */	
	private static final Pattern urlPattern =
		Pattern.compile("\\b"
	+ "("
	+ urls
	+ ":["
	+ any
	+ "]+?)(?=["
	+ punc
	+ "]*[^"
	+ any
	+ "]|$)");

	// TODO: Add more special entities - e.g. accenture chars such as �

	/** Special entities recognized by restore special entities */
	private static String[] SPECIAL_ENTITIES = {
	 	"&lt;",		"&gt;",		"&amp;",	"&nbsp;",	"&#160;",
		"&quot;",	"&apos;",	"&aelig;",	"&#230;",	"&oslash;",
		"&#248;",	"&aring;",	"&#229;",	"&AElig;",	"&#198;",
		"&Oslash;",	"&#216;",	"&Aring;",	"&#197;"
		};
			
	/** Normal chars corresponding to the defined special entities */
	private static char[] ENTITY_CHARS = {
		'<',		'>',		'&',		' ',		' ',
		'"',		'\'',		'�',		'�',		'�',
		'�',		'�',		'�',		'�',		'�',
		'�',		'�',		'�',		'�'
		};

	/**
	 * Strips html tags and removes extra spaces which occurs due
	 * to e.g. indentation of the html and the head section, which does 
	 * not contain any textual information.
	 * <br>
	 * The conversion rutine does the following:<br>
	 * 1. Removes the header from the html file, i.e. everything from
	 *    the html tag until and including the starting body tag.<br>
	 * 2. Replaces multiple consecutive whitespace characters with a single
	 *    space (since extra whitespace should be ignored in html).<br>
	 * 3. Replaces ending br tags with a single newline character<br>
	 * 4. Replaces ending p, div and heading tags with two newlines characters; 
	 *    resulting in a single empty line btw. paragraphs.<br>
	 * 5. Strips remaining html tags.<br>
	 * <br>
	 * NB: The tag stripping is done using a very simple regular expression,
	 * which removes everything between &lt and &gt. Therefore too much text
	 * could in some (hopefully rare!?) cases be removed.
	 * 
	 * @param	s		Input string
	 * @return	Input stripped for html tags
	 * @author	Karl Peder Olesen (karlpeder)
	 */
	public static String stripHtmlTags(String s) {
		// initial check of input:
		if (s == null)
			return null;

		// remove header
		s = headerRemovalPattern.matcher(s).replaceAll("");

		// remove extra whitespace
		s = whiteSpaceRemovalPattern.matcher(s).replaceAll(" ");
		
		// replace br, p and heading tags with newlines
		s = breakToNLPattern.matcher(s).replaceAll("\n");
		s = pToDoubleNLPattern.matcher(s).replaceAll("\n\n");
		s = divToDoubleNLPattern.matcher(s).replaceAll("\n\n");
		s = hToDoubleNLPattern.matcher(s).replaceAll("\n\n");
		
		// strip remaining tags
		s = stripTagsPattern.matcher(s).replaceAll("");
		
		return s;
	}

	/**
	 * Strips html tags. The method used is very simple:
	 * Everything between tag-start (&lt) and tag-end (&gt) is removed.
	 * Optionaly br tags are replaced by newline and ending p tags with
	 * double newline.
	 * 
	 * @param	s			input string
	 * @param	breakToNl	if true, newlines are inserted for br and p tags
	 * @return	output without html tags (null on error)
	 * @author	karlpeder, 20030623
	 * 			(moved from org.columba.mail.gui.message.util.DocumentParser)
	 * 
	 * @deprecated	Please use the more advanced and correct 
	 *              @see stripHtmlTags(String) method 
	 */
	public static String stripHtmlTags(String s, boolean breakToNl) {

		// initial check of input:
		if (s == null)
			return null;

		if (breakToNl) {
			// replace <br> and </br> with newline
			s = breakToNLPattern.matcher(s).replaceAll("\n");

			// replace </p> with double newline
			s = pToDoubleNLPattern.matcher(s).replaceAll("\n\n");
		}

		// strip tags
		s = stripTagsPattern.matcher(s).replaceAll("");

		return s;

	}

	/**
	 * Performs in large terms the reverse of
	 * substituteSpecialCharacters (though br tags are not 
	 * converted to newlines, this should be handled separately).
	 * More preciesly it changes special entities like
	 * amp, nbsp etc. to their real counter parts: &, space etc. 
	 * 	 
	 * @param	s	input string
	 * @return	output with special entities replaced with their
	 * 			"real" counter parts (null on error)
	 * @author  karlpeder, 20030623
	 * 			(moved from org.columba.mail.gui.message.util.DocumentParser)
	 */
	public static String restoreSpecialCharacters(String s) {

		// initial check of input:
		if (s == null)
			return null;

		StringBuffer sb = new StringBuffer(s.length());
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String ss = null;

		try {

			while ((ss = br.readLine()) != null) {
				int pos = 0;
				while (pos < ss.length()) {
					char c = ss.charAt(pos);
					if (c == '&') {
						// a special character is possibly found
						
						if (ss.substring(pos).startsWith("&nbsp;&nbsp;&nbsp;&nbsp;") ||
						    ss.substring(pos).startsWith("&#160;&#160;&#160;&#160;")) {
						    	// 4 spaces -> tab character
						    	sb.append('\t');
						    	pos = pos + 24;
						    	
						} else {
							// seach among know special entities
							boolean found = false;
							for (int i=0; i<SPECIAL_ENTITIES.length; i++) {
								if (ss.substring(pos).
										startsWith(SPECIAL_ENTITIES[i])) {
									sb.append(ENTITY_CHARS[i]);
									pos = pos + SPECIAL_ENTITIES[i].length();
									found = true;
									break;
								}
							}
							if (!found) {
								// unknown special char - just keep it as-is
								sb.append(c);
								pos++;
							}
					
						}
						
					} else {
						// a "normal" char - keep it as is
						sb.append(c);
						pos++;
					}
				}
				// end of line
				sb.append('\n');
			}

		} catch (Exception e) {
			ColumbaLogger.log.error("Error restoring special characters", e);
			return null; // error
		}

		return sb.toString();
	}

	/**
	 * Strips html tags. and replaces special entities with their
	 * "normal" counter parts, e.g. <code>&gt; => ></code>.<br>
	 * Calling this method is the same as calling first stripHtmlTags
	 * and then restoreSpecialCharacters.
	 * 
	 * @param	html	input string
	 * @return	output without html tags and special entities
	 * 			(null on error)
	 * @author	karlpeder, 20030623
	 * 			(moved from org.columba.mail.parser.text.BodyTextParser)
	 */
	public static String htmlToText(String html) {
		// stripHtmlTags called with true ~ p & br => newlines
		String text = stripHtmlTags(html);
		return restoreSpecialCharacters(text);
	}

	/**
	 * Replaces special chars - <,>,&,\t,\n," - with the special
	 * entities used in html (amp, nbsp, ...). Then the complete
	 * text is surrounded with proper html tags: Starting- and
	 * ending html tag, header section and body section.
	 * The complete body section is sorround with p tags.
	 * <br>
	 * This is the same as first calling substituteSpecialCharacters
	 * and then add starting and ending html tags etc.
	 * <br>
	 * Further more urls and email adresses are converted into links
	 * Optionally a title and css definition is inserted in the 
	 * html header.
	 * <br>
	 * 
	 * TODO: Add support for smilies and coloring of quoted text
	 * 
	 * @param	text	Text to convert to html
	 * @param	title	Title to include in header, not used if null
	 * @param	css		Style sheet def. to include in header, 
	 * 					not used if null.
	 * 					The input shall not include the style tag
	 * @return	Text converted to html
	 * @author	Karl Peder Olesen (karlpeder), 20030916
	 */
	public static String textToHtml(String text, String title, String css) {

		// convert special characters
		String html = HtmlParser.substituteSpecialCharacters(text);

		// parse for urls / email adresses and substite with HTML-code
		html = HtmlParser.substituteURL(html);
		html = HtmlParser.substituteEmailAddress(html);

		// insert surrounding html tags
		StringBuffer buf = new StringBuffer();
		buf.append("<html><head>");
		if (title != null) {
			buf.append("<title>");
			buf.append(title);
			buf.append("</title>");
		}
		if (css != null) {
			buf.append("<style type=\"text/css\"><!-- ");
			buf.append(css);
			buf.append(" --></style>");
		}
		buf.append("</head><body><p>");
		buf.append(html);
		buf.append("</p></body></html>");

		return buf.toString();
	}

	/**	
	 * Substitute special characters like:
	 * <,>,&,\t,\n,"
	 * with special entities used in html (amp, nbsp, ...)
	 * 
	 * @param	s	input string containing special characters
	 * @return	output with special characters substituted
	 * 			(null on error)
	 */
	public static String substituteSpecialCharacters(String s) {

		StringBuffer sb = new StringBuffer(s.length());
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String ss = null;

		// TODO: Extend handling of special entities as in restoreSpecialCharacters

		/*
		 * *20030618, karlpeder* Changed the way multiple spaces are 
		 * replaced with &nbsp; to give better word wrap
		 * *20030623, karlpeder* Added " => &quot;
		 */

		try {

			while ((ss = br.readLine()) != null) {
				int i = 0;
				while (i < ss.length()) {
					switch (ss.charAt(i)) {
						case '<' :
							sb.append("&lt;");
							i++;
							break;
						case '>' :
							sb.append("&gt;");
							i++;
							break;
						case '&' :
							sb.append("&amp;");
							i++;
							break;
						case '"' :
							sb.append("&quot;");
							i++;
							break;
						case ' ' :
							//sb.append("&nbsp;");
							if (ss.substring(i).startsWith("    ")) {
								sb.append("&nbsp; ");
								i = i + 2;
							} else if (ss.substring(i).startsWith("   ")) {
								sb.append("&nbsp;&nbsp; ");
								i = i + 3;
							} else if (ss.substring(i).startsWith("  ")) {
								sb.append("&nbsp; ");
								i = i + 2;
							} else {
								sb.append(' ');
								i++;
							}
							break;
						case '\t' :
							sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
							i++;
							break;
						case '\n' :
							sb.append("<br>");
							i++;
							break;
						default :
							sb.append(ss.charAt(i));
							i++;
							break;
					}
				}
				sb.append("<br>\n");
			}

		} catch (Exception e) {
			ColumbaLogger.log.error("Error substituting special characters", e);
			return null; // error
		}

		return sb.toString();

	}

	/**	
	 * 
	 * substitute special characters like:
	 * <,>,&,\t,\n
	 * with special entities used in html<br>
	 * This is the same as substituteSpecialCharacters, but
	 * here an extra newline character is not inserted.
	 * 
	 * @param	s	input string containing special characters
	 * @return	output with special characters substituted
	 * 			(null on error)
	 */
	public static String substituteSpecialCharactersInHeaderfields(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String ss = null;

		// TODO: Extend handling of special entities as in restoreSpecialCharacters

		/*
		 * *20030623, karlpeder* " and space handled also
		 */

		try {

			while ((ss = br.readLine()) != null) {
				int i = 0;
				while (i < ss.length()) {
					switch (ss.charAt(i)) {
						case '<' :
							sb.append("&lt;");
							i++;
							break;
						case '>' :
							sb.append("&gt;");
							i++;
							break;
						case '&' :
							sb.append("&amp;");
							i++;
							break;
						case '"' :
							sb.append("&quot;");
							i++;
							break;
						case ' ' :
							if (ss.substring(i).startsWith("    ")) {
								sb.append("&nbsp; ");
								i = i + 2;
							} else if (ss.substring(i).startsWith("   ")) {
								sb.append("&nbsp;&nbsp; ");
								i = i + 3;
							} else if (ss.substring(i).startsWith("  ")) {
								sb.append("&nbsp; ");
								i = i + 2;
							} else {
								sb.append(' ');
								i++;
							}
							break;
						case '\t' :
							sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
							i++;
							break;
						case '\n' :
							sb.append("<br>");
							i++;
							break;
						default :
							sb.append(ss.charAt(i));
							i++;
							break;
					}
				}

			}

		} catch (Exception e) {
			ColumbaLogger.log.error("Error substituting special characters", e);
			return null; // error
		}

		return sb.toString();

	}

	/**
	 * Tries to fix broken html-strings by inserting 
	 * html start- and end tags if missing, and by
	 * removing content after the html end tag.
	 * 
	 * @param	input	html content to be validated
	 * @return	content with extra tags inserted if necessary
	 */
	public static String validateHTMLString(String input) {
		StringBuffer output = new StringBuffer(input);
		int index = 0;

		String lowerCaseInput = input.toLowerCase();

		// Check for missing  <html> tag
		if (lowerCaseInput.indexOf("<html>") == -1) {
			if (lowerCaseInput.indexOf("<!doctype") != -1)
				index =
					lowerCaseInput.indexOf(
						"\n",
						lowerCaseInput.indexOf("<!doctype"))
						+ 1;
			output.insert(index, "<html>");
		}

		// Check for missing  </html> tag
		if (lowerCaseInput.indexOf("</html>") == -1) {
			output.append("</html>");
		}

		// remove characters after </html> tag
		index = lowerCaseInput.indexOf("</html>");
		if (lowerCaseInput.length() >= index + 7) {
			lowerCaseInput = lowerCaseInput.substring(0, index + 7);
		}

		return output.toString();
	}

	/**
	 * parse text and transform every email-address
	 * in a HTML-conform address
	 * 
	 * @param	s	input text
	 * @return	text with email-adresses transformed to links
	 * 			(null on error)
	 */
	public static String substituteEmailAddress(String s) {

		return emailPattern.matcher(s).replaceAll("<A HREF=mailto:$1>$1</A>");
	}

	/**
	 * parse text and transform every url
	 * in a HTML-conform url
	 * 
	 * @param	s	input text
	 * @return	text with urls transformed to links
	 * 			(null on error)
	 */
	public static String substituteURL(String s) {
		/*
		PatternMatcher urlMatcher = new Perl5Matcher();
		PatternCompiler urlCompiler = new Perl5Compiler();
		Pattern urlPattern;

		String urls = "(http|https|ftp)";
		String letters = "\\w";
		String gunk = "/#~:;.?+=&@!\\-%";
		String punc = ".:?\\-";
		String any = "${" + letters + "}${" + gunk + "}${" + punc + "}";
*/
		/*
		String pattern =
			"\\b"
				+ "("
				+ urls
				+ ":["
				+ any
				+ "]+?)(?=["
				+ punc
				+ "]*[^"
				+ any
				+ "]|$)";

		try {
			urlCompiler = new Perl5Compiler();
			urlPattern =
				urlCompiler.compile(
					pattern,
					Perl5Compiler.CASE_INSENSITIVE_MASK);

			urlMatcher = new Perl5Matcher();

			String result =
				Util.substitute(
					urlMatcher,
					urlPattern,
					new Perl5Substitution("<A HREF=$1>$1</A>"),
					s,
					Util.SUBSTITUTE_ALL);

			return result;
		} catch (MalformedPatternException e) {
			ColumbaLogger.log.error("Error transforming urls to links", e);
			return null; // error
		}
		*/
		return urlPattern.matcher(s).replaceAll("<A HREF=$1>$1</A>");
	}

}
