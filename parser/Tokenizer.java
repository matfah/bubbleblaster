package parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

public class Tokenizer {
   private static final int NUMBER_TYPE = 0;
   private static final int OPERATOR_TYPE = 1;
   private static final int FUNCTION_TYPE = 2;
   private static final int CLOSING_TYPE = 3;
   private static final int VARIABLE_TYPE = 4;
   private static final int CONSTANT_TYPE = 5;

   private static int precedenceFor(char op) {
      if (op == '-') {
         return 1;
      } else if (op == '+') {
         return 1;
      } else if (op == '/') {
         return 2;
      } else if (op == '*') {
         return 2;
      } else {
         return op == '^' ? 3 : -1;
      }
   }

   private static boolean leftComesBeforeRight(char opA, char opB) {
      return precedenceFor(opA) >= precedenceFor(opB);
   }

   private static boolean shouldMultiplyBetween(int last, int next) {
      return (last == 0 || last == 4 || last == 5 || last == 3) && (next == 2 || next == 0 || next == 4 || next == 5);
   }

   public static ExpressionTreeNode toTree(List<ExpressionObject> list) throws Exception {
      Stack<ExpressionOperator> stack = new Stack();
      LinkedList<ExpressionTreeNode> queue = new LinkedList();

      for(int i = 0; i < list.size(); ++i) {
         ExpressionObject obj = (ExpressionObject)list.get(i);
         if (!(obj instanceof ExpressionNumber) && !(obj instanceof ExpressionVariable) && !(obj instanceof ExpressionConstant)) {
            if (obj instanceof ExpressionOperator) {
               ExpressionOperator op = (ExpressionOperator)obj;
               if (stack.size() == 0) {
                  stack.push(op);
               } else {
                  while(stack.size() > 0 && leftComesBeforeRight(((ExpressionOperator)stack.peek()).getOperator(), op.getOperator())) {
                     ExpressionTreeNode second = (ExpressionTreeNode)queue.removeLast();
                     ExpressionTreeNode first = (ExpressionTreeNode)queue.removeLast();
                     queue.add(new ExpressionTreeNode(first, second, (ExpressionObject)stack.pop()));
                  }

                  stack.push(op);
               }
            } else if (obj instanceof ExpressionFunction) {
               int openCount = 1;

               int j;
               for(j = i + 1; openCount != 0 && j < list.size(); ++j) {
                  if (list.get(j) instanceof ExpressionClosure) {
                     --openCount;
                  } else if (list.get(j) instanceof ExpressionFunction) {
                     ++openCount;
                  }
               }

               if (openCount != 0) {
                  throw new Exception("No closing parenthesis found!");
               }

               List<ExpressionObject> subList = list.subList(i + 1, j - 1);
               ExpressionTreeNode result = toTree(subList);
               queue.add(new ExpressionTreeNode(result, new ExpressionTreeNode((ExpressionTreeNode)null, (ExpressionTreeNode)null, (ExpressionObject)list.get(j - 1)), obj));
               i = j - 1;
            }
         } else {
            queue.add(new ExpressionTreeNode((ExpressionTreeNode)null, (ExpressionTreeNode)null, obj));
         }
      }

      while(!stack.isEmpty()) {
         ExpressionTreeNode second = (ExpressionTreeNode)queue.removeLast();
         ExpressionTreeNode first = (ExpressionTreeNode)queue.removeLast();
         queue.add(new ExpressionTreeNode(first, second, (ExpressionObject)stack.pop()));
      }

      return (ExpressionTreeNode)queue.remove();
   }

   public static ArrayList<ExpressionObject> tokenize(String expression) throws Exception {
      expression = expression.replaceAll(" ", "");
      ArrayList<ExpressionObject> list = new ArrayList();
      int lastType = -1;
      if (expression.startsWith("-")) {
         expression = "0" + expression;
      }

      TreeSet<Integer> fixNegativeSymbolClosureFor = new TreeSet();
      int openCount = 0;

      for(int i = 0; i < expression.length(); ++i) {
         char c = expression.charAt(i);
         if (isNumberComponent(c)) {
            StringBuilder fullNumber = new StringBuilder();
            fullNumber.append(c);

            while(i + 1 < expression.length() && isNumberComponent(c = expression.charAt(i + 1))) {
               fullNumber.append(c);
               ++i;
            }

            double value = Double.parseDouble(fullNumber.toString());
            if (shouldMultiplyBetween(lastType, 0)) {
               list.add(new ExpressionOperator('*'));
            }

            list.add(new ExpressionNumber(value));

            for(lastType = 0; fixNegativeSymbolClosureFor.contains(openCount); --openCount) {
               list.add(new ExpressionClosure());
               fixNegativeSymbolClosureFor.remove(openCount);
               lastType = 3;
            }
         } else if (c == 'p' && i + 1 < expression.length() && expression.charAt(i + 1) == 'i') {
            if (shouldMultiplyBetween(lastType, 5)) {
               list.add(new ExpressionOperator('*'));
            }

            list.add(new ExpressionNumber(3.141592653589793D));
            lastType = 5;
            ++i;

            while(fixNegativeSymbolClosureFor.contains(openCount)) {
               list.add(new ExpressionClosure());
               fixNegativeSymbolClosureFor.remove(openCount);
               lastType = 3;
               --openCount;
            }
         } else if (c == 'e') {
            if (shouldMultiplyBetween(lastType, 5)) {
               list.add(new ExpressionOperator('*'));
            }

            list.add(new ExpressionNumber(2.718281828459045D));

            for(lastType = 5; fixNegativeSymbolClosureFor.contains(openCount); --openCount) {
               list.add(new ExpressionClosure());
               fixNegativeSymbolClosureFor.remove(openCount);
               lastType = 3;
            }
         } else if (c != '+' && c != '-' && c != '*' && c != '/' && c != '^') {
            if (c == ')') {
               --openCount;
               if (openCount < 0) {
                  throw new Exception("Incorrect Parenthesis usage!");
               }

               if (shouldMultiplyBetween(lastType, 3)) {
                  list.add(new ExpressionOperator('*'));
               }

               list.add(new ExpressionClosure());

               for(lastType = 3; fixNegativeSymbolClosureFor.contains(openCount); --openCount) {
                  list.add(new ExpressionClosure());
                  fixNegativeSymbolClosureFor.remove(openCount);
                  lastType = 3;
               }
            } else if (c != 'x' && c != 'y') {
               int j;
               for(j = i; j < expression.length() && expression.charAt(j) != '('; ++j) {
               }

               if (j == expression.length()) {
                  throw new Exception("Function Parenthesis Not Found!");
               }

               String func = expression.substring(i, j + 1);
               i = j;
               if (shouldMultiplyBetween(lastType, 2)) {
                  list.add(new ExpressionOperator('*'));
               }

               list.add(new ExpressionFunction(func));
               ++openCount;

               for(lastType = 2; fixNegativeSymbolClosureFor.contains(openCount); --openCount) {
                  list.add(new ExpressionClosure());
                  fixNegativeSymbolClosureFor.remove(openCount);
                  lastType = 3;
               }
            } else {
               if (shouldMultiplyBetween(lastType, 4)) {
                  list.add(new ExpressionOperator('*'));
               }

               list.add(new ExpressionVariable(String.valueOf(c)));

               for(lastType = 4; fixNegativeSymbolClosureFor.contains(openCount); --openCount) {
                  list.add(new ExpressionClosure());
                  fixNegativeSymbolClosureFor.remove(openCount);
                  lastType = 3;
               }
            }
         } else {
            if (shouldMultiplyBetween(lastType, 1)) {
               list.add(new ExpressionOperator('*'));
            } else if (c == '-' && lastType == 2) {
               list.add(new ExpressionNumber(0.0D));
            } else if (c == '-' && lastType == 1 && i + 1 < expression.length()) {
               ++openCount;
               list.add(new ExpressionFunction("("));
               list.add(new ExpressionNumber(-1.0D));
               list.add(new ExpressionOperator('*'));
               lastType = 1;
               fixNegativeSymbolClosureFor.add(openCount);
               continue;
            }

            list.add(new ExpressionOperator(c));
            lastType = 1;
         }
      }

      if (openCount != 0) {
         throw new Exception("Every open parenthesis is not followed by a closing parenthesis!");
      } else {
         return list;
      }
   }

   public static boolean isNumberComponent(char c) {
      return c >= '0' && c <= '9' || c == '.';
   }
}
