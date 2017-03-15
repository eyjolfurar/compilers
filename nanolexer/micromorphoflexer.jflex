

import java.io.*;

%%

%public
%class MicroMorphoFlex
%unicode
%byaccj
%line
%column

%{

// Skilgreiningar á tókum (tokens):
final static int ERROR = -1;
final static int IF = 1001;
final static int ELSE = 1002;
final static int ELSEIF = 1003;
final static int NAME = 1004;
final static int LITERAL = 1005;
final static int WHILE = 1006;
final static int RETURN = 1007;
final static int OPERATOR = 1008;
final static int VAR = 1009;

// Breyta sem mun innihalda les (lexeme):
//private static String first_lexeme;
private static String lexeme;

private static String lexeme1;
private static String lexeme2;
private static int token1;
private static int token2;
private static MicroMorphoFlex lexer;
private static int line1, column1, line2, column2;


public static void startLex(String garg) throws Exception{
	lexer = new MicroMorphoFlex(new FileReader(garg));
	token2 = lexer.yylex();
	line2 = lexer.yyline;
	column2 = lexer.yycolumn;

	advance();

}

public static String advance() throws Exception {

	String res = lexeme1;
	token1 = token2;
	lexeme1 = lexeme2;
	line1 = line2;
	if(token2==0) return res;
	lexeme2 = lexer.yytext();
	token2 = lexer.yylex();
	line2 = lexer.yyline;

	return res;

	//lexer.yypushback(lexer.yylength());
	//System.out.println("Token 1: "+token+": \'"+ first_lexeme +"\'");
	//System.out.println("Token 2: "+token2+": \'"+ lexeme +"\'");

}

public static String over( int tok )
throws Exception
{
	if( token1!=tok ) expected(tok);
	String res = lexeme1;
	advance();
	return res;
}

public static String over( char tok )
throws Exception
{
	if( token1!=(int)tok ) expected(tok);
	String res = lexeme1;
	advance();
	return res;
}

private static void expected( int tok )
{
	expected(tokname(tok));
}

private static void expected( char tok )
{
	expected("'"+tok+"'");
}

public static void expected( String tok )
{
	throw new Error("Expected "+tok+", found '"+lexeme1+"' near line "+(line1+1)+", column "+(column1+1));
}

public static int getToken1(){

	return token1;

}

public static int getToken2(){

	return token2;

}

private static String tokname( int tok )
{
	if( tok<1000 ) return ""+(char)tok;
	switch( tok )
	{
	case MicroMorphoParser.IF:
		return "if";
	case MicroMorphoParser.ELSE:
		return "else";
	case MicroMorphoParser.ELSEIF:
		return "elseif";
	case MicroMorphoParser.WHILE:
		return "while";
	case MicroMorphoParser.VAR:
		return "var";
	case MicroMorphoParser.RETURN:
		return "return";
	case MicroMorphoParser.NAME:
		return "name";
	case MicroMorphoParser.OPERATOR:
		return "operation";
	case MicroMorphoParser.LITERAL:
		return "literal";
	}
	throw new Error();
}
/*
public static int getNextToken(){

	return token2;

}
*/

public static String getFirstLex(){
	return "";//first_lexeme;
}

public static String getLexeme(){

	return lexeme;

}

public static int getLine(){
	return lexer.yyline;
}

%}

  /* Reglulegar skilgreiningar */

  /* Regular definitions */

_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_STRING=\"([^\"\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|\\[0-7][0-7]|\\[0-7])*\"
_CHAR=\'([^\'\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7]))\'
_DELIM=[{}(),;=]
_NAME=[:letter:]+ ([:letter:]|{_DIGIT})*
_OPERATOR= [\+\-*/!%&=><\:\^\~&|?]+

%%

  /* Lesgreiningarreglur */

{_DELIM} {
	lexeme2 = yytext();
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	lexeme2 = yytext();
	return LITERAL;
}

{_OPERATOR} {
	lexeme2 = yytext();
	return OPERATOR;
}

"if" {
	lexeme2 = yytext();
	return IF;
}

"else" {
	lexeme2 = yytext();
	return ELSE;
}

"elseif" {
	lexeme2 = yytext();
	return ELSEIF;
}

"while" {
	lexeme2 = yytext();
	return WHILE;
}

"return" {
	lexeme2 = yytext();
	return RETURN;
}

"var" {
	lexeme2 = yytext();
	return VAR;
}

{_NAME} {
	lexeme2 = yytext();
	return NAME;
}

";;;".*$ {
}

/* Étur of mikið.
"{;;;"(.|\r|\n)*";;;}" {
}
*/

[ \t\r\n\f] {
}

. {
	lexeme2 = yytext();
	return ERROR;
}
