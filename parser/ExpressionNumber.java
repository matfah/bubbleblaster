package parser;

import java.util.TreeMap;

public class ExpressionNumber extends ExpressionObject {
   private double value;

   public ExpressionNumber(double v) {
      this.value = v;
   }

   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      return this.value;
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
