public class Token {
    // 1. Has enum
    // 2. Has String numbers
    // 3. Has line number
    // 4. Has start position
    // 5. Has both token constructors
    // 6. toString() Exists and outputs type and value members clearly
    // 7. All four (variables) are correct and private
    enum TokenType {
        WORD,
        NUMBER,
        SEPARATOR
    }

    private TokenType type;
    private String str;
    private int ln;
    private int charPos;

    public Token(String s, int i, int p) {
        str = s;
        ln = i;
        charPos = p;
    }

    public Token() {
        type = TokenType.SEPARATOR;
    }

    public String toString() {
        String f = "(";
        return "";
    }

    public String getString() {
        return str;
    }

}