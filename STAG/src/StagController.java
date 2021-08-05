import java.util.*;

public class StagController {

    StagMap gameMap;
    StagServer gameServer;

    public StagController(StagMap map, StagServer server){
        gameMap = map;
        gameServer = server;
    }

    public void nextCommand(String line){
        InstructionParser commandsParsed = new InstructionParser(this, gameMap, gameServer);
        commandsParsed.instructionParser(line);
    }

    public void addPlayerLocation(String playerName, String location){
        //adds player to location
        gameMap.map.get(location).get("players").add(playerName);
        gameMap.playerLocation.put(playerName, location);
    }

    public boolean printInventory(String playerName, ArrayList<String> commands, int from){
        //checks no game command after
        if (!checkForLegality(commands, from)){
            return false;
        }

        ArrayList<String> currentInventory = gameMap.inventory.get(playerName);
        //stops overflow
        //checks if empty or null
        if (currentInventory == null) {
            gameServer.narration.add("Your inventory is empty");
        } else if (currentInventory.isEmpty()){
            gameServer.narration.add("Your inventory is empty");
        } else {
            gameServer.narration.add("Your inventory contains:");
            for (String item : currentInventory) {
                //only want to print name which is first in split
                String[] itemSplit = item.split("\t");
                gameServer.narration.add(itemSplit[0]);
            }
        }
        return true;
    }

    public boolean printHealth(String playerName, ArrayList<String> commands, int from){

        if (!checkForLegality(commands, from)){
            return false;
        }
        //health stored in this hashmap
        gameServer.narration.add("Your health is: " + gameMap.playerHealth.get(playerName).toString());
        return true;
    }

    public boolean checkForLegality(ArrayList<String> commands, int from){

        for (int i = from; i < commands.size(); i++){
            InstructionParser checker = new InstructionParser(this, gameMap, gameServer);
            if (!checkForLegalityExpanded(checker, commands, i)){
                return false;
            }
        }
        return true;
    }

    private boolean checkForLegalityExpanded(InstructionParser checker, ArrayList<String> commands, int i){

        if ((i + 1) < commands.size()){
            if (checker.actionCommands.contains(commands.get(i+1))){
                gameServer.narration.add("ERROR: Illegal command entered");
                return false;
            }
            if (checker.gameCommands.contains(commands.get(i+1))){
                gameServer.narration.add("ERROR: Illegal command entered");
                return false;
            }
        }
        return true;
    }

    private String fullConsumedList(String location, String item){
        //want to store with location, item and description for easy storage
        //for deletion
        String[] newItem = new String[3];

        String[] itemSplit = item.split("\t");

        newItem[0] = location;
        newItem[1] = itemSplit[0];
        newItem[2] = itemSplit[1];

        return newItem[0] + "\t" + newItem[1] + "\t" + newItem[2];
    }

    public void addArtefact(String location, String artefact, String locationToRemove){
        //can add straight as always arraylist present
        gameMap.map.get(location).get("artefacts").add(artefact);
        //if nothing in consumed item need blank arraylist so no exception
        if (gameMap.consumedItems.get("artefacts") == null){
            ArrayList<String> emptyList = new ArrayList<>();
            gameMap.consumedItems.put("artefacts", emptyList);
        }
        String artefactToAdd = fullConsumedList(locationToRemove, artefact);
        //add to consumed arraylist for easier deletion
        gameMap.consumedItems.get("artefacts").add(artefactToAdd);
    }

    public void addFurniture(String location, String furniture, String locationToRemove){
        gameMap.map.get(location).get("furniture").add(furniture);
        if (gameMap.consumedItems.get("furniture") == null){
            ArrayList<String> emptyList = new ArrayList<>();
            gameMap.consumedItems.put("furniture", emptyList);
        }
        String furnitureToAdd = fullConsumedList(locationToRemove, furniture);
        gameMap.consumedItems.get("furniture").add(furnitureToAdd);
    }

    public void addCharacter(String location, String character, String locationToRemove){
        gameMap.map.get(location).get("characters").add(character);
        if (gameMap.consumedItems.get("characters") == null){
            ArrayList<String> emptyList = new ArrayList<>();
            gameMap.consumedItems.put("characters", emptyList);
        }
        String characterToAdd = fullConsumedList(locationToRemove, character);
        gameMap.consumedItems.get("characters").add(characterToAdd);
    }

    public void removeProduced(){
        //go through each type of item to remove
        producedArtefacts("artefacts");
        producedArtefacts("furniture");
        producedArtefacts("characters");
        //reset list once done
        gameMap.consumedItems.clear();
    }

    private void producedArtefacts(String type){
        //stored in hashmap by type
        ArrayList<String> toRemove = gameMap.consumedItems.get(type);
        //if null nothing there
        if (toRemove == null){
            return;
        }
        //how many items to remove
        int size = toRemove.size();
        int i = 0;
        //loop through
        while (i < size){
            String remove = toRemove.get(i);
            //split to match name
            String[] removeSplit = remove.split("\t");
            //remove each type
            if (type.equals("artefacts")) {
                removeArtefact(removeSplit[0], removeSplit[1]);
            }
            if (type.equals("furniture")){
                removeFurniture(removeSplit[0], removeSplit[1]);
            }
            if (type.equals("characters")){
                removeCharacter(removeSplit[0], removeSplit[1]);
            }
            i++;
        }
    }

    //adds path to map
    public void addPaths(String location, String path){
        gameMap.map.get(location).get("paths").add(path);
    }

    public void removeFurniture(String location, String furniture){

        String itemName = null;

        for (String furn : gameMap.map.get(location).get("furniture")){
            String[] furnitureSplit = furn.split("\t");
            if (furnitureSplit[0].equals(furniture)) {
                itemName = furn;
            }
        }
        //removes furniture from map
        gameMap.map.get(location).get("furniture").remove(itemName);
    }

    public void removeArtefact(String location, String artefact){

        String itemName = null;

        for (String art : gameMap.map.get(location).get("artefacts")){
            String[] artSplit = art.split("\t");
            if (artSplit[0].equals(artefact)) {
                itemName = art;
            }
        }
        //removes artefact.  This way stops any exceptions
        gameMap.map.get(location).get("artefacts").remove(itemName);
    }

    public void removeFromInventory(String item, String playerName){

        String itemName = null;
        for (String invItem : gameMap.inventory.get(playerName)){
            String[] invItemSplit = invItem.split("\t");
            if (invItemSplit[0].equals(item)) {
                itemName = invItem;
            }
        }
        gameMap.inventory.get(playerName).remove(itemName);
    }

    public void removeCharacter(String location, String character){

        String itemName = null;
        for (String chara : gameMap.map.get(location).get("characters")){
            String[] charaSplit = chara.split("\t");
            if (charaSplit[0].equals(character)) {
                itemName = chara;
            }
        }
        gameMap.map.get(location).get("characters").remove(itemName);
    }

    //moves from from location to inventory
    public void moveArteFactInventory(String playerName, String location, String artefactName){
        //get artefacts from location
        ArrayList<String> artefactsImport = gameMap.map.get(location).get("artefacts");
        String artefactFull = null;

        for (String s : artefactsImport) {
            String[] artefactArrayName = s.split("\t");
            //if matches then leaves loop
            if (artefactArrayName[0].equals(artefactName)) {
                artefactFull = s;
                break;
            }
        }
        //removes artefact from map
        gameMap.map.get(location).get("artefacts").remove(artefactFull);
        //if inventory empty need to add arraylist so no exception
        if (gameMap.inventory.get(playerName) == null){
            ArrayList<String> inventoryNew = new ArrayList<>();
            //adds artefact to inventory
            inventoryNew.add(artefactFull);
            gameMap.inventory.put(playerName, inventoryNew);
        } else {
            //if inventory exists then just add item
            gameMap.inventory.get(playerName).add(artefactFull);
        }
    }

    public void dropWholeInventory(String playerName, String location){

        ArrayList<String> playerInv = gameMap.inventory.get(playerName);
        //arraylist to add items to remove
        ArrayList<String> toRemove = new ArrayList<>();
        if (!playerInv.isEmpty()){
            for (String inv : playerInv){
                toRemove.add(inv);
            }
            dropFromInventory(playerName, location, toRemove);
        }
    }

    public void dropFromInventory(String playerName, String location, ArrayList<String> artefactName){
        //deletes all items from arraylist from inventory
        gameMap.inventory.get(playerName).removeAll(artefactName);
        for (String artefact : artefactName) {
            //if empty then make new arraylist to add to
            if (gameMap.map.get(location).get("artefacts").isEmpty()) {
                ArrayList<String> artefactsNew = new ArrayList<>();
                artefactsNew.add(artefact);
                gameMap.map.get(location).put("artefacts", artefactsNew);
            } else {
                gameMap.map.get(location).get("artefacts").add(artefact);
            }
        }
    }

    public void moveTo(String locationFrom, String locationTo, String playerName){
        //remove player from current position
        gameMap.map.get(locationFrom).get("players").remove(playerName);
        //add to new position
        gameMap.map.get(locationTo).get("players").add(playerName);
        //change player locations arraylist
        gameMap.playerLocation.put(playerName, locationTo);
    }

    public void printLook(String location, String playerName) {

        gameServer.narration.add("You are in:");
        //description of current location
        addToNarrationListNoSplit(gameMap.map.get(location).get("description"));

        gameServer.narration.add("You can see:");
        //adds each type of item
        addToNarrationList(gameMap.map.get(location).get("artefacts"));
        addToNarrationList(gameMap.map.get(location).get("furniture"));
        addToNarrationList(gameMap.map.get(location).get("characters"));
        addPlayerToNarration(gameMap.map.get(location).get("players"), playerName);

        gameServer.narration.add("You can access from here:");
        addToNarrationListNoSplit(gameMap.map.get(location).get("paths"));
    }

    private void addToNarrationListNoSplit(ArrayList<String> toAdd) {
        //adds full item
        for (String adder : toAdd) {
            gameServer.narration.add(adder);
        }
    }

    private void addPlayerToNarration(ArrayList<String> toAdd, String playerName){
        //looks for other players
        for (String adder : toAdd) {
            if (!playerName.equals(adder)) {
                gameServer.narration.add(adder);
            }
        }
    }

    private void addToNarrationList(ArrayList<String> toAdd){
        //adds only the description
        for (String adder : toAdd){
            String[] adderSplit = adder.split("\t");
            gameServer.narration.add(adderSplit[1]);
        }
    }

    //adds new player health
    public void newPlayerHealth(String playerName){
        gameMap.playerHealth.put(playerName, 3);
    }

    //adds health as long as below 3
    public void addHealth(String playerName){
        if(gameMap.playerHealth.get(playerName) < 3){
            Integer health = gameMap.playerHealth.get(playerName);
            health += 1;
            gameMap.playerHealth.put(playerName, health);
        }
    }

    //removes health unless player health below 1
    public boolean removeHealth(String playerName){
        Integer health = gameMap.playerHealth.get(playerName);
        health -= 1;
        if (health >= 1) {
            gameMap.playerHealth.put(playerName, health);
            return true;
        } else {
            gameMap.playerHealth.put(playerName, health);
            return false;
        }
    }
}
