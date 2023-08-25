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
        SEPARATOR
    }

    // Type used to specify enum type
    private TokenType type;

    // Holds string value
    private String str;

    // Holds line #
    private int ln;

    // Holds string position in text file
    private int startPos;

    // Constructor used for string type cases
    public Token(String s, int l, int p) {
        if(s.charAt(0) == 10)
            type = TokenType.SEPARATOR;
        else
            type = TokenType.WORD;
        str = s;
        ln = l;
        startPos = p;

    }

    // Constructor used for int type cases
    public Token(double v, int l, int p) {
        type = TokenType.NUMBER;
        str = String.valueOf(v);
        if (str.charAt(str.length() - 1) == 48)
            str = conv(str);
        ln = l;
        startPos = p;

    }

    // Shows what the file would look like as a toString
    public String toString() {
        String f = "";
        if (type == TokenType.WORD)
            f += "WORD(";
        else if (type == TokenType.NUMBER)
            f += "NUMBER(";
        else if (type == TokenType.SEPARATOR)
            return "SEPARATOR";

        return f + str + ")";
    }

    public String conv(String s) {
        int l = s.length();
        return s.substring(0, l - 2);
    }

    public int getLine() {
        return ln;
    }

    public int getStartPos() {
        return startPos;
    }
}
