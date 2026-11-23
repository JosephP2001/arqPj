package ec.edu.uce.library.lending.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCopyRepository extends JpaRepository<CopyJpaEntity, String> {
}
