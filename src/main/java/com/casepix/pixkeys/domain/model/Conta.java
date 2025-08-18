package com.casepix.pixkeys.domain.model;

import com.casepix.pixkeys.domain.enums.TipoConta;
import com.casepix.pixkeys.domain.enums.TipoPessoa;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public final class Conta {
    private final UUID id;
    private final UUID titularId;
    private final TipoPessoa tipoPessoa;
    private final TipoConta tipoConta;
    private final String numeroAgencia;
    private final String numeroConta;

    private final int LIMITE_PF = 5;
    private final int LIMITE_PJ = 20;

    public Conta(UUID id, UUID titularId, TipoPessoa tipoPessoa, TipoConta tipoConta, String numeroAgencia, String numeroConta) {
        this.id = id;
        this.titularId = titularId;
        this.tipoPessoa = tipoPessoa;
        this.tipoConta = tipoConta;
        this.numeroAgencia = numeroAgencia;
        this.numeroConta = numeroConta;
    }

    public static Conta nova(UUID id, UUID titularId,TipoPessoa tipoPessoa, TipoConta tipoConta,
                             String numeroAgencia, String numeroConta) {
        return new Conta(id, titularId, tipoPessoa, tipoConta, numeroAgencia, numeroConta);
    }

    public static Conta reconstruir(UUID id, UUID titularId, TipoPessoa tipoPessoa, TipoConta tipoConta,
                                    String numeroAgencia, String numeroConta) {
        return new Conta(id, titularId, tipoPessoa, tipoConta, numeroAgencia, numeroConta);
    }

    public int limiteChaves() {
        return (tipoPessoa == TipoPessoa.PF) ? LIMITE_PF : LIMITE_PJ;
    }
}
