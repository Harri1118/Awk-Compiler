import java.util.LinkedList;

public class Lex {
    // 1. Exists, holds StringHandler, line number and position
    // 2. Skips appropriate values, calls ProcessWord and ProcessNumber and adds
    // their return values to the list (10)
    private int line;
    private int position;
    public LinkedList<Token> tokens = new LinkedList<>();

    public Lex(int l, int p, String s) {
        l = line;
        p = position;

        boolean tempRunner = false;
        while (tempRunner == false) {

            Lexer reader = new Lexer(s, line, position);
            String t = reader.getToken().getString();
            if (notNum(t) == true)
                processWord(t);
            else
                processNumber(t);
        }

    }

    public String processWord(String s) {
        return "";
    }

    public int processNumber(String s) {
        return 0;
    }

    private boolean notNum(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)) == false) {
                return true;
            }
        }
        return false;
    }
}