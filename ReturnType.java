package icsi311;

public class ReturnType{

    public enum TypeOfReturn{
        NORMAL,
        BREAK,
        CONTINUE,
        RETURN,
    }

    private TypeOfReturn returnVal;
    private String Content;
    public ReturnType(TypeOfReturn ReturnType){
        returnVal = ReturnType;
    }

    public ReturnType(TypeOfReturn ReturnType, String inp){
        returnVal = ReturnType;
        Content = inp;
    }

    public String toString(){
        if(returnVal == TypeOfReturn.RETURN)
            return Content;
        else
            return returnVal.toString();
    }
    public ReturnType.TypeOfReturn getReturnType(){
        return returnVal;
    }
}
