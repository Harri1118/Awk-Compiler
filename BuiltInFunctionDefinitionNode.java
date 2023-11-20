package icsi311;

import java.util.HashMap;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode{
    private boolean Variadic = false;

    private Function<HashMap<String, InterpreterDataType>, String> function;
    public BuiltInFunctionDefinitionNode(String n, String[] p, boolean b, Function<HashMap<String, InterpreterDataType>, String> f) {
        super(n,p);
        Variadic = b;
        function = f;
        int i;
    }

    public String Execute(HashMap<String, InterpreterDataType> input){
        return function.apply(input);
    }

    @Override
    public boolean isVariadic(){
        return Variadic;
    }
    public String toString(){
        return getName();
    }
}
