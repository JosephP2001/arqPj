package ec.edu.uce.library.lending.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;

/**
 * Persistence model of a {@code Copy}. The loan columns are null while the copy is available.
 * {@code @Version} makes the second of two concurrent lends fail (BR-01).
 */
@Entity
@Table(name = "copies")
class CopyJpaEntity {

    @Id
    @Column(name = "copy_code")
    private String copyCode;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "lent_on")
    private LocalDate lentOn;

    @Column(name = "due_on")
    private LocalDate dueOn;

    @Version
    private Long version;

    protected CopyJpaEntity() {
    }

    CopyJpaEntity(String copyCode) {
        this.copyCode = copyCode;
    }

    String getCopyCode() {
        return copyCode;
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

    void setLoan(String memberId, LocalDate lentOn, LocalDate dueOn) {
        this.memberId = memberId;
        this.lentOn = lentOn;
        this.dueOn = dueOn;
    }

    void clearLoan() {
        setLoan(null, null, null);
    }
}
