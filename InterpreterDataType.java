package icsi311;

public class InterpreterDataType {
    private String value;
    public InterpreterDataType(){
        value = "";
    }
    public InterpreterDataType(String s){
        value = s;
    }

    public String toString(){
        if(isFloat() == true){
            float val = Float.parseFloat(value);
            float rounded = (float) Math.floor(val);
            if(val - rounded == 0)
                value = String.valueOf((int) val);
        }
        return value;
    }

    public boolean isFloat(){
        try{
            Float.parseFloat(value);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public boolean isEmpty(){
        if(value.length() == 0)
            return true;
        return false;
    }
}
