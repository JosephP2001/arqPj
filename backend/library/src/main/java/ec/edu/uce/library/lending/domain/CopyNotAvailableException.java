package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;

/**
 * BR-01: a lent copy cannot be lent again until it is returned.
 * Carries the due date of the active loan so the UI can say until when the copy is lent.
 */
public class CopyNotAvailableException extends RuntimeException {

    private final CopyId copyId;
    private final LocalDate dueOn;

    public CopyNotAvailableException(CopyId copyId, LocalDate dueOn) {
        super("Copy " + copyId.code() + " is not available: it is lent until " + dueOn);
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
