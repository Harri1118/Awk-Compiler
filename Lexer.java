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

    // 'Lex()' Method to parse through the document and convert to tokens for the
    // 'tokens' variable.
    public void Lex() {
        // Try parsing through document until there is an error.
        try {
            // Stopper boolean which doesn't stop the loop until it is finished.
            boolean stopper = false;
            // While loop to initiate parsing.
            while (stopper == false) {
                if (document.IsDone() == true)
                    break;
                // char c initialized to hold the value of Peek at 0 (to see what character the
                // document is on).
                char c = document.Peek(0);
                // Checks if 'c' is a digit. Calls processNumber() and changes state.
                if ((c == 46) || Character.isDigit(c)) {
                    processNumber();
                }
                // Checks if 'c' is alphabetic. Calls processWord() and changes state.
                else if (Character.isAlphabetic(c)) {
                    processWord();
                }
                // Checks if 'c' is a separator. Adds a separator token to tokens. Adds 1 to
                // line, 1 to position, and the document swallows by 1.
                else if (c == 10) {
                    tokens.add(new Token(String.valueOf(c), position, line));
                    position++;
                    document.Swallow(1);
                    line++;
                }
                // Checks if the character is white space (space bar, non newline characters,
                // etc. Will ignore these characters)
                else if (Character.isWhitespace(c)) {
                    document.Swallow(1);
                    position++;
                }
                // If 'c' isn't recognized by any of these, it will throw an unrecognized
                // character exception. Will specify the line and position where the error
                // occured.
                else {
                    throw new Error("Not a recognized character at line: " + line + ", position " + position);
                }
            }
        }
        // If Lex() doesn't work (for whatever reason) it prints the error
        // that was thrown.
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // processWord() collects word characters until there is no more an alphabetic
    // char.
    public void processWord() {
        // buffer used for adding all numbers into a string.
        String buffer = "";
        // while loop checks if the document is finished, as well as if the character is
        // a digit or a period.
        while (document.IsDone() == false && Character.isAlphabetic(document.Peek(0))) {
            // 'c' initialized as the current char of the document, the index is also added
            // by one.
            char c = document.GetChar();
            // 'c' added to buffer.
            buffer += c;
        }
        // number finally added to tokens.
        tokens.add(new Token(buffer, position, line));
        // position increments after Token is added.
        position += buffer.length();
    }

    // processNumber() to check the number line by line until it can derive a valid
    // double value.
    public void processNumber() {
        // foundPoint initialized to check if there's one decimal in the number.
        boolean foundPoint = false;
        // buffer used for adding all numbers into a string.
        String buffer = "";
        // while loop checks if the document is finished, as well as if the character is
        // a digit or a period.
        while (document.IsDone() == false && (Character.isDigit(document.Peek(0)) == true || document.Peek(0) == '.')) {
            // 'c' initialized as the current char of the document, the index is also added
            // by one.
            char c = document.GetChar();
            // 'c' added to buffer.
            buffer += c;
            // 'throws an error if c is a decimal and point is already found'.
            if (c == '.' && foundPoint == true)
                throw new Error("Not a valid number at line " + line);
            // if c is '.' foundPoint is true.
            else if (c == '.')
                foundPoint = true;
        }
        // number finally added to tokens.
        tokens.add(new Token(Double.parseDouble(buffer), position, line));
        // position increments after Token is added.
        position += buffer.length();
    }

    // Returns the 'tokens' variable.
    public LinkedList<Token> getTokens() {
        return tokens;
    }

}
