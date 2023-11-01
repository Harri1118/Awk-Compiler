package icsi311;

import java.util.Optional;
public class IfNode extends StatementNode{

    Node Condition;
    BlockNode Block;
    Optional<StatementNode> Next = Optional.empty();

    private boolean isElse = false;
    public IfNode(Node c, BlockNode b, Optional<StatementNode> n){
        Block = b;
        Condition = c;
        Next = n;
        isElse = false;
    }
    public IfNode(Node c, BlockNode b){
        Block = b;
        Condition = c;
        isElse = false;
    }

    public IfNode(BlockNode b){
        isElse = true;
        Block = b;
    }
    public String toString(){
        if(isElse == true)
            return "{" + Block.toString() + "}";
        if(Next.isEmpty())
            return "If(" + Condition.toString() + "){" + Block.toString() + "}";
        return "If(" + Condition.toString() + "){" + Block.toString() + "} else " + Next.get().toString();
    }
}
