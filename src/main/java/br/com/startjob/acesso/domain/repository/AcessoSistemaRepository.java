package br.com.startjob.acesso.domain.repository;

import br.com.startjob.acesso.domain.entity.AcessoSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcessoSistemaRepository extends JpaRepository<AcessoSistemaEntity, Long> {
}
