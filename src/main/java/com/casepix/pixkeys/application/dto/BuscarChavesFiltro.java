package com.casepix.pixkeys.application.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;

import java.time.Instant;

public record BuscarChavesFiltro(
    TipoChave tipoChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    Instant inclusaoDe,
    Instant inclusaoAte,
    Instant inativacaoDe,
    Instant inativacaoAte
) {
}
