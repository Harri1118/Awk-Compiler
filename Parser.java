package icsi311;

import org.w3c.dom.Node;

public class Parser {

    // One thing that is always tricky in parsing languages is that people can put
    // empty lines anywhere they want in their code. Since the parser expects
    // specific tokens in specific
    // places, it happens frequently that we want to say, “there HAS to be a “;” or
    // a new line, but there can
    // be more than one”. That’s what this function does – it accepts any number of
    // separators (newline or semi-colon)
    // and returns true if it finds at least one.
    private boolean AcceptSeparators() {
        return false;
    }

    // Create a Parse method that returns a ProgramNode. While there are more tokens
    // in the TokenManager,
    // it should loop calling two other methods – ParseFunction() and ParseAction().
    // If neither one is true,
    // it should throw an exception.
    private Node Parse() {
        return null;
    }

    private boolean ParseFuncion(Node n) {
        return false;
    }

    private boolean ParseAction(Node n) {
        return false;
    }

}
