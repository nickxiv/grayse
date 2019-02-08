//Written by Nick Martin for CS403 - Programming Languages

import java.io.IOException;

public class Enviroments implements Types {

    static Lexeme CurrentLexeme;

    public static void main(String[] args) throws IOException {
        System.out.println("Creating new enviroment");
        Lexeme EnviromentTree = create();
        printAllEnviroments(EnviromentTree);
        System.out.println("Inserting variable \'x\' with value 3");
        Lexeme idLexeme = new Lexeme(VARIABLE, 'x', 0);
        Lexeme valLexeme = new Lexeme(INTEGER, 3, 0);
        insert(idLexeme, valLexeme, EnviromentTree);
        printAllEnviroments(EnviromentTree);

        System.exit(0);
    }

    //Note that we are using a typed cons function. It returns a lexeme whose left pointer is the second argument and whose right pointer is the third argument and whose type is the first argument. 

    static Lexeme cons(String type, Lexeme car, Lexeme cdr){
        return new Lexeme(type, car, cdr);
    }

    static Lexeme extend(Lexeme variables, Lexeme values, Lexeme env) {
        return cons(ENV, variables, cons(ENV, values, env));
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
            System.out.println("The enviroment is:");
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
