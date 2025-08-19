package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.inbound.api.mapper.ChavePixJpaMapper;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.ContaRepository;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.application.port.out.ChavePixRepositoryPort;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.model.ChavePix;
import com.casepix.pixkeys.domain.model.ChavePixComTitular;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChavePixRepositoryAdapter implements ChavePixRepositoryPort {
    private final PixChaveRepository pixRepo;
    private final ContaRepository contaRepo;

    @Override
    public boolean existsByValor(String valorNormalizado){
        return pixRepo.existsByValorChave(valorNormalizado);
    }

    @Override
    public long countAtivasByContaId(UUID contaId) {
        return pixRepo.countByConta_IdAndDeletedAtIsNull(contaId);
    }

    @Override
    public ChavePix save(ChavePix chave) {
        var conta = contaRepo.getReferenceById(chave.getContaId());
        var entity = ChavePixJpaMapper.paraEntidade(chave, conta);
        var saved  = pixRepo.save(entity);
        return ChavePixJpaMapper.paraDominio(saved);
    }

    @Override
    public Optional<ChavePixComTitular> findById(UUID chaveId) {
        return pixRepo.findById(chaveId)
            .map(e -> new ChavePixComTitular(
                e.getId(),
                e.getConta().getId(),
                e.getConta().getTitular().getId(),
                e.getTipoChave(),
                e.getValorChave(),
                e.getConta().getTitular().getNome(),
                e.getConta().getTitular().getSobrenome(),
                e.getDeletedAt()
            ));

    }

    @Override
    public void relinkConta(UUID chaveId, UUID contaDestinoId) {
        var conta = contaRepo.findById(contaDestinoId)
            .orElseThrow(() -> new ValidacaoException("Conta destino inexistente"));

        int updated = pixRepo.relinkConta(chaveId, conta);
        if (updated == 0) {
            throw new ChavePixNaoEncontradaException("Chave não encontrada ou inativa");
        }
    }
}
