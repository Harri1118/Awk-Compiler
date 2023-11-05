package icsi311;

import java.util.HashMap;

public class InterpreterArrayDataType extends InterpreterDataType{
    private HashMap<String, InterpreterDataType> storage;
    private boolean isNumeric = false;
    public InterpreterArrayDataType(){
        storage = new HashMap<String, InterpreterDataType>();
    }

    public void put(String s){
        int size = storage.size();
        String key = String.valueOf(size);
        storage.put(key, new InterpreterDataType(s));
        if(isNumeric == false)
            isNumeric = true;
    }

    public void putKey(String s){
        storage.put(s, new InterpreterDataType(s));
    }

    public void put(InterpreterDataType i){

    }
    public InterpreterDataType get(String s){
        return storage.get(s);
    }

    public String toString(){
        if(isNumeric == true){
        String fin = "(";
        for(int i = 0; i < storage.size(); i++){
            fin += storage.get(String.valueOf(i)).toString() + ",";
        }
        fin = fin.substring(0,fin.length()-1);
        return fin + ")";}
        return storage.toString();
    }


    public String printValue(){
        String fin = "";
        for(int i = 0; i < storage.size(); i++)
            fin += storage.get(String.valueOf(i)).toString();
        return fin;
    }

    public boolean containsVal(String s){
        if(storage.containsKey(s))
            return true;
        return false;
    }

    public boolean containsKey(String s){
        if(storage.containsKey(s))
            return true;
        return false;
    }
}
