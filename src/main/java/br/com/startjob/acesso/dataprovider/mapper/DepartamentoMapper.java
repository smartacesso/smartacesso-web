package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.dataprovider.entity.DepartamentoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DepartamentoMapper {

    Departamento toDomain(final DepartamentoEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("departamentoSemEmpresa")
    @Mapping(target = "empresa", ignore = true)
    Departamento toDomainSemEmpresa(final DepartamentoEntity entity, @Context CycleAvoidingMappingContext context);

    DepartamentoEntity toEntity(final Departamento domain, @Context CycleAvoidingMappingContext context);
}
