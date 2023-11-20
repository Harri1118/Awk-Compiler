package icsi311;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

public class Parser {
    // BooleamCompares HashMap meant for making ParseCompares() method easier to parse through
    private HashMap<Token.TokenType, OperationNode.PossibleOperations> BooleanCompares = new HashMap<Token.TokenType, OperationNode.PossibleOperations>();

    // AssignmentCompares HashMap meant for making ParseAssignment() method easier to parse through
    private HashMap<Token.TokenType, OperationNode.PossibleOperations> AssignmentCompares = new HashMap<Token.TokenType, OperationNode.PossibleOperations>();

    // importantMethods linkedList created to search for special methods which the interpreter will utilize
    private LinkedList<Token.TokenType> importantMethods = new LinkedList<Token.TokenType>();
    // tokenManager created to parse through Lexer tokens
    TokenManager tokenManager;

    // BooleanCompares, OperationTypes, and AssignmentCompares are delcared and given hash values to make parsing faster
    public Parser(LinkedList<Token> t) {
        addBooleanCompares();
        addAssignments();
        initializeImportantFunctions();
        tokenManager = new TokenManager(t);
    }


    // Parsemethod created to parse through the TokenManager object
    public ProgramNode Parse() throws Exception {
        // Will throw an exception if Parsed incorrectly
        try {
            ProgramNode Program = new ProgramNode();
            //While loop to check if tokenManager has more tokens
            while (tokenManager.MoreTokens()) {
                AcceptSeparators();
                //If ParseFunction and Parseaction aren't true, then it throws an exception
                if (!ParseFunction(Program) && !ParseAction(Program))
                    throw new Exception("Not a valid program structure!");
                AcceptSeparators();
            }
            return Program;
        }
        //This exception is caught if any other exception occurs
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // ParseFunction called to iterate through a function if in script
    private boolean ParseFunction(ProgramNode Program) throws Exception {
        //AcceptSeparators called to get rid of any pesky SEPARATOR tokens
        AcceptSeparators();
        // Checks if function is in list, will throw false if not
        Optional<Token> t = tokenManager.MatchAndRemove(Token.TokenType.FUNCTION);
        if (!tokenManager.MoreTokens())
            throw new Exception("File ended before function parsed!");
        if (t.isEmpty())
            return false;
        //tk is declared for in cases when there's no WORD tokenType, it will be checked if it returns a token and throw an exception if not
        Optional<Token> tk = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        // Checks if there's a function name, will return a new exception if no name is found
        if (tk.isEmpty())
            throw new Exception("Must have a valid method name!");
        // Name declared when tk is not empty
        String name = tk.get().toString();
        if (!tokenManager.MoreTokens())
            throw new Exception("File ended before function parsed!");
        // Checks for parenthesis, checks for params, throws Exception if parenthesis aren't valid
        if (tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty())
            throw new Exception("Function must have parenthesis for proper parameter declaration! Ex: function myFunction(a){}");
        String[] params = parseParamsToStringList();
        if (tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
            throw new Exception("Function must have parenthesis for proper parameter declaration! Ex: function myFunction(a){}");
        // Parses for a BlockNode
        BlockNode b = ParseBlock();
        // Adds a new FunctionDefinitionNode
        Program.addFunction(new FunctionDefinitionNode(name, params, b.getStatements()));
        return true;
    }

    // Parse action called for in cases of BEGIN or END
    private boolean ParseAction(ProgramNode Program) throws Exception {
        AcceptSeparators();
        // Declared type to prepare for if statement, if its BEGIN END OR OPBRAC, then it is valid, if not, return false
        // matchAndRemove BEGIN, if so then Parse it and add it, return true
        if (!tokenManager.MatchAndRemove(Token.TokenType.BEGIN).isEmpty()) {
            Program.addBegin(ParseBlock());
            return true;
        }
        // matchAndRemove END, if so then Parse it and add it, return true
        else if (!tokenManager.MatchAndRemove(Token.TokenType.END).isEmpty()) {
            Program.addEnd(ParseBlock());
            return true;
        }
        // ParseCondition. If returned then ParseBlock, return true
        Optional<Node> Condition = ParseOperation();
        Optional<BlockNode> Block = Optional.of(ParseBlock());
        if (Block.isEmpty())
            return false;
        if (Condition.isEmpty()) {
            Program.addOther(new BlockNode(Block.get().getStatements()));
            return true;
        }
        // Adds neww blocknode to other lists
        Program.addOther(new BlockNode(Condition.get(), Block.get().getStatements()));
        return true;
    }

    // ParseBlock called when ParseFunction or ParseAction are called
    private BlockNode ParseBlock() throws Exception {
        // Checks for more tokens, will throw an exception if none is found
        if (!tokenManager.MoreTokens())
            throw new Exception("Error! File ended before program could parse block!");
        // Creates list of statements for Block to parse
        LinkedList<StatementNode> statements = new LinkedList<StatementNode>();
        // Accepts Separators in case there is a separator up next
        AcceptSeparators();
        // Checks for Openbracket. If there is none, parse only one line
        if (tokenManager.MatchAndRemove(Token.TokenType.OPBRAC).isEmpty()) {
            // Checks again for separators
            AcceptSeparators();
            // Singular statement parsed
            Optional<StatementNode> statement = ParseStatement();
            // Throws an exceptuon if statement was empty.
            if (statement.isEmpty())
                throw new Exception("Invalid program structure! Must have a valid block structure! ex: BEGIN{Operation}");
            // Returns a new BlockNdoe with a singular Statement
            return new BlockNode(statement.get());
        }
        // In cases where there is no singular statement
        do {
            // Accept Separators before statement is parsed
            AcceptSeparators();
            // Statement is parsed
            Optional<StatementNode> statement = ParseStatement();
            // If empty, break the dowhile loop
            if (statement.isEmpty())
                break;
            // Add the parsed statement
            statements.add(statement.get());
            // Accept more separators in case after statement is parsed
            AcceptSeparators();
        }
        while (true);
        // In cases where there are separators between the last statement and the closing brace, accept separators
        AcceptSeparators();
        // MatchAndRemove close bracket. If there is none then throw an exception
        if (tokenManager.MatchAndRemove(Token.TokenType.CLBRAC).isEmpty())
            throw new Exception("Invalid program structure! Must have a valid block structure! ex: BEGIN{Operation}");
        // Create new blockNode with statemeents
        return new BlockNode(statements);
    }

    // ParseStatement to parse through all statements in a blockNode
    public Optional<StatementNode> ParseStatement() throws Exception {
        // Checks for Any sorts of separators.
        AcceptSeparators();
        // matchAndRemove all keywords. Parse them to their respective methods
        if (!tokenManager.MatchAndRemove(Token.TokenType.CONTINUE).isEmpty())
            return ParseContinue();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.BREAK).isEmpty())
            return ParseBreak();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.IF).isEmpty())
            return ParseIf();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.FOR).isEmpty())
            return ParseFor();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.WHILE).isEmpty())
            return ParseWhile();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.DO).isEmpty())
            return ParseDoWhile();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.DELETE).isEmpty())
            return ParseDelete();
        else if (!tokenManager.MatchAndRemove(Token.TokenType.RETURN).isEmpty())
            return ParseReturn();
            // In cases where an else is found (without any sort of if), throw a new Exception
        else if (!tokenManager.MatchAndRemove(Token.TokenType.ELSE).isEmpty())
            throw new Exception("Cannot call 'else' and 'else if' outside of any initial if statement!");
            // In cases where else, Parse a single operation
        else {
            // ParseOperation called
            Optional<Node> singleOperation = ParseOperation();
            // if empty, return empty
            if (singleOperation.isEmpty())
                return Optional.empty();
            // Return new ParseOperation()
            return Optional.of((StatementNode) singleOperation.get());
        }
    }

    // Parses continue statements
    private Optional<StatementNode> ParseContinue() {
        return Optional.of(new ContinueNode());
    }

    // Parses break statements
    private Optional<StatementNode> ParseBreak() {
        return Optional.of(new BreakNode());
    }

    // Parses if statements
    private Optional<StatementNode> ParseIf() throws Exception {
        // Parsees an operation for the if statement
        Optional<Node> Condition = ParseOperation();
        // If the condition is empty, throw a new exception
        if (Condition.isEmpty())
            throw new Exception("If must have a Condition! Ex: If(EXPR){STATEMENT}");
        // Parse for block in if node
        BlockNode Block = ParseBlock();
        // Account for next and else statements
        Optional<StatementNode> Next = Optional.empty();
        Optional<BlockNode> ElseBlock = Optional.empty();
        // If no more tokens after block is parsed, return the ifnode
        if (!tokenManager.MoreTokens())
            return Optional.of(new IfNode(Condition.get(), Block));
        // AcceptSeparators
        AcceptSeparators();
        // Check for else token, remove it if necessary
        if (!tokenManager.MatchAndRemove(Token.TokenType.ELSE).isEmpty()) {
            // Checks for if token, removes it if necessary and performs actions when removed
            if (tokenManager.MatchAndRemove(Token.TokenType.IF).isEmpty()) {
                // Checks for open parenthesis after an else statement but no 'if' statement, throws an exception if found
                if (!tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty())
                    throw new Exception("Else statements cannot have parameters!");
                // Parses a block for the elseBlock
                ElseBlock = Optional.of(ParseBlock());
            }
            // Parses an if for an else-if statement if there is an 'if' after 'else'.
            else {
                Next = ParseIf();
            }
        }
        // If next isn'y empty, return a new ifNode with a condition, block, and next node
        if (!Next.isEmpty())
            return Optional.of(new IfNode(Condition.get(), Block, Next));
        // If elseBlock isn't empty, assign it to ifnode and return it
        if (!ElseBlock.isEmpty()) {
            Next = Optional.of(new IfNode(ElseBlock.get()));
            return Optional.of(new IfNode(Condition.get(), Block, Next));
        }
        // Return ifNode with only condition and statements if no else ifs or elses are found
        return Optional.of(new IfNode(Condition.get(), Block));
    }

    // Parses for loops
    private Optional<StatementNode> ParseFor() throws Exception {
        // Check ahead for In and semi colon with do-while. int i is declared to controll the peeks ahead. ReferenceToken initialzed (to be peeked)
        int i = 0;
        Optional<Token> ReferenceToken = Optional.empty();
        // Cheks ahead to see the structure of the for loop
        try {
            do {
                // ReferenceTokens peeks ahead to check and see if token is IN or SEPARATOR
                ReferenceToken = tokenManager.Peek(i);
                // After peeked, i++
                i++;
                // If ReferenceToken is IN or SEPARATOR, escape the do-while
                if (ReferenceToken.get().getType() == Token.TokenType.IN || ReferenceToken.get().getType() == Token.TokenType.SEPARATOR)
                    break;
                // Checks for close parenthesis. If one is found, throw an exception
                if (ReferenceToken.get().getType() == Token.TokenType.CPAREN)
                    throw new Exception("For loop params cannot be empty!");
            }
            while (true);
        }
        // If do-while loop reaches end of the document. Throw a new exception
        catch (Exception e) {
            throw new Exception("For loop params cannot be empty!");
        }
        // If for separator, create a new ForNode
        if (ReferenceToken.get().getType() == Token.TokenType.SEPARATOR) {
            // If OPAREN is empty, throw a new Exception
            if (tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty())
                throw new Exception("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])");
            // Create condition LinkedList
            LinkedList<Node> ConditionList = new LinkedList<Node>();
            Optional<Node> nextOp = ParseOperation();
            if (nextOp.isEmpty())
                throw new Exception("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])");
            // Add ParseOperation to list
            ConditionList.add(nextOp.get());
            // If AcceptSeparators return false, throw a new error (must have separators between each for loop operation to properly function
            if (AcceptSeparators() == false)
                throw new Exception("Error! For loop params must have a valid condition! ex: for(expr;expr;expr){}");
            // Parse another operation
            nextOp = ParseOperation();
            if (nextOp.isEmpty())
                throw new Exception("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])");
            // Add ParseOperation to list
            ConditionList.add(nextOp.get());
            // If AcceptSeparator is empty, throw another exception
            if (AcceptSeparators() == false)
                throw new Exception("Error! For loop params must have a valid condition! ex: for(expr;expr;expr){}");
            nextOp = ParseOperation();
            if (nextOp.isEmpty())
                throw new Exception("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])");
            // Add ParseOperation to list
            ConditionList.add(nextOp.get());
            // If cParen is empty, throw another exception
            if (tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
                throw new Exception("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])");
            // Block is parsed
            BlockNode Block = ParseBlock();
            // Return new ForNode after it passes all tests
            return Optional.of(new ForNode(ConditionList, Block));
        }
        // If has IN, return ForEachNode
        Optional<Node> Condition = ParseOperation();
        // If condition is empty, throw new exception
        if (Condition.isEmpty())
            throw new Exception("For loop must have a condition! ex: for(EXPR){}");
        // ParseBlock
        BlockNode Block = ParseBlock();
        // return new ForEachNode if not ForNode
        return Optional.of(new ForEachNode(Condition.get(), Block));
    }

    // ParseDelete
    private Optional<StatementNode> ParseDelete() throws Exception {
        // Target parsed first
        Optional<Node> Target = ParseOperation();
        // If target is empty, throw new Exception
        if (Target.isEmpty())
            throw new Exception("Must have a statement accompanying the Delete statement! Ex: 'delete: EXPR'");
        // Check if target is an instanceof VariableReferenceNode. If not, throw an exception
        if (!(Target.get() instanceof VariableReferenceNode))
            throw new Exception("Target must be an array element! Ex: delete a[\"test\"]");
        // Return new DeleteNode with Target in constructor
        return Optional.of(new DeleteNode(Target.get()));
    }

    // Parses while loops
    private Optional<StatementNode> ParseWhile() throws Exception {
        // Parse Condition
        Optional<Node> Condition = ParseOperation();
        // If condition is empty, throw an exception
        if (Condition.isEmpty())
            throw new Exception("Must be a valid WHILE loop! EX: While(EXP){}");
        // If not, ParseBlock and Condition
        return Optional.of(new WhileNode(Condition.get(), ParseBlock()));
    }

    // Parses dowhile loops
    private Optional<StatementNode> ParseDoWhile() throws Exception {
        // ParseBlock
        BlockNode Block = ParseBlock();
        // AcceptSeparators
        AcceptSeparators();
        // Check for WHILE
        if (tokenManager.MatchAndRemove(Token.TokenType.WHILE).isEmpty())
            throw new Exception("Must have a while at the end of a do-while statement! ex: do{} while(expr)");
        // Parse for a condition
        Optional<Node> Condition = ParseOperation();
        // throw an exception if condition is empty
        if (Condition.isEmpty())
            throw new Exception("Must have a condition for a do-while loop! Ex: do{} while(EXPR)");
        // Return new DoWhile if condition and block are met
        return Optional.of(new DoWhileNode(Condition.get(), Block));
    }

    // Parses return statements
    private Optional<StatementNode> ParseReturn() throws Exception {
        // Parses operation after return is matched and removed
        Optional<Node> Value = ParseOperation();
        // Throws exception if value is empty
        if (Value.isEmpty())
            throw new Exception("Must return a value for a function! Ex: return (expr)");
        // return new returnNode if value is not empty
        return Optional.of(new ReturnNode(Value.get()));
    }

    private void initializeImportantFunctions() {
        // Methods with no parenthesis needed
        importantMethods.add(Token.TokenType.PRINT);
        importantMethods.add(Token.TokenType.PRINTF);
        importantMethods.add(Token.TokenType.GETLINE);
        importantMethods.add(Token.TokenType.EXIT);
        importantMethods.add(Token.TokenType.NEXTFILE);
        importantMethods.add(Token.TokenType.NEXT);
    }

    // Parses function calls
    private Optional<StatementNode> ParseFunctionCall() throws Exception {
        // Checks for more tokens
        if (!tokenManager.MoreTokens())
            return Optional.empty();
        // Peek word
        Optional<Token> Checker = tokenManager.Peek(0);
        // Check if Checker contains a string which points to an important function (print, printf, etc)
        if (importantMethods.contains(Checker.get().getType()))
            return ParseBuiltInMethods(Checker.get().getType());
        if (Checker.get().getType() != Token.TokenType.WORD)
            return Optional.empty();
        // Checks if character after is present. If error is thrown make this method return Optional.empty
        try {
            Checker = tokenManager.Peek(1);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
        // If closing brace, automatically return empty (this was a common character after peeking at 0 which was returned)
        if (Checker.get().getType() == Token.TokenType.CLBRACE)
            return Optional.empty();
            // Peek Parenthesis
        else if (Checker.get().getType() != Token.TokenType.OPAREN)
            return Optional.empty();
        // MatchAndRemove word
        String Name = tokenManager.MatchAndRemove(Token.TokenType.WORD).get().getValue();
        // If OPAREN is empty, throw new exception
        if (tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty())
            throw new Exception("Function must be called with ()! Ex: myFunction()");
        // If OperationNode is empty, return name with empty OperationNode
        LinkedList<Node> Params = new LinkedList<Node>();
        // Parse Condition (from parseOperation)
        Optional<Node> Condition = ParseOperation();
        // If condition is empty, return new functionCallNode
        if (Condition.isEmpty()) {
            // Check for cparen, return empty if so. If not, return new function call node
            if (tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
                throw new Exception("Incomplete Function call!");
            return Optional.of(new FunctionCallNode(Name));
        }
        // Keep checking for params until cParen is reached.
        do {
            // Add condition to params list
            Params.add(Condition.get());
            // MatchAndRemove comma. If empty then break
            if (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isEmpty())
                break;
            // MatchAndRemove cParen, if so then throw an exception (it means cParen was types after comma)
            if (!tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
                throw new Exception("Incomplete Function Call!");
            // ParseOperation for Condition and repeat process
            Condition = ParseOperation();
        }
        while (true);
        // Throw an exception if CParen is not found
        if (tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
            throw new Exception("Incomplete Function Call!");
        // Return new functionCallNode
        return Optional.of(new FunctionCallNode(Name, Params));
    }

    // ParseBuiltInMethods used to parse through methods such as print, printf, etc
    // Which are specially built for AWK programs (These methods don't require patenthesis
    // in their parameters)
    public Optional<StatementNode> ParseBuiltInMethods(Token.TokenType type) throws Exception {
        tokenManager.MatchAndRemove(type);
        // match and remove type, set it to string.
        String functionName = type.toString().toLowerCase();
        // Check if functionName is next or nextFile. Simply return a functioncall with the string
        if (functionName.equals("next") || functionName.equals("nextfile"))
            return Optional.of(new FunctionCallNode(functionName));
            // Check for exit
        else if (functionName.equals("exit")) {
            // Parse for condition.
            Optional<Node> Condition = ParseOperation();
            // Only if Condition is empty, check if it is a constantNode. Throw an error if so
            if (!Condition.isEmpty()) {
                if (!(Condition.get() instanceof ConstantNode) && !(Condition.get() instanceof OperationNode))
                    throw new Exception("Error! 'exit' cannot accept any other condition besides an int! (Or none) Example: exit Optional[int]");
                if (Condition.get() instanceof OperationNode) {
                    OperationNode ref = (OperationNode) Condition.get();
                    if (ref.getOperation() != OperationNode.PossibleOperations.DOLLAR)
                        throw new Exception("Error! 'exit' only supports field references and ints! ex: exit Optional[$(int) || (int)]");
                }
            }

            return Optional.of(new FunctionCallNode(functionName, Condition));
        }
        else if(functionName.equals("getline"))
            return Optional.of(new FunctionCallNode(functionName));
            // Check if functionName is any of the other methods (print, printf, getline)
        else {
            LinkedList<Node> Params = new LinkedList<Node>();
            // firstCondition initiated to check for errors in param types of method calls
            boolean firstCondition = true;
            Optional<Node> Condition = ParseOperation();
            // If there are no params and the method is print or printf, then throw an exception
            if (Condition.isEmpty() && (functionName.equals("print") || functionName.equals("printf")))
                throw new Exception("Error! Cannot use print/printf without statements!");
            // Keep checking for params until cParen is reached.
            do {
                // If functionName is printf and the first condition isn't a stringliteral, throw an exception
                if (functionName.equals("printf") && !(Condition.get() instanceof ConstantNode) && firstCondition == true)
                    throw new Exception("First parameter must be of a format type! Ex: printf \"Name: %s, Age: %d\\n\", name, age");
                // firstCondition remains false after the first loop
                firstCondition = false;
                // Add condition to params list
                Params.add(Condition.get());
                // MatchAndRemove comma. If empty then break. try/catch statement for testing purposes
                try {
                    if (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isEmpty())
                        break;
                } catch (Exception e) {
                    break;
                }
                // Condition is a new operation
                Condition = ParseOperation();
                // if condition is empty, then throw an exception (comma is put after a param but there is no param up next)
                if (Condition.isEmpty())
                    throw new Exception("Error! You cannot end print statements with a comma! ex: print var,$0,var2,$1");
            }
            while (true);
            return Optional.of(new FunctionCallNode(functionName, Params));
        }
    }

    // ParseOperation to parse single line statements
    public Optional<Node> ParseOperation() throws Exception {
        // Operation is ParseAssignment in order to go through the list of operation (starting at lowest operation)
        Optional<Node> Operation = ParseAssignment();
        return Operation;
    }

    // AcceptSeparators returns true after all the separators in the TokenManager are removed until the next non-separator
    public boolean AcceptSeparators() {
        // Recursive method which removes a separator until either the MatchAndRemove returns empty or an Exception is thrown. Whereas it will return false.
        try {
            Optional<Token> nextToken = tokenManager.MatchAndRemove(Token.TokenType.SEPARATOR);
            if (!(nextToken.isEmpty())) {
                AcceptSeparators();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // parseParams looks through the params entered in a function declaration
    private LinkedList<Token> ParseParams() throws Exception {
        //Makes a LinkedList of tokens for params
        LinkedList<Token> params = new LinkedList<Token>();
        boolean switcher = false;
        // Continually checks for words and commas, if a comma comes after a word and the next token is a word, then keep going, or else return false
        while (tokenManager.Peek(0).get().getType() == Token.TokenType.WORD) {
            params.add(tokenManager.MatchAndRemove(Token.TokenType.WORD).get());
            switcher = false;
            if (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isEmpty())
                break;
            else
                switcher = true;
        }
        // Throws errors in cases where a parenthesis doesn't have a subsequent word afterwards or when the closing parenthesis isn't there.
        if (!(tokenManager.Peek(0).get().getType() == Token.TokenType.CPAREN))
            throw new Exception("Function must have parenthesis for proper parameter declaration!");
        else if (switcher == true)
            throw new Exception("Cannot have comma at the end of a parameter list without properly declaring parameters! Ex: function myFunc(a,b,c)");
        return params;
    }

    // parseParamsToStringList called when converting ParseParams to a String[]. Used for function call declarations
    private String[] parseParamsToStringList() throws Exception {
        LinkedList<Token> params = ParseParams();
        String[] paramsStringList = new String[params.size()];
        for (int i = 0; i < params.size(); i++)
            paramsStringList[i] = params.get(i).toString();
        return paramsStringList;
    }

    // ParseLValue purpose is to parse through 'WORD' tokens and turn them into VariableReferenceNodes
    private Optional<Node> ParseLValue() throws Exception {
        // Checks if expression is a field reference
        if (!tokenManager.MatchAndRemove(Token.TokenType.DOLLAR).isEmpty()) {
            Optional<Node> leftValue = ParseBottomLevel();
            if (leftValue.isEmpty())
                return Optional.empty();
            return Optional.of(new OperationNode(leftValue.get(), OperationNode.PossibleOperations.DOLLAR));
        }
        // Checks if expression is a plain variable delcaration
        else if (tokenManager.Peek(0).get().getType() == Token.TokenType.WORD) {
            // name declared to be used for later use cases
            Token name = tokenManager.MatchAndRemove(Token.TokenType.WORD).get();
            // In some cases, tokenManager will run out of tokens, return name if so.
            if (!tokenManager.MoreTokens())
                return Optional.of(new VariableReferenceNode(name.getValue()));
            // Check for braces, will create 1d (or 2d) if so
            if (!tokenManager.MatchAndRemove(Token.TokenType.OPBRACE).isEmpty()) {
                // Parses through expression inside braces
                Optional<Node> leftOperation = ParseOperation();
                // In the case the expression is finished and there is no closed brace, an exception will be thrown
                if (!tokenManager.MoreTokens() || tokenManager.MatchAndRemove(Token.TokenType.CLBRACE).isEmpty())
                    throw new Exception("Error! Complete a full operation with a closing brace for the array! ex: VARIABLE[OPERATION]");
                // If closing brace is found but there is no more tokens, return new variableReferenceNode(name)
                if (!tokenManager.MoreTokens())
                    return Optional.of(new VariableReferenceNode(name.getValue(), leftOperation));
                // Checks for 2d array
                if (!tokenManager.MatchAndRemove(Token.TokenType.OPBRACE).isEmpty()) {
                    Optional<Node> rightOperation = ParseOperation();
                    if (!tokenManager.MoreTokens())
                        throw new Exception("Must complete 2d array with a closing brace! ex: VARIABLE[OPERATION][OPERATION]");
                    // Checks for closed brace. Will throw exception if there is none after operation is parsed
                    if (tokenManager.MatchAndRemove(Token.TokenType.CLBRACE).isEmpty())
                        throw new Exception("Must complete 2d array with a closing brace! ex: VARIABLE[OPERATION][OPERATION]");
                    // Returned if name is 2d array
                    return Optional.of(new VariableReferenceNode(name.getValue(), leftOperation, rightOperation));
                }
                // Returned if name is 1d array
                return Optional.of(new VariableReferenceNode(name.getValue(), leftOperation));
            }
            // Returned if name has no special attributes
            return Optional.of(new VariableReferenceNode(name.getValue()));
        }
        // Impossible to reach this point. This is a placeholder return value
        return Optional.empty();
    }

    // ParseBottomLevel checks for STRINGLITERAL, NUMBER, BACKTIC, OPAREN, and other low level operations which
    // are directly taken from tokenManager.
    public Optional<Node> ParseBottomLevel() throws Exception {
        // Checks for more tokens, if none then return empty value
        if (!tokenManager.MoreTokens())
            return Optional.empty();
        //CHECK STRING LITERAL
        if (tokenManager.Peek(0).get().getType() == Token.TokenType.STRINGLITERAL)
            return Optional.of(new ConstantNode(tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL).get().getValue()));
            //CHECK NUMBER
        else if (tokenManager.Peek(0).get().getType() == Token.TokenType.NUMBER)
            return Optional.of(new ConstantNode(tokenManager.MatchAndRemove(Token.TokenType.NUMBER).get().getVal()));
            //CHECK PATTERNx
        else if (!tokenManager.MatchAndRemove(Token.TokenType.BACKTIC).isEmpty()){
            String s = "";
            Token.TokenType tokenType = tokenManager.Peek(0).get().getType();
            while(tokenType != Token.TokenType.BACKTIC) {
                s += tokenManager.MatchAndRemove(tokenType).get().toString();
                tokenType = tokenManager.Peek(0).get().getType();
            }
            if(tokenManager.MatchAndRemove(Token.TokenType.BACKTIC).isEmpty())
                throw new Exception("Incomplete pattern declaration!");
            return Optional.of(new PatternNode(s));}
        // Check for parenthesis expression
        if (!tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty()) {
            // Parse expression when inside parenthesis
            Optional<Node> Expression = ParseOperation();
            // Throws exception if it cannot find a closing parenthesis
            if (!tokenManager.MoreTokens() || tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
                throw new Exception("Error! Must have closing parenthesis for an operation to occur!");
            // Return o if closed parenthesis was found
            return Expression;
        }
        // Checks for UnaryPlus operation
        if (!tokenManager.MatchAndRemove(Token.TokenType.ADD).isEmpty()) {
            Node Expression = ParseOperation().get();
            return Optional.of(new AssignmentNode(Expression, new OperationNode(Expression, OperationNode.PossibleOperations.PREINC)));
        }
        // Checks for PREDEC operation
        else if (!tokenManager.MatchAndRemove(Token.TokenType.SUBT).isEmpty()) {
            Node Expression = ParseOperation().get();
            return Optional.of(new AssignmentNode(Expression, new OperationNode(Expression, OperationNode.PossibleOperations.PREDEC)));
        }
        // Checks for Logical not operation
        else if (!tokenManager.MatchAndRemove(Token.TokenType.EXLPT).isEmpty()) {
            Optional<Node> n = ParseOperation();
            return Optional.of(new OperationNode(n.get(), OperationNode.PossibleOperations.NOT));
        }
            // Checks for UnaryPlus operation
        else if (!tokenManager.MatchAndRemove(Token.TokenType.PLUS).isEmpty())
            return Optional.of(new OperationNode(ParseOperation().get(), OperationNode.PossibleOperations.UNARYPOS));
            // Checks for UnaryMinus operation
        else if (!tokenManager.MatchAndRemove(Token.TokenType.MINUS).isEmpty())
            return Optional.of(new OperationNode(ParseOperation().get(), OperationNode.PossibleOperations.UNARYNEG));
        // Parse for functioncall. If not empty ,then return FunctionCall
        Optional<StatementNode> funcCall = ParseFunctionCall();
        if (!funcCall.isEmpty()) {
            FunctionCallNode FunctionCall = (FunctionCallNode) funcCall.get();
            return Optional.of(FunctionCall);
        } else
            return ParseLValue();
    }

    // Parses for POSTDEC and POSTINC cases
    private Optional<Node> ParsePost() throws Exception {
        // n declared to parse at bottom level
        Optional<Node> Expression = ParseBottomLevel();
        // Checks for more tokens just in case there are none left
        if (!tokenManager.MoreTokens())
            return Expression;
        if (Expression.isEmpty())
            return Expression;
        // t created for exception handling purposes
        Token t = tokenManager.Peek(0).get();
        // Checks if n is a variable. If not, return n and don't check for POSTINC or POSTDEC
        if (!(Expression.get() instanceof VariableReferenceNode) && !(t.getType() == Token.TokenType.ADD || t.getType() == Token.TokenType.SUBT))
            return Expression;
        else if (!(Expression.get() instanceof VariableReferenceNode))
            throw new Exception("Incorrect syntax! Must be in the form of \"Variable++ or Variable--\"");
        // Check for ADD token, returns POSTINC if so.
        if (!tokenManager.MatchAndRemove(Token.TokenType.ADD).isEmpty()) {
            VariableReferenceNode Variable = (VariableReferenceNode) Expression.get();
            return Optional.of(new AssignmentNode(Variable, new OperationNode(Variable, OperationNode.PossibleOperations.POSTINC)));
        }// Check for ADD token, returns PRETINC if so.
        else if (!tokenManager.MatchAndRemove(Token.TokenType.SUBT).isEmpty()) {
            VariableReferenceNode Variable = (VariableReferenceNode) Expression.get();
            return Optional.of(new AssignmentNode(Variable, new OperationNode(Variable, OperationNode.PossibleOperations.POSTDEC)));
        }
        return Expression;
    }

    //ParseExponent pases exponential expressions
    public Optional<Node> ParseExponent() throws Exception {
        // Node n declared as first field
        Optional<Node> leftExpression = ParsePost();
        // if no more Tokens left, return n
        if (!tokenManager.MoreTokens())
            return leftExpression;
        // Check for '^' operand
        if (tokenManager.MatchAndRemove(Token.TokenType.CARROT).isEmpty())
            return leftExpression;
        if (leftExpression.isEmpty())
            throw new Exception("Must have a complete exponential expression! ex: EXPRESSION^EXPRESSION");
        // Check for right expression.
        Optional<Node> rightExpression = ParseExponent();
        // if n2 is empty, throw new exception.
        if (!tokenManager.MoreTokens() && rightExpression.isEmpty())
            throw new Exception("Must have a complete exponential expression! ex: EXPRESSION^EXPRESSION");
        return Optional.of(new OperationNode(leftExpression.get(), OperationNode.PossibleOperations.EXPONENT, rightExpression.get()));
    }


    // Parse for factors
    public Optional<Node> ParseFactor() throws Exception {
        // num Parsed, is returned if no more tokens are left or if its empty
        Optional<Node> num = ParseExponent();
        if (!num.isEmpty())
            return num;
        if (!tokenManager.MoreTokens())
            return num;
        // Check for open parenthesis.
        if (!tokenManager.MatchAndRemove(Token.TokenType.OPAREN).isEmpty()) {
            Optional<Node> exp = ParseExpression();
            // if expression is empty or there's no closing parenthesis, throw new exception
            if (exp.isEmpty())
                throw new Exception("Exception cannot be empty!");
            if (tokenManager.MatchAndRemove(Token.TokenType.CPAREN).isEmpty())
                throw new Exception("Error! Must have closing parenthesis for an operation to occur!");
            return exp;
        }
        return Optional.empty();
    }

    // ParseTerm meant to parse multiply/divide factors
    public Optional<Node> ParseTerm() throws Exception {
        // Factor is parsed
        Optional<Node> left = ParseFactor();
        do {
            // If no more tokens, return left
            if (!tokenManager.MoreTokens())
                return left;
            // If a star is parsed from tokenManager, set it as operand
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.STAR);
            // If op is empty, parse for a slash
            if (op.isEmpty())
                op = tokenManager.MatchAndRemove(Token.TokenType.SLASH);
            if(op.isEmpty())
                op = tokenManager.MatchAndRemove(Token.TokenType.PERCNT);
            // If still empty, return left (not a valid term)
            if (op.isEmpty())
                return left;
            // Parse for the right parse of the term
            Optional<Node> right;
            right = ParseFactor();
            // Throw exception if right is empty
            if (right.isEmpty())
                throw new Exception("Invalid Term! Must be in the form of \"FACTOR *|/ FACTOR\"");
            OperationNode.PossibleOperations operation = null;
            if (op.get().getType() == Token.TokenType.STAR)
                operation = OperationNode.PossibleOperations.MULTIPLY;
            else if (op.get().getType() == Token.TokenType.SLASH)
                operation = OperationNode.PossibleOperations.DIVIDE;
            else if(op.get().getType() == Token.TokenType.PERCNT)
                operation = OperationNode.PossibleOperations.MODULO;
            if (left.isEmpty())
                throw new Exception("Invalid Term! Must be in the form of \"FACTOR *|/ FACTOR\"");
            // Left is a new Term node if it parsed at this point
            left = Optional.of(new OperationNode(left.get(), operation, right.get()));
        }
        while (true);
    }

    // ParseExpression created to parse through minus or plus cases
    public Optional<Node> ParseExpression() throws Exception {
        // left node delcared as a parsed term
        Optional<Node> left = ParseTerm();
        do {
            // If no more tokens left, return left
            if (!tokenManager.MoreTokens())
                return left;
            // Create op, check for plus
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.PLUS);
            // If empty, check for minus
            if (op.isEmpty())
                op = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
            // Otherwise, return left
            if (op.isEmpty())
                return left;
            // Check for right term
            Optional<Node> right = ParseTerm();
            if (right.isEmpty())
                throw new Exception("Invalid Term! Must be in the form of \"TERM +|- TERM\"");
            OperationNode.PossibleOperations operation = null;
            if (op.get().getType() == Token.TokenType.PLUS)
                operation = OperationNode.PossibleOperations.ADD;
            else if (op.get().getType() == Token.TokenType.MINUS)
                operation = OperationNode.PossibleOperations.SUBTRACT;
            left = Optional.of(new OperationNode(left.get(), operation, right.get()));
        }
        while (true);
    }

    // ParseConcatination puts two strings together
    public Optional<Node> ParseConcatination() throws Exception {
        // n declared as new node and parsed
        Optional<Node> n = ParseExpression();
        // if no more tokens, return n
        if (!tokenManager.MoreTokens())
            return n;
        // Peek if next token is a STRINGLITERAL or an OperationNode
        if (tokenManager.Peek(0).get().getType() != Token.TokenType.STRINGLITERAL && tokenManager.Peek(0).get().getType() != Token.TokenType.WORD && tokenManager.Peek(0).get().getType() != Token.TokenType.NUMBER && tokenManager.Peek(0).get().getType() != Token.TokenType.DOLLAR)
            return n;
        // if so, parse the second value
        Optional<Node> n2 = ParseConcatination();
        return Optional.of(new OperationNode(n.get(), OperationNode.PossibleOperations.CONCATENATION, n2.get()));
    }

    // Method to add BooleanCompares
    private void addBooleanCompares() {
        BooleanCompares.put(Token.TokenType.LETHAN, OperationNode.PossibleOperations.LT);
        BooleanCompares.put(Token.TokenType.LEEQ, OperationNode.PossibleOperations.LE);
        BooleanCompares.put(Token.TokenType.NEQ, OperationNode.PossibleOperations.NE);
        BooleanCompares.put(Token.TokenType.EQUALS, OperationNode.PossibleOperations.EQ);
        BooleanCompares.put(Token.TokenType.GRTHAN, OperationNode.PossibleOperations.GT);
        BooleanCompares.put(Token.TokenType.GREQ, OperationNode.PossibleOperations.GE);
    }

    // ParseBooleanCompare created to Parse for comparison expressions
    public Optional<Node> ParseBooleanCompare() throws Exception {
        // n Parsed as a concatination. If there's no more tokens return n
        Optional<Node> n = ParseConcatination();
        if (!tokenManager.MoreTokens())
            return n;
        // Checks next character
        Optional<Token> t = tokenManager.Peek(0);
        // Checks if BooleanCompares contains the TokenType of t. If it doesn't, return n as it is
        if (!BooleanCompares.containsKey(t.get().getType()))
            return n;
        if (n.isEmpty())
            throw new Exception("Must have another value in order to make a comparison! Format must be (EXRESSION) (COMPARISON EXPRESSION) (EXPRESSION) ex: (x < 3)");
        // Retrieve ty, remove it from tokenManager, get operation.
        Token.TokenType ty = t.get().getType();
        tokenManager.MatchAndRemove(ty);
        OperationNode.PossibleOperations o = BooleanCompares.get(ty);
        // n2 is parsed
        Optional<Node> n2 = ParseConcatination();
        // if n2 is empty, throw an exception
        if (n2.isEmpty())
            throw new Exception("Must have another value in order to make a comparison! Format must be (EXRESSION) (COMPARISON EXPRESSION) (EXPRESSION) ex: (x < 3)");
        return Optional.of(new OperationNode(n.get(), o, n2.get()));
    }

    // ParseMatch created to check if two tokens properly matched
    public Optional<Node> ParseMatch() throws Exception {
        // n Parsed, if no there are no more tokens then return n
        Optional<Node> leftValue = ParseBooleanCompare();
        if (!tokenManager.MoreTokens())
            return leftValue;
        Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.TILDE);
        if (op.isEmpty())
            op = tokenManager.MatchAndRemove(Token.TokenType.REGEXP);
        if (op.isEmpty())
            return leftValue;
        if (leftValue.isEmpty())
            throw new Exception("Must have a valid satatement to match expressions! ex: EXPRESSION !~ | ~ EXPRESSION");
        // n2 is parsed as the second expression
        Optional<Node> n2 = ParseBooleanCompare();
        // If n2 is empty, throw new exception
        if (n2.isEmpty())
            throw new Exception("Must have a valid satatement to match expressions! ex: EXPRESSION !~ | ~ EXPRESSION");
        // Check for tilde tomeType. Return new OperationNode if found
        if (op.get().getType() == Token.TokenType.TILDE)
            return Optional.of(new OperationNode(leftValue.get(), OperationNode.PossibleOperations.MATCH, n2.get()));
        // If not, return REGEXP
        return Optional.of(new OperationNode(leftValue.get(), OperationNode.PossibleOperations.NOTMATCH, n2.get()));
    }

    // ParseMembership() created to check for expressions inside arrays.
    public Optional<Node> ParseMembership() throws Exception {
        // Expression is parsed. is returned if no more tokens
        Optional<Node> Value = ParseMatch();
        if (!tokenManager.MoreTokens())
            return Value;
        // if IN token is not returned in matchAndRemove(), return n
        if (tokenManager.MatchAndRemove(Token.TokenType.IN).isEmpty())
            return Value;
        if (Value.isEmpty())
            throw new Exception("Incomplete IN condition! Must be in the form of EXPRESSION IN ARRAY");
        // Parse n2
        Optional<Node> Array = ParseMatch();
        if(Array.isEmpty())
            throw new Exception("Incomplete IN condition! Must be in the form of EXPRESSION IN ARRAY");
        // Check if n2 is a VariableReferenceNode. If it is, create f and set it to n
        if (!(Array.get() instanceof VariableReferenceNode))
            throw new Exception("Not an array! Must be a valid array! Must be in the form of EXPRESSION IN ARRAY");
        // Return if passes all tests
        return Optional.of(new OperationNode(Value.get(), OperationNode.PossibleOperations.IN, Array.get()));
    }

    // ParseAnd checks for AND cases
    public Optional<Node> ParseAnd() throws Exception {
        // n is parsed. if there are no more tokens then return n.
        Optional<Node> n = ParseMembership();
        if (!tokenManager.MoreTokens())
            return n;
        // If next token isn't an AND statement, return n.
        if (tokenManager.MatchAndRemove(Token.TokenType.AND).isEmpty())
            return n;
        if (n.isEmpty())
            throw new Exception("Must have a valid AND statement! ex: EXP && EXP");
        // n2 is parsed
        Optional<Node> n2 = ParseMembership();
        if (n2.isEmpty())
            throw new Exception("Must have a valid AND statement! ex: EXP && EXP");
        // Return new OperationNode if otherwise
        return Optional.of(new OperationNode(n.get(), OperationNode.PossibleOperations.AND, n2.get()));
    }

    // Parse an OR statement
    public Optional<Node> ParseOr() throws Exception {
        // n parsed. returned if no more tokens
        Optional<Node> n = ParseAnd();
        if (!tokenManager.MoreTokens())
            return n;
        // Check for OR token. if none then return n.
        if (tokenManager.MatchAndRemove(Token.TokenType.OR).isEmpty())
            return n;
        if (n.isEmpty())
            throw new Exception("Invalid OR statement! ex: EXPRESSION || EXPRESSION");
        // Parse for n2 if empty then throw exception
        Optional<Node> n2 = ParseAnd();
        if (n2.isEmpty())
            throw new Exception("Invalid OR statement! ex: EXPRESSION || EXPRESSION");
        // If passes all tests, return new OperationNode
        return Optional.of(new OperationNode(n.get(), OperationNode.PossibleOperations.OR, n2.get()));
    }

    // Parses TernaryNode. This checks for ternary expressions
    public Optional<Node> ParseTernary() throws Exception {
        // n is Parsed, if tokenManager is empty then return n
        Optional<Node> Condition = ParseOr();
        if (!tokenManager.MoreTokens())
            return Condition;
        // if next token is a '?', return n
        if (tokenManager.MatchAndRemove(Token.TokenType.QMARK).isEmpty())
            return Condition;
        if (Condition.isEmpty())
            throw new Exception("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP");
        // n2 is parsed as the first expression
        Optional<Node> Consequent = ParseOperation();
        // if tokenManager is empty, throw an exception
        if (!tokenManager.MoreTokens())
            throw new Exception("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP");
        if (Consequent.isEmpty())
            throw new Exception("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP");
        // if n2 is empty, throw an exception
        if (tokenManager.MatchAndRemove(Token.TokenType.COLON).isEmpty())
            throw new Exception("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP");
        Optional<Node> Alternate = ParseOperation();
        // if n3 is empty, throw an exception
        if (Alternate.isEmpty())
            throw new Exception("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP");
        // If all tests are pasts, return new TernaryNode
        return Optional.of(new TernaryNode(Condition.get(), Consequent.get(), Alternate.get()));
    }

    // addAssignment keys and values added to AssignmentCompares HashMap
    public void addAssignments() {
        AssignmentCompares.put(Token.TokenType.CAREQ, OperationNode.PossibleOperations.EXPONENT);
        AssignmentCompares.put(Token.TokenType.PEREQ, OperationNode.PossibleOperations.MODULO);
        AssignmentCompares.put(Token.TokenType.TIEQ, OperationNode.PossibleOperations.MULTIPLY);
        AssignmentCompares.put(Token.TokenType.DIVEQ, OperationNode.PossibleOperations.DIVIDE);
        AssignmentCompares.put(Token.TokenType.PLEQ, OperationNode.PossibleOperations.ADD);
        AssignmentCompares.put(Token.TokenType.MINEQ, OperationNode.PossibleOperations.SUBTRACT);
        AssignmentCompares.put(Token.TokenType.ASSIGN, OperationNode.PossibleOperations.ASSIGN);
    }

    // ParseAssignment used to parse through tokens
    public Optional<Node> ParseAssignment() throws Exception {
        // Declare n, set it as Parse ternary
        Optional<Node> n = ParseTernary();
        // If there's no more tokens, return n
        if (!tokenManager.MoreTokens())
            return n;
        Optional<Token> t = tokenManager.Peek(0);
        Optional<OperationNode.PossibleOperations> op = Optional.empty();
        if (AssignmentCompares.containsKey(t.get().getType())) {
            op = Optional.of(AssignmentCompares.get(t.get().getType()));
            tokenManager.MatchAndRemove(t.get().getType());
        } else
            return n;
        if (n.isEmpty())
            throw new Exception("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION");
        // If n is not an instanceof  VariableReferenceNode, return n
        if (n.get() instanceof VariableReferenceNode) {
            // vf is set to declare new AssignmentNode
            VariableReferenceNode vf = (VariableReferenceNode) n.get();
            // Declare n2. Make it a parseTernary
            Optional<Node> n2 = ParseTernary();
            if (n2.isEmpty())
                throw new Exception("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION");
            // Return new AssignmentNode with params being (n, new OperationNode(n, operation, n2)
            return Optional.of(new AssignmentNode(vf, new OperationNode(n.get(), op.get(), n2.get())));
        }
        else if(n.get() instanceof OperationNode){
            OperationNode opNode = (OperationNode) n.get();
            if(opNode.getOperation() != OperationNode.PossibleOperations.DOLLAR)
                throw new Exception();
            // Declare n2. Make it a parseTernary
            Optional<Node> n2 = ParseTernary();
         return Optional.of(new AssignmentNode(opNode, new OperationNode(n.get(), op.get(), n2.get())));
        }
        else
            throw new Exception("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION");
    }
}

//             throw new Exception("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION");