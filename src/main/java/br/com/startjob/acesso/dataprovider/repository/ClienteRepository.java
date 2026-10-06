package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Query("""
            select count(c) from ClienteEntity c
            where lower(c.nomeUnidadeOrganizacional) = lower(:unidade)
              and (c.removido = false or c.removido is null)
            """)
    long countByUnidadeAtiva(@Param("unidade") String unidade);

    @Query("""
            select c from ClienteEntity c
            where lower(c.nomeUnidadeOrganizacional) = lower(:unidade)
              and (c.removido = false or c.removido is null)
            """)
    @EntityGraph(attributePaths = "planos")
    Optional<ClienteEntity> findByUnidadeAtiva(@Param("unidade") String unidade);

    @Query("""
            select c from ClienteEntity c
            where (:nome is null or lower(c.nome) like lower(concat('%', :nome, '%')))
              and (c.removido = false or c.removido is null)
            """)
    Page<ClienteEntity> buscarClientes(@Param("nome") String nome, Pageable pageable);
}
