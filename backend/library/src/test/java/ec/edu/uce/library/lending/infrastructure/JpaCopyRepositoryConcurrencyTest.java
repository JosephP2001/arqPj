package ec.edu.uce.library.lending.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
class JpaCopyRepositoryConcurrencyTest {

    @Autowired
    private CopyRepository copies;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void br01_whenTwoLibrariansLendTheSameCopyAtOnceTheSecondCommitFails() {
        CopyId copyId = new CopyId("EJ-90001");
        copies.save(new Copy(copyId));
        LoanPeriod period = new LoanPeriod(LocalDate.now(), LocalDate.now().plusDays(15));
        MemberId first = new MemberId("1700000004");
        MemberId second = new MemberId("1712345678");

        TransactionTemplate newTransaction = new TransactionTemplate(transactionManager);
        newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        assertThatThrownBy(() -> newTransaction.executeWithoutResult(slow -> {
            Copy seenBySlowLibrarian = copies.findById(copyId).orElseThrow();

            newTransaction.executeWithoutResult(fast -> {
                Copy seenByFastLibrarian = copies.findById(copyId).orElseThrow();
                seenByFastLibrarian.lend(first, period);
                copies.save(seenByFastLibrarian);
            });

            seenBySlowLibrarian.lend(second, period);
            copies.save(seenBySlowLibrarian);
        })).isInstanceOf(OptimisticLockingFailureException.class);

        assertThat(copies.findById(copyId)).hasValueSatisfying(copy ->
                assertThat(copy.loan()).hasValueSatisfying(loan -> assertThat(loan.memberId()).isEqualTo(first)));
    }
}
