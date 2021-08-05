import java.util.*;
//class to convert from stored format to rows
public class Rows {

    private ArrayList<String> rows;

    public Rows() {  rows = new ArrayList<>(); }
    //find columns from table
    public ArrayList<String> findColumnTitles(LinkedHashMap<String, ArrayList<String>> keyMap) {
        //to store titles
        ArrayList<String> titleLine = new ArrayList<>();
        //iterates through table
        for (Map.Entry<String, ArrayList<String>> entry : keyMap.entrySet()) {
            String name = entry.getKey();
            //the key will be the title of the column
            titleLine.add(name);
        }
        return titleLine;
    }


    public void populateRows(LinkedHashMap<String, ArrayList<String>> keyMap, ArrayList<String> titleLine, ArrayList<String> rows) {
        //the first column will say how many items in table
        int numberOfRows = keyMap.get(titleLine.get(0)).size();
        //converts to array for uniformality
        String[] tempTitles = titleLine.toArray(new String[0]);
        //converts it to a format for printing with rows or writing to file from way stored in database in memory
        convertToRowList(tempTitles, rows);

        for (int i = 0; i < numberOfRows; i++) {
            String[] rowValues = new String[titleLine.size()];
            for (int j = 0; j < titleLine.size(); j++) {
                //key is the titleline
                String key = titleLine.get(j);
                String splitValue;
                String[] value = new String[2];
                //intitialise
                value[0] = "";
                //ensures that key is ID and isn't empty
                if (!keyMap.get(key).isEmpty() && key.equals("ID")){
                    //set both to id since only 1 value stored in ID column
                    value[0] = keyMap.get(key).get(i);
                    value[1] = keyMap.get(key).get(i);
                }
                //next the columns which are not ID
                if(!keyMap.get(key).isEmpty() && !key.equals("ID")) {
                    //split on tab as first id and then value
                    value = keyMap.get(key).get(i).split("\t");
                }
                //differentiate between id column values and other columns
                //ensures no overflow
                if (value.length > 1) {
                    //this is value stored in every column as 1st value in array is ID
                    splitValue = value[1];
                    //for id column
                } else { splitValue = value[0]; }
                    //copy to row value
                    rowValues[j] = splitValue;
            }
            //add to rowList
            convertToRowList(rowValues, rows);
        }
    }
    //converts to row format
    public void convertToRowList(String[] rowValues,ArrayList<String> rows){
        //makes new string for full row
        StringJoiner convertToRows = new StringJoiner("\t");

        for (int i = 0; i < rowValues.length; i++) {
            convertToRows.add(rowValues[i]);
        }
        rows.add(convertToRows.toString());
    }
}
