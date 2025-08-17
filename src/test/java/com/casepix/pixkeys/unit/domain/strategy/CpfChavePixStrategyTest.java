package com.casepix.pixkeys.unit.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.strategy.impl.CpfChavePixStrategy;
import com.casepix.pixkeys.domain.strategy.impl.EmailChavePixStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CpfChavePixStrategyTest {
    CpfChavePixStrategy s = new CpfChavePixStrategy();


    @Test
    void tipo_correto() {
        assertThat(s.tipo()).isEqualTo(TipoChave.CPF);
    }

    @Test
    void valida_retorna_valor() {
        String valorValidado = s.valida("59230828025");
        assertThat(valorValidado).isEqualTo("59230828025");
    }

    @Test
    void mascara_usa_vo() {
        String mascarado = s.mascara("59230828025");
        assertThat(mascarado).contains("***");
    }

    @Test
    void valida_invalido_lanca_do_vo() {
        assertThatThrownBy(() -> s.valida("11111111111"))
            .isInstanceOf(ValidacaoException.class);
    }
}
