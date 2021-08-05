import java.io.*;
import java.util.*;
//class for dealing with insertions
public class InsertCommand {

    public boolean insertCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser)
            throws IOException {
        //ensures next letter is into
        if (!commands.get(parser.counter).equals("INTO")){
            return false;
        }
        parser.counter++;
        //sets table name to insert into
        String tableName = commands.get(parser.counter);
        //ensures table exists
        if (!checkForTable(tableName, server)){
            return false;
        }
        parser.counter++;
        if (!commands.get(parser.counter).equals("VALUES")){
            return false;
        }
        parser.counter++;

        if(!valueAdder(commands, server, parser, tableName)){
            return false;
        }
        //writes it to file space
        server.writeDBToFile();

        return true;
    }
    //function to extend the main insert command
    private boolean valueAdder(ArrayList<String> commands, DBServer server, ParseAndInterpret parser, String tableName)
            throws IOException{

        //arraylist to store the values being inserted
        ArrayList<String> valuesToAdd = new ArrayList<>();
        //function to find values to insert and populate arraylist
        if(!valuesToInsert(commands,tableName, server,parser, valuesToAdd)){
            return false;
        }
        //calls table class
        TableModify toAdd = new TableModify();
        //returned reformatted list
        ArrayList<String> reformattedList = addToTable(tableName, server, valuesToAdd);
        //adds table to database to memory from table class
        toAdd.addTableToDB(tableName, server, reformattedList);

        return true;
    }

    //checks that table exists
    private boolean checkForTable(String name, DBServer server){
        //checks database set and that table is in database
        if (server.returnCurrentDBName().equals("DEFAULT")  || !server.doesTableExist(name)){
            return false;
        }

        return true;
    }

    private boolean valuesToInsert(ArrayList<String> commands, String tableName,
                                   DBServer server, ParseAndInterpret parser, ArrayList<String> valuesToAdd){

        //counter for number of columns adding to table
        int counterForAdds = 0;
        //ensures that ( is present
        if (!commands.get(parser.counter).equals("(")){
            return false;
        }
        parser.counter++;
        //keeps going until it encounters closing brackets
        while (!commands.get(parser.counter).equals(")")){
            //adds to it as soon as goes past (
            counterForAdds++;
            //checks that not over table
           if (!insertChecks(commands, tableName, server, parser, counterForAdds)){
               return false;
           }
           if (!stringLiteralCheck(commands, parser, valuesToAdd)){
               return false;
           }
           //if no comma and not ) after then false
           if(!commands.get(parser.counter).equals(",") && !commands.get(parser.counter).equals(")")){
               return false;
           }
           //increment counter as no need to add comma
           if (commands.get(parser.counter).equals(",")) {
               parser.counter++;
           }
        }
        parser.counter++;
        //ensures that closing ; present
        if(!commands.get(parser.counter).equals(";")){
            return false;
        }
        return true;
    }

    private boolean insertChecks(ArrayList<String> commands, String tableName, DBServer server, ParseAndInterpret parser,
                                 int counterForAdds){
        //ensures that counter is not over the size of table
        if (server.sizeOfTable(tableName)< counterForAdds){
            return false;
        }
        //ensures that no ; inside the ( )s
        if (commands.get(parser.counter).equals(";")){
            return false;
        }
        return true;
    }

    private boolean stringLiteralCheck(ArrayList<String> commands, ParseAndInterpret parser, ArrayList<String> valuesToAdd){

        //checks for string
        if(commands.get(parser.counter).equals("'")){
            parser.counter++;
            //ensures that next is closing for string literal
            //tokeniser has taken care of string literals
            if(!commands.get(parser.counter + 1).equals("'")){
                return false;
            }
        }
        //adds values to arraylist
        valuesToAdd.add(commands.get(parser.counter));
        parser.counter++;
        //increment counter as knows already present in previous check
        if(commands.get(parser.counter).equals("'")){
            parser.counter++;
        }
        return true;
    }
    //adds values to table
    private ArrayList<String> addToTable(String tableName, DBServer server, ArrayList<String> valuesToAdd){
        //number for ID which is being inserted
        int numberOfValues;
        //list to reformat with added values
        ArrayList<String> reformatedList = new ArrayList<>();
        //returns table from database
        LinkedHashMap<String, ArrayList<String>> tableFromDatabase = server.returnTable(tableName);
        //returns int for new ID number for row
        numberOfValues = findIDToAdd(tableFromDatabase);
        //adds 1 to last value for new ID
        Integer id = numberOfValues + 1;
        //converts it back to string to add to table
        reformatedList.add(id.toString());
        //iterates through valueToAdd list
        for (int i = 0; i < valuesToAdd.size(); i++){
            //generates string with id tab value
            String toAddList = id + "\t" + valuesToAdd.get(i);
            //adds reformatted list to list
            reformatedList.add(toAddList);
        }
       return reformatedList;
    }

    private int findIDToAdd(LinkedHashMap<String, ArrayList<String>> tableFromDatabase){

        int numberOfValues;
        //if ID column is null or empty
        if (tableFromDatabase.get("ID") == null || tableFromDatabase.get("ID").size() == 0){
            //sets the number of values to be 0
            numberOfValues = 0;
        }
        else {
            //returns arraylist containing the ID column
            ArrayList<String> getLastID = tableFromDatabase.get("ID");
            //returns the string (number) of last ID stored
            //better to do this way as can delete so can't rely on index number
            String lastID = getLastID.get(getLastID.size()-1);
            //converts to integer from string
            numberOfValues = Integer.parseInt(lastID);
        }
        return numberOfValues;
    }
}
