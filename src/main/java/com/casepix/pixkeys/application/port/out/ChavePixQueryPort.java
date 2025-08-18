package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.application.port.in.query.ConsultarChaveQuery;
import com.casepix.pixkeys.application.port.result.ChavePixResumoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChavePixQueryPort {
    Page<ChavePixResumoResult> findBy(ConsultarChaveQuery query, Pageable pageable);
}
