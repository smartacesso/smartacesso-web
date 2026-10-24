package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Responsavel;

import java.util.Optional;

public interface ResponsavelGateway {

    Optional<Responsavel> findById(Long id);

}
