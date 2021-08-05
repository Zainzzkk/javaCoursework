import java.io.*;
import java.util.*;
//table class
public class TableModify {
    //stores temporary table
    private LinkedHashMap<String, ArrayList<String>> table;

    public TableModify() { table = new LinkedHashMap<>(); }
    //creates new table
    public LinkedHashMap<String, ArrayList<String>> createTable(String name, DBServer server, ArrayList<String> columnTitles) throws IOException{
        //creates file to store in with full path
        FileWriter titleToFile = new FileWriter(server.returnCurrentDBName() + File.separator +
                name + ".txt");
        //if no titles then puts in null
        //if not empty then writes column titles to file
        if (!columnTitles.get(0).equals("")){
            //adds this to temp table
            table.put("ID", null);
            //writes id column to file with tab
            titleToFile.write("ID" + "\t");
            //goes through column titles and adds to file and to temp table
            for (int i = 0; i < columnTitles.size(); i++){
                titleToFile.write(columnTitles.get(i) + "\t");
                table.put(columnTitles.get(i), null);
            }
        }
        titleToFile.flush();
        titleToFile.close();
        return table;
    }
    //for importing from file
    public LinkedHashMap<String, ArrayList<String>> tableImport(String name) throws IOException {
        //calls class
        TableReadIn tableToImport = new TableReadIn();

        try
        {
            //returns table which goes into temp table
           table = tableToImport.tableImporter(name);
        } catch (IOException e) {
            System.out.println("File not found!");
        }

       return table;
    }
    //exports table to file
    public void tableExport(DBServer server, String tableName, LinkedHashMap<String, ArrayList<String>> keyMap) throws IOException{
        //calls relevant class
        TableExportOut tableToExport = new TableExportOut();
        tableToExport.exportToFile(server.returnCurrentDBName(), tableName, keyMap);

    }
    //for deleting a table
    public boolean dropTable(String name, DBServer server){
        //removes from memory
        if (!server.deleteTableDatabase(name)){
            return false;
        }
        //makes filename
        String fileName = name + ".txt";
        //ful path
        String path = server.returnCurrentDBName()+ File.separator + fileName;
        File toDelete = new File(path);
        //if it is file then delete
        if (toDelete.isFile()){
            if(toDelete.delete()){
                return true;
            }
            return false;
        }

        return false;

    }
    //adds table to DB
    public void addTableToDB(String tableName,DBServer server, ArrayList<String> valuesToAdd){
        //imports table
        table = server.returnTable(tableName);
        //index to ensure no overflow
        int listIndex = 0;
        //iterate through table
        for (Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
            //ensures that not over number of values
            if(listIndex < valuesToAdd.size()){
                String key = entry.getKey();
                ArrayList<String> values = new ArrayList<>();
                //gets value stored in column
                if (entry.getValue() != null) {
                    values = entry.getValue();
                }
                values.add(valuesToAdd.get(listIndex));
                table.put(key, values);
                listIndex++;
            } else { break;}
        }
        //adds to memory
        server.addToDatabase(tableName, table);
    }
    //adss a column to a DB
    public void addColumnToDB (String tableName, String alteration, DBServer server){
        //import table
        table = server.returnTable(tableName);

        ArrayList<String> toAdd = new ArrayList<>();
        //iterate through rows
        for (int i = 0; i < table.get("ID").size(); i++){
            //gets IDs from columns
            String idToAdd = table.get("ID").get(i);
            //adds ID and then null in format stored in memory
            String columnAdd = idToAdd + "\t" + null;
            toAdd.add(columnAdd);
        }
        //puts in temp table
        table.put(alteration, toAdd);
        //adds to memory database
        server.addToDatabase(tableName, table);
    }

}
