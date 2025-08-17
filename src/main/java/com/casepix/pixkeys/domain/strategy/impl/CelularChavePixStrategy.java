package com.casepix.pixkeys.domain.strategy.impl;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.vo.Celular;

public class CelularChavePixStrategy implements ChavePixStrategy {
    @Override
    public TipoChave tipo() {
        return TipoChave.CELULAR;
    }

    @Override
    public String valida(String texto) {
        return Celular.validar(texto).valor();
    }

    @Override
    public String mascara(String texto) {
        return Celular.validar(texto).mascarado();
    }
}
