package com.casepix.pixkeys.unit.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.vo.Celular;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class CelularTest {


    @Test
    void valida_ok_formatoPadraoInternacional(){
        var celular = Celular.validar("+5511999999999");
        assertThat(celular.valor()).isEqualTo("+5511999999999");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "5511999999999",
        "+0119999999999",
        "+1234567",
        "+1234567890123456"
    })
    void valida_invalido_lanca(String invalido) {
        assertThatThrownBy(() -> Celular.validar(invalido))
            .isInstanceOf(ValidacaoException.class)
            .hasMessageContaining("E.164");
    }

    @Test
    void mascara_esconde_miolo() {
        var celular = Celular.validar("+5511999999999");
        assertThat(celular.mascarado()).contains("*****");
    }

    @Test
    void equals_hashCode_por_valor() {
        var celular1 = Celular.validar("+5511999999999");
        var celular2 = Celular.validar("+5511999999999");
        assertThat(celular1).isEqualTo(celular2);
        assertThat(celular1.hashCode()).isEqualTo(celular2.hashCode());
    }

    @Test
    void trim_aplicado_no_valor_canonico() {
        var celular = Celular.validar("  +5511999999999  ");
        assertThat(celular.valor()).isEqualTo("+5511999999999");
    }
}
