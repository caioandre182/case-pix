package com.casepix.pixkeys.adapters.outbound.persistence.jpa.repository;

import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ChavePixEntity;
import com.casepix.pixkeys.adapters.outbound.persistence.jpa.entity.ContaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.UUID;

public interface PixChaveRepository extends JpaRepository<ChavePixEntity, UUID>, JpaSpecificationExecutor<ChavePixEntity> {

    @EntityGraph(attributePaths = {"conta", "conta.titular"})
    Optional<ChavePixEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"conta","conta.titular"})
    Page<ChavePixEntity> findAll(Specification<ChavePixEntity> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    update pix_chave
       set conta_id = :contaId,
           updated_at = now()
     where id = :chaveId
       and deleted_at is null
    """, nativeQuery = true)
    int relinkConta(@Param("chaveId") UUID chaveId, @Param("contaId") UUID contaId);

    boolean existsByValorChave(String valorChave);
    long countByConta_IdAndDeletedAtIsNull(UUID contaId);

}
