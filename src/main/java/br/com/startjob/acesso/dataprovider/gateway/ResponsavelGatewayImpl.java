package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Responsavel;
import br.com.startjob.acesso.core.gateway.ResponsavelGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.ResponsavelMapper;
import br.com.startjob.acesso.dataprovider.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ResponsavelGatewayImpl implements ResponsavelGateway {

    private final ResponsavelRepository responsavelRepository;
    private final ResponsavelMapper responsavelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Responsavel> findById(Long id) {
        return responsavelRepository.findByIdWithCliente(id)
                .map(entity -> responsavelMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }
}
