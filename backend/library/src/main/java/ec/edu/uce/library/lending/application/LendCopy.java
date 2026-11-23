package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyLent;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Use case: lend a copy to a member. BR-01 is protected by {@link Copy#lend}.
 */
public class LendCopy {

    private final CopyRepository copies;
    private final DomainEventPublisher events;
    private final Clock clock;

    public LendCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        this.copies = Objects.requireNonNull(copies);
        this.events = Objects.requireNonNull(events);
        this.clock = Objects.requireNonNull(clock);
    }

    public CopyLent lend(LendCopyCommand command) {
        CopyId copyId = new CopyId(command.copyCode());
        Copy copy = copies.findById(copyId).orElseThrow(() -> new CopyNotFoundException(copyId));

        LoanPeriod period = new LoanPeriod(LocalDate.now(clock), command.dueDate());
        CopyLent event = copy.lend(new MemberId(command.memberId()), period);

        copies.save(copy);
        events.publish(event);
        return event;
    }
}
