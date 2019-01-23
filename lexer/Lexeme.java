//Written by Nick Martin for CS403 - Programming Languages

public class Lexeme {

    String type;
    Object value;
    int lineNumber;
    
    public Lexeme(String type, int lineNumber) {
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public Lexeme(String type, Object value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }


}