package icsi311;

import java.util.LinkedList;

public class DoWhileNode extends StatementNode{
    private Node Condition;
    private BlockNode Block;
    public DoWhileNode(Node c, BlockNode b){
        Block = b;
        Condition = c;
    }

    public String toString(){
        return "do{" + Block.toString() + "}\nwhile(" + Condition.toString() + ")";
    }

    public LinkedList<StatementNode> getStatements(){
        return Block.getStatements();
    }
    public Node getCondition(){
        return Condition;
    }
}
