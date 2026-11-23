package ec.edu.uce.library.lending.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final LoanPeriod PERIOD = new LoanPeriod(TODAY, TODAY.plusDays(15));
    private static final MemberId ANA = new MemberId("1712345678");
    private static final MemberId LUIS = new MemberId("1798765432");

    @Test
    void newCopyIsAvailable() {
        Copy copy = new Copy(new CopyId("UCE-0001"));

        assertThat(copy.isAvailable()).isTrue();
    }

    @Test
    void lendingAnAvailableCopyCreatesTheLoanAndRaisesCopyLent() {
        Copy copy = new Copy(new CopyId("UCE-0001"));

        CopyLent event = copy.lend(ANA, PERIOD);

        assertThat(copy.isAvailable()).isFalse();
        assertThat(copy.loan()).hasValueSatisfying(loan -> {
            assertThat(loan.memberId()).isEqualTo(ANA);
            assertThat(loan.period()).isEqualTo(PERIOD);
        });
        assertThat(event).isEqualTo(new CopyLent(new CopyId("UCE-0001"), ANA, PERIOD));
    }

    @Test
    void br01_lendingALentCopyThrowsCopyNotAvailableException() {
        Copy copy = new Copy(new CopyId("UCE-0001"));
        copy.lend(ANA, PERIOD);

        assertThatThrownBy(() -> copy.lend(LUIS, PERIOD))
                .isInstanceOf(CopyNotAvailableException.class)
                .hasMessage("The copy is not available");
        assertThat(copy.loan()).hasValueSatisfying(loan -> assertThat(loan.memberId()).isEqualTo(ANA));
    }

    @Test
    void br02_returningACopyLeavesItAvailable() {
        Copy copy = new Copy(new CopyId("UCE-0001"));
        copy.lend(ANA, PERIOD);

        copy.returnCopy(TODAY.plusDays(3));

        assertThat(copy.isAvailable()).isTrue();
        assertThat(copy.loan()).isEmpty();
    }

    @Test
    void br02_returningACopyRaisesCopyReturned() {
        Copy copy = new Copy(new CopyId("UCE-0001"));
        copy.lend(ANA, PERIOD);

        CopyReturned event = copy.returnCopy(TODAY.plusDays(3));

        assertThat(event).isEqualTo(new CopyReturned(new CopyId("UCE-0001"), ANA, TODAY.plusDays(3)));
    }

    @Test
    void br02_aReturnedCopyCanBeLentAgain() {
        Copy copy = new Copy(new CopyId("UCE-0001"));
        copy.lend(ANA, PERIOD);
        copy.returnCopy(TODAY.plusDays(3));

        copy.lend(LUIS, PERIOD);

        assertThat(copy.loan()).hasValueSatisfying(loan -> assertThat(loan.memberId()).isEqualTo(LUIS));
    }

    @Test
    void returningAnAvailableCopyFails() {
        Copy copy = new Copy(new CopyId("UCE-0001"));

        assertThatThrownBy(() -> copy.returnCopy(TODAY)).isInstanceOf(IllegalStateException.class);
    }
}
