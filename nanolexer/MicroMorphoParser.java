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
			Object els = null;
			Object[] i = new Object[]{expr(), body()};
			Vector<Object> ei = new Vector<Object>();
			while(getToken1()==ELSEIF){
				over(ELSEIF);
				ei.add(new Object[]{expr(), body()});
			}
			if(getToken1()==ELSE){
				over(ELSE);
				els = body();
			}
			return new Object[]{"IF", i, ei.toArray(), els}; 
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


	public static void emit(String line){
		System.out.println(line);
	}
	
	private static int uniLab = 0;
	public static int newLab(){
		return uniLab++;
	}

	public static void generateProgram(String name, Object[] code){
		String pName = name.substring(0,name.indexOf('.'));
		emit("\""+pName+".mexe\" = main in");
		emit("!{{");
		for( int i = 0 ; i!=code.length ; i++) generateFunction((Object[])code[i]);
		emit("}}*BASIS");
	}

	public static void generateFunction(Object[] f){
		//f {fName, parCount, varCount, expr()};
		String fname = (String)f[0];
		int parCount = (Integer)f[1];
		int varCount = (Integer)f[2];

		emit("#\""+fname+"[f"+parCount+"]\" =");
		emit("[");	
		//System.out.println(f[0]);
		Object[] exprObj = (Object[])f[3];
		System.out.println(Arrays.deepToString(exprObj));
		for( int i = 0 ; i!=exprObj.length ; i++) generateExpr((Object[])exprObj[i]);
		
		emit("]");
	}

	public static void generateExpr(Object[] e){
		//String tag = e[0];
		switch((String)e[0]){
			case "RETURN":
				generateExpr((Object[])e[1]);
				emit("(Return)");
				return;
			case "STORE":
				generateExpr((Object[])e[2]);
				emit("(Store "+e[1]+")");
				return;
			case "NAME":
				//e = {NAME,name}
				System.out.println("name");
				emit("(Fetch "+e[1]+")");
				return;
			case "LITERAL":
				//e = { LITERAL , literal}
				emit("(MakeVal "+(String)e[1]+")");
				return;
			case "IF":
				System.out.println(Arrays.deepToString(e));
				int labElse = newLab();
				int labEnd = newLab();
				Object[] ifObj = (Object[])e[1];
				Object[] eIfArray = (Object[])e[2];
				Object[] els = (Object[])e[3];
				generateExpr((Object[])ifObj[0]);
				emit("(GoFalse _"+ labElse +")");
				generateBody((Object[])ifObj[1]);
				emit ("(Go _"+labEnd +")");
				emit("_"+labElse+":");
				int labTemp;
				for(int i=0; i<eIfArray.length; i++){
					labTemp = newLab();
					Object[] elsIf = (Object[])eIfArray[i];
					generateExpr((Object[])elsIf[0]);
					emit("(GoFalse _"+ labTemp +")");
					generateBody((Object[])elsIf[1]);
					emit ("(Go _"+labEnd +")");
					emit("_"+labTemp+":");
				}
				if(els.length != 0) generateBody((Object[])els[0]);
				emit("_"+labEnd+":");
				return;

			case "CALL":
				Object[] args = (Object[])e[2];
				int i;
				for(i=0; i!=args.length; i++){
					generateExpr((Object[])args[i]);
				}
				emit("(Call #\""+e[1]+"[f"+i+"]\" "+i+")");
				return;
			case "VARCALL":
				emit("(Fetch "+e[1]+")");
			default:
				return;
		}
	}

	public static void generateBody(Object[] e){
		System.out.println("tetta er ekki setning");
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
