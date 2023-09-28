package icsi311;

import java.util.LinkedList;

public class ProgramNode {
   public LinkedList<BlockNode> BEGIN = new LinkedList<BlockNode>();
   public LinkedList<BlockNode> END = new LinkedList<BlockNode>();
   public LinkedList<FunctionDefinitionNode> functions = new LinkedList<FunctionDefinitionNode>();

   public LinkedList<BlockNode> OTHER = new LinkedList<BlockNode>();
   public String toString(){
      try{
      return "BEGIN{" + BEGIN.toString() + "}\nOTHER{" + OTHER.toString() + "}\nEND{" + END.toString() + "}\nFunctions:\n" + functions.toString();}
      catch(Exception e){
         return "toString not valid";
      }
   }


}
