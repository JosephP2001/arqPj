package ec.edu.uce.library.lending.domain;

/**
 * Domain event: a copy was lent to a member.
 */
public record CopyLent(CopyId copyId, MemberId memberId, LoanPeriod loanPeriod) {
}
