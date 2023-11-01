package icsi311;

import java.util.LinkedList;
public class FunctionDefinitionNode extends Node{
private String name;
private String[] parameters;
private LinkedList<StatementNode> statements;

public FunctionDefinitionNode(String n, String[] p, LinkedList<StatementNode> s){
    name = n;
    parameters = p;
    statements = s;
}

public FunctionDefinitionNode(String n, String[] p){
    name = n;
    parameters = p;
}

    public String toString(){
    if(parameters.length > 0)
        return "\nfunc "+name + "(" + printParams() + "){\n" + betterParams(statements.toString()) + "\n}";
    else
        return "\nfunc "+name + "(){\n" + betterParams(statements.toString()) + "\n}";
    }

    //Method to print params for the toString() method.
public String printParams(){
    String f = "";
    for(int i = 0; i < parameters.length; i++)
        f += parameters[i] + ",";
    f = f.substring(0,f.length()-1);
    return f;
}

public String getName(){
    return name;
}

    private String betterParams(String s){
        return s.substring(1,s.length()-1);
    }
}
