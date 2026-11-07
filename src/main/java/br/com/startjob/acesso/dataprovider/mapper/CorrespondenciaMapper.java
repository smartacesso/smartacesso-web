package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.dataprovider.entity.CorrespondenciaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClienteMapper.class, PedestreMapper.class, UsuarioMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface CorrespondenciaMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "destinatario", qualifiedByName = "pedestreResumo")
    Correspondencia toDomain(final CorrespondenciaEntity entity, @Context CycleAvoidingMappingContext context);

    CorrespondenciaEntity toEntity(final Correspondencia domain, @Context CycleAvoidingMappingContext context);
}
