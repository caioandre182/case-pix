package com.casepix.pixkeys.application.port.out;

import com.casepix.pixkeys.domain.enums.TipoConta;
import com.casepix.pixkeys.domain.model.Conta;

import java.util.Optional;
import java.util.UUID;

public interface ContaRepositoryPort {
    Optional<Conta> findByAgenciaContaTipo(String agencia, String conta, TipoConta tipoConta);
    void atualizar(UUID contaId, String agencia, String conta, TipoConta tipoConta);
}
