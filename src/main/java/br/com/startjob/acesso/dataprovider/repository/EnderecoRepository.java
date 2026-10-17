package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {
}
