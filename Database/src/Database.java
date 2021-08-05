import java.util.*;
import java.io.*;

//database class for database related things
public class Database {
    //local instance of full database
    private LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> database;

    public Database () {
        database = new LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>();
    }
    //function to create database from scratch
    public boolean createDatabase(String name) throws IOException{
        //takes name for database
        File database = new File(name);
        //checks if folder already exists as DB exists as folder in filespace
        if (!database.isDirectory()) {
            //makes DB directory (folder)
              if(!database.mkdir()){
                  throw new InterruptedIOException();
              }
            return true;
        }
        //if for any reason fails then returns false
        return false;
    }
    //when use is called opens database from filesystem
    public void openDatabaseTable(String name, String location, DBServer server) throws IOException{
        //creates table class
        TableModify tableName = new TableModify();
        //key is name and location is directory for opening file and adding to db
        server.addToDatabase(name, tableName.tableImport(location));
    }
    //function to export database to file
    public void writeTableToFile(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> currentDatabase,
                                 DBServer server) throws IOException {
        //iterator to go through database
        Iterator databaseIterator = currentDatabase.entrySet().iterator();
        //ensures that doesn't overflow
        while(databaseIterator.hasNext()){
            //call table class
            TableModify tableName = new TableModify();
            Map.Entry databaseElement = (Map.Entry)databaseIterator.next();
            //gets key from element
            String nameOfTable = (String)databaseElement.getKey();
            //goes to table export class and puts table to file system
            tableName.tableExport(server, nameOfTable, (LinkedHashMap<String, ArrayList<String>>) databaseElement.getValue());
        }
    }
    //method to delete a database from drop command
    public boolean dropDatabase(DBServer server, String name){
        //name of folder/directory to delete
        File toDelete = new File(name);
        //ensures that exists so doesn't cause a problem
        if (toDelete.isDirectory()){
            //generates list of files in array
            String[] files = toDelete.list();
            //null if no files
            //have to delete files in directory first and then files
            if (files != null) {
                for (String s : files) {
                    //name of file to delete
                    File fileToDelete = new File(toDelete.getPath(), s);
                    //and deletes
                    fileToDelete.delete();
                }
            }
            //deletes directory
            toDelete.delete();
            //function call to delete memory DB
            dropMemoryDatabase(server, name);
        } else{
            return false;
        }
        return true;
    }

    private void dropMemoryDatabase(DBServer server, String name){
        //ensures that DB is currently in memory
        if (server.returnCurrentDBName().equals(name)){
            //calls function to wipe memory of DB
            server.deleteFullDatabase();
        }
    }

}
