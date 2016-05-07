package com.ccompiler.analyzer.lexical;

import java_cup.runtime.*;
import com.ccompiler.analyzer.syntactical.sym;

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
A =  [a-zA-Z_0-9]
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

letter          = [A-Za-z]
L               = [a-zA-Z_]
digit           = [0-9]
alphanumeric    = {letter}|{digit}
other_id_char   = [_]
identifier      = {letter}({alphanumeric}|{other_id_char})*

%%

<YYINITIAL> {

    /* Keywords */
	
	"auto"                  { return symbol(sym.AUTO, new String(yytext())); }	
    "break"                 { return symbol(sym.BREAK, new String(yytext())); }
    "case"                  { return symbol(sym.CASE, new String(yytext())); }
    "char"                  { return symbol(sym.CHAR, new String(yytext())); }
    "const"                 { return symbol(sym.CONST, new String(yytext())); }
	"continue"              { return symbol(sym.CONTINUE, new String(yytext())); }
	"default"               { return symbol(sym.DEFAULT, new String(yytext())); }
    "do"                    { return symbol(sym.DO, new String(yytext())); }
    "double"                { return symbol(sym.DOUBLE, new String(yytext())); }
    "else"                  { return symbol(sym.ELSE, new String(yytext())); }
	"enum"					{ return symbol(sym.ENUM, new String(yytext())); }
	"extern"                { return symbol(sym.EXTERN, new String(yytext())); }
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
	"sizeof"                { return symbol(sym.SIZEOF, new String(yytext())); }
    "static"                { return symbol(sym.STATIC, new String(yytext())); }
	"struct"                { return symbol(sym.STRUCT, new String(yytext())); }    
	"switch"                { return symbol(sym.SWITCH, new String(yytext())); }
    "typedef"               { return symbol(sym.TYPEDEF, new String(yytext())); }
    "union"                 { return symbol(sym.UNION, new String(yytext())); }
	"unsigned"              { return symbol(sym.UNSIGNED, new String(yytext())); }
    "void"                  { return symbol(sym.VOID, new String(yytext())); }
    "volatile"              { return symbol(sym.VOLATILE, new String(yytext())); }
	"while"                 { return symbol(sym.WHILE, new String(yytext())); }
	"_Alignas"              { return symbol(sym.ALIGNAS); }
	"_Alignof"              { return symbol(sym.ALIGNOF); }
	"_Atomic"               { return symbol(sym.ATOMIC); }
    "_Bool"                 { return symbol(sym.BOOL); }
	"_Complex"              { return symbol(sym.COMPLEX); }
	"_Generic"              { return symbol(sym.GENERIC); }
	"_Imaginary"            { return symbol(sym.IMAGINARY); }
	"_Noreturn"             { return symbol(sym.NORETURN); }
	"_Static_assert"        { return symbol(sym.STATIC_ASSERT); }
	"_Thread_local"         { return symbol(sym.THREAD_LOCAL); }
	"__func__"              { return symbol(sym.FUNC_NAME); }
    
	{L}{A}*					{ /**return check_type();*/ }
	
	{HP}{H}+{IS}?					{ return symbol(sym.I_CONSTANT , new String(yytext())); }
	{NZ}{D}*{IS}?					{ return symbol(sym.I_CONSTANT , new String(yytext())); }
	"0"{O}*{IS}?					{ return symbol(sym.I_CONSTANT , new String(yytext())); }
	{CP}?"'"([^'\\\n]|{ES})+"'"		{ return symbol(sym.I_CONSTANT , new String(yytext())); }

	{D}+{E}{FS}?				{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	{D}*"."{D}+{E}?{FS}?		{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	{D}+"."{E}?{FS}?			{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}+{P}{FS}?			{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}*"."{H}+{P}{FS}?		{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}+"."{P}{FS}?			{ return symbol(sym.F_CONSTANT , new String(yytext())); }
	
	({SP}?\"([^\"\\\n]|{ES})*\"{WS}*)+	{ return symbol(sym.STRING_LITERAL); }
	
	"..."                   { return symbol(sym.ELLIPSIS); }
	">>="                   { return symbol(sym.RIGHT_ASSIGN, new String(yytext())); }
    "<<="                   { return symbol(sym.LEFT_ASSIGN, new String(yytext())); }
    "+="                    { return symbol(sym.ADD_ASSIGN, new String(yytext())); }
    "-="                    { return symbol(sym.SUB_ASSIGN, new String(yytext())); }
    "*="                    { return symbol(sym.MUL_ASSIGN, new String(yytext())); }
    "/="                    { return symbol(sym.DIV_ASSIGN,  new String(yytext())); }
    "%="                    { return symbol(sym.MOD_ASSIGN, new String(yytext())); }
    "&="                    { return symbol(sym.AND_ASSIGN, new String(yytext())); }
    "^="                    { return symbol(sym.XOR_ASSIGN, new String(yytext())); }
    "|="                    { return symbol(sym.OR_ASSIGN, new String(yytext())); }
    ">>"                    { return symbol(sym.RIGHT_OP, new String(yytext())); }	
	"<<"                    { return symbol(sym.LEFT_OP, new String(yytext())); }
	"++"                    { return symbol(sym.INC_OP, new String(yytext())); }
    "--"                    { return symbol(sym.DEC_OP, new String(yytext())); }
	"->"                    { return symbol(sym.PTR_OP, new String(yytext())); }
	"&&"                    { return symbol(sym.AND_OP, new String(yytext())); }
	"||"                    { return symbol(sym.OR_OP, new String(yytext())); }
	"<="                    { return symbol(sym.LE_OP, new String(yytext())); }
    ">="                    { return symbol(sym.GE_OP, new String(yytext())); }
    "=="                    { return symbol(sym.EQ_OP, new String(yytext())); }
	"!="                    { return symbol(sym.NE_OP, new String(yytext())); }
	
	"="                     { return symbol(sym.ASSIGNMENT); }
	"!"                     { return symbol(sym.LOGNEGATION); }
    "~"                     { return symbol(sym.BINNEG); }
    "^"                     { return symbol(sym.XOROP); }
    "|"                     { return symbol(sym.SOROP); }
    "<"                     { return symbol(sym.LT); }
    ">"                     { return symbol(sym.GT); }
    "-"                     { return symbol(sym.MINUSOP); }
    "+"                     { return symbol(sym.PLUSOP); }
    "/"                     { return symbol(sym.DIVOP); }
    "%"                     { return symbol(sym.MODOP); }
    "&"                     { return symbol(sym.SINGLEAND); }
    "*"                     { return symbol(sym.STAR); }
    ";"                     { return symbol(sym.SEMICOLON); }
    "?"                     { return symbol(sym.QUESTION); }
    ("["|"<:")              { return symbol(sym.LSQRBRK); }
    ("]"|":>")              { return symbol(sym.RSQRBRK); }
    ","                     { return symbol(sym.COMMA); }
    ":"                     { return symbol(sym.COLON); }
    ("}"|"%>")              { return symbol(sym.RBRK); }
    ("{"|"<%")              { return symbol(sym.LBRK); }
    "("                     { return symbol(sym.LPAR); }
    ")"                     { return symbol(sym.RPAR); }
    "."                     { return symbol(sym.DOT); }

    /* Others */
    {identifier}            { return symbol(sym.IDENTIFIER, new String(yytext())); }	

    {BlankSpace}            { /* skip it */ }
    {Comments}              { /* skip it */ }
	{WS}+					{ /* whitespace separates tokens */ }
	.						{ /* discard bad characters */ }
}

/* Input not matched */
[^] { reportError(yyline+1, "Illegal character \"" + yytext() + "\""); }

