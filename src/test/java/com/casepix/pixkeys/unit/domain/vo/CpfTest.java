package com.casepix.pixkeys.unit.domain.vo;

import static org.assertj.core.api.Assertions.*;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.vo.Cpf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CpfTest {

    @Test
    void valida_ok_cpf(){
        var cpf = Cpf.validar("04566042006");
        assertThat(cpf.valor()).isEqualTo("04566042006");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "045.660.420-06",
        "11111111111",
        "123456789",
        "417ABC41839"
    })
    void invalido_dispara_validacao(String invalido) {
        assertThatThrownBy(() -> Cpf.validar(invalido))
            .isInstanceOf(ValidacaoException.class);
    }
}
