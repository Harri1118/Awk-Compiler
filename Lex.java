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
        StringHandler document = new StringHandler(s);
        boolean b = false;
        while (b == false) {
            if (document.IsDone() == true)
                break;
            int i = 0;
            char c;
            while (document.Peek(i) != ' ' && document.Peek(i) != '\n') {
                c = document.Peek(i);
                i = i + 1;

            }
            if (document.Peek(i) == '\n' && i == 0) {
                document.Swallow(1);
                position++;
                line++;
                i++;
            }
            if (i == 0) {
                document.Swallow(1);
                position++;
            } else {
                String w = document.PeekString(i);
                document.Swallow(w.length());
                Lexer lexer = new Lexer(w, line, position);
                position = position + w.length();
                tokens.add(lexer.getToken());
                System.out.println(tokens.get(tokens.size() - 1).toString());
            }
        }
    }

}
