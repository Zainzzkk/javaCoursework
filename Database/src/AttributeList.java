import java.util.*;
//class to deal with an attribute list
public class AttributeList {

    public boolean attributeList(ArrayList<String> commands, ArrayList<String> columnTitles, ParseAndInterpret parser) {

        IsOperator operatorCheck = new IsOperator();
        //checks that starts with opening (
        if (!commands.get(parser.counter).equals("(")) {
            return false;
        }
        parser.counter++;
        //loops until hits a )
        while (!commands.get(parser.counter).equals(")")) {
            //if there is an operator other than , then will return false
            if (commands.get(parser.counter).equals(";") || operatorCheck.ifIsOperatorAttributeList(commands.get(parser.counter))) {
                return false;
            }
            if (!commands.get(parser.counter).equals(",")) {
                //ensures comma attribute as long as not ) after
                if (!commands.get(parser.counter + 1).equals(",") && !commands.get(parser.counter + 1).equals(")")) {
                    return false;
                }
                //title of columns added to arraylist in order
                columnTitles.add(commands.get(parser.counter));
            }
            //makes sure there is no , before a )
            if (commands.get(parser.counter).equals(",")) {
                if (commands.get(parser.counter + 1).equals(")")) {
                    return false;
                }
            }
            parser.counter++;
        }
        return true;
    }

    public boolean selectAttributeList(ArrayList<String> commands, ArrayList<String> columnTitles, ParseAndInterpret parser) {

        if (commands.get(parser.counter).equals("*")) {
            //ensures nothing but from after a *
            if (!commands.get(parser.counter + 1).equals("FROM")) {
                return false;
            }
            columnTitles.add(commands.get(parser.counter));
            parser.counter++;
            return true;
        } else {
            //will go into a list of column titles
           if (!whileForSelect(commands, columnTitles, parser)){
               return false;
           }
        }
        return true;
    }

    private boolean whileForSelect(ArrayList<String> commands, ArrayList<String> columnTitles, ParseAndInterpret parser){

        IsOperator operatorCheck = new IsOperator();
        //loops until hits from
        while (!commands.get(parser.counter).equals("FROM")) {
            //ensures no ; or anything but , in list
            if (commands.get(parser.counter).equals(";") || operatorCheck.ifIsOperatorAttributeList(commands.get(parser.counter))) {
                return false;
            }
            if (!commands.get(parser.counter).equals(",")) {
                if (!commands.get(parser.counter + 1).equals(",") && !commands.get(parser.counter + 1).equals("FROM")) {
                    return false;
                }
                columnTitles.add(commands.get(parser.counter));
            }
            if (commands.get(parser.counter).equals(",")) {
                if (commands.get(parser.counter + 1).equals("FROM")) {
                    return false;
                }
            }
            parser.counter++;
        }
        return true;
    }
}
