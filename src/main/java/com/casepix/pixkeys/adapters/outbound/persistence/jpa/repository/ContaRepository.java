package com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ContaEntity;
import com.casepix.pixkeys.domain.enums.TipoConta;
import com.casepix.pixkeys.domain.enums.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {
    Optional<ContaEntity> findByNumeroAgenciaAndNumeroContaAndTipoConta(
        String numeroAgencia, String numeroConta,TipoConta tipoConta
    );

    boolean existsByNumeroAgenciaAndNumeroContaAndTipoConta(
        String numeroAgencia, String numeroConta,TipoConta tipoConta
    );

    @Query("""
           select t.tipoPessoa
           from ContaEntity c
             join c.titular t
           where c.tipoConta = :tipoConta
             and c.numeroAgencia = :numeroAgencia
             and c.numeroConta = :numeroConta
           """)
    Optional<TipoPessoa> findTipoPessoaByConta(
        String numeroAgencia, String numeroConta,TipoConta tipoConta
    );
}
