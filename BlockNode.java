package icsi311;

import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node {
    private LinkedList<StatementNode> statements = new LinkedList<StatementNode>();
    private Optional<Node> Condition;

    public BlockNode(LinkedList<StatementNode> n, Optional<Node> c){
        statements = n;
        Condition = c;
    }
    public String toString(){
        return "Condition: " + Condition + ", Statements: " + statements;
    }



    public void setCondition(Optional<Node> c){
        Condition = c;
    }

    public LinkedList<StatementNode> getStatements(){
        return statements;
    }

}
