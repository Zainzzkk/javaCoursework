import java.util.*;

public class ActionCommandsParse {

    //stores details like consumed for an action
    LinkedHashMap<String, ArrayList<String>> specificAction;
    LinkedHashMap<String, String> subjects;
    StagController gameController;
    StagMap gameMap;
    StagServer gameServer;

    public ActionCommandsParse(StagController controller, StagMap map, StagServer server){
        specificAction = new LinkedHashMap<>();
        subjects = new LinkedHashMap<>();
        gameController = controller;
        gameMap = map;
        gameServer = server;
    }

    //parser for action commands
    public boolean actionParsed(String playerName, ArrayList<String> commands, ArrayList<String> actions){

        String actionName = null;
        //location of current player
        String location = gameMap.getCurrentLocation(playerName);
        int from = 0;
        //iterates through action list
        for (String thisAction : actions){
            String[] currentAction = thisAction.split(" ");
                //goes through command
                for (int i = 0; i < commands.size(); i++) {
                //once found checks that no other keywords
                    if (currentAction[0].equals(commands.get(i))) {
                        if (!actionSplit(currentAction, i, commands)){
                            return false;
                        }
                        actionName = thisAction;
                        //checks beyond this point
                        from = i;
                        if (!gameController.checkForLegality(commands, i)) {
                            return false;
                        }
                }
            }
        }

        return actionParserExtended(playerName, location, actionName, from, commands);
    }

    private boolean actionSplit(String[] currentAction, int i, ArrayList<String> commands){

        if (currentAction.length == 1){
            return true;
        }
        else {
            return commands.get(i + 1).equals(currentAction[1]);
        }
    }

    private boolean actionParserExtended(String playerName, String location, String actionName, int from, ArrayList<String> commands) {

        //populates action list
        specificAction = gameMap.returnActionDetails(actionName);
        //ensures subjects are present
        if (!subjectsCheck(location, commands, playerName, from)){
            return false;
        }
        //consumed items
        consumedAction(location, playerName);
        //produced items
        producedAction(location, playerName);
        gameServer.narration.add(specificAction.get("narration").get(0));

        return true;
    }

    private boolean subjectsCheck(String location, ArrayList<String> commands, String playerName, int from){

        //returns subjects for specific action
        ArrayList<String> subjects = specificAction.get("subjects");
        //goes through subjects
        for (String subject : subjects){
            //checks all items required are there
            if (!furnitureCheck(subject, location) && !artefactCheck(subject, location, playerName)
            && !characterCheck(subject, location)){
                gameServer.narration.add("ERROR: Subject needs not met");
                return false;
            }
        }
        return commandSubjectCheck(commands, from, subjects);
    }

    private boolean commandSubjectCheck(ArrayList<String> commands, int from, ArrayList<String> subjects){

        for (String subject : subjects) {
            for (int i = from; i < commands.size(); i++) {
                //checks that on of the subjects is present
                if (subject.equals(commands.get(i))){
                    return true;
                }
            }
        }
        gameServer.narration.add("ERROR: no subject entered");
        return false;
    }

    private boolean furnitureCheck(String subject, String location){

        ArrayList<String> furniture = gameMap.furnitureAtLocation(location);

        if (furniture.isEmpty()){
            return false;
        }
        //checks if any of subjects are furniture and adds them to list
        for (String item : furniture){
            String[] itemSplit = item.split("\t");
            if (itemSplit[0].equals(subject)){
                subjects.put(item, "furniture");
                return true;
            }
        }

        return false;
    }

    private boolean artefactCheck(String subject, String location, String playerName){

        ArrayList<String> artefacts = gameMap.artefactsAtLocation(location);
        ArrayList<String> inventory = gameMap.returnInventory(playerName);
        //checks if artefact on ground
        if (artefactGroundCheck(subject, artefacts)){
            return true;
        }
        //or checks if in inventory
        if (artefactInventoryCheck(subject, inventory)){
            return true;
        }
        return false;
    }

    private boolean artefactGroundCheck(String subject,  ArrayList<String> artefacts) {

        if (!artefacts.isEmpty()){
            for (String item : artefacts){
                String[] itemSplit = item.split("\t");
                //if subject matches
                if (itemSplit[0].equals(subject)){
                    subjects.put(item, "artefacts");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean artefactInventoryCheck(String subject, ArrayList<String> inventory){

        if (inventory != null){
            for (String item : inventory){
                String[] itemSplit = item.split("\t");
                //if inventory matches
                if (itemSplit[0].equals(subject)){
                    subjects.put(item, "inventory");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean characterCheck(String subject, String location){

        ArrayList<String> character = gameMap.charactersAtLocation(location);

        if (character.isEmpty()){
            return false;
        }

        for (String item : character){
            String[] itemSplit = item.split("\t");
            //if character matches
            if (itemSplit[0].equals(subject)){
                subjects.put(item, "characters");
                return true;
            }
        }

        return false;

    }

    private void consumedAction(String location, String playerName){

        ArrayList<String> consumed = specificAction.get("consumed");

        if (!consumed.isEmpty()){
            for (String item : consumed){
                //if health first then will remove health
                if (item.equals("health")){
                   if (!gameController.removeHealth(playerName)){
                      playerDead(playerName, location);
                   }
                   break;
                }
                //where item is located currently
                String where = itemConsumed(item);
                whereItem(where, item, location, playerName);
            }
        }

    }

    private void playerDead(String playerName, String location){
        //drops whole inventory to ground
        gameController.dropWholeInventory(playerName, location);
        PlayerLocation locationToFind = new PlayerLocation();
        //finds start location
        String start = locationToFind.startLocation(gameMap);
        //moves player to location
        gameController.moveTo(location, start, playerName);
        //sets health to max
        gameController.newPlayerHealth(playerName);
        PlayerLocation startLocation = new PlayerLocation();
        gameServer.narration.add("You died and have been return back to " + startLocation.startLocation(gameMap));
    }

    private void whereItem(String where, String item, String location, String playerName){
        //removes each type of item from location
        if (where.equals("furniture")){
            gameController.removeFurniture(location, item);
        }
        if (where.equals("inventory")){
            gameController.removeFromInventory(item, playerName);
        }
        if (where.equals("characters")){
            gameController.removeCharacter(location, item);
        }
        if (where.equals("artefacts")){
            gameController.removeArtefact(location, item);
        }

    }

    private String itemConsumed(String item){

        String where = null;

        for (Map.Entry<String, String> entry : subjects.entrySet()) {
            String key = entry.getKey();
            String[] keySplit = key.split("\t");
            if (keySplit[0].equals(item)){
                where = subjects.get(key);
            }
        }

        return where;
    }

    private void producedAction(String location, String playerName){

        ArrayList<String> produced = specificAction.get("produced");

        if (!produced.isEmpty()){
            for (String item : produced) {
                //checks if health to be added
                if (item.equals("health")){
                    gameController.addHealth(playerName);
                } else {
                    //goes through each type
                    artefactProduced(item, location);
                    furnitureProduced(item, location);
                    characterProduced(item, location);
                    pathsProduced(item, location);
                }
            }
        }
        gameController.removeProduced();
    }

    private void artefactProduced(String item, String location){

        ArrayList<String> artefactToAdd = new ArrayList<>();
        //goes through map
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : gameMap.map.entrySet()) {
            String key = entry.getKey();
            //returns artefacts in each location
            ArrayList<String> unplacedArtefacts = gameMap.returnUnplaced(key,"artefacts");
            //adds location to each artefact
            for (String artefact : unplacedArtefacts) {
                String[] artefactSplit = artefact.split("\t");
                String fullArtefact = key + "\t" + artefactSplit[0] + "\t" + artefactSplit[1];
                //adds to artefact list
                artefactToAdd.add(fullArtefact);
            }
        }

        for (String artefact : artefactToAdd) {
            String[] artefactSplit = artefact.split("\t");
            //checks if they match
            if (artefactSplit[1].equals(item)) {
                String artefactString = artefactSplit[1] + "\t" + artefactSplit[2];
                gameController.addArtefact(location, artefactString, artefactSplit[0]);
            }
        }
    }

    private void characterProduced(String item, String location){
        //does same as artefacts for characters
        ArrayList<String> characterToAdd = new ArrayList<>();

        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : gameMap.map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> unplacedCharacter = gameMap.returnUnplaced(key, "characters");

            for (String character : unplacedCharacter) {
                String[] characterSplit = character.split("\t");
                String fullCharacter = key + "\t" + characterSplit[0] + "\t" + characterSplit[1];
                characterToAdd.add(fullCharacter);
            }
        }

        for (String character : characterToAdd) {
            String[] characterSplit = character.split("\t");
            if (characterSplit[1].equals(item)) {
                String characterString = characterSplit[1] + "\t" + characterSplit[2];
                gameController.addCharacter(location, characterString, characterSplit[0]);
            }
        }
    }

    private void furnitureProduced(String item, String location){

        ArrayList<String> furnitureToAdd = new ArrayList<>();


        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : gameMap.map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> furnitureArtefacts = gameMap.returnUnplaced(key, "furniture");

            for (String furniture : furnitureArtefacts) {
                String[] furnitureSplit = furniture.split("\t");
                String fullFurniture = key + "\t" + furnitureSplit[0] + "\t" + furnitureSplit[1];
                furnitureToAdd.add(fullFurniture);
            }
        }

        for (String furniture : furnitureToAdd) {
            String[] furnitureSplit = furniture.split("\t");
            if (furnitureSplit[1].equals(item)) {
                String characterString = furnitureSplit[1] + "\t" + furnitureSplit[2];
                gameController.addFurniture(location, characterString, furnitureSplit[0]);
            }
        }
    }

    private void pathsProduced(String item, String location){
        //adds path if matches
        ArrayList<String> pathsPaths = gameMap.returnAllPaths();

        for (String paths : pathsPaths){
            if (paths.equals(item)){
                gameController.addPaths(location, paths);
            }
        }
    }
}
