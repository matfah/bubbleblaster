package parser;

import java.util.TreeMap;

public class ExpressionVariable extends ExpressionObject {
   private String name;

   public ExpressionVariable(String n) {
      this.name = n;
   }

   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      return (Double)varValues.get(this.name);
   }

   public String toString() {
      return this.name;
   }
}
