/*
 * TokenMarker.java - Tokenizes lines of text
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998, 2002 Slava Pestov
 * Copyright (C) 1999, 2000 mike dillon
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit.syntax;

//{{{ Imports
import gnu.regexp.RE;
import javax.swing.text.Segment;
import java.util.*;
import org.gjt.sp.jedit.search.CharIndexedSegment;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * A token marker splits lines of text into tokens. Each token carries
 * a length field and an identification tag that can be mapped to a color
 * or font style for painting that token.
 *
 * @author Slava Pestov, mike dillon
 * @version $Id: TokenMarker.java,v 1.31 2002/05/30 10:17:45 spestov Exp $
 *
 * @see org.gjt.sp.jedit.syntax.Token
 * @see org.gjt.sp.jedit.syntax.TokenHandler
 */
public class TokenMarker
{
	//{{{ TokenMarker constructor
	public TokenMarker()
	{
		ruleSets = new Hashtable(64);
	} //}}}

	//{{{ getName() method
	public String getName()
	{
		return name;
	} //}}}

	//{{{ setName() method
	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException();

		this.name = name;
		rulePfx = name.concat("::");
	} //}}}

	//{{{ addRuleSet() method
	public void addRuleSet(String setName, ParserRuleSet rules)
	{
		if (rules == null)
			return;

		if (setName == null)
			setName = "MAIN";

		ruleSets.put(rulePfx.concat(setName), rules);

		if (setName.equals("MAIN"))
			mainRuleSet = rules;
	} //}}}

	//{{{ getMainRuleSet() method
	public ParserRuleSet getMainRuleSet()
	{
		return mainRuleSet;
	} //}}}

	//{{{ getRuleSet() method
	public ParserRuleSet getRuleSet(String setName)
	{
		ParserRuleSet rules;

		rules = (ParserRuleSet) ruleSets.get(setName);

		if (rules == null && !setName.startsWith(rulePfx))
		{
			int delim = setName.indexOf("::");
			if(delim == -1)
			{
				byte id = Token.stringToToken(setName);
				rules = ParserRuleSet.getStandardRuleSet(id);
			}
			else
			{
				String modeName = setName.substring(0, delim);

				Mode mode = jEdit.getMode(modeName);
				if(mode == null)
				{
					Log.log(Log.ERROR,TokenMarker.class,
						"Unknown edit mode: " + modeName);
					rules = null;
				}
				else
				{
					TokenMarker marker = mode.getTokenMarker();
					rules = marker.getRuleSet(setName);
				}
			}

			// store external ParserRuleSet in the local hashtable
			// for faster lookups later
			ruleSets.put(setName, rules);
		}

		if (rules == null)
		{
			Log.log(Log.ERROR,this,"Unresolved delegate target: " + setName);
			return ParserRuleSet.getStandardRuleSet(Token.INVALID);
		}
		else
			return rules;
	} //}}}

	//{{{ markTokens() method
	/**
	 * Do not call this method directly; call Buffer.markTokens() instead.
	 */
	public LineContext markTokens(LineContext prevContext,
		TokenHandler tokenHandler, Segment line,
		String noWordSep)
	{
		//{{{ Set up some instance variables
		// this is to avoid having to pass around lots and lots of
		// parameters.
		this.tokenHandler = tokenHandler;
		this.line = line;

		lastOffset = line.offset;
		lineLength = line.count + line.offset;

		context = new LineContext();

		if(prevContext == null)
			context.rules = getMainRuleSet();
		else
		{
			context.parent = prevContext.parent;
			context.inRule = prevContext.inRule;
			context.rules = prevContext.rules;
		}

		keywords = context.rules.getKeywords();
		escaped = false;
		//}}}

		//{{{ Main parser loop
		ParserRule rule;
		int terminateChar = context.rules.getTerminateChar();
		boolean terminated = false;

main_loop:	for(pos = line.offset; pos < lineLength; pos++)
		{
			//{{{ check if we have to stop parsing
			if(terminateChar >= 0 && pos >= terminateChar
				&& !terminated)
			{
				terminated = true;
				context = new LineContext(ParserRuleSet
					.getStandardRuleSet(context.rules
					.getDefault()),context);
				keywords = context.rules.getKeywords();
			} //}}}

			//{{{ check for end of delegate
			if(context.parent != null)
			{
				rule = context.parent.inRule;
				if(rule != null && rule.end != null)
				{
					if(checkDelegateEnd())
						continue main_loop;
				}
			} //}}}

			//{{{ check every rule
			char ch = line.array[pos];

			rule = context.rules.getRules(ch);
			while(rule != null)
			{
				// stop checking rules if there was a match
				if (handleRule(rule,false))
					continue main_loop;

				rule = rule.next;
			} //}}}

			//{{{ check if current character is a word separator
			if(Character.isWhitespace(ch))
			{
				handleSoftSpan();

				if(keywords != null)
					markKeyword(false);

				if(lastOffset != pos)
				{
					tokenHandler.handleToken(
						context.rules.getDefault(),
						lastOffset - line.offset,
						pos - lastOffset,
						context);
				}

				tokenHandler.handleToken(
					(ch == '\t' ? Token.TAB
					: Token.WHITESPACE),pos - line.offset,1,
					context);
				lastOffset = pos + 1;
			}
			else
			{
				String noWordSep2 = (keywords == null
					? "" : keywords.getNonAlphaNumericChars());

				if(!Character.isLetterOrDigit(ch)
					&& noWordSep.indexOf(ch) == -1
					&& noWordSep2.indexOf(ch) == -1)
				{
					handleSoftSpan();

					markKeyword(true);
	
					tokenHandler.handleToken(
						context.rules.getDefault(),
						lastOffset - line.offset,1,
						context);
					lastOffset = pos + 1;
				}
			} //}}}

			escaped = false;
		} //}}}

		//{{{ Mark all remaining characters
		pos = lineLength;

		handleSoftSpan();
		markKeyword(true);

		if(context.parent != null)
		{
			rule = context.parent.inRule;
			if((rule != null && (context.parent.inRule.action
				& ParserRule.NO_LINE_BREAK) == ParserRule.NO_LINE_BREAK)
				|| terminated)
			{
				context = context.parent;
				keywords = context.rules.getKeywords();
				context.inRule = null;
			}
		} //}}}

		tokenHandler.handleToken(Token.END,pos - line.offset,0,context);

		return context.intern();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private Hashtable ruleSets;
	private String name;
	private String rulePfx;
	private ParserRuleSet mainRuleSet;

	// Instead of passing these around to each method, we just store them
	// as instance variables. Note that this is not thread-safe.
	private TokenHandler tokenHandler;
	private Segment line;
	private LineContext context;
	private KeywordMap keywords;
	private Segment pattern = new Segment();
	private int lastOffset;
	private int lineLength;
	private int pos;
	private boolean escaped;
	//}}}

	//{{{ checkDelegateEnd() method
	private boolean checkDelegateEnd()
	{
		LineContext tempContext = context;
		context = context.parent;
		keywords = context.rules.getKeywords();
		boolean tempEscaped = escaped;
		boolean b = handleRule(context.inRule,true);
		context = tempContext;
		keywords = context.rules.getKeywords();

		if(b && !tempEscaped)
		{
			markKeyword(true);

			tokenHandler.handleToken(
				(context.parent.inRule.action & ParserRule.EXCLUDE_MATCH)
				== ParserRule.EXCLUDE_MATCH
				? context.parent.rules.getDefault()
				: context.parent.inRule.token,
				pos - line.offset,pattern.count,context);

			context = (LineContext)context.parent.clone();
			keywords = context.rules.getKeywords();
			context.inRule = null;
			lastOffset = pos + pattern.count;

			// move pos to last character of match sequence
			pos += (pattern.count - 1);

			return true;
		}

		// check escape rule of parent
		ParserRule rule = context.parent.rules.getEscapeRule();
		if(rule != null && handleRule(rule,false))
			return true;

		return false;
	} //}}}

	//{{{ handleRule() method
	/**
	 * Checks if the rule matches the line at the current position
	 * and handles the rule if it does match
	 */
	private boolean handleRule(ParserRule checkRule, boolean end)
	{
		pattern.array = (end ? checkRule.end : checkRule.start);
		pattern.offset = 0;
		pattern.count = pattern.array.length;

		if(!TextUtilities.regionMatches(context.rules.getIgnoreCase(),
			line,pos,pattern.array))
		{
			return false;
		}

		//{{{ Check for an escape sequence
		if ((checkRule.action & ParserRule.IS_ESCAPE) == ParserRule.IS_ESCAPE)
		{
			escaped = !escaped;
			pos += pattern.count - 1;
		}
		else if (escaped)
		{
			escaped = false;
			pos += pattern.count - 1;
		} //}}}
		//{{{ Not inside a rule
		else if (context.inRule == null)
		{
			if((checkRule.action & ParserRule.AT_LINE_START)
				== ParserRule.AT_LINE_START)
			{
				if((((checkRule.action & ParserRule.MARK_PREVIOUS) != 0) ?
					lastOffset : pos) != line.offset)
				{
					return false;
				}
			}

			markKeyword((checkRule.action & ParserRule.MARK_PREVIOUS)
				!= ParserRule.MARK_PREVIOUS);

			switch(checkRule.action & ParserRule.MAJOR_ACTIONS)
			{
			//{{{ SEQ
			case ParserRule.SEQ:
				tokenHandler.handleToken(checkRule.token,
					pos - line.offset,pattern.count,context);

				// a DELEGATE attribute on a SEQ changes the
				// ruleset from the end of the SEQ onwards
				ParserRuleSet delegateSet = checkRule.getDelegateRuleSet(this);
				if(delegateSet != null)
				{
					context = new LineContext(delegateSet,
						context.parent);
					keywords = context.rules.getKeywords();
				}
				break;
			//}}}
			//{{{ SPAN, EOL_SPAN, MARK_FOLLOWING
			case ParserRule.SPAN:
			case ParserRule.EOL_SPAN:
			case ParserRule.MARK_FOLLOWING:
				context.inRule = checkRule;

				delegateSet = checkRule.getDelegateRuleSet(this);

				tokenHandler.handleToken(
					((checkRule.action & ParserRule.EXCLUDE_MATCH)
					== ParserRule.EXCLUDE_MATCH
					? context.rules.getDefault() : checkRule.token),
					pos - line.offset,pattern.count,context);

				context = new LineContext(delegateSet, context);
				keywords = context.rules.getKeywords();

				break;
			//}}}
			//{{{ MARK_PREVIOUS
			case ParserRule.MARK_PREVIOUS:
				if ((checkRule.action & ParserRule.EXCLUDE_MATCH)
					== ParserRule.EXCLUDE_MATCH)
				{
					if(pos != lastOffset)
					{
						tokenHandler.handleToken(
							checkRule.token,
							lastOffset - line.offset,
							pos - lastOffset,
							context);
					}

					tokenHandler.handleToken(
						context.rules.getDefault(),
						pos - line.offset,pattern.count,
						context);
				}
				else
				{
					tokenHandler.handleToken(checkRule.token,
						lastOffset - line.offset,
						pos - lastOffset + pattern.count,
						context);
				}

				break;
			//}}}
			default:
				throw new InternalError("Unhandled major action");
			}

			// move pos to last character of match sequence
			pos += (pattern.count - 1); 
			lastOffset = pos + 1;

			// break out of inner for loop to check next char
		} //}}}

		return true;
	} //}}}

	//{{{ handleSoftSpan() method
	private boolean handleSoftSpan()
	{
		if (context.parent != null)
		{
			ParserRule rule = context.parent.inRule;
			if(rule != null && (context.parent.inRule.action
				& (ParserRule.NO_WORD_BREAK
				| ParserRule.MARK_FOLLOWING)) != 0)
			{
				tokenHandler.handleToken(rule.token,
					lastOffset - line.offset,
					pos - lastOffset,context);

				lastOffset = pos;
				context = context.parent;
				keywords = context.rules.getKeywords();
				context.inRule = null;
				return true;
			}
		}

		return false;
	} //}}}

	//{{{ markKeyword() method
	private void markKeyword(boolean addRemaining)
	{
		int len = pos - lastOffset;
		if(len == 0)
			return;

		//{{{ Do digits
		if(context.rules.getHighlightDigits())
		{
			boolean digit = false;
			boolean mixed = false;

			for(int i = lastOffset; i < pos; i++)
			{
				char ch = line.array[i];
				if(Character.isDigit(ch))
					digit = true;
				else
					mixed = true;
			}

			if(mixed)
			{
				RE digitRE = context.rules.getDigitRegexp();

				// only match against regexp if its not all
				// digits; if all digits, no point matching
				if(digit)
				{ 
					if(digitRE == null)
					{
						// mixed digit/alpha keyword,
						// and no regexp... don't
						// highlight as DIGIT
						digit = false;
					}
					else
					{
						CharIndexedSegment seg = new CharIndexedSegment(
							line,false);
						int oldCount = line.count;
						int oldOffset = line.offset;
						line.offset = lastOffset;
						line.count = len;
						if(!digitRE.isMatch(seg))
							digit = false;
						line.offset = oldOffset;
						line.count = oldCount;
					}
				}
			}

			if(digit)
			{
				tokenHandler.handleToken(Token.DIGIT,
					lastOffset - line.offset,
					len,context);
				lastOffset = pos;

				return;
			}
		} //}}}

		//{{{ Do keywords
		if(keywords != null)
		{
			byte id = keywords.lookup(line, lastOffset, len);

			if(id != Token.NULL)
			{
				tokenHandler.handleToken(id,
					lastOffset - line.offset,
					len,context);
				lastOffset = pos;
				return;
			}
		} //}}}

		//{{{ Handle any remaining crud
		if(addRemaining)
		{
			tokenHandler.handleToken(context.rules.getDefault(),
				lastOffset - line.offset,len,context);
			lastOffset = pos;
		} //}}}
	} //}}}

	//}}}

	//{{{ LineContext class
	public static class LineContext
	{
		private static Hashtable intern = new Hashtable();

		public LineContext parent;
		public ParserRule inRule;
		public ParserRuleSet rules;

		//{{{ LineContext constructor
		public LineContext(ParserRule r, ParserRuleSet rs)
		{
			inRule = r;
			rules = rs;
		} //}}}

		//{{{ LineContext constructor
		public LineContext(ParserRuleSet rs, LineContext lc)
		{
			rules = rs;
			parent = (lc == null ? null : (LineContext)lc.clone());
		} //}}}

		//{{{ LineContext constructor
		public LineContext(ParserRule r)
		{
			inRule = r;
		} //}}}

		//{{{ LineContext constructor
		public LineContext()
		{
		} //}}}

		//{{{ intern() method
		public LineContext intern()
		{
			Object obj = intern.get(this);
			if(obj == null)
			{
				intern.put(this,this);
				return this;
			}
			else
				return (LineContext)obj;
		} //}}}

		//{{{ hashCode() method
		public int hashCode()
		{
			if(inRule != null)
				return inRule.hashCode();
			else if(rules != null)
				return rules.hashCode();
			else
				return 0;
		} //}}}

		//{{{ equals() method
		public boolean equals(Object obj)
		{
			if(obj instanceof LineContext)
			{
				LineContext lc = (LineContext)obj;
				if(lc.parent == null)
				{
					if(parent != null)
						return false;
				}
				else //if(lc.parent != null)
				{
					if(parent == null)
						return false;
					else if(!lc.parent.equals(parent))
						return false;
				}

				return lc.inRule == inRule && lc.rules == rules;
			}
			else
				return false;
		} //}}}

		//{{{ clone() method
		public Object clone()
		{
			LineContext lc = new LineContext();
			lc.inRule = inRule;
			lc.rules = rules;
			lc.parent = (parent == null) ? null : (LineContext) parent.clone();

			return lc;
		} //}}}
	} //}}}
}
