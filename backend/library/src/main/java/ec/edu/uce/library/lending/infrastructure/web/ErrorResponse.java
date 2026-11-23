package ec.edu.uce.library.lending.infrastructure.web;

import java.time.LocalDate;

/**
 * Error body of the lending API. {@code dueDate} is only set for BR-01 (409): the due date of the active Loan.
 */
record ErrorResponse(String message, LocalDate dueDate) {

    static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }
}
