package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyLent;
import ec.edu.uce.library.lending.domain.CopyNotAvailableException;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LendCopyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);

    private final InMemoryCopyRepository copies = new InMemoryCopyRepository();
    private final List<Object> published = new ArrayList<>();
    private final Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private final LendCopy lendCopy = new LendCopy(copies, published::add, clock);

    @BeforeEach
    void setUp() {
        copies.save(new Copy(new CopyId("UCE-0001")));
    }

    @Test
    void lendsTheCopySavesItAndPublishesCopyLent() {
        CopyLent lent = lendCopy.lend(new LendCopyCommand("UCE-0001", "1712345678", TODAY.plusDays(15)));

        assertThat(copies.findById(new CopyId("UCE-0001"))).hasValueSatisfying(copy -> assertThat(copy.isAvailable()).isFalse());
        assertThat(lent).isEqualTo(new CopyLent(new CopyId("UCE-0001"), new MemberId("1712345678"),
                new LoanPeriod(TODAY, TODAY.plusDays(15))));
        assertThat(published).containsExactly(lent);
    }

    @Test
    void br01_lendingALentCopyFailsAndPublishesNothing() {
        lendCopy.lend(new LendCopyCommand("UCE-0001", "1712345678", TODAY.plusDays(15)));
        published.clear();

        assertThatThrownBy(() -> lendCopy.lend(new LendCopyCommand("UCE-0001", "1798765432", TODAY.plusDays(20))))
                .isInstanceOf(CopyNotAvailableException.class)
                .extracting(e -> ((CopyNotAvailableException) e).dueOn())
                .isEqualTo(TODAY.plusDays(15));
        assertThat(published).isEmpty();
    }

    @Test
    void unknownCopyFailsWithCopyNotFoundException() {
        assertThatThrownBy(() -> lendCopy.lend(new LendCopyCommand("UCE-9999", "1712345678", TODAY.plusDays(15))))
                .isInstanceOf(CopyNotFoundException.class);
    }
}
