package icsi311;

public class AssignmentNode extends StatementNode{
    Node Target;
    OperationNode Expression;

    public AssignmentNode(Node t, OperationNode e){
        // If t is VariableReferenceNode, then set it to target
        Target = t;
        Expression = e;
    }

    public String toString(){
        if(Target instanceof VariableReferenceNode) {
            VariableReferenceNode t = (VariableReferenceNode) Target;
            if (Expression.getOperation() == OperationNode.PossibleOperations.ASSIGN)
                return t.getName() + " = " + Expression.getRightValue().get();
        }
        else if(Target instanceof OperationNode){
            OperationNode opNode = (OperationNode) Target;
            String fin = opNode.getLeftValue().toString();
            if(fin.contains(".")){
                String[] change = fin.split(".");
                fin = change[0];
            }
            return "$" + opNode.getLeftValue().toString();
        }
        return Expression.toString();
    }

    public Node getTarget(){
        return Target;
    }

    public boolean isPost(){
        if(Expression.getOperation() == OperationNode.PossibleOperations.POSTDEC || Expression.getOperation() == OperationNode.PossibleOperations.POSTINC)
            return true;
        return false;
    }
}
