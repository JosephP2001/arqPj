package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Lend date and due date of a loan. Invariant: {@code dueOn} is after {@code lentOn}.
 */
public record LoanPeriod(LocalDate lentOn, LocalDate dueOn) {

    public LoanPeriod {
        Objects.requireNonNull(lentOn, "lentOn must not be null");
        Objects.requireNonNull(dueOn, "dueOn must not be null");
        if (!dueOn.isAfter(lentOn)) {
            throw new IllegalArgumentException("Due date must be after the lend date");
        }
    }
}
