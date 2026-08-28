package br.com.startjob.acesso.domain.repository;

import br.com.startjob.acesso.domain.entity.PlanoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PlanoRepository extends JpaRepository<PlanoEntity, Long> {

    @Query("""
            select p from PlanoEntity p
            where p.cliente.id = :clienteId
              and p.status = br.com.startjob.acesso.domain.enumeration.Status.ATIVO
              and p.fim >= :agora
              and (p.removido = false or p.removido is null)
            order by p.id desc
            """)
    List<PlanoEntity> findAtivos(@Param("clienteId") Long clienteId,
                                 @Param("agora") Date agora,
                                 Pageable pageable);
}
