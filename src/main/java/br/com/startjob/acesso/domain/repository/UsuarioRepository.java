package br.com.startjob.acesso.domain.repository;

import br.com.startjob.acesso.domain.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    @Query("""
            select u from UsuarioEntity u
            left join fetch u.cliente
            where (u.removido = false or u.removido is null)
              and lower(u.cliente.nomeUnidadeOrganizacional) = lower(:unidade)
              and u.login = :login
            """)
    List<UsuarioEntity> findByLoginAndUnidade(@Param("login") String login, @Param("unidade") String unidade);
}
