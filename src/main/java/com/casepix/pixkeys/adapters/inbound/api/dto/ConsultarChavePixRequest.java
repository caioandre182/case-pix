package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record ConsultarChavePixRequest(
    TipoChave tipoChave,

    @Pattern(regexp = "\\d{4}", message = "Conta deve ter 4 dígitos numéricos")
    String numeroAgencia,

    @Pattern(regexp = "\\d{8}", message = "Conta deve ter 8 dígitos numéricos")
    String numeroConta,

    @Size(max = 30)
    String nome,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant criadoDe,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant criadoAte,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant inativadoDe,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant inativadoAte
) {
}
