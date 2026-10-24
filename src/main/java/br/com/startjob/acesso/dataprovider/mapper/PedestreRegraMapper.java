package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.dataprovider.entity.PedestreRegraEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {RegraMapper.class, HorarioMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface PedestreRegraMapper {

    @Mapping(target = "pedestre", ignore = true)
    @Mapping(target = "horarios", qualifiedByName = "horarioSemRegra")
    PedestreRegra toDomain(final PedestreRegraEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("pedestreRegraSemPedestre")
    @Mapping(target = "pedestre", ignore = true)
    @Mapping(target = "horarios", qualifiedByName = "horarioSemRegra")
    PedestreRegra toDomainSemPedestre(final PedestreRegraEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    PedestreRegraEntity toEntity(final PedestreRegra domain, @Context CycleAvoidingMappingContext context);
}
