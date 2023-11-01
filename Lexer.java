package icsi311;

import icsi311.Token.TokenType;

import java.util.HashMap;
import java.util.LinkedList;

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

    // Constrictor for Lexer. Takes a string for document to utilize. Sets
    // statements, operands, and operators to TokenTypes when parsing through awk
    // file.
    public Lexer(String s) {
        document = new StringHandler(s);
        setStatements();
        setSingleChar();
        setDoubleChars();
    }

    // 'Lex()' Method to parse through the document and convert to tokens for the
    // 'tokens' variable.
    public void Lex() throws Exception {
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
                // Checks if s is in a '#'. Will skip over line until next line.
                else if (c == '#')
                    commentState();
                // Checks if s is a quotation mark. Will handle as a string literal if this is
                // the case.
                else if (c == '"')
                    handleStringLiteral();
                // Checks if character is an operand, goes into processSymbol() state if so.
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
                    throw new Exception("Not a recognized character!");
                }
            }
        }
        // If Lex() doesn't work (for whatever reason) it prints 'invalid file', and
        // tells the line and position where the error occured.
        catch (Exception e) {
            throw new Exception("Error at line " + line + "\n" + e.getMessage());
        }
    }

    // processWord() collects word characters until there is no more an alphabetic
    // char.
    private void processWord() {
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
        // Checks if buffer is a statement. Adds it as a token if so.
        if (Ophashes.containsKey(buffer))
            tokens.add(new Token(Ophashes.get(buffer), buffer, position, line));
        else
            tokens.add(new Token(buffer, position, line));
        // position increments after Token is added.
        position += buffer.length();
    }

    // processNumber() to check the number line by line until it can derive a valid
    // double value.
    private void processNumber() throws Exception{
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
                throw new Exception("Not a valid number!");
            // if c is '.' foundPoint is true.
            else if (c == '.')
                foundPoint = true;
        }
        // number finally added to tokens.
        tokens.add(new Token(TokenType.NUMBER, buffer, position, line));
        // position increments after Token is added.
        position += buffer.length();
    }

    // processSymbol state adds operand/operator tokens.
    private void processSymbol() throws Exception{
        // Tries to check if the next two characters are a valid operator. If not then
        // it will throw an exception
        try {
            // s is a string which is initiated from PeekString.
            String s = document.PeekString(2);
            // Checks if string is a operator. Adds the token and changes position/document
            // index.
            if (boolHashes.containsKey(s)) {
                tokens.add(new Token(boolHashes.get(s), s, position, line));
                position += 2;
                document.Swallow(2);
            }
            // Throw an exception if not an operator
            else
                throw new Exception();
        }
        // catch statement catches an exception. Used for cases when operands are a
        // single character.
        catch (Exception e) {
            // Checks if next character is a valid operand
            if (charHashes.containsKey(document.Peek(0))) {
                // char c initiated to equal the next character.
                char c = document.Peek(0);
                // Checks if c is a '&'. If so, throw an immediate error
                if (c == '&')
                    throw new Exception("Cannot have single character '&'!");
                // Add single character operand to tokens if no error is thrown.
                tokens.add(new Token(charHashes.get(c), String.valueOf(c), position, line));
                // Add position and swallow, add one to line if c is newline or semicolon
                position++;
                document.Swallow(1);
                if (c == '\n' || c == ';')
                    line++;
            }
            // Change positions if c is not a recognized character
            else {
                position++;
                document.Swallow(1);
            }

        }
    }

    // Comment state method
    private void commentState() {
        // Checks if the char is a separator. For cases when it hits the end of the
        // document, the method breaks. Adds position and line.
        while (!isSeparator(document.GetChar())) {
            if (document.IsDone())
                break;
            position++;
        }
        // Adds separator token.
        tokens.add(new Token("\n", position, line));
        position++;
        line++;
    }

    // isSeparator checks if the input 'c' is a newline or semicolon.
    private boolean isSeparator(char c) {
        return c == '\n' || c == ';';
    }

    // handleStringLiteral State method
    private void handleStringLiteral() throws Exception{
        try{
        // Buffer initiated to parse through document.
        String buffer = "";
        // Skip quote when iterated over.
        document.Swallow(1);
        position++;
        // While loop to peek through document, will check as long as the next char
        // isn't a quotation mark.
        while (document.Peek(0) != '"') {
            // c initiated as the next char.
            char c = document.GetChar();
            // if c is a backslash, check if next character is a quote. Adds to buffer and
            // position is added as well. countQuotes is incremented.
            if (c == '\\' && document.Peek(0) == '"') {
                position++;
                buffer += c;
                document.Swallow(1);
                buffer = HandlePattern(buffer + '"');
            }
            // else, add 'c' to buffer
            else
                buffer += c;
        }
        // Increment positions. Add buffer to tokens.
        document.Swallow(1);
        position += buffer.length() + 2;
        tokens.add(new Token(TokenType.STRINGLITERAL, buffer, position, line));}
        catch(Exception e){
            throw new Exception("Must have an even number of quotes!");
        }
    }

    // HandlePattern initiated to mutate a stringliteral. Changes all strings '\"'
    // to just "\".
    private String HandlePattern(String s) {
        return s.substring(0, s.length() - 2) + "\"";
    }

    // setStatements() used for setting statements to tokens.
    private void setStatements() {
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

    // setSingleChar used for setting single characters to tokens.
    private void setSingleChar() {
        charHashes.put('{', TokenType.OPBRAC);
        charHashes.put('}', TokenType.CLBRAC);
        charHashes.put('[', TokenType.OPBRACE);
        charHashes.put(']', TokenType.CLBRACE);
        charHashes.put('(', TokenType.OPAREN);
        charHashes.put(')', TokenType.CPAREN);
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
        charHashes.put('`', TokenType.BACKTIC);
    }

    // setDoubleChars used for setting double characters to tokens.
    private void setDoubleChars() {
        boolHashes.put(">=", TokenType.GREQ);
        boolHashes.put("++", TokenType.ADD);
        boolHashes.put("--", TokenType.SUBT);
        boolHashes.put("<=", TokenType.LEEQ);
        boolHashes.put("==", TokenType.EQUALS);
        boolHashes.put("!=", TokenType.NEQ);
        boolHashes.put("^=", TokenType.CAREQ);
        boolHashes.put("%=", TokenType.PEREQ);
        boolHashes.put("*=", TokenType.TIEQ);
        boolHashes.put("/=", TokenType.DIVEQ);
        boolHashes.put("+=", TokenType.PLEQ);
        boolHashes.put("-=", TokenType.MINEQ);
        boolHashes.put("!~", TokenType.REGEXP);
        boolHashes.put("&&", TokenType.AND);
        boolHashes.put(">>", TokenType.LEADS);
        boolHashes.put("||", TokenType.OR);

    }

    // Returns the 'tokens' variable.
    public LinkedList<Token> getTokens() throws Exception{
        Lex();
        return tokens;
    }
}
