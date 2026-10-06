package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.plano.Plano;
import br.com.startjob.acesso.dataprovider.entity.PlanoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PlanoMapper {

    Plano toDomain(final PlanoEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("planoSemCliente")
    @Mapping(target = "cliente", ignore = true)
    Plano toDomainSemCliente(final PlanoEntity entity, @Context CycleAvoidingMappingContext context);

    PlanoEntity toEntity(final Plano domain, @Context CycleAvoidingMappingContext context);
}
