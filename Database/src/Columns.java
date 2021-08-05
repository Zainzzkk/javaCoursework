import java.util.*;
//class to deal with columns
public class Columns {

    private LinkedHashMap<String, ArrayList<String>> columns;

    public Columns() {

        columns = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, ArrayList<String>> columnConverter(ArrayList<String> columnArray, String[] titleLine) {

        for (int i = 0; i < titleLine.length; i++) {
            //generates new arraylist for each column
            ArrayList<String> populatedColumnArray = populateColumnArray(columnArray, titleLine, i);
            //puts into table with column title as key
            columns.put(titleLine[i], populatedColumnArray);
        }
        return columns;
    }

    private ArrayList<String> populateColumnArray(ArrayList<String> columnArray, String[] titleLine, int titleNumber) {
        //arraylist to generate columns
        ArrayList<String> keyValuePair = new ArrayList<>();
        int i = titleNumber;
        int numberOfTitles = titleLine.length;
        //if no values then return
        if (columnArray.size() == 0){
            return keyValuePair;
        }
        while(i < columnArray.size()) {
            keyValuePair.add(columnArray.get(i));
            i += numberOfTitles;
        }
        return keyValuePair;
    }
}


