package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    @Query("select e from EmpresaEntity e left join fetch e.cliente left join fetch e.endereco where e.id = :id")
    Optional<EmpresaEntity> findByIdWithCliente(@Param("id") Long id);

    @Query("select e from EmpresaEntity e where e.id = :id and (e.removido = false or e.removido is null)")
    Optional<EmpresaEntity> findByIdActive(@Param("id") Long id);

    @Query(value = """
            select e from EmpresaEntity e
            where e.cliente.id = :idCliente
              and (:nome is null or lower(e.nome) like lower(concat('%', :nome, '%')))
              and (:cnpj is null or lower(e.cnpj) like lower(concat('%', :cnpj, '%')))
              and (e.removido = false or e.removido is null)
            """,
            countQuery = """
            select count(e) from EmpresaEntity e
            where e.cliente.id = :idCliente
              and (:nome is null or lower(e.nome) like lower(concat('%', :nome, '%')))
              and (:cnpj is null or lower(e.cnpj) like lower(concat('%', :cnpj, '%')))
              and (e.removido = false or e.removido is null)
            """)
    Page<EmpresaEntity> buscarEmpresas(
            @Param("idCliente") Long idCliente,
            @Param("nome") String nome,
            @Param("cnpj") String cnpj,
            Pageable pageable
    );
}
