import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {

        try {
            // Reads files with getAllBytes.
            Path myPath = Paths.get("test.awk");
            String content = new String(Files.readAllBytes(myPath));
            // Calls Lex
            Lex translator = new Lex(0, 0, content);
            // Prints Tokens
            for (int i = 0; i < translator.tokens.size(); i++)
                System.out.println(translator.tokens.get(i).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
