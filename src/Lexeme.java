//Written by Nick Martin for CS403 - Programming Languages

public class Lexeme {

    String type;
    Object value;
    int lineNumber;

    Lexeme car; //left child
    Lexeme cdr; //right child
    
    public Lexeme(String type, int lineNumber) {
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public Lexeme(String type, Lexeme car, Lexeme cdr) {
        this.type = type;
        this.car = car;
        this.cdr = cdr;
    }

    public Lexeme(String type, Object value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public Lexeme car() {
        return this.car;
    }

    public Lexeme cdr() {
        return this.cdr;
    }

    public void setCar(Lexeme parentLexeme, Lexeme childLexeme) {
        parentLexeme.car = childLexeme;
    }

    public void setCdr(Lexeme parentLexeme, Lexeme childLexeme) {
        parentLexeme.cdr = childLexeme;
    }
}
