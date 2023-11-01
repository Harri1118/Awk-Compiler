package icsi311;

public class AssignmentNode extends StatementNode{
    VariableReferenceNode Target;
    OperationNode Expression;

    public AssignmentNode(VariableReferenceNode t, OperationNode e){
        Target = t;
        Expression = e;
        // If Assignment, Target is a new VariableReferenceNode assigned to value from e
        if(e.getOperation() == OperationNode.PossibleOperations.ASSIGN){
        String s = t.getName();
        Target = new VariableReferenceNode(s, e.getRightValue());
        }
    }

    public AssignmentNode(Node t, OperationNode e){
        // If t is VariableReferenceNode, then set it to target
        if(t instanceof VariableReferenceNode)
            Target = (VariableReferenceNode) t;
        Expression = e;
    }

    public String toString(){
        if(Expression.getOperation() == OperationNode.PossibleOperations.ASSIGN)
            return Target.getName() + " = " + Expression.getRightValue().get();
        return Expression.toString();
    }
}
