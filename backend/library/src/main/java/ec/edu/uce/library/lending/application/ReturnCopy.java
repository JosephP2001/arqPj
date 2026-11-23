package ec.edu.uce.library.lending.application;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.CopyReturned;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Use case: loads the Copy, calls {@code returnCopy}, saves it and publishes {@link CopyReturned}.
 */
public class ReturnCopy {

    private final CopyRepository copies;
    private final DomainEventPublisher events;
    private final Clock clock;

    public ReturnCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        this.copies = copies;
        this.events = events;
        this.clock = clock;
    }

    public CopyReturned returnCopy(ReturnCopyCommand command) {
        CopyId copyId = new CopyId(command.copyCode());
        Copy copy = copies.findById(copyId).orElseThrow(() -> new CopyNotFoundException(copyId));

        CopyReturned returned = copy.returnCopy(LocalDate.now(clock));

        copies.save(copy);
        events.publish(returned);
        return returned;
    }
}
