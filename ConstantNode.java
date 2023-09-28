package icsi311;

public class ConstantNode extends Node {
    double value;
    String strValue;
    public ConstantNode(double i){
        value = i;
    }
    public ConstantNode(String s){strValue = s;}

    public String toString(){
        return "CONSTANT("+strValue+")";
    }
}
