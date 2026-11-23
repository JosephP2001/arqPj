package ec.edu.uce.library.lending.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Persistence model of a Copy. Loan columns are null while the Copy is Available.
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

    protected CopyJpaEntity() {
    }

    CopyJpaEntity(String code, String memberId, LocalDate lentOn, LocalDate dueOn) {
        this.code = code;
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
