package ec.edu.uce.library.lending.infrastructure;

import ec.edu.uce.library.lending.domain.Copy;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import ec.edu.uce.library.lending.domain.LoanPeriod;
import ec.edu.uce.library.lending.domain.MemberId;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA adapter of the {@link CopyRepository} port. Maps between {@link CopyJpaEntity} and {@link Copy}.
 * <p>
 * {@code save} updates the entity loaded in the same transaction, so the version read by {@code findById}
 * is the one checked on commit.
 */
@Repository
@Transactional
class JpaCopyRepository implements CopyRepository {

    private final SpringDataCopyRepository entities;

    JpaCopyRepository(SpringDataCopyRepository entities) {
        this.entities = entities;
    }

    @Override
    public Optional<Copy> findById(CopyId id) {
        return entities.findById(id.code()).map(JpaCopyRepository::toDomain);
    }

    @Override
    public void save(Copy copy) {
        CopyJpaEntity entity = entities.findById(copy.id().code())
                .orElseGet(() -> new CopyJpaEntity(copy.id().code()));
        copy.loan().ifPresentOrElse(
                loan -> entity.setLoan(loan.memberId().value(), loan.period().lentOn(), loan.period().dueOn()),
                entity::clearLoan);
        entities.save(entity);
    }

    private static Copy toDomain(CopyJpaEntity entity) {
        CopyId id = new CopyId(entity.getCopyCode());
        if (entity.getMemberId() == null) {
            return new Copy(id);
        }
        return Copy.lent(id, new MemberId(entity.getMemberId()),
                new LoanPeriod(entity.getLentOn(), entity.getDueOn()));
    }
}
