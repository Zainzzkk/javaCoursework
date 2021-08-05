import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class StagMap {

    //map for full game
    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> map;
    //list of all parsed actions
    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> actions;
    //holds inventory of all players
    public LinkedHashMap<String, ArrayList<String>> inventory;
    //holds current location of all players
    public LinkedHashMap<String, String> playerLocation;
    //holds health of all players
    public LinkedHashMap<String, Integer> playerHealth;
    //holds list of items to remove from area of map
    public LinkedHashMap<String, ArrayList<String>> consumedItems;
    StagServer server;

    public StagMap(String entityFilename, String actionFilename, StagServer server) {
        //initialise everything
        map = new LinkedHashMap<>();
        actions = new LinkedHashMap<>();
        inventory = new LinkedHashMap<>();
        playerLocation = new LinkedHashMap<>();
        playerHealth = new LinkedHashMap<>();
        consumedItems = new LinkedHashMap<>();
        this.server = server;
        EntityParser entity = new EntityParser(entityFilename, this);
        ActionParser action = new ActionParser(actionFilename, this);
    }

    public String getCurrentLocation(String playerName){
        return playerLocation.get(playerName);
    }

    //returns all paths in game
    public ArrayList<String> returnAllPaths(){

        ArrayList<String> allPaths = new ArrayList<>();
        //iterates through map - key is path
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : map.entrySet()) {
            String key = entry.getKey();
            allPaths.add(key);
        }
        return allPaths;
    }

    //current player inventory
    public ArrayList<String> returnInventory(String playerName){
        return inventory.get(playerName);
    }

    //artefacts at any location
    public ArrayList<String> artefactsAtLocation(String location){
        return map.get(location).get("artefacts");
    }

    //furniture at any location
    public ArrayList<String> furnitureAtLocation(String location){
        return map.get(location).get("furniture");
    }

    //characters at any location
    public ArrayList<String> charactersAtLocation(String location){
        return map.get(location).get("characters");
    }

    //returns specific items at a map
    public ArrayList<String> returnUnplaced(String key, String type){
        return map.get(key).get(type);
    }

    //returns all paths
    public ArrayList<String> getPaths(String location){
        return map.get(location).get("paths");
    }

    public ArrayList<String> returnActionsSplit(){

        ArrayList<String> actionList = new ArrayList<>();

        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : actions.entrySet()) {
            String key = entry.getKey();
            String[] keySplit = key.split(" ");
            actionList.addAll(Arrays.asList(keySplit));
        }

        return actionList;
    }

    public ArrayList<String> returnActions(){

        ArrayList<String> actionList = new ArrayList<>();

        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : actions.entrySet()) {
            String key = entry.getKey();
            actionList.add(key);
        }

        return actionList;
    }

    //details of specific action
    public LinkedHashMap<String, ArrayList<String>> returnActionDetails(String action){

        return actions.get(action);
    }

}


