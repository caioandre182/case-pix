package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.application.port.in.ConsultarChavePixPorIdUseCase;
import com.casepix.pixkeys.application.port.out.ConsultarChavePixPort;
import com.casepix.pixkeys.application.port.result.ConsultarChavePixResult;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarChavePixPorIdService implements ConsultarChavePixPorIdUseCase {
    private final ConsultarChavePixPort repo;

    @Override
    @Transactional(readOnly = true)
    public ConsultarChavePixResult executar(UUID id){
        return repo.findPixById(id).orElseThrow(
            () -> new ChavePixNaoEncontradaException("Chave não encontrada")
        );
    }
}
