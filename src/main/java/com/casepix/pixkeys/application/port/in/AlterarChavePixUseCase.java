package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.adapters.inbound.api.dto.AlterarChavePixResponse;
import com.casepix.pixkeys.application.port.in.command.AlterarChavePixCommand;

public interface AlterarChavePixUseCase {
    AlterarChavePixResponse executar(AlterarChavePixCommand command);
}
