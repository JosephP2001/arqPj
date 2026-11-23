package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test double of the {@link CopyRepository} port, so use cases are tested without Spring.
 */
final class InMemoryCopyRepository implements CopyRepository {

    private final Map<CopyId, Copy> store = new HashMap<>();

    @Override
    public Optional<Copy> findById(CopyId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(Copy copy) {
        store.put(copy.id(), copy);
    }
}
