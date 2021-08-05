import java.util.*;
//class for select command
public class SelectCommand {
    //imported table
    private LinkedHashMap<String, ArrayList<String>> selectFrom;

    public SelectCommand(){
        selectFrom = new LinkedHashMap<>();
    }

    public boolean selectCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
        //attribute class called
        AttributeList attributes = new AttributeList();
        //arraylist for columns
        ArrayList<String> columnTitles = new ArrayList<>();
        //populate attributes for column titles or *
        if(!attributes.selectAttributeList(commands, columnTitles, parser)){
            return false;
        }

        if (!commands.get(parser.counter).equals("FROM")){
            return false;
        }
        parser.counter++;
        String tableName = commands.get(parser.counter);;
        //checks table exists
        if(!server.doesTableExist(tableName)){
            server.setErrorMessage("Table does not exist");
            return false;
        }

        for (int i = 0; i < columnTitles.size(); i++){
            //check that each column exists if not *
            if (!server.doesDBContainName(tableName, columnTitles.get(i)) && !columnTitles.get(i).equals("*")){
               server.setErrorMessage("Column does not exist");
               return false;
            }
        }
        parser.counter++;
        //if no where condition
        if (commands.get(parser.counter).equals(";")){
            //imports from DB
            selectFrom = server.returnTable(tableName);
            //makes select table
            selectFromTable(columnTitles, server, selectFrom);
            return true;
        } else{
            //where conditions interpreted and table generated
            if(!whereCondition(commands, server, parser, columnTitles, tableName)){
                return false;
            }
            return true;
        }
    }

    private boolean whereCondition(ArrayList<String> commands, DBServer server, ParseAndInterpret parser,
                                   ArrayList<String> columnTitles, String tableName){

        if (!commands.get(parser.counter).equals("WHERE")){
            return false;
        }
        //gives IDs to use for select table
        ArrayList<String> idToSelect = new ArrayList<>();
        parser.counter++;
        //calls condition class
        Condition conditions = new Condition();
        //checks conditions are right
        if (!conditions.conditionToCheck(commands, server, parser, idToSelect, tableName)){
            return false;
        }

        newTable(server, tableName, idToSelect);
        //ensures that select table is not empty
        if (selectFrom.size() == 0){
            server.setErrorMessage("no results in select query");
            return false;
        }

        selectFromTable(columnTitles, server, selectFrom);
        return true;
    }
    //generates table
    private void newTable(DBServer server, String tableName, ArrayList<String> idToSelect){
        //import table given key
        LinkedHashMap<String, ArrayList<String>> tableToImport = server.returnTable(tableName);
        //iterate through table
        for (Map.Entry<String, ArrayList<String>> entry : tableToImport.entrySet()) {
            //column name
            String key = entry.getKey();
            //import column from table
            ArrayList<String> column = tableToImport.get(key);
            //arraylist to store new column
            ArrayList<String> newColumn = new ArrayList<>();

            for (int i = 0; i < idToSelect.size(); i++){
                for(int j = 0; j < column.size(); j++){
                    //split as ID stored first tab and then value
                    String[] columnSplit = column.get(j).split("\t");
                    //if ID matches ID for value then add to column
                    if (idToSelect.get(i).equals(columnSplit[0])){
                        newColumn.add(column.get(j));
                        //put in table
                        selectFrom.put(key, newColumn);
                    }
                }
            }

        }

    }

    private void selectFromTable(ArrayList<String> columnTitles, DBServer server, LinkedHashMap<String, ArrayList<String>> tableSelectFrom){
        //if * then all columns
        if(columnTitles.contains("*")){
            server.tablePrinter(selectFrom);
        }else {
            //hashmap to store new table
            LinkedHashMap<String, ArrayList<String>> tableToPrint = new LinkedHashMap<>();
            for (int i = 0; i < columnTitles.size(); i++){
                //column as key and then column contents as value
                tableToPrint.put(columnTitles.get(i), tableSelectFrom.get(columnTitles.get(i)));
            }
            //converts to printable format
            server.tablePrinter(tableToPrint);
        }
    }
}
