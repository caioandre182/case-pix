package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.adapters.inbound.api.dto.CriarChavePixResponse;



public interface CriarChavePixUseCase {
    CriarChavePixResponse executar(CriarChavePixCommand req);
}
