package com.casepix.pixkeys.domain.model;

import com.casepix.pixkeys.domain.enums.TipoChave;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public final class ChavePix {
    private final UUID id;
    private final UUID contaId;
    private final TipoChave tipo;
    private final String valor;
    private final String nome;
    private final String sobrenome;

    public ChavePix(UUID id, UUID contaId, TipoChave tipo, String valor, String nome, String sobrenome) {
        this.id = id;
        this.contaId = contaId;
        this.tipo = tipo;
        this.valor = valor;
        this.nome = nome;
        this.sobrenome = sobrenome;
    }

    public static ChavePix nova(UUID contaId, TipoChave tipo,
                                String valor, String nome, String sobrenome) {
        return new ChavePix(UUID.randomUUID(), contaId, tipo, valor, nome, sobrenome);
    }

    public static ChavePix reconstruir(UUID id, UUID contaId, TipoChave tipo,
                                       String valor, String nome, String sobrenome) {
        return new ChavePix(id, contaId, tipo, valor, nome, sobrenome);
    }
}
