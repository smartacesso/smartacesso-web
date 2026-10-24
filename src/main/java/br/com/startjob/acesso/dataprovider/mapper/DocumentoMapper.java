package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Documento;
import br.com.startjob.acesso.dataprovider.entity.DocumentoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface DocumentoMapper {

    Documento toDomain(final DocumentoEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    DocumentoEntity toEntity(final Documento domain, @Context CycleAvoidingMappingContext context);
}
