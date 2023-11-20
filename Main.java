package icsi311;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception{
        /*
         * Tries to run code, if it doesn't work it throws an exception.
         */
            if(args.length < 2)
                throw new Exception("args have not been declared!");
            String scriptPath = args[0];
            String inputPath = args[1];
            // Reads files with getAllBytes.
            Path myPath = Paths.get(scriptPath);
            String content = new String(Files.readAllBytes(myPath));
            //Lexer object created to create a linkedlist and display the tokens
            Lexer translator = new Lexer(content);
            //Parser initiated and is printed when Parse() is called.
            Parser parser = new Parser(translator.getTokens());
            // With parser initiated, interpret the program by retrieving the programnode from
            // parse, and then interpret the programnode produced from parse and pass in
            // the input file path.
            Path input = Paths.get(inputPath);
            Interpreter interpreter = new Interpreter(parser.Parse(), Optional.of(input));
            interpreter.InterpretProgram();

    }
}
