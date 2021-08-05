import java.io.*;
import java.util.*;
//class for the alter command
public class AlterCommand {

    public boolean alterCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser) throws IOException {
        //checks if next word is add
        if (!commands.get(parser.counter).equals("TABLE")) {
            return false;
        }
        parser.counter++;

        String tableName = commands.get(parser.counter);
        parser.counter++;
        //string that want to be altered to
        String alteration;
        //if adding to table
        if (commands.get(parser.counter).equals("ADD")){
            parser.counter++;
            alteration = commands.get(parser.counter);
            if (!addCommand(tableName, alteration,server,parser,commands)){
                return false;
            }
            return true;
        }
        //if deleting from table
        if (commands.get(parser.counter).equals("DROP")){
            parser.counter++;
            alteration = commands.get(parser.counter);
            if (!deleteCommand(tableName, alteration,server,parser,commands)){
                return false;
            }
            return true;
        }
        return false;
    }
    // function to add to a table
    public boolean addCommand(String tableName, String alteration, DBServer server, ParseAndInterpret parser,
                              ArrayList<String> commands){
        //ensures that DB is opened and the table is present
        if (server.returnCurrentDBName().equals("DEFAULT") || !server.doesTableExist(tableName)){
            return false;
        }
        TableModify addNewColumn = new TableModify();
        //adds new column to DB
        addNewColumn.addColumnToDB(tableName, alteration, server);

        parser.counter++;
        //check next is ;
        if (!commands.get(parser.counter).equals(";")){
            return false;
        }

        return true;

    }

    public boolean deleteCommand(String tableName, String alteration, DBServer server, ParseAndInterpret parser,
                                 ArrayList<String> commands){
        //check that DB is set and table exists
        if (server.returnCurrentDBName().equals("DEFAULT") || !server.doesTableExist(tableName)){
            return false;
        }
        //ensures that the current column exists in DB
        if(!server.doesDBContainName(tableName, alteration)){
            return false;
        }
        //removes that specific column
        server.removeColumnDB(tableName, alteration);

        parser.counter++;
        if (!commands.get(parser.counter).equals(";")){
            return false;
        }

        return true;

    }
}
