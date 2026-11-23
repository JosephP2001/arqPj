package ec.edu.uce.library.lending.domain;

import java.util.Objects;

/**
 * Active Loan of a {@link Copy}: who has it and for which period. Internal entity of the Copy aggregate.
 */
public final class Loan {

    private final MemberId memberId;
    private final LoanPeriod period;

    Loan(MemberId memberId, LoanPeriod period) {
        this.memberId = Objects.requireNonNull(memberId, "memberId must not be null");
        this.period = Objects.requireNonNull(period, "period must not be null");
    }

    public MemberId memberId() {
        return memberId;
    }

    public LoanPeriod period() {
        return period;
    }
}
