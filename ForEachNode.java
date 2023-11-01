package icsi311;

public class ForEachNode extends StatementNode{
    private Node Condition;
    private BlockNode Block;

    // If true, then don't print out block. This is to avoid erorrs
    private boolean emptyBlock;
    public ForEachNode(Node c, BlockNode b) {
        Condition = c;
        Block = b;
    }
    public ForEachNode(Node c){
        Condition = c;
        emptyBlock = true;
    }

    public String toString(){
        // If true, don't call or mention block
        if(emptyBlock == true)
            return "\nforEach(" + Condition + "){}";
        return "\nforEach(" + Condition + "){\n\t" + Block.toString() + "\n}\n";
    }
}
