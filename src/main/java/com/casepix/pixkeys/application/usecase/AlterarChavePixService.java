package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.application.port.in.AlterarChavePixUseCase;
import com.casepix.pixkeys.application.port.in.command.AlterarContaTitularCommand;
import com.casepix.pixkeys.application.port.out.ChavePixLeituraPort;
import com.casepix.pixkeys.application.port.out.ChavePixRepositoryPort;
import com.casepix.pixkeys.application.port.out.ContaRepositoryPort;
import com.casepix.pixkeys.application.port.out.TitularRepositoryPort;
import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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
        var chave = pixRepo.findById(cmd.idChave());

        if(chave.isEmpty()){
            throw new ChavePixNaoEncontradaException("Chave não encontrada");
        }

        contaRepo.atualizar(chave.get().contaId(), cmd.numeroAgencia(), cmd.numeroConta(), cmd.tipoConta());

        titularRepo.atualizar(chave.get().titularId(), cmd.nomeCorrentista(), cmd.sobrenomeCorrentista());

        return leituraPort.findChavePix(chave.get().chaveId())
            .orElseThrow(() -> new ChavePixNaoEncontradaException("Chave não encontrada"));
    }
}
