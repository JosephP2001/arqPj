package ec.edu.uce.library.lending.domain;

/**
 * Identifies a member inside Lending (national id or member code). Lending keeps only the identifier.
 */
public record MemberId(String value) {

    public MemberId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Member id must not be blank");
        }
    }
}
