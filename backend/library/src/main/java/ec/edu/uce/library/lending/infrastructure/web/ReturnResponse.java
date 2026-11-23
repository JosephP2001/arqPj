package ec.edu.uce.library.lending.infrastructure.web;

import ec.edu.uce.library.lending.domain.CopyReturned;

import java.time.LocalDate;

record ReturnResponse(String copyCode, String memberId, LocalDate returnedOn) {

    static ReturnResponse from(CopyReturned event) {
        return new ReturnResponse(event.copyId().code(), event.memberId().value(), event.returnedOn());
    }
}
