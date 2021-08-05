import java.io.*;
import java.net.*;
import java.util.*;

class DBServer
{
    private String currentDBName;
    private LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> currentDatabase;
    private ArrayList<String> tableToPrint;
    private String errorMessage;

    public DBServer(int portNumber)
    {
       setCurrentDBName("default");
       currentDatabase = new LinkedHashMap<>();
       errorMessage = null;

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) processNextConnection(serverSocket);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextConnection(ServerSocket serverSocket)
    {
        try {
            Socket socket = serverSocket.accept();
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connection Established");
            while(true) processNextCommand(socketReader, socketWriter);
        } catch(IOException ioe) {
            System.err.println(ioe);
        } catch(NullPointerException npe) {
            System.out.println("Connection Lost");
        }
    }

    private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException
    {
        Parser commands = new Parser();
        tableToPrint = new ArrayList<>();
        String incomingCommand = socketReader.readLine();
        System.out.println("Received message: " + incomingCommand);
        if (commands.tokeniseCommand(incomingCommand, this)){
            //if true then write ok to client
            socketWriter.write("[OK]");
            //print table to return (select or join) if there is a table there
            if (tableToPrint.size() != 0){
                socketWriter.write("\n");
                for (int i = 0; i < tableToPrint.size(); i++){
                    socketWriter.write(tableToPrint.get(i) + "\n");
                }
            }
        } else {
            //generic error message
            if (errorMessage == null) {
                socketWriter.write("[ERROR]");
            }else {
                socketWriter.write("[ERROR]: " + errorMessage);
            }
        }
        //reset error to null
        errorMessage = null;
        socketWriter.write("\n" + ((char)4) + "\n");
        socketWriter.flush();
    }

    public static void main(String args[]) throws IOException
    {
        DBServer server = new DBServer(8888);
    }
    //sets the database name to current one being used
    public void setCurrentDBName(String name){
        currentDBName = name.toUpperCase();
    }
    //returns current DB name to functions to know if set or not
    public String returnCurrentDBName(){
        return currentDBName;
    }
    //adds table to a database
   public void addToDatabase(String name, LinkedHashMap<String, ArrayList<String>> table){
        currentDatabase.put(name,table);
    }
    //drops the full database
    public void deleteFullDatabase(){
        currentDatabase.clear();
    }
    //sets a personalised error message
    public void setErrorMessage(String error){
        errorMessage = error;
    }
    //deletes a single table from database
    public boolean deleteTableDatabase(String name){

        if (currentDatabase.containsKey(name)){
            currentDatabase.remove(name);
            return true;
        }
        return false;
    }
    //checks if column exists
    public boolean doesDBContainName(String tableName, String key){

        if(currentDatabase.get(tableName).containsKey(key)){
            return true;
        }
        return false;
    }
    //removes certain column
    public void removeColumnDB(String tableName, String key){
        currentDatabase.get(tableName).remove(key);
    }
    //checks if table is in database
    public boolean doesTableExist(String name){
        if (currentDatabase.containsKey(name)){
            return true;
        }
        return false;
    }
    //size of current table in DB
    public int sizeOfTable(String name){
        return currentDatabase.get(name).size();
    }
    //returns a specific table from database
    public LinkedHashMap<String, ArrayList<String>> returnTable(String name){
        return currentDatabase.get(name);
    }
    //writing to file
    public void writeDBToFile() throws IOException{
        Database toWrite = new Database();
        toWrite.writeTableToFile(currentDatabase,this);
    }
    //prints out contents of table in format
    public void tablePrinter(LinkedHashMap<String, ArrayList<String>> tableIn){

        Rows row = new Rows();

        ArrayList<String> titleLine = row.findColumnTitles(tableIn);
        if (titleLine.size() == 0){
            return;
        }
        row.populateRows(tableIn, titleLine, tableToPrint);

    }
}


