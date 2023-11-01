package icsi311;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Interpreter {
    public String printContent = "";
    public HashMap<String, InterpreterDataType> GlobalVariables = new HashMap<String, InterpreterDataType>();
    public HashMap<String, FunctionDefinitionNode> Functions = new HashMap<String, FunctionDefinitionNode>();

    private int nextDifference = 0;
    public LineManager Manager;

    public Interpreter(ProgramNode program, Optional<Path> filePath) throws Exception, IOException {
        if(!filePath.isEmpty())
            Manager = new LineManager(Files.readAllLines(filePath.get()));
        else
            Manager = new LineManager(new LinkedList<String>());

        GlobalVariables.put("FILENAME", new InterpreterDataType(filePath.toString()));
        GlobalVariables.put("FS", new InterpreterDataType(" "));
        GlobalVariables.put("OFMT", new InterpreterDataType("%.6g"));
        GlobalVariables.put("ORS",new InterpreterDataType("\n"));
        GlobalVariables.put("NF", new InterpreterDataType("0"));
        GlobalVariables.put("NR", new InterpreterDataType("0"));
        GlobalVariables.put("NFR", new InterpreterDataType("1"));

        putBuiltInFunctions();
        if(program.getFuncs().size() > 0)
            putCustomFunctions(program);
    }
    private void putBuiltInFunctions() throws Exception{
        //Goal: print out a hashmap
        Function<HashMap<String, InterpreterDataType>, String> Function = hashMap -> {
        // Get "content" from hashMap
        InterpreterArrayDataType inp = (InterpreterArrayDataType) hashMap.get("content");
        printContent = inp.printValue();
        System.out.println(inp.printValue());
        return "";
        };
        BuiltInFunctionDefinitionNode node = new BuiltInFunctionDefinitionNode("print", new String[]{"content"}, true, Function);
        Functions.put("print", node);

        Function = hashMap -> {
            // Get "content" from hashMap
            InterpreterArrayDataType inp = (InterpreterArrayDataType) hashMap.get("content");
            if(hashMap.containsKey("format")) {
                printContent = "Format: " + hashMap.get("format").toString() + ", Result: " + inp.printValue();
                System.out.println(printContent);
                return "";
            }
            printContent = "Format: " + GlobalVariables.get("OFMT").toString() + ", Result: " + inp.printValue();
            System.out.println(printContent);
            return "";
        };
        node = new BuiltInFunctionDefinitionNode("printf", new String[]{"format", "content"}, true, Function);
        Functions.put("printf", node);

        Function = hashMap -> {
            if(hashMap.isEmpty()) {
                if (Manager.SplitAndAssign() == true)
                    return "1";
            }
            else if(hashMap.containsKey("variable")){
                Manager.SplitAndAssign(hashMap.get("variable").toString());
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
            Manager.SplitAndAssign();
            return "";
        };
        node = new BuiltInFunctionDefinitionNode("next", new String[]{}, false, Function);
        Functions.put("next", node);

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Invalid method call for gsub! must be in the form: gsub(regex, replacement, Optional[array])!");
            if(hashMap.containsKey("target")){
                String whole = hashMap.get("target").toString();
                String modded = whole.replace(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                if(whole.equals(modded))
                    return "0";
                hashMap.put("target", new InterpreterDataType(modded));
                return "1";
            }
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

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 2)
                throw new IllegalArgumentException("Illegal function call of index! It must only be called like index(string, substring)!");
            String s = hashMap.get("string").toString();
            String sub = hashMap.get("substring").toString();
            if(!s.contains(sub))
                return "0";
            int pos = 0;
            for(int i = 0; i < s.length(); i++){
                if(i+sub.length() > s.length())
                    return "0";
                if(s.charAt(i) == sub.charAt(0)){
                    if(s.substring(i,i+sub.length()).equals(sub)){
                        pos = i;
                        break;
                    }
                }
            }
            pos = pos + 1;
            return String.valueOf(pos);
        };
        node = new BuiltInFunctionDefinitionNode("index", new String[]{"string", "substring"}, false, Function);
        Functions.put("index", node);

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! Length must be explicitly called with a param! Ex: length(string)");
            return String.valueOf(hashMap.get("string").toString().length());
        };
        node = new BuiltInFunctionDefinitionNode("index", new String[]{"string"}, false, Function);
        Functions.put("length", node);

        Function = hashMap -> {
            if(hashMap.size() > 2 || !hashMap.containsKey("format") || !hashMap.containsKey("content"))
                throw new IllegalArgumentException("Error! Method to format string is incorrect! Must be in form sprintf(format, string)!");
            InterpreterArrayDataType content = (InterpreterArrayDataType) hashMap.get("content");
            return "Format: " + hashMap.get("format").toString() + ", content: " + content.printValue();
        };
        node = new BuiltInFunctionDefinitionNode("index", new String[]{"format", "content"}, false, Function);
        Functions.put("sprintf", node);

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 3)
                throw new IllegalArgumentException("Invalid method call for sub! must be in the form: sub(regex, replacement, Optional[array])!");
            if(hashMap.containsKey("target")){
                String whole = hashMap.get("target").toString();
                String modded = whole.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
                if(whole.equals(modded))
                    return "0";
                hashMap.put("target", new InterpreterDataType(modded));
                return "1";
            }
            String whole = GlobalVariables.get("$0").toString();
            String modded = whole.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
            if(whole.equals(modded))
                return "0";
            GlobalVariables.put("$0", new InterpreterDataType(modded));
            int i = 1;
            while(GlobalVariables.containsKey("$"+i)){
                String part = GlobalVariables.get("$" + i).toString();
                String modified = part.replaceFirst(hashMap.get("regexp").toString(), hashMap.get("replacement").toString());
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

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 2)
                throw new IllegalArgumentException("Illegal match declaration! The match method must be in the form: match(string, pattern)!");
            String content = hashMap.get("string").toString();
            String pattern = hashMap.get("pattern").toString();
            if(!content.contains(pattern))
                return "0";
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

        Function = hashMap -> {
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

        Function = hashMap -> {
            if(hashMap.size() > 3)
                throw new IllegalArgumentException("Warning! The substr method must have 3 and strictly 3 parameters! Ex: substr(str, 1,2)");
            try {
                int start = Integer.valueOf(hashMap.get("start").toString());
                int end = Integer.valueOf(hashMap.get("end").toString());
                return hashMap.get("string").toString().substring(start, end);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Warning! Substring is configured incorrectly!");
            }
        };
        node = new BuiltInFunctionDefinitionNode("substr", new String[]{"string", "start", "end"}, true, Function);
        Functions.put("substr", node);

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! tolower must have 1 parameter!");
            return hashMap.get("string").toString().toLowerCase();
        };
        node = new BuiltInFunctionDefinitionNode("tolower", new String[]{"content"}, true, Function);
        Functions.put("tolower", node);

        Function = hashMap -> {
            if(hashMap.isEmpty() || hashMap.size() > 1)
                throw new IllegalArgumentException("Error! toupper must have 1 parameter!");
            return hashMap.get("string").toString().toUpperCase();
        };
        node = new BuiltInFunctionDefinitionNode("toupper", new String[]{"content"}, true, Function);
        Functions.put("toupper", node);
    }

    private void putCustomFunctions(ProgramNode p){
        for(var f: p.getFuncs())
            Functions.put(f.getName(), f);
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
            int varNum = 0;
            int size = Integer.valueOf(GlobalVariables.get("NF").toString());
            for(int i = 0; i < size; i++)
                GlobalVariables.remove("$"+i);
        }

    }
}
