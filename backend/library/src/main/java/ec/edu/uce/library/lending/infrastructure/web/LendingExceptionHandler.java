package ec.edu.uce.library.lending.infrastructure.web;

import ec.edu.uce.library.lending.application.CopyNotFoundException;
import ec.edu.uce.library.lending.domain.CopyNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = LoanController.class)
class LendingExceptionHandler {

    @ExceptionHandler(CopyNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse copyNotAvailable(CopyNotAvailableException e) {
        return new ErrorResponse(e.getMessage(), e.dueOn());
    }

    @ExceptionHandler(CopyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse copyNotFound(CopyNotFoundException e) {
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse invalidArgument(IllegalArgumentException e) {
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse invalidRequest(MethodArgumentNotValidException e) {
        return ErrorResponse.of("Copy code, Member ID and Due date are required");
    }
}
