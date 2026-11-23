package ec.edu.uce.library.catalog.application;

import ec.edu.uce.library.catalog.domain.CopyId;
import ec.edu.uce.library.catalog.domain.CopyUsage;
import ec.edu.uce.library.catalog.domain.CopyUsageRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RecordCopyReturnTest {

    private final InMemoryCopyUsageRepository usages = new InMemoryCopyUsageRepository();
    private final RecordCopyReturn recordCopyReturn = new RecordCopyReturn(usages);

    @Test
    void theFirstReturnOfACopyStartsItsUsage() {
        CopyUsage usage = recordCopyReturn.record("EJ-00123");

        assertThat(usage.timesReturned()).isEqualTo(1);
        assertThat(usages.findById(new CopyId("EJ-00123"))).hasValueSatisfying(saved ->
                assertThat(saved.timesReturned()).isEqualTo(1));
    }

    @Test
    void eachReturnIsAdded() {
        recordCopyReturn.record("EJ-00123");
        recordCopyReturn.record("EJ-00123");

        assertThat(usages.findById(new CopyId("EJ-00123"))).hasValueSatisfying(saved ->
                assertThat(saved.timesReturned()).isEqualTo(2));
    }

    private static final class InMemoryCopyUsageRepository implements CopyUsageRepository {

        private final Map<CopyId, Integer> timesReturned = new HashMap<>();

        @Override
        public Optional<CopyUsage> findById(CopyId copyId) {
            return Optional.ofNullable(timesReturned.get(copyId)).map(times -> CopyUsage.rehydrate(copyId, times));
        }

        @Override
        public void save(CopyUsage usage) {
            timesReturned.put(usage.copyId(), usage.timesReturned());
        }
    }
}
