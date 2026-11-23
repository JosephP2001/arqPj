package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;

/**
 * Domain event: a lent copy was returned and is available again.
 */
public record CopyReturned(CopyId copyId, MemberId memberId, LocalDate returnedOn) {
}
