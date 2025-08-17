package com.casepix.pixkeys.unit.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.strategy.impl.CelularChavePixStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CelularChavePixStrategyTest {
    CelularChavePixStrategy s = new CelularChavePixStrategy();


    @Test
    void tipo_correto() {
        assertThat(s.tipo()).isEqualTo(TipoChave.CELULAR);
    }

    @Test
    void valida_retorna_valor() {
        String valorValidado = s.valida(" +5511999999999 ");
        assertThat(valorValidado).isEqualTo("+5511999999999");
    }

    @Test
    void mascara_usa_vo() {
        String mascarado = s.mascara("+5511999999999");
        assertThat(mascarado).contains("*****");
    }

    @Test
    void valida_invalido_lanca_do_vo() {
        assertThatThrownBy(() -> s.valida("5511..."))
            .isInstanceOf(ValidacaoException.class);
    }
}
