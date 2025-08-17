package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.adapters.inbound.api.dto.CriaChavePixRequest;
import com.casepix.pixkeys.adapters.inbound.api.dto.CriarChavePixResponse;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.PixChaveEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.ContaRepository;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.in.CriarChavePixCommand;
import com.casepix.pixkeys.application.port.in.CriarChavePixUseCase;
import com.casepix.pixkeys.domain.enums.TipoPessoa;
import com.casepix.pixkeys.domain.exception.ChavePixJaExisteException;
import com.casepix.pixkeys.domain.exception.ContaNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.LimiteExcedidoException;
import com.casepix.pixkeys.domain.strategy.RegistroChavePixStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarChavePixService implements CriarChavePixUseCase {
    private final ContaRepository contaRepo;
    private final PixChaveRepository pixRepo;
    private final RegistroChavePixStrategy strategy;

    private final int LIMITE_PF = 5;
    private final int LIMITE_PJ = 20;


    @Transactional
    public CriarChavePixResponse executar(CriarChavePixCommand req){
        var conta = contaRepo.findByNumeroAgenciaAndNumeroContaAndTipoConta(
            req.numeroAgencia(), req.numeroConta(), req.tipoConta()
            ).orElseThrow(() -> new ContaNaoEncontradaException(
                "Conta não encontrada para os dados: agência=%s, conta=%s e tipo=%s"
                    .formatted(req.numeroAgencia(), req.numeroConta(), req.tipoConta())
            ));

        var registro = strategy.get(req.tipoChave());
        final String valorValidado = registro.valida(req.valorChave());

        if(pixRepo.existsByValorChave(valorValidado)){
            throw new ChavePixJaExisteException("Chave já cadastrada");
        }

        TipoPessoa tipoPessoa = conta.getTitular().getTipoPessoa();
        long quantidadeAtivas = pixRepo.countByConta_IdAndDeletedAtIsNull(conta.getId());
        int limite = (tipoPessoa == TipoPessoa.PF) ? LIMITE_PF : LIMITE_PJ;

        if(quantidadeAtivas >= limite){
            throw new LimiteExcedidoException("Limite de chaves ativas excedido");
        }

        var entity = PixChaveEntity.builder()
            .id(UUID.randomUUID())
            .conta(conta)
            .tipoChave(req.tipoChave())
            .valorChave(valorValidado)
            .nomeCorrentista(req.nomeCorrentista())
            .sobrenomeCorrentista(req.sobrenomeCorrentista())
            .build();

        return new CriarChavePixResponse(pixRepo.save(entity).getId());
    }
}
