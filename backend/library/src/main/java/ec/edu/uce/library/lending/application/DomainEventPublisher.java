package ec.edu.uce.library.lending.application;

/**
 * Port to publish domain events (e.g. CopyLent). Implemented in infrastructure.
 */
public interface DomainEventPublisher {

    void publish(Object event);
}
