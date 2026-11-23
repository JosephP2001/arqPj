package ec.edu.uce.library.lending.infrastructure;

import ec.edu.uce.library.lending.application.DomainEventPublisher;
import ec.edu.uce.library.lending.application.LendCopy;
import ec.edu.uce.library.lending.application.ReturnCopy;
import ec.edu.uce.library.lending.domain.CopyRepository;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the Lending use cases as beans, so the application layer stays free of Spring.
 */
@Configuration
class LendingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    LendCopy lendCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        return new LendCopy(copies, events, clock);
    }

    @Bean
    ReturnCopy returnCopy(CopyRepository copies, DomainEventPublisher events, Clock clock) {
        return new ReturnCopy(copies, events, clock);
    }
}
