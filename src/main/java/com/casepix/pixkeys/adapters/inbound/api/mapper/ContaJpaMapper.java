package com.casepix.pixkeys.adapters.inbound.api.mapper;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ContaEntity;
import com.casepix.pixkeys.domain.model.Conta;

public final class ContaJpaMapper {
    private ContaJpaMapper() {}


    public static Conta toDomain(ContaEntity e) {

        return new Conta(
            e.getId(),
            e.getTitular().getId(),
            e.getTitular().getTipoPessoa(),
            e.getTipoConta(),
            e.getNumeroAgencia(),
            e.getNumeroConta()
        );
    }
}
