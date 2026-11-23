package ec.edu.uce.library.catalog.infrastructure.events;

import ec.edu.uce.library.catalog.application.RecordCopyReturn;
import ec.edu.uce.library.lending.domain.CopyReturned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Catalog reacts to the {@link CopyReturned} domain event published by Lending (Context Map, page 8).
 * Lending does not know this listener exists; Catalog only reads the event's Copy identifier.
 */
@Component
class CopyReturnedListener {

    private static final Logger log = LoggerFactory.getLogger(CopyReturnedListener.class);

    private final RecordCopyReturn recordCopyReturn;

    CopyReturnedListener(RecordCopyReturn recordCopyReturn) {
        this.recordCopyReturn = recordCopyReturn;
    }

    @EventListener
    void on(CopyReturned event) {
        var usage = recordCopyReturn.record(event.copyId().code());
        log.info("Catalog recorded the return of {} ({} times returned)", usage.copyId(), usage.timesReturned());
    }
}
