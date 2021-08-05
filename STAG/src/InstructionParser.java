import java.util.*;

public class InstructionParser {

    private ArrayList<String> commands;
    private String[] gameCommandsArray = {"inventory", "inv", "get", "drop", "goto", "look", "health"};
    public ArrayList<String> gameCommands;
    private String currentPlayerLocation;
    private String player;
    public ArrayList<String> actionCommands;
    public ArrayList<String> actionCommandsSplit;
    StagMap gameMap;
    StagController gameController;
    StagServer gameServer;

    public InstructionParser(StagController controller, StagMap map, StagServer server){
        commands = new ArrayList<>();
        gameCommands = new ArrayList<>(Arrays.asList(gameCommandsArray));
        actionCommands = map.returnActions();
        actionCommandsSplit = map.returnActionsSplit();
        gameMap = map;
        gameController = controller;
        gameServer = server;
    }

    public boolean instructionParser(String command){
        //splits commands on spaces
        String[] commandsSplit = command.split(" ");
        //adds to arraylist
        commands.addAll(Arrays.asList(commandsSplit));
        //checks if player is new or not
        playerName();
        //if 1 then no command entered
        if (commands.size() == 1){
            gameServer.narration.add("ERROR: No command entered");
            return false;
        }
        for (String comm : commands) {
            //for game commands goes to lowercase
            if (gameCommands.contains(comm.toLowerCase())) {
                GameCommandsParse gameComm = new GameCommandsParse(gameMap, gameController, gameServer, commands);
                return gameComm.parseGameCommands(player);
            }
            //if action command
            if (actionCommandsSplit.contains(comm)) {
                ActionCommandsParse actionComm = new ActionCommandsParse(gameController, gameMap, gameServer);
                return actionComm.actionParsed(player, commands, actionCommands);
            }
        }
        gameServer.narration.add("ERROR: Command not recognised");
        return false;
    }

    private void playerName(){

        String playerFull = commands.get(0);
        //split on : as that is format
        String[] playerSplit = playerFull.split(":");
        player = playerSplit[0];
        PlayerLocation location = new PlayerLocation();
        if(!location.isPlayerNew(player, gameMap)){
            //puts in start location
            currentPlayerLocation = location.startLocation(gameMap);
            gameController.addPlayerLocation(player, currentPlayerLocation);
            //puts health to 3
            gameController.newPlayerHealth(player);
        } else {
            //if exists then gets current location
            currentPlayerLocation = gameMap.getCurrentLocation(player);
        }
    }
}
