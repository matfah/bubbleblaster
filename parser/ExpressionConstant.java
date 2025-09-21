package parser;

import java.util.TreeMap;

public class ExpressionConstant extends ExpressionObject {
   private double value;
   private char name;

   public ExpressionConstant(char n, double v) {
      this.name = n;
      this.value = v;
   }

   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      return this.value;
   }

   public String toString() {
      return this.name + "=" + this.value;
   }
}
