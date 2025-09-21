import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import parser.ExpressionObject;
import parser.ExpressionTreeNode;
import parser.Tokenizer;

public class Relation {
   private String leftSide;
   private String rightSide;
   private ExpressionTreeNode left;
   private ExpressionTreeNode right;
   private String comparator;
   private Color color;

   public void setColor(Color c) {
      this.color = c;
   }

   public Color getColor() {
      return this.color;
   }

   public Relation(String e) throws Exception {
      int index;
      if ((index = e.indexOf("<=")) != -1) {
         this.comparator = "<=";
      } else if ((index = e.indexOf(">=")) != -1) {
         this.comparator = ">=";
      } else if ((index = e.indexOf("<")) != -1) {
         this.comparator = "<";
      } else if ((index = e.indexOf(">")) != -1) {
         this.comparator = ">";
      } else {
         if ((index = e.indexOf("=")) == -1) {
            throw new Exception("Relation has no sides!");
         }

         this.comparator = "=";
      }

      this.leftSide = e.substring(0, index).trim();
      this.rightSide = e.substring(index + this.comparator.length()).trim();
      ArrayList<ExpressionObject> list = Tokenizer.tokenize(this.leftSide);
      this.left = Tokenizer.toTree(list);
      list = Tokenizer.tokenize(this.rightSide);
      this.right = Tokenizer.toTree(list);
   }

   public boolean isInequality() {
      return !this.comparator.equals("=");
   }

   public boolean isEqualToInequality() {
      return this.comparator.equals("<=") || this.comparator.equals(">=");
   }

   public String getComparator() {
      return this.comparator;
   }

   public boolean isFunctionalFor(char var) {
      if (!this.comparator.equals("=")) {
         return false;
      } else {
         return this.leftSide.equals(String.valueOf(var)) && this.rightSide.indexOf(var) == -1 || this.rightSide.equals(String.valueOf(var)) && this.leftSide.indexOf(var) == -1;
      }
   }

   public boolean isSimpleHorizontalOrVerticalLine() {
      if (!this.comparator.equals("=")) {
         return false;
      } else {
         return (this.leftSide.equals("x") || this.leftSide.equals("y")) && this.rightSide.indexOf("x") == -1 && this.rightSide.indexOf("y") == -1 || (this.rightSide.equals("x") || this.rightSide.equals("y")) && this.leftSide.indexOf("x") == -1 && this.leftSide.indexOf("y") == -1;
      }
   }

   public boolean isSimpleHorizontalLine() {
      if (!this.comparator.equals("=")) {
         return false;
      } else {
         return this.leftSide.equals("y") && this.rightSide.indexOf("x") == -1 && this.rightSide.indexOf("y") == -1 || this.rightSide.equals("y") && this.leftSide.indexOf("x") == -1 && this.leftSide.indexOf("y") == -1;
      }
   }

   public boolean isSimpleVerticalLine() {
      if (!this.comparator.equals("=")) {
         return false;
      } else {
         return this.leftSide.equals("x") && this.rightSide.indexOf("x") == -1 && this.rightSide.indexOf("y") == -1 || this.rightSide.equals("x") && this.leftSide.indexOf("x") == -1 && this.leftSide.indexOf("y") == -1;
      }
   }

   public double simpleHorizontalOrVerticalIntercept() {
      TreeMap<String, Double> map = new TreeMap();
      return !this.leftSide.equals("x") && !this.leftSide.equals("y") ? this.left.getObj().evaluate(this.left.getLeft(), this.left.getRight(), map) : this.right.getObj().evaluate(this.right.getLeft(), this.right.getRight(), map);
   }

   public double evaluateFunction(char output, char input, double inputValue) {
      TreeMap<String, Double> map = new TreeMap();
      map.put(String.valueOf(input), inputValue);
      return this.leftSide.equals(String.valueOf(output)) ? this.right.getObj().evaluate(this.right.getLeft(), this.right.getRight(), map) : this.left.getObj().evaluate(this.left.getLeft(), this.left.getRight(), map);
   }

   public TreeSet<Point2D> findSolutionsWithGiven(char var, double value) {
      double[] vals = new double[2];
      char[] vars = new char[]{'x', 'y'};
      if (var == 'x') {
         vals[0] = value;
      } else {
         vals[1] = value;
      }

      TreeSet<Point2D> inter = new TreeSet();

      for(double start = -10.0D; start <= 10.0D; start += 0.001D) {
         if (var == 'x') {
            vals[1] = start;
         } else {
            vals[0] = start;
         }

         double sideDiff = this.sideDifference(vars, vals);
         if (Math.abs(sideDiff) < 0.005D) {
            if (var == 'x') {
               inter.add(new Point2D(value, start));
            } else {
               inter.add(new Point2D(start, value));
            }
         }
      }

      return inter;
   }

   public boolean equals(Relation other) {
      double[] vals = new double[2];
      char[] vars = new char[]{'x', 'y'};
      double xIncr = 20.0D / (double)AbstractGraph.GLOBAL_DIVISIONS;
      double yIncr = 20.0D / (double)AbstractGraph.GLOBAL_DIVISIONS;
      double MAX_SIDE_DIFF = 20.0D;
      int wrongCount = 0;

      double y;
      double lastDiff;
      double lastOtherDiff;
      double x;
      double diff;
      double otherDiff;
      boolean shouldAddMe;
      boolean shouldAddYou;
      for(y = -10.0D; y <= 10.0D; y += xIncr) {
         vals[0] = y;
         vals[1] = -10.0D;
         lastDiff = this.sideDifference(vars, vals);
         lastOtherDiff = other.sideDifference(vars, vals);

         for(x = -10.0D + yIncr; x <= 10.0D; x += yIncr) {
            vals[0] = y;
            vals[1] = x;
            diff = this.sideDifference(vars, vals);
            otherDiff = other.sideDifference(vars, vals);
            shouldAddMe = false;
            if (Math.abs(diff) < yIncr / 2.0D) {
               shouldAddMe = true;
            } else if (lastDiff <= 0.0D && diff >= 0.0D || lastDiff >= 0.0D && diff <= 0.0D) {
               shouldAddMe = true;
               if (Math.abs(lastDiff) > MAX_SIDE_DIFF || Math.abs(diff) > MAX_SIDE_DIFF) {
                  shouldAddMe = false;
               }
            }

            shouldAddYou = false;
            if (Math.abs(otherDiff) < yIncr / 2.0D) {
               shouldAddYou = true;
            } else if (lastOtherDiff <= 0.0D && otherDiff >= 0.0D || lastOtherDiff >= 0.0D && otherDiff <= 0.0D) {
               shouldAddYou = true;
               if (Math.abs(lastOtherDiff) > MAX_SIDE_DIFF || Math.abs(otherDiff) > MAX_SIDE_DIFF) {
                  shouldAddYou = false;
               }
            }

            if (shouldAddMe != shouldAddYou) {
               ++wrongCount;
            }

            lastDiff = diff;
            lastOtherDiff = otherDiff;
         }
      }

      for(y = -10.0D; y <= 10.0D; y += yIncr) {
         vals[0] = -10.0D;
         vals[1] = y;
         lastDiff = this.sideDifference(vars, vals);
         lastOtherDiff = other.sideDifference(vars, vals);

         for(x = -10.0D + xIncr; x <= 10.0D; x += xIncr) {
            vals[0] = x;
            vals[1] = y;
            diff = this.sideDifference(vars, vals);
            otherDiff = other.sideDifference(vars, vals);
            shouldAddMe = false;
            if (Math.abs(diff) < xIncr / 2.0D) {
               shouldAddMe = true;
            } else if (lastDiff <= 0.0D && diff >= 0.0D || lastDiff >= 0.0D && diff <= 0.0D) {
               shouldAddMe = true;
               if (Math.abs(lastDiff) > MAX_SIDE_DIFF || Math.abs(diff) > MAX_SIDE_DIFF) {
                  shouldAddMe = false;
               }
            }

            shouldAddYou = false;
            if (Math.abs(otherDiff) < xIncr / 2.0D) {
               shouldAddYou = true;
            } else if (lastOtherDiff <= 0.0D && otherDiff >= 0.0D || lastOtherDiff >= 0.0D && otherDiff <= 0.0D) {
               shouldAddYou = true;
               if (Math.abs(lastOtherDiff) > MAX_SIDE_DIFF || Math.abs(otherDiff) > MAX_SIDE_DIFF) {
                  shouldAddYou = false;
               }
            }

            if (shouldAddMe != shouldAddYou) {
               ++wrongCount;
            }

            lastDiff = diff;
            lastOtherDiff = otherDiff;
         }
      }

      if (wrongCount < 20) {
         return true;
      } else {
         return false;
      }
   }

   public double sideDifference(char[] vars, double[] vals) {
      TreeMap<String, Double> map = new TreeMap();

      for(int i = 0; i < vars.length; ++i) {
         map.put(String.valueOf(vars[i]), vals[i]);
      }

      double leftValue = this.left.getObj().evaluate(this.left.getLeft(), this.left.getRight(), map);
      double rightValue = this.right.getObj().evaluate(this.right.getLeft(), this.right.getRight(), map);
      return leftValue - rightValue;
   }

   public String toString() {
      return this.leftSide + this.comparator + this.rightSide;
   }
}
