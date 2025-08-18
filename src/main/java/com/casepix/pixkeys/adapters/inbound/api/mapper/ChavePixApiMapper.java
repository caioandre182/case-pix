package com.casepix.pixkeys.adapters.inbound.api.mapper;

import com.casepix.pixkeys.adapters.inbound.api.dto.*;
import com.casepix.pixkeys.application.port.in.command.AlterarContaTitularCommand;
import com.casepix.pixkeys.application.port.in.command.CriarChavePixCommand;
import com.casepix.pixkeys.application.port.in.query.ConsultarChaveQuery;
import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;
import com.casepix.pixkeys.application.port.result.ChavePixResumoResult;
import com.casepix.pixkeys.application.port.result.ConsultarChavePixResult;

import java.util.UUID;

public class ChavePixApiMapper {

    private ChavePixApiMapper() {}

    public static CriarChavePixCommand toCommand(CriaChavePixRequest req) {
        return new CriarChavePixCommand(
            req.tipoChave(),
            req.valorChave(),
            req.tipoConta(),
            req.numeroAgencia(),
            req.numeroConta(),
            req.nomeCorrentista(),
            req.sobrenomeCorrentista()
        );
    }

    public static ConsultarChavePixResponse toResponse(ConsultarChavePixResult r) {
        return new ConsultarChavePixResponse(
            r.id(),
            r.tipoChave(),
            r.valorChave(),
            r.tipoConta(),
            r.numeroAgencia(),
            r.numeroConta(),
            r.nomeCorrentista(),
            r.sobrenomeCorrentista(),
            r.createdAt(),
            r.deletedAt()
        );
    }

    public static ConsultarChavePixResponse toResumo(ChavePixResumoResult r){
        return new ConsultarChavePixResponse(
            r.id(),
            r.tipoChave(),
            r.valorChave(),
            r.tipoConta(),
            r.numeroAgencia(),
            r.numeroConta(),
            r.nomeCorrentista(),
            r.sobrenomeCorrentista(),
            r.createdAt(),
            r.deletedAt()
        );
    }

    public static ConsultarChaveQuery toQuery(ConsultarChavePixRequest r) {
        return new ConsultarChaveQuery(
            r.tipoChave(),
            r.numeroAgencia(),
            r.numeroConta(),
            r.nome(),
            r.criadoDe(),
            r.criadoAte(),
            r.inativadoDe(),
            r.inativadoAte()
        );
    }

    public static AlterarDadosContaResponse toResponse(AlterarContaTitularResult r){
        return new AlterarDadosContaResponse(
            r.id(),
            r.tipoChave(),
            r.valorChave(),
            r.tipoConta(),
            r.numeroAgencia(),
            r.numeroConta(),
            r.nomeCorrentista(),
            r.sobrenomeCorrentista(),
            r.createdAt()
        );
    }

    public static AlterarContaTitularCommand toCommand(UUID id, AlterarDadosContaRequest req) {
        return new AlterarContaTitularCommand(
            id,
            req.tipoConta(),
            req.numeroAgencia(),
            req.numeroConta(),
            req.nomeCorrentista(),
            req.sobrenomeCorrentista()
        );
    }
}
