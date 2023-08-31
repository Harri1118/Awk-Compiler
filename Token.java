public class Token {

    // Enum initiated with WORD, NUMBER, and SEPARATOR
    private enum TokenType {
        WORD,
        NUMBER,
        SEPARATOR
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

    // Constructor used for int type cases
    public Token(double v, int p, int l) {
        type = TokenType.NUMBER;
        value = v;
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

    // Conf method returns a string which gets rid of the '.0' section of a string
    // value when the number has no decimal.
    public String conv(String s) {
        int l = s.length();
        return s.substring(0, l - 2);
    }

    // returns the line in which the variable is part of.
    public int getLine() {
        return ln;
    }

    // returns the position in the document that the token started in.
    public int getStartPos() {
        return startPos;
    }
}
