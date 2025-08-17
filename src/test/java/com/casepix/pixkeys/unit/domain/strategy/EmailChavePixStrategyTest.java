package com.casepix.pixkeys.unit.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.strategy.impl.EmailChavePixStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class EmailChavePixStrategyTest {

    EmailChavePixStrategy s = new EmailChavePixStrategy();


    @Test
    void tipo_correto() {
        assertThat(s.tipo()).isEqualTo(TipoChave.EMAIL);
    }

    @Test
    void valida_retorna_valor() {
        String valorValidado = s.valida(" JOAOsilva@gmail.com ");
        assertThat(valorValidado).isEqualTo("joaosilva@gmail.com");
    }

    @Test
    void mascara_usa_vo() {
        String mascarado = s.mascara("joaosilva@gmail.com");
        assertThat(mascarado).contains("*****");
    }

    @Test
    void valida_invalido_lanca_do_vo() {
        assertThatThrownBy(() -> s.valida("joaosilva.gmail.com"))
            .isInstanceOf(ValidacaoException.class);
    }

}
