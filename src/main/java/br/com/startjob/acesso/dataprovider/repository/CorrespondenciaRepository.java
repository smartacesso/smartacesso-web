package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.CorrespondenciaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query(value = """
            SELECT c FROM CorrespondenciaEntity c
            LEFT JOIN FETCH c.destinatario d
            LEFT JOIN FETCH c.usuarioRecebeu u
            WHERE c.cliente.id = :idCliente
              AND (c.removido = false OR c.removido IS NULL)
              AND (:nomeDestinatario IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nomeDestinatario, '%')))
              AND (:codigoRastreio IS NULL OR LOWER(c.codigoRastreio) LIKE LOWER(CONCAT('%', :codigoRastreio, '%')))
              AND (:tipo IS NULL OR UPPER(c.tipo) = UPPER(:tipo))
              AND (:confirmaRetirada IS NULL OR UPPER(c.confirmaRetirada) = UPPER(:confirmaRetirada))
              AND (:dataRecebimentoInicio IS NULL OR c.dataRecebimento >= :dataRecebimentoInicio)
              AND (:dataRecebimentoFim IS NULL OR c.dataRecebimento <= :dataRecebimentoFim)
            """,
            countQuery = """
            SELECT COUNT(c) FROM CorrespondenciaEntity c
            LEFT JOIN c.destinatario d
            WHERE c.cliente.id = :idCliente
              AND (c.removido = false OR c.removido IS NULL)
              AND (:nomeDestinatario IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nomeDestinatario, '%')))
              AND (:codigoRastreio IS NULL OR LOWER(c.codigoRastreio) LIKE LOWER(CONCAT('%', :codigoRastreio, '%')))
              AND (:tipo IS NULL OR UPPER(c.tipo) = UPPER(:tipo))
              AND (:confirmaRetirada IS NULL OR UPPER(c.confirmaRetirada) = UPPER(:confirmaRetirada))
              AND (:dataRecebimentoInicio IS NULL OR c.dataRecebimento >= :dataRecebimentoInicio)
              AND (:dataRecebimentoFim IS NULL OR c.dataRecebimento <= :dataRecebimentoFim)
            """)
    Page<CorrespondenciaEntity> buscarCorrespondencias(
            @Param("idCliente") Long idCliente,
            @Param("nomeDestinatario") String nomeDestinatario,
            @Param("codigoRastreio") String codigoRastreio,
            @Param("tipo") String tipo,
            @Param("confirmaRetirada") String confirmaRetirada,
            @Param("dataRecebimentoInicio") LocalDateTime dataRecebimentoInicio,
            @Param("dataRecebimentoFim") LocalDateTime dataRecebimentoFim,
            Pageable pageable
    );

}
