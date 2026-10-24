package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.dataprovider.entity.EquipamentoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClienteMapper.class, UsuarioMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface EquipamentoMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    Equipamento toDomain(final EquipamentoEntity entity, @Context CycleAvoidingMappingContext context);

    EquipamentoEntity toEntity(final Equipamento domain, @Context CycleAvoidingMappingContext context);
}
