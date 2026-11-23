package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.CopyId;

/**
 * Thrown when no Copy exists with the given inventory code.
 */
public class CopyNotFoundException extends RuntimeException {

    public CopyNotFoundException(CopyId copyId) {
        super("Copy " + copyId + " does not exist");
    }
}
