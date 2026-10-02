package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.ParametroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParametroRepository extends JpaRepository<ParametroEntity, Long> {

    @Query("""
            select p from ParametroEntity p
            where p.cliente.id = :clienteId
              and p.nome = :nome
              and (p.removido = false or p.removido is null)
            """)
    Optional<ParametroEntity> findByClienteAndNome(@Param("clienteId") Long clienteId, @Param("nome") String nome);
}
