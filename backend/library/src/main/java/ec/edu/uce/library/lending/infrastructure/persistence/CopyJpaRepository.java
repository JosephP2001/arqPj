package ec.edu.uce.library.lending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface CopyJpaRepository extends JpaRepository<CopyJpaEntity, String> {
}
