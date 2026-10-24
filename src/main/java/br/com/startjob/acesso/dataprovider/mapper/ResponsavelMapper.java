package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Responsavel;
import br.com.startjob.acesso.dataprovider.entity.ResponsavelEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClienteMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface ResponsavelMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    Responsavel toDomain(final ResponsavelEntity entity, @Context CycleAvoidingMappingContext context);

    ResponsavelEntity toEntity(final Responsavel domain, @Context CycleAvoidingMappingContext context);
}
