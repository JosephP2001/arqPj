package ec.edu.uce.library.lending.domain;

import java.util.Objects;

/**
 * Identifier of a Member. Lending only keeps the identifier; the Member lives in the Members context.
 */
public record MemberId(String value) {

    public MemberId {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
