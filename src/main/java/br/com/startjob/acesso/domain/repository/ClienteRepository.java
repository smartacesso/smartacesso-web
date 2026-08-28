package br.com.startjob.acesso.domain.repository;

import br.com.startjob.acesso.domain.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    Optional<ClienteEntity> findByUnidadeAtiva(@Param("unidade") String unidade);
}
