package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.adapters.inbound.api.dto.CriarChavePixResponse;
import com.casepix.pixkeys.application.port.in.command.CriarChavePixCommand;


public interface CriarChavePixUseCase {
    CriarChavePixResponse executar(CriarChavePixCommand req);
}
