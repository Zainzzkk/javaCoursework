import java.util.*;
//class for checking if command is operator
public class IsOperator {

    String[] operators = {"!", "=", "<", ">", "*", "'", "(", ")", ","};
    //list of operators which should not be included apart from comma
    String[] operatorsNoComma = {"!", "=", "<", ">", "*", "'"};
    //operators for condition class
    String[] operatorConditions = {"!=", "==", "<", ">", "<=", ">=", "LIKE"};
    //string operators
    String[] stringOperators = {"==", "LIKE", "!="};
    String[] andOrOperator = {"AND", "OR"};
    //for operators in attribute list (only comma is accepted)
    public boolean ifIsOperatorAttributeList(String command){

        if (Arrays.asList(operatorsNoComma).contains(command)){
            return true;
        }
        return false;
    }
    //for conditions
    public boolean ifIsOperatorForCondition(String command){
        if (Arrays.asList(operatorConditions).contains(command)){
            return true;
        }
        return false;
    }
    //if string
    public boolean ifStringOperator(String command){
        if (Arrays.asList(stringOperators).contains(command)){
            return true;
        }
        return false;
    }
    //for and and or operator
    public boolean ifAndOrOperator(String command){

        if (Arrays.asList(andOrOperator).contains(command)){
            return true;
        }
        return false;
    }
}
