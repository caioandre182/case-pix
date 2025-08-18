package com.casepix.pixkeys.adapters.inbound.api.mapper;

import com.casepix.pixkeys.adapters.inbound.api.dto.CriaChavePixRequest;
import com.casepix.pixkeys.application.port.in.command.CriarChavePixCommand;

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
}
