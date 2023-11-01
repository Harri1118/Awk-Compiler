package icsi311;

import java.util.LinkedList;

public class TernaryNode extends Node{
    private Node Condition;
    private Node Consequent;

    private Node Alternate;

    private static LinkedList<OperationNode.PossibleOperations> List;
    public TernaryNode(Node cond, Node cons, Node a){
    Condition = cond;
    Consequent = cons;
    Alternate = a;
    }

    public String toString(){
        return "TernaryNode(CONDITION: " + Condition.toString() + ", CONSEQUENT: " + Consequent + ", ALTERNATE: " + Alternate + ")";
    }

}
