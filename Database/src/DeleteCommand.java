import java.util.*;
//class to deal with delete call
public class DeleteCommand {

    public boolean deleteCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
        //stores IDs that have to be deleted
        ArrayList<String> idToDelete = new ArrayList<>();

        if (!commands.get(parser.counter).equals("FROM")){
            return false;
        }
        parser.counter++;
        //sets tablename
        String tableName = commands.get(parser.counter);
        //ensures that DB is set and the table exists
        if (server.returnCurrentDBName().equals("DEFAULT") || !server.doesTableExist(commands.get(parser.counter))){
            return false;
        }
        parser.counter++;

        if (!commands.get(parser.counter).equals("WHERE")){
            return false;
        }
        parser.counter++;
        //call to condition class
        Condition conditions = new Condition();
        //generates IDs which have to be deleted
        if(!conditions.conditionToCheck(commands, server, parser, idToDelete, tableName)){
            return false;
        }
        //where delete will be done
        rowToDelete(server, tableName, idToDelete);

        return true;

    }
    //function deletes rows form table
    private void rowToDelete(DBServer server, String tableName, ArrayList<String> idToDelete){
        //imports table from database
        LinkedHashMap<String, ArrayList<String>> table = server.returnTable(tableName);
        //iterates through the table hashmap
        for (Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
            //gets key (column title)
            String key = entry.getKey();
            //returns the column contents
            ArrayList<String> column = table.get(key);
            //loop through number of IDs which have to be deleted
            for (int j = 0; j < idToDelete.size(); j++){
                //size of column to iterate through
                for(int k = 0; k < column.size(); k++){
                    //splits as first is id and then the value
                    String[] columnSplit = column.get(k).split("\t");
                    //if id from list of ids to be deleted equals the id part of column
                    if (idToDelete.get(j).equals(columnSplit[0])){
                        //removes
                        column.remove(k);
                        //puts column back into table with deleted value
                        table.put(key, column);
                    }
                }
            }
        }
        //adds modified table back to DB
        server.addToDatabase(tableName, table);
    }
}
