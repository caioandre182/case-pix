package com.casepix.pixkeys.domain.strategy.impl;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.vo.Cnpj;

public class CnpjChavePixStrategy implements ChavePixStrategy {
    @Override
    public TipoChave tipo() {
        return TipoChave.CNPJ;
    }

    @Override
    public String valida(String texto) {
        return Cnpj.validar(texto).valor();
    }

    @Override
    public String mascara(String texto) {
        return Cnpj.validar(texto).mascarado();
    }
}
