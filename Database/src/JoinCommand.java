import java.util.*;
//class for creating join table
public class JoinCommand {
    //actual join table populated
    private LinkedHashMap<String, ArrayList<String>> joinTable;
    //table for first join
    private LinkedHashMap<String, ArrayList<String>> tableOneImport;
    //table for second join
    private LinkedHashMap<String, ArrayList<String>> tableTwoImport;
    //matching IDs
    private ArrayList<String> matching;
    //counter for number for columns
    private int columnCount = 0;

    public JoinCommand(){
        joinTable = new LinkedHashMap<>();
        tableOneImport = new LinkedHashMap<>();
        tableTwoImport = new LinkedHashMap<>();
        matching = new ArrayList<>();
    }

    public boolean joinCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
        //name of table one
        String tableNameOne = commands.get(parser.counter);
        //checks table exists and imports

        if (!importTable(server, parser, 1, tableNameOne)){
            return false;
        }

        if (!commands.get(parser.counter).equals("AND")){
            return false;
        }
        parser.counter++;
        //name of second table to import
        String tableNameTwo = commands.get(parser.counter);
        if (!importTable(server, parser, 2, tableNameTwo)){
            return false;
        }

        if (!commands.get(parser.counter).equals("ON")){
            return false;
        }
        parser.counter++;
        //string for what to match with
        String joinIDOne = commands.get(parser.counter);
        //ensure that database contains this column
        if(!server.doesDBContainName(tableNameOne, joinIDOne)){
            return false;
        }
        parser.counter++;

        if (!commands.get(parser.counter).equals("AND")){
            return false;
        }
        parser.counter++;
        //name for second match from second table
        String joinIDTwo = commands.get(parser.counter);
        //check that column present
        if(!server.doesDBContainName(tableNameTwo, joinIDTwo)){
            return false;
        }
        parser.counter++;
        //ensures ends on ;
        if (!commands.get(parser.counter).equals(";")){
            return false;
        }
        //generates and populates join table
        generateTable(joinIDOne, joinIDTwo, tableNameOne, tableNameTwo);
        server.tablePrinter(joinTable);

        return true;
    }

    private boolean importTable(DBServer server, ParseAndInterpret parser, int tableNumber, String tableName){
        //checks if table exists
        if (!server.doesTableExist(tableName)){
            return false;
        }
        //which table to import to
        if (tableNumber == 1){
            tableOneImport = server.returnTable(tableName);
        } else {
            tableTwoImport = server.returnTable(tableName);
        }
        parser.counter++;
        return true;
    }

    private void generateTable(String joinIDOne, String joinIDTwo, String tableNameOne, String tableNameTwo){
        //for titles of columns
        ArrayList<String> columnTitles = new ArrayList<>();
        //populates titles from first table
        columnTitlesFinder(tableOneImport, columnTitles, tableNameOne);
        //then populates with titles from second table
        columnTitlesFinder(tableTwoImport, columnTitles, tableNameTwo);
        //adds ID column and generates other columns
        generateAndAddColumns(columnTitles);
        //matches ids from both
        idMatcher(joinIDOne, joinIDTwo);
        //removes ID column as not to add to ID to join table
        tableOneImport.remove("ID");
        tableTwoImport.remove("ID");

        ArrayList<String> idForJoin = new ArrayList<>();
        //iterates through number of matching ids
        for (int i = 0; i < matching.size(); i++){
            //here we can just generate IDs from according to number of matches
            int idNumber = i + 1;
            //converts ID from number to string to add
            String idNumberString = Integer.toString(idNumber);
            //adds ID number first
            idForJoin.add(idNumberString);
            //puts into table
            joinTable.put("ID", idForJoin);
            //if matching on ID, then it will be the first number in column split
            //as stored as ID tab value
            if (joinIDOne.equals("ID")){
                //iterates through matching table and also tableOne
                tableMaker(matching.get(i), columnTitles, tableOneImport);
            } else {
                //else will be second number stored
                //get row from table
                ArrayList<String> row = tableOneImport.get(joinIDOne);
                //find correct ids
                String tableIDNumber = findTableID(row, matching.get(i));
                //populate and iterate through both
                tableMaker(tableIDNumber, columnTitles, tableOneImport);
            }

            if (joinIDTwo.equals("ID")){
                tableMaker(matching.get(i), columnTitles, tableTwoImport);
            } else {
                ArrayList<String> row = tableTwoImport.get(joinIDTwo);
                String tableIDNumber = findTableID(row, matching.get(i));
                tableMaker(tableIDNumber, columnTitles, tableTwoImport);
            }
            //resets to 0 on each iteration through matching IDs
            columnCount = 0;
        }
    }

    private void generateAndAddColumns(ArrayList<String> columnTitles){

        //create ID column in join table
        ArrayList<String> toAddID = new ArrayList<>();
        //adds column for ID first
        joinTable.put("ID", toAddID);
        //adds column for each of titles
        for (int i = 0; i < columnTitles.size(); i++){
            ArrayList<String> toAddNormal = new ArrayList<>();
            //adds blank arraylist and column title
            joinTable.put(columnTitles.get(i), toAddNormal);
        }
    }
    //finding column titles
    private void columnTitlesFinder(LinkedHashMap<String, ArrayList<String>> table, ArrayList<String> columnTitles, String tableName){
        //iterates through columns
        for (Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
            //name of key
            String name = entry.getKey();
            //ensures that not looking in ID column from imported tables
            if (!name.equals("ID")) {
                //titles have . in middle to show table come from and name of column
                String newName = tableName + "." + name;
                //add to column titles arraylist
                columnTitles.add(newName);
            }
        }
    }
    //matches IDs between both tables
    private void idMatcher(String joinIDOne, String joinIDTwo){
        //import column from tableOne using key
        ArrayList<String> idOne = tableOneImport.get(joinIDOne);
        //import column from tableTwo using key
        ArrayList<String> idTwo = tableTwoImport.get(joinIDTwo);
        //lists with IDs
        ArrayList<String> matchingIDOne = new ArrayList<>();
        ArrayList<String> matchingIDTwo = new ArrayList<>();
        //populates id arraylists
        addIDsToMatch(idOne, matchingIDOne);
        addIDsToMatch(idTwo, matchingIDTwo);
        //iterates through first arraylist ids
        for (int i = 0; i < matchingIDOne.size(); i++){
            //iterates through 2nd table arraylist ids
            for (int j = 0; j < matchingIDTwo.size(); j++){
                //if they are the same then add to matching IDs arraylist
                if (matchingIDTwo.get(j).equals(matchingIDOne.get(i))){
                    matching.add(matchingIDTwo.get(j));
                }
            }
        }
    }
    //finds ids from respective columns
    private void addIDsToMatch( ArrayList<String> id, ArrayList<String> matchingID){
        //iterates through column
        for (int i = 0; i < id.size(); i++){
            //get string with column
            String idFromList = id.get(i);
            //splits as most columns apart from ID split with tab
            //first ID and then 2nd is value
            String[] splitID = idFromList.split("\t");
            //if length over 1 then from not ID column so first value is id
            if (splitID.length > 1){
                matchingID.add(splitID[1]);
            } else {
                //from id column so only 1 value
                matchingID.add(splitID[0]);
            }
        }
    }

    private void tableMaker(String matcher, ArrayList<String> columnTitles, LinkedHashMap<String, ArrayList<String>> table){
        //size of table imported
        int size = table.size();
        //limit to says how far to iterate through join table
        //incase matching id in table so stops as soon as reaches all columns in table
        int limitTo = columnCount + size;
        for (int i = columnCount; i < limitTo; i++) {
            //split on .
            String[] columnTitlesSplit = columnTitles.get(i).split("\\.");
            //this is name of column from table as saved table name . column name
            String columnTitleToMatch = columnTitlesSplit[1];
            //iterates through table
            for (Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
                String originalName = entry.getKey();
                //makes sure skips ID column and also if title matches column from original table
                if (!originalName.equals("ID") && columnTitleToMatch.equals(originalName)) {
                    populator(matcher, columnTitles.get(i), originalName, table);
                }
            }
        }
        //sets column count for second table to start from in join table
        columnCount = limitTo;
    }
    //for if column is not ID to get from 2nd value instead
    private String findTableID(ArrayList<String> row, String matcher){

        String tableIDNumber = null;
        //iterates through row
        for (int i = 0; i < row.size(); i++){
            //splits on tab
            String[] rowValues = row.get(i).split("\t");
            //value is id (rather than table id)
            if(rowValues[1].equals(matcher)){
                //then adds id from table to match to other columns
                tableIDNumber = rowValues[0];
            }
        }
        //returns matching id
        return tableIDNumber;
    }

    private void populator(String matcher, String joinTableColumn, String originalColumnTitle, LinkedHashMap<String, ArrayList<String>> table){
        //imports column from original table
        ArrayList<String> rowFromOriginalTable = table.get(originalColumnTitle);
        //iterates through column
        for (int i = 0; i < rowFromOriginalTable.size(); i++){
            //splits on tab as how stored in table
            String[] idValueSplit = rowFromOriginalTable.get(i).split("\t");
            //if it matches the id given
            if(idValueSplit[0].equals(matcher)){
                //gets row from join table
                ArrayList<String> rowFromJoinTable = joinTable.get(joinTableColumn);
                //adds id
                rowFromJoinTable.add(idValueSplit[1]);
                //puts back into join table
                joinTable.put(joinTableColumn, rowFromJoinTable);

            }
        }
    }

}
