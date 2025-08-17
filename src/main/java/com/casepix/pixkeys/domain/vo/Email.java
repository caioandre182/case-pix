package com.casepix.pixkeys.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.Generated;

import java.util.Locale;
import java.util.Objects;

public class Email implements ObjetoDeValor<String>{
    private final String valor;

    private Email(String valor) { this.valor = valor; }

    public static Email validar(String texto) {
        if (texto == null) throw new ValidacaoException("Email obrigatorio");
        String s = texto.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) throw new ValidacaoException("Email obrigatorio");
        if (s.length() > 77) throw new ValidacaoException("Email muito longo (max 77)");
        if (!s.contains("@")) throw new ValidacaoException("Email invalido");
        return new Email(s);
    }

    @Override
    public String valor() {
        return valor;
    }

    @Override
    public String mascarado() {
        final String v = valor;
        final int indice = v.indexOf('@');
        if (indice <= 0 || indice == v.length() - 1) return "***@***";

        final String local = v.substring(0, indice);

        final String localMascarado = (local.length() <= 3)
            ? local
            : local.substring(0, 3) + "*".repeat(local.length() - 3);

        return localMascarado + "@***";
    }

    @Generated
    @Override
    public String toString() { return mascarado(); }

    @Generated
    @Override
    public boolean equals(Object o) {
        return (o instanceof Email e) && e.valor.equals(this.valor);
    }

    @Generated
    @Override
    public int hashCode() { return Objects.hash(valor); }
}
