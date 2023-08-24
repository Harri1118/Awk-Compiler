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
    private String toString = "";

    public Token(String s, int l, int p) {
        type = TokenType.WORD;
        str = s;
        ln = l;
        charPos = p;
        toString += "WORD(" + str + ")";
    }

    public Token(int v, int l, int p) {
        type = TokenType.NUMBER;
        str = String.valueOf(v);
        ln = l;
        charPos = p;
        toString += "NUMBER(" + str + ")";
    }

    public Token(int l, int p) {
        type = TokenType.SEPARATOR;
        ln = l;
        charPos = p;
        toString += "SEPARATOR";
    }

    public String toString() {
        return toString;
    }
}
