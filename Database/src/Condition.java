import java.util.*;
//checks for condition
public class Condition {
    //for the original table
    private LinkedHashMap<String, ArrayList<String>> tableToCheck;
    //final list of matching
    private ArrayList<String> finalList;
    //left side of comparison
    private ArrayList<String> compareOne;
    //right side of comparison
    private ArrayList<String> compareTwo;
    //keeping track of how many brackets
    public int bracketCounter = 0;

    public Condition(){
        tableToCheck = new LinkedHashMap<>();
        finalList = new ArrayList<>();
        compareOne = new ArrayList<>();
        compareTwo = new ArrayList<>();
    }

    public boolean conditionToCheck(ArrayList<String> commands, DBServer server, ParseAndInterpret parser,
                                    ArrayList<String> id, String tableName){
        //if first char is ; then false
        if (commands.get(parser.counter).equals(";")){
            return false;
        }
        //table name fed in so imports
        tableToCheck = server.returnTable(tableName);
        //if bracket then knows that has to check left and right
        if (commands.get(parser.counter).equals("(")){
            parser.counter++;
            //adds one to bracket counter
            bracketCounter++;
            //checking between brackets
            if(!conditionWithBrackets(commands, parser, id)){
                return false;
            }
            parser.counter++;
            //ensures closes with ;
            if (!commands.get(parser.counter).equals(";")){
                return false;
            }
            return true;
        }
        else {
            //if not ( then you know is only 1 condition
            if (!conditionsWithoutBrackets(commands, parser, id)){
                return false;
            }
            parser.counter++;
            if (!commands.get(parser.counter).equals(";")){
                return false;
            }
        }
        return true;
    }
    //for if brackets present only
    private boolean conditionWithBrackets(ArrayList<String> commands, ParseAndInterpret parser, ArrayList<String> id){
        //checks between the brackets
        if (!brackets(commands, parser, compareOne)){
            return false;
        }
        parser.counter++;
        IsOperator operator = new IsOperator();
        //puts in the operator
        String comparitor = commands.get(parser.counter);
        //checks if it is valid operator
        if(!operator.ifAndOrOperator(comparitor)){
            return false;
        }
        parser.counter++;
        //ensures that opens next statement with brackets
        if (!commands.get(parser.counter).equals("(")){
            return false;
        }
        //adds 1 to counter since open
        bracketCounter++;
        parser.counter++;
        if (!brackets(commands, parser, compareTwo)){
            return false;
        }
        //compares between left and right
        compareConditions(compareOne, compareTwo,comparitor,id);

        return true;

    }

    private boolean brackets(ArrayList<String> commands, ParseAndInterpret parser, ArrayList<String> id){
        ArrayList<String> IDOne = new ArrayList<>();
        ArrayList<String> IDTwo = new ArrayList<>();
        IsOperator operator = new IsOperator();
        //if next char isn't a bracket so not nested
        if (!commands.get(parser.counter).equals("(")){
            //can use function for not within brackets - adds to ID
            if (!withinBrackets(commands, parser, id)){
                return false;
            }
            //bracket counter should be 0 or false
            if (bracketCounter != 0){
                return false;
            }

            return true;

        } else {
            //nested brackets so same logic
            bracketCounter += 1;
            parser.counter++;
            //checks left first
            if (!withinBrackets(commands, parser, IDOne)){
                return false;
            }
            parser.counter++;
            //makes sure that there is operator
            String comparitor = commands.get(parser.counter);
            if(!operator.ifAndOrOperator(comparitor)){
                return false;
            }
            parser.counter++;
            if (!commands.get(parser.counter).equals("(")){
                return false;
            }
            parser.counter++;
            if (!withinBrackets(commands, parser, IDTwo)){
                return false;
            }//compares right and left of same side
            compareConditions(IDOne, IDTwo, comparitor, id);
            //ensures counter is 0
            if (bracketCounter == 0){
                parser.counter++;
                return true;
            }
        }

        return false;
    }

    //checks within the brackets
    private boolean withinBrackets (ArrayList<String> commands, ParseAndInterpret parser, ArrayList<String> id){
            //checks for between the bracket
            if (!conditionsWithoutBrackets(commands, parser, id)){
                return false;
            }
            parser.counter++;
            //if not closing then false
            if (!commands.get(parser.counter).equals(")")){
                return false;
            }
            //since ) then reduce bracket counter
            bracketCounter = bracketCounter - 1;
            return true;
    }

    //compares the and or conditions
    private void compareConditions(ArrayList<String> intermediateIDOne, ArrayList<String> intermediateIDTwo,
                                      String comparitor, ArrayList<String> id){

        if (comparitor.equals("OR")){
            //makes intermediate copy inside
            ArrayList<String> intermediateTwoCopy = new ArrayList<>(intermediateIDTwo);
            //removes all that are not in the intermediate 1 (left side)
            intermediateTwoCopy.removeAll(intermediateIDOne);
            //copies these
            intermediateIDOne.addAll(intermediateTwoCopy);
            //adds all
            id.addAll(intermediateIDOne);
        }

        if (comparitor.equals("AND")){
            for (int i = 0; i < intermediateIDOne.size(); i++){
                //if it is in both then it adds them to intermediate 1
                if(intermediateIDTwo.contains(intermediateIDOne.get(i))){
                    id.add(intermediateIDOne.get(i));
                }
            }

        }

    }
    //checks between brackets or if none present (no and or or)
    private boolean conditionsWithoutBrackets(ArrayList<String> commands, ParseAndInterpret parser,
                                              ArrayList<String> id){
        //key of column
        String key = commands.get(parser.counter);
        parser.counter++;
        //checks if approved operator
        IsOperator operatorCheck = new IsOperator();
        if(!operatorCheck.ifIsOperatorForCondition(commands.get(parser.counter))){
            return false;
        }
        String operator = commands.get(parser.counter);
        parser.counter++;
        String value;
        //checks if it is string literal
        if (commands.get(parser.counter).equals("'")){
            parser.counter++;
            //adds it (already done by tokeniser so just has to add)
            value = commands.get(parser.counter);
            parser.counter++;
            //makes sure closes
            if (!commands.get(parser.counter).equals("'")){
                return false;
            }
            //matches to table to return ids
            if (!conditionStringMatcher(id, key, value, operator)){
                return false;
            }
        }
        else {
            value = commands.get(parser.counter);
            //if boolean literal
            if (value.equals("TRUE") || value.equals("FALSE")){
                //checks in table to return ids
                if (!conditionStringMatcher(id, key, value, operator)){
                    return false;
                }
            } else {
                //else convert to float and check for number
                if (!conditionFloatMatcher(id, key, value, operator)) {
                    return false;
                }
            }
        }

        return true;
    }
    //only for checking matching ids from strings
    private boolean conditionStringMatcher(ArrayList<String> id, String key, String value, String operator){

        IsOperator operatorCheck = new IsOperator();
        //ensures operator is correct operator for string
        if (!operatorCheck.ifStringOperator(operator)){
            return false;
        }
        //returns column from table
        ArrayList<String> column = tableToCheck.get(key);
        //stops crashing if empty
        if (column == null){
            return false;
        }
        if (column.size() == 0){
            return false;
        }
        //checks based on operator
        operationCheck(id, column, operator, value);

        return true;
    }

    private void operationCheck(ArrayList<String> id, ArrayList<String> column, String operator, String value){
        //iterates through operator
        for (int i = 0; i < column.size(); i++) {
            //splits the column on tab (since column is ID tab value
            String[] contents = column.get(i).split("\t");
            String columnId = contents[0];
            String columnValue = contents[1];
            //calls for each operator
            if (operator.equals("==")) {
                conditionStringEqual(id, columnId, columnValue, value);
            }

            if (operator.equals("LIKE")) {
                conditionStringLike(id, columnId, columnValue, value);
            }

            if (operator.equals("!=")) {
                conditionStringNotEq(id, columnId, columnValue, value);
            }
        }
    }

    private void conditionStringEqual(ArrayList<String> id, String columnId, String columnValue, String value){
        //if both the same then adds the ID
        if (columnValue.equals(value)){
            id.add(columnId);
        }
    }

    private void conditionStringLike(ArrayList<String> id, String columnId, String columnValue, String value){
        //if the string is contained within a cell then adds value
        if (columnValue.contains(value)){
            id.add(columnId);
        }
    }

    private void conditionStringNotEq(ArrayList<String> id, String columnId, String columnValue, String value){
        //if != then adds
        if (!columnValue.equals(value)){
            id.add(columnId);
        }
    }

    private boolean conditionFloatMatcher(ArrayList<String> id, String key, String value, String operator){

        ArrayList<String> column = tableToCheck.get(key);

        if (column == null){
            return false;
        }
        if (column.size() == 0){
            return false;
        }
        //convert string to float
        //try catch block to ensure that no string is converted
        try {
            Float valueConverted = Float.parseFloat(value);
            floatOperationCheck(id, column, operator, valueConverted);
        }catch (NumberFormatException e){
            System.err.println("not a number");
        }

        return true;
    }
    //function for checking matching for floats
    private void floatOperationCheck(ArrayList<String> id, ArrayList<String> column, String operator, Float value){

        for (int i = 0; i < column.size(); i++) {
            //again splits as first is id then tab and then value
            String[] contents = column.get(i).split("\t");
            String columnId = contents[0];
            String columnValue = contents[1];
            //try catch block to ensure that no string is converted
            try {
                Float columnValueConverted = Float.parseFloat(columnValue);
                if (operator.equals(">")){
                    conditionFloatGreater(id, columnId, value, columnValueConverted);
                }
                if (operator.equals(">=")){
                    conditionFloatGreaterEqual(id, columnId, value, columnValueConverted);
                }
                if (operator.equals("<")){
                    conditionFloatLess(id, columnId, value, columnValueConverted);
                }
                if (operator.equals("<=")){
                    conditionFloatLessEqual(id, columnId, value, columnValueConverted);
                }
                if (operator.equals("!=")){
                    conditionFloatNotEqual(id, columnId, value, columnValueConverted);
                }
                if (operator.equals("==")){
                    conditionFloatEqual(id, columnId, value, columnValueConverted);
                }
            }catch (NumberFormatException e){
                System.err.println("not a number");
            }
        }
    }

    private void conditionFloatGreater(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (valueConverted > value){
            id.add(columnId);
        }
    }

    private void conditionFloatGreaterEqual(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (valueConverted >= value){
            id.add(columnId);
        }
    }

    private void conditionFloatLess(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (valueConverted < value){
            id.add(columnId);
        }
    }

    private void conditionFloatLessEqual(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (valueConverted <= value){
            id.add(columnId);
        }
    }

    private void conditionFloatNotEqual(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (!valueConverted.equals(value)){
            id.add(columnId);
        }
    }

    private void conditionFloatEqual(ArrayList<String> id, String columnId, Float value, Float valueConverted){

        if (valueConverted.equals(value)){
            id.add(columnId);
        }
    }


}
