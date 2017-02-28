/**
JFlex lexgreiningardæmi byggt á lesgreini fyrir NanoLisp.
Höfundur: Snorri Agnarsson, janúar 2017

Þennan lesgreini má þýða og keyra með skipununum

java -jar JFlex-1.6.1.jar micromorphoflexer.jflex
javac MicroMorphoFlex.java MicroMorphoParser.java
java MicroMorphoParser inntaksskrá > úttaksskrá
Einnig má nota forritið 'make', ef viðeigandi 'makefile'
er til staðar:
make test
**/

import java.util.Vector;
import java.util.HashMap;

public class MicroMorphoParser{

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


	static public void main( String[] args ) throws Exception
	{
		try
		{
			MicroMorphoFlex.startLex(args[0]);
			program();
		}
		catch( Throwable e )
		{
			System.out.println(e.getMessage());
		}
	}
	private static int getToken(){
		return MicroMorphoFlex.getToken();
	}
	private static int getNextToken(){
		return MicroMorphoFlex.getNextToken();
	}
	private static String getLexeme(){
		return MicroMorphoFlex.getFirstLex();
	}
	private static String getNextLexeme(){
		return MicroMorphoFlex.getNextLexeme();
	}
	private static void advance() throws Exception {
		MicroMorphoFlex.advance();
	}
	private static String over(int tok) throws Exception {
		MicroMorphoFlex.over(int tok);
	}

	public static void program() throws Exception {

		while (getToken() != 0) {
			function();
		}
	}


	public static void function() throws Exception {
		over(NAME);
		over('(');
		if(getToken() == NAME) {
			over(NAME);
			while (getToken() == ',' && getNextToken() == NAME) {
				over(',');
				over(NAME);
			}
		}
		over(')');
		over('{');
		while (getToken() == VAR) {
			decl();
			over(';');
		}
		while (getToken() != '}') {
			expr();
			over(';');
		}
		over('}');
		/*
		if (getToken() == NAME) {
			advance();
			if (getLexeme().equals("(")) {
				advance();
				if (getToken() == NAME) {
					advance();
					while(getLexeme().equals(",") && getNextToken() == NAME) {
						advance();
						advance();
					}
				}
				if (getLexeme().equals(")")) {
					advance();
					if (getLexeme().equals("{")) {
						advance();
					}
					while (getToken() == VAR) {
						decl();
						if (getLexeme().equals(";")) {
							advance();
						}
						else {
							throw new Error("ekki i lagi 1 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
						}
					}
					while (!getLexeme().equals("}")) {
						expr();
						if (getLexeme().equals(";")) {
							advance();
						}

						else {
							throw new Error("ekki i lagi 2 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
						}
					}
					advance();
				}
				else {
					throw new Error("ekki i lagi 3 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
				}
			}
			else {
				throw new Error("ekki i lagi 4 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
			}
		}
		else {
			throw new Error("ekki i lagi 5 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
		}
		*/
	}

	public static void expr() throws Exception {
		if(getToken() == RETURN){
			advance();
			expr();
		}
		else if(getToken() == NAME){
			advance();
			if(getLexeme().equals("=")){
				advance();

				expr();
			}
		}
		else{
			binopexpr();
		}
	}

	public static void binopexpr() throws Exception {
		smallexpr();
		while(getToken() == OPERATOR){
			advance();
			smallexpr();
		}

	}

	public static void smallexpr() throws Exception {
		if(getToken()==NAME && getNextLexeme().equals("(")){
			advance();
			advance();
			if(!getLexeme().equals(")")) {
				expr();
				while(!getLexeme().equals(")")){
					if(getLexeme().equals(",")){
						advance();
					}
					else {
						throw new Error("ekki i lagi 6 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
					}
					expr();
				}
				advance();
			}
			advance();
		}
		else if(getToken()==NAME){
			advance();
		}
		else if(getToken()==OPERATOR){
			advance();
			smallexpr();
		}
		else if(getToken()==LITERAL){
			advance();
		}
		else if(getLexeme().equals("(")){
			advance();
			expr();
			if(getLexeme().equals(")")){
				advance();
			}
			else {
				throw new Error("ekki i lagi 7 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
			}
		}
		else if(getToken()==IF){
			advance();
			expr();
			body();
			while(getNextToken()==ELSEIF){
				advance();
				expr();
				body();
			}
			if(getNextToken()==ELSE){
				advance();
				body();
			}
			else{
				throw new Error("ekki i lagi 8 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
			}
		}
		else if(getToken()==WHILE){
			advance();
			expr();
			body();
		}
		else {
			throw new Error("ekki i lagi 9 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
		}
	}


	public static void decl() throws Exception {

		if (getToken() == VAR && getNextToken() == NAME) {
			advance();
			advance();

			while(getLexeme().equals(",")) {
				advance();
				if(getToken() == NAME){
					advance();
				}
				else {
					throw new Error("ekki i lagi 10 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
				}
			}
		}
		else {
			throw new Error("ekki i lagi 11 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
		}
	}

	public static void body() throws Exception {
		if (getLexeme().equals("{")) {
			while (!getLexeme().equals("}")) {
				expr();
				if (getLexeme().equals(";")) {
					advance();
				}
				else {
					throw new Error("ekki i lagi 12 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
				}
			}
			advance();
		}
		else {
			throw new Error("ekki i lagi 13 nalaegt: "+ getLexeme() + " og " + getNextLexeme());
		}
	}



}
//{núll eða fleiri} [optional]
/*
program		=	{ function }
;

function	= 	NAME, '(', [ NAME, { ',', NAME } ] ')'
'{', { decl, ';' }, { expr, ';' }, '}'
;

decl		=	'var', NAME, { ',', NAME }
;

expr		=	'return', expr
|	NAME, '=', expr
|	binopexpr
;

binopexpr	=	smallexpr, { OPNAME, smallexpr }
;

smallexpr	=	NAME
|	NAME, '(', [ expr, { ',', expr } ], ')' //hvað er þetta?
|	OPNAME, smallexpr
| 	LITERAL
|	'(', expr, ')'
|	'if', expr, body, { 'elsif', expr, body }, [ 'else', body ]
|	'while', expr, body
;

body		=	'{', { expr, ';' }, '}'
;
*/
