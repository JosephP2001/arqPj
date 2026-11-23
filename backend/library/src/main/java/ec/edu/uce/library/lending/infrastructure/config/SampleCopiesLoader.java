package ec.edu.uce.library.lending.infrastructure.config;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Loads the sample copies of the Figma screens at startup: EJ-00123 and EJ-00124 Available,
 * and EJ-00456 lent to Member 1700000004 until 10/12/2026 (today + 15 days once that date has passed).
 */
@Component
class SampleCopiesLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleCopiesLoader.class);

    private static final LocalDate FIGMA_DUE_DATE = LocalDate.of(2026, 12, 10);
    private static final int DEFAULT_LOAN_DAYS = 15;

    private final CopyRepository copies;
    private final Clock clock;

    SampleCopiesLoader(CopyRepository copies, Clock clock) {
        this.copies = copies;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        copies.save(new Copy(new CopyId("EJ-00123")));
        copies.save(new Copy(new CopyId("EJ-00124")));

        LocalDate today = LocalDate.now(clock);
        LocalDate dueOn = FIGMA_DUE_DATE.isBefore(today) ? today.plusDays(DEFAULT_LOAN_DAYS) : FIGMA_DUE_DATE;
        Copy lent = new Copy(new CopyId("EJ-00456"));
        lent.lend(new MemberId("1700000004"), new LoanPeriod(today.minusDays(5), dueOn));
        copies.save(lent);

        log.info("Sample copies loaded: EJ-00123, EJ-00124 (Available), EJ-00456 (lent until {})", dueOn);
    }
}
