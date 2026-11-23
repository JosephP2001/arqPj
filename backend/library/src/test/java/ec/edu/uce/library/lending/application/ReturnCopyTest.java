package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyNotLentException;
import ec.edu.uce.library.lending.domain.CopyReturned;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReturnCopyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final CopyId COPY = new CopyId("EJ-00456");
    private static final MemberId MEMBER = new MemberId("1700000004");

    private final InMemoryCopyRepository copies = new InMemoryCopyRepository();
    private final List<Object> published = new ArrayList<>();
    private final Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private final ReturnCopy returnCopy = new ReturnCopy(copies, published::add, clock);

    @Test
    void br02_returnsTheCopySavesItAvailableAndPublishesCopyReturned() {
        Copy lent = new Copy(COPY);
        lent.lend(MEMBER, new LoanPeriod(TODAY.minusDays(5), TODAY.plusDays(10)));
        copies.save(lent);

        CopyReturned returned = returnCopy.returnCopy(new ReturnCopyCommand("EJ-00456"));

        assertThat(copies.findById(COPY)).hasValueSatisfying(copy -> assertThat(copy.isAvailable()).isTrue());
        assertThat(returned).isEqualTo(new CopyReturned(COPY, MEMBER, TODAY));
        assertThat(published).containsExactly(returned);
    }

    @Test
    void returningACopyThatIsNotLentFailsAndPublishesNothing() {
        copies.save(new Copy(COPY));

        assertThatThrownBy(() -> returnCopy.returnCopy(new ReturnCopyCommand("EJ-00456")))
                .isInstanceOf(CopyNotLentException.class);
        assertThat(published).isEmpty();
    }

    @Test
    void unknownCopyFailsWithCopyNotFoundException() {
        assertThatThrownBy(() -> returnCopy.returnCopy(new ReturnCopyCommand("EJ-99999")))
                .isInstanceOf(CopyNotFoundException.class);
    }
}
