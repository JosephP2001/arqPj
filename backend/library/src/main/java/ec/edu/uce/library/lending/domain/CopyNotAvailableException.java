package ec.edu.uce.library.lending.domain;

/**
 * Thrown when BR-01 is violated: a lent Copy cannot be lent again until it is returned.
 */
public class CopyNotAvailableException extends RuntimeException {

    private final CopyId copyId;

    public CopyNotAvailableException(CopyId copyId) {
        super("The copy is not available");
        this.copyId = copyId;
    }

    public CopyId copyId() {
        return copyId;
    }
}
