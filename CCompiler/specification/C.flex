package com.ccompiler.analysis;

import java_cup.runtime.*;

%%

%class Lexical
%public
%unicode
%line
%column
%cup
%cupdebug


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

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
BlankSpace = {LineTerminator} | [ \t\f]
Comments = {LineComment} | {BlockComment}
LineComment = "//" {InputCharacter}* {LineTerminator}?
BlockComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

%%

<YYINITIAL> {

    /* Keywords */
	
	"auto"                  { return symbol(Sym.AUTO, new String(yytext())); }	
    "break"                 { return symbol(Sym.BREAK, new String(yytext())); }
    "case"                  { return symbol(Sym.CASE, new String(yytext())); }
    "char"                  { return symbol(Sym.CHAR, new String(yytext())); }
    "const"                 { return symbol(Sym.CONST, new String(yytext())); }
	"continue"              { return symbol(Sym.CONTINUE, new String(yytext())); }
	"default"               { return symbol(Sym.DEFAULT, new String(yytext())); }
    "do"                    { return symbol(Sym.DO, new String(yytext())); }
    "double"                { return symbol(Sym.DOUBLE, new String(yytext())); }
    "else"                  { return symbol(Sym.ELSE, new String(yytext())); }
	"enum"					{ return symbol(Sym.ENUM, new String(yytext())); }
	"extern"                { return symbol(Sym.EXTERN, new String(yytext())); }
    "float"                 { return symbol(Sym.FLOAT, new String(yytext())); }
    "for"                   { return symbol(Sym.FOR, new String(yytext())); }
    "goto"                  { return symbol(Sym.GOTO, new String(yytext())); }
    "if"                    { return symbol(Sym.IF, new String(yytext())); }
	"inline"				{ return symbol(Sym.INLINE, new String(yytext())); }
    "int"                   { return symbol(Sym.INT, new String(yytext())); }
    "long"                  { return symbol(Sym.LONG, new String(yytext())); }
    "register"              { return symbol(Sym.REGISTER, new String(yytext())); }
	"restrict"				{ return symbol(Sym.RESTRICT, new String(yytext())); }
    "return"                { return symbol(Sym.RETURN, new String(yytext())); }
    "short"                 { return symbol(Sym.SHORT, new String(yytext())); }
    "signed"                { return symbol(Sym.SIGNED, new String(yytext())); }
	"sizeof"                { return symbol(Sym.SIZEOF, new String(yytext())); }
    "static"                { return symbol(Sym.STATIC, new String(yytext())); }
	"struct"                { return symbol(Sym.STRUCT, new String(yytext())); }    
	"switch"                { return symbol(Sym.SWITCH, new String(yytext())); }
    "typedef"               { return symbol(Sym.TYPEDEF, new String(yytext())); }
    "union"                 { return symbol(Sym.UNION, new String(yytext())); }
	"unsigned"              { return symbol(Sym.UNSIGNED, new String(yytext())); }
    "void"                  { return symbol(Sym.VOID, new String(yytext())); }
    "volatile"              { return symbol(Sym.VOLATILE, new String(yytext())); }
	"while"                 { return symbol(Sym.WHILE, new String(yytext())); }
	"_Alignas"              { return symbol(Sym.ALIGNAS); }
	"_Alignof"              { return symbol(Sym.ALIGNOF); }
	"_Atomic"               { return symbol(Sym.ATOMIC); }
    "_Bool"                 { return symbol(Sym.BOOL); }
	"_Complex"              { return symbol(Sym.COMPLEX); }
	"_Generic"              { return symbol(Sym.GENERIC); }
	"_Imaginary"            { return symbol(Sym.IMAGINARY); }
	"_Noreturn"             { return symbol(Sym.NORETURN); }
	"_Static_assert"        { return symbol(Sym.STATIC_ASSERT); }
	"_Thread_local"         { return symbol(Sym.THREAD_LOCAL); }
	"__func__"              { return symbol(Sym.FUNC_NAME); }
							
	{L}{A}*					{ return symbol(Sym.IDENTIFIER, new String(yytext())); }
	
	'(.|\\(n|t|r))?'		{ return symbol(Sym.C_CONSTANT , new String(yytext())); }
	{HP}{H}+{IS}?					{ return symbol(Sym.I_CONSTANT , new String(yytext())); }
	{NZ}{D}*{IS}?					{ return symbol(Sym.I_CONSTANT , new String(yytext())); }
	"0"{O}*{IS}?					{ return symbol(Sym.I_CONSTANT , new String(yytext())); }
	{CP}?"'"([^'\\\n]|{ES})+"'"		{ return symbol(Sym.I_CONSTANT , new String(yytext())); }

	{D}+{E}{FS}?				{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	{D}*"."{D}+{E}?{FS}?		{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	{D}+"."{E}?{FS}?			{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}+{P}{FS}?			{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}*"."{H}+{P}{FS}?		{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	{HP}{H}+"."{P}{FS}?			{ return symbol(Sym.F_CONSTANT , new String(yytext())); }
	
	({SP}?\"([^\"\\\n]|{ES})*\"{WS}*)+	{ return symbol(Sym.STRING_LITERAL, new String(yytext())); }
	
	"..."                   { return symbol(Sym.ELLIPSIS); }
	">>="                   { return symbol(Sym.RIGHT_ASSIGN, new String(yytext())); }
    "<<="                   { return symbol(Sym.LEFT_ASSIGN, new String(yytext())); }
    "+="                    { return symbol(Sym.ADD_ASSIGN, new String(yytext())); }
    "-="                    { return symbol(Sym.SUB_ASSIGN, new String(yytext())); }
    "*="                    { return symbol(Sym.MUL_ASSIGN, new String(yytext())); }
    "/="                    { return symbol(Sym.DIV_ASSIGN,  new String(yytext())); }
    "%="                    { return symbol(Sym.MOD_ASSIGN, new String(yytext())); }
    "&="                    { return symbol(Sym.AND_ASSIGN, new String(yytext())); }
    "^="                    { return symbol(Sym.XOR_ASSIGN, new String(yytext())); }
    "|="                    { return symbol(Sym.OR_ASSIGN, new String(yytext())); }
    ">>"                    { return symbol(Sym.RIGHT_OP, new String(yytext())); }	
	"<<"                    { return symbol(Sym.LEFT_OP, new String(yytext())); }
	"++"                    { return symbol(Sym.INC_OP, new String(yytext())); }
    "--"                    { return symbol(Sym.DEC_OP, new String(yytext())); }
	"->"                    { return symbol(Sym.PTR_OP, new String(yytext())); }
	"&&"                    { return symbol(Sym.AND_OP, new String(yytext())); }
	"||"                    { return symbol(Sym.OR_OP, new String(yytext())); }
	"<="                    { return symbol(Sym.LE_OP, new String(yytext())); }
    ">="                    { return symbol(Sym.GE_OP, new String(yytext())); }
    "=="                    { return symbol(Sym.EQ_OP, new String(yytext())); }
	"!="                    { return symbol(Sym.NE_OP, new String(yytext())); }
	
	"="                     { return symbol(Sym.ASSIGNMENT); }
	"!"                     { return symbol(Sym.LOGNEGATION); }
    "~"                     { return symbol(Sym.BINNEG); }
    "^"                     { return symbol(Sym.XOROP); }
    "|"                     { return symbol(Sym.SOROP); }
    "<"                     { return symbol(Sym.LT); }
    ">"                     { return symbol(Sym.GT); }
    "-"                     { return symbol(Sym.MINUSOP); }
    "+"                     { return symbol(Sym.PLUSOP); }
    "/"                     { return symbol(Sym.DIVOP); }
    "%"                     { return symbol(Sym.MODOP); }
    "&"                     { return symbol(Sym.SINGLEAND); }
    "*"                     { return symbol(Sym.STAR); }
    ";"                     { return symbol(Sym.SEMICOLON); }
    "?"                     { return symbol(Sym.QUESTION); }
    ("["|"<:")              { return symbol(Sym.LSQRBRK); }
    ("]"|":>")              { return symbol(Sym.RSQRBRK); }
    ","                     { return symbol(Sym.COMMA); }
    ":"                     { return symbol(Sym.COLON); }
    ("}"|"%>")              { return symbol(Sym.RBRK); }
    ("{"|"<%")              { return symbol(Sym.LBRK); }
    "("                     { return symbol(Sym.LPAR); }
    ")"                     { return symbol(Sym.RPAR); }
    "."                     { return symbol(Sym.DOT); }
-
    /* Others */
    {WS}           			{ /* skip it */ }	
    {BlankSpace}            { /* skip it */ }
    {Comments}              { /* skip it */ }
	{WS}+					{ /* whitespace separates tokens */ }
	.						{ /* discard bad characters */ }
}

/* Input not matched */
[^] { reportError(yyline+1, "Illegal character \"" + yytext() + "\""); }
