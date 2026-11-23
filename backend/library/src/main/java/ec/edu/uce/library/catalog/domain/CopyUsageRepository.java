package ec.edu.uce.library.catalog.domain;

import java.util.Optional;

/**
 * Port to store the {@link CopyUsage} of each Copy. Implemented in infrastructure.
 */
public interface CopyUsageRepository {

    Optional<CopyUsage> findById(CopyId copyId);

    void save(CopyUsage usage);
}
