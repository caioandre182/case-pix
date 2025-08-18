package com.casepix.pixkeys.application.port.out;

import java.util.UUID;

public interface TitularRepositoryPort {
    void atualizar(UUID titularId, String nome, String sobrenome);
}
