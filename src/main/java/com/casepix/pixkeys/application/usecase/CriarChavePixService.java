package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.application.port.in.command.CriarChavePixCommand;
import com.casepix.pixkeys.application.port.in.CriarChavePixUseCase;
import com.casepix.pixkeys.application.port.out.ChavePixRepositoryPort;
import com.casepix.pixkeys.application.port.out.ContaRepositoryPort;
import com.casepix.pixkeys.application.port.result.CriarChavePixResult;
import com.casepix.pixkeys.domain.enums.TipoPessoa;
import com.casepix.pixkeys.domain.exception.ChavePixJaExisteException;
import com.casepix.pixkeys.domain.exception.ContaNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.LimiteExcedidoException;
import com.casepix.pixkeys.domain.model.ChavePix;
import com.casepix.pixkeys.domain.strategy.RegistroChavePixStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarChavePixService implements CriarChavePixUseCase {
    private final ContaRepositoryPort contaPort;
    private final ChavePixRepositoryPort pixPort;
    private final RegistroChavePixStrategy strategy;


    @Override
    @Transactional
    public CriarChavePixResult executar(CriarChavePixCommand req){
        var conta = contaPort.findByAgenciaContaTipo(req.numeroAgencia(), req.numeroConta(), req.tipoConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(
                "Conta não encontrada para os dados: agência=%s, conta=%s e tipo=%s"
                    .formatted(req.numeroAgencia(), req.numeroConta(), req.tipoConta())
            ));


        var valorValidadoNormalizado = strategy.get(req.tipoChave()).valida(req.valorChave());

        if (pixPort.existsByValor(valorValidadoNormalizado)){
            throw new ChavePixJaExisteException("Chave já cadastrada");
        }

        int limite = conta.limiteChaves();

       if (pixPort.countAtivasByContaId(conta.getId()) >= limite){
           throw new LimiteExcedidoException("Limite de chaves ativas excedido");
       }

        var saved = pixPort.save(ChavePix.nova(conta.getId(), req.tipoChave(), valorValidadoNormalizado, req.nomeCorrentista(), req.sobrenomeCorrentista()));


        return new CriarChavePixResult(saved.getId());
    }
}
