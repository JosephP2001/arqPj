package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.CopyId;

/**
 * Thrown by a use case when no copy exists with the given code.
 */
public class CopyNotFoundException extends RuntimeException {

    private final CopyId copyId;

    public CopyNotFoundException(CopyId copyId) {
        super("Copy " + copyId.code() + " was not found");
        this.copyId = copyId;
    }

    public CopyId copyId() {
        return copyId;
    }
}
