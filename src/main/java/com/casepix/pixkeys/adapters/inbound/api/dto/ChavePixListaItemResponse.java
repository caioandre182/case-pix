package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record ChavePixListaItemResponse(
    UUID id,
    TipoChave tipoChave,
    String valorChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    String nomeCorrentista,
    String dataInclusao,
    String dataInativacao
) {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static ChavePixListaItemResponse from(
        UUID id,
        TipoChave tipo,
        String valor,
        TipoConta tipoConta,
        String agencia,
        String conta,
        String nome,
        String sobrenome,
        Instant createdAt,
        Instant deletedAt
    ) {
        String nomeCompleto = (nome == null ? "" : nome) +
            ((sobrenome == null || sobrenome.isBlank()) ? "" : " " + sobrenome);
        String inc = createdAt == null ? "" : ISO.format(createdAt.atZone(ZONE));
        String ina = deletedAt == null ? "" : ISO.format(deletedAt.atZone(ZONE));
        return new ChavePixListaItemResponse(
            id,
            tipo,
            valor,
            tipoConta,
            agencia,
            conta,
            nomeCompleto.isBlank() ? "" : nomeCompleto,
            inc, ina
        );
    }
}
