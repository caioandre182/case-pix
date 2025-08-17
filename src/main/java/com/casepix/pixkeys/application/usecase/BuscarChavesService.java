package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.adapters.inbound.api.dto.ChavePixListaItemResponse;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.PixChaveEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.spec.PixChaveSpecs;
import com.casepix.pixkeys.application.dto.BuscarChavesFiltro;
import com.casepix.pixkeys.application.port.in.BuscarChavesUseCase;
import com.casepix.pixkeys.domain.exception.ChavePixNaoEncontradaException;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BuscarChavesService implements BuscarChavesUseCase
{
    private final PixChaveRepository repo;

    @Override
    public Page<ChavePixListaItemResponse> executar(BuscarChavesFiltro filtro, Pageable pageable){
        boolean temInclusao = filtro.inclusaoDe() != null || filtro.inclusaoAte() != null;
        boolean temInativacao = filtro.inativacaoDe() != null || filtro.inativacaoAte() != null;

        if(temInclusao && temInativacao){
            throw new ValidacaoException("Não é possível ter filtro de inclusão e inativação, escolha apenas um grupo");
        }

        Specification<PixChaveEntity> spec = Specification.allOf(
            PixChaveSpecs.tipoChave(filtro.tipoChave()),
            PixChaveSpecs.conta(filtro.tipoConta(), filtro.numeroAgencia(), filtro.numeroConta()),
            temInclusao ? PixChaveSpecs.inclusaoEntre(filtro.inclusaoDe(), filtro.inclusaoAte()) : null,
            temInativacao ? PixChaveSpecs.inativacaoEntre(filtro.inativacaoDe(), filtro.inativacaoAte()) : null
        );

        Page<PixChaveEntity> page = repo.findAll(spec, pageable);

        if (page.isEmpty()){
            throw new ChavePixNaoEncontradaException("Nenhuma chave foi encontrada para os filtros");
        }

        return page.map(e -> {
                var conta = e.getConta();
                return ChavePixListaItemResponse.from(
                    e.getId(),
                    e.getTipoChave(),
                    e.getValorChave(),
                    e.getConta().getTipoConta(),
                    e.getConta().getNumeroAgencia(),
                    e.getConta().getNumeroConta(),
                    e.getConta().getTitular().getNome(),
                    e.getConta().getTitular().getSobrenome(),
                    e.getCreatedAt(),
                    e.getDeletedAt());
            }

        );

    }
}
