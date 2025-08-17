package com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity;

import com.casepix.pixkeys.domain.enums.TipoChave;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "pix_chave",
    uniqueConstraints = @UniqueConstraint(name = "uk_pix_valor", columnNames = "valor_chave"),
    indexes = {
        @Index(name = "idx_pix_conta", columnList = "conta_id"),
        @Index(name = "idx_pix_tipo", columnList = "tipo_chave"),
        @Index(name = "idx_pix_created_at", columnList = "created_at"),
        @Index(name = "idx_pix_dt_inativacao", columnList = "deleted_at"),
        @Index(name = "idx_pix_nome_correntista", columnList = "nome_correntista")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class PixChaveEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaEntity conta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_chave", nullable = false, length = 9)
    private TipoChave tipoChave;

    @Size(max = 77)
    @Column(name = "valor_chave", nullable = false, length = 77)
    private String valorChave;

    @Size(max = 30)
    @Column(name = "nome_correntista", nullable = false, length = 30)
    private String nomeCorrentista;

    @Size(max = 45)
    @Column(name = "sobrenome_correntista", length = 45)
    private String sobrenomeCorrentista;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

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
