package com.casepix.pixkeys.unit.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import com.casepix.pixkeys.domain.vo.Celular;
import com.casepix.pixkeys.domain.vo.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class EmailTest {

    @Test
    void valida_ok_email(){
        var email = Email.validar(" JoaoSilva@gmail.com ");
        assertThat(email.valor()).isEqualTo("joaosilva@gmail.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "joaosilva.gmail.com",
        "",
    })
    void invalido_dispara_validacao(String invalido) {
        assertThatThrownBy(() -> Email.validar(invalido))
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    void tamanho_maximo_77_ok() {
        String emailTamanhoMaximo = emailOfLength(77);
        assertThatCode(() -> Email.validar(emailTamanhoMaximo)).doesNotThrowAnyException();
    }

    @Test
    void tamanho_78() {
        String big78 = emailOfLength(78);
        assertThatThrownBy(() -> Email.validar(big78))
            .isInstanceOf(ValidacaoException.class);
    }

    private static String emailOfLength(int totalLen) {
        String domain = "x.io";
        int localLen = totalLen - domain.length() - 1;
        return "a".repeat(localLen) + "@" + domain;
    }
}
