// Copyright (c) 1996-2001 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

/** This class was generated by reflection against
 *  sun.tools.java.Constants, part of
 *  the standard JDK 1.2 and 1.3.
 *
 *  @author org.argouml.util.ConstantsRE
 *  @author Thierry Lach
 *  @since ARGO0.9.4
 */

package org.argouml.language.java;
public final class JavaConstants {
    public final static int F_VERBOSE = 1;
    public final static int F_DUMP = 2;
    public final static int F_WARNINGS = 4;
    public final static int F_DEBUG_LINES = 4096;
    public final static int F_DEBUG_VARS = 8192;
    public final static int F_DEBUG_SOURCE = 262144;
    public final static int F_OPT = 16384;
    public final static int F_OPT_INTERCLASS = 32768;
    public final static int F_DEPENDENCIES = 32;
    public final static int F_COVERAGE = 64;
    public final static int F_COVDATA = 128;
    public final static int F_DEPRECATION = 512;
    public final static int F_PRINT_DEPENDENCIES = 1024;
    public final static int F_VERSION12 = 2048;
    public final static int F_ERRORSREPORTED = 65536;
    public final static int F_STRICTDEFAULT = 131072;
    public final static int M_PUBLIC = 1;
    public final static int M_PRIVATE = 2;
    public final static int M_PROTECTED = 4;
    public final static int M_STATIC = 8;
    public final static int M_TRANSIENT = 128;
    public final static int M_SYNCHRONIZED = 32;
    public final static int M_ABSTRACT = 1024;
    public final static int M_NATIVE = 256;
    public final static int M_FINAL = 16;
    public final static int M_VOLATILE = 64;
    public final static int M_INTERFACE = 512;
    public final static int M_ANONYMOUS = 65536;
    public final static int M_LOCAL = 131072;
    public final static int M_DEPRECATED = 262144;
    public final static int M_SYNTHETIC = 524288;
    public final static int M_INLINEABLE = 1048576;
    public final static int M_STRICTFP = 2097152;
    public final static int MM_CLASS = 2098705;
    public final static int MM_MEMBER = 31;
    public final static int MM_FIELD = 223;
    public final static int MM_METHOD = 2098495;
    public final static int ACCM_CLASS = 3633;
    public final static int ACCM_MEMBER = 31;
    public final static int ACCM_INNERCLASS = 3615;
    public final static int ACCM_FIELD = 223;
    public final static int ACCM_METHOD = 3391;
    public final static int TC_BOOLEAN = 0;
    public final static int TC_BYTE = 1;
    public final static int TC_CHAR = 2;
    public final static int TC_SHORT = 3;
    public final static int TC_INT = 4;
    public final static int TC_LONG = 5;
    public final static int TC_FLOAT = 6;
    public final static int TC_DOUBLE = 7;
    public final static int TC_NULL = 8;
    public final static int TC_ARRAY = 9;
    public final static int TC_CLASS = 10;
    public final static int TC_VOID = 11;
    public final static int TC_METHOD = 12;
    public final static int TC_ERROR = 13;
    public final static int CT_FIRST_KIND = 1;
    public final static int CT_METHOD = 1;
    public final static int CT_FIKT_METHOD = 2;
    public final static int CT_BLOCK = 3;
    public final static int CT_FIKT_RET = 4;
    public final static int CT_CASE = 5;
    public final static int CT_SWITH_WO_DEF = 6;
    public final static int CT_BRANCH_TRUE = 7;
    public final static int CT_BRANCH_FALSE = 8;
    public final static int CT_LAST_KIND = 8;
    public final static int TM_NULL = 256;
    public final static int TM_VOID = 2048;
    public final static int TM_BOOLEAN = 1;
    public final static int TM_BYTE = 2;
    public final static int TM_CHAR = 4;
    public final static int TM_SHORT = 8;
    public final static int TM_INT = 16;
    public final static int TM_LONG = 32;
    public final static int TM_FLOAT = 64;
    public final static int TM_DOUBLE = 128;
    public final static int TM_ARRAY = 512;
    public final static int TM_CLASS = 1024;
    public final static int TM_METHOD = 4096;
    public final static int TM_ERROR = 8192;
    public final static int TM_INT32 = 30;
    public final static int TM_NUM32 = 94;
    public final static int TM_NUM64 = 160;
    public final static int TM_INTEGER = 62;
    public final static int TM_REAL = 192;
    public final static int TM_NUMBER = 254;
    public final static int TM_REFERENCE = 1792;
    public final static int CS_UNDEFINED = 0;
    public final static int CS_UNDECIDED = 1;
    public final static int CS_BINARY = 2;
    public final static int CS_SOURCE = 3;
    public final static int CS_PARSED = 4;
    public final static int CS_CHECKED = 5;
    public final static int CS_COMPILED = 6;
    public final static int CS_NOTFOUND = 7;
    public final static int ATT_ALL = -1;
    public final static int ATT_CODE = 2;
    public final static int ATT_ALLCLASSES = 4;
    public final static int WHEREOFFSETBITS = 32;
    public final static int COMMA = 0;
    public final static int ASSIGN = 1;
    public final static int ASGMUL = 2;
    public final static int ASGDIV = 3;
    public final static int ASGREM = 4;
    public final static int ASGADD = 5;
    public final static int ASGSUB = 6;
    public final static int ASGLSHIFT = 7;
    public final static int ASGRSHIFT = 8;
    public final static int ASGURSHIFT = 9;
    public final static int ASGBITAND = 10;
    public final static int ASGBITOR = 11;
    public final static int ASGBITXOR = 12;
    public final static int COND = 13;
    public final static int OR = 14;
    public final static int AND = 15;
    public final static int BITOR = 16;
    public final static int BITXOR = 17;
    public final static int BITAND = 18;
    public final static int NE = 19;
    public final static int EQ = 20;
    public final static int GE = 21;
    public final static int GT = 22;
    public final static int LE = 23;
    public final static int LT = 24;
    public final static int INSTANCEOF = 25;
    public final static int LSHIFT = 26;
    public final static int RSHIFT = 27;
    public final static int URSHIFT = 28;
    public final static int ADD = 29;
    public final static int SUB = 30;
    public final static int DIV = 31;
    public final static int REM = 32;
    public final static int MUL = 33;
    public final static int CAST = 34;
    public final static int POS = 35;
    public final static int NEG = 36;
    public final static int NOT = 37;
    public final static int BITNOT = 38;
    public final static int PREINC = 39;
    public final static int PREDEC = 40;
    public final static int NEWARRAY = 41;
    public final static int NEWINSTANCE = 42;
    public final static int NEWFROMNAME = 43;
    public final static int POSTINC = 44;
    public final static int POSTDEC = 45;
    public final static int FIELD = 46;
    public final static int METHOD = 47;
    public final static int ARRAYACCESS = 48;
    public final static int NEW = 49;
    public final static int INC = 50;
    public final static int DEC = 51;
    public final static int CONVERT = 55;
    public final static int EXPR = 56;
    public final static int ARRAY = 57;
    public final static int GOTO = 58;
    public final static int IDENT = 60;
    public final static int BOOLEANVAL = 61;
    public final static int BYTEVAL = 62;
    public final static int CHARVAL = 63;
    public final static int SHORTVAL = 64;
    public final static int INTVAL = 65;
    public final static int LONGVAL = 66;
    public final static int FLOATVAL = 67;
    public final static int DOUBLEVAL = 68;
    public final static int STRINGVAL = 69;
    public final static int BYTE = 70;
    public final static int CHAR = 71;
    public final static int SHORT = 72;
    public final static int INT = 73;
    public final static int LONG = 74;
    public final static int FLOAT = 75;
    public final static int DOUBLE = 76;
    public final static int VOID = 77;
    public final static int BOOLEAN = 78;
    public final static int TRUE = 80;
    public final static int FALSE = 81;
    public final static int THIS = 82;
    public final static int SUPER = 83;
    public final static int NULL = 84;
    public final static int IF = 90;
    public final static int ELSE = 91;
    public final static int FOR = 92;
    public final static int WHILE = 93;
    public final static int DO = 94;
    public final static int SWITCH = 95;
    public final static int CASE = 96;
    public final static int DEFAULT = 97;
    public final static int BREAK = 98;
    public final static int CONTINUE = 99;
    public final static int RETURN = 100;
    public final static int TRY = 101;
    public final static int CATCH = 102;
    public final static int FINALLY = 103;
    public final static int THROW = 104;
    public final static int STAT = 105;
    public final static int EXPRESSION = 106;
    public final static int DECLARATION = 107;
    public final static int VARDECLARATION = 108;
    public final static int IMPORT = 110;
    public final static int CLASS = 111;
    public final static int EXTENDS = 112;
    public final static int IMPLEMENTS = 113;
    public final static int INTERFACE = 114;
    public final static int PACKAGE = 115;
    public final static int PRIVATE = 120;
    public final static int PUBLIC = 121;
    public final static int PROTECTED = 122;
    public final static int CONST = 123;
    public final static int STATIC = 124;
    public final static int TRANSIENT = 125;
    public final static int SYNCHRONIZED = 126;
    public final static int NATIVE = 127;
    public final static int FINAL = 128;
    public final static int VOLATILE = 129;
    public final static int ABSTRACT = 130;
    public final static int STRICTFP = 131;
    public final static int SEMICOLON = 135;
    public final static int COLON = 136;
    public final static int QUESTIONMARK = 137;
    public final static int LBRACE = 138;
    public final static int RBRACE = 139;
    public final static int LPAREN = 140;
    public final static int RPAREN = 141;
    public final static int LSQBRACKET = 142;
    public final static int RSQBRACKET = 143;
    public final static int THROWS = 144;
    public final static int ERROR = 145;
    public final static int COMMENT = 146;
    public final static int TYPE = 147;
    public final static int LENGTH = 148;
    public final static int INLINERETURN = 149;
    public final static int INLINEMETHOD = 150;
    public final static int INLINENEWINSTANCE = 151;
     public static final String[] opNames = {
            ","
         ,   "="
         ,   "*="
         ,   "/="
         ,   "%="
         ,   "+="
         ,   "-="
         ,   "<<="
         ,   ">>="
         ,   ">>>="
         ,   "&="
         ,   "|="
         ,   "^="
         ,   "?:"
         ,   "||"
         ,   "&&"
         ,   "|"
         ,   "^"
         ,   "&"
         ,   "!="
         ,   "=="
         ,   ">="
         ,   ">"
         ,   "<="
         ,   "<"
         ,   "instanceof"
         ,   "<<"
         ,   ">>"
         ,   ">>>"
         ,   "+"
         ,   "-"
         ,   "/"
         ,   "%"
         ,   "*"
         ,   "cast"
         ,   "+"
         ,   "-"
         ,   "!"
         ,   "~"
         ,   "++"
         ,   "--"
         ,   "new"
         ,   "new"
         ,   "new"
         ,   "++"
         ,   "--"
         ,   "field"
         ,   "method"
         ,   "[]"
         ,   "new"
         ,   "++"
         ,   "--"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "convert"
         ,   "expr"
         ,   "array"
         ,   "goto"
         ,   "null"
         ,   "Identifier"
         ,   "boolean"
         ,   "byte"
         ,   "char"
         ,   "short"
         ,   "int"
         ,   "long"
         ,   "float"
         ,   "double"
         ,   "string"
         ,   "byte"
         ,   "char"
         ,   "short"
         ,   "int"
         ,   "long"
         ,   "float"
         ,   "double"
         ,   "void"
         ,   "boolean"
         ,   "null"
         ,   "true"
         ,   "false"
         ,   "this"
         ,   "super"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "if"
         ,   "else"
         ,   "for"
         ,   "while"
         ,   "do"
         ,   "switch"
         ,   "case"
         ,   "default"
         ,   "break"
         ,   "continue"
         ,   "return"
         ,   "try"
         ,   "catch"
         ,   "finally"
         ,   "throw"
         ,   "stat"
         ,   "expression"
         ,   "declaration"
         ,   "declaration"
         ,   "null"
         ,   "import"
         ,   "class"
         ,   "extends"
         ,   "implements"
         ,   "interface"
         ,   "package"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   "private"
         ,   "public"
         ,   "protected"
         ,   "const"
         ,   "static"
         ,   "transient"
         ,   "synchronized"
         ,   "native"
         ,   "final"
         ,   "volatile"
         ,   "abstract"
         ,   "strictfp"
         ,   "null"
         ,   "null"
         ,   "null"
         ,   ";"
         ,   ":"
         ,   "?"
         ,   "{"
         ,   "}"
         ,   "("
         ,   ")"
         ,   "["
         ,   "]"
         ,   "throws"
         ,   "error"
         ,   "comment"
         ,   "type"
         ,   "length"
         ,   "inline-return"
         ,   "inline-method"
         ,   "inline-new"
     };
}
