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
        return strValue;
    return fixNum(value);
    }

    public boolean isString(){
        return isString;
    }
    public String fixNum(double d){
        String f = String.valueOf(d);
        int rd = (int) d;
        double diff = d - (double)rd;
        if(diff == 0)
            return f.substring(0, f.length()-2);
        return f;
    }
}