package com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity;

import com.casepix.pixkeys.domain.enums.TipoPessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "titular",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_titular_cpf",  columnNames = "cpf"),
        @UniqueConstraint(name = "uk_titular_cnpj", columnNames = "cnpj")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class TitularEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    private TipoPessoa tipoPessoa;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "cnpj", length = 14)
    private String cnpj;

    @Size(max = 30)
    @Column(name = "nome", nullable = false, length = 30)
    private String nome;

    @Size(max = 45)
    @Column(name = "sobrenome", length = 45)
    private String sobrenome;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
