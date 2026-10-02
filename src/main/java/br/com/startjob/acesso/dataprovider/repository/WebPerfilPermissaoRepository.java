package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.WebPerfilPermissaoEntity;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WebPerfilPermissaoRepository extends JpaRepository<WebPerfilPermissaoEntity, Long> {

    @Query("""
            select count(w) from WebPerfilPermissaoEntity w
            where w.cliente.id = :clienteId
              and (w.removido = false or w.removido is null)
            """)
    long countByClienteAtivo(@Param("clienteId") Long clienteId);

    @Query("""
            select w from WebPerfilPermissaoEntity w
            where w.cliente.id = :clienteId
              and w.perfil = :perfil
              and (w.removido = false or w.removido is null)
            """)
    List<WebPerfilPermissaoEntity> findByClienteAndPerfil(
            @Param("clienteId") Long clienteId, @Param("perfil") PerfilAcesso perfil);
}
