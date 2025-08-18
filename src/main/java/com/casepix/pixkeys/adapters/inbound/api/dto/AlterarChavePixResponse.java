package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record AlterarChavePixResponse(
    UUID id,
    TipoChave tipoChave,
    String valorChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    String nomeCorrentista,
    String dataInclusao
) {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static AlterarChavePixResponse from(
        UUID id,
        TipoChave tipo,
        String valor,
        TipoConta tipoConta,
        String agencia,
        String conta,
        String nome,
        String sobrenome,
        Instant createdAt
    ) {
        String nomeCompleto = (nome == null ? "" : nome) +
            ((sobrenome == null || sobrenome.isBlank()) ? "" : " " + sobrenome);
        String inc = createdAt == null ? "" : ISO.format(createdAt.atZone(ZONE));
        return new AlterarChavePixResponse(
            id,
            tipo,
            valor,
            tipoConta,
            agencia,
            conta,
            nomeCompleto.isBlank() ? "" : nomeCompleto,
            inc
        );
    }
}
