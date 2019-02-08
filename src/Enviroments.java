//Written by Nick Martin for CS403 - Programming Languages

public class Enviroments implements Types {
    public static void main(String[] args)  {
        System.out.println("Creating new enviroment");
        Lexeme GlobalTree = create();
        printAllEnviroments(GlobalTree);
        System.out.println("Inserting variable \'x\' with value 3");
        Lexeme idLexeme = new Lexeme(VARIABLE, 'x', 0);
        Lexeme valLexeme = new Lexeme(INTEGER, 3, 0);
        insert(idLexeme, valLexeme, GlobalTree);
        idLexeme = new Lexeme(VARIABLE, "zz", 0);
        valLexeme = new Lexeme(INTEGER, 55, 0);
        insert(idLexeme, valLexeme, GlobalTree);

        printAllEnviroments(GlobalTree);

        // System.out.println("Extending the environment with y:4 and z:\"hello\"");
        // Lexeme localTree = create();
        // idLexeme = new Lexeme(VARIABLE, 'y', 0);
        // valLexeme = new Lexeme(INTEGER, 4, 0);
        // insert(idLexeme, valLexeme, GlobalTree);
        // idLexeme = new Lexeme(VARIABLE, 'z', 0);
        // valLexeme = new Lexeme(STRING, "hello", 0);
        // insert(idLexeme, valLexeme, localTree);

        // extend(localTree.car().car(), localTree.car().cdr(), GlobalTree);
        // printAllEnviroments(GlobalTree);


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
    Lexeme table = env.car();
    table.setCar(cons(JOIN, variable, table.car()));
    table.setCdr(cons(JOIN, value, table.cdr()));
    return value;
    }

    static void printAllEnviroments(Lexeme tree) {
        while (tree != null) {
            System.out.println("The local enviroment is:");
            Lexeme table = tree.car();
            Lexeme currVar = table.car();
            Lexeme currVal = table.cdr();

            while (currVal != null) {
                System.out.print("ID \'" + currVar.car().value + "\' ");
                System.out.println("has value " + currVal.car().value);
                currVal = currVal.cdr();
                currVar = currVar.cdr();
            }
            tree = tree.cdr();
        }
        System.out.println();
    }

    static void printLocalEnviroment(Lexeme tree) {
        System.out.println("Printing local enviroment: ");
        Lexeme table = tree.car();
        Lexeme currVar = table.car();
        Lexeme currVal = table.cdr();

        while (currVal != null) {
            System.out.print("ID \'" + currVar.car().value + "\' ");
            System.out.println("has value " + currVal.car().value);
            currVal = currVal.cdr();
            currVar = currVar.cdr();
        }

    }


}
