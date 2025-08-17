package com.casepix.pixkeys.adapters.outbound.persistence.jpa.spec;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.PixChaveEntity;
import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class PixChaveSpecs {

    private PixChaveSpecs() { }

    public static Specification<PixChaveEntity> tipoChave(TipoChave tipo){
        return (root, query, builder) -> tipo == null ? builder.conjunction()
                : builder.equal(root.get("tipoChave"), tipo);
    }

    public static Specification<PixChaveEntity> conta(TipoConta tipoConta, String agencia, String conta) {
        return (root, query, builder) -> {
            var join = root.join("conta");
            var p = builder.conjunction();
            if (tipoConta != null) p = builder.and(p, builder.equal(join.get("tipoConta"), tipoConta));
            if (agencia != null && !agencia.isBlank()) p = builder.and(p, builder.equal(join.get("numeroAgencia"), agencia));
            if (conta != null && !conta.isBlank())     p = builder.and(p, builder.equal(join.get("numeroConta"), conta));
            return p;
        };
    }

    public static Specification<PixChaveEntity> inclusaoEntre(Instant de, Instant ate) {
        return (root, query, builder) -> {
            if (de == null && ate == null) return builder.conjunction();
            if (de != null && ate != null) return builder.between(root.get("createdAt"), de, ate);
            return de != null ? builder.greaterThanOrEqualTo(root.get("createdAt"), de)
                : builder.lessThanOrEqualTo(root.get("createdAt"), ate);
        };
    }

    public static Specification<PixChaveEntity> inativacaoEntre(Instant de, Instant ate) {
        return (root, query, builder) -> {
            if (de == null && ate == null) return builder.conjunction();
            var notNull = builder.isNotNull(root.get("deletedAt"));
            var range = (de != null && ate != null) ? builder.between(root.get("deletedAt"), de, ate)
                : de != null ? builder.greaterThanOrEqualTo(root.get("deletedAt"), de)
                : builder.lessThanOrEqualTo(root.get("deletedAt"), ate);
            return builder.and(notNull, range);
        };
    }
}
