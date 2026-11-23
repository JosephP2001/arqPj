package ec.edu.uce.library.lending.domain;

/**
 * Thrown by {@link Copy#returnCopy} when the copy has no active loan.
 */
public class CopyNotLentException extends RuntimeException {

    private final CopyId copyId;

    public CopyNotLentException(CopyId copyId) {
        super("Copy " + copyId.code() + " is not lent");
        this.copyId = copyId;
    }

    public CopyId copyId() {
        return copyId;
    }
}
