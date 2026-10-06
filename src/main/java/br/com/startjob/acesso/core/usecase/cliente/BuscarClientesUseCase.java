package br.com.startjob.acesso.core.usecase.cliente;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BuscarClientesUseCase {

    private final ClienteGateway clienteGateway;

    public Page<Cliente> execute(String nome, Pageable pageable) {
        return clienteGateway.buscarClientes(nome, pageable);
    }
}
