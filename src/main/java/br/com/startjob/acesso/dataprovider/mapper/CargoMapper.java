package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.dataprovider.entity.CargoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CargoMapper {

    Cargo toDomain(final CargoEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("cargoSemEmpresa")
    @Mapping(target = "empresa", ignore = true)
    Cargo toDomainSemEmpresa(final CargoEntity entity, @Context CycleAvoidingMappingContext context);

    CargoEntity toEntity(final Cargo domain, @Context CycleAvoidingMappingContext context);
}
