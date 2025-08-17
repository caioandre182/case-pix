package com.casepix.pixkeys.infra.config;

import com.casepix.pixkeys.domain.strategy.ChavePixStrategy;
import com.casepix.pixkeys.domain.strategy.RegistroChavePixStrategy;
import com.casepix.pixkeys.domain.strategy.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StrategyConfig {

    @Bean
    public ChavePixStrategy cpfStrategy() {
        return new CpfChavePixStrategy();
    }

    @Bean
    public ChavePixStrategy cnpjStrategy() {
        return new CnpjChavePixStrategy();
    }

    @Bean
    public ChavePixStrategy celularStrategy() {
        return new CelularChavePixStrategy();
    }

    @Bean
    public ChavePixStrategy emailStrategy() {
        return new EmailChavePixStrategy();
    }

    @Bean
    public ChavePixStrategy aleatoriaStrategy() {
        return new AleatoriaChavePixStrategy();
    }

    @Bean
    public RegistroChavePixStrategy registroChavePixStrategy(List<ChavePixStrategy> todas){
        return new RegistroChavePixStrategy(todas);
    }
}
