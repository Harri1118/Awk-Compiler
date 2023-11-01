package icsi311;

public class ConstantNode extends Node {
    double value;
    String strValue;

    private boolean isString;
    public ConstantNode(double i){
        value = i;
        strValue = String.valueOf(i);
        isString = false;
    }
    public ConstantNode(String s){strValue = s;
    isString= true;
    }

    public String toString(){
    if(isString == true)
        return "\""+strValue+"\"";
    return String.valueOf(value);
    }

    public boolean isString(){
        return isString;
    }
}