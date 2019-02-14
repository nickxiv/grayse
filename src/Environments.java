//Written by Nick Martin for CS403 - Programming Languages

public class Environments implements Types {
    public static void main(String[] args)  {
        System.out.println("Creating new Environment");
        Lexeme GlobalTree = create();
        printAllEnvironments(GlobalTree);
        System.out.println("Inserting variable \'x\' with value 3");
        Lexeme idLexeme = new Lexeme(VARIABLE, 'x', 0);
        Lexeme valLexeme = new Lexeme(INTEGER, 3, 0);
        insert(idLexeme, valLexeme, GlobalTree);

        printAllEnvironments(GlobalTree);

        System.out.println("Extending the environment with y:4 and z:\"hello\"\n");
        Lexeme localTree = create();
        idLexeme = new Lexeme(VARIABLE, 'y', 0);
        valLexeme = new Lexeme(INTEGER, 4, 0);
        insert(idLexeme, valLexeme, localTree);
        idLexeme = new Lexeme(VARIABLE, 'z', 0);
        valLexeme = new Lexeme(STRING, "hello", 0);
        insert(idLexeme, valLexeme, localTree);

        GlobalTree = extend(localTree.car().car(), localTree.car().cdr(), GlobalTree);
        printAllEnvironments(GlobalTree);


        System.out.println("Inserting variable w with value \"why\" into most local environment");

        idLexeme = new Lexeme(VARIABLE, 'w', 0);
        valLexeme = new Lexeme(STRING, "why", 0);
        System.out.println("ID \'w\' has value " + insert(idLexeme, valLexeme, GlobalTree).value);
        System.out.println();


        printAllEnvironments(GlobalTree);

        System.out.println("Finding value of variable y");
        System.out.println("ID \'y\' has value " + lookup("y", GlobalTree).value);
        System.out.println();
        System.out.println("Finding value of variable x");
        System.out.println("ID \'x\' has value " + lookup("x", GlobalTree).value);
        System.out.println();
        System.out.println("Updating value of variable x to 6");
        System.out.println("ID \'x\' value updated to " + update("x", 6, GlobalTree).value);
        System.out.println();
        printAllEnvironments(GlobalTree);
        System.out.println();

        System.exit(0);
    }

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

    static Lexeme insert(Lexeme variable, Lexeme value, Lexeme env) {
        if(lookup(variable.value.toString(), env) != null) {
            System.out.println("ERROR: redefinition of variable \'" + variable.value.toString() +"\' in local environment");
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
                if (sameVariable(variable, vars.car())) return vals.car();
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
                System.out.println("has value " + currVal.car().value);
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
            System.out.println("has value " + currVal.car().value);
            currVal = currVal.cdr();
            currVar = currVar.cdr();
        }
        System.out.println();
    }

    static boolean sameVariable(String lookupString, Lexeme lookupLexeme) {
        return lookupString.equals(lookupLexeme.value.toString());
    }


}
