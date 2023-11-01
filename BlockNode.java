package icsi311;

import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node {
    private LinkedList<StatementNode> statements = new LinkedList<StatementNode>();

    private Optional<Node> Condition = Optional.empty();


    public BlockNode(Node c, LinkedList<StatementNode> n){
        statements = n;
        Condition = Optional.of(c);
    }

    public BlockNode(Node c, StatementNode s){
        Condition = Optional.of(c);
        statements.add(s);
    }
    public BlockNode(StatementNode s){
        statements.add(s);
    }
    public BlockNode(LinkedList<StatementNode> s){
        statements = s;
    }
    public String toString(){
        if(Condition.isEmpty())
            return  statementsAsString() + "\n";
        return "("+Condition.get().toString() + "){" + statementsAsString() + "\n}";
    }

    public LinkedList<StatementNode> getStatements(){
        return statements;
    }

    public String statementsAsString(){
        String f = "";
        for(int i = 0; i < statements.size(); i++){
            f += "\n" + statements.get(i);
        }
        return f;
    }
}
