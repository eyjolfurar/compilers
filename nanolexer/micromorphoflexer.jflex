/**
	JFlex lexgreiningardæmi byggt á lesgreini fyrir NanoLisp.
	Höfundur: Snorri Agnarsson, janúar 2017

	Þennan lesgreini má þýða og keyra með skipununum
	
		java -jar JFlex-1.6.1.jar micromorphoflexer.jflex
		javac MicroMorphoFlex.java
		java MicroMorphoFlex inntaksskrá > úttaksskrá
	Einnig má nota forritið 'make', ef viðeigandi 'makefile'
	er til staðar:
		make test
 **/

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
private static String first_lexeme;
private static String lexeme;

private static int token;
private static int token2;
private static MicroMorphoFlex lexer;

public static void startLex(String garg) throws Exception{
	lexer = new MicroMorphoFlex(new FileReader(garg));
	
	token = lexer.yylex();
	first_lexeme = lexer.yytext();

	token2 = lexer.yylex();
	//System.out.println("Token 1: "+token+" : "+first_lexeme+"");
	//System.out.pritln("Token 2: "+token2+": \'"+lexer.yytext()+"\'");
	lexer.yypushback(lexer.yylength());

}

public static void advance() throws Exception {
	
		if( token != 0) {
			
			token = lexer.yylex();			
			first_lexeme = lexer.yytext();
			token2 = lexer.yylex();
			lexer.yypushback(lexer.yylength());
			//System.out.println("Token 1: "+token+": \'"+ first_lexeme +"\'");
			//System.out.println("Token 2: "+token2+": \'"+ lexeme +"\'");
		}

}

public static void over(){

	//hoppa yfir name og sviga?!

}

public static int getToken(){

	return token;

}

public static int getNextToken(){

	return token2;

}

public static String getFirstLex(){
	return first_lexeme;
}

public static String getLexeme(){

	return lexeme;


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
	lexeme = yytext();		
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	lexeme = yytext();
	return LITERAL;
}

{_OPERATOR} {
	lexeme = yytext();
	return OPERATOR;
}

"if" {
	lexeme = yytext();
	return IF;
}

"else" {
	lexeme = yytext();
	return ELSE;
}

"elseif" {
	lexeme = yytext();
	return ELSEIF;
}

"while" {
	lexeme = yytext();
	return WHILE;
}

"return" {
	lexeme = yytext();
	return RETURN;
}

"VAR" {
	lexeme = yytext();
	return VAR;
}

{_NAME} {
	lexeme = yytext();
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
	lexeme = yytext();
	return ERROR;
}
