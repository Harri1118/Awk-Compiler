package icsi311;
import java.util.Optional;
public class VariableReferenceNode extends Node{
    private String name;
    private Optional<Node> operation;

    public VariableReferenceNode(String n){name = n;}
    public VariableReferenceNode(String n, Optional<Node> o){
        name = n;
        operation = o;
    }
    public String toString(){
        return name + " : " + operation;
    }
}
