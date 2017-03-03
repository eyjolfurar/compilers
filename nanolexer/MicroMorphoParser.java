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
	/*
	private static String getNextLexeme(){
		return MicroMorphoFlex.getNextLexeme();
	}
	*/
	private static void advance() throws Exception {
		MicroMorphoFlex.advance();
	}
	private static String over(int tok) throws Exception {
		return MicroMorphoFlex.over(tok);
	}

	private static int varCount;
	private static HashMap<String,Integer> varTable;

	private static void addVar( String name )
	{
		if( varTable.get(name) != null )
			throw new Error("Variable "+name+" already exists, near line ");
		varTable.put(name,varCount++);
	}

	private static int findVar( String name )
	{
		Integer res = varTable.get(name);
		if( res == null )
			throw new Error("Variable "+name+" does not exist, near line ");
		return res;
	}

	public static void program() throws Exception {

		while (getToken() != 0) {
			function();
		}
	}


	public static void function() throws Exception {
		varCount = 0;
    varTable = new HashMap<String,Integer>();

		String fName = over(NAME); //geyma nafnið

		over('(');
		if(getToken() == NAME) {
			addVar(over(NAME));
			while (getToken() == ',' && getNextToken() == NAME) {
				over(',');
				addVar(over(NAME));
			}
		}
		over(')');
		over('{');
		while (getToken() == VAR) {
			decl(); // inn í decl bæta við addVar fyrir allar breytur
			over(';');
		}

		// Búa til new Vector<Object> b = new Vector<Object>();
		while (getToken() != '}') {
			expr(); // Leggja öll expr á minnið b.add(expr());
			over(';');
		}
		over('}');
	}

	public static void expr() throws Exception {
		if(getToken() == RETURN){
			over(RETURN);
			expr();
		}
		else if(getToken() == NAME && getNextToken() == '='){
			over(NAME);
			over('=');
			expr();
		}
		else{
			binopexpr();
		}
	}

	public static void binopexpr() throws Exception {
		smallexpr();
		while(getToken() == OPERATOR){
			over(OPERATOR);
			smallexpr();
		}

	}

	public static void smallexpr() throws Exception {
		if(getToken()==NAME){
			over(NAME);
			if(getToken() == '(') {
				over('(');
				while(getToken()!=')'){
					expr();
					if( getToken() == ')' ) break;
					over(',');				
				}
				over(')');
			}
			advance();
		}

		else if(getToken()==OPERATOR){
			over(OPERATOR);
			smallexpr();
		}

		else if(getToken()==LITERAL){
			over(LITERAL);
		}

		else if(getToken() == '('){
			over('(');
			expr();
			over(')');
		}

		else if(getToken()==IF){
			over(IF);
			expr();
			body();
			while(getToken()==ELSEIF){
				over(ELSEIF);
				expr();
				body();
			}
			if(getToken()==ELSE){
				over(ELSE);
				body();
			}
		}

		else if(getToken()==WHILE){
			advance();
			expr();
			body();
		}

		else{
			System.out.println("vantaði else statement, filler efni");
		}
	}


	public static void decl() throws Exception {

		over(VAR);
		over(NAME);
			while(getToken() == ',') {
				over(',');
				over(NAME);
			}		
	}

	public static void body() throws Exception {
		over('{');
		while (getToken() != '}') {
			expr();
			over(';');
		}
		over('}');
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
