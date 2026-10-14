package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegraRepository extends JpaRepository<RegraEntity, Long> {

    @Query("select distinct r from RegraEntity r left join fetch r.empresa left join fetch r.cliente left join fetch r.horarios where r.id = :id")
    Optional<RegraEntity> findByIdComplete(@Param("id") Long id);

    @Query("select r from RegraEntity r where r.id = :id and (r.removido = false or r.removido is null)")
    Optional<RegraEntity> findByIdActive(@Param("id") Long id);

    @Query(value = """
            select r from RegraEntity r
            left join fetch r.empresa e
            where r.cliente.id = :idCliente
              and (:nome is null or lower(r.nome) like lower(concat('%', :nome, '%')))
              and (:idEmpresa is null or e.id = :idEmpresa)
              and (:nomeEmpresa is null or lower(e.nome) like lower(concat('%', :nomeEmpresa, '%')))
              and (:tipoPedestre is null or r.tipoPedestre = :tipoPedestre)
              and (:tipo is null or r.tipo = :tipo)
              and (r.removido = false or r.removido is null)
            """,
            countQuery = """
            select count(r) from RegraEntity r
            left join r.empresa e
            where r.cliente.id = :idCliente
              and (:nome is null or lower(r.nome) like lower(concat('%', :nome, '%')))
              and (:idEmpresa is null or e.id = :idEmpresa)
              and (:nomeEmpresa is null or lower(e.nome) like lower(concat('%', :nomeEmpresa, '%')))
              and (:tipoPedestre is null or r.tipoPedestre = :tipoPedestre)
              and (:tipo is null or r.tipo = :tipo)
              and (r.removido = false or r.removido is null)
            """)
    Page<RegraEntity> buscarRegras(
            @Param("idCliente") Long idCliente,
            @Param("nome") String nome,
            @Param("idEmpresa") Long idEmpresa,
            @Param("nomeEmpresa") String nomeEmpresa,
            @Param("tipoPedestre") TipoPedestre tipoPedestre,
            @Param("tipo") TipoRegra tipo,
            Pageable pageable
    );
}
