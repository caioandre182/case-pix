package com.casepix.pixkeys.application.port.in.query;

import com.casepix.pixkeys.domain.enums.TipoChave;

import java.time.Instant;

public record ConsultarChaveQuery(
    TipoChave tipoChave,
    String numeroAgencia,
    String numeroConta,
    String nome,
    Instant criadoDe,
    Instant criadoAte,
    Instant inativadoDe,
    Instant inativadoAte
) {
}
