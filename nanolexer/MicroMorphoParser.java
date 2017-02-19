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
	        while(getToken() != 0){
	        	System.out.println("token 1: " + getToken() + " lex : " + getFirstLexeme() + "");
	        	System.out.println("token 2: " + getNextToken() + " lex2 : " + getLexeme() + "");
	        	program();
	        	
	        	advance();
	        	
	        }
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
									System.out.println("fínt maður");
									advance();
									if(getFirstLexeme().equals(";")){
										advance();
									} else { throw new Error("expected ;, found "+ getFirstLexeme()); }
								}
							}
							else if(getToken() != 0){
								System.out.println("okokok");
								advance();
								if(getFirstLexeme().equals(";")){
									advance();
								} else { throw new Error("expected ;, found "+ getFirstLexeme()); }
							}	
						}
					}
				}
			}
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