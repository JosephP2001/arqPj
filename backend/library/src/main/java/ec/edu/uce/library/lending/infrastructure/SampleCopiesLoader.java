package ec.edu.uce.library.lending.infrastructure;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Loads the sample copies of the acceptance scenarios (Confluence page 14) into the in-memory database.
 */
@Component
class SampleCopiesLoader implements CommandLineRunner {

    private final CopyRepository copies;

    SampleCopiesLoader(CopyRepository copies) {
        this.copies = copies;
    }

    @Override
    public void run(String... args) {
        saveIfMissing(new Copy(new CopyId("EJ-00123")));
        saveIfMissing(Copy.lent(new CopyId("EJ-00456"), new MemberId("1700000004"),
                new LoanPeriod(LocalDate.of(2026, 11, 25), LocalDate.of(2026, 12, 10))));
        saveIfMissing(Copy.lent(new CopyId("EJ-02021"), new MemberId("1712345678"),
                new LoanPeriod(LocalDate.of(2026, 11, 20), LocalDate.of(2026, 12, 5))));
    }

    private void saveIfMissing(Copy copy) {
        if (copies.findById(copy.id()).isEmpty()) {
            copies.save(copy);
        }
    }
}
