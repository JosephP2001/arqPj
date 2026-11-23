package ec.edu.uce.library.lending.domain;

/**
 * Thrown when returning a Copy that has no active Loan ("Ejemplar no prestado").
 */
public class CopyNotLentException extends RuntimeException {

    private final CopyId copyId;

    public CopyNotLentException(CopyId copyId) {
        super("The copy is not lent");
        this.copyId = copyId;
    }

    public CopyId copyId() {
        return copyId;
    }
}
