package ec.edu.uce.library.lending.infrastructure.persistence;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA adapter of the {@link CopyRepository} port.
 */
@Repository
class JpaCopyRepository implements CopyRepository {

    private final CopyJpaRepository jpa;

    JpaCopyRepository(CopyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Copy> findById(CopyId id) {
        return jpa.findById(id.code()).map(JpaCopyRepository::toDomain);
    }

    @Override
    public void save(Copy copy) {
        jpa.save(toEntity(copy));
    }

    private static Copy toDomain(CopyJpaEntity entity) {
        if (entity.getMemberId() == null) {
            return Copy.rehydrate(new CopyId(entity.getCode()), null, null);
        }
        return Copy.rehydrate(new CopyId(entity.getCode()), new MemberId(entity.getMemberId()),
                new LoanPeriod(entity.getLentOn(), entity.getDueOn()));
    }

    private static CopyJpaEntity toEntity(Copy copy) {
        return copy.loan()
                .map(loan -> new CopyJpaEntity(copy.id().code(), loan.memberId().value(),
                        loan.period().lentOn(), loan.period().dueOn()))
                .orElseGet(() -> new CopyJpaEntity(copy.id().code(), null, null, null));
    }
}
