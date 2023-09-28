package icsi311;

public class ConstantNode extends Node {
    double value;
<<<<<<< HEAD
    String strValue;
    public ConstantNode(double i){
        value = i;
    }
    public ConstantNode(String s){strValue = s;}

    public String toString(){
        return "CONSTANT("+strValue+")";
    }
=======
    public ConstantNode(double i){
        value = i;
    }
>>>>>>> 4e5781c (Changes for Parser 2)
}
