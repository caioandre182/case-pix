package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.adapters.inbound.api.dto.AlterarChavePixResponse;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.PixChaveEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.ContaRepository;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.in.AlterarChavePixUseCase;
import com.casepix.pixkeys.application.port.in.command.AlterarChavePixCommand;
import com.casepix.pixkeys.domain.enums.TipoPessoa;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.ContaNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.LimiteExcedidoException;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AlterarChavePixService implements AlterarChavePixUseCase {

    private final PixChaveRepository pixRepo;
    private final ContaRepository contaRepo;

    private final int LIMITE_PF = 5;
    private final int LIMITE_PJ = 20;

    @Override
    @Transactional
    public AlterarChavePixResponse executar(AlterarChavePixCommand cmd){
        PixChaveEntity pix = pixRepo.findById(cmd.idChave())
            .orElseThrow(() -> new ChavePixNaoEncontradaException("Chave não encontrada: " + cmd.idChave()));

        if(pix.getDeletedAt() != null) {
            throw new ValidacaoException("Chave inativa, não pode ser alterada");
        }

        var contaASerAlterada = contaRepo.findByNumeroAgenciaAndNumeroContaAndTipoConta(
            cmd.numeroAgencia(), cmd.numeroConta(), cmd.tipoConta()
        ).orElseThrow(() -> new ContaNaoEncontradaException(
            "Conta não encontrada para os dados: agência=%s, conta=%s e tipo=%s"
                .formatted(cmd.numeroAgencia(), cmd.numeroConta(), cmd.tipoConta())
        ));

        var contaAtualId = pix.getConta().getId();
        var contaASerAlteradaId = contaASerAlterada.getId();

        if(!contaASerAlteradaId.equals(contaAtualId)) {
            var tipoPessoa = contaASerAlterada.getTitular().getTipoPessoa();

            int limite = (tipoPessoa == TipoPessoa.PF) ? LIMITE_PF : LIMITE_PJ;

            long chavesAtivasNaContaASerAlterada = pixRepo.countByConta_IdAndDeletedAtIsNull(contaASerAlteradaId);

            if(chavesAtivasNaContaASerAlterada + 1 > limite){
                throw new LimiteExcedidoException("Limite de chaves ativas excedido");
            }

            pix.setConta(contaASerAlterada);


        }
        pix.setNomeCorrentista(cmd.nomeCorrentista());
        pix.setSobrenomeCorrentista(cmd.sobrenomeCorrentista());
        contaASerAlterada.setNumeroAgencia(cmd.numeroAgencia());
        contaASerAlterada.setNumeroConta(cmd.numeroConta());
        contaASerAlterada.setTipoConta(cmd.tipoConta());

        pix.setUpdatedAt(Instant.now());

        PixChaveEntity salvo = pixRepo.save(pix);
        return AlterarChavePixResponse.from(
            salvo.getId(),
            salvo.getTipoChave(),
            salvo.getValorChave(),
            salvo.getConta().getTipoConta(),
            salvo.getConta().getNumeroAgencia(),
            salvo.getConta().getNumeroConta(),
            salvo.getNomeCorrentista(),
            salvo.getSobrenomeCorrentista(),
            salvo.getCreatedAt()
        );
    }
}
