import java.util.LinkedList;
import java.lang.TypeNotPresentException;

public class Lexer {

    // StringHandler initiated in this class. Used to parse through document and for
    // Lex to 'peek' through.
    public StringHandler document;
    // Line is inputted from lex into lexer to put into the Token object.
    private int line;
    // Pos is inputted from lex into lexer to put into the Token object.
    private int position;

    public LinkedList<Token> tokens = new LinkedList<>();

    // Constrictor for Lexer. Takes a string, line, and position args in order to
    // properly configure the type of token and values for the token.
    public Lexer(String s, int l, int p) {
        document = new StringHandler(s);
        line = l;
        position = p;
    }

    public void Lex() {
        // Controller
        boolean b = false;

        // While loop which parses StringHandler (document)
        while (b == false) {
            if (document.IsDone() == true)
                break;
            char c = document.Peek(0);

            // Checks if i is a space (ASCII decimal value 32)
            if (c == 32 || c == 9 || c == 11 || c == 13) {
                document.Swallow(1);
                position++;
            }

            // Checks if i is a newline (ASCII decimal value 10)
            else if (c == 10) {
                document.Swallow(1);
                line++;
                tokens.add(processNewLine());
                position++;
            }
            // If document.peek(i) isn't a space or a newline, it gets checked whether it is
            // a number or word.
            else if (c > 32 && c < 127) {
                String w = document.PeekString(getNextCharLen());
                document.Swallow(w.length());
                if (notNum(w) == true) {
                    tokens.add(processWord(w));
                } else
                    tokens.add(processNumber(w));
                position = position + w.length();

            } else
                throw new TypeNotPresentException(null, null);
        }
    }

    // Checks if the incoming input from main is a number or not
    private boolean notNum(String s) {
        try {
            Double.parseDouble(s);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    // method called to create a Token of a word type.
    public Token processWord(String s) {
        Token f = new Token(s, line, position);
        return f;
    }

    // method called to create a Token of a number type.
    public Token processNumber(String s) {
        double v = Double.valueOf(s);
        Token f = new Token(v, line, position);
        return f;
    }

    // method called to create a Token of a separator type.
    public Token processNewLine() {
        Token f = new Token(line, position);
        return f;
    }

    public int getNextCharLen() {
        int i = 0;
        try {
            // 'i' iterates by one each time a character is not decimal value 20 (based off
            // raw ASCII characters)
            while (document.Peek(i) > 32 && document.Peek(i) < 127)
                i = i + 1;

            return i;

        } catch (Exception e) {
            return i;
        }

    }
}
