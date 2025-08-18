package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.application.port.result.ConsultarChavePixResult;

import java.util.Optional;
import java.util.UUID;

public interface ConsultarChavePixPort {
    Optional<ConsultarChavePixResult> findPixById(UUID id);
}
