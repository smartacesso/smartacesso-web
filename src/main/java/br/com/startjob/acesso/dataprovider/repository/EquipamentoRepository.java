package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.EquipamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipamentoRepository extends JpaRepository<EquipamentoEntity, Long> {

    @Query("""
            SELECT e FROM EquipamentoEntity e
            LEFT JOIN FETCH e.cliente
            WHERE e.id = :id
              AND (e.removido = false OR e.removido IS NULL)
            """)
    Optional<EquipamentoEntity> findByIdWithCliente(@Param("id") Long id);

}
