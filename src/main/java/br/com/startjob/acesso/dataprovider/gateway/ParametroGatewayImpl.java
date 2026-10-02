package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.ParametroMapper;
import br.com.startjob.acesso.dataprovider.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ParametroGatewayImpl implements ParametroGateway {

    private final ParametroRepository parametroRepository;
    private final ParametroMapper parametroMapper;

    @Override
    public Optional<Parametro> findByClienteAndNome(Long clienteId, String nome) {
        return parametroRepository.findByClienteAndNome(clienteId, nome)
                .map(entity -> parametroMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

}
