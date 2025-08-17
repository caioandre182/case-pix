package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;
import jakarta.validation.constraints.*;

public record CriaChavePixRequest(
    @NotNull
    TipoChave tipoChave,

    @NotBlank
    @Size(max = 77)
    String valorChave,

    @NotNull
    TipoConta tipoConta,

    @NotBlank
    @Size(max = 4)
    @Pattern(regexp = "^[0-9]{1,4}$")
    String numeroAgencia,

    @NotBlank
    @Size(max = 8)
    @Pattern(regexp = "^[0-9]{1,8}$")
    String numeroConta,

    @NotBlank
    @Size(max = 30)
    String nomeCorrentista,

    @Size(max = 45)
    String sobrenomeCorrentista
    ) {}
