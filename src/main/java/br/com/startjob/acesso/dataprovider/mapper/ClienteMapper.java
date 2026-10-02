package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClienteMapper {

    Cliente toDomain(final ClienteEntity entity, @Context CycleAvoidingMappingContext context);

    ClienteEntity toEntity(final Cliente domain, @Context CycleAvoidingMappingContext context);
}
