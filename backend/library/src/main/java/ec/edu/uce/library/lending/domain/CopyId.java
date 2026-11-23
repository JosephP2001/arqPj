package ec.edu.uce.library.lending.domain;

/**
 * Identity of a {@link Copy}: its inventory code (e.g. EJ-00123).
 */
public record CopyId(String code) {

    public CopyId {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Copy code must not be blank");
        }
    }
}
