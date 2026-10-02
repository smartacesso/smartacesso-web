package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.PedestreMapper;
import br.com.startjob.acesso.dataprovider.repository.PedestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PedestreGatewayImpl implements PedestreGateway {

    private final PedestreRepository pedestreRepository;
    private final PedestreMapper pedestreMapper;

    @Override
    public Optional<Pedestre> findByLoginOtimizado(String login, String unidade) {
        return pedestreRepository.findByLoginOtimizado(login, unidade)
                .map(entity -> pedestreMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

}
