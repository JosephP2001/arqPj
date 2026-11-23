package ec.edu.uce.library.lending.infrastructure.persistence;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JpaCopyRepositoryConcurrencyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final LoanPeriod PERIOD = new LoanPeriod(TODAY, TODAY.plusDays(15));
    private static final MemberId MARIA = new MemberId("1712345678");
    private static final MemberId LUIS = new MemberId("1798765432");

    @Autowired
    private CopyRepository copies;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void br01_whenTwoLibrariansLendTheSameCopyAtOnceTheSecondCommitFails() {
        CopyId copyId = new CopyId("EJ-90001");
        copies.save(new Copy(copyId));

        TransactionTemplate firstLibrarian = new TransactionTemplate(transactionManager);
        TransactionTemplate secondLibrarian = new TransactionTemplate(transactionManager);
        secondLibrarian.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        assertThatThrownBy(() -> firstLibrarian.executeWithoutResult(status -> {
            Copy seenAsAvailable = copies.findById(copyId).orElseThrow();

            secondLibrarian.executeWithoutResult(other -> {
                Copy copy = copies.findById(copyId).orElseThrow();
                copy.lend(MARIA, PERIOD);
                copies.save(copy);
            });

            seenAsAvailable.lend(LUIS, PERIOD);
            copies.save(seenAsAvailable);
        })).isInstanceOf(OptimisticLockingFailureException.class);

        assertThat(copies.findById(copyId)).hasValueSatisfying(copy ->
                assertThat(copy.loan()).hasValueSatisfying(loan -> assertThat(loan.memberId()).isEqualTo(MARIA)));
    }
}
