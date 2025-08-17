package com.casepix.pixkeys.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.Generated;

import java.util.Objects;

public class ChaveAleatoria implements ObjetoDeValor<String>{
    private final String valor;

    private ChaveAleatoria(String valor) {
        this.valor = valor;
    }

    public static ChaveAleatoria validar(String texto) {
        if (texto == null) throw new ValidacaoException("Chave aleatoria obrigatoria");
        String s = texto.trim();
        if (s.length() != 36) {
            throw new ValidacaoException("Chave aleatoria deve ter exatamente 36 caracteres");
        }
        if (!s.matches("^[A-Za-z0-9]{36}$")) {
            throw new ValidacaoException("Chave aleatoria deve conter apenas letras e numeros (sem hifen)");
        }
        return new ChaveAleatoria(s);
    }

    @Override public String valor() { return valor; }

    @Override public String mascarado() {
        return valor.substring(0, 8) + "****" + valor.substring(valor.length() - 4);
    }

    @Generated
    @Override public String toString() { return mascarado(); }

    @Generated
    @Override public boolean equals(Object o) {
        return (o instanceof ChaveAleatoria c) && c.valor.equals(this.valor);
    }

    @Generated
    @Override public int hashCode() { return Objects.hash(valor); }
}
