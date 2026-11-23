package ec.edu.uce.library.lending.infrastructure.web;

import ec.edu.uce.library.lending.application.LendCopy;
import ec.edu.uce.library.lending.application.LendCopyCommand;
import ec.edu.uce.library.lending.application.ReturnCopy;
import ec.edu.uce.library.lending.application.ReturnCopyCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
class LoanController {

    private final LendCopy lendCopy;
    private final ReturnCopy returnCopy;

    LoanController(LendCopy lendCopy, ReturnCopy returnCopy) {
        this.lendCopy = lendCopy;
        this.returnCopy = returnCopy;
    }

    /**
     * Runs the use case in one transaction, so the optimistic lock of the Copy is checked on commit.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    LoanResponse lend(@Valid @RequestBody LendCopyRequest request) {
        var lent = lendCopy.lend(new LendCopyCommand(request.copyCode(), request.memberId(), request.dueDate()));
        return LoanResponse.from(lent);
    }

    /**
     * Receives a lent Copy back (BR-02). 409 if the Copy has no active Loan, 404 if it does not exist.
     */
    @PostMapping("/{copyCode}/return")
    @Transactional
    ReturnResponse returnCopy(@PathVariable String copyCode) {
        return ReturnResponse.from(returnCopy.returnCopy(new ReturnCopyCommand(copyCode)));
    }
}
