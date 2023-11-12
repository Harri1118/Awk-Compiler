package icsi311;

public class WhileNode extends StatementNode{
    private Node Condition;
    private BlockNode Block;
    public WhileNode(Node c, BlockNode b){
        Block = b;
        Condition = c;
    }

    public Node getCondition(){
        return Condition;
    }

    public BlockNode getBlock(){
        return Block;
    }
    public String toString(){
        return "while(" + Condition.toString() + "){" + Block.toString() + "}";
    }

}
