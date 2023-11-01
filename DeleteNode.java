package icsi311;

public class DeleteNode extends StatementNode{
    private Node Target;

    public DeleteNode(Node n){
        Target = n;
    }
public String toString(){
        return "DELETE " + Target.toString();
}
}
