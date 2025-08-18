package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;

import java.time.Instant;
import java.util.UUID;

public record AlterarDadosContaResponse(
    UUID id,
    TipoChave tipoChave,
    String valorChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    String nomeCorrentista,
    String sobrenomeCorrentista,
    Instant createdAt
) {
}
