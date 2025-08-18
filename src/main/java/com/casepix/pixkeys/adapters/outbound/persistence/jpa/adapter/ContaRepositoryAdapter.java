package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.inbound.api.mapper.ContaJpaMapper;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.ContaRepository;
import com.casepix.pixkeys.application.port.out.ContaRepositoryPort;
import com.casepix.pixkeys.domain.enums.TipoConta;
import com.casepix.pixkeys.domain.exception.ContaNaoEncontradaException;
import com.casepix.pixkeys.domain.model.Conta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContaRepositoryAdapter implements ContaRepositoryPort {
    private final ContaRepository repo;

    @Override
    public Optional<Conta> findByAgenciaContaTipo(String agencia, String conta, TipoConta tipoConta) {
        return repo.findByNumeroAgenciaAndNumeroContaAndTipoConta(agencia, conta, tipoConta).map(ContaJpaMapper::toDomain);
    }

    @Override
    public void atualizar(UUID contaId, String agencia, String conta, TipoConta tipoConta) {
        var contaEncontrada = repo.findById(contaId)
            .orElseThrow(() -> new ContaNaoEncontradaException("Conta não foi encontrada"));

        contaEncontrada.setTipoConta(tipoConta);
        contaEncontrada.setNumeroAgencia(agencia);
        contaEncontrada.setNumeroConta(conta);

        repo.save(contaEncontrada);
    }
}
