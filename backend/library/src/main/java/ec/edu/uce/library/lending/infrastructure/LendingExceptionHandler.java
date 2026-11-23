package ec.edu.uce.library.lending.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import ec.edu.uce.library.lending.application.CopyNotFoundException;
import ec.edu.uce.library.lending.domain.CopyNotAvailableException;
import ec.edu.uce.library.lending.domain.CopyNotLentException;
import java.time.LocalDate;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates Lending exceptions to HTTP. Messages stay in English; the frontend shows the Spanish text.
 */
@RestControllerAdvice(assignableTypes = LoanController.class)
class LendingExceptionHandler {

    @ExceptionHandler(CopyNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse copyNotAvailable(CopyNotAvailableException e) {
        return new ErrorResponse("The copy is not available", e.dueOn());
    }

    @ExceptionHandler(CopyNotLentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse copyNotLent(CopyNotLentException e) {
        return new ErrorResponse("The copy is not lent", null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse concurrentChange(OptimisticLockingFailureException e) {
        return new ErrorResponse("The copy was changed by another request", null);
    }

    @ExceptionHandler(CopyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse copyNotFound(CopyNotFoundException e) {
        return new ErrorResponse("The copy was not found", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse invalidInput(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage(), null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record ErrorResponse(String message, LocalDate dueDate) {
    }
}
