package icsi311;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        /*
         * Tries to run code, if it doesn't work it throws an exception.
         */
        try {
            // Reads files with getAllBytes.
            Path myPath = Paths.get("test.awk");
            String content = new String(Files.readAllBytes(myPath));
            //Lexer object created to create a linkedlist and display the tokens
            Lexer translator = new Lexer(content);
            //Parser initiated and is printed when Parse() is called.
            Parser parser = new Parser(translator.getTokens());
            // With parser initiated, interpret the program by retrieving the programnode from
            // parse, and then interpret the programnode produced from parse and pass in
            // the input file path.
            Path inputPath = Paths.get("input.txt");
            Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(inputPath));
            ProgramNode p = parser.Parse();
            System.out.println(interpreter.InterpretedListOfStatements(null, p.OTHER.get(0).getStatements()));
            System.out.println(interpreter.GlobalVariables.get("a").toString());
            System.out.println(interpreter.GlobalVariables.get("b").toString());
            System.out.println(interpreter.GlobalVariables.get("c").toString());
            System.out.println(interpreter.GlobalVariables.get("d").toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
