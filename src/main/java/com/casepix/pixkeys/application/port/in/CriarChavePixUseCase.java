package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.application.port.in.command.CriarChavePixCommand;
import com.casepix.pixkeys.application.port.result.CriarChavePixResult;


public interface CriarChavePixUseCase {
    CriarChavePixResult executar(CriarChavePixCommand req);
}
