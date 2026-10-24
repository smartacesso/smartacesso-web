package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.core.gateway.EquipamentoGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.EquipamentoMapper;
import br.com.startjob.acesso.dataprovider.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EquipamentoGatewayImpl implements EquipamentoGateway {

    private final EquipamentoRepository equipamentoRepository;
    private final EquipamentoMapper equipamentoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipamento> findById(Long id) {
        return equipamentoRepository.findByIdWithCliente(id)
                .map(entity -> equipamentoMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }
}
