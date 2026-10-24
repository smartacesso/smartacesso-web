package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.ResponsavelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<ResponsavelEntity, Long> {

    @Query("""
            SELECT r FROM ResponsavelEntity r
            LEFT JOIN FETCH r.cliente
            WHERE r.id = :id
              AND (r.removido = false OR r.removido IS NULL)
            """)
    Optional<ResponsavelEntity> findByIdWithCliente(@Param("id") Long id);

}
