package icsi311;

import java.util.LinkedList;
import java.util.Optional;



public class Parser {

    TokenManager tokenManager;
    public Parser(LinkedList<Token> t){
        tokenManager = new TokenManager(t);
    }


    // Parsemethod created to parse through the TokenManager object
    public ProgramNode Parse() throws Exception {
        // Will throw an exception if Parsed incorrectly
        try{ProgramNode n = new ProgramNode();
            //While loop to check if tokenManager has more tokens
        while(tokenManager.MoreTokens()){
            AcceptSeparators();
            //If ParseFunction and Parseaction aren't true, then it throws an exception
        if(!ParseFunction(n) && !ParseAction(n))
            throw new Exception("Not a valid program structure!");
        }
            return n;
        }
        // This exception is caught in cases where the program ends abruptly
        catch(IndexOutOfBoundsException e){
            System.out.println("Reached end of line before proper method return!");
            return null;
        }
        //This exception is caught if any other exception occurs
        catch(Exception e){
            System.out.println(e);
            return null;
        }


    }

    // ParseFunction called to iterate through a function if in script
    private boolean ParseFunction(ProgramNode n) throws Exception{
        //AcceptSeparators called to get rid of any pesky SEPARATOR tokens
        AcceptSeparators();
        // Checks if function is in list, will throw false if not
        Optional<Token> t = tokenManager.MatchAndRemove(Token.TokenType.FUNCTION);
        if(t.isEmpty())
            return false;
        //tk is declared for in cases when there's no WORD tokenType, it will be checked if it returns a token and throw an exception if not
        Optional<Token> tk = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        if(tk.isEmpty())
            throw new Exception("Must have a valid method name!");
        // Name declared when tk is not empty
        String name = tk.get().toString();
        // Checks for parenthesis, checks for params, throws Exception if parenthesis aren't valid
        if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAREN).isEmpty())
            throw new Exception("Function must have parenthesis!");
        String[] params = parseParamsToStringList();
        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAREN).isEmpty())
            throw new Exception("Function must have parenthesis!");
        // Parses for a BlockNode
        BlockNode b = ParseBlock();
        // Adds a new FunctionDefinitionNode
        n.functions.add(new FunctionDefinitionNode(name, params, b.getStatements()));
        return true;
    }
    // Parse action called for in cases of BEGIN or END
    private boolean ParseAction(ProgramNode n) throws Exception {
        AcceptSeparators();
        // Peeks to see what sort of action it could be
        Optional<Token> t = tokenManager.Peek(0);
        // Declared type to prepare for if statement, if its BEGIN END OR OPBRAC, then it is valid, if not, return false
        Token.TokenType ty = t.get().getType();
        if (ty == Token.TokenType.BEGIN || ty == Token.TokenType.END || ty == Token.TokenType.OPBRAC) {
            // When passed, it will ParseBlock for each case. Action Not Recognized Exception is a placeholder
            if (!(tokenManager.MatchAndRemove(Token.TokenType.BEGIN).isEmpty()))
                n.BEGIN.add(ParseBlock());
            else if (!(tokenManager.MatchAndRemove(Token.TokenType.END).isEmpty()))
                n.END.add(ParseBlock());
            else
                throw new Exception("Action not recognized!");
            return true;
        }

        return false;
    }
    // ParseBlock called when ParseFunction or ParseAction are called
    private BlockNode ParseBlock(){
        AcceptSeparators();
        /*Optional<Node> o = ParseOperation();

        if(!(tokenManager.MatchAndRemove(Token.TokenType.OPBRAC).get().getType() == Token.TokenType.OPBRAC))
            throw new Error();
        while(tokenManager.Peek(0).get().getType() != Token.TokenType.CLBRAC){
            Token t = tokenManager.Peek(0).get();
            tokenManager.MatchAndRemove(t.getType());
        }
        if(!(tokenManager.MatchAndRemove(Token.TokenType.CLBRAC).get().getType() == Token.TokenType.CLBRAC))
            throw new Error();*/

        return new BlockNode(null,null);
    }

    //ParseOperationCalled in ParseBlock. Isn't implemented yet
    private Optional<Node> ParseOperation(){
        /*if(!(tokenManager.MatchAndRemove(Token.TokenType.OPENPAREN).isEmpty())){
        LinkedList<Token> p = parseParams();
        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAREN).isEmpty())
            throw new Error();
        }*/
        return Optional.empty();
    }

    // AcceptSeparators returns true after all the separators in the TokenManager are removed until the next non-separator
    private boolean AcceptSeparators() {
        // Recursive method which removes a separator until either the MatchAndRemove returns empty or an Exception is thrown. Whereas it will return false.
        try{
        Optional<Token> t = tokenManager.MatchAndRemove(Token.TokenType.SEPARATOR);
        if(!(t.isEmpty())){
            AcceptSeparators();
            return true;
        }
        return false;}
        catch(Exception e){
            return false;}
    }

    // parseParams looks through the params entered in a function declaration
    private LinkedList<Token> parseParams() throws Exception{
        //Makes a LinkedList of tokens for params
       LinkedList<Token> params = new LinkedList<Token>();
       boolean switcher = false;
       // Continually checks for words and commas, if a comma comes after a word and the next token is a word, then keep going, or else return false
       while(tokenManager.Peek(0).get().getType() == Token.TokenType.WORD){
           params.add(tokenManager.MatchAndRemove(Token.TokenType.WORD).get());
           switcher = false;
           if(tokenManager.MatchAndRemove(Token.TokenType.COMMA).isEmpty())
               break;
           else
               switcher = true;
       }
       // Throws errors in cases where a parenthesis doesn't have a subsequent word afterwards or when the closing parenthesis isn't there.
       if(!(tokenManager.Peek(0).get().getType() == Token.TokenType.CLOSEPAREN))
           throw new Exception("Must have parenthesis for proper parameter declaration!");
       else if(switcher == true)
           throw new Exception("Cannot have comma at the end of a parameter list!");
        return params;
    }

    // parseParamsToStringList called when converting ParseParams to a String[]. Used for function call declarations
    private String[] parseParamsToStringList() throws Exception{
        LinkedList<Token> params = parseParams();
        String[] f = new String[params.size()];
        for(int i = 0; i < params.size(); i++)
            f[i] = params.get(i).toString();
        return f;
    }

    private Node Factor() throws Exception{
        Optional<Token> num = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
        if(!(num.isEmpty()))
            return new ConstantNode(num.get().getVal());
        if(!(tokenManager.MatchAndRemove(Token.TokenType.OPENPAREN).isEmpty())){
            Node exp = Expression();
            if(exp == null)
                throw new Exception("Idk what this is yet lol");
        }
        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAREN).isEmpty())
            throw new Exception("Idk what this is yet lol");
        return null;
    }
    private Node Term() throws Exception{
        Node left =  Factor();
        do{
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.STAR);
            if(op.isEmpty())
                op = tokenManager.MatchAndRemove(Token.TokenType.SLASH);
            if(op.isEmpty())
                return left;
            Optional<Node> right = Optional.of(Factor());
            left = new OperationNode(left, op.get().getType(), right);
        }
        while(true);
    }

    private Node Expression() throws Exception{
        Node left = Term();
        do{
            Optional<Token> op =  tokenManager.MatchAndRemove(Token.TokenType.PLUS);
            if(op.isEmpty())
                op = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
            if(op.isEmpty())
                return left;
            Node right = Term();
            left = new OperationNode(left, op.get().getType());
        } while(true);

    }


    private Optional<Node> ParseBottomLevel(){
        if(tokenManager.Peek(0).get().getType() == Token.TokenType.STRINGLITERAL){}
        else if(tokenManager.Peek(0).get().getType() == Token.TokenType.NUMBER){}


        return null;
    }

    /*
    * ParseLValue looks for these patterns:
    DOLLAR + ParseBottomLevel()  OperationNode(value, DOLLAR)
    WORD + OPENARRAY + ParseOperation() + CLOSEARRAY  VariableReferenceNode(name, index)
    WORD (and no OPENARRAY)  VariableReferenceNode(name)
    * */
    private Optional<Node> ParseLValue(){
        return null;
    }

    private boolean checkPattern(){
        Optional<Token> t = Optional.of(tokenManager.Peek(0).get());
        if(t.isEmpty())
            return false;
        else if(t.get().getType() == Token.TokenType.STAR || t.get().getType() == Token.TokenType.SLASH)
            return true;
        return false;
    }

}
