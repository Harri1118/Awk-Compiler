public class Token {
    
    // 2. Has String numbers
    // 3. Has line number
    // 4. Has start position
    // 5. Has both token constructors
    // 6. toString() Exists and outputs type and value members clearly
    // 7. All four (variables) are correct and private

    // Enum initiated with WORD, NUMBER, and SEPARATOR
    enum TokenType {
        WORD,
        NUMBER,
        SEPARATOR
    }

    //Type used to specify enum type
    private TokenType type;

    //Holds string value
    private String str;

    //Holds line #
    private int ln;

    //Holds string position in text file
    private int charPos;

    private String toString = "";

    //Constructor used for string type cases
    public Token(String s, int l, int p) {
        type = TokenType.WORD;
        str = s;
        ln = l;
        charPos = p;
        toString += "WORD(" + str + ")";
    }

    //Constructor used for int type cases
    public Token(int v, int l, int p) {
        type = TokenType.NUMBER;
        str = String.valueOf(v);
        ln = l;
        charPos = p;
        toString += "NUMBER(" + str + ")";
    }

    //Constructor used for newLine type cases
    public Token(int l, int p) {
        type = TokenType.SEPARATOR;
        ln = l;
        charPos = p;
        toString += "SEPARATOR";
    }

    //Shows what the file would look like as a toString
    public String toString() {
        return toString;
    }
}
