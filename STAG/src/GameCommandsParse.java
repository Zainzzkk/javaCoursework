import java.util.*;

public class GameCommandsParse {

    StagMap gameMap;
    StagController gameController;
    StagServer gameServer;
    ArrayList<String> gameCommands;

    public GameCommandsParse(StagMap map, StagController controller, StagServer server, ArrayList<String> commands){
        gameMap = map;
        gameController = controller;
        gameServer = server;
        gameCommands = commands;
    }

    public boolean parseGameCommands(String playerName){
        //iterates through game commands
        for (int i = 0; i < gameCommands.size(); i++) {
            //checks if one of the main game commands
            switch (gameCommands.get(i).toLowerCase()) {
                case "inventory":
                case "inv":
                    return gameController.printInventory(playerName, gameCommands, i);
                case "get":
                    return getCommand(playerName, i);
                case "drop":
                   return dropCommand(playerName, i);
                case "goto":
                    return goToCommand(playerName, i);
                case "look":
                    return lookCommand(playerName, i);
                case "health":
                    return gameController.printHealth(playerName, gameCommands, i);
            }
        }
        return false;
    }

    private boolean getCommand(String playerName, int from) {
        //if below 2 then nothing to specified to get
        if (gameCommands.size() <= 2) {
            gameServer.narration.add("ERROR: Item not entered");
            return false;
        }
        //checks no other game commands part of command
        if (!gameController.checkForLegality(gameCommands, from)) {
            return false;
        }
        //current location of player
        String location = gameMap.getCurrentLocation(playerName);
        //artefacts at current location
        ArrayList<String> currentArtefacts = gameMap.artefactsAtLocation(location);
        for (String artefact : currentArtefacts) {
            //splits as stored with name tab description
            String[] artefactName = artefact.split("\t");
            //checks artefact exists
            if (checkGetForItem(playerName, artefactName, location, from)){
                return true;
            }
        }
        gameServer.narration.add("ERROR: Cannot get item");
        return false;
    }

    private boolean checkGetForItem(String playerName,String[] artefactName,
                                        String location, int from){
        //goes through command from current command position
        for (int i = from; i < gameCommands.size(); i++) {
            //[0] as that is where name of item is stored
            if (artefactName[0].equals(gameCommands.get(i))) {
                //move from ground to inventory
                gameController.moveArteFactInventory(playerName, location, gameCommands.get(i));
                gameServer.narration.add("You picked up a " + artefactName[0]);
                return true;
            }
        }
        return false;
    }

    private boolean dropCommand(String playerName, int from){

        if (gameCommands.size() <= 2) {
            gameServer.narration.add("ERROR: Item not entered");
            return false;
        }

        if (!gameController.checkForLegality(gameCommands, from)) {
            return false;
        }

        String location = gameMap.getCurrentLocation(playerName);
        ArrayList<String> playerInventory = gameMap.returnInventory(playerName);
        //adds items to list of items to drop
        ArrayList<String> splitInventoryName = new ArrayList<>();
        if (!(playerInventory == null)) {
            for (String item : playerInventory) {
                String[] inventorySplit = item.split("\t");
                if (checkDropCommand(playerName, inventorySplit, splitInventoryName, item, location, from)){
                    return true;
                }
            }
        }
        gameServer.narration.add("ERROR: Item not in inventory");
        return false;
    }

    private boolean checkDropCommand(String playerName, String[] inventorySplit, ArrayList<String> splitInventoryName,
                                     String item, String location, int from){

        for (int i = from; i < gameCommands.size(); i++) {
            if (inventorySplit[0].equals(gameCommands.get(i))) {
                //add to list first and then remove
                splitInventoryName.add(item);
                gameServer.narration.add("You dropped a " + inventorySplit[0]);
            }
            if (!splitInventoryName.isEmpty()) {
                gameController.dropFromInventory(playerName, location, splitInventoryName);
                return true;
            }
        }
        return false;
    }

    private boolean goToCommand(String playerName, int from){

        if (gameCommands.size() <= 2) {
            gameServer.narration.add("ERROR: Target not entered");
            return false;
        }

        if (!gameController.checkForLegality(gameCommands, from)) {
            return false;
        }

        String location = gameMap.getCurrentLocation(playerName);
        //arraylist of paths on location
        ArrayList<String> pathsTo = gameMap.getPaths(location);

        for (String path: pathsTo){
            if (checkGoTo(playerName, path, location, from)){
                return true;
            }
        }
        gameServer.narration.add("ERROR: Path does not exist");
        return false;
    }

    private boolean checkGoTo(String playerName, String path, String location, int from){

        for (int i = from; i < gameCommands.size(); i++) {
            //ensures path matches and not going to same location
            if (path.equals(gameCommands.get(i)) && (!path.equals(location))) {
                gameController.moveTo(location, gameCommands.get(i), playerName);
                //prints look command on move
                gameController.printLook(gameCommands.get(i), playerName);
                return true;
            }
        }
        return false;
    }

    private boolean lookCommand(String playerName, int from){

        if (!gameController.checkForLegality(gameCommands, from)) {
            return false;
        }

        String location = gameMap.getCurrentLocation(playerName);
        gameController.printLook(location, playerName);

        return true;

    }

}
