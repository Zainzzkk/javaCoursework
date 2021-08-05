import java.io.*;
import java.util.*;
//class for use command
public class UseCommand {

    public boolean useDatabase(ArrayList<String> commands, DBServer server, ParseAndInterpret parser)  throws IOException {

        File database = new File(commands.get(parser.counter));
        //ensures it is a directory in file system
        if (!database.isDirectory()){
            server.setErrorMessage("Database not created");
            return false;
        }
        //sets current DB name in server
        server.setCurrentDBName(commands.get(parser.counter));
        //checks if files present in directory to import
        if (ifTablesExist(database)){
            addFilesToDB(database, commands.get(parser.counter), server );
        }
        parser.counter++;

        if (!commands.get(parser.counter).equals(";")){
            return false;
        }
        return true;
    }

    private boolean ifTablesExist(File database){
        //creates list of files
        String[] files = database.list();
        //if no files
        if (files == null){
            return false;
        }
        return true;
    }

    private void addFilesToDB(File database, String name, DBServer server) throws IOException{
        //list files again
        String[] files = database.list();
        //calls database class
        Database toAdd = new Database();
        //iterates through IDs
        for (String s : files){
            //full location
            String location = name + File.separator + s;
            //splits on . due to .txt
            String[] nameOfKey = s.split("\\.");
            //only need name which is key
            String key = nameOfKey[0];
            //adds to DB
            toAdd.openDatabaseTable(key, location, server);
        }
    }

}
