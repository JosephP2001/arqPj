package ec.edu.uce.library.lending.domain;

import java.util.Optional;

/**
 * Port to load and save {@link Copy} aggregates. Implemented in infrastructure.
 */
public interface CopyRepository {

    Optional<Copy> findById(CopyId id);

    void save(Copy copy);
}
