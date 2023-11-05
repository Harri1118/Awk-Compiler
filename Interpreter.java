package icsi311;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

    // printContent records what is being printed every time the print function is called.
    public String printContent = "";

    // GlobalVariables store all the variables stored in the interpreter (which are not local)
    public HashMap<String, InterpreterDataType> GlobalVariables = new HashMap<String, InterpreterDataType>();

    // HashMap which stores all functions in the programNode + build in functions.
    public HashMap<String, FunctionDefinitionNode> Functions = new HashMap<String, FunctionDefinitionNode>();

    // nextDifference int used for SplitAndAssign method, keeps track of field creation ($1, $2, etc)
    private int nextDifference = 0;

    // LineManager keeps track of the lines in an input file.
    public LineManager Manager;

    // Interpreter Constructor takes in a ProgramNode and FilePath
    public Interpreter(ProgramNode program, Optional<Path> filePath) throws Exception, IOException {
        // if filepath is not empty, LineManager is populated with inputted lines
        if(!filePath.isEmpty())
            Manager = new LineManager(Files.readAllLines(filePath.get()));
        // If not, then initiate LineManager with empty LinkedList of strings
        else
            Manager = new LineManager(new LinkedList<String>());
        // Initial built-in variable declarations
        GlobalVariables.put("FILENAME", new InterpreterDataType(filePath.toString()));
        GlobalVariables.put("FS", new InterpreterDataType(" "));
        GlobalVariables.put("OFMT", new InterpreterDataType("%.6g"));
        GlobalVariables.put("ORS",new InterpreterDataType("\n"));
        GlobalVariables.put("NF", new InterpreterDataType("0"));
        GlobalVariables.put("NR", new InterpreterDataType("0"));
        GlobalVariables.put("FNR", new InterpreterDataType("1"));

        // All the functions which are build in is put into the Functions hashmap in this method
        putBuiltInFunctions();
        // If the amount of functions in programNode's size is bigger than 0, put the custom functions in.
        if(program.getFuncs().size() > 0)
            putCustomFunctions(program);
    }

    // PutBuiltInFunctions is self explanatory, put all the built-in functions inside the Functions hashMap!
    private void putBuiltInFunctions() throws Exception{
        //Goal: print out a hashmap
        Function<HashMap<String, InterpreterDataType>, String> Function = hashMap -> {
        // Get "content" from hashMap
        InterpreterArrayDataType inp = (InterpreterArrayDataType) hashMap.get("content");
        // printContent takes what will be printed and store it (this is for unit testing purposes)
        printContent = inp.printValue();
        // Prints the value
        System.out.println(inp.printValue());
        // Return nothing??
        return "";
        };
        // Initialize and add print
        BuiltInFunctionDefinitionNode node = new BuiltInFunctionDefinitionNode("print", new String[]{"content"}, true, Function);
        Functions.put("print", node);

        // Function for printf
        Function = hashMap -> {
            // Get "content" from hashMap
            InterpreterArrayDataType inp = (InterpreterArrayDataType) hashMap.get("content");
            // if format key is contained within, utilize it
            if(hashMap.containsKey("format")) {
                printContent = "Format: " + hashMap.get("format").toString() + ", Result: " + inp.printValue();
                System.out.println(printContent);
                return "";
            }
            // printcontent is simplified for debugging purposes
            printContent = "Format: " + GlobalVariables.get("OFMT").toString() + ", Result: " + inp.printValue();
            // Print result
            System.out.println(printContent);
            return "";
        };
        // printf initialized and added
        node = new BuiltInFunctionDefinitionNode("printf", new String[]{"format", "content"}, true, Function);
        Functions.put("printf", node);

        // getline method
        Function = hashMap -> {
            // if hashMap input is empty, splitandAssign and return 1.
            if(hashMap.isEmpty()) {
                if (Manager.SplitAndAssign() == true)
                    return "1";
            }
            // if input contains variable, splitAndAssign the second half of the method ONLY
            else if(hashMap.containsKey("variable")){
                Manager.SplitAndAssign(hashMap.get("variable").toString());
                // Check if nextDifference is more than 1, return 0 otherwise
                if(nextDifference > 1){
                    nextDifference = 0;
                    return "0";
                }
                return "1";
            }
            return "0";
        };
        node = new BuiltInFunctionDefinitionNode("getline", new String[]{"variable"}, false, Function);
        Functions.put("getline", node);

        Function = hashMap -> {
            // Next performs a simple SplitAndAssign()
            Manager.SplitAndAssign();
            return "";
        };
        node = new BuiltInFunctionDefinitionNode("next", new String[]{}, false, Function);
        Functions.put("next", node);

        // Function for regexp
        Function = hashMap -> {
            // Check for illegal arguments
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Invalid method call for gsub! must be in the form: gsub(regex, replacement, Optional[array])!");
            // Check if contains target
            if(hashMap.containsKey("target")){
                // whole is what the target will look like as a string
                String whole = hashMap.get("target").toString();
                // replace all the targets with replacement in modded
                String modded = whole.replace(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                // if no changes were made, return 0, elsewise return 1 and modify target
                if(whole.equals(modded))
                    return "0";
                hashMap.put("target", new InterpreterDataType(modded));
                return "1";
            }
            // If no target, do something simipar but instead replace the fields/
            String whole = GlobalVariables.get("$0").toString();
            String modded = whole.replace(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
            if(whole.equals(modded))
                return "0";
            GlobalVariables.put("$0", new InterpreterDataType(modded));
            for(int i  = 0; i < Integer.valueOf(GlobalVariables.get("NF").toString()); i++){
                int place = i+1;
                String part = GlobalVariables.get("$" + place).toString();
                String modified = part.replace(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                GlobalVariables.put("$"+place, new InterpreterDataType(modified));
            }
            return "1";
        };
        node = new BuiltInFunctionDefinitionNode("gsub", new String[]{"regexp", "replacement", "target"}, false, Function);
        Functions.put("gsub", node);

        // function for index
        Function = hashMap -> {
            // parameter checking
            if(hashMap.isEmpty() || hashMap.size() > 2)
                throw new IllegalArgumentException("Illegal function call of index! It must only be called like index(string, substring)!");
            String s = hashMap.get("string").toString();
            String sub = hashMap.get("substring").toString();
            // if s doesn't contain sub, instantly return 0
            if(!s.contains(sub))
                return "0";
            // iterate through string until substring is parsed
            int pos = 0;
            for(int i = 0; i < s.length(); i++){
                // If substring doesn't fit in the string, return 0
                if(i+sub.length() > s.length())
                    return "0";
                // check if the next few chars of a matching character allign with a substring. Break if true
                if(s.charAt(i) == sub.charAt(0)){
                    if(s.substring(i,i+sub.length()).equals(sub)){
                        pos = i;
                        break;
                    }
                }
            }
            // return pos in terms of the final substring index
            pos = pos + 1;
            return String.valueOf(pos);
        };
        node = new BuiltInFunctionDefinitionNode("index", new String[]{"string", "substring"}, false, Function);
        Functions.put("index", node);

        // Method for length
        Function = hashMap -> {
            // parameter checking
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! Length must be explicitly called with a param! Ex: length(string)");
            // Simply return the length of the string.
            return String.valueOf(hashMap.get("string").toString().length());
        };
        node = new BuiltInFunctionDefinitionNode("length", new String[]{"string"}, false, Function);
        Functions.put("length", node);

        // function for sprintf
        Function = hashMap -> {
            // if more than 2 arguments, throw an exception
            if(hashMap.size() > 2 || !hashMap.containsKey("format") || !hashMap.containsKey("content"))
                throw new IllegalArgumentException("Error! Method to format string is incorrect! Must be in form sprintf(format, string)!");
            // Get content
            InterpreterArrayDataType content = (InterpreterArrayDataType) hashMap.get("content");
            // return content formatted
            return "Format: " + hashMap.get("format").toString() + ", content: " + content.printValue();
        };
        node = new BuiltInFunctionDefinitionNode("index", new String[]{"format", "content"}, false, Function);
        Functions.put("sprintf", node);

        // method for sub
        Function = hashMap -> {
            // Field checking
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Invalid method call for sub! must be in the form: sub(regex, replacement, Optional[array])!");
            // if target is contained
            if(hashMap.containsKey("target")){
                // replace the first string
                String whole = hashMap.get("target").toString();
                String modded = whole.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                // if no diff then return 0
                if(whole.equals(modded))
                    return "0";
                // modify target and return
                hashMap.put("target", new InterpreterDataType(modded));
                return "1";
            }
            // get whole
            String whole = GlobalVariables.get("$0").toString();
            // Get modded from replacefirst of whole
            String modded = whole.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
            // return 0 if no diff
            if(whole.equals(modded))
                return "0";
            // put modded in $0 if there was a change
            GlobalVariables.put("$0", new InterpreterDataType(modded));
            // After change, change the other vars to fit the sub
            int i = 1;
            // Change first target of all other fields
            while(GlobalVariables.containsKey("$"+i)){
                // Get part
                String part = GlobalVariables.get("$" + i).toString();
                // Create modified
                String modified = part.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                // use put if difference
                if(!part.equals(modified)){
                    GlobalVariables.put("$"+i, new InterpreterDataType(modified));
                    return "1";
                }
                i++;
            }
            return "0";
        };
        node = new BuiltInFunctionDefinitionNode("sub", new String[]{"regexp", "replacement", "target"}, true, Function);
        Functions.put("sub", node);

        // Function for match
        Function = hashMap -> {
            // Argument checking
            if(hashMap.isEmpty() || hashMap.size() > 2)
                throw new IllegalArgumentException("Illegal match declaration! The match method must be in the form: match(string, pattern)!");
            // Check for string and pattern
            String content = hashMap.get("string").toString();
            String pattern = hashMap.get("pattern").toString();
            // if no pattern, return 0
            if(!content.contains(pattern))
                return "0";
            // Check for pattern
            int fin = 0;
            for(int i = 0; i < content.length(); i++){
                if(i + pattern.length() > content.length())
                    return "0";
                // check if current char is equal to first char of pattern
                if(content.charAt(i) == pattern.charAt(0)) {
                    // check if substring after is equal to pattern
                    if(content.substring(i, i+pattern.length()).equals(pattern)){
                        // if so, set fin and break out of for loop
                        fin = i;
                        break;
                    }
                }
            }
            fin++;
            return String.valueOf(fin);
        };
        node = new BuiltInFunctionDefinitionNode("match", new String[]{"string", "pattern"}, true, Function);
        Functions.put("match", node);

        //Function for split
        Function = hashMap -> {
            // Check arguments
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Incorrect method call for split! It must be in the form of: split(target, array, separator)!");
            String separator = "";
            // if hashmap contains separator, use it
            if(hashMap.containsKey("separator"))
                separator = hashMap.get("separator").toString();
            // else use fs
            else
                separator = GlobalVariables.get("FS").toString();
            // use string.split and set the resulting array into the hashmap
            String target = hashMap.get("target").toString();
            String[] arr = target.split(separator);
            InterpreterArrayDataType finArray = new InterpreterArrayDataType();
            for(var c : arr)
                finArray.put(c);
            hashMap.put("array", finArray);
            return String.valueOf(arr.length);
        };
        node = new BuiltInFunctionDefinitionNode("split", new String[]{"target", "array", "separator"}, true, Function);
        Functions.put("split", node);

        // function for substr method
        Function = hashMap -> {
            // param checking
            if(hashMap.size() > 3)
                throw new IllegalArgumentException("Warning! The substr method must have 3 and strictly 3 parameters! Ex: substr(str, 1,2)");
            // Try to do normal substring process. Will throw an exception if any issue occurs.
            try {
                int start = Integer.valueOf(hashMap.get("start").toString());
                int end = Integer.valueOf(hashMap.get("end").toString());
                String str = hashMap.get("string").toString().substring(start, end);
                return str;
            }
            catch(Exception e){
                throw new IllegalArgumentException("Warning! Substring is configured incorrectly!");
            }
        };
        node = new BuiltInFunctionDefinitionNode("substr", new String[]{"string", "start", "end"}, true, Function);
        Functions.put("substr", node);

        // Function for toLower
        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! tolower must have 1 parameter!");
            return hashMap.get("string").toString().toLowerCase();
        };
        node = new BuiltInFunctionDefinitionNode("tolower", new String[]{"content"}, true, Function);
        Functions.put("tolower", node);

        // Function for toUpper
        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! toupper must have 1 parameter!");
            return hashMap.get("string").toString().toUpperCase();
        };
        node = new BuiltInFunctionDefinitionNode("toupper", new String[]{"content"}, true, Function);
        Functions.put("toupper", node);
    }

    // Method to put in custon functions. A simple for loop.
    private void putCustomFunctions(ProgramNode p){
        for(var f: p.getFuncs())
            Functions.put(f.getName(), f);
    }

    // runFuncCall to interpret functions
    public  String RunFunctionCall(){
        return "Function call ran.";
    }

    // GetIDT to interpret calculations
    public InterpreterDataType GetIDT(Node node, HashMap<String, InterpreterDataType> Variables) throws Exception {
        InterpreterDataType fin = new InterpreterDataType();
        fin = AssignmentNode(node, Variables);
        return fin;
    }

    // Method to interpret AssignmentNodes
    private InterpreterDataType AssignmentNode(Node ASNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if ASNode is an assignmentNode, if not then return result of ConstantNode.
        if(ASNode instanceof AssignmentNode) {
            // AssignmentNode is cased to be used.
            AssignmentNode aNode = (AssignmentNode) ASNode;
            // derive target from AssignmentNode
            VariableReferenceNode Target = (VariableReferenceNode) aNode.getTarget();
            // Derive var name from target
            String name = Target.toString();
            // Derive value value to be assigned to var from expression
            InterpreterDataType val = GetIDT(aNode.Expression, Variables);
            // if Variables is null, make it GlobalVariables
            if(Objects.isNull(Variables))
                Variables = GlobalVariables;
            // Create an arr for array cases
            InterpreterArrayDataType arr;
            // Put new variable into Variables
            Variables.put(name, val);
            // If Target is part of an array and the array is already initialized, add it to the existing array and put it as a Variable into Variables
            if(Target.isArray() && Variables.containsKey(Target.getName())){
                arr = (InterpreterArrayDataType) Variables.get(Target.getName());
                arr.putKey(Target.getIndex());
            }
            // If first time initializing the array, then create it and set it in Variables, add its first variable, and return the value
            else if(Target.isArray() && !(Variables.containsKey(Target.getName()))) {
                arr = new InterpreterArrayDataType();
                arr.putKey(Target.getIndex());
                Variables.put(Target.getName(), arr);
            }
            return val;
        }
        return ConstantNode(ASNode, Variables);
    }


    private InterpreterDataType ConstantNode(Node CNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if CNode is a ConstantNode, if not then return result of FunctionCallNode.
        if(CNode instanceof ConstantNode){
            // Cast input
            ConstantNode constant = (ConstantNode) CNode;
            // Simply return what is stored in ConstantNode
            return new InterpreterDataType(constant.toString());
        }
        return FunctionCallNode(CNode, Variables);
    }

    // FunctionCallNode to interpret Function Calls
    private InterpreterDataType FunctionCallNode(Node FCallNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if FCallNode is an FunctionCallNode, if not then return result of PatternNode.
        if(FCallNode instanceof FunctionCallNode){
            // Check if functions contains the name for the function inputted. If not then throw an error
            FunctionCallNode func = (FunctionCallNode) FCallNode;
            if(!Functions.containsKey(func.getName()))
                throw new Exception("Error! Function doesn't exist!");
            // call RunFunctionCall() if its a valid function
            return new InterpreterDataType(RunFunctionCall());
        }
        return PatternNode(FCallNode, Variables);
    }

    // PatternNode to interpret PatternNodes
    private InterpreterDataType PatternNode(Node PNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if PNode is an PatternNode, if not then return result of TernaryNode.
        if(PNode instanceof PatternNode){
            // Since patterns cannot be declared outside of specific cases, throw an error if detected
            throw new Exception("Error! Cannot call a patternNode outside of a statement!");
        }
        return TernaryNode(PNode, Variables);
    }

    private InterpreterDataType TernaryNode(Node TNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if TNode is a TernaryNode, if not then return result of VariableReferenceNode.
        if(TNode instanceof TernaryNode){
            // TernaryNode casted
            TernaryNode tern = (TernaryNode) TNode;
            // Get Condition
            InterpreterDataType condition = GetIDT(tern.getCondition(), null);
            // Check if converted is true or false (1 or 0)
            if(isConvertable(condition))
                condition = new InterpreterDataType("1");
            else
                condition = new InterpreterDataType("0");
            // If it equals 1, return consequent. If s=0, return alternate
            if(condition.toString().equals("1"))
                return GetIDT(tern.getConsequent(), Variables);
            else if(condition.toString().equals("0"))
                return GetIDT(tern.getAlternate(), Variables);
            }
        return VariableReferenceNode(TNode, Variables);
    }
    private InterpreterDataType VariableReferenceNode(Node VNode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if VNode is a VariableReferenceNode, if not then return result of OperationNode.
        if(VNode instanceof VariableReferenceNode){
            // If Variables are null, set it to GlobalVariables
            if(Objects.isNull(Variables))
                Variables = GlobalVariables;
            // cast VariableReferenceNode
            VariableReferenceNode var = (VariableReferenceNode) VNode;
            // Check if Variable is contained in Variables. If not then return 0
            if(!Variables.containsKey(var.toString()) || Variables.get(var.toString()).isEmpty())
                return new InterpreterDataType("0");
            // If variable is not an array return its val
            else if(!var.isArray())
                return Variables.get(var.toString());
            // If Variables doesn't contain the variable, put it as a new Variable
            if(!Variables.containsKey(var.toString()))
                Variables.put(var.toString(),new InterpreterArrayDataType());
            return Variables.get(var.toString());
        }
        return OperationNode(VNode, Variables);
    }
    private InterpreterDataType OperationNode(Node Onode, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Because all else fails, it is assumed Onode is an OperatioNode
        OperationNode operationNode = (OperationNode) Onode;
        // Check if operation is ASSIGN, then return the value of which it is assigned to.
        if (operationNode.getOperation() == OperationNode.PossibleOperations.ASSIGN){
            InterpreterDataType i = new InterpreterDataType(GetIDT(operationNode.getRightValue().get(), null).toString());
            return i;}
        // Get left value from the operationNode's left node.
        InterpreterDataType Left = GetIDT(operationNode.getLeftValue(), null);
        // Check if operation is PREINC or PREDEC, then return PreIncrement with appropriate Variables
        if (operationNode.isPreIncremental())
            return PreIncrement(operationNode, Left, Variables);
        // Check if operation is POSTINC or POSTDEC, then return PostIncrement with appropriate Variables
        if (operationNode.isPostIncremental())
            return PostIncrement(operationNode, Left, Variables);
         // Check if operation is a booleanOperator, then return BooleanOperator with appropriate Variables
        if (operationNode.isBoolOperator() && operationNode.getRightValue().isEmpty())
            return BooleanOperator(Left, null, operationNode.getOperation());
        // Check if operation is DOLLAR, then return GetField with appropriate Variables
        if(operationNode.getOperation() == OperationNode.PossibleOperations.DOLLAR)
            return getField(Left,null,  Variables);
        // Check if operation is MATCH or NOTMATCH, then return Match with appropriate Variables
        if (operationNode.getOperation() == OperationNode.PossibleOperations.MATCH || operationNode.getOperation() == OperationNode.PossibleOperations.NOTMATCH)
            return Match(Left, operationNode.getRightValue().get(), operationNode.getOperation());
        // Check if operation is UNARYPOS or UNARYNEG, then return Unary with appropriate Variables
        if (operationNode.getOperation() == OperationNode.PossibleOperations.UNARYPOS || operationNode.getOperation() == OperationNode.PossibleOperations.UNARYNEG)
            return Unary(Left, operationNode.getOperation());
        // If all these ifs fail, interpret the right value.
        InterpreterDataType Right = GetIDT(operationNode.getRightValue().get(), null);
        // Check if operation is mathematic in nature, then return BasicMath with appropriate Variables
        if (operationNode.isMathematic())
            return BasicMath(Left, Right, operationNode.getOperation());
        // Check if operation is a comparitor, then return Compares with appropriate Variables
        else if (operationNode.isCompOperater())
            return Compares(Left, Right, operationNode.getOperation());
        // Check if operation is booleanOperator, then return BooleanOperator with appropriate Variables
        else if (operationNode.isBoolOperator())
            return BooleanOperator(Left, Right, operationNode.getOperation());
        // Check if operation is CONCATINATION, then return InterpreterDataType with Left and Right Variables
        else if (operationNode.getOperation() == OperationNode.PossibleOperations.CONCATENATION)
            return new InterpreterDataType(Left.toString() + Right.toString());
        // Check if operation is IN AND operationNode.getLeftValue() is VariableReferenceNode, then cast VariableReferenceNode and return In with appropriate Variables
        else if (operationNode.getOperation() == OperationNode.PossibleOperations.IN && (operationNode.getLeftValue() instanceof VariableReferenceNode)){ // Done
            VariableReferenceNode var = (VariableReferenceNode) operationNode.getLeftValue();
            return In(var.getIndex(), operationNode.getRightValue().get(), Variables);
    }
        // If not an in, then instead just call IN with appropriate variables
        else if(operationNode.getOperation() == OperationNode.PossibleOperations.IN)
            return In(Left.toString(), operationNode.getRightValue().get(), Variables);
        else
            return null;
    }

    //Basic math performs mathematical operations
    private InterpreterDataType BasicMath(InterpreterDataType Left, InterpreterDataType Right, OperationNode.PossibleOperations op) throws Exception {
            // left and right is converted and checked into numbers (if strings then they become 0).
            Float left = conv(Left.toString());
            Float right = conv(Right.toString());
            // Create result to store the nunber produced by both operations
            Float result = (float) 0;
            // Switch op depending on its type
            switch(op){
                case ADD:
                    result =  left + right;
                    break;
                case SUBTRACT:
                    result = left - right;
                    break;
                case MULTIPLY:
                    result =  left * right;
                    break;
                case DIVIDE:
                    result =  left/right;
                    break;
                case EXPONENT:
                    result = (float)Math.pow((double)left, (double)right);
                    break;
                case MODULO:
                    result = left % right;
            }
            // based off its case, perform mathematical operation and return result as a simple IDT
            return new InterpreterDataType(String.valueOf(result));
    }

    public float conv(String s){
        // attempts to convert the number to a float
        try{
            float num = Float.parseFloat(s);
            return num;
        }
        // if an exception occurs, return 0
        catch(Exception e){
            return 0;
        }
    }

    public InterpreterDataType Unary(InterpreterDataType Left, OperationNode.PossibleOperations op){
        // Tnum converted to float
        float num = conv(Left.toString());
        // if op is unaryneg, negate it and return it. Elsewise return the result.
        if(op == OperationNode.PossibleOperations.UNARYNEG){
            num *= -1;
            return new InterpreterDataType(String.valueOf(num));}
        return new InterpreterDataType(String.valueOf(num));
    }
    public InterpreterDataType Compares(InterpreterDataType Left, InterpreterDataType Right, OperationNode.PossibleOperations op) throws Exception {
        // Check if Left and Right are both isFloat
        if(Left.isFloat() && Right.isFloat()){
            // Convert to loat and perform appropriate operations
            float lValue = Float.parseFloat(Left.toString());
            float rValue = Float.parseFloat(Right.toString());
            boolean finBool = false;
            switch(op){
                case LT:
                    finBool = (lValue < rValue);
                    break;
                case LE:
                    finBool = (lValue <= rValue);
                    break;
                case GT:
                    finBool = (lValue > rValue);
                    break;
                case GE:
                    finBool = (lValue >= rValue);
                    break;
                case EQ:
                    finBool = (lValue == rValue);
                    break;
                case NE:
                    finBool = (lValue != rValue);
                    break;
            }
            // return 1 if true and 0 if false
            if(finBool == true)
                return new InterpreterDataType("1");
            return new InterpreterDataType("0");
        }
        // Check if Left and Right are both isFloat (false)
        else{
            // use string.compareto if both left and right aren't nums
            boolean finBool = false;
            int comparison = Left.toString().compareTo(Right.toString());
            switch(op){
                case LT:
                    finBool = (comparison < 0);
                    break;
                case LE:
                    finBool = (comparison < 0 || comparison == 0);
                    break;
                case GT:
                    finBool = (comparison > 0);
                    break;
                case GE:
                    finBool = (comparison > 0 || comparison == 0);
                    break;
                case EQ:
                    finBool = (comparison == 0);
                    break;
                case NE:
                    finBool = (comparison != 0);
                    break;
            }
            if(finBool == true)
                return new InterpreterDataType("1");
            return new InterpreterDataType("0");
        }
    }

    private InterpreterDataType BooleanOperator(InterpreterDataType Left,  InterpreterDataType Right, OperationNode.PossibleOperations op){
        // BooleanOperator works in a similar premise to convertable. Checks if each side is 1 or 0. Though in cases of NOT, perform a different action
        boolean leftVal = isConvertable(Left);
        boolean rightVal = false;
        // rightval is changed if Right is not null
        if(!Objects.isNull(Right))
            rightVal = isConvertable(Right);
        boolean fin = false;
        switch(op){
            case AND:
                fin = (leftVal && rightVal);
                break;
            case OR:
                fin = (leftVal || rightVal);
                break;
            case NOT:
                fin = !(leftVal);
        }
        // If fin is true return 1, 0 otheriwse
        if(fin == true)
            return new InterpreterDataType("1");
        return new InterpreterDataType("0");
    }

    private boolean isConvertable(InterpreterDataType IDT){
        // Checks if num is 0. If so then reutrn false.
        try{
            float num = Float.parseFloat(IDT.toString());
            if(num == 0)
                return false;
            return true;
        }
        // If IDT.toString is false or empty, return false
        catch(Exception e){
            if(IDT.toString() == "false" || IDT.isEmpty())
                return false;
        return true;}
    }

    public InterpreterDataType PreIncrement(OperationNode oNode, InterpreterDataType Value, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Check if LeftValue is VariableRefereceNode. Throw Exception if not the case
        if (!(oNode.getLeftValue() instanceof VariableReferenceNode))
            throw new Exception("Error! Cannot pre-increment to any other value except a VariableReferenceNode! Ex: ++var, --var");
        // Cast VariableReferenceNode
            VariableReferenceNode var = (VariableReferenceNode) oNode.getLeftValue();
        // f is converted to num
            float f = conv(Value.toString());
            // If op is PREINC, increase by 1
            if (oNode.getOperation() == OperationNode.PossibleOperations.PREINC)
                f++;
            else
                f--;
            // Check if Variables is null
            if(Objects.isNull(Variables))
                Variables = GlobalVariables;
            // Perform appriopriate actions
            Variables.put(var.getName(), new InterpreterDataType(String.valueOf(f)));
            return new InterpreterDataType(Variables.get(var.getName()).toString());

    }
    public InterpreterDataType PostIncrement(OperationNode oNode, InterpreterDataType Value, HashMap<String, InterpreterDataType> Variables) throws Exception {
        // Very similar method to preinc
        VariableReferenceNode var = (VariableReferenceNode) oNode.getLeftValue();
        float f;
        try{
            f  = Float.parseFloat(Value.toString());
        }
        catch(Exception e){
            f = conv(Value.toString());
        }
            // pre stores f before it was converted
            float pre = f;
            if (oNode.getOperation() == OperationNode.PossibleOperations.POSTINC)
                f++;
            else
                f--;
            if(Objects.isNull(Variables))
                Variables = GlobalVariables;
            // Variables puts modified value
            Variables.put(var.getName(), new InterpreterDataType(String.valueOf(f)));
            // returns pre
            return new InterpreterDataType(String.valueOf(pre));

    }

    private InterpreterDataType Match(InterpreterDataType Left, Node Right, OperationNode.PossibleOperations op) throws Exception{
        // expr is derived from Left value
        String expr = Left.toString();
        // If right is not a patternNode, throw an exception
        if(!(Right instanceof PatternNode))
            throw new Exception("Error! Cannot match with a non-pattern type! Ex: expr ~|!~ PATTERN");
        // Regex gets value of right
        String regex = Right.toString();
        // Check for patterns with Pattern and Matcher Objects from java
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expr);
        // If op is NOTMATCH, return 1 if it cannot find it, return 0 otherwise.
        if(op == OperationNode.PossibleOperations.NOTMATCH){
            if(!matcher.find())
                return new InterpreterDataType("1");
            return new InterpreterDataType("0");
        }
        // Otherwise, return 1 if found, return 0 otherwise.
        if (matcher.find())
            return new InterpreterDataType("1");
        return new InterpreterDataType("0");

    }

        private InterpreterDataType In(String Name, Node Right, HashMap<String, InterpreterDataType> Variables){
        // Cast VariableReferenceNode from right
        VariableReferenceNode arr = (VariableReferenceNode) Right;
        // If Variables is null, make it Global
        if(Objects.isNull(Variables))
            Variables = GlobalVariables;
        // If Varables doesn't contain the name of the array, put it as a new IDAT and return 0
        if(!Variables.containsKey(arr.getName())) {
            Variables.put(arr.getName(), new InterpreterArrayDataType());
            return new InterpreterDataType("0");
        }
        // Get Array from Variables if it exists
        InterpreterArrayDataType array = (InterpreterArrayDataType) Variables.get(arr.getName());
        // Check if it contains the name, return 1 if so and 0 if not.
        if(array.containsKey(Name))
            return new InterpreterDataType("1");
        return new InterpreterDataType("0");
    }

    private InterpreterDataType getField(InterpreterDataType left, InterpreterDataType Right, HashMap<String, InterpreterDataType> Variables) throws Exception{
        // Check if variables is null
        // If so, equal it to GlobalVaribales
        if(Objects.isNull(Variables))
            Variables = GlobalVariables;
        return Variables.get("$"+left.toString());
    }


    public class LineManager{
        private LinkedList<String> stringMembers;
        public LineManager(List<String> list){
            stringMembers = new LinkedList<String>();
            for(var c : list)
                stringMembers.add(c);
        }

        public boolean SplitAndAssign(){
            // Returns false if at empty line
            if(stringMembers.isEmpty())
                return false;
            // Resets variables
            reset();
            // Sets NR
            SplitAndAssign("$0");
            // Get next string from list
            String str = GlobalVariables.get("$0").toString();
            // Splits string up
            String[] splits = str.split(GlobalVariables.get("FS").toString());
            // Sets NF
            GlobalVariables.put("NF", new InterpreterDataType(String.valueOf(splits.length)));
            // Sets $1, $2, etc
            for(int i = 0; i < splits.length; i++)
                GlobalVariables.put("$"+Integer.valueOf(i+1), new InterpreterDataType(splits[i]));
            return true;
        }
        public void SplitAndAssign(String s){
            //set ln to nr
            int ln = Integer.valueOf(GlobalVariables.get("NR").toString());
            ln++;
            GlobalVariables.put("FNR", new InterpreterDataType(String.valueOf(ln)));
            GlobalVariables.put("NR", new InterpreterDataType(String.valueOf(ln)));
            // Returns a peek
            if(s.equals("$0"))
                GlobalVariables.put("$0", new InterpreterDataType(stringMembers.pop()));
            else{
                GlobalVariables.put(s, new InterpreterDataType(stringMembers.peek()));
                nextDifference++;
            }
        }
        public void reset(){
            int size = Integer.valueOf(GlobalVariables.get("NF").toString());
            for(int i = 0; i < size; i++)
                GlobalVariables.remove("$"+i);
        }

    }
}
