//Written by Nick Martin for CS403 - Programming Languages

public class Environments implements Types {
    //Note that we are using a typed cons function. It returns a lexeme whose left pointer is the second argument and whose right pointer is the third argument and whose type is the first argument. 

    static Lexeme cons(String type, Lexeme car, Lexeme cdr){
        return new Lexeme(type, car, cdr);
    }

    static Lexeme extend(Lexeme variables, Lexeme values, Lexeme env) {
        return cons(ENV,cons(TABLE ,variables, values), env);
    }
    
    static Lexeme create() {
        return cons(ENV, cons(TABLE,null,null), null);
    }

    static Lexeme getEnclosingScope(Lexeme env) {
        return env.cdr();
    }

    static Lexeme setEnclosingScope(Lexeme obj, Lexeme enc) {
        if (obj.type == ENV) {
            obj.setCdr(enc);
        }
        else if (obj.type == CLOSURE) {
            obj.setCar(enc);
        }
        else {
            System.err.println("Setting enclosing scope of non env/closure");
            System.exit(1);
        }
        return obj;
    }
    static Lexeme insert(Lexeme variable, Lexeme value, Lexeme env) {
        if((variable.value.toString() != "this") && lookup(variable.value.toString(), env) != null) {
            System.out.println("ERROR line: " + variable.lineNumber + ", redefinition of variable \'" + variable.value.toString() +"\'" );
            System.exit(1);
        }
        Lexeme table = env.car();
        table.setCar(cons(GLUE, variable, table.car()));
        table.setCdr(cons(GLUE, value, table.cdr()));
        return value;
    }

    static Lexeme lookup(String variable, Lexeme env) {
        while (env != null) {
            Lexeme table = env.car();
            Lexeme vars = table.car();
            Lexeme vals = table.cdr();
            while (vars != null) {
                if (vars.car().type == UVARIABLE) {
                    if (sameVariable(variable, vars.car().car())) return vals.car();
                }
                else {
                    if (sameVariable(variable, vars.car())) return vals.car();

                }
                vars = vars.cdr();
                vals = vals.cdr();    
            }
            env = env.cdr();
        }
        return null;
    }

    static Lexeme update(String variable, Object newValue, Lexeme env) {
        while (env != null) {
            Lexeme table = env.car();
            Lexeme vars = table.car();
            Lexeme vals = table.cdr();
            while (vars != null) {
                if (sameVariable(variable, vars.car())) {
                    Lexeme temp = vals.car();
                    temp.value = newValue;
                    vals.setCar(temp);
                    return vals.car();
                }
                vars = vars.cdr();
                vals = vals.cdr();    
            }
            env = env.cdr();
        }
        System.out.println("ERROR: Variable \'" + variable + "\' is not defined");
        System.exit(1);
        return null;
    }


    static void printAllEnvironments(Lexeme tree) {
        printLocalEnvironment(tree);
        tree = tree.cdr();
        int i = 1;
        while (tree != null) {
            System.out.println("Environment " + i + " is:");
            Lexeme table = tree.car();
            Lexeme currVar = table.car();
            Lexeme currVal = table.cdr();

            while (currVal != null) {
                System.out.print("ID \'" + currVar.car().value + "\' ");
                if (currVal.car() != null) 
                {
                    System.out.println("has value ");
                    PP.prettyPrint(currVal.car());
                }
                else System.out.println("is unassigned");
                currVal = currVal.cdr();
                currVar = currVar.cdr();
            }
            System.out.println();
            tree = tree.cdr();
            ++i;
        }
    }

    static void printLocalEnvironment(Lexeme tree) {
        System.out.println("Environment 0 (local) is: ");
        Lexeme table = tree.car();
        Lexeme currVar = table.car();
        Lexeme currVal = table.cdr();

        while (currVal != null) {
            System.out.print("ID \'" + currVar.car().value + "\' ");
            if (currVal.car() != null) {
                System.out.print("has value ");
                PP.prettyPrint(currVal.car());
                System.out.println();
            }
            else System.out.println("is unassigned");
        currVal = currVal.cdr();
            currVar = currVar.cdr();
        }
        System.out.println();
    }

    static boolean sameVariable(String lookupString, Lexeme lookupLexeme) {
        return lookupString.equals(lookupLexeme.value.toString());
    }


}
