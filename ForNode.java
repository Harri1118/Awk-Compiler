package icsi311;

import java.util.LinkedList;

public class ForNode extends StatementNode{
    private LinkedList<Node> Condition;
    private BlockNode Block;

    // If true, then don't print out block. This is to avoid erorrs
    private boolean emptyBlock = false;
    public ForNode(LinkedList<Node> l, BlockNode b){
        Block = b;
        Condition = l;
    }

    public ForNode(LinkedList<Node> l){
        Condition = l;
        emptyBlock = true;
    }
    public String toString(){
        // If true, don't call or mention block
        if(emptyBlock == true)
            return "for(" + ConditionListToString() + "){}";
        return "\nfor(" + ConditionListToString() + "){\n\t" + Block.toString() + "\n}\n";
    }

    public String ConditionListToString(){
        String f = "";
        for(int i  = 0; i < Condition.size(); i++)
            f += Condition.get(i).toString() + ", ";
        f = f = f.substring(0,f.length()-2);
        return f;
    }
}
