package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Biometria;
import br.com.startjob.acesso.dataprovider.entity.BiometriaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface BiometriaMapper {

    Biometria toDomain(final BiometriaEntity entity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "pedestre", ignore = true)
    BiometriaEntity toEntity(final Biometria domain, @Context CycleAvoidingMappingContext context);
}
