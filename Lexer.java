import java.util.LinkedList;

public class Lexer {
    // 1. Lexer constructor Instantiates StringHandler and sets line number and
    // position
    // 2. Accepts required characters, creates a token, doesn’t accept characters it
    // shouldn’t
    public StringHandler handle;
    private int line;
    private int pos;
    private Token token;

    public Lexer(String s, int l, int p) {
        handle = new StringHandler(s);
        line = l;
        pos = p;
        if (s.equals("\n")) {
            token = processNewLine();
        }
        if (notNum(s) == true) {
            token = processWord(s);
        } else
            token = processNumber(s);
    }

    private boolean notNum(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)) == false) {
                return true;
            }
        }
        return false;
    }

    public Token getToken() {

        return token;
    }

    public Token processWord(String s) {
        Token f = new Token(s, line, pos);
        return f;
    }

    public Token processNumber(String s) {
        int v = Integer.valueOf(s);
        Token f = new Token(v, line, pos);
        return f;
    }

    public Token processNewLine() {
        Token f = new Token(line, pos);
        return f;
    }
}
