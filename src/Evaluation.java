import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class Evaluation implements Types {
    static int argcCL;
    static String[] argsCL;
    static Lexer lexer;
    static Lexeme CurrentLexeme;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        argsCL = args;
        argcCL = args.length;
        lexer = new Lexer();
        FileReader fr = new FileReader(fileName);
        PushbackReader pr = new PushbackReader(fr);
        lexer.Pbr = new PushbackReader(pr);

        CurrentLexeme = lexer.lex();

        if (CurrentLexeme.type == ERROR) {
            return;
        }

        Lexeme Tree;
        Lexeme e = Environments.create();

        Tree = program();
        eval(Tree, e);

        match(ENDOFFILE);
    }

    static Lexeme eval(Lexeme tree, Lexeme env) throws IOException {
        if (tree == null)
            return null;
        switch (tree.type) {
        // self evaluating
        case INTEGER:
        case REAL:
        case STRING:
            return tree;

        // find value of variables in the environment
        case VARIABLE:
            return Environments.lookup(tree.value.toString(), env);

        // parenthesized expression
        case PARENEXPR:
            return eval(tree.car(), env);

        // operators (both sides evaluated)
        case PLUS:
        case MINUS:
        case TIMES:
        case DIVIDES:
        case MOD:
        case ISEQUALTO:
        case DOESNOTEQUAL:
            // ...
        case LESSTHAN:
        case GREATERTHAN:
            return evalSimpleOp(tree, env);

        // AND and OR short-circuit
        case AND:
        case OR:
            return evalShortCircuitOp(tree, env);

        // classes and objects

        case CLASSDEF:
            return evalClassDef(tree, env);
        case CLASSPROP:
            return evalClassProp(tree, env);
        //dot operator evals lhs, rhs a variable
        case DOT: return evalDot(tree,env);
        //assign operator evals rhs for sure
        // lhs is a variable or a dot operation


        case GETS:
            return evalAssign(tree, env);

        // //variable and function definitions
        case OPTVARASSIGN:
            return evalVarDef(tree, env);
        case FUNCDEF:
            return evalFuncDef(tree, env);
        case LAMBDA:
            return evalLambda(tree, env);

        // //imperative constructs
        case IF:
            return evalIf(tree, env);
        case WHILE:
            return evalWhile(tree, env);
        case ELSE:
            return evalElse(tree, env);

        // //function calls
        case FUNCCALL:
            return evalFuncCall(tree, env);

        // //program and function body are parsed as blocks
        case BLOCK:
            return evalBlock(tree, env);
        case LINE:
            return evalLine(tree, env);

        // others
        case UVARIABLE:
            return evalUVariable(tree, env);
        case EXPRLIST:
            return evalExprList(tree, env);
        case TRUE:
            return new Lexeme(INTEGER, 1, tree.lineNumber);
        case FALSE:
            return new Lexeme(INTEGER, 0, tree.lineNumber);
        case RETURN:
            return eval(tree.car(), env);
        default:
            System.out.println("IN EVAL, TYPE ISN'T IMPLEMENETED YET: " + tree.type);
            System.exit(1);
            return null;
        }
    }

    static Lexeme evalBlock(Lexeme t, Lexeme env) throws IOException {
        Lexeme result = null;
        t = t.car();
        while (t != null) {
            result = eval(t, env);
            t = t.cdr();
        }
        return result;
    }

    static Lexeme evalLine(Lexeme t, Lexeme env) throws IOException {
        Lexeme result = null;
        t = t.car();
        result = eval(t, env);
        return result;
    }

    static Lexeme evalFuncDef(Lexeme t, Lexeme env) {
        Lexeme closure = cons(CLOSURE, env, t);
        Environments.insert(t.car(), closure, env);
        return closure;
    }

    static Lexeme evalLambda(Lexeme t, Lexeme env) {
        return cons(CLOSURE, env, t);
    }

    static Lexeme evalFuncCall(Lexeme t, Lexeme env) throws IOException {
        String name = t.car.value.toString();
        Lexeme args = t.cdr(); // args passed into func call
        Lexeme eargs = null;
        if (args != null) {
            eargs = eval(args, env); // evaluation of args over calling environment
        }

        // BUILTIN FUNCTIONS
        if (name.equals("println"))
            return evalPrintLn(eargs);
        if (name.equals("print"))
            return evalPrint(eargs);
        if (name.equals("argc"))
            return evalGetArgCount();
        if (name.equals("argv"))
            return evalGetArg(eargs);
        if (name.equals("openFileForReading"))
            return evalOpenFileForReading(eargs);
        if (name.equals("readInteger"))
            return evalReadInteger(eargs);
        if (name.equals("atFileEnd"))
            return evalAtFileEnd(eargs);
        if (name.equals("closeFile"))
            return evalCloseFile(eargs);
        if (name.equals("newArray"))
            return evalNewArray(eargs);
        if (name.equals("getArray"))
            return evalGetArray(eargs);
        if (name.equals("setArray"))
            return evalSetArray(eargs);


        Lexeme closure = eval(t.car(), env); // eval t.car() looks up func name in environment and returns the closure
        if (closure == null) {
            System.out.println("ERROR line " + t.lineNumber + ": function " + name + " not defined");
            System.exit(1);
            return null;
        }
        if (closure.type == OCLOSURE)
            return evalConstructor(closure, env);

        Lexeme params = closure.cdr().cdr().car(); // formal params of funcdef
        Lexeme body = closure.cdr().cdr().cdr(); // body of func def
        Lexeme senv = closure.car(); // environment of funcdef
        Lexeme xenv = Environments.extend(params, eargs, senv); // environment of func definition extended with formal params set to values of evaluated args
        Environments.insert(new Lexeme(STRING, "this", args.lineNumber), xenv, xenv);

        return eval(body, xenv);
    }

    static Lexeme evalConstructor(Lexeme closure, Lexeme env) throws IOException {
        Lexeme senv = closure.car();
        Lexeme xenv = Environments.extend(null, null, senv);
        Lexeme body = closure.cdr().cdr();
        eval(body, xenv);
        return xenv;
    }

    static Lexeme evalVarDef(Lexeme t, Lexeme env) throws IOException {
        t = t.cdr();
        Lexeme value = (t.cdr() != null ? eval(t.cdr(), env) : null);
        Lexeme variables = t.car();
        while (variables != null) {
            Environments.insert(variables.car(), value, env);
            variables = variables.cdr();
        }
        return value;
    }

    static Lexeme evalClassDef(Lexeme t, Lexeme env) {
        Environments.insert(t.car(), cons(OCLOSURE, env, t), env);
        return cons(OCLOSURE, env, t);
    }

    static Lexeme evalClassProp(Lexeme t, Lexeme env) {
        while (t != null) {
            Environments.insert(t.car(), null, env);
            t = t.cdr();
        }
        return env;
    }

    static Lexeme evalDot(Lexeme t, Lexeme env) throws IOException {
        Lexeme obj = eval(t.car(), env);
        Lexeme att = eval(t.cdr(), obj);
        return att;
    }

    static Lexeme evalAssign(Lexeme t, Lexeme env) throws IOException {
        Lexeme value = (t.cdr() != null ? eval(t.cdr(), env) : null);
        Lexeme variables = t.car();

        while (variables != null) {
            String varName = variables.car().value.toString();
                Environments.update(varName, value.value, env);
                variables = variables.cdr();
        }
        return value;

    }

    static Lexeme evalIf(Lexeme t, Lexeme env) throws IOException {
        Lexeme cond = eval(t.car(), env);
        if (cond.type == FALSE) {
            if (t.cdr().cdr() != null)
                return eval(t.cdr().cdr(), env);
            else
                return null;
        }
        Lexeme block = eval(t.cdr().car(), env);
        return block;
    }

    static Lexeme evalElse(Lexeme t, Lexeme env) throws IOException {
        return eval(t.car(), env);
    }

    static Lexeme evalWhile(Lexeme t, Lexeme env) throws IOException {
        Lexeme cond = eval(t.car(), env);
        if (cond.type == FALSE)
            return null;
        Lexeme block = null;
        while (cond.type == TRUE) {
            block = eval(t.cdr(), env);
            cond = eval(t.car(), env);
        }
        return block;
    }

    static Lexeme evalSimpleOp(Lexeme t, Lexeme env) throws IOException {
        if (t.type == PLUS)
            return evalPlus(t, env);
        if (t.type == MINUS)
            return evalMinus(t, env);
        if (t.type == TIMES)
            return evalTimes(t, env);
        if (t.type == ISEQUALTO)
            return evalIsEqualTo(t, env);
        if (t.type == DOESNOTEQUAL)
            return evalDoesNotEqual(t, env);
        if (t.type == LESSTHAN)
            return evalLessThan(t, env);
        if (t.type == MOD)
            return evalMod(t, env);
        else {
            System.out.println("IN EVALSIMPLEOP, TYPE ISN'T IMPLEMENTED YET: " + t.type);
            System.exit(1);
            return null;
        }
    }

    static Lexeme evalShortCircuitOp(Lexeme t, Lexeme env) throws IOException {
        if (t.type == OR)
            return evalOr(t, env);
        if (t.type == AND)
            return evalAnd(t, env);
        else {
            System.out.println("IN EVALSHORTCIRCUITOP, TYPE ISN'T IMPLEMENTED YET: " + t.type);
            System.exit(1);
            return null;
        }
    }

    static Lexeme evalOr(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        if (left.type == TRUE || (int) left.value == 1)
            return new Lexeme(INTEGER, 1, t.lineNumber);
        Lexeme right = eval(t.cdr(), env);
        if (right.type == TRUE || (int) right.value == 1)
            return new Lexeme(INTEGER, 1, t.lineNumber);
        else
            return new Lexeme(INTEGER, 0, t.lineNumber);
    }

    static Lexeme evalAnd(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        if (left.type == FALSE || (int) left.value == 0)
            return new Lexeme(INTEGER, 0, t.lineNumber);
        Lexeme right = eval(t.cdr(), env);
        if (right.value == FALSE || (int) right.value == 0)
            return new Lexeme(INTEGER, 0, t.lineNumber);
        else
            return new Lexeme(INTEGER, 1, t.lineNumber);
    }

    static Lexeme evalPlus(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left.type == INTEGER && right.type == INTEGER)
            return new Lexeme(INTEGER, (int) left.value + (int) right.value, t.lineNumber);
        else if (left.type == INTEGER && right.type == REAL)
            return new Lexeme(REAL, (int) left.value + (double) right.value, t.lineNumber);
        else if (left.type == REAL && right.type == INTEGER)
            return new Lexeme(REAL, (double) left.value + (int) right.value, t.lineNumber);
        else
            return new Lexeme(REAL, (double) left.value + (double) left.value, t.lineNumber);
    }

    static Lexeme evalMinus(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left.type == INTEGER && right.type == INTEGER)
            return new Lexeme(INTEGER, (int) left.value - (int) right.value, t.lineNumber);
        else if (left.type == INTEGER && right.type == REAL)
            return new Lexeme(REAL, (int) left.value - (double) right.value, t.lineNumber);
        else if (left.type == REAL && right.type == INTEGER)
            return new Lexeme(REAL, (double) left.value - (int) right.value, t.lineNumber);
        else
            return new Lexeme(REAL, (double) left.value - (double) left.value, t.lineNumber);
    }

    static Lexeme evalTimes(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left.type == INTEGER && right.type == INTEGER)
            return new Lexeme(INTEGER, (int) left.value * (int) right.value, t.lineNumber);
        else if (left.type == INTEGER && right.type == REAL)
            return new Lexeme(REAL, (int) left.value * (double) right.value, t.lineNumber);
        else if (left.type == REAL && right.type == INTEGER)
            return new Lexeme(REAL, (double) left.value * (int) right.value, t.lineNumber);
        else
            return new Lexeme(REAL, (double) left.value * (double) left.value, t.lineNumber);
    }

    static Lexeme evalIsEqualTo(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left == null && right == null) return new Lexeme(TRUE, null, t.lineNumber);
        if (left.type == INTEGER && right.type == INTEGER) {
            if ((int) left.value == (int) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == INTEGER && right.type == REAL) {
            if ((int) left.value == (double) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == REAL && right.type == INTEGER) {
            if ((double) left.value == (int) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else {
            if ((double) left.value == (double) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        }
    }
    static Lexeme evalDoesNotEqual(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left.type == INTEGER && right.type == INTEGER) {
            if ((int)left.value != (int)right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == INTEGER && right.type == REAL) {
            if ((int)left.value != (double)right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == REAL && right.type == INTEGER) {
            if ((double)left.value != (int)right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else {
            if ((double)left.value != (double)right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        }
    }


    static Lexeme evalLessThan(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        if (left.type == INTEGER && right.type == INTEGER) {
            if ((int) left.value < (int) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == INTEGER && right.type == REAL) {
            if ((int) left.value < (double) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else if (left.type == REAL && right.type == INTEGER) {
            if ((double) left.value < (int) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        } else {
            if ((double) left.value < (double) right.value)
                return new Lexeme(TRUE, null, t.lineNumber);
            else
                return new Lexeme(FALSE, null, t.lineNumber);
        }
    }

    static Lexeme evalMod(Lexeme t, Lexeme env) throws IOException {
        Lexeme left = eval(t.car(), env);
        Lexeme right = eval(t.cdr(), env);
        return new Lexeme(INTEGER, (int) left.value % (int) right.value, t.lineNumber);
    }

    static Lexeme evalUVariable(Lexeme t, Lexeme env) throws IOException {
        Lexeme variable = eval(t.car(), env);
        if (t.cdr() == null)
            return variable;
        else {
            return eval(cons(FUNCCALL, t.car(), t.cdr().car()), env); // t.car = funccall name, t.cdr.car = call args
        }
    }

    static Lexeme evalExprList(Lexeme t, Lexeme env) throws IOException {
        Lexeme elist = new Lexeme(EXPRLIST, null, null);
        elist.setCar(eval(t.car, env));
        if (t.cdr != null)
            elist.setCdr(eval(t.cdr, env)); // t.cdr is either exprList or null
        return elist;
    }

    static Lexeme evalPrintLn(Lexeme args) {
        evalPrint(args);
        System.out.println();
        return args;
    }

    static Lexeme evalPrint(Lexeme args) {
        while (args != null) {
            if (args.car() == null) System.out.print("NULL");
            else System.out.print(args.car().value.toString());
            args = args.cdr();
        }
        return args;
    }

    static Lexeme evalGetArgCount() {
        return new Lexeme(INTEGER, argcCL, 0);
    }

    static Lexeme evalGetArg(Lexeme args) {
        Lexeme index = args.car();
        return new Lexeme(STRING, argsCL[Integer.parseInt(index.value.toString())], args.lineNumber);
    }

    static Lexeme evalOpenFileForReading(Lexeme args) throws FileNotFoundException {
        Lexeme fileName  = args.car();
        Lexeme fp = new Lexeme(FILEPOINTER, null, args.lineNumber);
        FileReader fr = new FileReader(fileName.value.toString());
        fp.value = new PushbackReader(fr);
        return fp;
    }

    static Lexeme evalReadInteger(Lexeme args) throws IOException {
        PushbackReader fp = (PushbackReader)args.car().value;
        if (fp == null) {
            System.out.println("ERROR: can't read empty file");
            System.exit(1);
            return null;
        }
        int i = 0;
        char ch = ' ';
        String token = "";
        

        i = fp.read();
        if (i == -1) return new Lexeme(ENDOFFILE, args.lineNumber);
        ch = (char)i;

        while (Character.isDigit(ch)) {
            token = token + ch;
            i = fp.read();
            ch = (char)i;
        }

        fp.unread(i);

        int x = Integer.parseInt(token);

        return new Lexeme(INTEGER, x, args.lineNumber);
    }

    static Lexeme evalAtFileEnd(Lexeme args) throws IOException {
        PushbackReader fp = (PushbackReader)args.car().value;
        int x = fp.read();
        if (x == -1) return new Lexeme(INTEGER, 1, args.lineNumber);
        else return new Lexeme(INTEGER, 0, args.lineNumber);
    }

    static Lexeme evalCloseFile(Lexeme args) throws IOException {
        PushbackReader fp = (PushbackReader)args.car().value;
        fp.close();
        return new Lexeme (TRUE, args.lineNumber);
    }

    static Lexeme evalNewArray(Lexeme args) throws IOException {
        if (args == null || args.cdr() != null) {
            System.out.println("ERROR line " + args.lineNumber + ": initialize new array with size of array");
            System.exit(1);
            return null;
        }

        Lexeme size = args.car();
        Lexeme[] val = new Lexeme[(int)size.value];
        Lexeme array = new Lexeme(ARRAY, val, args.lineNumber);
        return array;
    }

    static Lexeme evalGetArray(Lexeme args) throws IOException {
        if (args == null || args.cdr() == null || args.cdr().cdr() != null) {
            System.out.println("ERROR line " + args.lineNumber + ": usage is getArray(arrayName, index)");
            System.exit(1);
            return null;
        }
        Lexeme a = args.car();
        Lexeme[] array = (Lexeme[])a.value;

        int index = (int)args.cdr().car().value;
        return array[index];
    }

    static Lexeme evalSetArray(Lexeme args) throws IOException {
        if (args == null || args.cdr() == null || args.cdr().cdr() == null || args.cdr().cdr().cdr() != null) {
            System.out.println("ERROR line " + args.lineNumber + ": usage is setArray(arrayName, index, value)");
            System.exit(1);
            return null;
        }
        Lexeme arr = args.car();
        Lexeme ind = args.cdr().car();
        Lexeme val = args.cdr().cdr().car();

        Lexeme[] tempArray = (Lexeme[])arr.value;
        tempArray[(int)ind.value] = val;

        arr.value = tempArray;

        return arr;
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

    static Lexeme optExpression() throws IOException {
        if (expressionPending()) return expression();
        else return null;
    }

    static Lexeme returnStatement() throws IOException {
        match(RETURN);
        Lexeme returns = optExpression();
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
        else if (check(LAMBDA)) {
            match(LAMBDA);
            match(OPAREN);
            Lexeme args = optExpressionList();
            match(CPAREN);
            match(OCURLY);
            Lexeme block = block();
            match(CCURLY);
            return cons(LAMBDA, null, cons(GLUE, args, block));
            
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
        check(LAMBDA) ||
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
        else if (check(DOESNOTEQUAL)) return match(DOESNOTEQUAL);
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
        check(DOESNOTEQUAL) ||
        check(GETS) ||
        check(DOT);
    }


}