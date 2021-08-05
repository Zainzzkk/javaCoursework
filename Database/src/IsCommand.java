import java.util.*;
//for checking if first word is command
public class IsCommand {
    //array of commands
    String[] isWordCommand = {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN"};

    public boolean isCommand(String command){
        //if command word is contained within array
        if (Arrays.asList(isWordCommand).contains(command)){
            return true;
        }
        return false;
    }
}
