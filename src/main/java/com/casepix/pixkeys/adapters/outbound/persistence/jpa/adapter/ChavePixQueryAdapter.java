package com.casepix.pixkeys.adapters.outbound.persistence.jpa.adapter;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ChavePixEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository.PixChaveRepository;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.spec.PixChaveSpecs;
import com.casepix.pixkeys.application.port.in.query.ConsultarChaveQuery;
import com.casepix.pixkeys.application.port.out.ChavePixQueryPort;
import com.casepix.pixkeys.application.port.result.ChavePixResumoResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChavePixQueryAdapter implements ChavePixQueryPort {
    private final PixChaveRepository repo;


    @Override
    public Page<ChavePixResumoResult> findBy(ConsultarChaveQuery query, Pageable pageable) {
        var spec = PixChaveSpecs.allOf(
            PixChaveSpecs.tipoChave(query.tipoChave()),
            PixChaveSpecs.conta(query.numeroAgencia(), query.numeroConta()),
            PixChaveSpecs.nome(query.nome()),
            PixChaveSpecs.inativacaoEntre(query.inativadoDe(), query.inativadoAte()),
            PixChaveSpecs.inclusaoEntre(query.criadoDe(), query.criadoAte())
        );

        return repo.findAll(spec, sanatizar(pageable))
            .map(ChavePixQueryAdapter::paraResumo);

    }

    private static ChavePixResumoResult paraResumo(ChavePixEntity e){
        return new ChavePixResumoResult(
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

    private static Pageable sanatizar(Pageable p) {
        int page = (p == null) ? 0 : Math.max(0, p.getPageNumber());
        int sizeIn = (p == null) ? 20 : p.getPageSize();
        int size = Math.min(Math.max(1, sizeIn), 100);
        Sort sort = p != null && p.getSort().isSorted()
            ? p.getSort()
            : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page, size, sort);
    }
}
