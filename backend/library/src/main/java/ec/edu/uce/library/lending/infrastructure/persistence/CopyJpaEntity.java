package ec.edu.uce.library.lending.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;

/**
 * Persistence model of a Copy. Loan columns are null while the Copy is Available.
 * <p>
 * {@code version} enables optimistic locking: if two librarians lend the same Copy at the same time,
 * the second commit fails instead of overwriting the first Loan (protects BR-01 under concurrency).
 */
@Entity
@Table(name = "copies")
class CopyJpaEntity {

    @Id
    @Column(name = "code", nullable = false, updatable = false)
    private String code;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "lent_on")
    private LocalDate lentOn;

    @Column(name = "due_on")
    private LocalDate dueOn;

    @Version
    @Column(name = "version")
    private Long version;

    protected CopyJpaEntity() {
    }

    CopyJpaEntity(String code) {
        this.code = code;
    }

    /**
     * Copies the active Loan of the Copy; all values are null when the Copy is Available.
     */
    void updateLoan(String memberId, LocalDate lentOn, LocalDate dueOn) {
        this.memberId = memberId;
        this.lentOn = lentOn;
        this.dueOn = dueOn;
    }

    String getCode() {
        return code;
    }

    String getMemberId() {
        return memberId;
    }

    LocalDate getLentOn() {
        return lentOn;
    }

    LocalDate getDueOn() {
        return dueOn;
    }
}
