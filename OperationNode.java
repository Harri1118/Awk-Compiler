package icsi311;

import java.util.Optional;

public class OperationNode extends StatementNode {
    private Node Left;
    private Optional<Node> Right;

    private PossibleOperations Operation;



    public enum PossibleOperations{
        EQ,
        NE,
        LT,
        LE,
        GT,
        GE,
        AND,
        OR,
        NOT,
        MATCH,
        NOTMATCH,
        DOLLAR,
        PREINC,
        PREDEC,

        POSTINC,
        POSTDEC,
        UNARYPOS,
        UNARYNEG,
        IN,
        EXPONENT,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MODULO,
        CONCATENATION,
        REGEXP,
        TILDE,
        ASSIGN
    }



    public OperationNode(Node l, PossibleOperations o){
        Left = l;
        Operation = o;
        Right = Optional.empty();
    }

    public OperationNode(Node l, PossibleOperations o, Node r){
        Left = l;
        Operation = o;
        Right = Optional.of(r);
    }

    public String toString(){
        if(Operation == PossibleOperations.DOLLAR){
            String fin = Left.toString();
            if(fin.contains(".")){
                String[] change = fin.split("[.]");
                fin = change[0];
            }
            return "$" + fin;
        }
        if(!Right.isEmpty())
            return  Left.toString() + " " + Operation.toString() + " " + Right.get().toString();
        else
            return Left.toString() + "(" + Operation.toString() + ")";
    }

    public OperationNode.PossibleOperations getOperation(){
        return Operation;
    }
    public Optional<Node> getRightValue(){
        return Right;
    }
    public Node getLeftValue(){return Left;}

    public boolean isMathematic(){
        PossibleOperations op = getOperation();
        if(op == PossibleOperations.ADD || op == PossibleOperations.SUBTRACT || op == PossibleOperations.MULTIPLY || op == PossibleOperations.DIVIDE || op == PossibleOperations.EXPONENT || op == PossibleOperations.MODULO)
            return true;
        return false;
    }


    public boolean isPreIncremental(){
        if(getOperation() == PossibleOperations.PREDEC || getOperation() == PossibleOperations.PREINC)
            return true;
        return false;
    }

    public boolean isPostIncremental(){
        if(getOperation() == PossibleOperations.POSTDEC || getOperation() == PossibleOperations.POSTINC)
            return true;
        return false;
    }

    public boolean isBoolOperator(){
        PossibleOperations op = getOperation();
        if(op == PossibleOperations.AND || op == PossibleOperations.OR || op == PossibleOperations.NOT)
            return true;
        return false;
    }
    public boolean isCompOperater(){
        PossibleOperations op = getOperation();
        if(op == PossibleOperations.EQ || op == PossibleOperations.NE || op == PossibleOperations.LT || op == PossibleOperations.GT || op == PossibleOperations.GE || op == PossibleOperations.LE)
            return true;
        return false;
    }

    public Node VeryLeftNode(){
        Node fin;
        if(Left instanceof OperationNode){
            OperationNode left = (OperationNode) Left;
            fin = left.VeryLeftNode();
            return fin;
        }
        else
            return Left;
    }

}