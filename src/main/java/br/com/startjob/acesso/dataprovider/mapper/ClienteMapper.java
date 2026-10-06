package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = PlanoMapper.class, builder = @Builder(disableBuilder = true))
public interface ClienteMapper {

    @Named("clienteResumo")
    @Mapping(target = "planos", ignore = true)
    Cliente toDomainResumo(final ClienteEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("clienteComPlanos")
    @Mapping(target = "planos", qualifiedByName = "planoSemCliente")
    Cliente toDomainComPlanos(final ClienteEntity entity, @Context CycleAvoidingMappingContext context);

    ClienteEntity toEntity(final Cliente domain, @Context CycleAvoidingMappingContext context);
}
