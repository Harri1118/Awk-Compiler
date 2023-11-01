package icsi311;

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

}
