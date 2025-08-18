package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.application.port.in.query.ConsultarChaveQuery;
import com.casepix.pixkeys.application.port.result.ChavePixResumoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultarChavesUseCase {
    Page<ChavePixResumoResult> executar(ConsultarChaveQuery query, Pageable pageable);
}
