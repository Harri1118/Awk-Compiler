import java.util.LinkedList;

public class Lexer {

    // StringHandler initiated in this class. Used to parse through document and for
    // Lex to 'peek' through.
    public StringHandler document;
    // Line is inputted from lex into lexer to put into the Token object.
    private int line;
    // Pos is inputted from lex into lexer to put into the Token object.
    private int position;
    // Token object which is to be returned.
    private Token token;

    public LinkedList<Token> tokens = new LinkedList<>();

    // Constrictor for Lexer. Takes a string, line, and position args in order to
    // properly configure the type of token and values for the token.
    public Lexer(String s, int l, int p) {
        document = new StringHandler(s);
        line = l;
        position = p;
    }

    public void Lex() {
        boolean b = false;
        while (b == false) {
            int i = 0;
            char c = document.Peek(i);
            while (document.Peek(i) != ' ' && document.Peek(i) != '\n') {
                c = document.Peek(i);
                i = i + 1;

            }
            if (document.Peek(i) == '\n' && i == 0) {
                document.Swallow(1);
                position++;
                line++;
                i++;
            }
            if (i == 0) {
                document.Swallow(1);
                position++;
            } else {
                String w = document.PeekString(i);
                document.Swallow(w.length());
                if (w.equals("\n")) {
                    tokens.add(processNewLine());
                }
                if (notNum(w) == true) {
                    tokens.add(processWord(w));
                } else
                    tokens.add(processNumber(w));
                position = position + w.length();
                if(document.IsDone() == true)
                    b = true;
            }
        }
    }

    // Checks if the incoming input from main is a number or not
    private boolean notNum(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)) == false) {
                return true;
            }
        }
        return false;
    }

    // returns the token produced from the constructor
    public Token getToken() {

        return token;
    }

    // method called to create a Token of a word type.
    public Token processWord(String s) {
        Token f = new Token(s, line, position);
        return f;
    }

    // method called to create a Token of a number type.
    public Token processNumber(String s) {
        int v = Integer.valueOf(s);
        Token f = new Token(v, line, position);
        return f;
    }

    // method called to create a Token of a separator type.
    public Token processNewLine() {
        Token f = new Token(line, position);
        return f;
    }

}
