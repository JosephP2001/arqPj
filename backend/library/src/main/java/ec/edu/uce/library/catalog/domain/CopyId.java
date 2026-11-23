package ec.edu.uce.library.catalog.domain;

import java.util.Objects;

/**
 * Identifier of a Copy inside Catalog. Catalog only keeps the identifier; the Copy aggregate lives in Lending.
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
