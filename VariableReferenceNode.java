package icsi311;
import java.util.Optional;
public class VariableReferenceNode extends Node{
    private String name;
    private Optional<Node> Index = Optional.empty();

    private Optional<Node> Index2 = Optional.empty();
    public VariableReferenceNode(String n){
        name = n;
        Index = Optional.empty();
    }
    public VariableReferenceNode(String n, Optional<Node> o){
        name = n;
        Index = o;
    }

    public VariableReferenceNode(String n, Optional<Node> o, Optional<Node> o2){
        name = n;
        Index = o;
        Index2 = o2;
    }
    public String toString(){
        if(!Index.isEmpty() && Index2.isEmpty())
            return name + " V=(" + Index.get().toString() + "))";
        else if(!Index2.isEmpty())
            return "Variable(NAME=" + name + ", VALUE=(" + Index.get().toString() + ", " + Index2.get().toString() + "))";
        else
            return name;
    }

    public String getName(){
        return name;
    }

    public boolean hasValue(){
        if(!Index.isEmpty() || !Index2.isEmpty())
            return true;
        return false;
    }
}