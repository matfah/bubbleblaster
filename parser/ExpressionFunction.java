package parser;

import java.util.TreeMap;

public class ExpressionFunction extends ExpressionObject {
   private String function;

   public ExpressionFunction(String func) throws Exception {
      this.function = func;
      if (!this.function.equals("sin(") && !this.function.equals("cos(") && !this.function.equals("tan(") && !this.function.equals("asin(") && !this.function.equals("acos(") && !this.function.equals("atan(") && !this.function.equals("sec(") && !this.function.equals("csc(") && !this.function.equals("cot(") && !this.function.equals("log(") && !this.function.equals("ln(") && !this.function.equals("(") && !this.function.equals("abs(") && !this.function.equals("sqrt(")) {
         throw new Exception("Unknow function");
      }
   }

   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      if (this.function.equals("(")) {
         return left.getObj().evaluate(left.getLeft(), left.getRight(), varValues);
      } else {
         double leftValue = left.getObj().evaluate(left.getLeft(), left.getRight(), varValues);
         if (this.function.equals("sin(")) {
            return Math.sin(leftValue);
         } else if (this.function.equals("cos(")) {
            return Math.cos(leftValue);
         } else if (this.function.equals("tan(")) {
            return Math.tan(leftValue);
         } else if (this.function.equals("asin(")) {
            return Math.asin(leftValue);
         } else if (this.function.equals("acos(")) {
            return Math.acos(leftValue);
         } else if (this.function.equals("atan(")) {
            return Math.atan(leftValue);
         } else if (this.function.equals("sec(")) {
            return 1.0D / Math.cos(leftValue);
         } else if (this.function.equals("csc(")) {
            return 1.0D / Math.sin(leftValue);
         } else if (this.function.equals("cot(")) {
            return 1.0D / Math.tan(leftValue);
         } else if (this.function.equals("log(")) {
            return Math.log10(leftValue);
         } else if (this.function.equals("ln(")) {
            return Math.log(leftValue);
         } else if (this.function.equals("abs(")) {
            return Math.abs(leftValue);
         } else {
            return this.function.equals("sqrt(") ? Math.sqrt(leftValue) : 0.0D;
         }
      }
   }

   public String toString() {
      return this.function;
   }
}
