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
        program();
        match(ENDOFFILE);
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

    static boolean check(String type) {
        return CurrentLexeme.type == type;
    }

    static void match(String type) throws IOException {
        matchNoAdvance(type);
        if (CurrentLexeme.type != ERROR) advance();
        else {
             System.out.println(", expected: " + type);
            System.exit(1);
        } 
    }

    static void advance() throws IOException {
        CurrentLexeme = lexer.lex();
    }

    static void matchNoAdvance(String type) {
        if(!check(type)) {
            System.out.print("illegal line: " + CurrentLexeme.lineNumber);
            CurrentLexeme = new Lexeme(ERROR, SYNTAX_ERROR, CurrentLexeme.lineNumber);
        }
    }
    
    static void program() throws IOException{
        block();
    }

    static void block() throws IOException {
        line();
        match(SEMICOLON);
        if (blockPending()) block();
    }

    static boolean blockPending() throws IOException {
        return linePending();
    }

    static void line() throws IOException {
        if (definitionPending()) definition();
        else if (expressionPending()) expression();
        else if (ifStatementPending()) ifStatement();
        else if (whileStatementPending()) whileStatement();
        else if (returnStatementPending()) returnStatement();
    }

    static boolean linePending() throws IOException {
        return 
        definitionPending() || 
        expressionPending() || 
        ifStatementPending() ||
        whileStatementPending() ||
        returnStatementPending();
    }

    static void definition() throws IOException {
        if (varDefinitionPending()) varDefinition();
        else if (funcDefinitionPending()) funcDefinition();
        else classDefinition();
    }

    static boolean definitionPending() throws IOException {
        return
        varDefinitionPending() ||
        funcDefinitionPending() ||
        classDefinitionPending();
    }

    static void varDefinition() throws IOException {
        match(LET);
        optVarAssignment();
    }

    static boolean varDefinitionPending() throws IOException {
        return check(LET);
    }

    static void varList() throws IOException {
        match(VARIABLE);
        if (check(COMMA)) {
            match(COMMA);
            varList();
        }
    }

    static boolean varListPending() throws IOException {
        return check(VARIABLE);
    }

    static void optVarAssignment() throws IOException {
        varList();
        if (check(GETS)) {
            match(GETS);
            expression();
        }
    }

    static boolean optVarAssignmentPending() throws IOException {
        return varListPending();
    }

    static void funcDefinition() throws IOException {
        match(FUNC);
        match(VARIABLE);
        match(OPAREN);
        optVarList();
        match(CPAREN);
        match(OCURLY);
        block();
        match(CCURLY);
    }

    static boolean funcDefinitionPending() throws IOException {
        return check(FUNC);
    }

    static void optVarList() throws IOException {
        if (varListPending()) varList();
    }

    static void funcCall() throws IOException {
        match(OPAREN);
        optExpressionList();
        match(CPAREN);
    }

    static boolean funcCallPending() throws IOException {
        return check(OPAREN);
    }

    static void classDefinition() throws IOException {
        match(CLASS);
        match(VARIABLE);
        match(OCURLY);
        classProperties();
        match(CCURLY);
    }

    static boolean classDefinitionPending() throws IOException {
        return check(CLASS);
    }

    static void classProperties() throws IOException {
        match(VARIABLE);
        match(SEMICOLON);
        if (classPropertiesPending()) classProperties();
    }

    static boolean classPropertiesPending() throws IOException {
        return check(VARIABLE);
    }

    static void objProperties() throws IOException {
        if (propertyDefinitionPending()) {
            propertyDefinition();
            objProperties();
        }
    }

    static void propertyDefinition() throws IOException {
        match(VARIABLE);
        match(COLON);
        unary();
        match(SEMICOLON);
    }

    static boolean propertyDefinitionPending() throws IOException {
        return check(VARIABLE);
    }

    static void ifStatement() throws IOException {
        match(IF);
        match(OPAREN);
        expression();
        match(CPAREN);
        match(OCURLY);
        block();
        match(CCURLY);
        optElse();
    }
    
    static boolean ifStatementPending() throws IOException {
        return check(IF);
    }

    static void optElse() throws IOException {
        if (check(ELSE)) {
            match(ELSE);
            if(check(OCURLY)) {
                match(OCURLY);
                block();
                match(CCURLY);
            }
            else {
                ifStatement();
            }
        }
    }

    static void array() throws IOException {
        match(OBRACKET);
        optExpressionList();
        match(CBRACKET);
    }

    static boolean arrayPending() throws IOException {
        return check(OBRACKET);
    }

    static void whileStatement() throws IOException {
        match(WHILE);
        match(OPAREN);
        expression();
        match(CPAREN);
        match(OCURLY);
        block();
        match(CCURLY);
    }
    
    static boolean whileStatementPending() throws IOException {
        return check(WHILE);
    }

    static void returnStatement() throws IOException {
        match(RETURN);
        optExpressionList();
    }

    static boolean returnStatementPending() throws IOException {
        return check(RETURN);
    }

    static void optExpressionList() throws IOException {
        if (expressionListPending()) expressionList();
    }

    static void expressionList() throws IOException {
        expression();
        if (check(COMMA)) {
            match(COMMA);
            expressionList();
        }
    }

    static boolean expressionListPending() throws IOException {
        return expressionPending();
    }

    static void expression() throws IOException {
        unary();
        if (operatorPending()) {
            operator();
            expression();
        }
    }

    static boolean expressionPending() throws IOException {
        return unaryPending();
    }

    static void unary() throws IOException{
        if (check(INTEGER)) match(INTEGER);
        else if (check(REAL)) match(REAL);
        else if (uVariablePending())  uVariable();
        else if (check(TRUE)) match(TRUE);
        else if (check(FALSE)) match(FALSE);
        else if (check(STRING)) match(STRING);
        else if (check(MINUS)) {
            match(MINUS);
            unary();
        }
        else if (arrayPending()) array();
        else if (check(OPAREN)) {
            match(OPAREN);
            expression();
            match(CPAREN);
        }
        else if (check(OCURLY)) {
            match(OCURLY);
            objProperties();
            match(CCURLY);
        } 
        else if (check(NOT)) {
            match(NOT);
            expression();
        }
    }

    static void uVariable() throws IOException {
        match(VARIABLE);
        if(funcCallPending()) funcCall();
    }

    static boolean uVariablePending() throws IOException {
        return check(VARIABLE);
    }

    static boolean unaryPending() throws IOException {
        return
        check(INTEGER) ||
        check(REAL) ||
        uVariablePending() ||
        check(TRUE) ||
        check(FALSE) ||
        check(STRING) ||
        funcCallPending() ||
        check(MINUS) ||
        arrayPending() ||
        check(OPAREN) ||
        check(OCURLY);
    }

    static void operator() throws IOException {
        if (check(PLUS)) match(PLUS);
        else if (check(MINUS)) match(MINUS);
        else if (check(TIMES)) match(TIMES);
        else if (check(DIVIDES)) match(DIVIDES);
        else if (check(MOD)) match(MOD);
        else if (check(AND)) match(AND);
        else if (check(OR)) match(OR);
        else if (check(LESSTHAN)) match(LESSTHAN);
        else if (check(GREATERTHAN)) match(GREATERTHAN);
        else if (check(ISEQUALTO)) match(ISEQUALTO);
        else if (check(GETS)) match(GETS);
        else if (check(DOT)) match(DOT);
    }

    static boolean operatorPending() throws IOException {
        return
        check(PLUS) ||
        check(MINUS) ||
        check(TIMES) ||
        check(DIVIDES) ||
        check(MOD) ||
        check(AND) ||
        check(OR) ||
        check(NOT) ||
        check(LESSTHAN) ||
        check(GREATERTHAN) ||
        check(ISEQUALTO) ||
        check(GETS) ||
        check(DOT);
    }
}
