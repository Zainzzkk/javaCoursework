import java.util.*;

public class PlayerLocation {


    public String startLocation(StagMap gameMap){
        //returns first key which will be start
        Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry = gameMap.map.entrySet().iterator().next();
        return entry.getKey();
    }

    public boolean isPlayerNew(String playerName, StagMap gameMap){
        //checks if any locations contain player
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : gameMap.map.entrySet()) {
            String key = entry.getKey();
            if (gameMap.map.get(key).get("players").contains(playerName)){
                return true;
            }
        }
        return false;
    }

}
