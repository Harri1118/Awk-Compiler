package icsi311;

public class PatternNode extends Node{
    private String patternValue;
    public PatternNode(String s){
        patternValue = s;
    }
    public String toString(){
        return patternValue.substring(1,patternValue.length()-1);
    }

}
