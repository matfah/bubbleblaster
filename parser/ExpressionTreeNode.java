package parser;

import java.util.TreeMap;

public class ExpressionTreeNode {
   private ExpressionTreeNode left;
   private ExpressionTreeNode right;
   private ExpressionObject obj;

   public ExpressionTreeNode(ExpressionTreeNode l, ExpressionTreeNode r, ExpressionObject o) {
      this.left = l;
      this.right = r;
      this.obj = o;
   }

   public ExpressionTreeNode getLeft() {
      return this.left;
   }

   public void setLeft(ExpressionTreeNode left) {
      this.left = left;
   }

   public ExpressionTreeNode getRight() {
      return this.right;
   }

   public void setRight(ExpressionTreeNode right) {
      this.right = right;
   }

   public ExpressionObject getObj() {
      return this.obj;
   }

   public void setObj(ExpressionObject obj) {
      this.obj = obj;
   }

   public double evaluate(TreeMap<String, Double> map) {
      return this.obj.evaluate(this.left, this.right, map);
   }

   public String toString() {
      return this.obj.toString() + "\nLeft: " + this.left + "\nRight:" + this.right;
   }
}
