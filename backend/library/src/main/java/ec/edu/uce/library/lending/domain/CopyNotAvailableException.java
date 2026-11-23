package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;

/**
 * Thrown when BR-01 is violated: a lent Copy cannot be lent again until it is returned.
 * Carries the due date of the active Loan, so the caller can say until when the Copy is lent.
 */
public class CopyNotAvailableException extends RuntimeException {

    private final CopyId copyId;
    private final LocalDate dueOn;

    public CopyNotAvailableException(CopyId copyId, LocalDate dueOn) {
        super("The copy is not available");
        this.copyId = copyId;
        this.dueOn = dueOn;
    }

    public CopyId copyId() {
        return copyId;
    }

    public LocalDate dueOn() {
        return dueOn;
    }
}
