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
 * Loads sample copies at startup: UCE-0001..UCE-0003 Available and UCE-0004 already lent.
 */
@Component
class SampleCopiesLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleCopiesLoader.class);

    private final CopyRepository copies;
    private final Clock clock;

    SampleCopiesLoader(CopyRepository copies, Clock clock) {
        this.copies = copies;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        copies.save(new Copy(new CopyId("UCE-0001")));
        copies.save(new Copy(new CopyId("UCE-0002")));
        copies.save(new Copy(new CopyId("UCE-0003")));

        Copy lent = new Copy(new CopyId("UCE-0004"));
        LocalDate today = LocalDate.now(clock);
        lent.lend(new MemberId("1700000004"), new LoanPeriod(today.minusDays(5), today.plusDays(10)));
        copies.save(lent);

        log.info("Sample copies loaded: UCE-0001, UCE-0002, UCE-0003 (Available), UCE-0004 (lent)");
    }
}
