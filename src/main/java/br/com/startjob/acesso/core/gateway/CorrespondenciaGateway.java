package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;

import java.util.Optional;

public interface CorrespondenciaGateway {

    Optional<Correspondencia> findById(Long id);

    Correspondencia save(Correspondencia correspondencia);

}
