package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;

import java.util.Optional;
import java.util.UUID;

public interface ChavePixLeituraPort {
    Optional<AlterarContaTitularResult> findChavePix(UUID id);
}
