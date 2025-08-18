package com.casepix.pixkeys.domain.model;

import com.casepix.pixkeys.domain.enums.TipoChave;

import java.time.Instant;
import java.util.UUID;

public record ChavePixComTitular(
    UUID chaveId,
    UUID contaId,
    UUID titularId,
    TipoChave tipo,
    String valor,
    String nome,
    String sobrenome,
    Instant deletedAt
) {
}
