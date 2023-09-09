package project2;

public class Token {

    // 2. Has String numbers ?
    // 3. Has line number - Done
    // 4. Has start position - Done
    // 5. Has both token constructors - Done
    // 6. toString() Exists and outputs type and value members clearly - Done
    // 7. All four (variables) are correct and private - Done

    // Enum initiated with WORD, NUMBER, and SEPARATOR
    public enum TokenType {
        WORD,
        NUMBER,
        SEPARATOR,
        WHILE,
        IF,
        DO,
        FOR,
        BREAK,
        CONTINUE,
        ELSE,
        RETURN,
        BEGIN,
        END,
        PRINT,
        PRINTF,
        NEXT,
        IN,
        DELETE,
        GETLINE,
        EXIT,
        NEXTFILE,
        FUNCTION,
        STRINGLITERAL,
        // Single character types
        OPBRAC,
        CLBRAC,
        SQOPBRAC,
        SQCLBRAC,
        OPENPAREN,
        CLOSEPAREN,
        DOLLAR,
        TILDE,
        ASSIGN,
        LETHAN,
        GRTHAN,
        EXLPT,
        PLUS,
        CARROT,
        MINUS,
        QMARK,
        COLON,
        STAR,
        SLASH,
        PERCNT,
        VERTBAR,
        COMMA,
        // Two character symbols
        GREQ,
        ADD,
        SUBT,
        LEEQ,
        EQUALS,
        NEQ,
        CAREQ,
        PEREQ,
        MEQ,
        DIVEQ,
        PLEQ,
        MINEQ,
        REGEXP,
        AND,
        LEADS,
        OR
    }

    // Type used to specify enum type
    private TokenType type;

    // Holds string value
    private String str;

    // Holds numerical value
    private double value;

    // Holds line #
    private int ln;

    // Holds string position in text file
    private int startPos;

    // Constructor used for string type cases
    public Token(String s, int p, int l) {
        if (s.charAt(0) == 10)
            type = TokenType.SEPARATOR;
        else
            type = TokenType.WORD;
        str = s;
        ln = l;
        startPos = p;

    }

    public Token(TokenType t, String s, int p, int l) {
        type = t;
        ln = l;
        startPos = p;
        str = s;
        if (t == TokenType.NUMBER) {
            value = Double.valueOf(s);
        }
    }

    // Shows what the file would look like as a toString
    public String toString() {
        if (type == TokenType.SEPARATOR)
            return "SEPARATOR";
        String f = "";
        if (type == TokenType.WORD)
            f += "WORD(";
        else if (type == TokenType.NUMBER)
            f += "NUMBER(";
        else if (type == TokenType.STRINGLITERAL)
            f += "STRINGLITERAL(";
        else
            return String.valueOf(type);

        return f + str + ")";
    }

    public int getLine() {
        return ln;
    }

    public int getStartPos() {
        return startPos;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return str;
    }
}
