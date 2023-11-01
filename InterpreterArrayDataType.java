package icsi311;

import java.util.HashMap;

public class InterpreterArrayDataType extends InterpreterDataType{
    private HashMap<String, InterpreterDataType> storage;
    public InterpreterArrayDataType(){
        storage = new HashMap<String, InterpreterDataType>();
    }

    public void put(String s){
        int size = storage.size();
        String key = String.valueOf(size);
        storage.put(key, new InterpreterDataType(s));
    }

    public void put(InterpreterDataType i){

    }
    public InterpreterDataType get(String s){
        return storage.get(s);
    }

    public String toString(){
        String fin = "(";
        for(int i = 0; i < storage.size(); i++){
            fin += storage.get(String.valueOf(i)).toString() + ",";
        }
        fin = fin.substring(0,fin.length()-1);
        return fin + ")";
    }

    public String printValue(){
        String fin = "";
        for(int i = 0; i < storage.size(); i++)
            fin += storage.get(String.valueOf(i)).toString();
        return fin;
    }

}
