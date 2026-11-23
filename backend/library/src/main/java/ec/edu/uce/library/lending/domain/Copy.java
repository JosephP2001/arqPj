package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate root: a physical copy of a Book, identified by its inventory code.
 * <p>
 * Protects BR-01 (a lent Copy cannot be lent again until it is returned)
 * and BR-02 (returning a Copy makes it Available).
 */
public class Copy {

    private final CopyId id;
    private Loan loan;

    public Copy(CopyId id) {
        this(id, null);
    }

    private Copy(CopyId id, Loan loan) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.loan = loan;
    }

    /**
     * Rebuilds a Copy from persistence. {@code memberId} and {@code period} are both null when the Copy is Available.
     */
    public static Copy rehydrate(CopyId id, MemberId memberId, LoanPeriod period) {
        Loan loan = memberId == null ? null : new Loan(memberId, period);
        return new Copy(id, loan);
    }

    /**
     * Lends this Copy to a Member. BR-01: fails if the Copy is already lent.
     */
    public CopyLent lend(MemberId memberId, LoanPeriod period) {
        if (!isAvailable()) {
            throw new CopyNotAvailableException(id, loan.period().dueOn());
        }
        this.loan = new Loan(memberId, period);
        return new CopyLent(id, memberId, period);
    }

    /**
     * Receives this Copy back. BR-02: afterwards the Copy is Available.
     */
    public CopyReturned returnCopy(LocalDate returnedOn) {
        if (isAvailable()) {
            throw new IllegalStateException("The copy is not lent");
        }
        MemberId memberId = loan.memberId();
        this.loan = null;
        return new CopyReturned(id, memberId, returnedOn);
    }

    public boolean isAvailable() {
        return loan == null;
    }

    public CopyId id() {
        return id;
    }

    public Optional<Loan> loan() {
        return Optional.ofNullable(loan);
    }
}
