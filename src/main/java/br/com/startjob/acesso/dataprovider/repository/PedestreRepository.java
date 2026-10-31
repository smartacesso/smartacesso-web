package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.core.domain.pedestre.PedestreResumo;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedestreRepository extends JpaRepository<PedestreEntity, Long> {

    @Query(value = """
            select new br.com.startjob.acesso.core.domain.pedestre.PedestreResumo(
                p.id,
                p.matricula,
                p.codigoCartaoAcesso,
                p.nome,
                p.telefone,
                p.celular,
                p.cpf,
                p.rg,
                p.tipo,
                p.status,
                coalesce(ev.nome, p.empresaVisitada, e.nome),
                d.nome,
                cc.nome,
                cg.nome,
                case when p.foto is not null then true else false end
            )
            from PedestreEntity p
            left join p.empresa e
            left join p.empresaVisitadaRef ev
            left join p.departamento d
            left join p.centroCusto cc
            left join p.cargo cg
            where p.cliente.id = :idCliente
              and (p.removido = false or p.removido is null)
              and (:matricula is null or lower(p.matricula) like lower(concat('%', :matricula, '%')))
              and (:codigoCartaoAcesso is null or lower(p.codigoCartaoAcesso) like lower(concat('%', :codigoCartaoAcesso, '%')))
              and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
              and (:contato is null
                   or lower(p.telefone) like lower(concat('%', :contato, '%'))
                   or lower(p.celular) like lower(concat('%', :contato, '%')))
              and (:cpf is null or p.cpf like concat('%', :cpf, '%'))
              and (:rg is null or lower(p.rg) like lower(concat('%', :rg, '%')))
              and (:tipo is null or p.tipo = :tipo)
              and (:idEmpresa is null
                   or (:tipo = br.com.startjob.acesso.core.enumeration.TipoPedestre.PEDESTRE and e.id = :idEmpresa)
                   or (:tipo = br.com.startjob.acesso.core.enumeration.TipoPedestre.VISITANTE and (ev.id = :idEmpresa or (ev.id is null and e.id = :idEmpresa)))
                   or (:tipo is null and (e.id = :idEmpresa or ev.id = :idEmpresa)))
              and (:idDepartamento is null or d.id = :idDepartamento)
              and (:idCentroCusto is null or cc.id = :idCentroCusto)
              and (:idCargo is null or cg.id = :idCargo)
              and (:uuidLocal is null or p.uuidLocal = :uuidLocal)
              and (:status is null or p.status = :status)
              and (:possuiFoto is null
                   or (:possuiFoto = true and p.foto is not null)
                   or (:possuiFoto = false and p.foto is null))
            """,
            countQuery = """
            select count(p)
            from PedestreEntity p
            left join p.empresa e
            left join p.empresaVisitadaRef ev
            left join p.departamento d
            left join p.centroCusto cc
            left join p.cargo cg
            where p.cliente.id = :idCliente
              and (p.removido = false or p.removido is null)
              and (:matricula is null or lower(p.matricula) like lower(concat('%', :matricula, '%')))
              and (:codigoCartaoAcesso is null or lower(p.codigoCartaoAcesso) like lower(concat('%', :codigoCartaoAcesso, '%')))
              and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
              and (:contato is null
                   or lower(p.telefone) like lower(concat('%', :contato, '%'))
                   or lower(p.celular) like lower(concat('%', :contato, '%')))
              and (:cpf is null or p.cpf like concat('%', :cpf, '%'))
              and (:rg is null or lower(p.rg) like lower(concat('%', :rg, '%')))
              and (:tipo is null or p.tipo = :tipo)
              and (:idEmpresa is null
                   or (:tipo = br.com.startjob.acesso.core.enumeration.TipoPedestre.PEDESTRE and e.id = :idEmpresa)
                   or (:tipo = br.com.startjob.acesso.core.enumeration.TipoPedestre.VISITANTE and (ev.id = :idEmpresa or (ev.id is null and e.id = :idEmpresa)))
                   or (:tipo is null and (e.id = :idEmpresa or ev.id = :idEmpresa)))
              and (:idDepartamento is null or d.id = :idDepartamento)
              and (:idCentroCusto is null or cc.id = :idCentroCusto)
              and (:idCargo is null or cg.id = :idCargo)
              and (:uuidLocal is null or p.uuidLocal = :uuidLocal)
              and (:status is null or p.status = :status)
              and (:possuiFoto is null
                   or (:possuiFoto = true and p.foto is not null)
                   or (:possuiFoto = false and p.foto is null))
            """)
    Page<PedestreResumo> buscarPedestresOtimizado(
            @Param("idCliente") Long idCliente,
            @Param("matricula") String matricula,
            @Param("codigoCartaoAcesso") String codigoCartaoAcesso,
            @Param("nome") String nome,
            @Param("contato") String contato,
            @Param("cpf") String cpf,
            @Param("rg") String rg,
            @Param("tipo") TipoPedestre tipo,
            @Param("idEmpresa") Long idEmpresa,
            @Param("idDepartamento") Long idDepartamento,
            @Param("idCentroCusto") Long idCentroCusto,
            @Param("idCargo") Long idCargo,
            @Param("uuidLocal") String uuidLocal,
            @Param("status") Status status,
            @Param("possuiFoto") Boolean possuiFoto,
            Pageable pageable
    );

    @Query("""
            select pr.pedestre.id, r.nome
            from PedestreRegraEntity pr
            join pr.regra r
            where pr.pedestre.id in :idsPedestres
              and (pr.removido = false or pr.removido is null)
            order by pr.id asc
            """)
    List<Object[]> findNomesRegrasAtivasByPedestreIds(@Param("idsPedestres") List<Long> idsPedestres);

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
