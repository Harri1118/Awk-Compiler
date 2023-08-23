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
        line = l;
        pos = p;
        handle = new StringHandler(s);
        String word = handle.PeekString(pos);
        token = new Token(s, line, pos);
    }

    public Token getToken() {
        return token;
    }
}