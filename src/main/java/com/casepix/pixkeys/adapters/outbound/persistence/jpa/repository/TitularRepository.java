package com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.TitularEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TitularRepository extends JpaRepository<TitularEntity, UUID> {
    Optional<TitularEntity> findByCpf(String cpf);
    Optional<TitularEntity> findByCnpj(String cnpj);
}
