package com.ccompiler.analyzer.lexical;

import java_cup.runtime.*;

%%

%class LexicalAnalyzer
%public
%unicode
%line
%column
%cup


%{

  public static String curLine;

  /**
   * Factory method for creating Symbols for a given type.
   * @param type The type of this symbol
   * @return A symbol of a specific type
   */
  public Symbol symbol(int type) {
      curLine = "line :" + yyline;
      return new Symbol(type, yyline, yycolumn);
  }
  
  /**
   * Factory method for creating Symbols for a given type and its value.
   * @param type The type of this symbol
   * @param value The value of this symbol
   * @return A symbol of a specific type
   */
  public Symbol symbol(int type, Object value) {
      curLine = "line :" + yyline;
      return new Symbol(type, yyline, yycolumn, value);
  }
  
  /**
   * Reports an error occured in a given line.
   * @param line The bad line
   * @param msg Additional information about the error
   */
  private void reportError(int line, String msg) {
      throw new RuntimeException("Lexical error at line #" + line + ": " + msg);
  }

  public String current_lexeme(){
      int l = yyline+1;
      int c = yycolumn+1;
      return "line: " + l + ", column: " + c + ", with : '"+yytext()+"')";
  }

%}


/* Macros */
O =  [0-7]
D = [0-9]
NZ = [1-9]
L = [a-zA-Z_]
H = [a-fA-F0-9]
HP = (0[xX])
E = [Ee][+-]?{D}+
P =  ([Pp][+-]?{D}+)
FS = (f|F|l|L)
IS = (u|U|l|L)*
CP = (u|U|L)
SP = (u8|u|U|L)
ES = (\\([\'\"\?\\abfnrtv]|[0-7]{1,3}|x[a-fA-F0-9]+))
WS = [ \t\v\n\f]

Comment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

BlankSpace = {LineTerminator} | [ \t\f]

/* Comments */

Comments = {LineComment} | {BlockComment}
LineComment = "//" {InputCharacter}* {LineTerminator}?
BlockComment = "/*" [^*] ~"*/" | "/*" "*"+ "/" 

/* Identifier */
/* Identifier = [:jletter:][:jletterdigit:]* */

/*-*
 * Aqui definiremos os padr›es de defini�‹o:
 */
letter          = [A-Za-z]
L               = [a-zA-Z_]
digit           = [0-9]
alphanumeric    = {letter}|{digit}
other_id_char   = [_]
identifier      = {letter}({alphanumeric}|{other_id_char})*
/* integer         = {digit}* */


%%

<YYINITIAL> {

    /* Keywords */

    "_Static_assert"        { return symbol(sym.STATIC_ASSERT); }
    "default"               { return symbol(sym.DEFAULT); }
    "_Alignas"              { return symbol(sym.ALIGNAS); }
	"_Alignof"              { return symbol(sym.ALIGNOF); }
	"_Atomic"               { return symbol(sym.ATOMIC); }
    "extern"                { return symbol(sym.EXTERN); }
    "sizeof"                { return symbol(sym.SIZEOF); }
    "_Bool"                 { return symbol(sym.BOOL); }
	"_Complex"              { return symbol(COMPLEX); }
	"_Generic"              { return symbol(GENERIC); }
	"_Imaginary"            { return symbol(IMAGINARY); }
	"_Noreturn"             { return symbol(NORETURN); }
	"_Thread_local"         { return symbol(THREAD_LOCAL); }
	"__func__"              { return symbol(FUNC_NAME); }
	
    "break"                 { return symbol(sym.BREAK, new String(yytext())); }
    "auto"                  { return symbol(sym.AUTO, new String(yytext())); }
    "case"                  { return symbol(sym.CASE, new String(yytext())); }
    "char"                  { return symbol(sym.CHAR, new String(yytext())); }
    "continue"              { return symbol(sym.CONTINUE, new String(yytext())); }
    "do"                    { return symbol(sym.DO, new String(yytext())); }
    "double"                { return symbol(sym.DOUBLE, new String(yytext())); }
    "else"                  { return symbol(sym.ELSE, new String(yytext())); }
	"enum"					{ return symbol(sym.ENUM, new String(yytext())); }
    "float"                 { return symbol(sym.FLOAT, new String(yytext())); }
    "for"                   { return symbol(sym.FOR, new String(yytext())); }
    "goto"                  { return symbol(sym.GOTO, new String(yytext())); }
    "if"                    { return symbol(sym.IF, new String(yytext())); }
	"inline"				{ return symbol(sym.INLINE, new String(yytext())); }
    "int"                   { return symbol(sym.INT, new String(yytext())); }
    "long"                  { return symbol(sym.LONG, new String(yytext())); }
    "register"              { return symbol(sym.REGISTER, new String(yytext())); }
	"restrict"				{ return symbol(sym.RESTRICT, new String(yytext())); }
    "return"                { return symbol(sym.RETURN, new String(yytext())); }
    "short"                 { return symbol(sym.SHORT, new String(yytext())); }
    "signed"                { return symbol(sym.SIGNED, new String(yytext())); }
    "static"                { return symbol(sym.STATIC, new String(yytext())); }
    "switch"                { return symbol(sym.SWITCH, new String(yytext())); }
    "typedef"               { return symbol(sym.TYPEDEF, new String(yytext())); }
    "unsigned"              { return symbol(sym.UNSIGNED, new String(yytext())); }
    "void"                  { return symbol(sym.VOID, new String(yytext())); }
    "while"                 { return symbol(sym.WHILE, new String(yytext())); }

    /* Class Definition */

    "struct"                { return symbol(sym.STRUCT); }
    "union"                 { return symbol(sym.UNION); }

    /* Qualifiers */

    "volatile"              { return symbol(sym.VOLATILE); }
    "const"                 { return symbol(sym.CONST); }

    /* Unary Operators */

    "!"                     { return symbol(sym.LOGNEGATION); }
    "++"                    { return symbol(sym.AUTOINCRM); }
    "--"                    { return symbol(sym.AUTODECRM); }
    "~"                     { return symbol(sym.BINNEG); }

    /* Assignment */

    ">>="                   { return symbol(sym.RSHIFTASSIGN, new String(yytext())); }
    "<<="                   { return symbol(sym.LSHIFTASSIGN, new String(yytext())); }
    "-="                    { return symbol(sym.MINUSASSIGN, new String(yytext())); }
    "="                     { return symbol(sym.ASSIGNMENT, new String(yytext())); }
    "+="                    { return symbol(sym.PLUSASSIGN, new String(yytext())); }
    "*="                    { return symbol(sym.MULTASSIGN); }
    "/="                    { return symbol(sym.DIVASSIGN); }
    "%="                    { return symbol(sym.MODASSIGN); }
    "&="                    { return symbol(sym.ANDASSIGN); }
    "^="                    { return symbol(sym.XORASSIGN); }
    "|="                    { return symbol(sym.ORASSIGN); }

    /* Shift Operators */

    "<<"                    { return symbol(sym.LSHIFT); }
    ">>"                    { return symbol(sym.RSHIFT); }

    /* Relational and Logical Operators */

    "^"                     { return symbol(sym.XOROP, new String(yytext())); }
    "||"                    { return symbol(sym.OROP, new String(yytext())); }
    "|"                     { return symbol(sym.SOROP, new String(yytext())); }
    "!="                    { return symbol(sym.NEQOP, new String(yytext())); }
    "=="                    { return symbol(sym.EQOP, new String(yytext())); }
    "<="                    { return symbol(sym.LTE, new String(yytext())); }
    ">="                    { return symbol(sym.GTE, new String(yytext())); }
    "<"                     { return symbol(sym.LT, new String(yytext())); }
    ">"                     { return symbol(sym.GT, new String(yytext())); }

    /* Arithmetic Operators */

    "-"                     { return symbol(sym.MINUSOP); }
    "+"                     { return symbol(sym.PLUSOP); }
    "/"                     { return symbol(sym.DIVOP); }
    "%"                     { return symbol(sym.MODOP); }

    /* Overloaded Lexemes */

    "&&"                    { return symbol(sym.DOUBLEAND); }
    "&"                     { return symbol(sym.SINGLEAND); }
    "*"                     { return symbol(sym.STAR); }

    /* Separators */

    ";"                     { return symbol(sym.SEMICOLON, new String(yytext())); }
    "?"                     { return symbol(sym.QUESTION); }
    "["                     { return symbol(sym.LSQRBRK); }
    "]"                     { return symbol(sym.RSQRBRK); }
    "::"                    { return symbol(sym.SEPPTR); }
    ","                     { return symbol(sym.COMMA); }
    "->"                    { return symbol(sym.ARROW); }
    ":"                     { return symbol(sym.COLON); }
    "}"                     { return symbol(sym.RBRK, new String(yytext())); }
    "{"                     { return symbol(sym.LBRK, new String(yytext())); }
    "("                     { return symbol(sym.LPAR, new String(yytext())); }
    ")"                     { return symbol(sym.RPAR, new String(yytext())); }
    "."                     { return symbol(sym.DOT, new String(yytext())); }
-
    /* Others */

    "..."                   { return symbol(sym.DOTS); }

     \"([^\\\"]|\\.)*\"     { return symbol(sym.STRING_LITERAL, new String(yytext())); }

    {identifier}            { return symbol(sym.IDENTIFIER, new String(yytext())); }

    {D}+{IS}?       		{ return symbol(sym.INTEGER, new String(yytext())); }
	/**{L}{A}*					{ return check_type(); }*/
	
	("["|"<:")				{ return '['; }
	("]"|":>")				{ return ']'; }
	("{"|"<%")				{ return '{'; }
	("}"|"%>")				{ return '}'; }

	{HP}{H}+{IS}?					{ return I_CONSTANT; }
	{NZ}{D}*{IS}?					{ return I_CONSTANT; }
	"0"{O}*{IS}?					{ return I_CONSTANT; }
	{CP}?"'"([^'\\\n]|{ES})+"'"		{ return I_CONSTANT; }

	{D}+{E}{FS}?				{ return F_CONSTANT; }
	{D}*"."{D}+{E}?{FS}?		{ return F_CONSTANT; }
	{D}+"."{E}?{FS}?			{ return F_CONSTANT; }
	{HP}{H}+{P}{FS}?			{ return F_CONSTANT; }
	{HP}{H}*"."{H}+{P}{FS}?		{ return F_CONSTANT; }
	{HP}{H}+"."{P}{FS}?			{ return F_CONSTANT; }

    {BlankSpace}            { /* skip it */ }
    {Comments}              { /* skip it */ }
	{WS}+					{ /* whitespace separates tokens */ }
	.							{ /* discard bad characters */ }



}

/* Input not matched */
[^] { reportError(yyline+1, "Illegal character \"" + yytext() + "\""); }

