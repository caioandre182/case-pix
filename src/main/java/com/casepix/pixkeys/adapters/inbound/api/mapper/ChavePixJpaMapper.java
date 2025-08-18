package com.casepix.pixkeys.adapters.inbound.api.mapper;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ChavePixEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ContaEntity;
import com.casepix.pixkeys.domain.model.ChavePix;

import java.util.UUID;

public final class ChavePixJpaMapper {
    private ChavePixJpaMapper() {}

    public static ChavePixEntity paraEntidade(ChavePix pix, ContaEntity conta) {
        UUID id = (pix.getId() != null) ? pix.getId() : UUID.randomUUID();

        return ChavePixEntity.builder()
            .id(id)
            .conta(conta)
            .tipoChave(pix.getTipo())
            .valorChave(pix.getValor())
            .build();
    }

    public static ChavePix paraDominio(ChavePixEntity e) {

        return ChavePix.reconstruir(
            e.getId(),
            e.getConta().getId(),
            e.getTipoChave(),
            e.getValorChave(),
            e.getConta().getTitular().getNome(),
            e.getConta().getTitular().getSobrenome()

        );
    }
}
