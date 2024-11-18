import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
public class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------  Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix) {
        List<String> stack = new ArrayList<>();
        Double temp;
        for (int i = 0; i < postfix.size(); i++){
            if ("+-*/^".contains(postfix.get(i))){
                if(stack.size() < 2){
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                temp = applyOperator(postfix.get(i), Double.valueOf(stack.get(0)), Double.valueOf(stack.get(1)));
                stack.set(0, String.valueOf(temp));
                stack.remove(1);
            }
            else {
                stack.add(0, postfix.get(i));
                
            }
        }
        Double result = Double.valueOf(stack.get(0));
        return result;
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) {
        List<String> postFix = new ArrayList<>();
        List<String> stack = new ArrayList<>();
        boolean equal = false;
        boolean lessThan = false;
        int j;
        for (int i = 0; i < infix.size(); i++){
            if ("+-*/^()".contains(infix.get(i))){
                if (")".contains(infix.get(i))){
                    for (int a = 0; a < stack.size(); a++){
                        if (stack.get(a) == "("){
                            postFix.add(stack.get(0));
                            stack.remove(0);
                            break;
                        }
                        postFix.add(stack.get(0));
                        stack.remove(0);
                    }
                }
                else {
                    if (stack.size() != 0){
                        for (j = 0; j < stack.size(); j++){
                            if(getPrecedenceImproved(stack.get(j)) < getPrecedenceImproved(infix.get(i)) || getPrecedenceImproved(infix.get(i)) == 4 || getPrecedenceImproved(infix.get(i)) == 0){
                                break;
                            }
                            if(getPrecedenceImproved(stack.get(j)) > getPrecedenceImproved(infix.get(i))){
                                lessThan = true;
                                break;
                            }
                            if (getPrecedenceImproved(stack.get(j)) == getPrecedenceImproved(infix.get(i))){
                                equal = true;
                                break;
                            }
                        }
                        if (equal || lessThan){
                            postFix.addAll(stack);
                            stack.clear();
                            stack.add(infix.get(i));
                        }
                        else {
                            stack.add(j, infix.get(i));
                        }
                    }
                    else {
                        stack.add(infix.get(i));
                    }
                }
            }
            else {
                postFix.add(infix.get(i));
            }
        }
        postFix.addAll(stack);
        while (postFix.contains("(") || postFix.contains(")")){
            postFix.remove("(");
            postFix.remove(")");
        }
        

        return postFix;
    }

    //if(tempListArguments.size() != 0 && getPrecedence(tempListArguments) == getPrecedence(infix.get(j))){
      //                      
    //}
    List<String> insertCorrect(List<String> list, String string){
        boolean temp = false;
        for(int i = 0; i < list.size(); i++){
            if(getPrecedence(list.get(i)) < getPrecedence(string)){
                list.add(i, string);
                temp = true;
            } 
            else if (list.size() != 0 && getPrecedence(list.get(i)) == getPrecedence(string)){
                List<String> emptyList = new ArrayList<>();
                return emptyList;
            }
        }
        if(!temp){
            list.add(string);
        }
        
        return list;
    }
    List<String> copyList(List<String> list1){
        List<String> temp = new ArrayList<>(); 
        for(String element : list1){
            temp.add(element);
        }
        return temp;
    }


   


    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    int getPrecedenceImproved(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else if ("()".contains(op)) {
            return 0;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------

    // List String (not char) because numbers (with many chars)
    List<String> tokenize(String expr) {
       // Done
        List<String> stringList = new ArrayList<>();
        String[] temp = expr.split("");
        int result = 0;
        int j = 0;

        for (int i = temp.length - 1; i >= 0; i--){
            if(!" ".equals(temp[i])){
                if("+-*/^()".contains(temp[i])) {
                    if (j > 0){
                        stringList.add(0, String.valueOf(result));
                        j = 0;
                        result = 0;
                    }
                    stringList.add(0, temp[i]);
                }
                else {
                    result += Integer.valueOf(temp[i]) * pow(10,j);
                    j++;
                }
            }
        }
        if(j > 0){
            stringList.add(0, String.valueOf(result));
        }
        return stringList;
    }
}
