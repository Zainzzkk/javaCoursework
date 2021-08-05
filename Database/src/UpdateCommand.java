import java.util.*;
//class for update command
public class UpdateCommand {

    public boolean updateCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
        //arraylist of ID to update
        ArrayList<String> idToUpdate = new ArrayList<>();

        String tableName = commands.get(parser.counter);
        //check database is set and table exists
        if (server.returnCurrentDBName().equals("DEFAULT") || !server.doesTableExist(commands.get(parser.counter))){
            return false;
        }
        parser.counter++;

        if (!commands.get(parser.counter).equals("SET")){
            return false;
        }
        parser.counter++;
        ArrayList<String> nameValuePairs = new ArrayList<>();
        //name value pair for easy interpreting later
        if (!nameValuePair(commands, server, parser, nameValuePairs, tableName)){
            return false;
        }
        //calls condition class
        Condition conditions = new Condition();
        //gives IDs to update
        if(!conditions.conditionToCheck(commands, server, parser, idToUpdate, tableName)){
            return false;
        }

        updateCondition(server, nameValuePairs, tableName, idToUpdate);
        return true;

    }
    //generates pair for easy interpreting for what column and value
    public boolean nameValuePair(ArrayList<String> commands, DBServer server, ParseAndInterpret parser,
                                 ArrayList<String> nameValuePairs, String tableName){
        //goes round until reaches where
        while(!commands.get(parser.counter).equals("WHERE")) {
            String columnName = commands.get(parser.counter);
            //ensure column exists
            if (!server.doesDBContainName(tableName, columnName)) {
                return false;
            }
            parser.counter++;
            //ensure that is only =
            if (!commands.get(parser.counter).equals("=")) {
                return false;
            }
            parser.counter++;
            //value updated to
            String value = commands.get(parser.counter);
            //stores seperated by tab
            String pair =  columnName + "\t" + value;
            nameValuePairs.add(pair);
            //ensures comma if not at end
            if(!commands.get(parser.counter+1).equals("WHERE") && !commands.get(parser.counter+1).equals(",")){
                return false;
            }
            parser.counter++;
            //if comma then goes forward 1
            if(commands.get(parser.counter).equals(",")){
                parser.counter++;
            }
        }
        parser.counter++;
        return true;
    }
    //updates the value
    private void updateCondition(DBServer server, ArrayList<String> nameValuePairs, String tableName, ArrayList<String> idToUpdate){
        //returns table from DB
        LinkedHashMap<String, ArrayList<String>> table = server.returnTable(tableName);

        //for number of changes to be made
        for (int i = 0;  i < nameValuePairs.size(); i++){
            //split back as seperated by column name and then value
            String[] nameValueSplit = nameValuePairs.get(i).split("\t");
            //returns column from table
            ArrayList<String> column = table.get(nameValueSplit[0]);
            //iterates through ID list
            for (int j = 0; j < idToUpdate.size(); j++){
                //iterates through column
                for(int k = 0; k < column.size(); k++){
                    //splits column value (as id then value)
                    String[] columnSplit = column.get(k).split("\t");
                    //if IDs match
                    if (idToUpdate.get(j).equals(columnSplit[0])){
                        //ID
                        String newID = columnSplit[0];
                        //new value
                        String newValue = nameValueSplit[1];
                        //seperate by tab
                        String valueToSet = newID + "\t" + newValue;
                        //adds it to relevant place in column array
                        column.set(k, valueToSet);
                        //adds back to temp table
                        table.put(nameValueSplit[0], column);
                    }

                }

            }
        }
        //adds to DB in memory
        server.addToDatabase(tableName, table);
    }

}
