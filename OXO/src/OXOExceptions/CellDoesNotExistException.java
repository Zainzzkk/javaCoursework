package OXOExceptions;

public class CellDoesNotExistException extends OXOMoveException {

    public CellDoesNotExistException(){
        super();
    }

    @Override
    public String toString() {
        return "CellDoesNotExistException";
    }
}
