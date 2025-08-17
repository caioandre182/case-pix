package com.casepix.pixkeys.domain.strategy.impl;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.vo.Email;

public class EmailChavePixStrategy implements ChavePixStrategy {
    @Override
    public TipoChave tipo() {
        return TipoChave.EMAIL;
    }

    @Override
    public String valida(String texto) {
        return Email.validar(texto).valor();
    }

    @Override
    public String mascara(String texto) {
        return Email.validar(texto).mascarado();
    }
}
