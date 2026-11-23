package ec.edu.uce.library.catalog.infrastructure.persistence;

import ec.edu.uce.library.catalog.domain.CopyId;
import ec.edu.uce.library.catalog.domain.CopyUsage;
import ec.edu.uce.library.catalog.domain.CopyUsageRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter of the {@link CopyUsageRepository} port. Enough for the demo: usage statistics are lost on restart,
 * like the H2 data of Lending.
 */
@Repository
class InMemoryCopyUsageRepository implements CopyUsageRepository {

    private final Map<CopyId, Integer> timesReturnedByCopy = new ConcurrentHashMap<>();

    @Override
    public Optional<CopyUsage> findById(CopyId copyId) {
        return Optional.ofNullable(timesReturnedByCopy.get(copyId))
                .map(timesReturned -> CopyUsage.rehydrate(copyId, timesReturned));
    }

    @Override
    public void save(CopyUsage usage) {
        timesReturnedByCopy.put(usage.copyId(), usage.timesReturned());
    }
}
