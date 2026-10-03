package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.PlanoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanoRepository extends JpaRepository<PlanoEntity, Long> {

  @Query("""
      select p from PlanoEntity p
      where p.cliente.id = :clienteId
        and p.status = br.com.startjob.acesso.core.enumeration.Status.ATIVO
        and p.fim >= :agora
        and (p.removido = false or p.removido is null)
      order by p.id desc
      """)
  List<PlanoEntity> findAtivos(@Param("clienteId") Long clienteId,
      @Param("agora") LocalDateTime agora,
      Pageable pageable);
}
