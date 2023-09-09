package project2;

import java.util.HashMap;
import java.util.LinkedList;

import project2.Token.TokenType;

public class Lexer {
    // StringHandler initiated in this class. Used to parse through document and for
    // Lex to 'peek' through.
    private StringHandler document;

    // line variable used to count the amount of lines from document.
    private int line = 1;

    // Position tracks the location of the pointer while parsing through
    // StringHandler. Will be used for tokenizing.
    private int position = 0;

    private HashMap<String, TokenType> Ophashes = new HashMap<String, TokenType>();
    private HashMap<String, TokenType> boolHashes = new HashMap<String, TokenType>();
    private HashMap<Character, TokenType> charHashes = new HashMap<Character, TokenType>();

    // LinkedList for the tokens to be stored.
    private LinkedList<Token> tokens = new LinkedList<>();

    // Constrictor for Lexer. Takes a string for document to utilize.
    public Lexer(String s) {
        document = new StringHandler(s);
        setStatements();
        setSingleChar();
        setDoubleChars();
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
                if ((c == 46) || Character.isDigit(c))
                    processNumber();

                // Checks if 'c' is alphabetic. Calls processWord() and changes state.
                else if (Character.isAlphabetic(c))
                    processWord();

                else if (c == '#')
                    commentState();

                else if (c == '"')
                    handleStringLiteral();

                else if (charHashes.containsKey(c))
                    processSymbol();
                // Checks if the character is white space (space bar, non newline characters,
                // etc. Will ignore these characters)
                else if (Character.isWhitespace(c)) {
                    document.Swallow(1);
                    position++;
                }
                // If 'c' isn't recognized by any of these, it will be ignored and the document
                // swallows 1 and position adds by 1.
                else {
                    throw new Exception("Not a recognized character at line: " + line);
                }
            }
        }
        // If Lex() doesn't work (for whatever reason) it prints 'invalid file', and
        // tells the line and position where the error occured.
        catch (Exception e) {
            System.out.println("Invalid file! Error at line " + line);
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
        if (Ophashes.containsKey(buffer))
            tokens.add(new Token(Ophashes.get(buffer), buffer, position, line));
        else
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
        tokens.add(new Token(TokenType.NUMBER, buffer, position, line));
        // position increments after Token is added.
        position += buffer.length();
    }

    public void processSymbol() {
        try{
            //Check if next two chars are double symbol value
            String s = document.PeekString(2);
            if(boolHashes.containsKey(s)){
                tokens.add(new Token(boolHashes.get(s), s, position, line));
                position += 2;
                document.Swallow(2);
            }
            else
                throw new Exception();
        }
        catch(Exception e){ 
            if(charHashes.containsKey(document.Peek(0))){
                char c = document.Peek(0);
                if(c == '&')
                    throw new Error("cannot have single character '&'!");
                tokens.add(new Token(charHashes.get(c), String.valueOf(c), position, line));
                position++;
                document.Swallow(1);
                if (c == '\n' || c == ';')
                    line++;
            }
            
            else{
                position++;
                document.Swallow(1);
            }
            //Check if symbol is single character
        }
    }

    public void commentState() {
        while (document.GetChar() != 10) {
            if (document.IsDone())
                break;
            position++;
        }

        line++;
    }

    public void handleStringLiteral() {
        
        String buffer = "";
        document.Swallow(1);
        position++;
        int countQuotes = 0;
        while (document.Peek(0) != '"') {
            char c = document.GetChar();
            if (c == '\\' && document.Peek(0) == '"') {
                position++;
                buffer += c;
                document.Swallow(1);
                buffer = HandlePattern(buffer + '"');
                countQuotes++;
            } else
                buffer += c;
        }
        checkEven(countQuotes);
        document.Swallow(1);
        position += buffer.length() + 2;
        tokens.add(new Token(TokenType.STRINGLITERAL, buffer, position, line));
    
    

}

    public void checkEven(int i){
        if(i != 2)
            throw new Error("Must have an even number of quotes in a string literal!");
    }

    public String HandlePattern(String s) {
        return s.substring(0, s.length() - 2) + "\"";
    }

    public void setStatements() {
        Ophashes.put("while", TokenType.WHILE);
        Ophashes.put("if", TokenType.IF);
        Ophashes.put("do", TokenType.DO);
        Ophashes.put("for", TokenType.FOR);
        Ophashes.put("break", TokenType.BREAK);
        Ophashes.put("continue", TokenType.CONTINUE);
        Ophashes.put("else", TokenType.ELSE);
        Ophashes.put("return", TokenType.RETURN);
        Ophashes.put("BEGIN", TokenType.BEGIN);
        Ophashes.put("END", TokenType.END);
        Ophashes.put("print", TokenType.PRINT);
        Ophashes.put("printf", TokenType.PRINTF);
        Ophashes.put("next", TokenType.NEXT);
        Ophashes.put("in", TokenType.IN);
        Ophashes.put("delete", TokenType.DELETE);
        Ophashes.put("getline", TokenType.GETLINE);
        Ophashes.put("exit", TokenType.EXIT);
        Ophashes.put("nextfile", TokenType.NEXTFILE);
        Ophashes.put("function", TokenType.FUNCTION);

    }

    public void setSingleChar() {
        charHashes.put('{', TokenType.OPBRAC);
        charHashes.put('}', TokenType.CLBRAC);
        charHashes.put('[', TokenType.SQOPBRAC);
        charHashes.put(']', TokenType.SQCLBRAC);
        charHashes.put('(', TokenType.OPENPAREN);
        charHashes.put(')', TokenType.CLOSEPAREN);
        charHashes.put('$', TokenType.DOLLAR);
        charHashes.put('~', TokenType.TILDE);
        charHashes.put('=', TokenType.ASSIGN);
        charHashes.put('<', TokenType.LETHAN);
        charHashes.put('>', TokenType.GRTHAN);
        charHashes.put('!', TokenType.EXLPT);
        charHashes.put('+', TokenType.PLUS);
        charHashes.put('^', TokenType.CARROT);
        charHashes.put('-', TokenType.MINUS);
        charHashes.put('?', TokenType.QMARK);
        charHashes.put(':', TokenType.COLON);
        charHashes.put('*', TokenType.STAR);
        charHashes.put('/', TokenType.SLASH);
        charHashes.put('%', TokenType.PERCNT);
        charHashes.put(';', TokenType.SEPARATOR);
        charHashes.put('\n', TokenType.SEPARATOR);
        charHashes.put('|', TokenType.VERTBAR);
        charHashes.put(',', TokenType.COMMA);
        charHashes.put('&', null);
    }
    public void setDoubleChars() {
        boolHashes.put(">=", TokenType.GREQ);
        boolHashes.put("++", TokenType.ADD);
        boolHashes.put("--", TokenType.SUBT);
        boolHashes.put("<=", TokenType.LEEQ);
        boolHashes.put("==", TokenType.EQUALS);
        boolHashes.put("!=", TokenType.NEQ);
        boolHashes.put("^=", TokenType.CAREQ);
        boolHashes.put("%=", TokenType.PEREQ);
        boolHashes.put("*=", TokenType.MEQ);
        boolHashes.put("/=", TokenType.DIVEQ);
        boolHashes.put("+=", TokenType.PLEQ);
        boolHashes.put("-=", TokenType.MINEQ);
        boolHashes.put("!~", TokenType.REGEXP);
        boolHashes.put("&&", TokenType.AND);
        boolHashes.put(">>", TokenType.LEADS);
        boolHashes.put("||", TokenType.OR);

    }

    // Returns the 'tokens' variable.
    public LinkedList<Token> getTokens() {
        return tokens;
    }
}
