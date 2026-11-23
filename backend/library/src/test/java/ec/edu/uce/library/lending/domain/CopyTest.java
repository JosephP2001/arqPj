package ec.edu.uce.library.lending.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CopyTest {

    private static final MemberId MARIA = new MemberId("1712345678");
    private static final MemberId OTHER_MEMBER = new MemberId("1700000004");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);

    private static LoanPeriod periodUntil(LocalDate dueOn) {
        return new LoanPeriod(TODAY, dueOn);
    }

    @Test
    void aNewCopyIsAvailable() {
        Copy copy = new Copy(new CopyId("EJ-00123"));

        assertThat(copy.isAvailable()).isTrue();
    }

    @Test
    void br01_lendingAnAvailableCopyRaisesCopyLentAndLeavesItNotAvailable() {
        Copy copy = new Copy(new CopyId("EJ-00123"));
        LoanPeriod period = periodUntil(LocalDate.of(2026, 12, 8));

        CopyLent event = copy.lend(MARIA, period);

        assertThat(copy.isAvailable()).isFalse();
        assertThat(event).isEqualTo(new CopyLent(new CopyId("EJ-00123"), MARIA, period));
        assertThat(copy.loan()).hasValueSatisfying(loan -> {
            assertThat(loan.memberId()).isEqualTo(MARIA);
            assertThat(loan.period()).isEqualTo(period);
        });
    }

    @Test
    void br01_lendingALentCopyThrowsCopyNotAvailableException() {
        LocalDate dueOn = LocalDate.of(2026, 12, 10);
        Copy copy = Copy.lent(new CopyId("EJ-00456"), OTHER_MEMBER, periodUntil(dueOn));

        assertThatThrownBy(() -> copy.lend(MARIA, periodUntil(LocalDate.of(2026, 12, 8))))
                .isInstanceOf(CopyNotAvailableException.class)
                .extracting(e -> ((CopyNotAvailableException) e).dueOn())
                .isEqualTo(dueOn);
        assertThat(copy.loan()).hasValueSatisfying(loan -> assertThat(loan.memberId()).isEqualTo(OTHER_MEMBER));
    }

    @Test
    void br02_returningACopyLeavesItAvailable() {
        Copy copy = Copy.lent(new CopyId("EJ-02021"), MARIA, periodUntil(LocalDate.of(2026, 12, 8)));

        copy.returnCopy(TODAY.plusDays(5));

        assertThat(copy.isAvailable()).isTrue();
        assertThat(copy.loan()).isEmpty();
    }

    @Test
    void br02_returningACopyRaisesCopyReturned() {
        Copy copy = Copy.lent(new CopyId("EJ-02021"), MARIA, periodUntil(LocalDate.of(2026, 12, 8)));
        LocalDate returnedOn = TODAY.plusDays(5);

        CopyReturned event = copy.returnCopy(returnedOn);

        assertThat(event).isEqualTo(new CopyReturned(new CopyId("EJ-02021"), MARIA, returnedOn));
    }

    @Test
    void br02_aReturnedCopyCanBeLentAgain() {
        Copy copy = Copy.lent(new CopyId("EJ-02021"), MARIA, periodUntil(LocalDate.of(2026, 12, 8)));
        copy.returnCopy(TODAY.plusDays(5));

        CopyLent event = copy.lend(OTHER_MEMBER, periodUntil(LocalDate.of(2026, 12, 20)));

        assertThat(event.memberId()).isEqualTo(OTHER_MEMBER);
        assertThat(copy.isAvailable()).isFalse();
    }

    @Test
    void returningACopyThatIsNotLentThrowsCopyNotLentException() {
        Copy copy = new Copy(new CopyId("EJ-00123"));

        assertThatThrownBy(() -> copy.returnCopy(TODAY))
                .isInstanceOf(CopyNotLentException.class);
        assertThat(copy.isAvailable()).isTrue();
    }
}
