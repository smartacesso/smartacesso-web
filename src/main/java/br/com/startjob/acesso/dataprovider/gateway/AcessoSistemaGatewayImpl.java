package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.acessosistema.AcessoSistema;
import br.com.startjob.acesso.core.gateway.AcessoSistemaGateway;
import br.com.startjob.acesso.dataprovider.entity.AcessoSistemaEntity;
import br.com.startjob.acesso.dataprovider.mapper.AcessoSistemaMapper;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.repository.AcessoSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AcessoSistemaGatewayImpl implements AcessoSistemaGateway {

    private final AcessoSistemaRepository acessoSistemaRepository;
    private final AcessoSistemaMapper acessoSistemaMapper;

    @Override
    public AcessoSistema save(AcessoSistema acessoSistema) {
        AcessoSistemaEntity entity = acessoSistemaMapper.toEntity(acessoSistema, new CycleAvoidingMappingContext());
        AcessoSistemaEntity saved = acessoSistemaRepository.save(entity);
        return acessoSistemaMapper.toDomain(saved, new CycleAvoidingMappingContext());
    }

}
