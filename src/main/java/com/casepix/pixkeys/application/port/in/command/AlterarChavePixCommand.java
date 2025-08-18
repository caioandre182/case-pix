package com.casepix.pixkeys.application.port.in.command;

import com.casepix.pixkeys.domain.enums.TipoConta;

import java.util.UUID;

public record AlterarChavePixCommand(
    UUID idChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    String nomeCorrentista,
    String sobrenomeCorrentista
) {
}
