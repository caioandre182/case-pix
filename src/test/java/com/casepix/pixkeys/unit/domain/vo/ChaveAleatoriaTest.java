package com.casepix.pixkeys.unit.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.vo.ChaveAleatoria;
import com.casepix.pixkeys.domain.vo.Cnpj;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class ChaveAleatoriaTest {

    @Test
    void valida_ok_chave_aleatoria(){
        var chaveAleatoria = ChaveAleatoria.validar("4335889800018743358898000187ABCDEFGH");
        assertThat(chaveAleatoria.valor()).isEqualTo("4335889800018743358898000187ABCDEFGH");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "4335889800018743358898000187",
        "4335889800-187433588-8000187A-CDEFGH"
    })
    void invalido_dispara_validacao(String invalido) {
        assertThatThrownBy(() -> ChaveAleatoria.validar(invalido))
            .isInstanceOf(ValidacaoException.class);
    }
}
