package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.domain.enums.TipoPessoa;

import java.util.UUID;

public interface TitularRepositoryPort {
    void atualizar(UUID titularId, String nome, String sobrenome);
    TipoPessoa verificaTipoPessoa(UUID titularId);
}
