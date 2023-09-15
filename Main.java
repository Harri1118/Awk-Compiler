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
            Path myPath = Paths.get("D:\\icsi311\\project2\\test.awk");
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
