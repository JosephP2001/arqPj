package ec.edu.uce.library.catalog.infrastructure.config;

import ec.edu.uce.library.catalog.application.RecordCopyReturn;
import ec.edu.uce.library.catalog.domain.CopyUsageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the Catalog use cases with their Spring adapters.
 */
@Configuration
class CatalogConfiguration {

    @Bean
    RecordCopyReturn recordCopyReturn(CopyUsageRepository usages) {
        return new RecordCopyReturn(usages);
    }
}
