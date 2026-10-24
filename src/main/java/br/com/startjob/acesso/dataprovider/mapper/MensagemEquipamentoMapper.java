package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.MensagemEquipamento;
import br.com.startjob.acesso.dataprovider.entity.MensagemEquipamentoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface MensagemEquipamentoMapper {

    MensagemEquipamento toDomain(final MensagemEquipamentoEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    MensagemEquipamentoEntity toEntity(final MensagemEquipamento domain, @Context CycleAvoidingMappingContext context);
}
