package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.parametro.Parametro;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ParametroGateway {

    Optional<Parametro> findByClienteAndNome(Long clienteId, String nome);

    List<Parametro> findAllByCliente(Long clienteId);

    void salvarParametrosCliente(Long clienteId, Map<String, String> valoresPorNome);

}
