/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package bsh;

public interface ParserConstants {

  int EOF = 0;
  int NONPRINTABLE = 6;
  int SINGLE_LINE_COMMENT = 7;
  int HASH_BANG_COMMENT = 8;
  int MULTI_LINE_COMMENT = 9;
  int BOOLEAN = 10;
  int BREAK = 11;
  int CLASS = 12;
  int BYTE = 13;
  int CASE = 14;
  int CATCH = 15;
  int CHAR = 16;
  int CONST = 17;
  int CONTINUE = 18;
  int _DEFAULT = 19;
  int DO = 20;
  int DOUBLE = 21;
  int ELSE = 22;
  int FALSE = 23;
  int FINAL = 24;
  int FINALLY = 25;
  int FLOAT = 26;
  int FOR = 27;
  int GOTO = 28;
  int IF = 29;
  int IMPORT = 30;
  int INSTANCEOF = 31;
  int INT = 32;
  int INTERFACE = 33;
  int LONG = 34;
  int NEW = 35;
  int NULL = 36;
  int PRIVATE = 37;
  int PROTECTED = 38;
  int PUBLIC = 39;
  int RETURN = 40;
  int SHORT = 41;
  int STATIC = 42;
  int SWITCH = 43;
  int THROW = 44;
  int TRUE = 45;
  int TRY = 46;
  int VOID = 47;
  int WHILE = 48;
  int INTEGER_LITERAL = 49;
  int DECIMAL_LITERAL = 50;
  int HEX_LITERAL = 51;
  int OCTAL_LITERAL = 52;
  int FLOATING_POINT_LITERAL = 53;
  int EXPONENT = 54;
  int CHARACTER_LITERAL = 55;
  int STRING_LITERAL = 56;
  int FORMAL_COMMENT = 57;
  int IDENTIFIER = 58;
  int LETTER = 59;
  int DIGIT = 60;
  int LPAREN = 61;
  int RPAREN = 62;
  int LBRACE = 63;
  int RBRACE = 64;
  int LBRACKET = 65;
  int RBRACKET = 66;
  int SEMICOLON = 67;
  int COMMA = 68;
  int DOT = 69;
  int ASSIGN = 70;
  int GT = 71;
  int GTX = 72;
  int LT = 73;
  int LTX = 74;
  int BANG = 75;
  int TILDE = 76;
  int HOOK = 77;
  int COLON = 78;
  int EQ = 79;
  int LE = 80;
  int LEX = 81;
  int GE = 82;
  int GEX = 83;
  int NE = 84;
  int BOOL_OR = 85;
  int BOOL_ORX = 86;
  int BOOL_AND = 87;
  int BOOL_ANDX = 88;
  int INCR = 89;
  int DECR = 90;
  int PLUS = 91;
  int MINUS = 92;
  int STAR = 93;
  int SLASH = 94;
  int BIT_AND = 95;
  int BIT_ANDX = 96;
  int BIT_OR = 97;
  int BIT_ORX = 98;
  int XOR = 99;
  int MOD = 100;
  int LSHIFT = 101;
  int LSHIFTX = 102;
  int RSIGNEDSHIFT = 103;
  int RSIGNEDSHIFTX = 104;
  int RUNSIGNEDSHIFT = 105;
  int RUNSIGNEDSHIFTX = 106;
  int PLUSASSIGN = 107;
  int MINUSASSIGN = 108;
  int STARASSIGN = 109;
  int SLASHASSIGN = 110;
  int ANDASSIGN = 111;
  int ANDASSIGNX = 112;
  int ORASSIGN = 113;
  int ORASSIGNX = 114;
  int XORASSIGN = 115;
  int MODASSIGN = 116;
  int LSHIFTASSIGN = 117;
  int LSHIFTASSIGNX = 118;
  int RSIGNEDSHIFTASSIGN = 119;
  int RSIGNEDSHIFTASSIGNX = 120;
  int RUNSIGNEDSHIFTASSIGN = 121;
  int RUNSIGNEDSHIFTASSIGNX = 122;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\f\"",
    "\"\\n\"",
    "<NONPRINTABLE>",
    "<SINGLE_LINE_COMMENT>",
    "<HASH_BANG_COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\"boolean\"",
    "\"break\"",
    "\"class\"",
    "\"byte\"",
    "\"case\"",
    "\"catch\"",
    "\"char\"",
    "\"const\"",
    "\"continue\"",
    "\"default\"",
    "\"do\"",
    "\"double\"",
    "\"else\"",
    "\"false\"",
    "\"final\"",
    "\"finally\"",
    "\"float\"",
    "\"for\"",
    "\"goto\"",
    "\"if\"",
    "\"import\"",
    "\"instanceof\"",
    "\"int\"",
    "\"interface\"",
    "\"long\"",
    "\"new\"",
    "\"null\"",
    "\"private\"",
    "\"protected\"",
    "\"public\"",
    "\"return\"",
    "\"short\"",
    "\"static\"",
    "\"switch\"",
    "\"throw\"",
    "\"true\"",
    "\"try\"",
    "\"void\"",
    "\"while\"",
    "<INTEGER_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<CHARACTER_LITERAL>",
    "<STRING_LITERAL>",
    "<FORMAL_COMMENT>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\">\"",
    "\"@gt\"",
    "\"<\"",
    "\"@lt\"",
    "\"!\"",
    "\"~\"",
    "\"?\"",
    "\":\"",
    "\"==\"",
    "\"<=\"",
    "\"@lteq\"",
    "\">=\"",
    "\"@gteq\"",
    "\"!=\"",
    "\"||\"",
    "\"@or\"",
    "\"&&\"",
    "\"@and\"",
    "\"++\"",
    "\"--\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"&\"",
    "\"@bitwise_and\"",
    "\"|\"",
    "\"@bitwise_or\"",
    "\"^\"",
    "\"%\"",
    "\"<<\"",
    "\"@left_shift\"",
    "\">>\"",
    "\"@right_shift\"",
    "\">>>\"",
    "\"@right_unsigned_shift\"",
    "\"+=\"",
    "\"-=\"",
    "\"*=\"",
    "\"/=\"",
    "\"&=\"",
    "\"@and_assign\"",
    "\"|=\"",
    "\"@or_assign\"",
    "\"^=\"",
    "\"%=\"",
    "\"<<=\"",
    "\"@left_shift_assign\"",
    "\">>=\"",
    "\"@right_shift_assign\"",
    "\">>>=\"",
    "\"@right_unsigned_shift_assign\"",
  };

}
