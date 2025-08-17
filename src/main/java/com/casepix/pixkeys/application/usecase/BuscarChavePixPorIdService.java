package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.adapters.inbound.api.dto.ChavePixListaItemResponse;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.in.BuscarChavePixPorIdUseCase;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarChavePixPorIdService implements BuscarChavePixPorIdUseCase {
    private final PixChaveRepository repo;

    @Override
    public ChavePixListaItemResponse executar(UUID id){
        var entidade = repo.findById(id)
            .orElseThrow(() -> new ChavePixNaoEncontradaException("Chave não encontrada: " + id));

        var conta = entidade.getConta();

        return ChavePixListaItemResponse.from(
            entidade.getId(),
            entidade.getTipoChave(),
            entidade.getValorChave(),
            entidade.getConta().getTipoConta(),
            entidade.getConta().getNumeroAgencia(),
            entidade.getConta().getNumeroConta(),
            entidade.getConta().getTitular().getNome(),
            entidade.getConta().getTitular().getSobrenome(),
            entidade.getCreatedAt(),
            entidade.getDeletedAt());
    }
}
