package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.plano.Plano;
import br.com.startjob.acesso.core.gateway.PlanoGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.PlanoMapper;
import br.com.startjob.acesso.dataprovider.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PlanoGatewayImpl implements PlanoGateway {

    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;

    @Override
    public List<Plano> findAtivos(Long clienteId, LocalDateTime agora, Pageable pageable) {
        return planoRepository.findAtivos(clienteId, agora, pageable).stream()
                .map(entity -> planoMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

}
