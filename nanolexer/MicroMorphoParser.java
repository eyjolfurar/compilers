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

			while (getToken() != 0) {
				function();
			}
	}


	public static void function() throws Exception {
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
				if (getFirstLexeme().equals(")")) {
					advance();
					if (getFirstLexeme().equals("{")) {
						advance();
					}
					while (getToken() == VAR) {
						decl();
						if (getFirstLexeme().equals(";")) {
							advance();
						}
						else {
							throw new Error("ekki i lagi 1 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
						}
					}
					while (!getFirstLexeme().equals("}")) {
						expr();
						if (getFirstLexeme().equals(";")) {
							advance();
						}

						else {
							throw new Error("ekki i lagi 2 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
						}
					}
					advance();
				}
				else {
					throw new Error("ekki i lagi 3 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
				}
			}
			else {
				throw new Error("ekki i lagi 4 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
			}
		}
		else {
			throw new Error("ekki i lagi 5 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
		}
	}

	public static void expr() throws Exception {
		if(getToken() == RETURN){
			advance();
			expr();
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
		while(getToken() == OPERATOR){
			advance();
			smallexpr();
		}

	}

	public static void smallexpr() throws Exception {
		if(getToken()==NAME){
			advance();
		}
		else if(getToken()==NAME && getLexeme().equals("(")){
			advance();
			advance();
			if(!getFirstLexeme().equals(")")) {
				expr();
				while(!getFirstLexeme().equals(")")){
					if(getFirstLexeme().equals(",")){
						advance();
					}
					else {
						throw new Error("ekki i lagi 6 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
					}
					expr();
				}
				advance();
			}
			advance();
		}
		else if(getToken()==OPERATOR){
			advance();
			smallexpr();
		}
		else if(getToken()==LITERAL){
			advance();
		}
		else if(getFirstLexeme().equals("(")){
			advance();
			expr();
			if(getFirstLexeme().equals(")")){
				advance();
			}
			else {
				throw new Error("ekki i lagi 7 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
			}
		}
		else if(getToken()==IF){
			System.out.println("læakjsdf");
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
				throw new Error("ekki i lagi 8 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
			}
		}
		else if(getToken()==WHILE){
			advance();
			expr();
			body();
		}
		else {
			throw new Error("ekki i lagi 9 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
		}
	}


	public static void decl() throws Exception {

		if (getToken() == VAR && getNextToken() == NAME) {
			advance();
			advance();

			while(getFirstLexeme().equals(",")) {
				advance();
				if(getToken() == NAME){
					advance();
				}
				else {
					throw new Error("ekki i lagi 10 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
				}
			}
		}
		else {
			throw new Error("ekki i lagi 11 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
		}
	}

	public static void body() throws Exception {
		if (getFirstLexeme().equals("{")) {
			while (!getFirstLexeme().equals("}")) {
				expr();
				if (getFirstLexeme().equals(";")) {
					advance();
				}
				else {
					throw new Error("ekki i lagi 12 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
				}
			}
			advance();
		}
		else {
			throw new Error("ekki i lagi 13 nalaegt: "+ getFirstLexeme() + " og " + getLexeme());
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
