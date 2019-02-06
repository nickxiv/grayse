//Written by Nick Martin for CS403 - Programming Languages

import java.io.FileReader;
import java.io.PushbackReader;

import java.io.IOException;

public class Enviroments implements Types {

    static Lexeme CurrentLexeme;
    static Lexer lexer;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        lexer = new Lexer();

        FileReader fr = new FileReader(fileName);
        lexer.Pbr = new PushbackReader(fr);

        CurrentLexeme = lexer.lex();

        if (CurrentLexeme.type == ERROR) {
            handleError();
            System.exit(1);
        }
        System.out.println("legal");
        System.exit(0);
    }

    public static void handleError() {
        System.out.print("\n\n\n");
        if (CurrentLexeme.value == BAD_NUMBER) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad number detected");
        else if (CurrentLexeme.value == BAD_VARIABLE) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad variable name detected");
        else if (CurrentLexeme.value == BAD_STRING) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad string detected");
        else if (CurrentLexeme.value == SYNTAX_ERROR) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Syntax error");
        else System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Unknown error detected");
    }

    //Note that we are using a typed cons function. It returns a lexeme whose left pointer is the second argument and whose right pointer is the third argument and whose type is the first argument. 

    static Lexeme cons(String type, Lexeme car, Lexeme cdr){
        return new Lexeme(type, car, cdr);
    }

    static Lexeme extend(Lexeme variables, Lexeme values, Lexeme env) {
        return cons(ENV, variables, cons(ENV, values, env));
    }

    /*
        function create()
        {
        return cons(ENV,null,cons(VALUES,null,null));
        }
    */
    static Lexeme create() {
        return cons(ENV,null,cons(VALUES,null,null));
    }
}
