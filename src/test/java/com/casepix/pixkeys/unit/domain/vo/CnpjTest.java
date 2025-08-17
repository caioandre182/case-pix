package com.casepix.pixkeys.unit.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.vo.Cnpj;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class CnpjTest {

    @Test
    void valida_ok_cnpj(){
        var cnpj = Cnpj.validar("99752369000118");
        assertThat(cnpj.valor()).isEqualTo("99752369000118");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "23.329.923/0001-97",
        "11111111111111",
        "12345678910",
        "23ABC923000197"
    })
    void invalido_dispara_validacao(String invalido) {
        assertThatThrownBy(() -> Cnpj.validar(invalido))
            .isInstanceOf(ValidacaoException.class);
    }
}
