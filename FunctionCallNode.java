package icsi311;

import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode{
    private String Name;
    LinkedList<Node> parameters = new LinkedList<Node>();

    public FunctionCallNode(String n, LinkedList<Node> p){
        Name = n;
        parameters = p;
    }

    public FunctionCallNode(String n){
        Name = n;
    }

    public FunctionCallNode(String n, Optional<Node> p){
        Name = n;
        if(!p.isEmpty())
            parameters.add(p.get());
    }
    public String toString(){
        return Name + "Function Call: (" + betterParams(parameters.toString()) + ")";
    }

    public String betterParams(String s){
        return s.substring(1,s.length()-1);
    }
}
