package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.CorrespondenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrespondenciaRepository extends JpaRepository<CorrespondenciaEntity, Long> {

    @Query("""
            SELECT c FROM CorrespondenciaEntity c
            LEFT JOIN FETCH c.destinatario
            LEFT JOIN FETCH c.usuarioRecebeu
            LEFT JOIN FETCH c.cliente
            WHERE c.id = :id
              AND (c.removido = false OR c.removido IS NULL)
            """)
    Optional<CorrespondenciaEntity> findByIdComplete(@Param("id") Long id);

}
