package com.casepix.pixkeys.domain.strategy.impl;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.vo.Cpf;

public final class CpfChavePixStrategy implements ChavePixStrategy {
    @Override
    public TipoChave tipo() {
        return TipoChave.CPF;
    }

    @Override
    public String valida(String texto) {
        return Cpf.validar(texto).valor();
    }

    @Override
    public String mascara(String texto) {
        return Cpf.validar(texto).mascarado();
    }
}
