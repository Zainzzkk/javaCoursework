import java.io.IOException;
import java.util.*;

public class Parser {
    //array list of tokens after tokenising
    ArrayList<String> tokens;

    public Parser(){
        tokens = new ArrayList<>();
    }

    public boolean tokeniseCommand(String command, DBServer server) throws IOException {
        //class to tokenise incoming command
        CommandTokenised tokeniser = new CommandTokenised();
        //tokenises string
        if (!tokeniser.tokeniseCommand(command, server, tokens)){
            return false;
        }
        //no commands are under 2 tokens so invalid if less than 2
        if (tokens.size() < 2){
            server.setErrorMessage("Invalid query");
            return false;
        }
        //checks if first word is a command
        IsCommand isCommand = new IsCommand();
        if (!isCommand.isCommand(tokens.get(0))){
            server.setErrorMessage("First word is not command");
            return false;
        }
        //makes sure closes on ;
        if (!tokens.get(tokens.size()-1).equals(";")){
            server.setErrorMessage("No closing ;");
            return false;
        }
        //parses through tokens
        if(!parseCommand(server)){
            return false;
        }

        return true;
    }

    private boolean parseCommand(DBServer server) throws IOException{
        ParseAndInterpret parsing = new ParseAndInterpret();

        if(!parsing.parsingCommand(tokens, server)){
            return false;
        }
        return true;
    }




}
