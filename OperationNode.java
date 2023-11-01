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
}