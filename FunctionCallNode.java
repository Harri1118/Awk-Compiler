package icsi311;

import java.util.LinkedList;

public class FunctionCallNode extends Node{
    private String name;
    LinkedList<StatementNode> parameters;

    public FunctionCallNode(String n, LinkedList<StatementNode> p){
        name = n;
        parameters = p;
    }
    public String toString(){
        return name + " (" + parameters + ")";
    }
}
