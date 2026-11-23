package ec.edu.uce.library.lending.infrastructure.web;

import ec.edu.uce.library.lending.domain.CopyLent;

import java.time.LocalDate;

record LoanResponse(String copyCode, String memberId, LocalDate lentOn, LocalDate dueDate) {

    static LoanResponse from(CopyLent event) {
        return new LoanResponse(event.copyId().code(), event.memberId().value(),
                event.loanPeriod().lentOn(), event.loanPeriod().dueOn());
    }
}
