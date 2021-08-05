package OXOExceptions;

public class InvalidIdentifierLengthException extends InvalidIdentifierException {

    private int length;

    public InvalidIdentifierLengthException(int inputlength) {
        super();
        length = inputlength;
    }

    @Override
    public String toString() {
        return "Invalid length of input entered! " +
                "length:" + length +
                ". " + "Length should be 2 only!";
    }
}
