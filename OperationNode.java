package icsi311;

import java.util.Optional;

public class OperationNode extends Node {
    private Node Left;
    private Optional<Node> Right;

    public enum PossibleOperaions{
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
        POSTINC,
        PREDEC,
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
        CONCATENATION

    }

    public OperationNode(Node l, Token.TokenType t, Optional<Node> r){
        Left = l;
        Right = r;
    }

    public OperationNode(Node l, PossibleOperaions t){
        Left = l;
    }
    public String toString(){
        return Left.toString() + " Operation goes here? " + Right;
    }
}
