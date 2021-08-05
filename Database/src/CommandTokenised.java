import java.util.*;
import java.io.*;

//class to tokenise the input command string
public class CommandTokenised {
    //for storing the tokens in an arraylist
    ArrayList<String> tokens;

    public CommandTokenised() {
        tokens = new ArrayList<>();
    }

    public boolean tokeniseCommand(String command, DBServer server, ArrayList<String> outgoingCommandList){
        //if nothing passed then return false
        if (command.equals("")){
            server.setErrorMessage("no command given");
            return false;
        }
        //shortest command is 3 letters long, so anything under is invalid
        if (command.length() < 3){
            server.setErrorMessage("Invalid query");
            return false;
        }
        //tokenises
        if(!returnTokens(command)){
            server.setErrorMessage("Error in parsing");
            return false;
        }
        //adds tokens to parser
        outgoingCommandList.addAll(tokens);
        return true;
    }

    public boolean returnTokens(String command) {
        //parses through character by character
        String[] commandArray = command.split("");
        //stops crashing if last char is space
        if(commandArray[commandArray.length - 1].equals(" ")){
            return false;
        }
        int starting = 0;
        //deals with whitespace before command starts
        while (commandArray[starting].equals(" ")){
            starting++;
        }
        //j is for last point reached when parsing
        int j = starting;
        for (int i = starting; i < commandArray.length; i++) {

            if (commandArray[i].equals(" ")) {
                //adds to command list given j is last point and i-1 is current
                addToCommandList(commandArray, j, i - 1);
                //j checks if space and if space then moves up 1 as spaces not wanted
                j = checkForSpace(commandArray, i);
                //sets i to correct position
                i = j - 1;
            }
            //if =
            if (commandArray[i].equals("=")) {
                //if not space then delimits and adds to command list
                ifSpaceBefore(commandArray, j, i);
                //checks if operator after and sets j accordingly and adds accordingly to list
                j = checkForSecondOperator(commandArray, i);
                i = j - 1;
            }

            if (commandArray[i].equals("'")) {
                ifSpaceBefore(commandArray, j, i);
                //ensures that closing ' is there and adds the whole string to command (even if space)
                j = checkForClosingString(commandArray, i, j);
                j = checkForSpace(commandArray, j);
                i = j - 1;
            }

            if (commandArray[i].equals(";")){
                ifSpaceBefore(commandArray, j, i);
                //ensures at end
                if (!checkForClosing(commandArray, i)){
                    return false;
                }
            }

            if (commandArray[i].equals(">")){
                ifSpaceBefore(commandArray, j, i);
                //checks if = after or not
                j = checkForSecondOperator(commandArray, i);
                i = j-1;
            }
            if (commandArray[i].equals("<")){
                ifSpaceBefore(commandArray, j, i);
                j = checkForSecondOperator(commandArray, i);
                i = j-1;
            }
            if (commandArray[i].equals("!")){
                ifSpaceBefore(commandArray, j, i);
                //ensures = after !
                if (!checkForEqualOp(commandArray, i)){
                    return false;
                }
                //movies i forward 1
                i = i + 1;
                j = checkForSpace(commandArray, i);
                i = j - 1;
            }
            if (commandArray[i].equals("*")){
                ifSpaceBefore(commandArray, j, i);
                tokens.add(commandArray[i]);
                j = checkForSpace(commandArray, i);
                i = j - 1;
            }
            if (commandArray[i].equals(",")){
                ifSpaceBefore(commandArray, j, i);
                tokens.add(commandArray[i]);
                j = checkForSpace(commandArray, i);
                i = j - 1;
            }

            if (commandArray[i].equals("(")){
                ifSpaceBefore(commandArray, j, i);
                tokens.add(commandArray[i]);
                j = checkForSpace(commandArray, i);
                i = j - 1;
            }
            if (commandArray[i].equals(")")){
                ifSpaceBefore(commandArray, j, i);
                tokens.add(commandArray[i]);
                j = checkForSpace(commandArray, i);
                i = j - 1;
            }

        }
        return true;
    }

    private void ifSpaceBefore(String[] commandArray, int j, int position){
        //ensures not at beginning or end
        if ((position > 0 && position < commandArray.length) && (j >= 0 && j <commandArray.length)) {
            //if position before is not space or operator then adds to arraylist
            //operators add automatically so stops duplicating
            if (!commandArray[position - 1].equals(" ") && !isOperator(commandArray, position)) {
                addToCommandList(commandArray, j, position - 1);
            }
        }

    }

    private int checkForClosingString(String[] commandArray, int from, int currentCounter){
        //adds the '
        tokens.add(commandArray[from]);
        //moves forward 1 to start of string
        from = from + 1;
        currentCounter = currentCounter + 1;
        //ensures that does not overflow looking for '
        for (int k = from; k < commandArray.length; k++) {
            //breaks out of loop as soon as hits the second '
            if (commandArray[k].equals("'")) {
                addToCommandList(commandArray, currentCounter, k - 1);
                currentCounter = k;
                break;
            }
        }
        tokens.add(commandArray[currentCounter]);
        return currentCounter;

    }

    private boolean checkForClosing(String[] commandArray, int from){
        //ensures at end
        if (from == commandArray.length - 1){
            tokens.add(commandArray[from]);
        }
        else{
            return false;
        }
        return true;
    }

    private boolean checkForEqualOp(String[] commandArray, int from){
        //ensures next one is an = and then adds to arraylist
        if (commandArray[from+1].equals("=")) {
            addToCommandList(commandArray, from, from + 1);
            return true;
        }
        return false;
    }

    private boolean isOperator(String[] commandArray, int currentPointer){
        String[] operators = {"!", "=", "<", ">", "*", "'", "(", ")", ","};
        //keeping tokeniser independant
        if (Arrays.asList(operators).contains(commandArray[currentPointer-1])){
            return true;
        }
        return false;
    }

    private void addToCommandList(String[] commandArray, int from, int to) {
        //to convert from chars to string
        StringBuilder newString = new StringBuilder();
        for (int i = from; i <= to; i++){
            //makes a string and adds between 2 points
            newString.append(commandArray[i].toUpperCase());
        }
        tokens.add(newString.toString());
    }

    private int checkForSecondOperator(String[] commandArray, int currentPoint){

        int nextPoint;
        //checks if next point is = and if it is then adds whole operator to arraylist
        if(commandArray[currentPoint+1].equals("=")){
            addToCommandList(commandArray, currentPoint, currentPoint+1);
            currentPoint = currentPoint + 1;
        } else {
            //else just adds first one
            tokens.add(commandArray[currentPoint]);
        }
        //adjust next point accordingly
        nextPoint = checkForSpace(commandArray, currentPoint);

        return nextPoint;
    }

    private int checkForSpace(String[] commandArray, int currentPoint){
        //if space then moves forward 2 from last point
        if(commandArray[currentPoint+1].equals(" ")) {
            currentPoint = currentPoint + 2;
        }
        else {
            currentPoint = currentPoint + 1;
        }
        return currentPoint;
    }
}
