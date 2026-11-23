package ec.edu.uce.library.lending.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyLent;
import ec.edu.uce.library.lending.domain.CopyNotAvailableException;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LendCopyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));

    private InMemoryCopyRepository copies;
    private RecordingEventPublisher events;
    private LendCopy lendCopy;

    @BeforeEach
    void setUp() {
        copies = new InMemoryCopyRepository();
        events = new RecordingEventPublisher();
        lendCopy = new LendCopy(copies, events, CLOCK);
    }

    @Test
    void lendsTheCopySavesItAndPublishesCopyLent() {
        copies.add(new Copy(new CopyId("EJ-00123")));
        LocalDate dueDate = LocalDate.of(2026, 12, 8);

        CopyLent event = lendCopy.lend(new LendCopyCommand("EJ-00123", "1712345678", dueDate));

        assertThat(event).isEqualTo(new CopyLent(
                new CopyId("EJ-00123"), new MemberId("1712345678"), new LoanPeriod(TODAY, dueDate)));
        assertThat(copies.saves).isEqualTo(1);
        assertThat(copies.findById(new CopyId("EJ-00123"))).hasValueSatisfying(c -> assertThat(c.isAvailable()).isFalse());
        assertThat(events.published).containsExactly(event);
    }

    @Test
    void br01_lendingALentCopyFailsAndPublishesNothing() {
        copies.add(Copy.lent(new CopyId("EJ-00456"), new MemberId("1700000004"),
                new LoanPeriod(TODAY, LocalDate.of(2026, 12, 10))));

        assertThatThrownBy(() -> lendCopy.lend(
                new LendCopyCommand("EJ-00456", "1712345678", LocalDate.of(2026, 12, 8))))
                .isInstanceOf(CopyNotAvailableException.class);
        assertThat(copies.saves).isZero();
        assertThat(events.published).isEmpty();
    }

    @Test
    void unknownCopyFailsWithCopyNotFoundException() {
        assertThatThrownBy(() -> lendCopy.lend(
                new LendCopyCommand("EJ-99999", "1712345678", LocalDate.of(2026, 12, 8))))
                .isInstanceOf(CopyNotFoundException.class);
        assertThat(events.published).isEmpty();
    }
}
