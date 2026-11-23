package ec.edu.uce.library.lending.domain;

/**
 * Domain event: a Copy was lent successfully.
 */
public record CopyLent(CopyId copyId, MemberId memberId, LoanPeriod loanPeriod) {
}
