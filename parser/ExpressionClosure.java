package parser;

import java.util.TreeMap;

public class ExpressionClosure extends ExpressionObject {
   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      return 0.0D;
   }

   public String toString() {
      return ")";
   }
}
