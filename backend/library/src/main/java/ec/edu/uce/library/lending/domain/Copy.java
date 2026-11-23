package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate root of Lending: a physical copy of a book.
 * <p>
 * Protects BR-01 ({@link #lend}) and BR-02 ({@link #returnCopy}). A copy has at most one active {@link Loan}.
 */
public class Copy {

    private final CopyId id;
    private Loan loan;

    /** Creates an available copy. */
    public Copy(CopyId id) {
        this(id, null);
    }

    private Copy(CopyId id, Loan loan) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.loan = loan;
    }

    /** Rebuilds a lent copy from persistence. */
    public static Copy lent(CopyId id, MemberId memberId, LoanPeriod period) {
        return new Copy(id, new Loan(memberId, period));
    }

    /**
     * BR-01: lends the copy to a member only if it is available.
     *
     * @throws CopyNotAvailableException if the copy already has an active loan
     */
    public CopyLent lend(MemberId memberId, LoanPeriod period) {
        if (!isAvailable()) {
            throw new CopyNotAvailableException(id, loan.period().dueOn());
        }
        this.loan = new Loan(memberId, period);
        return new CopyLent(id, memberId, period);
    }

    /**
     * BR-02: returning the copy leaves it available.
     *
     * @throws CopyNotLentException if the copy has no active loan
     */
    public CopyReturned returnCopy(LocalDate returnedOn) {
        Objects.requireNonNull(returnedOn, "returnedOn must not be null");
        if (isAvailable()) {
            throw new CopyNotLentException(id);
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
