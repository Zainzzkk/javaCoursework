import java.io.*;
import java.util.*;
//class for reading in from file and importing
public class TableReadIn {
    //temp table
    private LinkedHashMap<String, ArrayList<String>> readIn;

    public TableReadIn()  {
         readIn = new LinkedHashMap<>();
    }
    //imports from file
    public LinkedHashMap<String, ArrayList<String>> tableImporter(String name) throws IOException {
        //file to open
        File fileToOpen = new File(name);
        //which row currently on
        String thisRow;
        //titles
        String[] titleLine;
        //row from file
        String[] fileRow;
        //arraylist of column
        ArrayList<String> columnArray = new ArrayList<>();
        //sets condition
        boolean fileStatus = true;
        //calls column class for population
        Columns columnsToAdd = new Columns();
        BufferedReader readFileBuffer = new BufferedReader(new FileReader(name));
        try {
            //ensures that file exists
            if (fileToOpen.exists()) {
                //reads in row
                thisRow = readFileBuffer.readLine();
                //if null
                if (!checkIfNull(thisRow)) {
                    //then closes buffer and throws exception
                    readFileBuffer.close();
                    throw new EOFException();
                } else {
                    //splits on tab (as stored in file)
                    titleLine = thisRow.split("\t");
                }
                //loops whilst there are still rows
                while (fileStatus) {
                    thisRow = readFileBuffer.readLine();
                    //if not null
                    if (checkIfNull(thisRow)) {
                        //splits on tab
                        fileRow = thisRow.split("\t");
                        //adds row to array
                        columnArray.add(fileRow[0]);
                        //converts to column format
                        lineColumnConverter(columnArray, titleLine, fileRow);
                        //if null then end loop
                    } else { fileStatus = false; }
                    //adds to temp table from column class
                    readIn = columnsToAdd.columnConverter(columnArray, titleLine);
                }
            } else { throw new FileNotFoundException(); }
        } catch (FileNotFoundException e) {
            System.err.println("File not found readin!");
        }
        readFileBuffer.close();
        return readIn;
    }
    //checks that row read in is not null
    private boolean checkIfNull(String thisRow) {
        if (thisRow == null){
            return false;
        }
        return true;
    }
    //converts to column format
    private void lineColumnConverter(ArrayList<String> columnArray, String[] titleLine, String[] fileRow) {
        //starts at 1 as 1st column is ID
        for (int i = 1; i < titleLine.length; i++) {
            //adds ID tab then value
            columnArray.add(fileRow[0] + "\t" + fileRow[i]);
        }
    }
}
