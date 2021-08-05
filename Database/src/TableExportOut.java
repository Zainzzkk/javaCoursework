import java.util.*;
import java.io.*;
//class to export tables to a file
public class TableExportOut {


    public void exportToFile(String databaseName, String name, LinkedHashMap<String, ArrayList<String>> keyMap) {

        ArrayList<String> titleLine = new ArrayList<>();
        ArrayList<String> rowsToRead = new ArrayList<>();
        Rows row = new Rows();
        //to get titles of table and covert to arraylist
        titleLine = row.findColumnTitles(keyMap);
        //populates rows based on title
        row.populateRows(keyMap, titleLine,rowsToRead);

        try {
            FileWriter tableToFile = new FileWriter(databaseName + File.separator + name + ".txt");
            //writes each line to file
            for (int i = 0; i < rowsToRead.size(); i++) {
                tableToFile.write(rowsToRead.get(i) + "\n");
            }
            tableToFile.flush();
            tableToFile.close();

        } catch (IOException e) {
            System.err.println("Something went wrong writing to file");
            e.printStackTrace();
        }
    }







}
