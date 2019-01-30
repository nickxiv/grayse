//Written by Nick Martin for CS403 - Programming Languages

import java.io.FileReader;
import java.io.PushbackReader;

import java.io.IOException;

public class Scanner implements Types {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        Lexer lexer = new Lexer();

        FileReader fr = new FileReader(fileName);
        lexer.Pbr = new PushbackReader(fr);

        Lexeme lexeme = lexer.lex();

        if (lexeme.type == ERROR) {
            handleError(lexeme);
            return;
        }
        System.out.print(lexeme.type);

        if (lexeme.value != null) System.out.print(" " + lexeme.value);
        System.out.println();

        while(lexeme.type != ENDOFFILE) {
            lexeme = lexer.lex();

            if (lexeme.type == ERROR) {
                handleError(lexeme);
                return;
            }

            System.out.print(lexeme.type);
            if (lexeme.value != null) System.out.print(" " + lexeme.value);
            System.out.println();
        }
    }

    static void handleError(Lexeme erroredLexeme) {
        System.out.print("\n\n\n");
        if (erroredLexeme.value == BAD_NUMBER)      System.out.println("ERROR line " + erroredLexeme.lineNumber + ": Bad number detected");
        else if (erroredLexeme.value == BAD_VARIABLE) System.out.println("ERROR line " + erroredLexeme.lineNumber + ": Bad variable name detected");
        else if (erroredLexeme.value == BAD_STRING) System.out.println("ERROR line " + erroredLexeme.lineNumber + ": Bad string detected");
        else System.out.println("ERROR line " + erroredLexeme.lineNumber + ": Unknown error detected");
        System.exit(1);
    }
}
