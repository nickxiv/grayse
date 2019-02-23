import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class PP implements Types {

    static Lexeme Tree;
    static Lexeme CurrentLexeme;
    static Lexer  lexer;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        lexer = new Lexer();
        FileReader fr = new FileReader(fileName);
        lexer.Pbr = new PushbackReader(fr);

        CurrentLexeme = lexer.lex();

        if (CurrentLexeme.type == ERROR) {
            return;
        }
        Tree = program();
        prettyPrint(Tree);

        match(ENDOFFILE);
    }

    static void prettyPrint(Lexeme tree) {
        switch (tree.type) {
            case BLOCK:
                printBlock(tree);
                break;

            case LINE:
                printLine(tree);
                break;

            case VARLIST:
                printVarList(tree);
                break;

            case EXPRLIST:
                printExprList(tree);
                break;

            case ARRAY:
                printArray(tree);
                break; 

            case INTEGER:
                System.out.print(tree.value);
                break;

            case STRING:
                System.out.print("\"" + tree.value + "\"");
                break;

            case VARIABLE:
                System.out.print(tree.value);
                break;

            case UVARIABLE:
                printUVariable(tree);
                break;

            case PARENEXPR:
                printParenExpression(tree);
                break;

            case PLUS:
                System.out.print(" + ");
                break;

            case MINUS:
                System.out.print(" - ");
                break;

            case TIMES:
                System.out.print(" * ");
                break;

            case MOD:
                System.out.print(" % ");
                break;

            case GETS:
                System.out.print(" = ");
                break;

            case LESSTHAN:
                System.out.print(" < ");
                break;

            case GREATERTHAN:
                System.out.print(" > ");
                break;

            case DOT:
                System.out.print(".");
                break;

            case AND:
                System.out.print(" & ");
                break;

            case OR:
                System.out.print(" | ");
                break;

            case ISEQUALTO:
                System.out.print(" == ");
                break;

            case TRUE:
                System.out.print("true");
                break;

            case FALSE:
                System.out.print("false");
                break;

            case FUNCCALL:
                printFuncCall(tree);
                break;

            case CLASSPROP:
                printClassProp(tree);
                break;

            case ALLOBJPROPS:
                printAllObjProps(tree);
                break;

            case PROPDEF:
                printPropDef(tree);
                break;

            case IF:
                printIfStatement(tree);
                break;

            case ELSE:
                printElseStatement(tree);
                break;

            case CLOSURE:
                System.out.print("CLOSURE");
                break;

            case BUILTIN:
                System.out.print("BUILTIN");
                break;

            case ENDOFFILE:
                System.out.println("REACHED ENDOFFILE");
                System.exit(1);
                break;

            default:
                System.out.println(tree.type + " not implemented yet");
                break;
        }
    }

    static Lexeme match(String type) throws IOException { //returns lexeme for parser
        Lexeme returnLexeme = matchNoAdvance(type);
        if (returnLexeme.type != ERROR) advance();
        else {
            System.out.println(", expected: " + type);
            System.exit(1);
        }
        return returnLexeme;

    }

    static void advance() throws IOException {
        CurrentLexeme = lexer.lex();
    }

    static Lexeme matchNoAdvance(String type) {
        if(!check(type)) {
            System.out.print("illegal line: " + CurrentLexeme.lineNumber);
            return new Lexeme(ERROR, SYNTAX_ERROR, CurrentLexeme.lineNumber);
        }
        else return CurrentLexeme;
    }

    static boolean check(String type) {
        return CurrentLexeme.type == type;
    }

    static Lexeme cons(String type, Lexeme car, Lexeme cdr){
        return new Lexeme(type, car, cdr);
    }

    static Lexeme program() throws IOException {
        return block();
    }

    static Lexeme block() throws IOException {
        Lexeme l, b;
        l = line();
        if (blockPending()) {
            b = block();
        }
        else b = null;
        return cons(BLOCK, l, b);
    }

    static boolean blockPending() throws IOException {
        return linePending();
    }

    static Lexeme line() throws IOException {
        Lexeme curr, next;
        if (definitionPending()) curr = definition();
        else if (expressionPending()) curr = expression();
        else if (ifStatementPending()) curr = ifStatement();
        else if (whileStatementPending()) curr = whileStatement();
        else if (returnStatementPending()) curr = returnStatement();
        else {
            System.out.println("ERROR IN LINE FUNCTION");
            System.exit(1);
            return null;
        }
        match(SEMICOLON);
        if (linePending()) next = line();
        else next = null;
        return cons(LINE, curr, next);
    }

    static boolean linePending() throws IOException {
        return definitionPending() ||
        expressionPending() ||
        ifStatementPending() ||
        whileStatementPending() ||
        returnStatementPending();
    }

    static Lexeme definition() throws IOException {
        if (varDefinitionPending()) return varDefinition();
        if (funcDefinitionPending()) return funcDefinition();
        if (classDefinitionPending()) return classDefinition();
        else {
            System.out.println("ERROR IN DEFINITION FUNCTION");
            System.exit(1);
            return null;
        }
    }

    static boolean definitionPending() throws IOException {
        return varDefinitionPending() ||
        funcDefinitionPending() ||
        classDefinitionPending();
    }

    static Lexeme varDefinition() throws IOException {
        match(LET);
        return optVarAssignment();
    }

    static boolean varDefinitionPending() {
        return check(LET);
    }

    static Lexeme varList() throws IOException {
        Lexeme v, l;
        v = match(VARIABLE);
        if (check(COMMA)) {
            match(COMMA);
            l = varList();
        }
        else l = null;
        return cons(VARLIST, v, l);
    }

    static boolean varListPending() throws IOException {
        return check(VARIABLE);
    }

    static Lexeme optVarAssignment() throws IOException {
        Lexeme vars, expr;
        vars = varList();
        if (check(GETS)) {
            match(GETS);
            expr = expression();
        }
        else expr = null;
        return cons(OPTVARASSIGN, null, cons(GETS, vars, expr));
    }

    static Lexeme optVarList() throws IOException {
        if (varListPending()) return varList();
        else return null;
    }


    static Lexeme funcDefinition() throws IOException {
        Lexeme name, paramList, body;
        match(FUNC);
        name = match(VARIABLE);
        match(OPAREN);
        paramList = optVarList();
        match(CPAREN);
        match(OCURLY);
        body = block();
        match(CCURLY);
        return cons(FUNCDEF, name, cons(GLUE, paramList, body));
    }

    static boolean funcDefinitionPending() {
        return check(FUNC);
    }

    static Lexeme funcCall() throws IOException {
        match(OPAREN);
        Lexeme params = optExpressionList();
        match(CPAREN);
        return cons(FUNCCALL, params, null);
    }

    static boolean funcCallPending() {
        return check(OPAREN);
    }

    static Lexeme classDefinition() throws IOException {
        match(CLASS);
        Lexeme className = match(VARIABLE);
        match(OCURLY);
        Lexeme props = classProperties();
        match(CCURLY);
        return cons(CLASSDEF, className, props);
    }

    static boolean classDefinitionPending() throws IOException {
        return check(CLASS);
    }

    static Lexeme classProperties() throws IOException {
        Lexeme prop = match(VARIABLE);
        match(SEMICOLON);
        Lexeme next;
        if(classPropertiesPending()) next = classProperties();
        else next = null;
        return cons(CLASSPROP, prop, next);
    }

    static boolean classPropertiesPending() throws IOException {
        return check(VARIABLE);
    }

    static Lexeme returnStatement() throws IOException {
        match(RETURN);
        Lexeme returns = optExpressionList();
        return cons(RETURN, returns, null);
    }

    static boolean returnStatementPending() {
        return check(RETURN);
    }

    static Lexeme ifStatement() throws IOException {
        match(IF);
        match(OPAREN);
        Lexeme cond = expression();
        match(CPAREN);
        match(OCURLY);
        Lexeme body = block();
        match(CCURLY);
        Lexeme optElse = optElse();

        return cons(IF, cond, cons(GLUE, body, optElse));
    }

    static boolean ifStatementPending() throws IOException {
        return check(IF);
    }

    static Lexeme whileStatement() throws IOException {
        Lexeme cond, body;
        match(WHILE);
        match(OPAREN);
        cond = expression();
        match(CPAREN);
        match(OCURLY);
        body = block();
        match(CCURLY);
        return cons(WHILE, cond, body);

    }

    static boolean whileStatementPending() throws IOException {
        return check(WHILE);
    }

    static Lexeme optElse() throws IOException {
        if (check(ELSE)) {
            match(ELSE);
            if (check(OCURLY)) {
                match(OCURLY);
                Lexeme block = block();
                match(CCURLY);
                return cons(ELSE, block, null);
            }
            else {
                return ifStatement();
            }
        }
        else return null;
    }

    // static void expr3() {
    //     Lexeme u, e;
    //     u = unary();
    //     while (check(DOT)) {
    //         match(DOT);
    //         e = unary();
    //         u = cons(DOT, u, e);
    //     }
    //     return u;
    // }

    // static void expr2() {
    //     Lexeme u, e;
    //     u = unary();
    // }

    // static void expr() {
    //     Lexeme u, e;
    //     u = unary();
    //     if (check(GETS)) {
    //         match(GETS);
    //         e = expr();
    //     }
    // }

    static Lexeme expression() throws IOException { // todo: replace this with the three functions
        Lexeme u, e, op;
        u = unary();
        if (operatorPending()) {
            op = operator();
            e = expression();
            return cons(op.type, u, e);
        }
        return u;
    }

    static boolean expressionPending() throws IOException {
        return unaryPending();
    }

    static Lexeme objProperties() throws IOException {
        Lexeme prop = null;
        Lexeme next;
        if (propertyDefinitionPending()) {
            prop = propertyDefinition();
            next = objProperties();
        }
        else return null;
        return cons(OBJPROP, prop, next);
    }

    static Lexeme propertyDefinition() throws IOException {
        Lexeme objName = match(VARIABLE);
        match(COLON);
        Lexeme val = unary();
        match(SEMICOLON);
        return cons(PROPDEF, objName, val);
    } 

    static boolean propertyDefinitionPending() throws IOException {
        return check(VARIABLE);
    }

    static Lexeme unary() throws IOException{
        if (check(INTEGER)) return match(INTEGER);
        else if (check(REAL)) return match(REAL);
        else if (uVariablePending())  return uVariable();
        else if (check(TRUE)) return match(TRUE);
        else if (check(FALSE)) return match(FALSE);
        else if (check(STRING)) return match(STRING);
        else if (check(MINUS)) {
            match(MINUS);
            Lexeme u = unary();
            return cons(UMINUS, u, null);
        }
        else if (arrayPending()) return array();
        else if (check(OPAREN)) {
            match(OPAREN);
            Lexeme expr = expression();
            match(CPAREN);
            return cons(PARENEXPR, expr, null);
        }
        else if (check(OCURLY)) {
            match(OCURLY);
            Lexeme op = objProperties();
            match(CCURLY);
            return cons(ALLOBJPROPS, op, null);
        } 
        else if (check(NOT)) {          //FIX THIS
            match(NOT);
            return cons(NOT,null, null);

            // match(NOT);
            // return expression();
        }
        else {
            System.out.println("ERROR IN UNARY");
            System.exit(1);
            return null;
        }
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

    static Lexeme array() throws IOException {
        Lexeme elements;
        match(OBRACKET);
        elements = optExpressionList();
        match(CBRACKET);
        return cons(ARRAY, elements, null);
    }

    static boolean arrayPending() throws IOException {
        return check(OBRACKET);
    }

    static Lexeme optExpressionList() throws IOException {
        if (expressionListPending()) return expressionList();
        return null;
    }

    static Lexeme expressionList() throws IOException {
        Lexeme expr = expression();
        Lexeme el;
        if (check(COMMA)) {
            match(COMMA);
            el = expressionList();
        }
        else el = null;
        return cons(EXPRLIST, expr, el);

    }

    static boolean expressionListPending() throws IOException {
        return expressionPending();
    }


    static Lexeme uVariable() throws IOException {
        Lexeme var, funcCall;
        var = match(VARIABLE);
        if(funcCallPending())  funcCall = funcCall();
        else funcCall = null;
        return cons(UVARIABLE, var, funcCall);
    }

    static boolean uVariablePending() throws IOException {
        return check(VARIABLE);
    }


    static Lexeme operator() throws IOException {
        if (check(PLUS)) return match(PLUS);
        else if (check(MINUS)) return match(MINUS);
        else if (check(TIMES)) return match(TIMES);
        else if (check(DIVIDES)) return match(DIVIDES);
        else if (check(MOD)) return match(MOD);
        else if (check(AND)) return match(AND);
        else if (check(OR)) return match(OR);
        else if (check(LESSTHAN)) return match(LESSTHAN);
        else if (check(GREATERTHAN)) return match(GREATERTHAN);
        else if (check(ISEQUALTO)) return match(ISEQUALTO);
        else if (check(GETS)) return match(GETS);
        else if (check(DOT)) return match(DOT);
        else {
            System.out.println("ERROR IN OPERATOR FUNCTION");
            System.exit(1);
            return null;
        }
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

    static void printProgram(Lexeme tree) {
        printBlock(tree);
    }

    static void printBlock(Lexeme tree) {
        tree = tree.car();
        while (tree != null) {
            prettyPrint(tree);
            tree = tree.cdr();
        }
    }

    static void printUVariable(Lexeme tree) {
        prettyPrint(tree.car());
        if(tree.cdr() != null) prettyPrint(tree.cdr());
    }

    static void printLine(Lexeme tree) {
        tree = tree.car();
        switch (tree.type) {
            case OPTVARASSIGN:
                printOVA(tree);
                break;

            case FUNCDEF:
                printFuncDefinition(tree);
                break;

            case UVARIABLE:
                printUVariable(tree);
                break;

            case RETURN:
                printReturnStatement(tree);
                break;

            case GETS:
                printExpression(tree);
                break;


            case PLUS:
                printExpression(tree);
                break;

            case TIMES:
                printExpression(tree);
                break;

            case DOT:
                printExpression(tree);
                break;

            case IF:
                printIfStatement(tree);
                break;

            case WHILE:
                printWhileStatement(tree);
                break;

            case CLASSDEF:
                printClassDef(tree);
                break;

            default:
                System.out.println("DEFAULT IN PRINTLINE FOR TYPE " + tree.type);
                break;
        }
        System.out.println(";");
    }

    static void printVarList(Lexeme tree) {
        while (tree != null) {
            prettyPrint(tree.car());
            tree = tree.cdr();
            if (tree != null) System.out.print(", ");
        }
    }

    static void printExprList(Lexeme tree) {
        while (tree != null) {
            prettyPrint(tree.car());
            if(tree.cdr != null) System.out.print(", ");
            tree = tree.cdr();
        }
    }

    static void printExpression(Lexeme tree) {
        if (tree.type == ARRAY) printArray(tree);
        else if (tree.type == ALLOBJPROPS) printAllObjProps(tree);
        else {
           while (tree != null && tree.value == null && tree.type != FUNCCALL) {
            if ((tree.cdr() == null || tree.cdr().type != FUNCCALL) && tree.type != ARRAY) prettyPrint(tree.car());
            if (tree.type == ARRAY || tree.cdr() != null) prettyPrint(tree);
            tree = tree.cdr();
        }
        if (tree != null && tree.type != FUNCCALL) prettyPrint(tree);
        }
    }

    static void printOVA(Lexeme tree) {
        tree = tree.cdr();
        System.out.print("let ");
        prettyPrint(tree.car());
        if (tree.cdr() == null) return;
        prettyPrint(tree);              //GETS
        tree = tree.cdr();
        // while (tree != null && tree.value == null) {
        //     if(tree.type != ARRAY) prettyPrint(tree.car());
        //     prettyPrint(tree);
        //     tree = tree.cdr();
        // }
        // if (tree != null) prettyPrint(tree);
        // if (tree.type == OBJPROPS) printObjProps(tree);
        printExpression(tree);
    }

    static void printIfStatement(Lexeme tree) {
        System.out.print("if (");
        printExpression(tree.car());
        System.out.println(") {");
        prettyPrint(tree.cdr().car());          //cdr = GLUE, cdr.car = block
        System.out.print("}");
        if (tree.cdr().cdr() != null) {
            System.out.print(" else ");
            prettyPrint(tree.cdr().cdr());
        }
    }

    static void printElseStatement(Lexeme tree) {
        System.out.println(" {");
        prettyPrint(tree.car());
        System.out.print("}");
    }

    static void printWhileStatement(Lexeme tree) {
        System.out.print("while (");
        printExpression(tree.car());
        System.out.println(") {");
        prettyPrint(tree.cdr());
        System.out.print("}");
    }

    static void printArray(Lexeme tree) {
        System.out.println("\n[");
        tree = tree.car();
        while (tree != null && tree.type == EXPRLIST) {
            prettyPrint(tree.car());
            if(tree.cdr() != null) System.out.print(", \n");
            tree = tree.cdr();
        }
        System.out.print("\n]");
    }

    static void printParenExpression(Lexeme tree) {
        System.out.print("(");
        printExpression(tree.car());
        System.out.print(")");
    }

    static void printFuncDefinition(Lexeme tree) {
        System.out.print("func ");
        prettyPrint(tree.car());
        System.out.print("(");
        if (tree.cdr().car() != null) prettyPrint(tree.cdr().car());
        System.out.println(") {");
        prettyPrint(tree.cdr().cdr());
        System.out.print("}");
    }

    static void printReturnStatement(Lexeme tree) {
        System.out.print("return ");
        if (tree.car() != null) prettyPrint(tree.car());
    }

    static void printFuncCall(Lexeme tree) {
        System.out.print("(");
        if (tree.car() != null) prettyPrint(tree.car());
        System.out.print(")");
    }

    static void printClassDef(Lexeme tree) {
        System.out.print("class ");
        prettyPrint(tree.car());
        System.out.println(" {");
        prettyPrint(tree.cdr());
        System.out.print("}");
    }

    static void printClassProp(Lexeme tree) {
        while (tree != null) {
            prettyPrint(tree.car());
            System.out.println(";");
            tree = tree.cdr();
        }
    }

    static void printAllObjProps(Lexeme tree) {
        System.out.println(" {");
        tree = tree.car();
        while (tree != null) {
            prettyPrint(tree.car());
            tree = tree.cdr();
        }
        System.out.print("}");
    }

    static void printPropDef(Lexeme tree) {
        prettyPrint(tree.car());
        System.out.print(": ");
        prettyPrint(tree.cdr());
        System.out.println(";");
    }
}