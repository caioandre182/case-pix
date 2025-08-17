package com.casepix.pixkeys.unit.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.strategy.impl.CnpjChavePixStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


public class CnpjChavePixStrategyTest {
    CnpjChavePixStrategy s = new CnpjChavePixStrategy();

    @Test
    void tipo_correto() {
        assertThat(s.tipo()).isEqualTo(TipoChave.CNPJ);
    }

    @Test
    void valida_retorna_valor() {
        String valorValidado = s.valida("43358898000187");
        assertThat(valorValidado).isEqualTo("43358898000187");
    }

    @Test
    void mascara_usa_vo() {
        String mascarado = s.mascara("43358898000187");
        assertThat(mascarado).contains("***");
    }

    @Test
    void valida_invalido_lanca_do_vo() {
        assertThatThrownBy(() -> s.valida("43358898000"))
            .isInstanceOf(ValidacaoException.class);
    }
}
