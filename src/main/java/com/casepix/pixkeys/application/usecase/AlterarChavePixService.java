package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.application.port.in.AlterarChavePixUseCase;
import com.casepix.pixkeys.application.port.in.command.AlterarContaTitularCommand;
import com.casepix.pixkeys.application.port.out.ChavePixLeituraPort;
import com.casepix.pixkeys.application.port.out.ChavePixRepositoryPort;
import com.casepix.pixkeys.application.port.out.ContaRepositoryPort;
import com.casepix.pixkeys.application.port.out.TitularRepositoryPort;
import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AlterarChavePixService implements AlterarChavePixUseCase {
    private final ChavePixRepositoryPort pixRepo;
    private final ChavePixLeituraPort leituraPort;
    private final ContaRepositoryPort contaRepo;
    private final TitularRepositoryPort titularRepo;

    @Override
    @Transactional
    public AlterarContaTitularResult executar(AlterarContaTitularCommand cmd){
        var chave = pixRepo.findById(cmd.idChave()).orElseThrow(
            () -> new ChavePixNaoEncontradaException("Chave não encontrada")
        );

        if(chave.deletedAt() != null){
            throw new ValidacaoException("Chave inativada não pode ser alterada");
        }

        contaRepo.atualizar(chave.contaId(), cmd.numeroAgencia(), cmd.numeroConta(), cmd.tipoConta());

        titularRepo.atualizar(chave.titularId(), cmd.nomeCorrentista(), cmd.sobrenomeCorrentista());

        return leituraPort.findChavePix(chave.chaveId())
            .orElseThrow(() -> new ChavePixNaoEncontradaException("Chave não encontrada"));
    }
}
