package parser;

import java.util.TreeMap;

public abstract class ExpressionObject {
   public abstract double evaluate(ExpressionTreeNode var1, ExpressionTreeNode var2, TreeMap<String, Double> var3);
}
