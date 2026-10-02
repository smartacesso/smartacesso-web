package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.dataprovider.entity.ParametroEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ParametroMapper {

    Parametro toDomain(final ParametroEntity entity, @Context CycleAvoidingMappingContext context);

    ParametroEntity toEntity(final Parametro domain, @Context CycleAvoidingMappingContext context);
}
