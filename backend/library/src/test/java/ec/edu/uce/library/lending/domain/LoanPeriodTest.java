package ec.edu.uce.library.lending.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanPeriodTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);

    @Test
    void dueDateAfterLendDateIsValid() {
        assertThatCode(() -> new LoanPeriod(TODAY, TODAY.plusDays(1))).doesNotThrowAnyException();
    }

    @Test
    void dueDateOnTheLendDateIsRejected() {
        assertThatThrownBy(() -> new LoanPeriod(TODAY, TODAY)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void dueDateBeforeTheLendDateIsRejected() {
        assertThatThrownBy(() -> new LoanPeriod(TODAY, TODAY.minusDays(1))).isInstanceOf(IllegalArgumentException.class);
    }
}
