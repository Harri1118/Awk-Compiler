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

    public String toString(){
    if(parameters.length > 0)
        return name + "(" + printParams() + ")" + " Statements: " + statements;
    else
        return name + "()" + " Statements: " + statements;
    }

    //Method to print params for the toString() method.
public String printParams(){
    String f = "";
    for(int i = 0; i < parameters.length; i++)
        f += parameters[i] + ",";
    f = f.substring(0,f.length()-1);
    return f;
}
}
