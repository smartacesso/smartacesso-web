package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.HistoricoCota;
import br.com.startjob.acesso.dataprovider.entity.HistoricoCotaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClienteMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface HistoricoCotaMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    HistoricoCota toDomain(final HistoricoCotaEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    HistoricoCotaEntity toEntity(final HistoricoCota domain, @Context CycleAvoidingMappingContext context);
}
