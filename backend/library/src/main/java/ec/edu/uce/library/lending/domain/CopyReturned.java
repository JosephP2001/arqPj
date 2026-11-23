package ec.edu.uce.library.lending.domain;

import java.time.LocalDate;

/**
 * Domain event: a lent Copy was returned.
 */
public record CopyReturned(CopyId copyId, MemberId memberId, LocalDate returnedOn) {
}
