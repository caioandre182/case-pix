package com.casepix.pixkeys.domain.vo;

import com.casepix.pixkeys.domain.exception.ValidacaoException;
import lombok.Generated;

import java.util.Objects;

public final class Cpf implements ObjetoDeValor<String>{
    private final String digitos;

    private Cpf(String digitos) { this.digitos = digitos;}

    public static Cpf validar(String texto){
        if (texto == null) throw  new ValidacaoException("CPF obrigatório");
        if (!texto.matches("^\\d{11}$"))
            throw new ValidacaoException("CPF deve conter 11 digitos, sem pontuacao");
        if (todosOsDigitosIguais(texto))
            throw new ValidacaoException("CPF invalido");
        if (!digitosVerificadoresConferem(texto))
            throw new ValidacaoException("CPF com digito verificador inválido");

        return new Cpf(texto);
    }

    private static boolean todosOsDigitosIguais(String d){
        return d.chars().distinct().count() == 1;
    }

    private static boolean digitosVerificadoresConferem(String d){
        int dv1 = calcularDv(d.substring(0,9), new int[]{10,9,8,7,6,5,4,3,2});
        int dv2 = calcularDv(d.substring(0, 9) + dv1, new int[]{11,10,9,8,7,6,5,4,3,2});
        return d.charAt(9) == (char)('0' + dv1) && d.charAt(10) == (char)('0' + dv2);
    }

    private static int calcularDv(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += (base.charAt(i) - '0') * pesos[i];
        int mod = soma % 11;
        return (mod < 2) ? 0 : 11 - mod;
    }

    @Override
    public String valor() { return digitos; }

    @Override
    public String mascarado() { return "***" + digitos.substring(3,9) + "***";}

    @Generated
    @Override public String toString() { return mascarado(); }

    @Generated
    @Override public boolean equals(Object o) {
        return (o instanceof Cpf c) && c.digitos.equals(this.digitos);
    }

    @Generated
    @Override public int hashCode() { return Objects.hash(digitos); }
}

