package ec.edu.uce.library.lending.infrastructure;

import ec.edu.uce.library.lending.application.LendCopy;
import ec.edu.uce.library.lending.application.LendCopyCommand;
import ec.edu.uce.library.lending.application.ReturnCopy;
import ec.edu.uce.library.lending.application.ReturnCopyCommand;
import ec.edu.uce.library.lending.domain.CopyLent;
import ec.edu.uce.library.lending.domain.CopyReturned;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point of Lending: lend (BR-01) and return (BR-02) a copy.
 * Each request runs in one transaction, so a concurrent lend of the same copy fails on commit.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LendCopy lendCopy;
    private final ReturnCopy returnCopy;

    LoanController(LendCopy lendCopy, ReturnCopy returnCopy) {
        this.lendCopy = lendCopy;
        this.returnCopy = returnCopy;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public LoanResponse lend(@Valid @RequestBody LendCopyRequest request) {
        CopyLent event = lendCopy.lend(new LendCopyCommand(request.copyCode(), request.memberId(), request.dueDate()));
        return new LoanResponse(event.copyId().code(), event.memberId().value(),
                event.loanPeriod().lentOn(), event.loanPeriod().dueOn());
    }

    @PostMapping("/{copyCode}/return")
    @Transactional
    public ReturnResponse returnCopy(@PathVariable String copyCode) {
        CopyReturned event = returnCopy.returnCopy(new ReturnCopyCommand(copyCode));
        return new ReturnResponse(event.copyId().code(), event.memberId().value(), event.returnedOn());
    }

    public record LendCopyRequest(@NotBlank String copyCode, @NotBlank String memberId, @NotNull LocalDate dueDate) {
    }

    public record LoanResponse(String copyCode, String memberId, LocalDate lentOn, LocalDate dueOn) {
    }

    public record ReturnResponse(String copyCode, String memberId, LocalDate returnedOn) {
    }
}
