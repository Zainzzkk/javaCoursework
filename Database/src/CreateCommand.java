import java.io.*;
import java.util.*;
//class for the create command
public class CreateCommand {

    public boolean createCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser) throws IOException {

        Database newDatabase = new Database();
        //if creating new database
        if (commands.get(parser.counter).equals("DATABASE")) {
            if (!createDatabaseConditions(newDatabase, commands, parser)) {
                return false;
            }
            return true;
        }
        //if creating new table
        if ((commands.get(parser.counter).equals("TABLE"))) {
            parser.counter++;
            if (!createTableConditions(commands, server, parser)) {
                server.setErrorMessage("Database not selected or created");
                return false;
            }
            return true;
        }
        return false;
    }

    //function for database creation
    private boolean createDatabaseConditions(Database newDatabase, ArrayList<String> commands, ParseAndInterpret parser) throws IOException {

        parser.counter++;
        //name of database which goes to respective class
        newDatabase.createDatabase(commands.get(parser.counter));
        parser.counter++;
        //ensures closing ;
        if (!commands.get(parser.counter).equals(";")) {
            return false;
        }
        return true;
    }

    private boolean createTableConditions(ArrayList<String> commands, DBServer server, ParseAndInterpret parser) throws IOException {
        //ensures that database is opened first
        if (server.returnCurrentDBName().equals("DEFAULT")) {
            return false;
        }
        //calls table class
        TableModify tableToInsert = new TableModify();
        //arraylist for the titles of table columns which will be specified
        ArrayList<String> columnTitles = new ArrayList<>();
        //create a hashmap for the table which then can be inserted into server database
        LinkedHashMap<String, ArrayList<String>> tableMade;
        //name of table
        String tableName = commands.get(parser.counter);
        parser.counter++;
        //if no attributes entered
        if (commands.get(parser.counter).equals(";")) {
            columnTitles.add("");
            //creates table using table class
            tableMade = tableToInsert.createTable(tableName, server, columnTitles);
            //adds table to database
            server.addToDatabase(tableName, tableMade);
            return true;
        } else {
            // if not ; then attribute class instance created
            AttributeList attributes = new AttributeList();
            //call to attributes which will populate columnTitles arraylist
            if (!attributes.attributeList(commands, columnTitles, parser)) {
                return false;
            }
            //inserts column titles into table using table class
            tableMade = tableToInsert.createTable(tableName, server, columnTitles);
            //adds to DB
            server.addToDatabase(tableName, tableMade);
            parser.counter++;
        }
        if (!commands.get(parser.counter).equals(";")) {
            return false;
        }
        return true;
    }

}
