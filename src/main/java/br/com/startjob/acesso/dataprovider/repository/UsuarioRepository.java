package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    @Query("select u from UsuarioEntity u left join fetch u.cliente where u.id = :id")
    Optional<UsuarioEntity> findByIdWithCliente(@Param("id") Long id);

    @Query("""
            select u from UsuarioEntity u
            left join fetch u.cliente
            where (u.removido = false or u.removido is null)
              and lower(u.cliente.nomeUnidadeOrganizacional) = lower(:unidade)
              and u.login = :login
            """)
    List<UsuarioEntity> findByLoginAndUnidade(@Param("login") String login, @Param("unidade") String unidade);

    @EntityGraph(attributePaths = "cliente")
    @Query("""
            select u from UsuarioEntity u
            where u.cliente.id = :idCliente
              and (:nome is null or lower(u.nome) like lower(concat('%', :nome, '%')))
              and (:cpf is null or u.cpf = :cpf)
              and (u.removido = false or u.removido is null)
            """)
    Page<UsuarioEntity> buscarUsuarios(
            @Param("idCliente") Long idCliente,
            @Param("nome") String nome,
            @Param("cpf") String cpf,
            Pageable pageable
    );
}
