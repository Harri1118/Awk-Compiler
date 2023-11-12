package icsi311;

public class ReturnNode extends StatementNode{
    private Node Returnable;

    public ReturnNode(Node n){
        Returnable = n;
    }

    public Node getReturnable(){
        return Returnable;
    }

    public String toString(){
        return "return " + Returnable.toString();
    }
}
