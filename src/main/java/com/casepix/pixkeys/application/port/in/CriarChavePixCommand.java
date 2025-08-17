package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;

public record CriarChavePixCommand(
    TipoChave tipoChave,
    String valorChave,
    TipoConta tipoConta,
    String numeroAgencia,
    String numeroConta,
    String nomeCorrentista,
    String sobrenomeCorrentista
) {
}
