package ec.edu.uce.library.lending.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanPeriodTest {

    private static final LocalDate LENT_ON = LocalDate.of(2026, 11, 23);

    @Test
    void dueDateAfterLendDateIsValid() {
        LoanPeriod period = new LoanPeriod(LENT_ON, LocalDate.of(2026, 12, 8));

        assertThat(period.lentOn()).isEqualTo(LENT_ON);
        assertThat(period.dueOn()).isEqualTo(LocalDate.of(2026, 12, 8));
    }

    @Test
    void dueDateOnTheLendDateIsRejected() {
        assertThatThrownBy(() -> new LoanPeriod(LENT_ON, LENT_ON))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void dueDateBeforeTheLendDateIsRejected() {
        assertThatThrownBy(() -> new LoanPeriod(LENT_ON, LENT_ON.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
