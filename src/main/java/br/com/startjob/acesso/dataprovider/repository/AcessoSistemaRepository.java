package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.AcessoSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcessoSistemaRepository extends JpaRepository<AcessoSistemaEntity, Long> {
}
