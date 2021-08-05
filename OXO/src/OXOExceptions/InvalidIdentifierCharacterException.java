package OXOExceptions;

public class InvalidIdentifierCharacterException extends InvalidIdentifierException {

    private char character;
    private RowOrColumn type;

    public InvalidIdentifierCharacterException(char input, String whattype) {

        super();
        character = input;

        if (whattype.equals("R")){
            type = RowOrColumn.ROW;
        }
        if (whattype.equals("C")){
            type = RowOrColumn.COLUMN;
        }

    }

    @Override
    public String toString() {
        return "Invalid character entered! " +
                "character: " + character + " for" +
                ", type " + type + " should be between a-z for Rows and 1-9 for columns";
    }
}
