import java.util.LinkedList;

public class Lexer {
    // StringHandler initiated in this class. Used to parse through document and for
    // Lex to 'peek' through.
    private StringHandler document;
    // line variable used to count the amount of lines from document.
    private int line = 0;
    // Position tracks the location of the pointer while parsing through
    // StringHandler. Will be used for tokenizing.
    private int position = 0;

    // LinkedList for the tokens to be stored.
    private LinkedList<Token> tokens = new LinkedList<>();

    // Constrictor for Lexer. Takes a string for document to utilize.
    public Lexer(String s) {
        document = new StringHandler(s);
    }

    public void Lex() {
        // Try/catch expression for cases when document is invalid (empty document, full
        // of invalid characters, etc)
        try {
            // finished boolean, will remain false as long as the status of the document is
            // not done (document.isDone())
            boolean finished = false;

            // While loop which parses through the StringHandler (document)
            while (finished == false) {
                // Checks if the document is finished.
                if (document.IsDone() == true)
                    break;
                // c variable initialized to analyze the sorting of the character.
                char c = document.Peek(0);

                // Checks if c is a space (ASCII decimal value 32), or any other common invalid
                // character
                if (c == 32 || c == 9 || c == 11 || c == 13) {
                    document.Swallow(1);
                    position++;
                }

                // Checks if i is a newline (ASCII decimal value 10)
                else if (c == 10) {
                    document.Swallow(1);
                    line++;
                    tokens.add(processWord(String.valueOf(c)));
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

                }
                // If no other case is plausable, it throws a typeNotPresent exception
                else
                    throw new Error("Invalid character at" + c);
            }
        } catch (Exception e) {
            System.out.println("Invalid file!");
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

    // Method called to create a Token of a word type.
    private Token processWord(String s) {
        Token f = new Token(s, line, position);
        return f;
    }

    // Method called to create a Token of a number type.
    private Token processNumber(String s) {
        double v = Double.valueOf(s);
        Token f = new Token(v, line, position);
        return f;
    }

    // Method to calculate the length of a string in 'document'. If it reaches the
    // end of a document, it will return i regardless.
    private int getNextCharLen() {
        int i = 0;
        try {
            // 'i' iterates by one each time a character is calid (ASCII value is greater
            // than 32 and below 128)
            while (document.Peek(i) > 32 && document.Peek(i) < 127)
                i = i + 1;

            return i;

        } catch (Exception e) {
            return i;
        }

    }

    // Method used to return the tokens in the Lexer class.
    public LinkedList<Token> getTokens() {
        return tokens;
    }
}
