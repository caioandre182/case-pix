package com.casepix.pixkeys.application.port.in;

import com.casepix.pixkeys.application.port.in.command.AlterarContaTitularCommand;
import com.casepix.pixkeys.application.port.result.AlterarContaTitularResult;

public interface AlterarChavePixUseCase {
    AlterarContaTitularResult executar(AlterarContaTitularCommand command);
}
