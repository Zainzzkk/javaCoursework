package OXOExceptions;

public class InvalidIdentifierException extends CellDoesNotExistException {

    public InvalidIdentifierException() {
        super();
    }

    @Override
    public String toString() {
        return "InvalidIdentifierException";
    }
}
