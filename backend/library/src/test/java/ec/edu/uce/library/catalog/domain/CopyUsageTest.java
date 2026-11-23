package ec.edu.uce.library.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopyUsageTest {

    @Test
    void aNewCopyUsageHasNoReturns() {
        assertThat(new CopyUsage(new CopyId("EJ-00123")).timesReturned()).isZero();
    }

    @Test
    void recordReturnAddsOneReturn() {
        CopyUsage usage = new CopyUsage(new CopyId("EJ-00123"));

        usage.recordReturn();
        usage.recordReturn();

        assertThat(usage.timesReturned()).isEqualTo(2);
    }

    @Test
    void aNegativeNumberOfReturnsIsRejected() {
        assertThatThrownBy(() -> CopyUsage.rehydrate(new CopyId("EJ-00123"), -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
