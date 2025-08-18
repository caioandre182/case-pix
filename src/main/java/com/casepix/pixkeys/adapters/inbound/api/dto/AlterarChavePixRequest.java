package com.casepix.pixkeys.adapters.inbound.api.dto;

import com.casepix.pixkeys.domain.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlterarChavePixRequest(
    @NotNull
    TipoConta tipoConta,

    @NotBlank
    @Pattern(regexp = "\\d{1,4}", message = "numeroAgencia deve conter apenas dígitos e até 4 caracteres")
    String numeroAgencia,

    @NotBlank
    @Pattern(regexp = "\\d{1,8}", message = "numeroConta deve conter apenas dígitos e até 8 caracteres")
    String numeroConta,

    @NotBlank
    @Size(max = 30)
    String nomeCorrentista,

    @Size(max = 45)
    String sobrenomeCorrentista
) {
}
