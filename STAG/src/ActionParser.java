import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class ActionParser {

    public ActionParser(String actionFilename, StagMap map) {

        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(actionFilename);
            //parses file
            Object object = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) object;
            //gets action array
            JSONArray array = (JSONArray)jsonObject.get("actions");
            //iterates through array
            for (Object o : array) {
                //force casts object
                JSONObject actionTypes = (JSONObject) o;
                //casts to arraylist and gets trigger names
                ArrayList<String> triggerNames = (ArrayList<String>) actionTypes.get("triggers");

                for (int i = 0; i < triggerNames.size(); i++){
                    //adds each type
                    String trigger = triggerNames.get(i);
                    subjectsAdd(trigger, actionTypes, map);
                    consumedAdd(trigger, actionTypes, map);
                    producedAdd(trigger, actionTypes, map);
                    narrationAdd(trigger, actionTypes, map);
                }
            }

        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void subjectsAdd(String trigger, JSONObject actionTypes, StagMap map) {

        LinkedHashMap<String, ArrayList<String>> subjectsToAdd = new LinkedHashMap<>();
        //force casts to ArrayList
        ArrayList<String> subjectTypes = (ArrayList<String>) actionTypes.get("subjects");
        ArrayList<String> subjectList = new ArrayList<>();
        //adds each subject
        for (String subject : subjectTypes) {
            subjectList.add(subject);
        }
        subjectsToAdd.put("subjects", subjectList);
        //adds it to action map
        map.actions.put(trigger, subjectsToAdd);
    }

    private void consumedAdd(String trigger, JSONObject actionTypes, StagMap map) {
        //gets what is stored in consumed
        LinkedHashMap<String, ArrayList<String>> consumedToAdd = map.actions.get(trigger);
        ArrayList<String> consumedTypes = (ArrayList<String>) actionTypes.get("consumed");
        ArrayList<String> consumedList = new ArrayList<>();
        for (String consumed : consumedTypes) {
            consumedList.add(consumed);
        }
        consumedToAdd.put("consumed", consumedList);
        map.actions.put(trigger, consumedToAdd);
    }

    private void producedAdd(String trigger, JSONObject actionTypes, StagMap map) {
        //gets what is stored in produced
        LinkedHashMap<String, ArrayList<String>> producedToAdd = map.actions.get(trigger);
        ArrayList<String> producedTypes = (ArrayList<String>) actionTypes.get("produced");
        ArrayList<String> producedList = new ArrayList<>();
        for (String produced :producedTypes) {
            producedList.add(produced);
        }
        producedToAdd.put("produced", producedList);
        map.actions.put(trigger, producedToAdd);
    }

    private void narrationAdd(String trigger, JSONObject actionTypes, StagMap map) {
        //adds the narration for each action
        LinkedHashMap<String, ArrayList<String>> narrationToAdd = map.actions.get(trigger);
        String narration = (String) actionTypes.get("narration");
        ArrayList<String> narrationTypes = new ArrayList<>();
        narrationTypes.add(narration);
        narrationToAdd.put("narration", narrationTypes);
        map.actions.put(trigger, narrationToAdd);
    }

}
