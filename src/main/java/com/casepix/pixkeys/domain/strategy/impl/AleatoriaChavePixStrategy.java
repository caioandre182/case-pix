package com.casepix.pixkeys.domain.strategy.impl;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.vo.ChaveAleatoria;

public class AleatoriaChavePixStrategy implements ChavePixStrategy {
    @Override
    public TipoChave tipo() {
        return TipoChave.ALEATORIA;
    }

    @Override
    public String valida(String texto) {
        return ChaveAleatoria.validar(texto).valor();
    }

    @Override
    public String mascara(String texto) {
        return ChaveAleatoria.validar(texto).mascarado();
    }
}
