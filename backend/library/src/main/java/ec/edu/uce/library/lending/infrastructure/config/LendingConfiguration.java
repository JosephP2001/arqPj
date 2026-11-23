package ec.edu.uce.library.lending.infrastructure.config;

import ec.edu.uce.library.lending.application.DomainEventPublisher;
import ec.edu.uce.library.lending.application.LendCopy;
import ec.edu.uce.library.lending.domain.CopyRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Wires the Lending use cases with their Spring adapters.
 */
@Configuration
class LendingConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    DomainEventPublisher domainEventPublisher(ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }

    @Bean
    LendCopy lendCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        return new LendCopy(copies, events, clock);
    }
}
