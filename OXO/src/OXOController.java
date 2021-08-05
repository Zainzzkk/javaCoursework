import OXOExceptions.*;

class OXOController
{
    OXOModel gameModel;
    private int nowPlaying;
    boolean gameOver =  false;

    public OXOController(OXOModel model)
    {
        gameModel = model;
        gameModel.setCurrentPlayer(gameModel.getPlayerByNumber(this.nowPlaying = 0));
        gameModel.setWinThreshold(gameModel.getWinThreshold());
    }

    public void handleIncomingCommand(String command) throws OXOMoveException
    {
        if (!gameOver) {
            decipherCommand(command);
            checkGameOver();
            nextPlayer();
        }
    }

    private void decipherCommand(String command) throws OXOMoveException {

        lengthOfCommand(command);
        firstCharLimit(command);
        int rowNumber = RowBounds(command);
        secondCharLimit(command);
        int columnNumber = ColumnBounds(command);
        putIn(rowNumber, columnNumber);
    }

    private void checkGameOver() {

        checkWinner();

        if (fullBoard()) {
            gameOver = true;
            gameModel.setGameDrawn();
        }


    }

    private boolean fullBoard() {

        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
                if (gameModel.getCellOwner(j, i).getPlayingLetter() == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkWinner() {

        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
                checkWinnerAcross(j, i);
            }
        }
            for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
                for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
                    checkWinnerDown(j, i);
                }
        }
        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
                checkAcrossRight(j, i);
            }
        }

        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
                checkAcrossLeft(j, i);
            }
        }

    }

    private void checkWinnerAcross(int rowNumber, int columnNumber) {

        if (gameModel.getWinThreshold() > gameModel.getNumberOfColumns()) {
            return;
        }

        for (int i = 0; i < gameModel.getWinThreshold() - 1; i++) {
            int newcolumnNumber = columnNumber + i;
            if ((newcolumnNumber + 1) >= gameModel.getNumberOfColumns()){
                return;
            }
            if (gameModel.getCellOwner(rowNumber, newcolumnNumber) != gameModel.getCellOwner(rowNumber, newcolumnNumber + 1)) {
                return;
            }
        }
        gameModel.setWinner(gameModel.getCellOwner(rowNumber,columnNumber));
        gameOver = true;
    }

    private void checkWinnerDown(int rowNumber, int columnNumber) {

        if (gameModel.getWinThreshold() > gameModel.getNumberOfRows()) {
            return;
        }

        for (int i = 0; i < gameModel.getWinThreshold() - 1; i++) {
            int newrowNumber = rowNumber + i;
            if ((newrowNumber + 1) >= gameModel.getNumberOfRows()){
                return;
            }
            if (gameModel.getCellOwner(newrowNumber, columnNumber) != gameModel.getCellOwner(newrowNumber+1, columnNumber)) {
                return;
            }
        }
        gameModel.setWinner(gameModel.getCellOwner(rowNumber, columnNumber));
        gameOver = true;
    }

    private void checkAcrossRight(int rowNumber, int columnNumber) {

        for (int i = 0; i < gameModel.getWinThreshold()-1; i++) {
            int newrowNumber = rowNumber + i;
            int newcolumnNumber = columnNumber + i;
            if ((newrowNumber + 1) >= gameModel.getNumberOfRows()){
                return;
            }
            if ((newcolumnNumber + 1) >= gameModel.getNumberOfColumns()){
                return;
            }
            if (gameModel.getCellOwner(newrowNumber, newcolumnNumber) != gameModel.getCellOwner(newrowNumber + 1, newcolumnNumber+1)) {
                return;
            }
        }

        gameModel.setWinner(gameModel.getCellOwner(rowNumber, columnNumber));
        gameOver = true;
    }

    private void checkAcrossLeft(int rowNumber, int columnNumber) {

        for (int i = 0; i < gameModel.getWinThreshold()-1; i++) {
            int newrowNumber = rowNumber - i;
            int newcolumnNumber = columnNumber + i;
            if ((newrowNumber - 1) < 0 ){
                return;
            }
            if ((newcolumnNumber + 1) >= gameModel.getNumberOfColumns()){
                return;
            }
            if (gameModel.getCellOwner(newrowNumber, newcolumnNumber) != gameModel.getCellOwner(newrowNumber - 1, newcolumnNumber+1)) {
                return;
            }
        }

        gameModel.setWinner(gameModel.getCellOwner(rowNumber, columnNumber  ));
        gameOver = true;
    }




    private void nextPlayer() {
        this.nowPlaying = nowPlaying + 1;

        if (this.nowPlaying >= gameModel.getNumberOfPlayers()){
            this.nowPlaying = 0;
        }

        gameModel.setCurrentPlayer(gameModel.getPlayerByNumber(this.nowPlaying));
    }

    private void putIn(int rowNumber, int columnNumber) throws OXOMoveException {

        if (gameModel.getCellOwner(rowNumber, columnNumber).getPlayingLetter() == ' ') {
            gameModel.setCellOwner(rowNumber, columnNumber, gameModel.getCurrentPlayer());
        }
        else {
            throw new CellAlreadyTakenException(rowNumber, columnNumber);
        }
    }

    private void lengthOfCommand(String command) throws OXOMoveException {

        if (command.length() > 2) {
            throw new InvalidIdentifierLengthException(command.length());
        }
    }

    private void firstCharLimit(String command) throws OXOMoveException {

        char firstChar = Character.toLowerCase(command.charAt(0));

        if (!Character.isLetter(firstChar)) {
            throw new InvalidIdentifierCharacterException(firstChar, "R");
        }
    }

    private void secondCharLimit(String command) throws OXOMoveException {

        char secondChar = command.charAt(1);

        if (!Character.isDigit(secondChar)) {
            throw new InvalidIdentifierCharacterException(secondChar, "C");
        }
    }

    private int ColumnBounds(String command) throws OXOMoveException{

        int columnNumber = command.charAt(1) - 49;
        if (columnNumber > gameModel.getNumberOfColumns()-1){
            throw new OutsideCellRangeException(columnNumber, "C", gameModel.getNumberOfRows(), gameModel.getNumberOfColumns());
        }
        return columnNumber;
    }

    private int RowBounds(String command) throws OXOMoveException{

        char rowChar = Character.toLowerCase(command.charAt(0));
        int rowNumber = rowChar - 'a';

        if (rowNumber > gameModel.getNumberOfRows()-1){
            throw new OutsideCellRangeException(rowNumber, "R", gameModel.getNumberOfRows(), gameModel.getNumberOfColumns());
        }
        return rowNumber;
    }

}