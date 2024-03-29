package icsi311;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

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

    private ProgramNode Program;
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
        Program = program;
    }

    // PutBuiltInFunctions puts all of awk's built in functions
    private void putBuiltInFunctions() throws Exception{
        // create function object with hashMap input for each method
        Function<HashMap<String, InterpreterDataType>, String> Function = hashMap -> {
        // fin is to be printed as the final product
        String fin = "";
        // Retrieve content from input
        InterpreterArrayDataType content = (InterpreterArrayDataType) hashMap.get("content");
        // add all strings from content keySet. Add it to fin
            for(String s : content.getContent().keySet())
                fin += content.get(s);
            // printContent set to fin for UnitTesting purposes
            printContent = fin;
            // Print fin, return nothing
            System.out.println(fin);
        return "";
        };
        // Initialize and add print
        BuiltInFunctionDefinitionNode node = new BuiltInFunctionDefinitionNode("print", new String[]{"content"}, true, Function);
        Functions.put("print", node);

        // Function for printf
        Function = hashMap -> {
            // Get "content" from hashMap
            InterpreterArrayDataType content = (InterpreterArrayDataType) hashMap.get("content");
            // fin is the final string product which this method will print out
            String fin = "";
            // Format extracted from hashmap
            String format = hashMap.get("format").toString();
            // formats so split up format so that each individual format instance is taken care of
            String[] formats = format.split("%");
            // i initialized to iterate through format conversion
            int i = 2;
            // Check if there are more than two fields in the format
            if(formats.length > 2){
                // Iterate through formats in order to account for all format instances
                for(int n = 0; n < formats.length; n++){
                    // add first of list no matter what (in every single case there is no format instance)
                    if(n == 0)
                        fin += formats[0];
                    // Else, account for each format field
                    else{
                        // innerFormat extracts the format in which is to be implemented
                        String innerFormat = "%" + formats[n];
                        // convertToFormat called, automatically returns the input from content formatted with format instance
                        fin += convertToFormat(innerFormat, content.getContent().get(String.valueOf(i)).toString());
                        // i is added to increment to next field
                        i++;
                    }
                }
                // print out fin, return nothing
                System.out.println(fin);
                return "";
            }
            // If only one field, Increment through the content keyset
            for(String s : content.getContent().keySet())
                fin += content.get(s);
            // print formatted fin, return nothing
            System.out.printf(format, fin);
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
            if(hashMap.containsKey("targetToChange")){
                // whole is what the target will look like as a string
                String whole = hashMap.get("targetToChange").toString();
                // replace all the targets with replacement in modded
                String modded = whole.replace(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                // if no changes were made, return 0, elsewise return 1 and modify target
                if(whole.equals(modded))
                    return "0";
                hashMap.put("targetToChange", new InterpreterDataType(modded));
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
        node = new BuiltInFunctionDefinitionNode("gsub", new String[]{"regexp", "replacement", "targetToChange"}, false, Function);
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
            // Get "content" from hashMap
            InterpreterArrayDataType content = (InterpreterArrayDataType) hashMap.get("content");
            // fin is the final string product which this method will print out
            String fin = "";
            // Format extracted from hashmap
            String format = hashMap.get("format").toString();
            // formats so split up format so that each individual format instance is taken care of
            String[] formats = format.split("%");
            // i initialized to iterate through format conversion
            int i = 2;
            // Check if there are more than two fields in the format
            if(formats.length > 2){
                // Iterate through formats in order to account for all format instances
                for(int n = 0; n < formats.length; n++){
                    // add first of list no matter what (in every single case there is no format instance)
                    if(n == 0)
                        fin += formats[0];
                        // Else, account for each format field
                    else{
                        // innerFormat extracts the format in which is to be implemented
                        String innerFormat = "%" + formats[n];
                        // convertToFormat called, automatically returns the input from content formatted with format instance
                        fin += convertToFormat(innerFormat, content.getContent().get(String.valueOf(i)).toString());
                        // i is added to increment to next field
                        i++;
                    }
                }
                // Return fin
                return fin;
            }
            // If only one field, Increment through the content keyset
            for(String s : content.getContent().keySet())
                fin += content.get(s);
            return fin;
        };
        node = new BuiltInFunctionDefinitionNode("sprintf", new String[]{"format", "content"}, true, Function);
        Functions.put("sprintf", node);

        // method for sub
        Function = hashMap -> {
            // Field checking
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Invalid method call for sub! must be in the form: sub(regex, replacement, Optional[array])!");
            // if target is contained
            if(hashMap.containsKey("targetToChange")){
                // replace the first string
                String whole = hashMap.get("targetToChange").toString();
                String modded = whole.replaceFirst(hashMap.get("pattern").toString(), hashMap.get("replacement").toString());
                // if no diff then return 0
                if(whole.equals(modded))
                    return "0";
                // modify target and return
                hashMap.put("targetToChange", new InterpreterDataType(modded));
                return "1";
            }
            // get whole
            String whole = GlobalVariables.get("$0").toString();
            // Get modded from replacefirst of whole
            String modded = whole.replaceFirst(hashMap.get("pattern").toString(), hashMap.get("replacement").toString());
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
                String modified = part.replaceFirst(hashMap.get("pattern").toString(), hashMap.get("replacement").toString());
                // use put if difference
                if(!part.equals(modified)){
                    GlobalVariables.put("$"+i, new InterpreterDataType(modified));
                    return "1";
                }
                i++;
            }
            return "0";
        };
        node = new BuiltInFunctionDefinitionNode("sub", new String[]{"pattern", "replacement", "targetToChange"}, false, Function);
        Functions.put("sub", node);

        // Function for match
        Function = hashMap -> {
            //HashMap<String, InterpreterDataType> content = hashMap.get("content");
            // Argument checking
            if(hashMap.isEmpty() || hashMap.size() > 2)
                throw new IllegalArgumentException("Illegal match declaration! The match method must be in the form: match(string, pattern)!");
            // Check for string and pattern
            String content = hashMap.get("content").toString();
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
        node = new BuiltInFunctionDefinitionNode("match", new String[]{"content", "pattern"}, false, Function);
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
            hashMap.put("arrayToPost", finArray);
            return String.valueOf(arr.length);
        };
        node = new BuiltInFunctionDefinitionNode("split", new String[]{"target", "arrayToPost", "separator"}, false, Function);
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
                String str = hashMap.get("content").toString().substring(start, end);
                return str;
            }
            catch(Exception e){
                throw new IllegalArgumentException("Warning! Substring is configured incorrectly!");
            }
        };
        node = new BuiltInFunctionDefinitionNode("substr", new String[]{"content", "start", "end"}, false, Function);
        Functions.put("substr", node);

        // Function for toLower
        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! tolower must have 1 parameter!");
            return hashMap.get("content").toString().toLowerCase();
        };
        node = new BuiltInFunctionDefinitionNode("tolower", new String[]{"content"}, false, Function);
        Functions.put("tolower", node);

        // Function for toUpper
        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! toupper must have 1 parameter!");
            return hashMap.get("content").toString().toUpperCase();
        };
        node = new BuiltInFunctionDefinitionNode("toupper", new String[]{"content"}, false, Function);
        Functions.put("toupper", node);
    }

    // Checks if input is a String format
    private boolean isAStringFormat(char s){
        // Check if s is s, d, f, or c for a string format. Return false if no cases are true
        switch(s){
            case 's':
                return true;
            case 'd':
                return true;
            case 'f':
                return true;
            case 'c':
                return true;
        }
        return false;
    }

    // method built to format individual fields in a string format for printf and sprintf
    public String convertToFormat(String formatType, String content){
        // i initialized to 1 to iterate through formatType
        int i = 1;
        // s is gathered from formatType
        char s = formatType.charAt(i);
        // check while isAStringFormat(s) is false
        while(isAStringFormat(s) == false){
            // add 1 to i
            i++;
            // set s to next char
            s = formatType.charAt(i);
        }
        // After iterating is done, then set s to the char
        s = formatType.charAt(i);
        try {
            // if s is 's', then format it accordingly
            if (s == 's')
                return String.format(formatType, content);
            // else if s is 'd', then format it to integer and return
            else if (s == 'd') {
                int intg = Integer.parseInt(content);
                return String.format(formatType, intg);
            }
            // else if s is 'f', then format it to double and return
            else if (s == 'f') {
                double dubl = Double.parseDouble(content);
                return String.format(formatType, dubl);
            }
            // else if 'c', then format it to char and return
            else if (s == 'c') {
                if (content.length() > 1)
                    throw new Exception("");
                char c = content.charAt(0);
                return String.format(formatType, c);
            }
            // in any other case, convert content to int and format it
            else{
                int it = Integer.valueOf(i);
                return String.format(formatType, it);
            }
        }
        catch(Exception e){
            // Return formatType and content otherwise
        return formatType + content;}
    }
    // Method to put in custon functions. A simple for loop.
    private void putCustomFunctions(ProgramNode p){
        for(var f: p.getFuncs())
            Functions.put(f.getName(), f);
    }

    // InterpretProgram is the method which initiates interpreter
    public void InterpretProgram() throws Exception{
        // Interpret begin blocks first
        for(BlockNode b : Program.getBegin())
            InterpretBlock(b);
        // While the Manager still has more stringMembers, SplitAndAssign and interpret blocknodes from programnode's other list
        while(!Manager.stringMembers.isEmpty()){
            Manager.SplitAndAssign();
            for(BlockNode b: Program.getOther())
                InterpretBlock(b);
        }
        // Interpret end blocks afterwards
        for(BlockNode b : Program.getEnd())
            InterpretBlock(b);
    }

    // InterpretBlock iterates and interprets blocks
    public void InterpretBlock(BlockNode b) throws Exception{
        // if a condition is present, GetIDT to check if its true or not
        if(b.getCondition().isPresent()){
            InterpreterDataType result = GetIDT(b.getCondition().get(), null);
            if(isConvertable(result) == true)
                InterpretedListOfStatements(null, b.getStatements());
        }
        // Else, run the block if it has no condition
        else
            InterpretedListOfStatements(null, b.getStatements());
    }

    // runFuncCall to interpret functions
    public  String RunFunctionCall(HashMap<String, InterpreterDataType> locals, FunctionCallNode func) throws Exception {
        if(Objects.isNull(locals))
            locals = GlobalVariables;
        // If the ProgramNode's functions doesn't contain the key, throw an exception
        if(!Functions.containsKey(func.getName()))
            throw new Exception("Error! The function \"" + func.toString() + "\" doesn't exist!");
        // Create get FunctionDefinitionNode from inputted func name
        FunctionDefinitionNode function = Functions.get(func.getName());
        String targetName = "";
        String arrayToPost = "";
        // If the FunctionCallNode's length is not equal to the BuiltIn's size and the function isn't variadic, throw an exception
        if(function.getParameters().length < func.getParameters().size() && !function.isVariadic())
            throw new Exception("Invalid method call! Parameters don't match the parameter count!");
        // Create a params hashmap, this si to be inputted into the BuiltInFunctionDefinitionNode
        HashMap<String, InterpreterDataType> params = new HashMap<String, InterpreterDataType>();
        // i is set to 1 to iterate through hashmap
        int i = 0;
        if(func.getName().equals("printf") || func.getName().equals("sprintf"))
            i++;
        // Handle BuiltInFunctionDef node and variadic cases
        if(function instanceof BuiltInFunctionDefinitionNode && function.isVariadic() == true){
            // Get the builtInFuncDef
        BuiltInFunctionDefinitionNode pFunc = (BuiltInFunctionDefinitionNode) Functions.get(func.getName());
        // Create an IDAT which will be created to input into the variadic functions
        InterpreterArrayDataType paramContent = new InterpreterArrayDataType();

        // If the function is printf or sprintf, then put the first param 'format' into the hashmap
        if(pFunc.getName().equals("printf") || pFunc.getName().equals("sprintf"))
            params.put("format", GetIDT(func.getParameters().get(0), null));
        // Retrieve each node from parametwers
        for(Node n : func.parameters){
            // If printf or sprintf, and the param doesn't equal the format, put the node in the param
            if(!n.toString().equals(func.parameters.get(0).toString()) && (pFunc.getName().equals("printf") || pFunc.getName().equals("sprintf")))
                paramContent.getContent().put(String.valueOf(i), GetIDT(n, locals));
            // If it's just a print, put it in regardless
            else if(pFunc.getName().equals("print"))
                paramContent.getContent().put(String.valueOf(i), GetIDT(n, locals));
            // Add 1 to i to iterate over
            i++;
        }
        // put the IDAT with key content in the parameter
        params.put("content", paramContent);
        // Execute and return the result
        String fin = pFunc.Execute(params);
        return fin;
        }
        if(func.getParameters().size() != 0) {
            // For all other cases, get param from the function parameters
            for (String param : function.getParameters()) {
                // if i is equal to funcsize, then break
                if(i == func.getParameters().size())
                    break;
                // if param is a pattern, make a value and create a new IDT for it
                if(param.equals("pattern")) {
                    String value = func.getParameters().get(i).toString();
                    params.put(param, new InterpreterDataType(value));
                }
                // if param is targetToChange, get variablename and set it to targetname
                else if(param.equals("targetToChange")){
                    VariableReferenceNode var = (VariableReferenceNode) func.getParameters().get(i);
                    targetName = var.getName();
                    params.put("targetToChange", GetIDT(func.getParameters().get(i), locals));
                }
                // if param is arrayToPost, set array target name to arrayToPost
                else if(param.equals("arrayToPost")){
                    VariableReferenceNode var = (VariableReferenceNode) func.getParameters().get(i);
                    arrayToPost = var.getName();
                    params.put("arrayToPost", new InterpreterArrayDataType());
                }
                // if param is a variable, check if its an instance of variablereferencenode and throw an error if not true
                else if(param.equals("variable")){
                    if(!(func.getParameters().get(i) instanceof VariableReferenceNode))
                        throw new Exception("Error! Cannot use a non-variable for getline! Must be in the form of \"getline var\"");
                    params.put(param, GetIDT(func.getParameters().get(i), locals));
                }
                // put param in otherwise
                else
                    params.put(param, GetIDT(func.getParameters().get(i), locals));
                // add 1 to i
                i++;
            }
        }
        // returned is the final product
        ReturnType returned = new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        // fin is empty in cases of NORMAL. Will change for return cases
        String fin = "";
        // If function is BuiltIn
        if(function instanceof BuiltInFunctionDefinitionNode) {
            // Get builtInFunction from function
            BuiltInFunctionDefinitionNode Bfunction = (BuiltInFunctionDefinitionNode) function;
            // fin is equal to the product of Bfunction
            fin = Bfunction.Execute(params);
        }
        // else, InterpretList of statements returned from the custom function
        else
            returned = InterpretedListOfStatements(params, function.getStatements());
        // If return, fin is set to the product of the return obj
        if(returned.getReturnType() == ReturnType.TypeOfReturn.RETURN)
            fin = returned.toString();
        // If TargetToChance is contained in params, put variable name in params and execute method
        if(params.containsKey("targetToChange"))
            locals.put(targetName, new InterpreterDataType(params.get("targetToChange").toString()));
        // If params contains ArraytoPost, create a new array and put it in params
        if(params.containsKey("arrayToPost")){
            InterpreterArrayDataType idat = (InterpreterArrayDataType) params.get("arrayToPost");
            InterpreterArrayDataType finalArray = new InterpreterArrayDataType();
            for(String n : idat.getContent().keySet()){
                locals.put(arrayToPost + "[" + n + "]", idat.get(n));
                finalArray.put(n);
            }
            locals.put(arrayToPost, finalArray);
        }
        // return fin
        return fin;
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
            if(!(aNode.getTarget() instanceof VariableReferenceNode) && (aNode.Expression.isPostIncremental() || aNode.Expression.isPreIncremental()))
                throw new Exception("Error! Cannot pre-increment to any other value except a VariableReferenceNode! Ex: ++var, --var");
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
            if(!aNode.Expression.isPostIncremental())
                Variables.put(name, val);
            else
                return val;
            // If Target is part of an array and the array is already initialized, add it to the existing array and put it as a Variable into Variables
            if(Target.isArray() && Variables.containsKey(Target.getName())){
                arr = (InterpreterArrayDataType) Variables.get(Target.getName());
                arr.put(Target.getIndex());
            }
            // If first time initializing the array, then create it and set it in Variables, add its first variable, and return the value
            else if(Target.isArray() && !(Variables.containsKey(Target.getName()))) {
                arr = new InterpreterArrayDataType();
                arr.put(Target.getIndex());
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
            return new InterpreterDataType(RunFunctionCall(Variables, func));
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
            if(((VariableReferenceNode) VNode).hasValue()){
                InterpreterDataType fin = Variables.get(var.getName() + "[\"" + Variables.get(var.getIndex().toString()) + "\"]");
                if(Objects.isNull(fin)) {
                    fin = Variables.get(var.getName() + "[" + Variables.get(var.getIndex().toString()) + "]");
                    if(Objects.isNull(fin)){
                        fin = Variables.get(var.getName() + "[\"" + var.getIndex().toString() + "\"]");
                        if(Objects.isNull(fin))
                            fin = Variables.get(var.getName() + "[" + var.getIndex().toString() + "]");
                    }
                }
                if(Objects.isNull(fin))
                    return new InterpreterDataType("");
                return fin;
            }
            // Check if Variable is contained in Variables. If not then return 0
            if(!Variables.containsKey(var.toString()) || Variables.get(var.toString()).isEmpty())
                return new InterpreterDataType("");

            if(Variables.containsKey(var.toString())){
                if(Variables.get(var.toString()) instanceof InterpreterArrayDataType){
                    return new InterpreterDataType();
                }
                return new InterpreterDataType(Variables.get(var.toString()).toString());}
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
        InterpreterDataType Left = GetIDT(operationNode.getLeftValue(), Variables);
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
            return getField(Left,  Variables);
        // Check if operation is MATCH or NOTMATCH, then return Match with appropriate Variables
        if (operationNode.getOperation() == OperationNode.PossibleOperations.MATCH || operationNode.getOperation() == OperationNode.PossibleOperations.NOTMATCH)
            return Match(Left, operationNode.getRightValue().get(), operationNode.getOperation());
        // Check if operation is UNARYPOS or UNARYNEG, then return Unary with appropriate Variables
        if (operationNode.getOperation() == OperationNode.PossibleOperations.UNARYPOS || operationNode.getOperation() == OperationNode.PossibleOperations.UNARYNEG)
            return Unary(Left, operationNode.getOperation());
        // If all these ifs fail, interpret the right value.
        InterpreterDataType Right = GetIDT(operationNode.getRightValue().get(), Variables);
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
        // if op is unaryneg, negate it and return it. if it is negative and the operator is unarypos, negate it to make it positive. Elsewise return the result.
        if(op == OperationNode.PossibleOperations.UNARYNEG){
            num *= -1;
            return new InterpreterDataType(String.valueOf(num));}
        else if(op == OperationNode.PossibleOperations.UNARYPOS && num < 0){
            num *= -1;
        }
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
        boolean fin = expr.contains(regex);
        // If op is NOTMATCH, return 1 if it cannot find it, return 0 otherwise.
        if(op == OperationNode.PossibleOperations.NOTMATCH){
            if(!fin)
                return new InterpreterDataType("1");
            return new InterpreterDataType("0");
        }
        // Otherwise, return 1 if found, return 0 otherwise.
        if (fin)
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
        LinkedList<String> values = new LinkedList<String>();
        for(String f : array.getContent().keySet())
            values.add(array.getContent().get(f).toString());
        // Check if it contains the name, return 1 if so and 0 if not.
        if(values.contains(Name))
            return new InterpreterDataType("1");
        return new InterpreterDataType("0");
    }

    private InterpreterDataType getField(InterpreterDataType left, HashMap<String, InterpreterDataType> Variables) throws Exception{
        // Check if variables is null
        // If so, equal it to GlobalVaribales
        if(Objects.isNull(Variables) || Variables.isEmpty())
            Variables = GlobalVariables;
        if(!Variables.containsKey("$" + left.toString()))
            return new InterpreterDataType("");
        return Variables.get("$"+left.toString());
    }

    // isLoop checks the instance type of StatementNode s. If it is not any of the below
    // objects, return false
    public boolean isLoop(StatementNode s){
        if(s instanceof ForNode)
            return true;
        else if(s instanceof ForEachNode)
            return true;
        else if(s instanceof DoWhileNode)
            return true;
        else if(s instanceof WhileNode)
            return true;
        else
            return false;
    }

    public ReturnType InterpretedListOfStatements(HashMap<String,InterpreterDataType> locals, LinkedList<StatementNode> Statements) throws Exception{
        // fin is the object which will be ultimately returned
        ReturnType fin = new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        // If the locals are null, then make them global
        if(Objects.isNull(locals))
            locals = GlobalVariables;
        // Iterate inside the input Statements to process each Statement
        for(var s: Statements){
            // If s is a loop, do not return back whatever ReturnType is returned.
            if(isLoop(s) == true) {
                ProcessStatement(locals, s);
            }
            // Process the Statement otherwise, and return a different returnType if not normal
            else {
                fin = ProcessStatement(locals, s);
                if (fin.getReturnType() != ReturnType.TypeOfReturn.NORMAL)
                    break;
            }
        }
        return fin;
    }

    // ProcessStatement takes in locals and a singular statement to process
    public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        ReturnType fin = ProcessBreakNode(locals, stmt);
        // Check if BreakNode returns null
        if(Objects.isNull(fin)){
            if(stmt instanceof ReturnNode)
                return new ReturnType(ReturnType.TypeOfReturn.RETURN, GetIDT(stmt, locals).toString());
            // GetIDT and process whatever the statement is. Then return normal
            GetIDT(stmt, locals);
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        }
        else
            return fin;
    }

    // ProcessBreakNode handles Break nodes
    public ReturnType ProcessBreakNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
        // Create new break ReturnType if stmt is an instance of breakNode
        if(stmt instanceof BreakNode)
            return new ReturnType(ReturnType.TypeOfReturn.BREAK);
        // Return ContinueNode otherwise
        return ProcessContinueNode(locals, stmt);
    }

    // ProcessContinueNode handles continue nodes
    public ReturnType ProcessContinueNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
        // Checks of stmt is an instance of a ContinueNode. Return a new ReturnType Continue if so
        if(stmt instanceof ContinueNode)
            return new ReturnType(ReturnType.TypeOfReturn.CONTINUE);
        // If not, return ProcessDeleteNode
        return ProcessDeleteNode(locals, stmt);
    }

    // ProcessDeleteNode handles delete nodes
    public ReturnType ProcessDeleteNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
        // Check if stmt is an instance of deletenode
        if(stmt instanceof DeleteNode){
            // get VariableReferenceNode from stmt
            DeleteNode inp = (DeleteNode) stmt;
            VariableReferenceNode var = (VariableReferenceNode) inp.getTarget();
            // Check of if the var's name attribute is an InterpreterArrayDataType.
            // If not, then throw an exception
            if(!(locals.get(var.getName()) instanceof InterpreterArrayDataType))
                throw new Exception("Cannot delete a non-array!");
            // if the toString is an interpreterArrayDataType in the locals, then call removeAll
            else if(locals.get(var.toString()) instanceof InterpreterArrayDataType)
                removeAll(locals, var.toString());
            // else, get the array, and delete the variable from locals and its reference in its IDAT
            else{
                InterpreterArrayDataType arr = (InterpreterArrayDataType) locals.get(var.getName());
                String s = var.getIndex();
                arr.removeKey(var.getIndex());
                locals.remove(var.toString());
            }
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        }

        return ProcessDoWhileNode(locals, stmt);
    }

    // removeAll eliminates all the elements from s
    public void removeAll(HashMap<String, InterpreterDataType> locals, String s){
        // pulls arr from locals
        InterpreterArrayDataType arr = (InterpreterArrayDataType) locals.get(s);
        // parses through each key in locals and deletes the accompanying var
        for(String key: arr.getContent().keySet())
            locals.remove(s + "[\"" + key + "\"]");
        // finally, remove IDAT
        locals.remove(s);
    }

    // doWhile deals with doWhile nodes
    public ReturnType ProcessDoWhileNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
        if(stmt instanceof DoWhileNode){
            DoWhileNode doWhile = (DoWhileNode) stmt;
            String s = "";
            // emulate the do-while loop. The condition depends of the doWhile objects' .getCondition()
            // returned from GetIDT
            do {
                // Get returnType fin from InterpretedListOfStatements()
                ReturnType fin = InterpretedListOfStatements(locals, doWhile.getStatements());
                // If fin doesn't return normal, return it
                if(fin.getReturnType() != ReturnType.TypeOfReturn.NORMAL)
                    return fin;
                s = GetIDT(doWhile.getCondition(), locals).toString();
            }
            while(s.equals("1"));
            // Return normal otherwise
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        }
        return ProcessForNode(locals, stmt);
    }

    public ReturnType ProcessForNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        if(stmt instanceof ForNode){
            // Create a ForNode if stmt is an instance of it
            ForNode forNode = (ForNode) stmt;
            // Retrieve its LinkedList<Node> Condition
            LinkedList<Node> condition = forNode.getCondition();
            // Process its first condition
            ProcessStatement(locals, (StatementNode) condition.get(0));
            // Make sure the GetIDT is equal to 1 from condition.get(1), and then
            // initiate a while loop which goes through the inner statements
            while(GetIDT(condition.get(1), locals).toString().equals("1")){
                // Get fin
                ReturnType fin = InterpretedListOfStatements(locals, forNode.getBlock().getStatements());
                // If fin isn't normal, return it
                if(fin.getReturnType() != ReturnType.TypeOfReturn.NORMAL)
                    return fin;
                // ProcessStatement for the third condition in the LinkedList<Node> condition
                ProcessStatement(locals, (StatementNode) condition.get(2));
            }
            // Return normal
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
             }
        return ProcessForEachNode(locals, stmt);
    }

    public ReturnType ProcessForEachNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        if(stmt instanceof ForEachNode){
            ForEachNode forEachNode = (ForEachNode) stmt;
            // Get OpoerationNode Condition from the ForEachNode
            OperationNode condition = (OperationNode) forEachNode.getCondition();
            // Create a variable from the left value of condition
            VariableReferenceNode leftValue  = (VariableReferenceNode) condition.getLeftValue();
            // Put the leftValue in the locals
            locals.put(leftValue.getName(), new InterpreterDataType(""));
            // Find the variable from the right value of the condition (first in the locals, and if not then find it in global)
            VariableReferenceNode rightValue = (VariableReferenceNode) condition.getRightValue().get();
            // Create the array. Find it in the locals
            InterpreterArrayDataType arr;
            if(!locals.containsKey(rightValue.getName())){
                // If it doesn't contain the key, return normal
                if (!GlobalVariables.containsKey(rightValue.getName()))
                    return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
                // If found in glovals, set it
                arr = (InterpreterArrayDataType) GlobalVariables.get(rightValue.getName());
            }
            // If found in locals, set it
            else
                arr = (InterpreterArrayDataType) locals.get(rightValue.getName());
            // Emulate java's forEach
            for(String f : arr.getContent().keySet()){
            String name = arr.getContent().get(f).toString();
            // Get value of f from array and set it to f
            //locals.put(leftValue.getName(), locals.get(rightValue.getName()+"[\"" + arr.getContent().get(f).toString() + "\"]"));
            locals.put(leftValue.getName(), new InterpreterDataType(name));
            if(Objects.isNull(locals.get(leftValue.getName())))
                locals.put(leftValue.getName(), locals.get(rightValue.getName()+"[" + arr.getContent().get(f).toString() + "]"));
            // Get returnType from InterpretedListOfStatements
            ReturnType fin = InterpretedListOfStatements(locals, forEachNode.getBlock().getStatements());
            // If not normal, return it
            if(fin.getReturnType() != ReturnType.TypeOfReturn.NORMAL)
                return fin;
            // Otherwise, if it is still contained as a local, set the value of f to it
            //if(locals.containsKey(rightValue.getName()+"[\"" + f + "\"]"))
              //  locals.put(rightValue.getName()+"[\"" + f + "\"]", locals.get(leftValue.getName()));
            }
            // Return normal otherwise
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        }
        return ProcessIfNode(locals, stmt);
    }

    public ReturnType ProcessIfNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        if(stmt instanceof IfNode){
            IfNode ifNode = (IfNode) stmt;
            // Check if IfNode is true. If so then call InterpretedListOfStatements
            if(GetIDT(ifNode.Condition, locals).toString().equals("1"))
                return InterpretedListOfStatements(locals, ifNode.Block.getStatements());
            // If this ifNode has no Next, return normal
            if(ifNode.Next.isEmpty()){
                return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
            }
            // Create an optional NextIfNode to parse through the next if statements
            Optional<IfNode> nextIfNode = Optional.of((IfNode) ifNode.Next.get());
            while(true){
                // if nextIfNode is an else, then break out of this while loop
               if(nextIfNode.get().isElse())
                   break;
               // If the GetIDT of this if condition returns 1, return InterpretedListOfStatements
               if(GetIDT(nextIfNode.get().Condition, locals).toString().equals("1"))
                   return InterpretedListOfStatements(locals, nextIfNode.get().Block.getStatements());
               // If the nextIfNode is empty, then return normal
               if(nextIfNode.isEmpty())
                   return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
               // If Next is empty, then return normal
               if(nextIfNode.get().Next.isEmpty())
                   return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
               // Set the nextIfNode to its next member
               nextIfNode = Optional.of((IfNode) nextIfNode.get().Next.get());
            }
            // Return nextIfNode is broken out from while loop (this is for else cases)
            return InterpretedListOfStatements(locals, nextIfNode.get().Block.getStatements());
        }
        return ProcessReturnNode(locals, stmt);
    }

    public ReturnType ProcessReturnNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        if(stmt instanceof ReturnNode){
            ReturnNode Return = (ReturnNode) stmt;
            // Get returnable from stmt and turn it into a node
            Node returnable = Return.getReturnable();
            // Return a ReturnType with a RETURN type and a GETIDT for returnable
            return new ReturnType(ReturnType.TypeOfReturn.RETURN, GetIDT(returnable, locals).toString());
        }
        return ProcessWhileNode(locals, stmt);
    }

    public ReturnType ProcessWhileNode(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception{
        if(stmt instanceof WhileNode){
            WhileNode whileNode = (WhileNode) stmt;
            // Create a while loop which runs as long as condition is 1
            while(GetIDT(whileNode.getCondition(), locals).toString().equals("1")){
                // Returned checks to see if InterpretedListOfStatements doesn't return NORMAL
                ReturnType returned = InterpretedListOfStatements(locals, whileNode.getBlock().getStatements());
                if(returned.getReturnType() == ReturnType.TypeOfReturn.CONTINUE)
                    continue;
                // If nor normal, return returned.
                if(returned.getReturnType() != ReturnType.TypeOfReturn.NORMAL)
                    return returned;
            }
            return new ReturnType(ReturnType.TypeOfReturn.NORMAL);
        }
        return null;
    }


    public ProgramNode getProgram(){
        return Program;
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
            //set ln to nr, increment ln
            int ln = Integer.valueOf(GlobalVariables.get("NR").toString());
            ln++;
            // set FNR and NR glovals to ln
            GlobalVariables.put("FNR", new InterpreterDataType(String.valueOf(ln)));
            GlobalVariables.put("NR", new InterpreterDataType(String.valueOf(ln)));
            // Returns a peek if s equals $0
            if(s.equals("$0"))
                GlobalVariables.put("$0", new InterpreterDataType(stringMembers.pop()));
            // else, put peek into globals and increment nextDifference
            else{
                GlobalVariables.put(s, new InterpreterDataType(stringMembers.peek()));
                nextDifference++;
            }
        }

        // resets all fields
        public void reset(){
            int size = Integer.valueOf(GlobalVariables.get("NF").toString());
            for(int i = 0; i < size; i++)
                GlobalVariables.remove("$"+i);
        }

    }
}
