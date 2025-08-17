package com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.PixChaveEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;
import java.util.UUID;

public interface PixChaveRepository extends JpaRepository<PixChaveEntity, UUID>, JpaSpecificationExecutor<PixChaveEntity> {

    @EntityGraph(attributePaths = {"conta", "conta.titular"})
    Optional<PixChaveEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"conta","conta.titular"})
    Page<PixChaveEntity> findAll(Specification<PixChaveEntity> spec, Pageable pageable);

    boolean existsByValorChave(String valorChave);
    long countByConta_IdAndDeletedAtIsNull(UUID contaId);

}
