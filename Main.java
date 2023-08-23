import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {

        try {
            // Reads files with getAllBytes.
            Path myPath = Paths.get("test.awk");
            String content = new String(Files.readAllBytes(myPath));
            System.out.println(content);
            // Calls Lex
            Lex translator = new Lex(0, 0, content);
            // Prints Tokens
            System.out.println(translator.tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}