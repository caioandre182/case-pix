package com.casepix.pixkeys.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.Generated;

import java.util.Objects;

public class Cnpj implements ObjetoDeValor<String>{
    private final String digitos;

    @Override
    public String valor() {
        return digitos;
    }

    private Cnpj(String digitos) { this.digitos = digitos; }

    public static Cnpj validar(String texto) {
        if (texto == null) throw new ValidacaoException("CNPJ obrigatorio");
        if (!texto.matches("^\\d{14}$")) {
            throw new ValidacaoException("CNPJ deve conter 14 digitos, sem pontuacao");
        }
        if (todosOsDigitosIguais(texto)) {
            throw new ValidacaoException("CNPJ invalido");
        }
        if (!digitosVerificadoresConferem(texto)) {
            throw new ValidacaoException("CNPJ com digito verificador invalido");
        }
        return new Cnpj(texto);
    }

    private static boolean todosOsDigitosIguais(String d) {
        return d.chars().distinct().count() == 1;
    }

    private static boolean digitosVerificadoresConferem(String d) {
        int dv1 = calcularDv(d.substring(0, 12), new int[]{5,4,3,2,9,8,7,6,5,4,3,2});
        int dv2 = calcularDv(d.substring(0, 12) + dv1, new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2});
        return d.charAt(12) == (char)('0' + dv1) && d.charAt(13) == (char)('0' + dv2);
    }

    private static int calcularDv(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int mod = soma % 11;
        return (mod < 2) ? 0 : 11 - mod;
    }

    @Override
    public String mascarado() {
        return digitos.substring(0, 2) + "********" + digitos.substring(12);
    }

    @Generated
    @Override public String toString() { return mascarado(); }

    @Generated
    @Override public boolean equals(Object o) {
        return (o instanceof Cnpj c) && c.digitos.equals(this.digitos);
    }

    @Generated
    @Override public int hashCode() { return Objects.hash(digitos); }
}
