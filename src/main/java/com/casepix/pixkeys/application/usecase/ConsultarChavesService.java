package com.casepix.pixkeys.application.usecase;

import com.casepix.pixkeys.application.port.in.ConsultarChavesUseCase;
import com.casepix.pixkeys.application.port.in.query.ConsultarChaveQuery;
import com.casepix.pixkeys.application.port.out.ChavePixQueryPort;
import com.casepix.pixkeys.application.port.result.ChavePixResumoResult;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ConsultarChavesService implements ConsultarChavesUseCase
{
    private final ChavePixQueryPort repo;

    @Override
    public Page<ChavePixResumoResult> executar(ConsultarChaveQuery query, Pageable pageable){
        boolean temAg = query.numeroAgencia() != null && !query.numeroAgencia().isBlank();
        boolean temCc = query.numeroConta()   != null && !query.numeroConta().isBlank();

        if (temAg ^ temCc) {
            throw new ValidacaoException("Informe agência E conta para filtrar por conta.");
        }

        boolean temInclusao = query.criadoDe() != null || query.criadoAte() != null;
        boolean temInativ  = query.inativadoDe() != null || query.inativadoAte() != null;
        if (temInclusao && temInativ) {
            throw new ValidacaoException(
                "Use apenas o período de inclusão OU apenas o período de inativação."
            );
        }



        return repo.findBy(query, pageable);
    }
}
