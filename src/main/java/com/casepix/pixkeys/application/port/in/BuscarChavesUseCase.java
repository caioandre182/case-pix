package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.adapters.inbound.api.dto.ChavePixListaItemResponse;
import com.casepix.pixkeys.application.dto.BuscarChavesFiltro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BuscarChavesUseCase {
    Page<ChavePixListaItemResponse> executar(BuscarChavesFiltro filtro, Pageable pageable);
}
