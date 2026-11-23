package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.CopyReturned;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Use case: return a lent copy. BR-02 is protected by {@link Copy#returnCopy}.
 */
public class ReturnCopy {

    private final CopyRepository copies;
    private final DomainEventPublisher events;
    private final Clock clock;

    public ReturnCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        this.copies = Objects.requireNonNull(copies);
        this.events = Objects.requireNonNull(events);
        this.clock = Objects.requireNonNull(clock);
    }

    public CopyReturned returnCopy(ReturnCopyCommand command) {
        CopyId copyId = new CopyId(command.copyCode());
        Copy copy = copies.findById(copyId).orElseThrow(() -> new CopyNotFoundException(copyId));

        CopyReturned event = copy.returnCopy(LocalDate.now(clock));

        copies.save(copy);
        events.publish(event);
        return event;
    }
}
