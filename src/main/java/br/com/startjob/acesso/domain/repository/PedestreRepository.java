package br.com.startjob.acesso.domain.repository;

import br.com.startjob.acesso.domain.entity.PedestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PedestreRepository extends JpaRepository<PedestreEntity, Long> {

    /**
     * Espelha {@code PedestreEntity.findByLoginOtimizado}: unidade e login exatos
     * após o caller aplicar {@code trim().toLowerCase()}, e {@code removido is null}.
     */
    @Query("""
            select p from PedestreEntity p
            join p.cliente c
            where p.removido is null
              and c.nomeUnidadeOrganizacional = :unidade
              and p.login = :login
            """)
    Optional<PedestreEntity> findByLoginOtimizado(@Param("login") String login, @Param("unidade") String unidade);
}
