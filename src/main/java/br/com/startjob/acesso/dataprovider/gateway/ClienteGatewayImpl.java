package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.gateway.ClienteGateway;
import br.com.startjob.acesso.dataprovider.mapper.ClienteMapper;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ClienteGatewayImpl implements ClienteGateway {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public long countByUnidadeAtiva(String unidade) {
        return clienteRepository.countByUnidadeAtiva(unidade);
    }

    @Override
    public Optional<Cliente> findByUnidadeAtiva(String unidade) {
        return clienteRepository.findByUnidadeAtiva(unidade)
                .map(entity -> clienteMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

}
