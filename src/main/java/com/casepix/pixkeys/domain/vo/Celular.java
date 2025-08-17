package com.casepix.pixkeys.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.Generated;

import java.util.Objects;
import java.util.regex.Pattern;

public class Celular implements ObjetoDeValor<String>{
    private static final Pattern PadraoInternacional = Pattern.compile("^\\+[1-9][0-9]{7,14}$");

    private final String numero;

    private Celular(String numero) { this.numero = numero; }

    public static Celular validar(String texto){
        if (texto == null) throw new ValidacaoException("Celular obrigatorio");
        String s = texto.trim();
        if(!PadraoInternacional.matcher(s).matches())
            throw new ValidacaoException("Celular deve estar no padrão E.164 ex.: +5511999999999");

        return new Celular(s);
    }

    @Override public String valor() { return numero; }

    @Override public String mascarado() {
        if (numero.length() <= 7) return numero;
        int fim = Math.max(3, numero.length() - 4);
        return numero.substring(0, 3) + "*****" + numero.substring(fim);
    }

    @Generated
    @Override public String toString() { return mascarado(); }

    @Generated
    @Override public boolean equals(Object o) {
        return (o instanceof Celular c) && c.numero.equals(this.numero);
    }

    @Generated
    @Override public int hashCode() { return Objects.hash(numero); }
}
