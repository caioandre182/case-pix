package com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity;

import com.casepix.pixkeys.domain.enums.TipoConta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "conta_bancaria",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_conta_unica_por_titular",
        columnNames = {"titular_id", "tipo_conta", "numero_agencia", "numero_conta"}
    ),
    indexes = {
        @Index(name = "idx_conta_ag_cc_tipo", columnList = "numero_agencia,numero_conta,tipo_conta")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class ContaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "titular_id", nullable = false)
    private TitularEntity titular;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false, length = 10)
    private TipoConta tipoConta;

    @Size(max = 4)
    @Pattern(regexp = "^[0-9]{1,4}$")
    @Column(name = "numero_agencia", nullable = false, length = 4)
    private String numeroAgencia;

    @Size(max = 8)
    @Pattern(regexp = "^[0-9]{1,8}$")
    @Column(name = "numero_conta", nullable = false, length = 8)
    private String numeroConta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
