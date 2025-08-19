package com.casepix.pixkeys.adapters.outbound.persistence.jpa.spec;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ChavePixEntity;
import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public final class PixChaveSpecs {

    private PixChaveSpecs() { }


    public static Specification<ChavePixEntity> tipoChave(TipoChave tipo){
        return (root, query, builder) -> tipo == null ? builder.conjunction()
                : builder.equal(root.get("tipoChave"), tipo);
    }

    public static Specification<ChavePixEntity> conta(String agencia, String conta) {
        boolean agenciaContaCompleta = agencia != null && conta != null;

        if(!agenciaContaCompleta) return null;

        return (root, query, builder) -> {
            var join = root.join("conta");
            var p = builder.conjunction();
            if (!agencia.isBlank()) p = builder.and(p, builder.equal(join.get("numeroAgencia"), agencia));
            if (!conta.isBlank()) p = builder.and(p, builder.equal(join.get("numeroConta"), conta));
            return p;
        };
    }

    public static Specification<ChavePixEntity> nome(String nome) {
        if (nome == null) return null;
        return (root, query, builder) -> {
            Join<Object, Object> conta = root.join("conta");
            Join<Object, Object> titular = conta.join("titular");
            return builder.like(
                builder.lower(titular.get("nome")),
                "%" + nome.toLowerCase() + "%"
            );
        };
    }

    public static Specification<ChavePixEntity> inclusaoEntre(Instant de, Instant ate) {
        return (root, query, builder) -> {
            if (de == null && ate == null) return builder.conjunction();
            if (de != null && ate != null) return builder.between(root.get("createdAt"), de, ate);
            return de != null ? builder.greaterThanOrEqualTo(root.get("createdAt"), de)
                : builder.lessThanOrEqualTo(root.get("createdAt"), ate);
        };
    }

    public static Specification<ChavePixEntity> inativacaoEntre(Instant de, Instant ate) {
        return (root, query, builder) -> {
            if (de == null && ate == null) return builder.conjunction();
            var notNull = builder.isNotNull(root.get("deletedAt"));
            var range = (de != null && ate != null) ? builder.between(root.get("deletedAt"), de, ate)
                : de != null ? builder.greaterThanOrEqualTo(root.get("deletedAt"), de)
                : builder.lessThanOrEqualTo(root.get("deletedAt"), ate);
            return builder.and(notNull, range);
        };
    }

    @SafeVarargs
    public static Specification<ChavePixEntity> allOf(Specification<ChavePixEntity>... parts) {
        Specification<ChavePixEntity> spec = null;
        if (parts == null) return null;
        for (Specification<ChavePixEntity> s : parts) {
            if (s == null) continue;
            spec = (spec == null) ? s : spec.and(s);
        }
        return spec;
    }
}
