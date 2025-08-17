package com.casepix.pixkeys.unit.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.strategy.impl.AleatoriaChavePixStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AleatoriaChavePixStrategyTest {

    AleatoriaChavePixStrategy s = new AleatoriaChavePixStrategy();

    @Test
    void tipo_correto() {
        assertThat(s.tipo()).isEqualTo(TipoChave.ALEATORIA);
    }

    @Test
    void valida_retorna_valor() {
        String valorValidado = s.valida("4335889800018743358898000187ABCDEFGH");
        assertThat(valorValidado).isEqualTo("4335889800018743358898000187ABCDEFGH");
    }

    @Test
    void mascara_usa_vo() {
        String mascarado = s.mascara("4335889800018743358898000187ABCDEFGH");
        assertThat(mascarado).contains("****");
    }

    @Test
    void valida_invalido_lanca_do_vo() {
        assertThatThrownBy(() -> s.valida("4335-898000187433588980-0187ABCDEFGH"))
            .isInstanceOf(ValidacaoException.class);
    }
}
