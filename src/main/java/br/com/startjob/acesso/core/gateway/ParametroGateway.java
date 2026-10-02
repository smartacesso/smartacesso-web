package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.parametro.Parametro;

import java.util.Optional;

public interface ParametroGateway {

    Optional<Parametro> findByClienteAndNome(Long clienteId, String nome);

}
