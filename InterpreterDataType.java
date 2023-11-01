package icsi311;

public class InterpreterDataType {
    private String value;
    public InterpreterDataType(){
        value = "null";
    }
    public InterpreterDataType(String s){
        value = s;
    }

    public String toString(){
        return value;
    }

}
