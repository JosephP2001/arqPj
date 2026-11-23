package ec.edu.uce.library.lending.domain;

import java.util.Objects;

/**
 * Identity of a {@link Copy}: its inventory code.
 */
public record CopyId(String code) {

    public CopyId {
        Objects.requireNonNull(code, "code must not be null");
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        code = code.trim();
    }

    @Override
    public String toString() {
        return code;
    }
}
