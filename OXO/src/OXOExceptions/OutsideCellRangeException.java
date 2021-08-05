package OXOExceptions;

public class OutsideCellRangeException extends CellDoesNotExistException {

    private int position, column;
    private char row;
    private RowOrColumn type;

    public OutsideCellRangeException (int input, String whattype, int rowLimit, int columnLimit) {
        super();

        position = input+1;
        column = columnLimit;
        int rowcon = rowLimit + 96;
        row = (char)rowcon;

        if (whattype.equals("R")){
            type = RowOrColumn.ROW;
        }
        if (whattype.equals("C")){
            type = RowOrColumn.COLUMN;
        }
    }

    @Override
    public String toString() {
        return "Your input: " +
                "position=" + position +
                ", type=" + type +
                " is outside the range of cells. " + "The range is: "
                + "row: a to " +  row + " column: 1 to " + column;
    }
}
