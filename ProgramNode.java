package icsi311;

import java.util.LinkedList;

public class ProgramNode {
   private LinkedList<BlockNode> BEGIN = new LinkedList<BlockNode>();
   private LinkedList<BlockNode> END = new LinkedList<BlockNode>();
   private LinkedList<FunctionDefinitionNode> functions = new LinkedList<FunctionDefinitionNode>();

   private LinkedList<BlockNode> OTHER = new LinkedList<BlockNode>();

   public void addBegin(BlockNode b) throws Exception{
      if(!functions.isEmpty())
         throw new Exception("Not a valid program structure! Must define a function after a BEGIN Block!");
      BEGIN.add(b);
   }

   public void addEnd(BlockNode b){
      END.add(b);
   }

   public void addFunction(FunctionDefinitionNode f){
      functions.add(f);
   }

   public void addOther(BlockNode b){
      OTHER.add(b);
   }

   public boolean beginIsEmpty(){
      if(BEGIN.isEmpty())
         return true;
      return false;
   }

   public boolean functionsIsEmpty(){
      if(functions.isEmpty())
         return true;
      return false;
   }
   public String toString(){
      try{
      return "BEGIN{" + betterParams(BEGIN.toString()) + "}\n" + betterParams(OTHER.toString()) + "\nEND{" + betterParams(END.toString()) + "}\nFunctions:\n" + betterParams(functions.toString());}
      catch(Exception e){
         return "toString not valid";
      }
   }
   public LinkedList<FunctionDefinitionNode> getFuncs(){
      return functions;
   }

   public String betterParams(String s){
      return s.substring(1,s.length()-1);
   }

   public LinkedList<BlockNode> getBegin(){
      return BEGIN;
   }

   public LinkedList<BlockNode> getEnd(){
      return END;
   }

   public LinkedList<BlockNode> getOther(){
      return OTHER;
   }
}
