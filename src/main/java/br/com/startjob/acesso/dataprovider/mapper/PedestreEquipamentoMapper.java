package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.PedestreEquipamento;
import br.com.startjob.acesso.dataprovider.entity.PedestreEquipamentoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {EquipamentoMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface PedestreEquipamentoMapper {

    PedestreEquipamento toDomain(final PedestreEquipamentoEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    PedestreEquipamentoEntity toEntity(final PedestreEquipamento domain, @Context CycleAvoidingMappingContext context);
}
