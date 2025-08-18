package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ChavePixEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.out.ConsultarChavePixPort;
import com.casepix.pixkeys.application.port.result.ConsultarChavePixResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultarChavePixAdapter implements ConsultarChavePixPort {

    private final PixChaveRepository repo;

    @Override
    public Optional<ConsultarChavePixResult> findPixById(UUID id) {
        return repo.findById(id).map(ConsultarChavePixAdapter::toResult);
    }

    private static ConsultarChavePixResult toResult(ChavePixEntity e) {
        return new ConsultarChavePixResult(
            e.getId(),
            e.getTipoChave(),
            e.getValorChave(),
            e.getConta().getTipoConta(),
            e.getConta().getNumeroAgencia(),
            e.getConta().getNumeroConta(),
            e.getConta().getTitular().getNome(),
            e.getConta().getTitular().getSobrenome(),
            e.getCreatedAt(),
            e.getDeletedAt()
        );
    }
}
