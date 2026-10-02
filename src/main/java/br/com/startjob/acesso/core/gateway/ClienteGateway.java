package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;

import java.util.Optional;

public interface ClienteGateway {

    long countByUnidadeAtiva(String unidade);

    Optional<Cliente> findByUnidadeAtiva(String unidade);

}
