package ec.edu.uce.library.lending.application;

/**
 * Port to publish domain events after the aggregate has been saved. Implemented in infrastructure.
 */
public interface DomainEventPublisher {

    void publish(Object event);
}
