//Written by Nick Martin for CS403 - Programming Languages

import java.io.FileReader;
import java.io.PushbackReader;

import java.io.IOException;

public class Recognizer implements Types {

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
            return;
        }
        System.out.print(CurrentLexeme.type);

        if (CurrentLexeme.value != null) System.out.print(" " + CurrentLexeme.value);
        System.out.println();

        while(CurrentLexeme.type != ENDOFFILE) {
            CurrentLexeme = lexer.lex();

            if (CurrentLexeme.type == ERROR) {
                handleError();
                return;
            }

            System.out.print(CurrentLexeme.type);
            if (CurrentLexeme.value != null) System.out.print(" " + CurrentLexeme.value);
            System.out.println();
        }
    }

    public static void handleError() {
        System.out.print("\n\n\n");
        if (CurrentLexeme.value == BAD_NUMBER)      System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad number detected");
        else if (CurrentLexeme.value == BAD_VARIABLE) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad variable name detected");
        else if (CurrentLexeme.value == BAD_STRING) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Bad string detected");
        else if (CurrentLexeme.value == SYNTAX_ERROR) System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Syntax error");
        else System.out.println("ERROR line " + CurrentLexeme.lineNumber + ": Unknown error detected");
    }

    boolean check(String type) {
        return CurrentLexeme.type == type;
    }

    void match(String type) throws IOException {
        matchNoAdvance(type);
        advance();
    }

    void advance() throws IOException {
        CurrentLexeme = lexer.lex();
    }

    void matchNoAdvance(String type) {
        if(!check(type)) {
            int errorLine = CurrentLexeme.lineNumber;
            CurrentLexeme = new Lexeme(ERROR, SYNTAX_ERROR, errorLine);
        }
    }
    
}
