package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.TitularRepository;
import com.casepix.pixkeys.application.port.out.TitularRepositoryPort;
import com.casepix.pixkeys.domain.exception.ContaNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TitularRepositoryAdapter implements TitularRepositoryPort {
    private final TitularRepository repo;


    @Override
    public void atualizar(UUID titularId, String nome, String sobrenome) {
        var titularEncontrado = repo.findById(titularId)
            .orElseThrow(() -> new ContaNaoEncontradaException("Titular da conta não encontrado(a)"));

        titularEncontrado.setNome(nome);
        titularEncontrado.setSobrenome(sobrenome);

        repo.save(titularEncontrado);
    }
}
