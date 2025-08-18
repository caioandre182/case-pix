package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.out.ChavePixLeituraPort;
import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChavePixLeituraAdapter implements ChavePixLeituraPort {

    private final PixChaveRepository repo;


    @Override
    public Optional<AlterarContaTitularResult> findChavePix(UUID id) {
        return repo.findById(id).map(e -> new AlterarContaTitularResult(
            e.getId(),
            e.getTipoChave(),
            e.getValorChave(),
            e.getConta().getTipoConta(),
            e.getConta().getNumeroAgencia(),
            e.getConta().getNumeroConta(),
            e.getConta().getTitular().getNome(),
            e.getConta().getTitular().getSobrenome(),
            e.getCreatedAt()
        ));
    }
}
