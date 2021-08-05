package OXOExceptions;

public class CellAlreadyTakenException extends OXOMoveException {

    private char row;
    private int column;

    public CellAlreadyTakenException(int rowNumber, int columnNumber) {

        super();
        int rowcon = rowNumber + 97;
        row = (char)rowcon;
        column = columnNumber + 1;
    }

    @Override
    public String toString() {
        return "This cell : " + row +  column + " is already taken.";
    }
}
