package icsi311;

import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        /*
         * Tries to run code, if it doesn't work it throws an exception.
         */
        try {
            // Reads files with getAllBytes.
            Path myPath = Paths.get("test.awk");
            String content = new String(Files.readAllBytes(myPath));
            // Lexer object created to create a linkedlist and display the tokens
            Lexer translator = new Lexer(content);
            // Prints Tokens
            translator.Lex();
            //Parser initiated and is printed when Parse() is called.
            Parser parser = new Parser(translator.getTokens());
            System.out.println(parser.Parse());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
