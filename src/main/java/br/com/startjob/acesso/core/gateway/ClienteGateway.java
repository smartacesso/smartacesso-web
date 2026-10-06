package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteGateway {

    long countByUnidadeAtiva(String unidade);

    Optional<Cliente> findByUnidadeAtiva(String unidade);

    Page<Cliente> buscarClientes(String nome, Pageable pageable);
}
