public class Recognizer implements Types {
    public static void main(String[] args) {
        Lexeme currentLexeme = new Lexeme(ENDOFFILE, 0);
        System.out.println(currentLexeme.type);
    }
}