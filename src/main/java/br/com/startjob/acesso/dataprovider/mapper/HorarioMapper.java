package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.dataprovider.entity.HorarioEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface HorarioMapper {

    Horario toDomain(final HorarioEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("horarioSemRegra")
    @Mapping(target = "regra", ignore = true)
    Horario toDomainSemRegra(final HorarioEntity entity, @Context CycleAvoidingMappingContext context);

    HorarioEntity toEntity(final Horario domain, @Context CycleAvoidingMappingContext context);
}
