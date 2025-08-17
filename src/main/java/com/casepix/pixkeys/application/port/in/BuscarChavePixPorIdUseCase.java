package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.adapters.inbound.api.dto.ChavePixListaItemResponse;

import java.util.UUID;

public interface BuscarChavePixPorIdUseCase {
    ChavePixListaItemResponse executar(UUID id);
}
