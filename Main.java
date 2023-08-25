import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        /*
         * Exists - Done, reads file with GetAllBytes - Done?, calls lex, prints tokens (10) - Done
         */
        try {
            // Reads files with getAllBytes.
            Path myPath = Paths.get("test.awk");
            String content = new String(Files.readAllBytes(myPath));
            // Lexer object created to create a linkedlist and display the tokens
            Lexer translator = new Lexer(content);
            // Prints Tokens
            translator.Lex();
            System.out.println(translator.getTokens());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
