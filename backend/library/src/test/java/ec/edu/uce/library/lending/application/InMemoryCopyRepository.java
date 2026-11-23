package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryCopyRepository implements CopyRepository {

    private final Map<CopyId, Copy> copies = new HashMap<>();
    int saves;

    @Override
    public Optional<Copy> findById(CopyId id) {
        return Optional.ofNullable(copies.get(id));
    }

    @Override
    public void save(Copy copy) {
        copies.put(copy.id(), copy);
        saves++;
    }

    void add(Copy copy) {
        copies.put(copy.id(), copy);
    }
}
