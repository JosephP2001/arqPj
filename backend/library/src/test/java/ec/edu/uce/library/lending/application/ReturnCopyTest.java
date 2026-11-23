package ec.edu.uce.library.lending.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyNotLentException;
import ec.edu.uce.library.lending.domain.CopyReturned;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnCopyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 28);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));

    private InMemoryCopyRepository copies;
    private RecordingEventPublisher events;
    private ReturnCopy returnCopy;

    @BeforeEach
    void setUp() {
        copies = new InMemoryCopyRepository();
        events = new RecordingEventPublisher();
        returnCopy = new ReturnCopy(copies, events, CLOCK);
    }

    @Test
    void br02_returnsTheCopySavesItAvailableAndPublishesCopyReturned() {
        MemberId maria = new MemberId("1712345678");
        copies.add(Copy.lent(new CopyId("EJ-02021"), maria,
                new LoanPeriod(LocalDate.of(2026, 11, 20), LocalDate.of(2026, 12, 5))));

        CopyReturned event = returnCopy.returnCopy(new ReturnCopyCommand("EJ-02021"));

        assertThat(event).isEqualTo(new CopyReturned(new CopyId("EJ-02021"), maria, TODAY));
        assertThat(copies.saves).isEqualTo(1);
        assertThat(copies.findById(new CopyId("EJ-02021"))).hasValueSatisfying(c -> assertThat(c.isAvailable()).isTrue());
        assertThat(events.published).containsExactly(event);
    }

    @Test
    void returningACopyThatIsNotLentFailsAndPublishesNothing() {
        copies.add(new Copy(new CopyId("EJ-00123")));

        assertThatThrownBy(() -> returnCopy.returnCopy(new ReturnCopyCommand("EJ-00123")))
                .isInstanceOf(CopyNotLentException.class);
        assertThat(copies.saves).isZero();
        assertThat(events.published).isEmpty();
    }

    @Test
    void unknownCopyFailsWithCopyNotFoundException() {
        assertThatThrownBy(() -> returnCopy.returnCopy(new ReturnCopyCommand("EJ-99999")))
                .isInstanceOf(CopyNotFoundException.class);
        assertThat(events.published).isEmpty();
    }
}
