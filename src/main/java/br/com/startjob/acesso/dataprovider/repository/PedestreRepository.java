package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedestreRepository extends JpaRepository<PedestreEntity, Long> {

    /**
     * Espelha {@code PedestreEntity.findByLoginOtimizado}: unidade e login exatos
     * após o caller aplicar {@code trim().toLowerCase()}, e {@code removido is null}.
     */
    @Query("""
            select p from PedestreEntity p
            join p.cliente c
            where (p.removido = false or p.removido is null)
              and c.nomeUnidadeOrganizacional = :unidade
              and p.login = :login
            """)
    Optional<PedestreEntity> findByLoginOtimizado(@Param("login") String login, @Param("unidade") String unidade);

    @Query("""
            select distinct p from PedestreEntity p
            left join fetch p.cliente
            left join fetch p.endereco
            left join fetch p.empresa
            left join fetch p.empresaVisitadaRef
            left join fetch p.departamento
            left join fetch p.centroCusto
            left join fetch p.cargo
            left join fetch p.usuario
            left join fetch p.regras pr
            left join fetch pr.regra
            where p.id = :id
            """)
    Optional<PedestreEntity> findByIdComplete(@Param("id") Long id);

    @Query("""
            select count(p) > 0 from PedestreEntity p
            where p.cliente.id = :idCliente
              and p.cpf = :cpf
              and p.status = br.com.startjob.acesso.core.enumeration.Status.ATIVO
              and (p.removido = false or p.removido is null)
              and (:excludeId is null or p.id <> :excludeId)
            """)
    boolean existsAtivoByCpfAndCliente(
            @Param("cpf") String cpf,
            @Param("idCliente") Long idCliente,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            select count(p) > 0 from PedestreEntity p
            where p.cliente.id = :idCliente
              and p.rg = :rg
              and p.status = br.com.startjob.acesso.core.enumeration.Status.ATIVO
              and (p.removido = false or p.removido is null)
              and (:excludeId is null or p.id <> :excludeId)
            """)
    boolean existsAtivoByRgAndCliente(
            @Param("rg") String rg,
            @Param("idCliente") Long idCliente,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            select count(p) > 0 from PedestreEntity p
            where p.cliente.id = :idCliente
              and p.matricula = :matricula
              and p.status = br.com.startjob.acesso.core.enumeration.Status.ATIVO
              and (p.removido = false or p.removido is null)
              and (:excludeId is null or p.id <> :excludeId)
            """)
    boolean existsAtivoByMatriculaAndCliente(
            @Param("matricula") String matricula,
            @Param("idCliente") Long idCliente,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            select count(p) > 0 from PedestreEntity p
            where p.cliente.id = :idCliente
              and p.codigoCartaoAcesso is not null
              and trim(leading '0' from p.codigoCartaoAcesso) = :cartaoNormalizado
              and p.status = br.com.startjob.acesso.core.enumeration.Status.ATIVO
              and (p.removido = false or p.removido is null)
              and (:excludeId is null or p.id <> :excludeId)
            """)
    boolean existsAtivoByCartaoNormalizadoAndCliente(
            @Param("cartaoNormalizado") String cartaoNormalizado,
            @Param("idCliente") Long idCliente,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            select p.matricula from PedestreEntity p
            where p.cliente.id = :idCliente
              and p.matricula is not null
              and p.matricula <> ''
            order by p.id desc
            """)
    List<String> findMatriculasByClienteOrderByIdDesc(@Param("idCliente") Long idCliente, Pageable pageable);
}
