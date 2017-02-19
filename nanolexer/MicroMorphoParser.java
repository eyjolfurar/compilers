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
		//MicroMorphoFlex lexer = new MicroMorphoFlex(new FileReader(garg));

	    //Object[] code = null;
	    try
	    {
	        MicroMorphoFlex.startLex(args[0]);
	        //program();
	        //while(getToken() != 0){
	        	//System.out.println("token 1: " + getToken() + " lex : " + getFirstLexeme() + "");
	        	//System.out.println("token 2: " + getNextToken() + " lex2 : " + getLexeme() + "");
	        	program();

	        	//advance();

	        //}
	    }
	    catch( Throwable e )
	    {
	        System.out.println(e.getMessage());
	    }
	    //generateProgram(args[0],code);
	}
	private static int getToken(){
		return MicroMorphoFlex.getToken();
	}
	private static int getNextToken(){
		return MicroMorphoFlex.getNextToken();
	}
	private static String getFirstLexeme(){
		return MicroMorphoFlex.getFirstLex();
	}
	private static String getLexeme(){
		return MicroMorphoFlex.getLexeme();
	}
	private static void advance() throws Exception {
		MicroMorphoFlex.advance();
	}

	public static void program() throws Exception {

			function();

		System.out.println("suxxxxess");
	}


	public static void function() throws Exception {
		System.out.println("tok1: " + getToken() + " lex1: " + getFirstLexeme());
		if (getToken() == NAME) {
			advance();
			if (getFirstLexeme().equals("(")) {
				advance();
				if (getToken() == NAME) {
					advance();
					while(getFirstLexeme().equals(",") && getNextToken() == NAME) {
						advance();
						advance();
					}
				}
				System.out.println("tok: " + getToken() + " lex: " + getFirstLexeme());
				if (getFirstLexeme().equals(")")) {
					advance();
					System.out.println("HAE!");
					if (getFirstLexeme().equals("{")) {
						advance();
					}
					while (getToken() == VAR) {
						decl();
						if (getFirstLexeme().equals(";")) {
							advance();
						}
						else {
							throw new Error("ekki í lagi 1");
						}
					}
					while (!getFirstLexeme().equals('}')) {
						expr();
						if (getFirstLexeme().equals(";")) {
							advance();
						}
						else {
							throw new Error("ekki í lagi 2");
						}
					}
					advance();
				}
				else {
					throw new Error("ekki í lagi 3");
				}
			}
			else {
				throw new Error("ekki í lagi 4");
			}
		}
		else {
			throw new Error("ekki í lagi 5");
		}
	}

	/*public static void function() throws Exception {
		//System.out.println("tok: " + getToken());
		//System.out.println("lex: " + getLexeme());
		if(getToken() == NAME){
			advance();
			System.out.println(getToken());
			if(getFirstLexeme().equals("(")){
				advance();
				while(getToken() == NAME){
					advance();

					if(getFirstLexeme().equals(",")){
						System.out.println(getToken());
						advance();
					} else { break;	}
				}
				System.out.println(getToken() + " " + getFirstLexeme());
				if(getFirstLexeme().equals(")")){
					advance();
					if(getFirstLexeme().equals("{")){
						advance();
						System.out.println(getToken() + " " + getFirstLexeme());
						while(getToken() != 0){
							if(getFirstLexeme().equals("}")){ advance(); }
							else if(getToken() == VAR){
								while(getToken() == VAR){
			//ATH eyjó hérna er kallað á decl!!

									decl();

									if(getFirstLexeme().equals(";")){
										advance();
									} else { throw new Error("expected ;, found "+ getFirstLexeme()); }
								}
							}
							else if(getToken() != 0){
								System.out.println("okokok");
								expr();

								if(getFirstLexeme().equals(";")){
									advance();
								} else { throw new Error("expected ;, found "+ getFirstLexeme()); }
							}
						}
					}
				}
			}
		}
	}*/
	//Vinnusvæði Matta ætla að henda í expr fallið
	public static void expr() throws Exception {
		if(getToken() == RETURN){
			advance();
			expr();
			System.out.println("hérna maður");
		}
		else if(getToken() == NAME){
			advance();
			if(getFirstLexeme().equals("=")){
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
		System.out.println("tok: "+getToken()+" lex: "+getFirstLexeme() );
		while(getToken() == OPERATOR){
			advance();
			smallexpr();
		}

	}

	public static void smallexpr() throws Exception {
		if(getToken()==NAME){
			advance();
		}
		else if(getToken()==NAME && getLexeme().equals('(')){
			advance();
			advance();
			if(!getFirstLexeme().equals(")") {
				expr();
				while(!getFirstLexeme().equals(")")){
					if(getFirstLexeme().equals(',')){
						advance();
					}
					else {
						// Throw Exception
					}
					expr();
				}
				advance();
			}
		}
		else if(getToken()==OPERATOR){
			advance();
			smallexpr();
		}
		else if(getToken()==LITERAL){
			advance();
		}
		else if(getFirstLexeme().equals('(')){
			advance();
			expr();
			if(getFirstLexeme().equals(')')){
				advance();
			}
			else {
				//Throw Exception;
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
				//Throw Exception
			}
		}
		else if(getToken()==WHILE){
			advance();
			expr();
			body();
		}
		else {
			// Throw Exception
		}
	}






	//Vinnusvæði Eyjó

	public static void decl() throws Exception {
		System.out.println(getToken() + " : " + getLexeme() + " next: "+ getNextToken());
		if (getToken() == VAR && getNextToken() == NAME) {
			advance();
			advance();
			System.out.println("eg virka " + getToken() + " lex: "+ getFirstLexeme());
			while(getFirstLexeme().equals(",")) {
				advance();
				if(getToken() == NAME){
					advance();
				}
				else {
					// Throw Exception
				}
			}
		}
		else {
			// THrow Exception
		}
		System.out.println(getToken() + " next2: "+ getNextToken());
	}

	public static void body() throws Exception {
		if (getFirstLexeme().equals("{")) {
			while (!getFirstLexeme().equals("}")) {
				expr();
				System.out.print("Expression í body búin!");
				if (getFirstLexeme().equals(";")) {
					advance();
				}
				else {
					// THrow Exception
				}
			}
			advance();
			System.out.println("Body búið!");
		}
		else {
			// Throw Exception
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
