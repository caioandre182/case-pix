package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.application.port.result.ConsultarChavePixResult;

import java.util.UUID;

public interface ConsultarChavePixPorIdUseCase {
    ConsultarChavePixResult executar(UUID id);
}
