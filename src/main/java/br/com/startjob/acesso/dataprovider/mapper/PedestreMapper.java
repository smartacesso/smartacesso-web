package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PedestreMapper {

    Pedestre toDomain(final PedestreEntity entity, @Context CycleAvoidingMappingContext context);

    PedestreEntity toEntity(final Pedestre domain, @Context CycleAvoidingMappingContext context);
}
