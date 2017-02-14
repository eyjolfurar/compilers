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
	        program();
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

	public static void program() throws Exception{
		
		while(getToken() != 0){
			function();
			MicroMorphoFlex.advance();
		}
	}

	public static void function(){
	

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
			|	NAME, '(', [ expr, { ',', expr } ], ')'
			|	OPNAME, smallexpr
			| 	LITERAL 
			|	'(', expr, ')'
			|	'if', expr, body, { 'elsif', expr, body }, [ 'else', body ]
			|	'while', expr, body
			;

body		=	'{', { expr, ';' }, '}'
			;
			*/