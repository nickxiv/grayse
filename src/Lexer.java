//Written by Nick Martin for CS403 - Programming Languages

import java.io.PushbackReader;

import java.io.IOException;

public class Lexer implements Types {
    public String FileName;
    public PushbackReader Pbr;
    public int CurrentLine = 1;
    public boolean isDecimal = false;

    public Lexeme lex() throws IOException {
        int i;

        this.skipWhiteSpace();

        i = this.Pbr.read();
        if (i == 65535) return new Lexeme(ENDOFFILE, CurrentLine); //PushbackReader returns 65535 when trying to read past end of file.
        char ch = (char)i;
        switch(ch) { 
            case '(': return new Lexeme(OPAREN, CurrentLine);
            case ')': return new Lexeme(CPAREN, CurrentLine); 
            case ',': return new Lexeme(COMMA, CurrentLine); 
            case '+': 
                i = this.Pbr.read();
                ch = (char)i;
                if (ch == '+') return new Lexeme(PLUSPLUS, CurrentLine);
                else {
                    this.Pbr.unread(i);
                    return new Lexeme(PLUS, CurrentLine);
                }
            case '*': return new Lexeme(TIMES, CurrentLine); 
            case '-': 
                i = this.Pbr.read();
                ch = (char)i;
                if (ch == '-') return new Lexeme(MINUSMINUS, CurrentLine);
                else {
                    this.Pbr.unread(i);
                    return new Lexeme(MINUS, CurrentLine);
                }
            case '/': return new Lexeme(DIVIDES, CurrentLine); 
            case '%': return new Lexeme(MOD, CurrentLine);
            case '<': return new Lexeme(LESSTHAN, CurrentLine); 
            case '>': return new Lexeme(GREATERTHAN, CurrentLine); 
            case '=': 
                i = this.Pbr.read();
                ch = (char)i;
                if (ch == '=') return new Lexeme(ISEQUALTO, CurrentLine);
                else {
                    this.Pbr.unread(i);
                    return new Lexeme(GETS, CurrentLine);
                } 
            case ';': return new Lexeme(SEMICOLON, CurrentLine); 
            case ':': return new Lexeme(COLON, CurrentLine); 
            case '{': return new Lexeme(OCURLY, CurrentLine);
            case '}': return new Lexeme(CCURLY, CurrentLine);
            case '!': return new Lexeme(NOT, CurrentLine);
            case '|': return new Lexeme(OR, CurrentLine);
            case '&': return new Lexeme(AND, CurrentLine);
            case '.':
                i = this.Pbr.read();
                ch = (char)i;
                if (Character.isDigit(ch)) {
                    isDecimal = true;
                    this.Pbr.unread(i);
                    return lexNumber();
                }
                else {
                    this.Pbr.unread(i);
                    return new Lexeme(DOT, CurrentLine);
                }
            case '[': return new Lexeme(OBRACKET, CurrentLine);
            case ']': return new Lexeme(CBRACKET, CurrentLine);
        default: 
            // multi-character tokens 
            if (Character.isDigit(ch)) { 
                this.Pbr.unread(i);
                return lexNumber();
            } 
            else if (Character.isLetter(ch) || ch == '_') { 
                this.Pbr.unread(i);
                return lexVariableOrKeyword();
            } 
            else if (ch == '\"') { 
                return lexString();
            } 
            else return new Lexeme(UNKNOWN, ch, CurrentLine);
        } 
    }

    public Lexeme lexNumber() throws IOException {
        int i;
        char ch;
        String token = "";

        i = this.Pbr.read();
        ch = (char)i;

        if(isDecimal) token = token + "0.";

        while (Character.isDigit(ch)) {
            token = token + ch;
            i = this.Pbr.read();
            ch = (char)i;    
        }

        if (ch == '.') {
            if(isDecimal) return new Lexeme(ERROR, BAD_NUMBER,CurrentLine);
            token = token + ch;
            i = this.Pbr.read();
            ch = (char)i;
        }
        else {
            this.Pbr.unread(i);
            if(!isDecimal) return new Lexeme(INTEGER, Integer.parseInt(token), CurrentLine);
            else return new Lexeme(REAL, Double.parseDouble(token), CurrentLine);
        }
        while (Character.isDigit(ch)) {
            token = token + ch;
            i = this.Pbr.read();
            ch = (char)i;    
        }

        if(ch == '.') {
            this.Pbr.unread(i);
            return new Lexeme(ERROR, BAD_NUMBER,CurrentLine);
        }

        this.Pbr.unread(i);
        return new Lexeme(REAL, Double.parseDouble(token), CurrentLine);



    }
    

    public Lexeme lexVariableOrKeyword() throws IOException {
        int i;
        char ch;
        String token = "";

        i = this.Pbr.read();
        ch = (char)i;

        if (Character.isDigit(ch)) return new Lexeme(ERROR, BAD_VARIABLE, CurrentLine);

        while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_') {
            token = token + ch;
            i = this.Pbr.read();
            ch = (char)i;    
        }

        this.Pbr.unread(i);

        switch (token.toLowerCase()) {
            case "let":
                return new Lexeme(LET, CurrentLine);
            case "return":
                return new Lexeme(RETURN, CurrentLine);
            case "class":
                return new Lexeme(CLASS, CurrentLine);
            case "if":
                return new Lexeme(IF, CurrentLine);
            case "else":
                return new Lexeme(ELSE, CurrentLine);
            case "while":
                return new Lexeme(WHILE, CurrentLine);
            case "true":
                return new Lexeme(TRUE, CurrentLine);
            case "false":
                return new Lexeme(FALSE, CurrentLine);
            default:
                return new Lexeme(VARIABLE, token, CurrentLine);
        }
    }

    public Lexeme lexString() throws IOException {
        int i;
        char ch;
        String token = "";

        i = this.Pbr.read();
        ch = (char)i;

        while(ch != '\"' && i != -1) {
            token = token + ch;
            i = this.Pbr.read();
            ch = (char)i;
        }
        if (i == -1) return new Lexeme(ERROR, BAD_STRING, CurrentLine);
        return new Lexeme(STRING, token, CurrentLine);
    }

    public void skipWhiteSpace() throws IOException {
        int i = this.Pbr.read();
        char ch = (char)i;


        while ((i != -1) && Character.isWhitespace(ch)) {
            if (ch == '\n') ++CurrentLine;
            i = this.Pbr.read();
            ch = (char)i;
        }

        if(ch == '/') {
            i = this.Pbr.read();
            ch = (char)i;

            if (ch == '/') {
                skipSingleLineComment();
                skipWhiteSpace();
            }
            else if (ch == '*') {
                skipMultiLineComment();
                skipWhiteSpace();
            }
            else this.Pbr.unread(i);
        }
        else this.Pbr.unread(i);
    }

    public void skipSingleLineComment () throws IOException {
        int i = this.Pbr.read();
        char ch = (char)i;

        while ((i != -1) && ch != '\n') {
            i = this.Pbr.read();
            ch = (char)i;
        }
        if (ch == '\n') ++CurrentLine;

    }

    public void skipMultiLineComment() throws IOException {
        int i = this.Pbr.read();
        char ch = (char)i;

        while ((i != -1) && ch != '*') {
            if (ch == '\n') ++CurrentLine;
            i = this.Pbr.read();
            ch = (char)i;
        }

        if (ch == '*') {
            i = this.Pbr.read();
            ch = (char)i;
            if (ch == '\n') ++CurrentLine;

            if (ch == '/') {
                return;
            }
            else skipMultiLineComment();
        }

    }

}
    
