/*
JFlex lexgreiningardæmi byggt á lesgreini fyrir NanoLisp.
Höfundur: Snorri Agnarsson, janúar 2017

Þennan lesgreini má þýða og keyra með skipununum

java -jar JFlex-1.6.1.jar micromorphoflexer.jflex
javac MicroMorphoFlex.java MicroMorphoParser.java
java MicroMorphoParser inntaksskrá > úttaksskrá
Einnig má nota forritið 'make', ef viðeigandi 'makefile'
er til staðar:
make test
*/

import java.util.Vector;
import java.util.HashMap;
import java.util.Arrays;
import static java.lang.System.out;

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
		Object[] code = null;
		try
		{
			MicroMorphoFlex.startLex(args[0]);

			code = program();
			System.out.println("Millithulu Objectid: " + Arrays.deepToString(code));
			generateProgram(args[0], code);

		}
		catch( Throwable e )
		{
			System.out.println(e.getMessage());
		}
	}
	private static int getToken1(){
		return MicroMorphoFlex.getToken1();
	}
	
	private static int getToken2(){
		return MicroMorphoFlex.getToken2();
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
		if( varTable.get(name) != null ){
			throw new Error("Variable "+name+" already exists, near line " + MicroMorphoFlex.getLine());
		}
		varTable.put(name,varCount++);
	}

	private static int findVar( String name )
	{
		Integer res = varTable.get(name);
		if( res == null ){
			throw new Error("Variable "+name+" does not exist, near line" + MicroMorphoFlex.getLine());
		}
		return res;
	}

	public static Object[] program() throws Exception {
		
		Vector<Object> a = new Vector<Object>();

		while (getToken1() != 0) {
			a.add(function());
		}
		return a.toArray();
	}


	public static Object[] function() throws Exception {
		varCount = 0;
    varTable = new HashMap<String,Integer>();

		String fName = over(NAME); //geyma nafnið

		over('(');
		if(getToken1() == NAME) {
			addVar(over(NAME));
			while (getToken1() == ',' && getToken2() == NAME) {
				over(',');
				addVar(over(NAME));
			}
		}
		over(')');
		int parCount = varCount;
		over('{');
		while (getToken1() == VAR) {
			decl(); // inn í decl bæta við addVar fyrir allar breytur
			over(';');
		}

		Vector<Object> b = new Vector<Object>();
		while (getToken1() != '}') {
			b.add(expr());
			over(';');
		}
		over('}');
		return new Object[]{fName, parCount, varCount-parCount, b.toArray()};
	}

	public static Object[] expr() throws Exception {
		if(getToken1() == RETURN){
			over(RETURN);
			return new Object[]{"RETURN", expr()};
		}
		else if(getToken1() == NAME && getToken2() == '='){
			int pos = findVar(over(NAME));
			over('=');
			return new Object[]{"STORE", pos, expr()};
		}
		else{
			return binopexpr();
		}

	}


	public static Object[] binopexpr() throws Exception {
		Object[] e = smallexpr();

		while(getToken1() == OPERATOR){

			String op = over(OPERATOR);
			
			e = new Object[]{"CALL", op, new Object[]{e, smallexpr()}};

		}
		return e;
	}

	public static Object[] smallexpr() throws Exception {
		if(getToken1()==NAME){
			String c = over(NAME);
			if(getToken1() == '(') {
				over('(');
					Vector<Object> v = new Vector<Object>();
				while(getToken1()!=')'){
					v.add(expr());
					if( getToken1() == ')' ) break;
					over(',');				
				}
				over(')');
				return new Object[] {"FUNCALL",c, v.toArray()};
			} else { 
				int pos = findVar(c);
				return new Object[]{"VARCALL", pos}; }

		}

		else if(getToken1()==WHILE){
			over(WHILE);
			Object[] e = expr();
			Object[] b = body();
			return new Object[]{"WHILE", e, b};
		}

		else if(getToken1()==OPERATOR){
			String op = over(OPERATOR);
			return new Object[] {"OPERATOR", op, smallexpr()};
		}

		else if(getToken1()==LITERAL){
			String lit = over(LITERAL);
			return new Object[]{"LITERAL", lit};
		}

		else if(getToken1() == '('){
			over('(');
			Object[] e = {expr()};
			over(')');
			return e;
		}

		else if(getToken1()==IF){
			over(IF);
			Object[] els = new Object[]{};
			Object[] i = new Object[]{"IF", expr(), body()};
			Vector<Object> ei = new Vector<Object>();
			while(getToken1()==ELSEIF){
				over(ELSEIF);
				ei.add(new Object[]{"ELSEIF", expr(), body()});
			}
			if(getToken1()==ELSE){
				over(ELSE);
				els = new Object[]{"ELSE", body()};
			}
			return new Object[]{"IFS", i, ei.toArray(), els}; 
		}

		else{
			MicroMorphoFlex.expected("expression");	
		}
		return null;
	}


	public static void decl() throws Exception {

		over(VAR);
		addVar(over(NAME));
			while(getToken1() == ',') {
				over(',');
				addVar(over(NAME));
			}
	}

	public static Object[] body() throws Exception {
		over('{');
		Vector<Object> b = new Vector<Object>();
		while (getToken1() != '}') {
			b.add(expr());
			over(';');
		}
		over('}');
		return new Object[]{"BODY", b.toArray()};
	}

	public static void generateProgram(String filename, Object[] p){
		String pName = filename.substring(0,filename.indexOf('.'));
		out.println("\""+pName+".mexe\" = main in");
		out.println("!{{");
		for( int i = 0 ; i!=p.length ; i++) generateFunction((Object[])p[i]);
		out.println("}}*BASIS");
	}

	public static void generateFunction(Object[] f){
		//f {fName, parCount, varCount, expr()};
		String fname = (String)f[0];
		int parCount = (Integer)f[1];
		int varCount = (Integer)f[2];
		out.println("#\""+fname+"[f"+parCount+"]\" =");
		out.println("[");
		generateExpr((Object[])f[3]);
		out.println("]");
	}
	public static void generateExpr(Object[] e){
		switch( (String) e[0]){
			case "RETURN":
			case "STORE":

			case "NAME":
				//e = {NAME,name}
				emit ("(FetchP"+e[1]+")");
				return;
			case "LITERAL":
				//e = { LITERAL , l i t e r a l } 537
				emit ("(Make ValR "+(String)e[1]+")");
				return;
			case "IF":
			case "CALL":
			case "":

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
|	NAME, '(', [ expr, { ',', expr } ], ')' fun(hundur, 2 3)
|	OPNAME, smallexpr
| 	LITERAL
|	'(', expr, ')'
|	'if', expr, body, { 'elsif', expr, body }, [ 'else', body ]
|	'while', expr, body
;

body		=	'{', { expr, ';' }, '}'
;
*/
