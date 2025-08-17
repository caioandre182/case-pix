package com.casepix.pixkeys.domain.strategy;

import com.casepix.pixkeys.domain.enums.TipoChave;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class RegistroChavePixStrategy {
    private final Map<TipoChave, ChavePixStrategy> porTipo;

    public RegistroChavePixStrategy(Collection<ChavePixStrategy> strategy){
        this.porTipo = strategy.stream()
            .collect(Collectors.toUnmodifiableMap(ChavePixStrategy::tipo, s -> s));
    }

    public ChavePixStrategy get(TipoChave tipo){
        ChavePixStrategy s = porTipo.get(tipo);
        if(s == null) throw new IllegalArgumentException("Strategy ausente para tipo: " + tipo);
        return s;
    }
}
