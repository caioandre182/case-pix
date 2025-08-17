package com.casepix.pixkeys.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;

public interface ChavePixStrategy {
    TipoChave tipo();
    String valida(String texto);
    String mascara(String texto);
}
