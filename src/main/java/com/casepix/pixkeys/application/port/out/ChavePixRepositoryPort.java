package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.domain.model.ChavePix;
import com.casepix.pixkeys.domain.model.ChavePixComTitular;

import java.util.Optional;
import java.util.UUID;

public interface ChavePixRepositoryPort {
    boolean existsByValor(String valorNormalizado);
    long countAtivasByContaId(UUID contaId);
    ChavePix save(ChavePix pix);
    Optional<ChavePixComTitular> findById(UUID chaveId);
}
