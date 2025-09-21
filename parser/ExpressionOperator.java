package parser;

import java.util.TreeMap;

public class ExpressionOperator extends ExpressionObject {
   private char operator;

   public ExpressionOperator(char o) throws Exception {
      this.operator = o;
      if (this.operator != '+' && this.operator != '-' && this.operator != '*' && this.operator != '/' && this.operator != '^') {
         throw new Exception("Unknown operator type");
      }
   }

   public char getOperator() {
      return this.operator;
   }

   public double evaluate(ExpressionTreeNode left, ExpressionTreeNode right, TreeMap<String, Double> varValues) {
      double leftValue = left.getObj().evaluate(left.getLeft(), left.getRight(), varValues);
      double rightValue = right.getObj().evaluate(right.getLeft(), right.getRight(), varValues);
      if (this.operator == '+') {
         return leftValue + rightValue;
      } else if (this.operator == '-') {
         return leftValue - rightValue;
      } else if (this.operator == '*') {
         return leftValue * rightValue;
      } else if (this.operator == '/') {
         return leftValue / rightValue;
      } else if (this.operator != '^') {
         return 0.0D;
      } else {
         if (leftValue < 0.0D) {
            for(int denom = 3; denom < 100; denom += 2) {
               double numer = rightValue * (double)denom;
               double leftOver = numer - (double)((int)numer);
               if (Math.abs(leftOver) < 1.0E-7D) {
                  double result = Math.pow(Math.abs(leftValue), rightValue);
                  if ((int)Math.round(numer) % 2 == 0) {
                     return result;
                  }

                  return -result;
               }
            }
         }

         return Math.pow(leftValue, rightValue);
      }
   }

   public String toString() {
      return "" + this.operator;
   }
}
